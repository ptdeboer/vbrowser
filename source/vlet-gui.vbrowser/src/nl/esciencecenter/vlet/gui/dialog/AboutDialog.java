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
import java.awt.FlowLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.net.URL;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;

import nl.esciencecenter.ptk.data.BooleanHolder;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.gui.HyperLinkListener;
import nl.esciencecenter.vlet.gui.Messages;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.gui.UIPlatform;
import nl.esciencecenter.vlet.gui.vbrowser.BrowserController;
import nl.esciencecenter.vlet.gui.vhtml.VHTMLEditorPane;
import nl.esciencecenter.vlet.gui.vhtml.VHyperListener;


/**
 * Simple 'About' dialog. 
 */
public class AboutDialog extends javax.swing.JDialog implements ActionListener, WindowListener
{
    private static final long serialVersionUID = -4769345816214794352L;

    private JPanel buttonPanel;
    
    private VHTMLEditorPane  aboutHTMLPane;
    private JPanel mainPanel;
    private JLabel topLabel;
    private JPanel topPanel;
    private JLabel vleImageLabel;
    private JLabel bottomLabel;
    private JLabel rightLabel;
    private JLabel leftLabel;
    
    private JButton okButton;
    
    private HyperLinkListener browserController;
    
    private JScrollPane scrollPane;
    
    /**
     * Auto-generated main method to display this JDialog
     */
    public static void main(String[] args)
    {
        AboutDialog.showMe(null); 
        
        //AboutDialog inst = new AboutDialog();
        //inst.setVisible(true);
    }
    
    public AboutDialog(BrowserController bc)
    {
        super((bc!=null?BrowserController.getMasterFrame():null));
        UIPlatform.getPlatform().getWindowRegistry().register(this);
        this.browserController=bc;
        init();
    }
    
    public AboutDialog()
    {
        UIPlatform.getPlatform().getWindowRegistry().register(this);
        init(); 
    }
    
    private void init()
    {
        initGUI();
        this.addWindowListener(this); 
    }
    
