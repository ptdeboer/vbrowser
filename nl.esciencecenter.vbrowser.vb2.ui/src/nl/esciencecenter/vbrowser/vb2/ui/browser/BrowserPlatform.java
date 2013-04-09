package nl.esciencecenter.vbrowser.vb2.ui.browser;

import javax.swing.TransferHandler;

import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.dnd.DnDUtil;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactoryRegistry;

/** 
 * Browser Platform.
 *  
 * Typically one Platform instance per application environment is created. 
 */
public class BrowserPlatform
{
    private static BrowserPlatform instance=null;
    
    public static synchronized BrowserPlatform getInstance()
    {
        if (instance==null)
            instance=new BrowserPlatform();
        
        return instance; 
    }
    
    // ========================================================================
    // Instance
    // ========================================================================
    
    private ProxyFactoryRegistry proxyRegistry=null;
    
    
    protected BrowserPlatform()
    {
        init(); 
    }
    
    private void init()
    {
        // init defaults: 
        this.proxyRegistry=ProxyFactoryRegistry.getInstance(); 
    }
    
    public ProxyFactory getFactoryFor(VRI locator)
    {
        return this.proxyRegistry.getProxyFactoryFor(locator); 
    }
    
    public BrowserInterface createBrowser()
    {
    	return createBrowser(true);
    }
    
    public BrowserInterface createBrowser(boolean show)
    {
    	return new ProxyBrowser(this,show); 
    }
   
    public void registerProxyFactory(ProxyFactory factory)
    {
        this.proxyRegistry.registerProxyFactory(factory); 
    }

    /**
     * Returns Inter Browser DnD TransferHandler for DnDs between browser frames and ViewNodeComponents. 
     */
    public TransferHandler getTransferHandler()
    {
        // default;
       return DnDUtil.getDefaultTransferHandler();
    }
    
}
