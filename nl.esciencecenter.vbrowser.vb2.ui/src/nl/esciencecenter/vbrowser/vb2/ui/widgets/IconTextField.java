package nl.esciencecenter.vbrowser.vb2.ui.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class IconTextField extends JPanel implements ActionListener 
{
    private static final long serialVersionUID = -3502306954828479242L;
    private AutoCompleteTextField textField;
    private JLabel iconLabel;
    private ActionListener textFieldListener;
    private String comboBoxEditedCommand;
    private String comboBoxUpdateSelectionCommand;

    public IconTextField()
    {
    	super(); 
        initGUI(); 
        initDnD();  
    }
    
    private void initGUI() 
    {
        {
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        }
        {
            iconLabel = new JLabel();
            this.add(iconLabel);
        }
        {
            textField = new AutoCompleteTextField();
            this.add(textField);
            textField.setText("TextField");
            textField.setLocation(16,0); 
        }
        
        // move border from textfield to panel: 
        this.setBackground(textField.getBackground()); 
        this.setBorder(textField.getBorder()); 
        textField.setBorder(null); 
        
    }
    
    private void initDnD()
    {
//        // One For All: Transfer Handler: 
//        setTransferHandler(VTransferHandler.getDefault());
//            
//        // reuse draglistener from iconsPanel:
//        DragSource dragSource=DragSource.getDefaultDragSource();
//        DragGestureListener dragListener = new VComponentDragGestureListener(this);
//         
//        dragSource.createDefaultDragGestureRecognizer(
//                    this, DnDConstants.ACTION_COPY_OR_MOVE, dragListener );
//           
//        // I am also a DROP target: 
//        setDropTarget(new NodeDropTarget(this));
//        // Have to set Keymapping to my component 
//        GuiSettings.setDefaultKeymappings(this); 
    }
    
    public void setText(String txt)
    {
        this.textField.setText(txt); 
    }
    
    public void setIcon(Icon icon)
    {
        iconLabel.setIcon(icon);
        this.revalidate();
        this.repaint(); 
    }
    
    public void setComboActionCommand(String str)
    {
        this.textField.setActionCommand(str); 
    }
    
    public String getText()
    {
        return this.textField.getText(); 
    }


    public void setURI(java.net.URI uri)
    {
        this.setText(uri.toString());
    }
 
    public void setTextActionListener(ActionListener listener)
    {
        // wrap textfield listener: 
        this.textFieldListener=listener; 
        this.textField.removeActionListener(this);
        this.textField.addActionListener(this);
    }
   
    public void setComboEditedCommand(String str)
    {
        this.comboBoxEditedCommand=str;   
    }
    
    @Override
    public void actionPerformed(ActionEvent e)
    {
        String cmd=e.getActionCommand();
        
        if (cmd.equals(AutoCompleteTextField.COMBOBOXEDITED)) 
            cmd=this.comboBoxEditedCommand;
        
        if (cmd.equals(AutoCompleteTextField.UPDATESELECTION))  
            cmd=this.comboBoxUpdateSelectionCommand;  
            
        if (cmd==null)
            return; // filter out combo command.  
        
        ActionEvent wrapEvent=new ActionEvent(e.getSource(),
                e.getID(),
                cmd,
                e.getWhen(),
                e.getModifiers());
        
        this.textFieldListener.actionPerformed(wrapEvent); 
    }
}
