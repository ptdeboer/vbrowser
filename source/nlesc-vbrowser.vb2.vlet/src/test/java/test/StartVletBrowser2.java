/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package test;

import nl.esciencecenter.ptk.vbrowser.ui.browser.BrowserPlatform;
import nl.esciencecenter.ptk.vbrowser.ui.browser.ProxyBrowserController;
import nl.esciencecenter.ptk.vbrowser.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs.VRSProxyFactory;
import nl.esciencecenter.vbrowser.vb2.vlet.viewers.VLTermStarter;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.util.VRSResourceLoader;

/**
 * Start new PTK VBrowser from Platinum, but use VLET VRS.
 */
public class StartVletBrowser2
{

	public static void main(String args[])
	{
	    // Use ResourceLoaders ! 
	    VletConfig.setInitURLStreamFactory(false); 
	    
		try 
		{
			BrowserPlatform platform=BrowserPlatform.getInstance("vlet"); 
		    
			VRSProxyFactory fac = new VRSProxyFactory(platform);  
		    platform.registerProxyFactory(fac);  
            
		    VRSContext context=VRS.getDefaultVRSContext(); 
		    platform.getViewerRegistry().registerPlugin(VLTermStarter.class);
		    
		    ProxyBrowserController frame=(ProxyBrowserController)platform.createBrowser(); 
    		ProxyNode root = fac.openLocation("myvle:/"); 
    		
			frame.setRoot(root,true,true); 
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
		// frame.setRoot(root); 
		
	}
}
