package nl.vbrowser.ui.proxy.anyfile;

import nl.nlesc.ptk.io.FSNode;
import nl.nlesc.ptk.net.VRI;
import nl.vbrowser.ui.proxy.ProxyException;
import nl.vbrowser.ui.proxy.ProxyFactory;
import nl.vbrowser.ui.proxy.ProxyNode;

public class AnyFileProxyFactory extends ProxyFactory
{
    private static ProxyFactory instance; 
    
    public static synchronized ProxyFactory getDefault() 
    {
        if (instance==null)
            instance=new AnyFileProxyFactory();
              
        return instance; 
   }
    // ========================================================================
    // 
    // ========================================================================

    public ProxyNode doOpenLocation(VRI locator) throws ProxyException
    {
        return new AnyFileProxyNode(locator); 
    }

	@Override
	public boolean canOpen(VRI locator) 
	{
	    return locator.hasScheme(FSNode.FILE_SCHEME);
	}

}
