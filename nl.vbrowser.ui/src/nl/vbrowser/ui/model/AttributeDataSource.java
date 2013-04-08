package nl.vbrowser.ui.model;

import nl.nlesc.ptk.net.VRI;
import nl.vbrowser.ui.data.Attribute;
import nl.vbrowser.ui.proxy.ProxyException;

public interface AttributeDataSource
{

    // =====================
    // Attribute Interface
    // =====================
    
    String[] getAttributeNames(VRI locator) throws ProxyException; 
    
    Attribute[] getAttributes(VRI locator,String attrNames[]) throws ProxyException; 
}
