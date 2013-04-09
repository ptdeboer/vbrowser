package nl.vbrowser.ui.dcm.proxy;

import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;


/** 
 * VRS Proxy Factory for VRSProxyNodes.  
 */
public class DCMProxyFactory extends ProxyFactory
{
    private static ClassLogger logger; 
    
    static
    {
    	logger=ClassLogger.getLogger(DCMProxyFactory.class);
    	logger.setLevelToDebug();
    }
    
	private static DCMProxyFactory instance; 
    
    public static DCMProxyFactory getDefault() 
    {
        if (instance==null)
            instance=new DCMProxyFactory();
              
        return instance; 
   }
    // ========================================================================
    // 
    // ========================================================================

    protected DCMProxyFactory()
    {
        super(); 
    }
    
//	public DCMProxyNode openLocation(VRI vrl) throws ProxyException
//	{
//		try 
//		{
//			return (DCMProxyNode)openLocation(new VRI(vrl.toURI()));
//		}
//		catch (Exception e) 
//		{
//			throw new ProxyException("Failed to open location:"+vrl+"\n"+e.getMessage(),e); 
//		} 
//	}
	
	// actual open location: 
	
    public DCMProxyNode doOpenLocation(VRI locator) throws ProxyException
    {
    	logger.infoPrintf(">>> doOpenLocation():%s <<<\n",locator);
    	
        try
        {
            FSNode fsnode=FSUtil.getDefault().newLocalFSNode(locator.getPath());
            return new DCMProxyNode(this,fsnode,locator);
        }
        catch (Exception e)
        {
            throw new ProxyException("Failed to open location:"+locator+"\n"+e.getMessage(),e); 
        }
    }
    

    @Override
	public boolean canOpen(VRI locator) 
	{
		// internal scheme!
		if (StringUtil.equals("file",locator.getScheme())) 
			return true; 

		return false;
	}


}
