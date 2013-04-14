/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */ 
// source: 

package nl.esciencecenter.vbrowser.vb2.ui.browser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.border.EtchedBorder;

import nl.esciencecenter.vbrowser.vb2.ui.actionmenu.ActionMethod;
import nl.esciencecenter.vbrowser.vb2.ui.iconspanel.IconsPanel;
import nl.esciencecenter.vbrowser.vb2.ui.model.DataSource;
import nl.esciencecenter.vbrowser.vb2.ui.model.UIViewModel;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNodeDataSource;
import nl.esciencecenter.vbrowser.vb2.ui.resourcetable.ProxyNodeTableDataProducer;
import nl.esciencecenter.vbrowser.vb2.ui.resourcetable.ResourceTable;
import nl.esciencecenter.vbrowser.vb2.ui.resourcetable.ResourceTableModel;
import nl.esciencecenter.vbrowser.vb2.ui.resourcetable.TableDataProducer;
import nl.esciencecenter.vbrowser.vb2.ui.tree.ResourceTree;
import nl.esciencecenter.vbrowser.vb2.ui.widgets.NavigationBar;

/**
 * Master browser frame.  
 * 
 * */ 
public class BrowserFrame extends JFrame 
{
    public static enum BrowserViewMode
    {
        ICONS, ICONLIST, TABLE, CONTENT
    };
    
	private static final long serialVersionUID = 3076698217838089389L;
	
	private BrowserInterface browserController;
	private JPanel uiMainPanel;
	private JSplitPane uiMainSplitPane;
	private JScrollPane uiLeftScrollPane;
	private ResourceTree uiResourceTree;
	private JTabbedPane uiRightTabPane;
	private JPanel uiTopPanel;
    private NavigationBar uiNavigationBar;
	private JTabbedPane uiLeftTabPane;
	private JMenuBar uiMainMenuBar;
	private ActionListener actionListener;

    private JToolBar uiViewBar;

    private JButton uiViewAsIconsBtn;

    private JButton uiViewAsIconListBtn;

    private JButton uiViewAsTableBtn;

    private JPanel uiToolBarPanel;

	public BrowserFrame(BrowserInterface controller,ActionListener actionListener)
	{
		this.browserController=controller;
		this.actionListener=actionListener; 
		initGUI();
	}
	
