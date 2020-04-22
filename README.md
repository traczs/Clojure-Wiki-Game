# untitled_unmastered

A Clojure library designed web scrape Wikipedia atricles and tell you 
how many wiki links it takes to get from one link to another.

# References
Naccache,  R. (2015). Clojure Data Structures and Algorithms Cookbook.
    Packt Publishing.
    
## Usage
You have to have an internet connection (hopefully a good one) 

How I run it:
- lein repl
- Call on function (run-spider param1 param2 param3)
    - param1 requires 1 of 2 function options
        - call scrape-targets, which scrapes linearly
        - call async-scrape-targets-10s, which scrapes asynchronously
    - param2 is the starting wiki link extension
        - in the form of wiki/topic
        - pretty much anything after https://en.wikipedia.org/ in the url
        - example "wiki/Salloon"
    - param3 is the end link
        - in the same form as param2     
    - example calls
        - (run-spider scrape-targets "wiki/Salloon" "wiki/Acre")
        - (run-spider async-scrape-targets-10s "wiki/Salloon" "wiki/Acre")
        - (run-spider scrape-targets "wiki/Clojure" "wiki/New_York_Stock_Exchange")

      
## Limitations
- For scrape-targets, since it's linear it may hang up on a link and
just stop running. In this case you would have to quit the repl and 
exit the batch job and try again.

- I haven't tested anything further than 3 links apart, even then the
linear one takes a while to get through and only works some of the
time. Not sure if my internet speeds have to do anything with it.

- If the links are too far apart, the vector might run out of memory
due to it storing all the links it comes by. I tried to fix this by 
only storing wiki links, but there still has to be some upper limit.

- For async-scrape-targets-10s, it throws an error at the beginning or 
end of each depth it reaches, not sure which one. I don't know why,
but it then continues to work (most of the time).

- anything greater than 2 links is just uncharted territory that may 
or may not work.

## Future Improvements

- Currently, it only says how much links it would take to get from
point a to b. I would like it to show the path that it took to get there.
   
- I would like to fix all the warnings and errors that this program throws,
but I don't know enough about asynchronous Clojure to do that.   
             
## License

Copyright © 2020 FIXME

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
