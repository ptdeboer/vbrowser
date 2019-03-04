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

package nl.esciencecenter.vlet.gui.panels.acldialog;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.gui.UILogger;
import nl.esciencecenter.vlet.gui.proxyvrs.ProxyNode;

public class ACLPanelController implements ActionListener, WindowListener
{
    private ACLPanel aclPanel=null; 

    public ACLPanelController(ACLPanel panel)
    { 
        this.aclPanel=panel; 
    }

    public void actionPerformed(ActionEvent e)
    {
        String cmd=e.getActionCommand(); 
        Component comp=(Component)e.getSource(); 
        
        //Global.debugPrintln("ACLPanelController","cmd="+cmd);
        if ((cmd.length()>4) && (cmd.substring(0,4).compareTo("add:")==0))
        {
            String entityName=cmd.substring(4,cmd.length());
            addEntity(entityName); 
        }
        else if (cmd.compareTo(ACLPanel.ADD)==0)
        {
            JPopupMenu menu=createEntityMenu(); 
            menu.show(comp,5,5); 
        }
        else if (cmd.compareTo(ACLPanel.DELETE)==0)
        {
            deleteSelectedRows(); 
        }
        else if (cmd.compareTo(ACLPanel.APPLY)==0)
        {
            apply(); 
        }
        else if (cmd.compareTo(ACLPanel.REREAD)==0)
        {
            reread(); 
        }
        else if (cmd.compareTo(ACLPanel.ACCEPT)==0)
        {
            apply(); 
            close();
        }
        else if (cmd.compareTo(ACLPanel.CANCEL)==0)
        {
            close();
        }
    }

    private void deleteSelectedRows()
    {
        int rows[]=aclPanel.getTable().getSelectedRows();
        aclPanel.getModel().delRows(rows);
    }

    private void reread()
    {
        try
        {
            Attribute[][] acl;
            acl = this.aclPanel.getNode().getACL();
            this.aclPanel.getModel().setACL(acl);
            this.aclPanel.getTable().initColumns(); 
        }
        catch (VrsException e)
        {
            handle(e); 
        }
    }

    private void close()
    {
        aclPanel.close(); 
    }

    private void apply()
    {
        try
        {
            Attribute[][] acl = aclPanel.getModel().getACL();
            this.aclPanel.getNode().setACL(acl);
            
            reread();
        }
        catch (VrsException e)
        {
           handle(e); 
        }
    }

    private JPopupMenu createEntityMenu()
    {
        JPopupMenu popupmenu=new JPopupMenu();
        JComponent menu=popupmenu; 
        
        Attribute ents[]=aclPanel.getACLEntities();
        JMenuItem mitem=null;
        
        int maxMenuItems=30; 
        int itemnr=0; 
        
        if (ents==null)
        {
            menu.add(mitem=createMenuItem(this,"None",null));
            mitem.setEnabled(false); 
        }
        else
        {
          for (Attribute attr:ents)
          {
              String name=attr.getStringValue(); 
              menu.add(mitem=createMenuItem(this,name,"add:"+name)); 
              itemnr++; 
              
              if (itemnr>maxMenuItems)
              {
                  mitem=new JMenu("More");
                  menu.add(mitem); 
                  menu=mitem;
                  itemnr=0; 
              }
          }
        }
        
        return popupmenu;
    }
    

    private static JMenuItem createMenuItem(ACLPanelController listener,String name,String cmd)
    {
        JMenuItem mitem = new JMenuItem();
        mitem.setText(name); 
        mitem.setActionCommand(cmd); 
        mitem.addActionListener(listener); 
        return mitem; 
    }
    
    public void addEntity(String entityName)
    {
        Attribute entityAttr=null; 
        
        Attribute[] aclEntities = aclPanel.getACLEntities(); 
        
        for (int i=0;i<aclEntities.length;i++) 
        {
            if (aclEntities[i].getStringValue().compareTo(entityName)==0) 
            {
                entityAttr=aclEntities[i]; 
            }
        }
        
        if (entityAttr==null) 
        {
            UILogger.errorPrintf(this,"Couldn't find entity:%s\n",entityName); 
            return; 
        }
        
        ProxyNode node=aclPanel.getNode();
        
        try
        {
            Attribute record[]=node.createACLRecord(entityAttr,false);
            // Add data row obly (No Row Object!)
            aclPanel.getModel().addACLRecord(record); 
            
        }
        catch (VrsException e)
        {
           handle(e); 
        } 
        
        //add record:
    }

    private void handle(VrsException e)
    {
        aclPanel.handle(e); 
    }

    public void windowOpened(WindowEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        close(); 
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowDeactivated(WindowEvent e)
    {
    }
    

}
