package test;

import nl.vbrowser.ui.browser.BrowserPlatform;
import nl.vbrowser.ui.browser.ProxyBrowser;
import nl.vbrowser.ui.proxy.ProxyNode;
import nl.vbrowser.vlet.proxy.vrs.VRSProxyFactory;

public class testVRSBrowser 
{

	public static void main(String args[])
	{
		try 
		{
			BrowserPlatform platform=BrowserPlatform.getInstance(); 
		    
			VRSProxyFactory fac = VRSProxyFactory.getDefault();  
		    platform.registerProxyFactory(fac);  
            
		    ProxyBrowser frame=(ProxyBrowser)platform.createBrowser(); 
		    
    		ProxyNode root = fac.openLocation("myvle:/"); 
    		
			frame.setRoot(root,true); 
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
		// frame.setRoot(root); 
		
	}
}
