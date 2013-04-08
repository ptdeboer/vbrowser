package nl.vbrowser.ui.proxy.dummy;

import nl.nlesc.ptk.net.VRI;
import nl.vbrowser.ui.proxy.ProxyFactory;
import nl.vbrowser.ui.proxy.ProxyNode;

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
