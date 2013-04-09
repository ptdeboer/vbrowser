package test;

import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserFrame;
import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserInterface;
import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserPlatform;
import nl.esciencecenter.vbrowser.vb2.ui.browser.ProxyBrowser;
import nl.esciencecenter.vbrowser.vb2.ui.model.UIViewModel;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNodeDataSource;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.dummy.DummyProxyFactory;
import nl.esciencecenter.vbrowser.vb2.ui.tree.ResourceTree;

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
