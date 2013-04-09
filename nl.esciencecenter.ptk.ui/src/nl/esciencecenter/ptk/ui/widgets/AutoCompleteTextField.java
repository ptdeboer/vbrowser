/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.ui.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import nl.esciencecenter.ptk.Global;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.util.StringUtil;



public class AutoCompleteTextField extends JComboBox
{
    public final static String COMBOBOXEDITED="comboBoxEdited";
    
    public final static String UPDATESELECTION="updateSelection"; 
    		
    private static final long serialVersionUID = 2531178303560053536L;

    private StringList history = new StringList();

    // private String oldText;

    public class CBDocument extends PlainDocument
    {

        private static final long serialVersionUID = -7002767598883985096L;

        public void insertString(int offset, String str, AttributeSet a) throws BadLocationException
        {
            if (str == null)
                return;

            super.insertString(offset, str, a);
            String clear = str.replaceAll("\\p{Cntrl}", "");

            if (!StringUtil.isEmpty(clear))
            {
                completeText();
            }

        }
    }

    public AutoCompleteTextField()
    {        
        init();
    }
    
    private void init()
    {
        history.addUnique("file:///"+Global.getGlobalUserHome());
        
        history.sort(true);

        ComboBoxModel historyListModel = new DefaultComboBoxModel(history.toArray());
        setModel(historyListModel);

        if (getEditor() != null)
        {
            JTextField tf = getTextField();
            if (tf != null)
            {

                tf.setDocument(new CBDocument());

                addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent evt)
                    {
                        if (evt.getActionCommand().equals(COMBOBOXEDITED))
                        {
                            saveHistory();
                        }

                    }
                });
            }
        }
        
        this.setEditable(true); 
    }

    protected void completeText()
    {
        JTextField tf = getTextField();
        String text = tf.getText();

        ComboBoxModel aModel = getModel();
        String current;

//        StringList tmp = new StringList();
//        for (int i = 0; i < aModel.getSize(); i++)
//        {
//            current = aModel.getElementAt(i).toString();
//
//            if (current.toLowerCase().startsWith(text.toLowerCase()))
//            {
//                tmp.addUnique(current);
//            }
//        }
//
//        if (!tmp.isEmpty())
//        {
//            ComboBoxModel tmpListModel = new DefaultComboBoxModel(tmp.toArray());
//            setModel(tmpListModel);
//        }

        for (int i = 0; i < aModel.getSize(); i++)
        {
            current = aModel.getElementAt(i).toString();

            if (current.toLowerCase().startsWith(text.toLowerCase()))
            {
                tf.setText(current);
                tf.setSelectionStart(text.length());
                tf.setSelectionEnd(current.length());
                int old=this.getSelectedIndex();
                
                if (old!=i)
                {
                    String orgCmd=this.getActionCommand();
                    this.setActionCommand(UPDATESELECTION);
                    setSelectedIndex(i);
                    this.setActionCommand(orgCmd);
                }
                break;
            }
        }
    }

    public JTextField getTextField()
    {
        return (JTextField) getEditor().getEditorComponent();
    }

    protected void saveHistory()
    {

        String insertedText = getTextField().getText();
        if (!StringUtil.isEmpty(insertedText) || !insertedText.equals(" "))
        {
            history.addUnique(insertedText);
        }

        history.sort(true);

        ComboBoxModel historyListModel = new DefaultComboBoxModel(history.toArray());
        setModel(historyListModel);
        
        
        int index = history.indexOf(insertedText);
        if (getSelectedIndex()!=index)
        {
            String orgCmd=this.getActionCommand();
            this.setActionCommand(UPDATESELECTION);
            setSelectedIndex(index);
            this.setActionCommand(orgCmd);
        }

    }

    public String getText()
    {
        return this.getTextField().getText(); 
    }

    public void setText(String txt)
    {
        this.getTextField().setText(txt); 
    }

}
