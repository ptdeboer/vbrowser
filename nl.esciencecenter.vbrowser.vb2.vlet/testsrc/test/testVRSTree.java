package test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserInterface;
import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserPlatform;
import nl.esciencecenter.vbrowser.vb2.ui.model.UIViewModel;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNodeDataSource;
import nl.esciencecenter.vbrowser.vb2.ui.tree.ResourceTree;
import nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs.VRSProxyFactory;

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