	public void initGUI()
	{

		{
			this.uiMainPanel=new JPanel(); 
			this.add(uiMainPanel); 
			this.uiMainPanel.setLayout(new BorderLayout()); 
			
			{
				   uiMainMenuBar = createMenuBar(actionListener);
				   setJMenuBar(uiMainMenuBar);
			}
			{
			    // === Top Panel === // 
				this.uiTopPanel=new JPanel(); 
				this.uiMainPanel.add(uiTopPanel,BorderLayout.NORTH); 
				uiTopPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
				uiTopPanel.setLayout(new BorderLayout());  
				
                {
                    // === Nav Bar === //
                    this.uiNavigationBar=new NavigationBar();
                    uiTopPanel.add(uiNavigationBar,BorderLayout.NORTH);
                    this.uiNavigationBar.setEnableNagivationButtons(true); 
                }
                
                {
                    // === Tool Bar Panel === //
                    
                    uiToolBarPanel=new JPanel(); 
                    uiTopPanel.add(uiToolBarPanel,BorderLayout.CENTER);
                    uiToolBarPanel.setLayout(new FlowLayout());
                    
                    {
                        // === View Icons Tool Bar === //

                        this.uiViewBar=new JToolBar();
                        uiToolBarPanel.add(uiViewBar);
                    
                        {
                            uiViewAsIconsBtn = new JButton();
                            uiViewBar.add(uiViewAsIconsBtn);
                            // viewAsIconsBut.setText("IC");
                            uiViewAsIconsBtn.setIcon(loadIcon("menu/viewasicons.png"));
                            uiViewAsIconsBtn.setActionCommand(ActionMethod.VIEW_AS_ICONS.toString());
                            uiViewAsIconsBtn.addActionListener(actionListener);
                           // uiViewAsIconsBtn.setToolTipText(Messages.TT_VIEW_AS_ICONS);
                        }
                        {
                            uiViewAsIconListBtn = new JButton();
                            uiViewBar.add(uiViewAsIconListBtn);
                            // viewAsIconRows.setText("ICR");
                            uiViewAsIconListBtn.setIcon(loadIcon("menu/viewasiconlist.png"));
                            uiViewAsIconListBtn.setActionCommand(ActionMethod.VIEW_AS_ICON_LIST.toString());
                            uiViewAsIconListBtn.addActionListener(actionListener);
                            uiViewAsIconListBtn.setEnabled(true);
                        }
                        {
                            uiViewAsTableBtn= new JButton();
                            uiViewBar.add(uiViewAsTableBtn);
                            // viewAsListBut.setText("AL");
                            uiViewAsTableBtn.setActionCommand(ActionMethod.VIEW_AS_TABLE.toString());
                            uiViewAsTableBtn.addActionListener(actionListener);
    
                            uiViewAsTableBtn.setIcon(loadIcon("menu/viewastablelist.png"));
                            //uiViewAsTableBtn.setEnabled(false); 
                           // uiViewAsTableBtn.setToolTipText(Messages.TT_VIEW_AS_TABLE);
                        }
                    }
                }
				
			}
			
			{
				// === Split Pane === // 
				this.uiMainSplitPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT); 
				this.uiMainSplitPane.setResizeWeight(0.2d);
				
				this.uiMainPanel.add(uiMainSplitPane,BorderLayout.CENTER);
				{
					// LEFT 
					this.uiLeftTabPane=new JTabbedPane(); 
					this.uiLeftScrollPane=new JScrollPane(); 
					this.uiLeftTabPane.add(uiLeftScrollPane); 

					this.uiMainSplitPane.add(this.uiLeftTabPane,JSplitPane.LEFT); 
					
					{
						// no data source during initialization ! 
						this.uiResourceTree=new ResourceTree(this.browserController,null); 
						this.uiLeftScrollPane.setViewportView(this.uiResourceTree); 
						this.uiResourceTree.setFocusable(true); 
					}
				}
				{
					// RIGHT
					this.uiRightTabPane=new JTabbedPane();  
					this.uiMainSplitPane.add(this.uiRightTabPane,JSplitPane.RIGHT); 
					
					// ... iconsPanel 
                    {
                    	IconsPanel iconsPanel = new IconsPanel(this.browserController,null); 
                    	addTab("Icons",iconsPanel); 
                       
                        
                    }
					// default table panel
                    //{
                    // 	ResourceTable tablePanel = new ResourceTable(new ProxyNodeTableModel(null));//new ProxyNodeTableModel(node)); 		
                    // 	addTab("Table",tablePanel); 
                    //}
					
				}
			}
		}
		
