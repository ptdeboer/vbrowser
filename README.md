VBrowser
========

Development version of the VBrowser. 
Based on VLET 1.6.

__IMPORTANT__: as of 2015 this project _is not maintainted anymore_...

The VL-e's 'Virtual Resource Browser' was used by scientists as 'main access point to the Grid'.
Featuring domain specific plugins mostly for the biomedical community.
The project lasted from 2005-2010.

After the VL-e project the VBrowser was featured in the Big Grid (2010-2012) and after that maintained by the
Netherlands eScience Center.

More information:

 - The Virtual Laboratory for e-Science: [www.vl-e.nl](http://www.vl-e.nl)
 - The Big Grid: [www.biggrid.nl](https://www.biggrid.nl)
 - The Netherlands eScience Center: [www.esciencecenter.nl](https://www.esciencecenter.nl)

Dependencies
---

This project requires platinum-1.6.3.

    https://github.com/ptdeboer/Platinum

Java
---
This project compiles under Java 11 with backward compatibility to Java 1.8
Newer versions (>11) are not supported.

Maven
---

This project has been converted to a maven project and now needs Java 11.
To build the project perform a toplevel build as follows:

    mvn clean package

A binary distribution can be found after building (see above) in:
    
    vbrowser-dist/target/

Missing maven dependencies have been manually constructed and placed in:
    
    maven2/
    
These are custom maven compatible artifacts for legacy code which do not have a maven repository.
Some plugins have been upgraded to maven2, but have not been tested 100%.
