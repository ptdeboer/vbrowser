package nl.esciencecenter.vbrowser.vb2.ui.model;


import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;

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
