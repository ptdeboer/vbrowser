package nl.esciencecenter.ptk.ui.widgets;

import java.awt.Font;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.TooManyListenersException;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import java.awt.Color;

import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.io.FileURISyntaxException;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

public class LocationSelectionField extends JPanel implements URIDropTargetLister
{
    private static final long serialVersionUID = 7668815810402466501L;
 
    public class FileLocationEvaluator implements ActionListener, FocusListener
    {
    	private JTextField locationTF;
    	//Color originalBGColor; 
    	
    	public FileLocationEvaluator(JTextField textField)
    	{
    		this.locationTF=textField;
    		//originalBGColor=textField.getBackground(); 
    	}

    	@Override
    	public void actionPerformed(ActionEvent e) 
    	{
    		evaluate(); 
    	}
    	
    	public boolean evaluate()
    	{
    		String text=locationTF.getText();
    		StringHolder reasonH=new StringHolder(); 
    		boolean isValidPath=FSUtil.getDefault().isValidPath(text,reasonH); 
    		if (isValidPath==false)
    		{
    		    showError(reasonH.value);
    		    return false; 
    		}
    		
    		try
    		{
    			java.net.URI uri;
    			uri = FSUtil.getDefault().resolveURI(text);
    			locationTF.setText(uri.getPath());
    			//locationTF.setBackground(originalBGColor);
    			return true; 
    		}
    		catch (FileURISyntaxException e1) 
    		{
    		    showError("Syntax Error: Not a valid path or URI:"+e1.getInput());
    			//locationTF.setBackground(Color.RED);
    			return false;
    		}
    	}

    	public void showError(String errorTxt)
        {
    	    locationTF.setToolTipText(errorTxt); 
        }

        @Override
		public void focusGained(FocusEvent e) 
		{
		}

		@Override
		public void focusLost(FocusEvent e)
		{
			evaluate(); 
		}
    }

    // ========
    // Instance 
    // ========
    
    private JTextField locationTF;
    
    JLabel fileIconLbl; 
    
    public LocationSelectionField() 
    {
        initGui();
        initDnD();
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
            FileLocationEvaluator evaluator=new FileLocationEvaluator(locationTF);
            locationTF.addActionListener(evaluator);
            locationTF.addFocusListener(evaluator);
        }
        
        {
            JLabel fileSelectorIconLbl = new JLabel("|");   
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

    public java.net.URI getLocationURI() throws FileURISyntaxException
    {	
    	// Use FSUtil to resolve URI: 
    	return FSUtil.getDefault().resolveURI(locationTF.getText()); 
    }
    
    public boolean isURI()
    {
    	try
    	{
    		return (getLocationURI()!=null); 
    	}
    	catch (FileURISyntaxException e)
    	{
    		return false; 
    	}
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

    
    /** 
     * Adds default support for dropped URI and URls. 
     */
    protected void initDnD()
    {
        DropTarget dt1=new DropTarget(); 
        DropTarget dt2=new DropTarget(); 

        // enable toolbar and icontext field:  
        this.fileIconLbl.setDropTarget(dt1);
        this.locationTF.setDropTarget(dt2); 
        
        try
        { 
            dt1.addDropTargetListener(new URIDropHandler(this));
            dt2.addDropTargetListener(new URIDropHandler(this));
        }
        catch (TooManyListenersException e)
        {
            ClassLogger.getLogger(this.getClass()).logException(ClassLogger.ERROR, e, "TooManyListenersException:"+e);
        }
    }

    public void notifyDnDDrop(String txt)
    {
        this.locationTF.setText(txt);         
    }
}
