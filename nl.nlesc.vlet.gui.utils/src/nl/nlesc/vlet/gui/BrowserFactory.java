package nl.nlesc.vlet.gui;

import nl.nlesc.vlet.vrl.VRL;

public interface BrowserFactory
{
    MasterBrowser createBrowser(); 
    
    MasterBrowser createBrowser(VRL vrl); 
}
