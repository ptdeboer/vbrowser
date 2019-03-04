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

package nl.esciencecenter.vlet.gui.dialog;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import nl.esciencecenter.ptk.crypt.Secret;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.gui.GuiSettings;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.gui.UILogger;
import nl.esciencecenter.vlet.gui.UIPlatform;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRSContext;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 *
 */
public class AuthenticationDialog extends javax.swing.JDialog implements ActionListener, FocusListener, WindowListener
{
    private static final long serialVersionUID = -1605502578076510125L;
    private JPanel buttonsPanel;
	private JPanel AuthenticationPanel;
	private JButton OKbutton;
	private JLabel passwordLabel;
	private JLabel userLabel;
	private JTextArea messageTextField;
	private JTextField userNameTextField;
	private JPasswordField passphraseField;
	private JPasswordField passwordField;
	private JLabel passphraseLabel;
	private JButton cancelButton;
    private ServerInfo authInfo;
    private boolean cancel=false;
	private String message;  
    
	/**
	* Auto-generated main method to display this JDialog
	*/
	private void initGUI() 
    {
		try
        {
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
            this.setSize(402, 173);
            {
                AuthenticationPanel = new JPanel();
                this.getContentPane().add(
                    AuthenticationPanel,
                    BorderLayout.CENTER);
                FormLayout AuthenticationPanelLayout = new FormLayout(
                    "10px, right:p, 5px, p:grow, 10px",
                    "10px, fill:p, 10px, max(p;5px), max(p;5px), max(p;5px)");
                AuthenticationPanel.setLayout(AuthenticationPanelLayout);
                AuthenticationPanel.setPreferredSize(new java.awt.Dimension(400,140));

                {
                    userLabel = new JLabel();
                    AuthenticationPanel.add(userLabel, new CellConstraints(
                        "2, 4, 1, 1, default, default"));
                    userLabel.setText("User");
                }
                {
                    passwordLabel = new JLabel();
                    AuthenticationPanel.add(passwordLabel, new CellConstraints(
                        "2, 5, 1, 1, default, default"));
                    passwordLabel.setText("Password");
                }
                {
                    passphraseLabel = new JLabel();
                    AuthenticationPanel.add(
                        passphraseLabel,
                        new CellConstraints("2, 6, 1, 1, default, default"));
                    passphraseLabel.setText("Passphrase");
                    passphraseLabel.setVisible(false); 
                    
                }
                {
                    userNameTextField = new JTextField();
                    AuthenticationPanel.add(
                        userNameTextField,
                        new CellConstraints("4, 4, 1, 1, default, default"));
                    userNameTextField.setText("");
                    userNameTextField.addFocusListener(this);
                    userNameTextField.addActionListener(this);
                }
                {
                    passwordField = new JPasswordField();
                    AuthenticationPanel.add(passwordField, new CellConstraints(
                        "4, 5, 1, 1, default, default"));
                    passwordField.addFocusListener(this);
                    passwordField.addActionListener(this);
                }
                {
                    passphraseField = new JPasswordField();
                    AuthenticationPanel.add(
                        passphraseField,
                        new CellConstraints("4, 6, 1, 1, default, default"));
                    passphraseField.addFocusListener(this);
                    passphraseField.addActionListener(this);
                    passphraseField.setEnabled(false); 
                    passphraseField.setVisible(false); // setBackground(Color.GRAY);
                }
                {
                    messageTextField = new JTextArea();
                    AuthenticationPanel.add(
                        messageTextField,
                        new CellConstraints("2, 2, 3, 1, default, default"));
                    messageTextField.setText(message);
                    messageTextField.setEditable(false); 
                    messageTextField.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
                    messageTextField.setBackground(UIGlobal.getGuiSettings().textfield_non_editable_background_color);
                }
            }
            {
                buttonsPanel = new JPanel();
                this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
                {
                    OKbutton = new JButton();
                    buttonsPanel.add(OKbutton);
                    OKbutton.setText("OK");
                    OKbutton.addActionListener(this);
                }
                {
                    cancelButton = new JButton();
                    buttonsPanel.add(cancelButton);
                    cancelButton.setText("Cancel");
                    cancelButton.addActionListener(this);
                }
            }
		}
		catch (Exception e)
		{
			UILogger.logException(this,ClassLogger.ERROR,e,"InitGUI Exception!"); 
		}
		// auto-resize: 
		//this.validate(); 
        //this.setSize(this.getPreferredSize()); 
	}

