/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: TestProxyNodeTable.java,v 1.1 2012/11/18 13:20:35 piter Exp $  
 * $Date: 2012/11/18 13:20:35 $
 */ 
// source: 

package test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import nl.vbrowser.ui.browser.BrowserPlatform;
import nl.vbrowser.ui.browser.ProxyBrowser;
import nl.vbrowser.ui.proxy.ProxyFactory;
import nl.vbrowser.ui.proxy.ProxyNode;
import nl.vbrowser.ui.proxy.anyfile.AnyFileProxyFactory;
import nl.vbrowser.ui.resourcetable.ProxyNodeTableDataProducer;
import nl.vbrowser.ui.resourcetable.ResourceTable;
import nl.vbrowser.ui.resourcetable.ResourceTableModel;


public class TestProxyNodeTable
{
    public static void main(String args[])
    {
        try 
        {
            BrowserPlatform platform=BrowserPlatform.getInstance(); 
            
            //ProxyBrowser frame=(ProxyBrowser)platform.createBrowser();
            
            ProxyFactory fac = AnyFileProxyFactory.getDefault(); 
            
            platform.registerProxyFactory(fac); 
            
            final ProxyNode node = fac.openLocation("file:/home/");
            
            Runnable runnable=new Runnable()
            {
                public void run()
                {
                    try
                    {
                        JFrame frame=new JFrame(); 
                        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 

                        {
                            JPanel panel=new JPanel();
                            panel.setLayout(new BorderLayout());
                            frame.add(panel);
                            
                            {
                                JScrollPane scrollPanel = new JScrollPane();
                                panel.add(scrollPanel, BorderLayout.CENTER);
                                {
                                	ResourceTableModel model = new ResourceTableModel(); 
                                    ResourceTable table=new ResourceTable(null,model);
                                    table.setDataProducer(new ProxyNodeTableDataProducer(node,model),true); 
                                    scrollPanel.setViewportView(table);
                                }
                            }
                        }
                        
                        frame.setSize(new Dimension(700,300)); 
                        frame.pack(); 
                        frame.setVisible(true); 
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    } 
                   
                }
            };
            
            SwingUtilities.invokeLater(runnable);       
           
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        } 
        
   }
}
