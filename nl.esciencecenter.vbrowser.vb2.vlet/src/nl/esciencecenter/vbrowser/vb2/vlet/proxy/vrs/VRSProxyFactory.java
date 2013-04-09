package nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs;

import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.uva.vlet.exception.VlURISyntaxException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSClient;
import nl.uva.vlet.vrs.VRSContext;
import nl.uva.vlet.vrs.VRSFactory;


/** 
 * VRS Proxy Factory for VRSProxyNodes.  
 */
public class VRSProxyFactory extends ProxyFactory
{
    private static ClassLogger logger; 
    
    static
    {
    	logger=ClassLogger.getLogger(VRSProxyFactory.class);
    	logger.setLevelToDebug();
    }
    
	private static VRSProxyFactory instance; 
    
    public static VRSProxyFactory getDefault() 
    {
        if (instance==null)
            instance=new VRSProxyFactory();
              
        return instance; 
   }
    // ========================================================================
    // 
    // ========================================================================

    private VRSContext vrsContext;
    
    private VRSClient vrsClient;

    protected VRSProxyFactory()
    {
        super(); 
        
        this.vrsContext=VRS.getDefaultVRSContext(); 
        this.vrsClient=new VRSClient(vrsContext); 
    }
    
	public VRSProxyNode openLocation(VRL vrl) throws ProxyException
	{
		try 
		{
			return (VRSProxyNode)openLocation(new VRI(vrl.toURI()));
		}
		catch (Exception e) 
		{
			throw new ProxyException("Failed to open location:"+vrl+"\n"+e.getMessage(),e); 
		} 
	}
	
	// actual open location: 
	
    public VRSProxyNode doOpenLocation(VRI locator) throws ProxyException
    {
    	logger.infoPrintf(">>> doOpenLocation():%s <<<\n",locator);
    	
        try
        {
            VNode vnode=vrsClient.openLocation(createVRL(locator));
            return new VRSProxyNode(this,vnode,locator);
        }
        catch (Exception e)
        {
            throw new ProxyException("Failed to open location:"+locator+"\n"+e.getMessage(),e); 
        }
    }
    
	private VRL createVRL(VRI locator)
    {
	    return new VRL(locator.getScheme(),
	            locator.getUserinfo(),
	            locator.getHostname(),
	            locator.getPort(),
	            locator.getPath(),
	            locator.getQuery(),
	            locator.getFragment()); 
    }

    @Override
	public boolean canOpen(VRI locator) 
	{
		// internal scheme!
		if (StringUtil.equals("myvle",locator.getScheme())) 
			return true; 
			
		VRSFactory vrs = this.vrsContext.getRegistry().getVRSFactoryForScheme(locator.getScheme()); 
		return (vrs!=null); 
	}


}