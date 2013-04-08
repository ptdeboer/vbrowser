package test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.vbrowser.ui.browser.BrowserPlatform;
import nl.vbrowser.ui.browser.DummyBrowserInterface;
import nl.vbrowser.ui.model.UIViewModel;
import nl.vbrowser.ui.proxy.ProxyFactory;
import nl.vbrowser.ui.proxy.ProxyNode;
import nl.vbrowser.ui.proxy.ProxyNodeDataSource;
import nl.vbrowser.ui.proxy.dummy.DummyProxyFactory;
import nl.vbrowser.ui.tree.ResourceTree;

public class testResourceTree 
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
    		ResourceTree tree;
    		ProxyFactory fac = DummyProxyFactory.getDefault(); 
    		ProxyNode root = fac.openLocation("proxy:///"); 
    		ProxyNodeDataSource dataSource = new ProxyNodeDataSource (fac,root); 
    		tree=new ResourceTree(new DummyBrowserInterface(platform),dataSource);
    		//tree=new ResourceTree(null,dataSource);
    	        		
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
