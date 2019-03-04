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

package nl.esciencecenter.vlet.gui.panels.resourcetable;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTable;


/** Table Header popup menu + popup listener */ 
public class HeaderPopupMenu extends JPopupMenu
{
    /** */
    private static final long serialVersionUID = -3678190881308127063L;
    
    // ========================================================================
    // Class Stuff
    // ========================================================================
    
    public enum HeaderCommand
    { 
         INSERT_COLUMN_AFTER,INSERT_COLUMN_BEFORE,DELETE_COLUMN,AUTO_FIT_COLUMNS_ON,AUTO_FIT_COLUMNS_OFF
    }; 
        
    /** Non static ActionListener so that it can access outer class members */ 
    public class ANPopupListener implements ActionListener
    {
        public void actionPerformed(ActionEvent e) 
        {
            //Global.debugPrintln("PopupListener","Action:"+e);
            //Global.debugPrintln("PopupListener","source:"+e.getSource());
            
            String cmdstr=e.getActionCommand();
            String vals[]=cmdstr.split(":");
            cmdstr=vals[0];
            String argstr=null; 
            
            if (vals.length>1) 
                argstr=vals[1]; 
            
            HeaderCommand cmd=HeaderCommand.valueOf(cmdstr);
            
            switch (cmd) 
            {
                case INSERT_COLUMN_BEFORE:
                case INSERT_COLUMN_AFTER:
                    boolean insertBefore=(cmd==HeaderCommand.INSERT_COLUMN_BEFORE); 
                    tablePanel.insertColumn(headerName,argstr,insertBefore);
                    break; 
                case DELETE_COLUMN:
                    tablePanel.removeColumn(argstr); 
                    break;
                case AUTO_FIT_COLUMNS_OFF:
                    tablePanel.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
                    break; 
                case AUTO_FIT_COLUMNS_ON: 
                    tablePanel.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                    break;
            }
        }

    }
    
    // ========================================================================
    // Object Stuff
    // ========================================================================
   
    private ActionListener popupListener=new ANPopupListener();

    private ResourceTable tablePanel;

    /** Header name that was clicked on */ 
    private String headerName; 
    
    public HeaderPopupMenu(ResourceTable table,String header)
    {
        this.tablePanel=table; 
        this.headerName=header; 
        
        this.add(createAddAttrNamesSubMenu());
        this.add(createMenuItem("Delete column",HeaderCommand.DELETE_COLUMN+":"+header));
        add(new JSeparator());
        // add to current menu
        addHeaderOptionsTo(this); 
        // create submenu:
        //this.add(createHeaderOptionsMenu());
    }
    
  
    private JMenuItem createMenuItem(String name, String cmdString)
    {
        JMenuItem mitem = new JMenuItem();
        mitem.setText(name); 
        mitem.setActionCommand(cmdString); 
        mitem.addActionListener(popupListener);
        
        return mitem; 
    }
    private JMenuItem createCheckBoxMenuItem(String name, String cmdString,boolean selected)
    {
        JCheckBoxMenuItem mitem = new JCheckBoxMenuItem();
        mitem.setText(name); 
        mitem.setActionCommand(cmdString); 
        mitem.addActionListener(popupListener);
        mitem.setSelected(selected);
        return mitem; 
    }
   
    private JMenu createAddAttrNamesSubMenu()
    {
        JMenu menu = new JMenu(); 
        menu.setText("Attributes");
        
        // All Headers!
        String names[]=tablePanel.getModel().getAllHeaders();
        
        @SuppressWarnings("unused")
        JMenuItem mitem=null;
        
        if (names!=null)
        {
           for (String name:names)
           {
              boolean present=tablePanel.hasColumn(name); 
              if (present==false)
              {
                  menu.add(mitem=createCheckBoxMenuItem(name,""+HeaderCommand.INSERT_COLUMN_AFTER+":"+name,present));
                  //menu.add(mitem=createCheckBoxMenuItem(name,""+HeaderCommand.INSERT_COLUMN_AFTER+":"+name,present));
              }
              else
                  menu.add(mitem=createCheckBoxMenuItem(name,""+HeaderCommand.DELETE_COLUMN+":"+name,present));
              
           }
        }
        
        return menu;
    }
  
    // JPopupMenu and JMenu only have JComponent as shared parent...
    private void addHeaderOptionsTo(JComponent menu)
    {
        @SuppressWarnings("unused")
        JMenuItem mitem;
        
         //boolean state=(tablePanel.getAutoResizeMode()==JTable.AUTO_RESIZE_OFF);
        boolean state=(tablePanel.getAutoResizeMode()==JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        if (state==false)
            menu.add(mitem=createCheckBoxMenuItem("Auto fit columns",""+HeaderCommand.AUTO_FIT_COLUMNS_ON,state));
        else
            menu.add(mitem=createCheckBoxMenuItem("Auto fit columns",""+HeaderCommand.AUTO_FIT_COLUMNS_OFF,state));
    }

}
