VBrowser
========

Development version of the VBrowser. 
Based on VLET 1.6.

__IMPORTANT__: as of 2015 this project _is not maintainted anymore_...

Dependencies
---

This project requires platinum-1.6.3:

    https://github.com/ptdeboer/Platinum

Maven
---

This project has been converted to maven. To build the project perform a toplevel build as follows:

    mvn clean package

A binary distribution can be found after building (see above) in:
    
    vbrowser-dist/target/

Missing maven dependencies have been manually constructed and placed in:
    
    maven2/
    
These are custom maven compatible artifacts for legacy code which do not have a maven repository.
Some plugins have been upgraded to maven2, but have not been tested 100%.


Ant
---
The Ant build is now broken.
Most of them have been deleted.