    /*public AuthenticationDialog(JFrame frame) 
    {
        super(frame);
        init(null); 
    }*/
    
    private void init(String message,ServerInfo info)
    {
        String user=null;
        
        this.message=message; 
        
        if (info==null)
            authInfo=new ServerInfo(VRSContext.getDefault(),new VRL("file","localhost","/dummypath"));
        else
            authInfo=info; 
        
        user=authInfo.getUsername();
        
        if (user==null) 
        {
            user=VletConfig.getUserName();  
            authInfo.setUsername(user);
        }
        
        initGUI();
        
        this.userNameTextField.setText(user);
        this.messageTextField.setText(message); 
     
        this.validate(); 
    }

    public AuthenticationDialog(String message,ServerInfo info) 
    {
        // super(frame);
    	 UIPlatform.getPlatform().getWindowRegistry().register(this);
         init(message,info); 
    }
    
    /**
    private void waitForUserInput()
    {
        try
        {  
            synchronized(waitMutex)
            {
              waitMutex.wait();
            }
        }
        catch (InterruptedException e)
        {
            TermGlobal.errorPrintln(this,"***Error: Exception:"+e); 
            // e.printStackTrace();
        } 
    }*/

    public void waitForUserInput()
    {
       try 
       {
    	   synchronized(this)
    	   {
    		   this.wait(100*1000);
    	   }
       }
       catch (InterruptedException e) 
       {
    	   e.printStackTrace();
       } 
    }

    public ServerInfo getAuthenticationInfo()
    {
        if (this.cancel==true)
            return null; 
        
        return authInfo; 
    }

    public void actionPerformed(ActionEvent e)
    {
        update((Component)e.getSource()); 
    }

    public void focusGained(FocusEvent e)
    {
    }

    public void focusLost(FocusEvent e)
    {
        update(e.getComponent()); 
    }

    private void update(Component comp)
    {
        //UIGlobal.debugPrintf(this,"Update:%s\n",comp); 
        
        if (comp==this.userNameTextField)
        {
            this.authInfo.setUsername(((JTextField)comp).getText()); 
        }
        else if (comp==this.passwordField)
        {
            // auto-except when entering password: 
            this.authInfo.setPassword(Secret.wrap( ((JPasswordField)comp).getPassword()));
            this.cancel=false;  
            Exit(); 
        }
        else if (comp==this.passphraseField)
        {
            this.authInfo.setPassphrase(Secret.wrap( ((JPasswordField)comp).getPassword())); 
        }
        else if (comp==this.OKbutton)
        {
            this.cancel=false; 
            Exit(); 
        }
        else if(comp==this.cancelButton)
        {
            authInfo=null;
            this.cancel=true; 
            Exit(); 
        }
    }
 
    private void Exit()
    {
        synchronized(this)
        {
          this.notifyAll();
        }
        
        this.dispose(); 
    }
    
    public void windowOpened(WindowEvent e)
    {
    }

    public void windowClosing(WindowEvent e)
    {
        this.authInfo=null;
        this.cancel=true; 
        Exit(); 
    }

    public void windowClosed(WindowEvent e)
    {
    }

    public void windowIconified(WindowEvent e)
    {
    }

    public void windowDeiconified(WindowEvent e)
    {
    }

    public void windowActivated(WindowEvent e)
    {
    }

    public void windowDeactivated(WindowEvent e)
    {
    }
    
    // ========================================================================
    // ========================================================================
    
	public static ServerInfo askAuthentication(String message,ServerInfo info)
    {
       /* if (frame==null) 
            frame = new JFrame();
        */ 
	    AuthenticationDialog dialog=new AuthenticationDialog(message,info); 
	    
        GuiSettings.setToOptimalWindowSize(dialog);
        
        dialog.setModal(true);
        
        if (VletConfig.isApplet()==false)
        	dialog.setAlwaysOnTop(true);

        // if modal is enabled, this method blocks until window is closed !
        dialog.setVisible(true);         
 
        // not modal: non blocking
        if (dialog.isModal()==false)   
            dialog.waitForUserInput(); 
        
        info=dialog.getAuthenticationInfo();
        dialog.dispose();
        
        return info; 
    }
	
}