		// default sizes: 
		this.setSize(800,600); 
	}

	protected TabContentPanel addTab(String name, JComponent comp)
    {
    	 TabContentPanel tabPanel = TabContentPanel.createTab(name,comp,this.actionListener); 
         uiRightTabPane.add(tabPanel);
         return tabPanel;
	}


	protected IconsPanel getIconsPanel()
	{
		return getIconsPanel(true); 
	}
	
	protected IconsPanel getIconsPanel(boolean autoCreate)
	{
		TabContentPanel tab = this.getCurrentTab(); 
		if (tab==null)
		{
			if (autoCreate==false)
				return null; 
			
			tab=this.addTab("Icons",null); 
		}
		
		JComponent comp = tab.getContent(); 
		if (comp instanceof IconsPanel)
			return (IconsPanel)comp;  
		
		if (autoCreate==false)
			return null; 
		
		ProxyNode node=this.getViewedProxyNode(); 
		
		IconsPanel pnl = new IconsPanel(this.browserController,null); 
		pnl.setDataSource(node,true); 
		
		tab.setContent(pnl); 
		return pnl; 	
	}
	
	protected TabContentPanel createIconsPanelTab(ProxyNode node) 
	{
		TabContentPanel tab=this.addTab("Icons",null);
		IconsPanel pnl = new IconsPanel(this.browserController,null); 
		pnl.setDataSource(node,true); 
		tab.setContent(pnl); 
		return tab; 
	}
	
	public ResourceTable getTablePanel() 
	{
		return getTablePanel(false); 
	}

	protected ResourceTable getTablePanel(boolean autoCreate)
	{
		TabContentPanel tab = this.getCurrentTab(); 
		if (tab==null)
		{
			if (autoCreate==false)
				return null; 
			
			tab=this.addTab("Table",null); 
		}
		
		JComponent comp = tab.getContent(); 
		if (comp instanceof ResourceTable)
			return (ResourceTable)comp;  
		
		if (autoCreate==false)
			return null; 
		
		// Clone ! 
		ProxyNode node=this.getViewedProxyNode(); 
		
		ResourceTable tbl = new ResourceTable(this.browserController,new ResourceTableModel());
		tbl.setDataSource(node,true);  		
		tab.setContent(tbl);  
		return tbl; 
	}
	
	protected JComponent getFirstTab(Class<? extends JComponent> clazz) 
	{
		for (int i=0;i<this.uiRightTabPane.getComponentCount();i++)
		{
			TabContentPanel tab = this.getTab(i); 
			if (tab.contains(clazz))
					return tab.getContent(); 
		}
		return null; 
	}

		
	private JMenuBar createMenuBar(ActionListener actionListener) 
	{
		JMenuBar menu = new JMenuBar(); 
	
		 {
		        // Location
	            JMenu mainMenu = new JMenu();
	            menu.add(mainMenu);
	            mainMenu.setText("Location");
	            mainMenu.setMnemonic(KeyEvent.VK_L);
	            {
	                JMenuItem viewNewWindowMenuItem = new JMenuItem();
	                mainMenu.add(viewNewWindowMenuItem);
	                viewNewWindowMenuItem.setText("New Window");
	                viewNewWindowMenuItem.setMnemonic(KeyEvent.VK_W);
	                viewNewWindowMenuItem.addActionListener(actionListener);
	                viewNewWindowMenuItem.setActionCommand(ActionMethod.CREATE_NEW_WINDOW.toString());
	            }
	            {
	                JMenuItem openMenuItem = new JMenuItem();
	                mainMenu.add(openMenuItem);
	                openMenuItem.setText("Open Location");
	                openMenuItem.setMnemonic(KeyEvent.VK_O);
	                openMenuItem.addActionListener(actionListener);
	                openMenuItem.setActionCommand(ActionMethod.OPEN_LOCATION.toString());
	            }
	            {
	                JMenuItem openInWinMenuItem = new JMenuItem();
	                mainMenu.add(openInWinMenuItem);
	                openInWinMenuItem.setText("Open in new Window");
	                openInWinMenuItem.setMnemonic(KeyEvent.VK_N);
	                openInWinMenuItem.addActionListener(actionListener);
	                openInWinMenuItem.setActionCommand(ActionMethod.OPEN_IN_NEW_WINDOW.toString());
	            }
	            {
	                JMenuItem openInWinMenuItem = new JMenuItem();
	                mainMenu.add(openInWinMenuItem);
	                openInWinMenuItem.setText("Open in new Tab");
	                openInWinMenuItem.setMnemonic(KeyEvent.VK_T);
	                openInWinMenuItem.addActionListener(actionListener);
	                openInWinMenuItem.setActionCommand(ActionMethod.NEW_TAB.toString());
	            }
	            JSeparator jSeparator = new JSeparator();
	            mainMenu.add(jSeparator);
		 }
		 // "View" Menu
		 {
             JMenu viewMenu = new JMenu();
             menu.add(viewMenu);
             viewMenu.setText("View");
             viewMenu.setMnemonic(KeyEvent.VK_V);
             {
                 JMenuItem viewMI = new JMenuItem();
                 viewMenu.add(viewMI);
                 viewMI.setText("View");
                 //viewMI.setMnemonic(KeyEvent.VK_W);
                 viewMI.addActionListener(actionListener);
                 //viewNewWindowMenuItem.setActionCommand(ActionMethod.CREATE_NEW_WINDOW.toString());
             }
		 }
		 
		return menu; 
	}

	public ResourceTree getResourceTree()
	{	
		return this.uiResourceTree; 
	}
	
	public JTabbedPane getTabbedPane()
	{
		return this.uiRightTabPane; 
	}

    public void setNavigationBarListener(ActionListener handler)
    {
        this.uiNavigationBar.addTextFieldListener(handler); 
        this.uiNavigationBar.addNavigationButtonsListener(handler); 
        
    }

	public ViewNode getCurrentTabViewedNode() 
	{
		TabContentPanel tab = this.getCurrentTab();
		if(tab!=null)
			return tab.getViewNode(); 
		// EMPTY TABS!
		//this.uiResourceTree.getSel
		return null; 
	}

	public TabContentPanel getCurrentTab() 
	{
		Component tab = this.uiRightTabPane.getSelectedComponent(); 
		if (tab instanceof TabContentPanel)
		{
			return ((TabContentPanel)tab); 
		}
		return null;
	}


    public void closeTab(TabContentPanel tab)
    {
        this.uiRightTabPane.removeTabAt(uiRightTabPane.indexOfComponent(tab));  
    }
    
	public TabContentPanel getTab(int index) 
	{
		Component tab = this.uiRightTabPane.getComponent(index); 
		if (tab instanceof TabContentPanel)
		{
			return ((TabContentPanel)tab); 
		}
		return null;
	}

	
	public NavigationBar getNavigationBar() 
	{
		return this.uiNavigationBar; 
	}
	
	private Icon loadIcon(String urlstr)
	{
	    return new ImageIcon(getClass().getClassLoader().getResource(urlstr)); 
	}
	
	public void setViewMode(BrowserViewMode mode)
	{
		
		switch(mode)
	    {
	        case ICONS:
	        	this.getIconsPanel(true).updateUIModel(UIViewModel.createIconsModel()); 
	            break; 
	        case ICONLIST:
	        	this.getIconsPanel(true).updateUIModel(UIViewModel.createIconsListModel()); 
	            break; 
	        case TABLE:
	        	this.getTablePanel(true);
	    }
	}

	protected ProxyNode getViewedProxyNode()
    {
    	TabContentPanel tab = getCurrentTab(); 
    	
    	if (tab==null)
    		return null;
    	
    	JComponent comp = tab.getContent(); 
        
    	// Todo: Combine DataSource/DataProducer interfaces 
    	
    	if (comp instanceof IconsPanel)
    	{
    	   	DataSource dataSource= ((IconsPanel)comp).getDataSource(); 
    	   	if (dataSource instanceof ProxyNodeDataSource)
    	   	{
    	   		return ((ProxyNodeDataSource)dataSource).getRootNode(); 
    	   	}
    	}
    	
    	if (comp instanceof ResourceTable)
    	{
    		TableDataProducer dataProducer=((ResourceTable)comp).getDataProducer();  
    		if (dataProducer instanceof ProxyNodeTableDataProducer)
    		{
    			return ((ProxyNodeTableDataProducer)dataProducer).getRootProxyNode(); 
    		}
     	}
    	
    	return null; 
    }

	public void setTabTitle(TabContentPanel tab, String name) 
	{
		int index=this.uiRightTabPane.indexOfComponent(tab); 
		
		if (index<0)
			return;  
		
		this.uiRightTabPane.setTitleAt(index,name);
		tab.setName(name); 
		
	}

}
