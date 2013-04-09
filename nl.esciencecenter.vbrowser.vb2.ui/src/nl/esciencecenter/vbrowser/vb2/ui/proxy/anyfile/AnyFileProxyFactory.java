package nl.esciencecenter.vbrowser.vb2.ui.proxy.anyfile;

import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;

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
