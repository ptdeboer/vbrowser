package test;

import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserPlatform;
import nl.esciencecenter.vbrowser.vb2.ui.browser.ProxyBrowser;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.vbrowser.ui.dcm.proxy.DCMProxyFactory;

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
