package test;

import nl.vbrowser.ui.browser.BrowserFrame;
import nl.vbrowser.ui.browser.BrowserInterface;
import nl.vbrowser.ui.browser.BrowserPlatform;
import nl.vbrowser.ui.browser.ProxyBrowser;
import nl.vbrowser.ui.model.UIViewModel;
import nl.vbrowser.ui.proxy.ProxyException;
import nl.vbrowser.ui.proxy.ProxyFactory;
import nl.vbrowser.ui.proxy.ProxyNode;
import nl.vbrowser.ui.proxy.ProxyNodeDataSource;
import nl.vbrowser.ui.proxy.dummy.DummyProxyFactory;
import nl.vbrowser.ui.tree.ResourceTree;

public class testDummyBrowser 
{

	public static void main(String args[])
	{
	    
	    
		try 
		{
			BrowserPlatform platform=BrowserPlatform.getInstance(); 
		    
		    ProxyBrowser frame=(ProxyBrowser)platform.createBrowser();
		    
		    ProxyFactory dummyFac = DummyProxyFactory.getDefault(); 
		    
		    platform.registerProxyFactory(dummyFac); 
		    
			ProxyNode root = dummyFac.openLocation("proxy:///");
		
			frame.setRoot(root,true); 
			
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
		// frame.setRoot(root); 
		
	}
}
