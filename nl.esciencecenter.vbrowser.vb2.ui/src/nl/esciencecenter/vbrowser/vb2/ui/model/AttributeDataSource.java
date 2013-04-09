package nl.esciencecenter.vbrowser.vb2.ui.model;

import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.data.Attribute;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;

public interface AttributeDataSource
{

    // =====================
    // Attribute Interface
    // =====================
    
    String[] getAttributeNames(VRI locator) throws ProxyException; 
    
    Attribute[] getAttributes(VRI locator,String attrNames[]) throws ProxyException; 
}
