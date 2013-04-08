package nl.uva.vlet.gui.panels.fields;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

public class JStringComboBox extends JComboBox//<String> 
{
    public JStringComboBox(String[] vals)
    {
        super(new StringComboBoxModel(vals)); 
    }

    public JStringComboBox()
    {
        super();
    }

    public static class StringComboBoxModel extends DefaultComboBoxModel//<String>
    {

        public StringComboBoxModel(String[] values)
        {
            super(values);
        }

        public StringComboBoxModel()
        {
           super();
        }

    }
    
}
