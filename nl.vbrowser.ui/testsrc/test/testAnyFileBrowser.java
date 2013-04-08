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
import nl.vbrowser.ui.proxy.anyfile.AnyFileProxyFactory;
import nl.vbrowser.ui.proxy.dummy.DummyProxyFactory;
import nl.vbrowser.ui.tree.ResourceTree;

public class testAnyFileBrowser 
{

	public static void main(String args[])
	{
		try 
		{
			BrowserPlatform platform=BrowserPlatform.getInstance(); 
		    
		    ProxyBrowser frame=(ProxyBrowser)platform.createBrowser();
		    
		    ProxyFactory fac = AnyFileProxyFactory.getDefault(); 
		    
		    platform.registerProxyFactory(fac); 
		    
			ProxyNode root = fac.openLocation("file:/home/");
		
			frame.setRoot(root,true); 
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
	}
}
