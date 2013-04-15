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

package nl.nlesc.vlet.gui.panels.fields;

import javax.swing.JCheckBox;

import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.data.VAttributeType;

public class AttrCheckBoxField extends JCheckBox implements IAttributeField
{
    private static final long serialVersionUID = 3100398728004063981L;

    // needed by jigloo
    public AttrCheckBoxField()
    {
        super();
    }

    public AttrCheckBoxField(String name, boolean value)
    {
        super();
        this.setName(name);
        this.setSelected(value);
    }

    public String getName()
    {
        return super.getName();
    }

    public String getValue()
    {
        return "" + this.isSelected();
    }

    public boolean getBooleanValue()
    {
        return this.isSelected();
    }

    public void updateFrom(VAttribute attr)
    {
        this.setSelected(attr.getBooleanValue());
    }

    public VAttributeType getVAttributeType()
    {
        return VAttributeType.BOOLEAN;
    }

    public void setEditable(boolean flag)
    {
        this.setEnabled(flag);
    }

}
