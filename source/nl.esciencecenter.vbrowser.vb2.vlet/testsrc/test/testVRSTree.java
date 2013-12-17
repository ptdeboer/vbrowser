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

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.esciencecenter.ptk.vbrowser.ui.browser.BrowserInterface;
import nl.esciencecenter.ptk.vbrowser.ui.browser.BrowserPlatform;
import nl.esciencecenter.ptk.vbrowser.ui.model.UIViewModel;
import nl.esciencecenter.ptk.vbrowser.ui.proxy.ProxyNode;
import nl.esciencecenter.ptk.vbrowser.ui.proxy.ProxyNodeDataSource;
import nl.esciencecenter.ptk.vbrowser.ui.tree.ResourceTree;
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
    		
    		ProxyNodeDataSource dataSource = new ProxyNodeDataSource (root); 
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
