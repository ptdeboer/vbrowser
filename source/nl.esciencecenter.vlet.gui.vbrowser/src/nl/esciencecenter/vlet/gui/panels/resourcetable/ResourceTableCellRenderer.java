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

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import nl.esciencecenter.vbrowser.vrs.data.Attribute;

public class ResourceTableCellRenderer extends DefaultTableCellRenderer 
{
    private static final long serialVersionUID = -7461721298242661750L;

    @Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) 
	{
	    if (value==null)
	        value="?";
	    
	    if (value instanceof Attribute)
	    {
	        value=((Attribute)value).getStringValue(); 
	    }
	    
	    // thiz should be *this* 
	    Component thiz = super.getTableCellRendererComponent(table,value, isSelected, hasFocus, row, column); 
	    
		return thiz;    
	}

}
