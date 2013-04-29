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

package nl.esciencecenter.vbrowser.vb2.ui.browser;

import javax.swing.TransferHandler;

import nl.esciencecenter.vbrowser.vb2.ui.dnd.DnDUtil;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactoryRegistry;
import nl.esciencecenter.vbrowser.vrs.net.VRL;

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
    
    public ProxyFactory getFactoryFor(VRL locator)
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
