package nl.esciencecenter.vbrowser.vb2.ui.model;

import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;

public interface AttributeDataSource
{

    // =====================
    // Attribute Interface
    // =====================
    
    String[] getAttributeNames(VRI locator) throws ProxyException; 
    
    Attribute[] getAttributes(VRI locator,String attrNames[]) throws ProxyException; 
}
