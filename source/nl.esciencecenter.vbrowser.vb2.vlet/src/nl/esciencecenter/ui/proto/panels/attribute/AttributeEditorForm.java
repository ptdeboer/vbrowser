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

package nl.esciencecenter.ui.proto.panels.attribute;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import nl.esciencecenter.ptk.ui.fonts.FontUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.UIGlobal;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.data.VAttributeUtil;


public class AttributeEditorForm extends JDialog
{
    private static final long serialVersionUID = 9136623460001660679L;
    // ---
    AttributePanel infoPanel;
    // package accesable buttons: 
    JButton cancelButton;
    JButton okButton;
    JButton resetButton;
    // data: 
    Attribute[] originalAttributes;
    // ui stuff 
    private JTextField topLabelTextField;
    private JPanel buttonPanel;
    private AttributeEditorController formController;
    private String titleName;
    //private JFrame frame=null; 
    private JPanel mainPanel;
	private boolean isEditable;
    
    private void initGUI(Attribute attrs[])
    {
        try
        {
            this.setTitle(this.titleName); 
            
            BorderLayout thisLayout = new BorderLayout();
            this.getContentPane().setLayout(thisLayout); 
            // this.setSize(398, 251);
            
            // mainPanel: 
            /*{
               mainPanel=new JPanel(); 
               mainPanel.setLayout(new BorderLayout()); 
               this.getContentPane().add(mainPanel,BorderLayout.CENTER);
            }*/
            
            Container rootContainer = this.getContentPane(); 
            
            {
                topLabelTextField = new JTextField();
               
                rootContainer.add(topLabelTextField, BorderLayout.NORTH);
                
                topLabelTextField.setText(this.titleName);
                topLabelTextField.setEditable(false);
                topLabelTextField.setFocusable(false);
                topLabelTextField.setBorder(BorderFactory
                        .createEtchedBorder(BevelBorder.RAISED));
                topLabelTextField.setFont(FontUtil.createFont("dialog")); // GuiSettings.current.default_label_font) ; 
                // new java.awt.Font("Lucida Sans", 1,14));
                topLabelTextField.setHorizontalAlignment(SwingConstants.CENTER);
                topLabelTextField.setName("huh");
            }
            {
                infoPanel=new AttributePanel(attrs,isEditable);  
                rootContainer.add(infoPanel,BorderLayout.CENTER);
                //infoPanel.setAttributes(attrs,true); 
            }
            {
                buttonPanel = new JPanel();
                rootContainer.add(buttonPanel, BorderLayout.SOUTH);
                {
                    okButton = new JButton();
                    buttonPanel.add(okButton);
                    okButton.setText("Accept");
                    okButton.addActionListener(formController);
                    okButton.setEnabled(this.isEditable);
                }
                {
                    resetButton = new JButton();
                    buttonPanel.add(resetButton);
                    resetButton.setText("Reset");
                    resetButton.addActionListener(formController);
                }
                {
                    cancelButton = new JButton();
                    buttonPanel.add(cancelButton);
                    cancelButton.setText("Cancel");
                    cancelButton.addActionListener(formController);
                }
            }
            validate(); 
            // update size: 
            //infoPanel.setSize(infoPanel.getPreferredSize());
            Dimension size = this.getPreferredSize(); 
            // bug in FormLayout ? Last row is now shown
            // add extra space:
            size.height+=32;
            size.width+=128; // make extra wide
            setSize(size); 
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

//  ==========================================================================
//  Constructor 
//  ==========================================================================
    
    private void init(String titleName, Attribute attrs[])
    {
        //frame = new JFrame(); 
        
        this.originalAttributes=attrs;
        this.titleName=titleName; 
        
        attrs=VAttributeUtil.duplicateArray(attrs);  // use duplicate to edit;
        
        // Must first create ActionListener since it is used in initGui...
        this.formController = new AttributeEditorController(this); 
        this.addWindowListener(formController);
        
        // only set to editable if there exists at least one editable attribute
        this.isEditable=false; 
        
        for (Attribute attr:attrs)
        {
        	if ((attr!=null) && (attr.isEditable()==true))
        		this.isEditable=true; 
        }
        initGUI(attrs);
        formController.update();
        
        //frame.add(this); 
        //frame.pack(); 
        
//        Rectangle windowRec=GuiSettings.getOptimalWindow(this); 
//        //this.setPreferredSize(windowRec.getSize());
//        this.setLocation(windowRec.getLocation());
        this.validate(); 
        // No auto-show: this.setVisible(true);
    }
    
    public AttributeEditorForm(String titleName, Attribute attrs[]) 
    {
        super();
//        UIPlatform.getPlatform().getWindowRegistry().register(this);
        init(titleName,attrs);
    }
    
    public AttributeEditorForm() 
    {
//        UIPlatform.getPlatform().getWindowRegistry().register(this);
        //init("Example Attribute Editor",null);
    }
    
//  ==========================================================================
//  
//  ==========================================================================

    public void setAttributes(Attribute[] attributes)
    {
        this.originalAttributes=attributes;
        
        // use duplicate to edit:
        attributes=VAttributeUtil.duplicateArray(attributes);
        
        this.infoPanel.setAttributes(new AttributeSet(attributes),true);
        
        validate();
    }

    public synchronized void Exit()
    {
        // notify waiting threads: waitForDialog().
        this.notifyAll();
        
        myDispose();
    }
    
    private void myDispose()
    {
       this.dispose(); 
    }
    
//  ==========================================================================
//  main 
//  ==========================================================================
	
    /**
     * Static method to interactively ask user for attribute settings 
     * Automatically call Swing invoke later if current thread is not the Gui Event thread. 
     * 
     */
    
    public static Attribute[] editAttributes(final String titleName, final Attribute[] attrs, 
            final boolean returnChangedAttributesOnly)
    {        
		final AttributeEditorForm dialog = new AttributeEditorForm(); 
		   
    	Runnable formTask=new Runnable()
	    {
    		public void run()
 	        {
    			// perform init during GUI thread 
    			dialog.init(titleName,attrs);
    				
 	            //is now in constructor: dialog.setEditable(true);
 	            // modal=true => after setVisible, dialog will not return until windows closed
 	            dialog.setModal(true);
 	            dialog.setAlwaysOnTop(true); 
 	            dialog.setVisible(true);

 	            synchronized(this)
 	            {
 	            	this.notifyAll();
 	            }	
 	        }
	    };

 	    /** Run during gui thread of use Swing invokeLater() */ 
 	        
 	    if (UIGlobal.isGuiThread())
 	    {
 	    	formTask.run(); // run directly:
 	    }
 	    else
 	    {
 	    	// go background: 
 	    	
 	    	SwingUtilities.invokeLater(formTask);
 	    	
 	    	synchronized(formTask)
 	    	{
 	    		try
				{
 	    			//System.err.println("EditForm.wait()");
					formTask.wait();
				}
				catch (InterruptedException e)
				{
					getLogger().logException(ClassLogger.ERROR,e,"--- Interupted ---\n");
				}
 	    	}
 	    }
 	    
 	   // wait 
         
         if  (dialog.formController.isOk==true) 
         {
             // boolean update=dialog.hasChangedAttributes(); 
             if (returnChangedAttributesOnly)
                 return dialog.infoPanel.getChangedAttributes();
             else
                 return dialog.infoPanel.getAttributes(); 
         }
         else
         {
             return null;
         }
 	}

    private static ClassLogger getLogger()
    {
        return ClassLogger.getLogger(AttributeEditorForm.class); 
    }

    public boolean hasChangedAttributes()
    {
        return this.infoPanel.hasChangedAttributes(); 
    }
}
