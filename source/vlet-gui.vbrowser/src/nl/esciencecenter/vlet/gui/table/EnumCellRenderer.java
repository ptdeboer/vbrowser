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

package nl.esciencecenter.vlet.gui.table;
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
 * $Id: EnumCellRenderer.java,v 1.1 2013/01/22 15:48:05 piter Exp $  
 * $Date: 2013/01/22 15:48:05 $
 */ 
// source: 

//package nl.uva.vlet.gui.table;
//
//import java.awt.Component;
//
//import javax.swing.DefaultComboBoxModel;
//import javax.swing.JComboBox;
//import javax.swing.JTable;
//import javax.swing.table.TableCellRenderer;
//
//import nl.uva.vlet.Global;
//import nl.uva.vlet.data.VAttribute;

//public class EnumCellRenderer extends JComboBox implements TableCellRenderer 
//{
//    public EnumCellRenderer(String[] items) 
//    {
//        super(items);
//    }
//    
//    public EnumCellRenderer() 
//    {
//        super();
//    }
//    public Component getTableCellRendererComponent(JTable table, Object value,
//            boolean isSelected, boolean hasFocus, int row, int column)
//    {
//        if (isSelected) 
//        {
//            setForeground(table.getSelectionForeground());
//            super.setBackground(table.getSelectionBackground());
//        }
//        else 
//        {
//            setForeground(table.getForeground());
//            setBackground(table.getBackground());
//        }
//
//        // Select the current value
//        setSelectedItem(value);
//        return this;
//    }
//    
//    public void set(VAttribute attr)
//    {
//       Global.debugPrintln("EnumCellRenderer","rendering:"+attr); 
//       
//       String name=attr.getName(); 
//       String vals[]=attr.getEnumValues(); 
//    
//       setModel(new DefaultComboBoxModel(vals));
//       
//       if (vals==null)
//           Global.errorPrintln(this,"Error no enumvalues for attribute:"+attr.getName()); 
//    
//       setSelectedIndex(attr.getEnumIndex());
//        
//    
//       setFont(nl.uva.vlet.gui.font.FontUtil.createFont("iconlabel")); // GuiSettings.current.default_label_font);
//       setEditable(this.isEditable && attr.isEditable());
//       //addActionListener(attributeListener);
//       setActionCommand(name);
//    }
//}
