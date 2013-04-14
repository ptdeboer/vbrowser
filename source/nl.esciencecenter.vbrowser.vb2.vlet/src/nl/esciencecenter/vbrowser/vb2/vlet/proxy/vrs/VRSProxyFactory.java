/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */ 
// source: 

package nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs;

import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.nlesc.vlet.exception.VlURISyntaxException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSClient;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.VRSFactory;


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
