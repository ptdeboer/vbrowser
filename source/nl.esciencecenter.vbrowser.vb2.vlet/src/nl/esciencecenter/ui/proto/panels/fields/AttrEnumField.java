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

package nl.esciencecenter.ui.proto.panels.fields;

import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeType;

public class AttrEnumField extends JStringComboBox implements IAttributeField
{
    private static final long serialVersionUID = -2524144091178443352L;
    boolean enumEditable = false; // whether enum types are editable

    public AttrEnumField()
    {
        super(new String[0]);
        init();
    }

    public AttrEnumField(String name, String[] vals)
    {
        super(vals);
        setName(name);
    }

    private void init()
    {
        StringComboBoxModel model = new StringComboBoxModel();
        setModel(model);
    }

    public void setValues(String[] values)
    {
        if (values == null)
            values = new String[0];

        this.setModel(new StringComboBoxModel(values));
    }
    
    public StringComboBoxModel getModel()
    {
        return (StringComboBoxModel)super.getModel(); 
    }
    
    public void addValue(String enumVal)
    {
        this.getModel().addElement(enumVal);
    }

    public void removeValue(String enumVal)
    {
        ((StringComboBoxModel) this.getModel()).removeElement(enumVal);
    }

    public void setValue(String txt)
    {
        this.getModel().setSelectedItem(txt);
    }

    public String getName()
    {
        return super.getName();
    }

    public String getValue()
    {
        Object obj = this.getSelectedItem();
        if (obj != null)
            return obj.toString();
        return null;
    }

    public void updateFrom(Attribute attr)
    {
        this.setValue(attr.getStringValue());
    }

    // public void setEditable(boolean flag)
    // {
    // this.setEditable(flag);
    // }

    public AttributeType getVAttributeType()
    {
        return AttributeType.ENUM;
    }

    /**
     * Selectable => drop down option is 'selectable'. optionsEditable = drop
     * down selection entries are editable as well !
     */
    public void setEditable(boolean selectable, boolean optionsEditable)
    {
        this.setEnabled(selectable);
        this.setEditable(optionsEditable);
    }

}
