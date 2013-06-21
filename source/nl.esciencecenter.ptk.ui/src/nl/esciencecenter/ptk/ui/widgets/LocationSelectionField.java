package nl.esciencecenter.ptk.ui.widgets;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.Color;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

public class LocationSelectionField extends JPanel
{
    private static final long serialVersionUID = 7668815810402466501L;
    
    private JTextField locationTF;
    
    JLabel fileIconLbl; 
    
    public LocationSelectionField() 
    {
        initGui();
    }

    public void initGui()
    {
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        setBorder(new LineBorder(Color.GRAY));
        
        {
            fileIconLbl = new JLabel("");
            add(fileIconLbl);
            fileIconLbl.setIcon(loadIcon("files/directory16.png")); 
        }
        
        {
            locationTF = new JTextField();
            locationTF.setForeground(Color.BLACK);
            add(locationTF);
            locationTF.setColumns(10);
            locationTF.setBorder(new EmptyBorder(2,2,2,2)); 
        }
        
        {
            JLabel fileSelectorIconLbl = new JLabel("X");   
            add(fileSelectorIconLbl);
        }
    }
    
    private Icon loadIcon(String iconUrl)
    {
        URL res = getClass().getClassLoader().getResource(iconUrl);
        return new ImageIcon(res);

    }

    // Accepts both URI and non URI text. 
    public void setLocationText(String txt)
    {
        this.locationTF.setText(txt); 
    }

    public void setLocationURI(java.net.URI uri)
    {
        this.locationTF.setText(uri.toString());  
    }

    public java.net.URI getLocationURI() throws URISyntaxException
    {
        // try to parse string as is:
        return new java.net.URI(locationTF.getText()); 
    }
    
    public void setIcon(Icon icon)
    {
        fileIconLbl.setIcon(icon); 
    }
    
    public void setTextFont(Font font)
    {
        this.locationTF.setFont(font); 
    }

    public void setLocationActionCommand(String commandString)
    {
        this.locationTF.setActionCommand(commandString);
    }

    public void addLocationActionListener(ActionListener controller)
    {
        this.locationTF.addActionListener(controller); 
    }

    public String getLocationText()
    {
        return locationTF.getText();
    }

    public void setLocationEnabled(boolean value)
    {
        locationTF.setEnabled(value); 
    }
    
    public void setLocationEditable(boolean value)
    {
        locationTF.setEditable(value); 
    }

}
