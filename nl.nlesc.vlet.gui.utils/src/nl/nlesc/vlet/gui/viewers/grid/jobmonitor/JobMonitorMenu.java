/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */ 
// source: 

package nl.nlesc.vlet.gui.viewers.grid.jobmonitor;

import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JSeparator;

import nl.nlesc.vlet.gui.panels.resourcetable.ResourceTable;
import nl.nlesc.vlet.gui.panels.resourcetable.TablePopupMenu;

public class JobMonitorMenu extends TablePopupMenu
{
    private static final long serialVersionUID = 4730955073412271989L;
    private JMenuItem actionItem;

    private JMenuItem openJobItem;
    
    public JobMonitorMenu(JobMonitorController controller)
    {
        super();
        
        actionItem=new JMenuItem("Refresh All");
        this.add(actionItem);
        actionItem.setActionCommand(JobMonitor.ACTION_REFRESH_ALL); 
        actionItem.addActionListener(controller); 
        
        this.add(new JSeparator());
        openJobItem=new JMenuItem("Open Job");
        this.add(openJobItem);
        openJobItem.setActionCommand(JobMonitor.ACTION_OPEN_JOB); 
        openJobItem.addActionListener(controller); 
        openJobItem.setEnabled(false); 
        
        this.add(new JSeparator()); 
    }
    
    @Override
    public void updateFor(ResourceTable table, MouseEvent e,boolean canvasMenu)
    {
    	String rowKey=table.getKeyUnder(e.getPoint()); 
    	
    	if ((rowKey==null) || (canvasMenu==true))
    	{
    		openJobItem.setEnabled(false); 
    		rowKey="";
    	}
    	else
    	{
    		openJobItem.setEnabled(true); 
    	}
    	
		openJobItem.setActionCommand(JobMonitor.ACTION_OPEN_JOB+","+rowKey); 
    }
    
}
