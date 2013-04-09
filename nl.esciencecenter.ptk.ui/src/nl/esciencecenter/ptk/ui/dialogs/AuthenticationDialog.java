/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.ui.dialogs;


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

import nl.esciencecenter.ptk.Global;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.ui.UIPlatform;

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
    private boolean cancel=false;
	private String message;
    private String passphrase;
    private Object username;
    private String password;  
    
	/**
	* Auto-generated main method to display this JDialog
	*/
	private void initGUI() 
    {
		try
        {
			BorderLayout thisLayout = new BorderLayout();
			this.getContentPane().setLayout(thisLayout);
            {
                AuthenticationPanel = new JPanel();
                this.getContentPane().add(
                    AuthenticationPanel,
                    BorderLayout.CENTER);
                FormLayout AuthenticationPanelLayout = new FormLayout(
                    "10px, right:p, 5px, p:grow, 10px",
                    "10px, fill:p, 10px, max(p;5px), max(p;5px), max(p;5px)");
                AuthenticationPanel.setLayout(AuthenticationPanelLayout);
                AuthenticationPanel.setPreferredSize(new java.awt.Dimension(392, 102));
                AuthenticationPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));

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
                    //messageTextField.setBackground(UIGlobal.getGuiSettings().textfield_non_editable_background_color);
                }
            }
            {
                buttonsPanel = new JPanel();
                this.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
                buttonsPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
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
            pack();
		}
		catch (Exception e)
		{
			handle("InitGUi Exception",e); 
		}			
		// auto-resize: 
		//this.validate(); 
        //this.setSize(this.getPreferredSize()); 
	}

    
    private void handle(String string, Exception e)
    {
    }

    private void init(String message,VRI vri)
    {
        String user=null;
        
        this.message=message; 
//        
//        if (info==null)
//            authInfo=new ServerInfo(VRSContext.getDefault(),new VRL("file","localhost","/dummypath"));
//        else
//            authInfo=info; 
//        
//        user=authInfo.getUsername();
//        
        user=vri.getUserinfo(); 
        
        if (user==null) 
        {
            user=Global.getGlobalUserName(); 
            //authInfo.setUsername(user);
        }
        
        initGUI();
        
        this.userNameTextField.setText(user);
        this.messageTextField.setText(message); 
     
        this.validate(); 
    }

    public AuthenticationDialog(String message,VRI vri) 
    {
        // super(frame);
        //UIPlatform.getPlatform().getWindowRegistry().register(this);
         init(message,vri); 
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

//    public ServerInfo getAuthenticationInfo()
//    {
//        if (this.cancel==true)
//            return null; 
//        
//        return authInfo; 
//    }

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
        debugPrintf("Update:%s\n",comp); 
        
        if (comp==this.userNameTextField)
        {
            setUsername(((JTextField)comp).getText()); 
        }
        else if (comp==this.passwordField)
        {
            // auto-except when entering password: 
            setPassword(((JTextField)comp).getText());
            this.cancel=false;  
            Exit(); 
        }
        else if (comp==this.passphraseField)
        {
            setPassphrase(((JTextField)comp).getText()); 
        }
        else if (comp==this.OKbutton)
        {
            this.cancel=false; 
            Exit(); 
        }
        else if(comp==this.cancelButton)
        {
            this.username=null;
            this.password=null;
            this.cancel=true; 
            Exit(); 
        }
    }
 
    private void setPassphrase(String text)
    {
        this.passphrase=text; 
    }


    private void debugPrintf(String string, Component comp)
    {
        
    }


    private void setPassword(String text)
    {
        this.password=text; 
    }

    private void setUsername(String text)
    {
        this.username=text;
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
        this.username=null;
        this.password=null;
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
    
//	public static ServerInfo askAuthentication(String message,ServerInfo info)
//    {
//       /* if (frame==null) 
//            frame = new JFrame();
//        */ 
//	    AuthenticationDialog dialog=new AuthenticationDialog(message,info); 
//	    
//        GuiSettings.setToOptimalWindowSize(dialog);
//        
//        dialog.setModal(true);
//        
//        if (GlobalConfig.isApplet()==false)
//        	dialog.setAlwaysOnTop(true);
//
//        // if modal is enabled, this method blocks until window is closed !
//        dialog.setVisible(true);         
// 
//        // not modal: non blocking
//        if (dialog.isModal()==false)   
//            dialog.waitForUserInput(); 
//        
//        info=dialog.getAuthenticationInfo();
//        dialog.dispose();
//        
//        return info; 
//    }
	
	public static String askAuthentication(String message,VRI vri)
    {
       /* if (frame==null) 
            frame = new JFrame();
        */ 
        AuthenticationDialog dialog=new AuthenticationDialog(message,vri); 
        
        // GuiSettings.setToOptimalWindowSize(dialog);
        
        dialog.setModal(true);
        
        if (UIPlatform.getPlatform().getAppletMode()==false)
            dialog.setAlwaysOnTop(true);

        // if modal is enabled, this method blocks until window is closed !
        dialog.setVisible(true);         
 
        // not modal: non blocking
        if (dialog.isModal()==false)   
            dialog.waitForUserInput(); 
        
        dialog.dispose();
        
        return dialog.password;
    }
	
}
