package test;

import nl.nlesc.ptk.io.FSUtil;
import nl.vbrowser.ui.browser.BrowserPlatform;
import nl.vbrowser.ui.browser.ProxyBrowser;
import nl.vbrowser.ui.dcm.proxy.DCMProxyFactory;
import nl.vbrowser.ui.proxy.ProxyNode;

public class testDCMBrowser 
{

	public static void main(String args[])
	{
		try 
		{
			BrowserPlatform platform=BrowserPlatform.getInstance(); 
		    
			DCMProxyFactory fac = DCMProxyFactory.getDefault();  
		    platform.registerProxyFactory(fac);  
            
		    ProxyBrowser frame=(ProxyBrowser)platform.createBrowser(); 
		    
    		ProxyNode root = fac.openLocation(FSUtil.getDefault().getUserHomeDir().getVRI().toString()+"/dicom"); 
    		
			frame.setRoot(root,true); 
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		} 
		
		// frame.setRoot(root); 
		
	}
}
