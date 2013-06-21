package widgets;

import javax.swing.JFrame;

import nl.esciencecenter.ptk.ui.widgets.LocationSelectionField;

public class TestLocationSelectionField
{
    public static void main(String args[])
    {
        JFrame frame=new JFrame();
        LocationSelectionField selField=new LocationSelectionField();
        selField.setLocationText("file:/thisisalongdirectoryname/directory/subdirectory/"); 
        frame.getContentPane().add(selField);
        frame.pack(); 
        frame.setVisible(true); 
    }
}
