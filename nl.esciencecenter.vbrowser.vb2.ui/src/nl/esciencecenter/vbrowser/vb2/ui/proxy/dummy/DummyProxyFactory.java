package nl.esciencecenter.vbrowser.vb2.ui.proxy.dummy;

import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;

public class DummyProxyFactory extends ProxyFactory
{
    private static ProxyFactory instance; 
    
    public static ProxyFactory getDefault() 
    {
        if (instance==null)
            instance=new DummyProxyFactory();
              
        return instance; 
   }
    // ========================================================================
    // 
    // ========================================================================

    public ProxyNode doOpenLocation(VRI locator)
    {
        return new DummyProxyNode(locator); 
    }

	@Override
	public boolean canOpen(VRI locator) 
	{
	    return locator.hasScheme("proxy");
	}

}
