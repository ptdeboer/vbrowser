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

package nl.esciencecenter.vlet.gui.viewers.grid.replicaviewer;

import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;


import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vlet.gui.panels.resourcetable.ResourceTable;
import nl.esciencecenter.vlet.gui.panels.resourcetable.TablePopupMenu;
import nl.esciencecenter.vlet.gui.viewers.grid.replicaviewer.ReplicaDataModel.ReplicaStatus;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;

public class ReplicaPopupMenu extends TablePopupMenu
{
    private static final long serialVersionUID = 4937904778804370242L;
    
    public static final String OPEN_PARENT      = "openParent"; 
    public static final String DELETE           = "delete";
    public static final String UNREGISTER       = "unregister";
    public static final String KEEP             = "keep";
    public static final String DELETE_SELECTION = "deleteSelection";
    public static final String KEEP_SELECTION   = "keepSelection";
    public static final String SHOW_PROPERTIES  = "showProperties"; 
    
    // Multi action item: is updated for Replica status 
    private JMenuItem actionItem;
    private JMenuItem openItem; 
    private JMenuItem propItem; 
    
    public ReplicaPopupMenu(ReplicaController controller)
    {
        super();
        actionItem=new JMenuItem("Delete");
        this.add(actionItem);
        actionItem.addActionListener(controller); 
        
        this.add(new JSeparator()); 
        
        openItem=new JMenuItem("Open Storage Location"); 
        openItem.setActionCommand("openParent");
        openItem.addActionListener(controller);
        this.add(openItem); 
        
        propItem=new JMenuItem("Show replica Properties"); 
        propItem.setActionCommand("showProperties");
        propItem.addActionListener(controller);
        this.add(propItem);
        
    }
    
    @Override
    public void updateFor(ResourceTable table, MouseEvent e,boolean canvasMenu)
    {
        int rows[]=table.getSelectedRows();
        actionItem.setEnabled(true);
        
        if ((rows!=null) && (rows.length>=2)) 
        {
            boolean allDeletes=true;  
            for (int row:rows)
                if (isToBeDeleted(table,row)==false)
                {
                    allDeletes=false;  
                    break;
                }
            
            if (allDeletes==true)
            {
                // Keep All 
                actionItem.setText("Keep Replicas");
                actionItem.setActionCommand(KEEP_SELECTION); 
            }
            else
            {
                // selection menu: 
                actionItem.setText("Delete Replicas");
                actionItem.setActionCommand(DELETE_SELECTION); 
            }
            
        }
        else
        {
            String rowKey=table.getKeyUnder(e.getPoint());
            boolean isnew=isNew(table,table.getModel().getRowIndex(rowKey));
            boolean hasError=hasError(table,table.getModel().getRowIndex(rowKey));
            
            openItem.setActionCommand(OPEN_PARENT+":"+rowKey);
 
            openItem.setEnabled(isnew==false);
            
            propItem.setActionCommand(SHOW_PROPERTIES+":"+rowKey);
            propItem.setEnabled(isnew==false);
            
            if (rowKey!=null)
            {
                if (isToBeDeleted(table,table.getModel().getRowIndex(rowKey)))
                {
                    actionItem.setText("Keep Replica");
                    actionItem.setActionCommand(KEEP+":"+table.getKeyUnder(e.getPoint()));
                }
                else if (isToBeUnregistered(table,table.getModel().getRowIndex(rowKey)))
                {
                    actionItem.setText("Keep Replica");
                    actionItem.setActionCommand(KEEP+":"+table.getKeyUnder(e.getPoint()));
                }
                else
                {
                    if (hasError==false)
                    {
                        actionItem.setText("Delete Replica");
                        actionItem.setActionCommand(DELETE+":"+table.getKeyUnder(e.getPoint()));
                    }
                    else
                    {
                        actionItem.setText("Unregister Replica");
                        actionItem.setActionCommand(UNREGISTER+":"+table.getKeyUnder(e.getPoint()));
                    }
                }
            }
            else
            {
                // miss clicked ? 
                actionItem.setText("?");
                actionItem.setActionCommand("?"); 
                actionItem.setEnabled(false); 
            }
        }
    }

    private boolean isToBeDeleted(ResourceTable table, int row)
    {
        if (row<0)
            return false;
        
        String stat=table.getModel().getAttrStringValue(row,VAttributeConstants.ATTR_STATUS);
        
        if (StringUtil.equalsIgnoreCase(stat,ReplicaStatus.DELETE))
            return true;
    
        return false; 
    }
    
    
    private boolean isToBeUnregistered(ResourceTable table, int row)
    {
        if (row<0)
            return false;
        
        String stat=table.getModel().getAttrStringValue(row,VAttributeConstants.ATTR_STATUS);
        
        if (StringUtil.equalsIgnoreCase(stat,ReplicaStatus.UNREGISTER))
            return true;
    
        return false; 
    }
    
    private boolean isNew(ResourceTable table, int row)
    {
        if (row<0)
            return false; 
        
        String stat=table.getModel().getAttrStringValue(row,VAttributeConstants.ATTR_STATUS);
        
        if (StringUtil.equalsIgnoreCase(stat,ReplicaStatus.NEW))
            return true;
        
        return false; 
    }
    
    private boolean hasError(ResourceTable table, int row)
    {
        if (row<0)
            return false; 
        
        String stat=table.getModel().getAttrStringValue(row,VAttributeConstants.ATTR_STATUS);
        
        if (StringUtil.equalsIgnoreCase(stat,ReplicaStatus.ERROR))
            return true;
        
        return false; 
    }
    

}
