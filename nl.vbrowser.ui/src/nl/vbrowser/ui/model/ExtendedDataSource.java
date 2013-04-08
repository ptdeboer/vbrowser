package nl.vbrowser.ui.model;


import nl.nlesc.ptk.net.VRI;
import nl.nlesc.ptk.presentation.Presentation;
import nl.vbrowser.ui.proxy.ProxyException;

/**
 * Combines DataSource and AttributeDataSource and adds Presentation.  
 * 
 * @author Piter T. de Boer. 
 */
public interface ExtendedDataSource extends DataSource, AttributeDataSource
{
    Presentation getPresentation() throws ProxyException;

    Presentation getChildPresentation(VRI locator) throws ProxyException;

}
