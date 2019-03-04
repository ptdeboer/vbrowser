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

package nl.esciencecenter.vlet.gui.widgets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.gui.UIGlobal;


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
        history.addUnique("myvle:/");
        history.addUnique("file:"+VletConfig.getUserHome());
        history.addUnique("file:/"); 
        
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
                        if (evt.getActionCommand().equals("comboBoxEdited"))
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

    public void setText(final String txt)
    {
        // got deadlock when updating text field outside GUI event thread: 
        if (UIGlobal.isGuiThread()==false)
        {
            Runnable updater=new Runnable()
            {
                public void run()
                {
                    setText(txt);
                }
            };
            
            UIGlobal.swingInvokeLater(updater);
            return; 
        }
        
        this.getTextField().setText(txt); 
    }

}
