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
