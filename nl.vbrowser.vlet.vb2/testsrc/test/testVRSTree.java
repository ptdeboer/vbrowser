package test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.vbrowser.ui.browser.BrowserInterface;
import nl.vbrowser.ui.browser.BrowserPlatform;
import nl.vbrowser.ui.model.UIViewModel;
import nl.vbrowser.ui.proxy.ProxyNode;
import nl.vbrowser.ui.proxy.ProxyNodeDataSource;
import nl.vbrowser.ui.tree.ResourceTree;
import nl.vbrowser.vlet.proxy.vrs.VRSProxyFactory;

public class testVRSTree 
{
	
	public static void main(String args[])
	{
	    
	    BrowserPlatform platform=BrowserPlatform.getInstance(); 
	    
		JFrame frame=new JFrame(); 
		JPanel panel=new JPanel(); 
		frame.add(panel); 
		
		panel.setLayout(new BorderLayout()); 
		
		try
		{ 
		    VRSProxyFactory fac = VRSProxyFactory.getDefault();  
		    platform.registerProxyFactory(fac);  
            
    		ResourceTree tree;
    		 
    		ProxyNode root = fac.openLocation("myvle:/"); 
    		BrowserInterface browser = platform.createBrowser();
    		//browser.setRoot(root); 
    		
    		ProxyNodeDataSource dataSource = new ProxyNodeDataSource (fac,root); 
    		UIViewModel uIModel=UIViewModel.createTreeViewModel(); 
    		
    		tree=new ResourceTree(null,dataSource);
    				
    		JScrollPane pane=new JScrollPane(); 
    			
    		pane.setViewportView(tree); 
    		panel.add(pane,BorderLayout.CENTER); 
    		frame.setSize(new Dimension(600,400)); 
    		
    		//frame.pack();
    		frame.setVisible(true); 
		}
		catch (Exception e)
		{
		    e.printStackTrace(); 
		}
		
	}	
	
}