    private void initGUI()
    {
        
        try
        {
            BorderLayout thisLayout = new BorderLayout();
            
            this.getContentPane().setLayout(thisLayout);
            this.setResizable(true);
            {
                mainPanel = new JPanel();
                this.getContentPane().add(mainPanel, BorderLayout.CENTER);
                BorderLayout mainPanelLayout = new BorderLayout();
                mainPanel.setLayout(mainPanelLayout);
                mainPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
                //mainPanel.setBounds(10, 12, 354, 315);
                //mainPanel.setPreferredSize(new java.awt.Dimension(368, 302));
                {
                    scrollPane=new JScrollPane(); 
                    mainPanel.add(scrollPane, BorderLayout.CENTER);
                    
                    
                    {
                        aboutHTMLPane = new VHTMLEditorPane();
                        scrollPane.setViewportView(aboutHTMLPane); 
                        aboutHTMLPane.setEditable(false);
                        // null viewer. 
                        aboutHTMLPane.addHyperlinkListener(new VHyperListener(
                                null,
                                browserController));
                        
                        aboutHTMLPane.setContentType("text/html"); // HTML text s
                        aboutHTMLPane.setText(Messages.aboutText);
                        aboutHTMLPane.setPreferredSize(new java.awt.Dimension(413, 201));
                        aboutHTMLPane.setEditable(false);
                        aboutHTMLPane.setAutoscrolls(false);
                        
                        aboutHTMLPane.setBorder(BorderFactory
                                .createEtchedBorder(BevelBorder.RAISED));
                        //aboutHTMLPane.setBounds(1, 139, 350, 103);
                    }
                }
                {
                    topPanel = new JPanel();
                    mainPanel.add(topPanel, BorderLayout.NORTH);
                    topPanel.setBackground(new java.awt.Color(255,255,255));
                    topPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    {
                        vleImageLabel = new JLabel();
                        topPanel.add(vleImageLabel);
                        vleImageLabel.setIcon(loadIcon("images/nlesc_logo_medium.png"));
                    }
                }
                {
                    buttonPanel = new JPanel();
                    FlowLayout buttonPanelLayout = new FlowLayout();
                    mainPanel.add(buttonPanel, BorderLayout.SOUTH);
                    buttonPanel.setLayout(buttonPanelLayout);
                    buttonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
                    {
                        okButton = new JButton();
                        buttonPanel.add(okButton);
                        okButton.setText("OK");
                        okButton.addActionListener(this);
                    }
                }
                
            }
            {
                leftLabel = new JLabel();
                this.getContentPane().add(leftLabel, BorderLayout.WEST);
                leftLabel.setPreferredSize(new java.awt.Dimension(10, 405));
            }
            {
                rightLabel = new JLabel();
                this.getContentPane().add(rightLabel, BorderLayout.EAST);
                rightLabel.setPreferredSize(new java.awt.Dimension(10, 405));
            }
            {
                topLabel = new JLabel();
                this.getContentPane().add(topLabel, BorderLayout.NORTH);
                topLabel.setPreferredSize(new java.awt.Dimension(375, 10));
            }
            {
                bottomLabel = new JLabel();
                this.getContentPane().add(bottomLabel, BorderLayout.SOUTH);
                bottomLabel.setPreferredSize(new java.awt.Dimension(375, 10));
            }
            this.setSize(550,550);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    private Icon loadIcon(String urlStr)
    {
        URL url = getClass().getClassLoader().getResource(urlStr); 
        return new ImageIcon(url);
        
    }

    public void actionPerformed(ActionEvent e)
    {
        Exit();  
    }
    
    private void Exit()
    {
        this.dispose();
    }
    
    public void windowOpened(WindowEvent e)
    {
    }
    
    public void windowClosing(WindowEvent e)
    {
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
    
    public static void showMe(final BrowserController bc) 
    {
        if (UIGlobal.isGuiThread()==false)
        {
            Runnable run=new Runnable()
            {
                public void run()
                {
                    showMe(bc);
                }
            };
            UIGlobal.swingInvokeLater(run);
            return;
        }
        
        AboutDialog dialog=new AboutDialog(bc); 
        dialog.setLocationRelativeTo(null); 
        dialog.setVisible(true);
        dialog.scrollDown(bc); 
        
    }
    // cleanup:see super.dispose
    public void dispose()
    {
        super.dispose(); 
        if (scrollTask!=null) 
            scrollTask.signalTerminate();
    }
    
    ActionTask scrollTask=null;
    
    
    private void scrollDown(BrowserController bc)
    {
        // scroll up:

        
        //this.aboutHTMLPane.scrollToReference("top");
        
        final JViewport vport = this.scrollPane.getViewport();
        final AboutDialog dialog=this; 
        
        //Rectangle rect=new Rectangle(0,-1000); 
        //vport.scrollRectToVisible(rect);
        
        //Rectangle rect=new Rectangle(0,-1000); 
        //vport.scrollRectToVisible(rect);
        // init: scrollup: 
        
        
        scrollTask=new ActionTask(bc,"scrollTask")
        {
            boolean stop=false; 
            
            @Override
            protected void doTask() throws VrsException
            {
                // must use booleanholder 
                final BooleanHolder scrollToTop=new BooleanHolder(true);  
                
                try
                {
                    while(stop==false)
                    {
                        Rectangle rect=vport.getViewRect(); 
                        //rect.y=i;
                        rect.y=1; 
                        
                        //System.out.println("Scrolling to:"+rect.y);
                        final Rectangle frect=rect; 
                        Runnable run = new Runnable()
                        {
                            public void run()
                            {
                                if (scrollToTop.value)
                                {
                                    dialog.aboutHTMLPane.scrollToReference("top");
                                    scrollToTop.value=false; 
                                }
                                vport.scrollRectToVisible(frect);
                            }
                        };
                        SwingUtilities.invokeLater(run); 
                        
                        Thread.sleep(100);
                    }
                    
                }
                catch (InterruptedException e)
                {
                    System.out.println("***Error: Exception:"+e); 
                    e.printStackTrace();
                }
            }
            
            @Override
            public void stopTask()
            {
                stop=true; 
            }
        };
        
        scrollTask.startTask();
    }
    
}
