package nl.vbrowser.ui.proxy;

import java.util.Vector;

import nl.nlesc.ptk.net.VRI;
import nl.nlesc.ptk.util.logging.ClassLogger;
import nl.vbrowser.ui.proxy.dummy.DummyProxyFactory;

/**
 * Registry for ProxyFactories to multiple sources
 */
public class ProxyFactoryRegistry
{
    private static ProxyFactoryRegistry instance;
    
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger("ProxyRegistry.class"); 
    }
    
    public static ProxyFactoryRegistry getInstance()
    {
        if (instance==null)
           instance=new ProxyFactoryRegistry(); 
        
        return instance; 
    }
    
    // ========================================================================
    //
    // ========================================================================
    
    private Vector<ProxyFactory> factories=new Vector<ProxyFactory>(); 
    
    protected ProxyFactoryRegistry()
    {
        initRegistry(); 
    }
    
    protected void initRegistry()
    {
        logger.debugPrintf("--- ProxyRegistry:initRegistry() ---\n"); 
    }
    
    public ProxyFactory getProxyFactoryFor(VRI locator)
    {
    	synchronized(this.factories)
    	{
    		for (ProxyFactory fac:factories)
    			if (fac.canOpen(locator))
    				return fac; 
    	}
    
    	return null; 
    }

    public ProxyFactory getDefaultProxyFactory()
    {
        return DummyProxyFactory.getDefault(); 
    }

    public void registerProxyFactory(ProxyFactory factory)
    {
        this.factories.add(factory); 
    }
    
    public void unregisterProxyFactory(ProxyFactory factory)
    {
        this.factories.remove(factory); 
    }
}
