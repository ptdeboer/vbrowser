VBrowser
========

Development version of the VBrowser. 
Based on VLET 1.6. 


Maven
---
Update to full maven build is in progress. Most modules have been converted.
A minimal working distribution can be found after building (mvn package) in:
    
    vbrowser-dist/target/

Missing maven dependencies have been manually constructed and placed in:
    
    maven2/
    
These are custom maven compatible artifacts for legacy code which do not have a maven repository.
Some legacy modules contain their own private repository.


Plugins
---
Some plugins have been upgraded to maven2, but have not been tested 100%. 
Also they depend on very old code for which custom maven artifacts have been created. 
These are:

 - LoboBrowser: source/vlet-gui.lobo.viewer
 - PedalViewer: source/vlet-gui.pedal

Ant
---
The Ant build is now broken. 
The build files are kept for references purpose only.

