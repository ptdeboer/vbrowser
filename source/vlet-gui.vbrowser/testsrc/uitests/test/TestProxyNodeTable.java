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

package uitests.test;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.panels.resourcetable.ResourceTable;
import nl.esciencecenter.vlet.gui.proxymodel.ProxyNodeTableModel;
import nl.esciencecenter.vlet.gui.proxyvrs.ProxyNode;
import nl.esciencecenter.vlet.gui.vbrowser.VBrowserInit;

public class TestProxyNodeTable
{
    public static void main(String args[])
    {
        
        ProxyNode pnode=null; 
        
        try
        {
            VBrowserInit.initPlatform(); 
            
            pnode = ProxyNode.getProxyNodeFactory().openLocation(new VRL("file:///"+GlobalProperties.getGlobalUserHome()));
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }  
        
        final ProxyNode fnode=pnode; 
        
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
                                ResourceTable table=new ResourceTable(new ProxyNodeTableModel(fnode)); 
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
}
