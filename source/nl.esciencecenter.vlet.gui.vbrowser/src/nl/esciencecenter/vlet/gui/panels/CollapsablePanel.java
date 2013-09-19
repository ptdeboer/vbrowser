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

package nl.esciencecenter.vlet.gui.panels;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class CollapsablePanel extends JPanel
{
    private static final long serialVersionUID = 2215114817505542917L;
    
    private Container contentPanel; 
    
    private JLabel header; 
    
    public CollapsablePanel()
    {
        super();
        initGUI(); 
    }
    
    private void initGUI()
    {
        this.setLayout(new BorderLayout()); 
        {
            header=new JLabel("Panel Header"); 
            this.add(header,BorderLayout.NORTH);
            header.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }
        
        {
            contentPanel=new JPanel();
            this.add(contentPanel,BorderLayout.CENTER);
        }       
    }
    
    public Container getContentPanel()
    {
        return contentPanel; 
    }
    
    public void setContentPanel(Container container)
    {
        if (contentPanel!=null)
            this.remove(contentPanel);
        this.contentPanel=container;
        this.add(container,BorderLayout.CENTER); 
    }   
}
