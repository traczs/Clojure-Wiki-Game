(ns untitled-unmastered.core
  (:require [pl.danieljanus.tagsoup :as http-parser]
            [clojure.core.async :as async :refer :all :exclude
[into]]
            [clojure.zip :as z]))

(defn fq-url
  [domain url]
  (let [protocol (first (clojure.string/split url #"/"))]
    ;;find the first of the tokens seperated by / in the given url
    (if (not (some #{protocol} ["https:" "http:" "mailto:"]))
      ;;if it's not https etc, append the domain to it
      (str domain url)
      url)))

(defn scrape-target-for-hrefs
  [target-path]
  (let [target (last target-path)
        html-tree (try (http-parser/parse target) (catch Exception e nil))
        zpr (z/zipper vector? #(filter vector? %) conj html-tree)
        url (java.net.URL. target)
        domain (.getHost url)
        protocol (.getProtocol url)
        fqd (str protocol "://" domain)]
  (loop [output #{}
        loc (-> zpr z/down)]
    (if (nil? loc)
      nil
      (if (z/end? loc)
        output
        (let [tag (get (z/node loc) 0)
             a-href (get-in (z/node loc) [1 :href])
             new-output (if (and (= tag :a)
                                 (not (nil? a-href))
                                 (clojure.string/includes? a-href "/wiki/"))
                          (conj output [target (fq-url fqd a-href)])
                          output)]
          (recur new-output
                 (z/next loc))))))))

(defn scrape-targets
  [targets]
  (loop [remaining-t targets
         output #{}]
    (if (not (seq remaining-t))
        output
        (let [new-ts (scrape-target-for-hrefs (first remaining-t))]
          (recur (rest remaining-t)
                 (concat output new-ts))))))

(defn async-scrape-targets
  [ch-timeout targets]
  (let [c (timeout ch-timeout)]
    (doseq [t targets]
      (go (>! c (scrape-target-for-hrefs t))))
    (loop [cur-t 0
           output #{}]
      (if (= cur-t (count targets))
          output
          (let [new-ts (<!! c)]
            (recur (inc cur-t)
                   (concat output new-ts)))))))

(def async-scrape-targets-10s (partial async-scrape-targets 10000))

(defn run-spider
  [scrape-target-fn strt end]
  (println "attempting to reach" end)
  (def start [[(str "https://en.wikipedia.org/" strt)]])
  (loop [targets start
                 output #{}
                 cur-depth 0
                 done (vector) ]
    (println "depth" cur-depth "count" (count targets))
    (if (= (last done) 1)
        (println "it takes" cur-depth "link(s) to get from" (flatten start) "to (" (str "https://en.wikipedia.org/"end)")")
        (let [new-targets (scrape-target-fn targets)]
          (def isdone done)
          ;loops through and checks if the end is in the vector
          (loop [i 0]
            (when (< i (count new-targets))
              (println i)
              ; if we found the link we want, we set isdone so we dont do this loop anymore
              (if (= (clojure.string/includes? (str (last (nth new-targets i))) end) true)
                (def isdone (conj isdone 1))
                (recur (inc i)))))
          (recur new-targets
                 (into output new-targets)
                 (inc cur-depth)
                 isdone)))))


; (run-spider scrape-targets [["https://en.wikipedia.org/wiki/Salloon"]] "wiki/Acre")
; (run-spider scrape-targets [["https://en.wikipedia.org/wiki/Salloon"]] "wiki/Metrication")