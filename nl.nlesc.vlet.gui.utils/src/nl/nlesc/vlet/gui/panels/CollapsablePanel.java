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
 * $Id: CollapsablePanel.java,v 1.1 2013/01/22 15:42:16 piter Exp $  
 * $Date: 2013/01/22 15:42:16 $
 */ 
// source: 

package nl.nlesc.vlet.gui.panels;

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