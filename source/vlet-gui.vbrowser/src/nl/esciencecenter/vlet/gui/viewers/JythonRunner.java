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

package nl.esciencecenter.vlet.gui.viewers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.border.BevelBorder;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.gui.GuiSettings;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.gui.UILogger;
import nl.esciencecenter.vlet.gui.dialog.ExceptionForm;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.ptk.ui.fonts.FontInfo;
import nl.esciencecenter.ptk.ui.fonts.FontToolBar;
import nl.esciencecenter.ptk.ui.fonts.FontToolbarListener;

/**
 *Jython Runner To Be.  
 * Not finished. 
 */
public class JythonRunner extends ViewerPlugin implements ActionListener,
     FontToolbarListener
{
	private static final long serialVersionUID = -5089094250601371894L;

	public static final String jythonSettingsFile = "jythonrunner.props";

	/** The mimetypes i can view */
	private static String mimeTypes[] =
	{ 
		"text/x-jython",
		"text/x-python",
		"text/plain", 
	};

	JTextArea textArea = null;
	VNode vnode = null;
	private boolean muststop = false;

	// =======================================================================

	// =======================================================================

	boolean editable=false; 

	// GUI Components
	// --- 

	private JToolBar toolbar;
	private JButton refreshButton;
	private JScrollPane textScrollPane;
	private FontToolBar fontToolbar;
	private JPanel toolPanel;
	private JToolBar optionsToolbar;
	private JButton saveConfigButton;
	private JToggleButton enableEditButton;
	private JButton saveButton;
	private JButton runButton;
	private JMenuBar menuBar;
	private JMenuItem refreshMenuItem;
	private JMenuItem saveMenuItem;
	private JMenuItem runMenuItem;
	private JPanel topPanel;

	private JCheckBoxMenuItem editMenuItem;
	private JMenuItem saveConfigMenuItem;

	private JythonExecutor jythonExecutor;

	public void initGui()
	{
		{
			this.setLayout(new BorderLayout());
			this.setPreferredSize(new Dimension(800,600)); 
		}
		{
			topPanel=new JPanel(); 
			this.add(topPanel,BorderLayout.NORTH); 
			topPanel.setLayout(new BorderLayout()); 
			topPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
			
			//      Menu
			{
				menuBar=new JMenuBar();

				topPanel.add(menuBar,BorderLayout.NORTH);

				{
					JMenu menu;
					JMenuItem dumm;
					menuBar.add(menu=new JMenu("Jython"));
					{
						dumm=new JMenuItem("Resource");
						menu.add(dumm);
						dumm.setEnabled(false);
					}
					menu.add(new JSeparator());
					{
						runMenuItem=new JMenuItem("Run");
						menu.add(runMenuItem);
						runMenuItem.addActionListener(this);
						//runMenuItem.setEnabled(false); 
					}
					menu.add(new JSeparator());					

					{
						editMenuItem=new javax.swing.JCheckBoxMenuItem("Edit");
						menu.add(editMenuItem);
						editMenuItem.addActionListener(this); 
						editMenuItem.setSelected(false); 
					}
					{
						refreshMenuItem=new JMenuItem("Reload");
						menu.add(refreshMenuItem);
						refreshMenuItem.addActionListener(this);
					}
					{
						saveMenuItem=new JMenuItem("Save");
						menu.add(saveMenuItem);
						saveMenuItem.addActionListener(this);
						saveMenuItem.setEnabled(false); 
					}
				}
				{
					JMenu menu;
					//JMenuItem dumm;
					menuBar.add(menu=new JMenu("Settings"));
					menu.add(new JSeparator()); 
					{
						saveConfigMenuItem=new JMenuItem("Save Settings");
						menu.add(saveConfigMenuItem);
						saveConfigMenuItem.addActionListener(this); 
						saveConfigMenuItem.setSelected(false); 
					}
				}
			}
			// Toolbars 
			{
				toolPanel = new JPanel();
				topPanel.add(toolPanel,BorderLayout.CENTER);
				toolPanel.setLayout(new FlowLayout());
				//            // Font Toolbar 
				{
					this.fontToolbar = new FontToolBar(this);
					toolPanel.add(fontToolbar);
				}
				{
					toolbar = new JToolBar();
					toolPanel.add(toolbar);
					{
						refreshButton = new JButton();
						toolbar.add(refreshButton);
						refreshButton.setIcon(UIGlobal.getIconOrBroken("menu/refresh.gif"));

						refreshButton.setActionCommand("refresh");
						refreshButton.addActionListener(this);
						refreshButton.setToolTipText("Reload text");
					}

				}
				{
					optionsToolbar = new JToolBar();
					toolPanel.add(optionsToolbar);
					{
						saveConfigButton = new JButton();
						optionsToolbar.add(saveConfigButton);
						saveConfigButton.setIcon(UIGlobal.getIconOrBroken("menu/saveconfig.png"));
						saveConfigButton.setActionCommand("saveConfig");
						saveConfigButton.addActionListener(this);
						saveConfigButton.setToolTipText("Save setings");
					} 
					{
						enableEditButton = new JToggleButton();
						optionsToolbar.add(enableEditButton);
						enableEditButton.setIcon(UIGlobal.getIconOrBroken("menu/enableedit.png"));

						enableEditButton.setActionCommand("enableEdit");
						enableEditButton.addActionListener(this);
						enableEditButton.setToolTipText("Edit this text");
						enableEditButton.setEnabled(true); 
					}
					{
						saveButton = new JButton();
						optionsToolbar.add(saveButton);
						saveButton.setIcon(UIGlobal.getIconOrBroken("menu/save.png"));

						saveButton.setActionCommand("save");
						saveButton.addActionListener(this);
						saveButton.setToolTipText("Save text");
						saveButton.setEnabled(false); 
					}
					{
						runButton = new JButton();
						optionsToolbar.add(runButton);
						//saveButton.setIcon(UIGlobal.getResourceLoader().getIconOrDefault("menu/run-jython.png"));
						runButton.setText("Run");
						runButton.setActionCommand("execute");
						runButton.addActionListener(this);
						runButton.setToolTipText("Run Jython");
						
						// runButton.setEnabled(true); 
					}
				}
			}
		}

		{
			textScrollPane = new JScrollPane();
			add(textScrollPane, BorderLayout.CENTER);

			{
				textArea = new JTextArea();
				// java 1.5 anti aliasing:

				/*textArea.putClientProperty(
                 com.sun.java.swing.SwingUtilities2.AA_TEXT_PROPERTY_KEY,
                 this.fontToolbar.getAntiAliasing());*/

				textArea.setText("Starting default text viewer:...");
				textArea.setSize(800, 600);
				textArea.setEditable(this.editable);
				textScrollPane.setViewportView(textArea);
			}
		}
		// update with stored settings: 
		{
			loadSettings();
			FontInfo info = getFontInfo();
			info.updateComponentFont(this.textArea); 
		}
	}

	private FontInfo getFontInfo()
	{
		return fontToolbar.getFontInfo();
	}

	@Override
	public boolean haveOwnScrollPane()
	{
		return true;
	}

	/**
	 * @param txt
	 */
	public void setText(String txt)
	{
		textArea.setText(txt);
		// this.notifySizeChange(textArea.getPreferredSize());
	}

	public void updateFont(Font font, Map<?,?> renderingHints)
	{
		GuiSettings.updateRenderingHints(textArea,renderingHints); 
		textArea.setFont(font);
		textArea.repaint();

		// this.notifySizeChange(textArea.getPreferredSize());
	}

	/**
	 * @param location
	 * @throws VrsException 
	 */
	public void updateLocation(VRL location) throws VrsException
	{
		if (location==null)
			return; 
		
		// reset stop flag: 
		this.muststop=false; 
		
		//System.err.println("UpdateLoc:"+location);
		setVRL(location); 

		setViewerTitle("Viewing:"+getVRL().getBasename());

		setBusy(true);

		try
		{
			vnode = getVNode(location);
			String txt = "";
			
			if (this.muststop == true)
				return; // receive stop signal BEFORE getContents...

			txt=UIGlobal.getResourceLoader().getText(vnode.getVRL()); 

			if (this.muststop == true)
				return; // receive stop signal AFTER getContents...

			setText(txt);
		}
		catch (Exception e)
		{
		    throw new VrsException(e);
		}
		finally
		{
			setBusy(false);
		}
		
		requestFocusOnText(); 
	}

	private void requestFocusOnText()
	{
		this.textArea.requestFocusInWindow(); 
	}

	@Override
	public String[] getMimeTypes()
	{
		return mimeTypes;
	}

	@Override
	public void startViewer(VRL loc) throws VrsException
	{
		updateLocation(loc);
	}

	@Override
	public void stopViewer()
	{
		this.muststop = true;
	}

	@Override
	public void disposeViewer()
	{
		this.textArea = null;
	}

	@Override
    public void initViewer()
	{
		initGui();
	}	

	@Override
	public String getName()
	{
		return "JythonRunner";
	}

	public void actionPerformed(ActionEvent e)
	{
		Object source = e.getSource();

		// FontToolbar events are handled by that component: 
		if ((source == this.refreshButton) || (source==this.refreshMenuItem))
		{
			try	

			{
				updateLocation(getVRL());
			}
			catch (VrsException ex)
			{
				handle(ex);
			}
		}
		else if ((source == this.saveConfigButton) || (source==this.saveConfigMenuItem))
		{
			saveSettings();
		}
		else if (source == this.enableEditButton)
		{
			enableEdit(this.editable==false); // toggle 
			this.requestFocusOnText();
		}
		else if (source==this.editMenuItem)
		{
			enableEdit(editMenuItem.isSelected()); 
		}
		else if ((source == this.saveButton) || (source==this.saveMenuItem))
		{
			try
			{
				save();
			}
			catch (VrsException ex)

			{
				this.handle(ex); 
			} 
		}
		else if ((source == this.runButton) || (source==this.runMenuItem))
		{
			try
			{
				runJython();
			}
			catch (Exception ex)
			{
				this.handle(ex); 
			} 
		}
	}

	private void runJython()
	{
		if (this.jythonExecutor==null)
		{
			this.jythonExecutor=new JythonExecutor(UIGlobal.getVRSContext());
		}
		
		this.jythonExecutor.execute(this.getVRL()); 
	}

	private void save() throws VrsException
	{
		// saveContents(this.textArea.getText());
	}

	/** Enable/Disable edit  */ 
	private void enableEdit(boolean val)
	{
		if (val==true)
		{
			this.editable=true; 
			this.textArea.setEditable(true); 
			this.saveButton.setEnabled(true); 
			this.enableEditButton.setSelected(true); 
			this.setViewerTitle("Editing:"+getVRL().getBasename());
			this.editMenuItem.setSelected(true); 
			this.saveMenuItem.setEnabled(true); 
		}
		else
		{
			this.editable=false; 
			this.textArea.setEditable(false); 
			this.saveButton.setEnabled(false);
			this.enableEditButton.setSelected(false); 
			this.setViewerTitle("Viewing:"+getVRL().getBasename());
			this.editMenuItem.setSelected(false); 
			this.saveMenuItem.setEnabled(false); 
		}

		//this.enableEditButton.setSelected(true); 
	}

	private void saveSettings()
	{
		Properties props = this.fontToolbar.getFontInfo().getFontProperties();

		VRL loc = getConfigFile();

		try
		{
			saveProperties(loc, props);
		}
		catch (Exception e)
		{
			handle(e);
		}
	}

	private VRL getConfigFile()
	{
		return VletConfig.getUserConfigDir().appendPath(
				"textviewer.props");
	}

	private void loadSettings()
	{
		VRL loc = getConfigFile();

		try
		{
			Properties props = UIGlobal.loadProperties(loc);
			//System.err.println("load settings"); 
			FontInfo info = new FontInfo(props);

			this.fontToolbar.setFontInfo(info);
			//System.err.println(" after load settings");
		}
		catch (Exception e)
		{
			UILogger.logException(this,ClassLogger.ERROR,e,"Exception when loading settings from:%s\n",loc);
			//handle(e); 
		}
	}

	protected void handle(VrsException ex)
	{
		ExceptionForm.show(ex);
	}

	// =======================================================================
	// Main 
	// =======================================================================

	/**
	 * @param args
	 */
	public static void main(String args[])
	{
		try
		{
			VRL file=new VRL(VletConfig.getInstallBaseDir().appendPath("py/examples/vfsclient.py"));
			viewStandAlone(file);
		}
		catch (Exception e)
		{
			System.out.println("***Error: Exception:" + e);
			e.printStackTrace();
		}
	}

	public static void viewStandAlone(VRL loc)
	{
		JythonRunner tv = new JythonRunner();
	
		try
		{
			tv.startAsStandAloneApplication(loc); 
		}
		catch (VrsException e)
		{
			System.out.println("***Error: Exception:" + e);
			e.printStackTrace();
		}
	}
	/**
	 * This method should return an instance of this class which does 
	 * NOT initialize it's GUI elements. This method is ONLY required by
	 * Jigloo if the superclass of this class is abstract or non-public. It 
	 * is not needed in any other situation.
	 */
	public static Object getGUIBuilderInstance() {
		return new JythonRunner(Boolean.FALSE);
	}

	/**
	 * Code added by Jigloo:
	 * This constructor is used by the getGUIBuilderInstance method to
	 * provide an instance of this class which has not had it's GUI elements
	 * initialized (ie, initGUI is not called in this constructor).
	 */
	public JythonRunner(Boolean initGUI) 
	{
		super();
		if (initGUI) 
			this.initViewer(); 
	}

	public JythonRunner()
	{
		; // initialization is done in initViewer() ! 
	}

}
