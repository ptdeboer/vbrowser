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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JViewport;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nl.esciencecenter.vlet.gui.GuiSettings;
import nl.esciencecenter.vlet.gui.UILogger;


public class TableMouseListener implements MouseListener
{
    private ResourceTable table;

    public TableMouseListener(ResourceTable source)
    {
        table=source;
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Component comp=(Component)e.getSource();
        
        // click on header: 
        if (comp instanceof JTableHeader)
        { 
          //  click on header
          if (GuiSettings.isSelection(e))
          {
              String name=this.getColumnNameOf(e); 
              if (name==null)
                  return; // no column; 

              table.sortColumn(name);
              
           }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        Component comp=(Component)e.getSource();
        //Point clickPoint=e.getPoint(); 
       
        debugPrintf("MouseClicked:%s\n",e);
        
        //boolean ctrl=((e.getModifiersEx() & e.CTRL_DOWN_MASK) !=0);
        Container parent = comp.getParent(); 
        
        // Show Header Popup! 
        if ((comp instanceof JTableHeader) && (GuiSettings.isPopupTrigger(e)))
        {
            String  name=this.getColumnNameOf(e); 
            if(name!=null)
            {
                HeaderPopupMenu popupMenu=new HeaderPopupMenu(table,name); 
                popupMenu.show(comp,e.getX(),e.getY());
            }
            else
            {
                debugPrintf("No Column Header name!:%s\n",e);
            }
        }
        else if (comp.equals(table))
        {
            if (GuiSettings.isPopupTrigger(e))
            {
                TablePopupMenu popupMenu=table.getPopupMenu(e,false); 
                if (popupMenu!=null)
                    popupMenu.show(comp,e.getX(),e.getY());
            }
        }
        else if (comp.equals(parent))
        {
            if (GuiSettings.isPopupTrigger(e))
            {
                TablePopupMenu popupMenu=table.getPopupMenu(e,true); 
                if (popupMenu!=null)
                    popupMenu.show(comp,e.getX(),e.getY());
            }
        }
        else if (comp instanceof javax.swing.JScrollPane)
        {
            // Check whether resourcepanel is embedded in a ScrollPane 
            JViewport viewPort = ((javax.swing.JScrollPane)comp).getViewport();   
            Component viewPortView=null;
            if (viewPort!=null)
                viewPortView= viewPort.getView(); 

            if (viewPortView==table)
            {
                // show!
                TablePopupMenu popupMenu=table.getPopupMenu(e,true); 
                if (popupMenu!=null)
                    popupMenu.show(comp,e.getX(),e.getY());
            }
            
        }
        else
        {
            // System.err.printf("Click on:%s\n",comp); 
            // System.err.printf("Table Parent=:%s\n",table.getParent()); 
        }
    }

    private void debugPrintf(String format,Object... args)
    {
      UILogger.debugPrintf(this,format,args); 
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }
    
    private String getColumnNameOf(MouseEvent e)
    {
        // Warning: Apply coordinates to VIEW model ! 
        TableColumnModel columnModel = table.getColumnModel();
        
        int colnr=columnModel.getColumnIndexAtX(e.getX());
        
        if (colnr<0) 
            return null; 
        
        TableColumn column = columnModel.getColumn(colnr); 
        String name=(String)column.getHeaderValue();
        return name; 
    }
    

}
