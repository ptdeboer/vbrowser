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

package nl.esciencecenter.vlet.gui.table;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import nl.esciencecenter.ptk.GlobalProperties;

/** Combined TableModel and TableColumn Listener */ 

public class TableModelEventHandler implements TableModelListener,TableColumnModelListener
{
    private TablePanel tablePanel;

    TableModelEventHandler(TablePanel tPanel)
    {
        this.tablePanel=tPanel; 
    }
    
    
    // TableModel.tableChanged 
    public void tableChanged(TableModelEvent e)
    {
//        int type = e.getType(); // event type 
//        int col=e.getColumn(); // column event type
//        int rows=e.getFirstRow(); // column event type
//        int rowe=e.getLastRow(); // column event type
        
        tablePanel.updateCellRenderers(); 
        tablePanel.updateCellEditors(); 
        
    }


    // === 
    // Table Column Model events  
    // === 
    
    // currently not used 
    public void columnAdded(TableColumnModelEvent e)
    {
     }

    public void columnRemoved(TableColumnModelEvent e)
    {

    }

    public void columnMoved(TableColumnModelEvent e)
    {
        //Global.debugPrintf(this,"messagePrintln:%s\n",e);
    }
 
    public void columnMarginChanged(ChangeEvent e)
    {
        javax.swing.table.DefaultTableColumnModel colummodel=(DefaultTableColumnModel) e.getSource();
        TableDataProducer prod = this.tablePanel.getDataProducer(); 
        
        for (int i=0;i<colummodel.getColumnCount();i++) 
        {
            TableColumn column = colummodel.getColumn(i); 
            int w=column.getWidth();
            String name=column.getHeaderValue().toString();
            
            //Global.debugPrintf(this,"columnMarginChanged name:%s=%s\n",name,w);
            // store the new column width in the table Presentation: 
            
            this.tablePanel.storeColumnWidth(name,w); 
        }
    }

    public void columnSelectionChanged(ListSelectionEvent e)
    {
        //Global.debugPrintf(this,"columnSelectionChanged:%s\n",e);
    }

}
