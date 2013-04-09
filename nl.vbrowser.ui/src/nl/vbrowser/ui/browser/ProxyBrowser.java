package nl.vbrowser.ui.browser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import nl.nlesc.ptk.net.VRI;
import nl.nlesc.ptk.util.logging.ClassLogger;
import nl.vbrowser.ui.actionmenu.Action;
import nl.vbrowser.ui.actionmenu.ActionMenu;
import nl.vbrowser.ui.actionmenu.ActionMenuListener;
import nl.vbrowser.ui.actionmenu.ActionMethod;
import nl.vbrowser.ui.browser.BrowserFrame.BrowserViewMode;
import nl.vbrowser.ui.data.History;
import nl.vbrowser.ui.dialogs.ExceptionDialog;
import nl.vbrowser.ui.iconspanel.IconsPanel;
import nl.vbrowser.ui.model.ViewNode;
import nl.vbrowser.ui.model.ViewNodeContainer;
import nl.vbrowser.ui.proxy.ProxyFactory;
import nl.vbrowser.ui.proxy.ProxyNode;
import nl.vbrowser.ui.proxy.ProxyNodeDataSource;
import nl.vbrowser.ui.proxy.ProxyNodeEvent;
import nl.vbrowser.ui.proxy.ProxyNodeEventNotifier;
import nl.vbrowser.ui.resourcetable.ResourceTable;
import nl.vbrowser.ui.widgets.NavigationBar;
import nl.vbrowser.ui.widgets.NavigationBar.NavigationAction;

/**
 * Proxy Resource Browser.
 * 
 */
public class ProxyBrowser implements BrowserInterface, ActionMenuListener
{
    private static ClassLogger logger;

    {
        logger = ClassLogger.getLogger(ProxyBrowser.class);
        logger.setLevelToDebug();
    }

    // ========================================================================
    // Inner Classes
    // ========================================================================

    public class NavBarHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            ProxyBrowser.this.handleNavBarEvent(e);
        }
    }

    public class ActionHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent e)
        {
            ProxyBrowser.this.handleMenuEvent(e);
        }
    }
    
    public class BrowserFrameListener implements WindowListener
    {
        @Override
        public void windowOpened(WindowEvent e)
        {
        }

        @Override
        public void windowClosing(WindowEvent e)
        {
            ProxyBrowser.this.browserFrame.dispose(); 
        }

        @Override
        public void windowClosed(WindowEvent e)
        {
        }

        @Override
        public void windowIconified(WindowEvent e)
        {
        }

        @Override
        public void windowDeiconified(WindowEvent e)
        {
        }

        @Override
        public void windowActivated(WindowEvent e)
        {
        }

        @Override
        public void windowDeactivated(WindowEvent e)
        {
        }
        
    }

    // ========================================================================
    // Instance
    // ========================================================================

    private BrowserPlatform platform;

    private BrowserFrame browserFrame;

    private ProxyNode rootNode;

    private ProxyBrowserTaskWatcher taskWatcher = null;

    private History<VRI> history=new History<VRI>();

    private ProxyActionHandler proxyActionHandler=null;
    
    public ProxyBrowser(BrowserPlatform platform, boolean show)
    {
        init(platform,show);
    }
    

    private void init(BrowserPlatform platform, boolean show)
    {
        this.platform = platform;
        this.browserFrame = new BrowserFrame(this, new ActionHandler());
        this.taskWatcher = new ProxyBrowserTaskWatcher(this);
        this.browserFrame.addWindowListener(new BrowserFrameListener()); 
        browserFrame.setNavigationBarListener(new NavBarHandler());
        browserFrame.setVisible(show);
        this.proxyActionHandler=new ProxyActionHandler(this); 
    }

    @Override
    public BrowserPlatform getPlatform()
    {
        return this.platform;
    }

    @Override
    public void handleException(Throwable ex)
    {
        logger.logException(ClassLogger.ERROR, ex, "Exception:%s\n", ex);
        // does ui syncrhonisation:
        ExceptionDialog.show(this.browserFrame, ex);
    }

    @Override
    public JPopupMenu createActionMenuFor(ViewNodeContainer container,ViewNode viewNode, boolean canvasMenu)
    {
        return ActionMenu.createSimpleMenu(this,container, viewNode, canvasMenu);
    }

    /** Set Root Node and update UI */
    public void setRoot(ProxyNode root, boolean update)
    {
        this.rootNode = root;
        ProxyNodeDataSource dataSource = new ProxyNodeDataSource(root.getProxyFactory(), root);
        this.browserFrame.getResourceTree().setDataSource(dataSource, update);
        IconsPanel iconsPnl = browserFrame.getIconsPanel();
        if (iconsPnl!=null)
        	iconsPnl.setDataSource(dataSource, update);
    }

    /** Event from Menu Bar */
    public void handleMenuEvent(ActionEvent e)
    {
        String cmdStr = e.getActionCommand();
        Action theAction = Action.createFrom(getCurrentViewNode(), cmdStr);
        handleMenuAction(theAction);
    }

    @Override
    public void handleMenuAction(Action theAction)
    {
        logger.debugPrintf(">>> ActionPerformed:%s\n", theAction);

        Object source = theAction.getActionSource();

        if (source instanceof ViewNode)
        {
            // node action
            handleNodeAction((ViewNode) source, theAction);
        }
        else
        {
            // Global Menu action !
            handleNodeAction(null, theAction);
            // check source ???
            // logger.fixmePrintf("\n>>>\n>>> FIXME: Global Action (null ViewNode):%s\n>>>\n", theAction);
        }
    }
    
    public void handleNavBarEvent(ActionEvent e)
    {
        logger.debugPrintf(">>> NavBarAction: %s\n", e);
        String cmd=e.getActionCommand(); 

        NavigationAction navAction = NavigationAction.valueOf(cmd); 
        ActionMethod meth=null;  
        
        switch(navAction)
        {
            case BROWSE_BACK:   
                meth=ActionMethod.BROWSE_BACK; 
                break; 
            case BROWSE_UP:
                meth=ActionMethod.BROWSE_UP;
                break; 
            case BROWSE_FORWARD: 
                meth=ActionMethod.BROWSE_FORWARD;
                break; 
            case REFRESH:
                meth=ActionMethod.REFRESH;
                break; 
            case LOCATION_EDITED: 
            case LOCATION_CHANGED:
            default: 
                logger.errorPrintf("FIXME: NavBar action not implemented:%s\n",navAction); 
        }
        
        if (meth!=null)
        {
            Action action=Action.createGlobalAction(meth);
            handleAction(action);
        }
    }

    public void handleAction(Action action) 
    {
        handleNodeAction(null, action); 
    }
    
    @Override
	public void handleNodeAction(ViewNode node, Action action) 
	{
		logger.debugPrintf(">>> nodeAction: %s on:%s\n", action, node);
		boolean global = false;

		if (node == null) 
		{
			// global action from menu on current viewed node!
			node = this.getCurrentViewNode();
			global = true;
		}

		switch (action.getActionMethod()) 
		{
            case BROWSE_BACK: 
                doBrowseBack(); 
                break; 
            case BROWSE_FORWARD: 
                doBrowseForward(); 
                break; 
            case BROWSE_UP: 
                doBrowseUp(); 
                break; 
		    case CREATE:
		        this.proxyActionHandler.handleCreate(action,node); 
		        break; 
		    case CREATE_NEW_WINDOW:
		        createBrowser(node);
		        break;
		    case COPY: 
		        this.proxyActionHandler.handleCopy(action,node); 
		        break; 
		    case COPY_SELECTION:
		        this.proxyActionHandler.handleCopySelection(action,node); 
		        break; 
		    case DELETE:
		        this.proxyActionHandler.handleDelete(action,node); 
    			break;
    		case DEFAULT_ACTION:
    			defaultAction(node);
    			break;
    		case DELETE_SELECTION:
                this.proxyActionHandler.handleDeleteSelection(action,node);  
    		    break; 
    		case OPEN_LOCATION:
    			defaultAction(node);
    			break;
    		case OPEN_IN_NEW_WINDOW:
    			createBrowser(node);
    			break;
    		case PASTE:
                this.proxyActionHandler.handlePaste(action,node); 
    		    break; 
    		case NEW_TAB:
    		    createNewTab(node); 
    			break ;
    		case CLOSE_TAB:
                closeCurrentTab(); 
                break;
            case REFRESH:
                doRefresh(node); 
                break;
            case VIEW_AS_ICONS:
                doViewAsIcons();
                break; 
            case VIEW_AS_ICON_LIST:
                doViewAsList(); 
                break; 
            case VIEW_AS_TABLE: 
                doViewAsTable(); 
                break; 
            default:
    			logger.errorPrintf("\n",
    			        ">>>\n>>> FIXME: ACTION NOT IMPLEMENTED:%s !\n<<<\n",
    					action);
    			break;
		}
	}

	

    private void doRefresh(ViewNode node)
    {
        ProxyNodeEventNotifier.getInstance().scheduleEvent(
                ProxyNodeEvent.createRefreshEvent(null, node.getVRI()));
    }

    private void doViewAsTable()
    {
        this.browserFrame.setViewMode(BrowserViewMode.TABLE); 
    }

    private void doViewAsList()
    {
        this.browserFrame.setViewMode(BrowserViewMode.ICONLIST);
    }

    private void doViewAsIcons()
    {
        this.browserFrame.setViewMode(BrowserViewMode.ICONS);
    }

    private void doBrowseForward()
    {
        VRI loc = history.forward();
        logger.debugPrintf("doBrowseForward:%s\n",loc);
        
        if (loc!=null)
            this.openLocation(loc,false,false);
    }

    private void doBrowseBack()
    {
        VRI loc = history.back();
        logger.debugPrintf("doBrowseBack:%s\n",loc);

        if (loc!=null)
            this.openLocation(loc,false,false);
    }

    private void doBrowseUp()
    {
    	if (this.getCurrentViewNode()==null)
    	{
    		logger.warnPrintf("*** Warning: NULL CurrentViewNode!\n"); 
    		return; 
    	}

    	final VRI loc = this.getCurrentViewNode().getVRI();  
        
        final ProxyFactory factory = this.platform.getFactoryFor(loc);
        

        ProxyBrowserTask task = new ProxyBrowserTask(this, "doBrowseUp():" + loc)
        {
            @Override
            protected void doTask()
            {
                try
                {
                	
                    ProxyNode node = factory.openLocation(loc);
                    VRI parentLoc = node.getParentLocation();
                    if (parentLoc==null)
                        return; // NO parent; 
                    
                    openLocation(parentLoc,true,false); 
                }
                catch (Throwable e)
                {
                    handleException(e);
                }
            }
        };

        task.startTask();
    }
 
    private void addToHistory(VRI loc)
    {
        logger.debugPrintf("addToHistory:%s\n",loc);
        this.history.add(loc); 
    }

    // Open,etc //
    public void openNode(ViewNode actionNode)
    {
        openLocation(actionNode.getVRI(),true,false);
    }

    public void defaultAction(ViewNode actionNode)
    {
        openLocation(actionNode.getVRI(),true,false);
    }

    protected void createNewTab(ViewNode node)
    {
    	this.openLocation(node.getVRI(),true,true); 
	}

    protected void closeCurrentTab()
    {
        TabContentPanel tab = this.browserFrame.getCurrentTab();
        this.browserFrame.closeTab(tab); 
    }
    
    private void setViewedNode(ProxyNode node, Icon icon, boolean addHistory, boolean newTab)
    {
    	TabContentPanel tab; 
    	
    	if (newTab==false)
    	{
    		tab = this.browserFrame.getCurrentTab(); 
    		
    		tab.setName(node.getName()); 
    		
	        if (node.isComposite())
	        {
	        	JComponent comp = tab.getContent(); 
	        	
	        	if (comp instanceof IconsPanel)
	        	{
	        		((IconsPanel)comp).setDataSource(node, true);
	        	}
	        	
	        	else if (comp instanceof ResourceTable)
	        	{
	        		((ResourceTable)comp).setDataSource(node, true);
	        	}
	        	else
	        	{
	        	    logger.errorPrintf("\n" + ">>>\n>>> FIXME: Unknown Component:%s\n>>>\n", comp.getClass());
	        	}
	        }
	        else
	        {
	            logger.errorPrintf("\n" + ">>>\n>>> FIXME: Set SingleNode view:%s\n>>>\n", node);
	        }
    	}
    	else
    	{
    		tab=browserFrame.createIconsPanelTab(node); 
    	}
    	browserFrame.setTabTitle(tab,node.getName()); 
    	
        updateNavBar(node.getVRI(),icon); 
        
        if (addHistory)
            addToHistory(node.getVRI());
    }
    
    public void openLocation(final VRI locator,final boolean addToHistory, final boolean newTab)
    {
        logger.debugPrintf(">>> openLocation: %s\n", locator);

        final ProxyFactory factory = this.platform.getFactoryFor(locator);

        if (factory == null)
        {
            handleException(new Exception("Cannot find factory for:" + locator));
            return;
        }

        // pre: update nav bar: 
        this.updateNavBar(locator,null); 
        
        ProxyBrowserTask task = new ProxyBrowserTask(this, "openLocation" + locator)
        {
            @Override
            protected void doTask()
            {
                try
                {
                    ProxyNode node = factory.openLocation(locator);
                    setViewedNode(node, node.getIcon(16), addToHistory,newTab);
                }
                catch (Throwable e)
                {
                    handleException(e);
                }
            }
        };

        task.startTask();
    }
    
    public void updateNavBar(VRI locator, Icon icon)
    {
        NavigationBar navbar = this.browserFrame.getNavigationBar();
        navbar.setLocationText(locator.toString());

        if (icon != null)
            navbar.setIcon(icon);
    }

    private ViewNode getCurrentViewNode()
    {
        ViewNode node = this.browserFrame.getCurrentTabViewedNode();
        if (node!=null)
            return node;
        node=this.browserFrame.getResourceTree().getCurrentSelectedNode(); 
        return (node); 
    }

    private ProxyBrowser createBrowser(ViewNode node)
    {
        // clone browser and update ViewNode
        ProxyBrowser newB = new ProxyBrowser(this.platform, true);
        newB.setRoot(this.rootNode, true);
        newB.setCurrentViewNode(node);

        return newB;
    }

    private void setCurrentViewNode(ViewNode node)
    {
        logger.debugPrintf(">>> Update Current ViewNode: %s\n", node);
        this.openLocation(node.getVRI(),true,false);
    }

    public ProxyBrowserTaskWatcher getTaskWatcher()
    {
        return taskWatcher;
    }

 
    /** Message  package */  
    void messagePrintf(Object source,String format, Object... args)
    {
        logger.infoPrintf("MESG:"+format, args); 
    }


    public void updateHasActiveTasks(boolean active)
    {
        logger.infoPrintf("HasActiveTasks=%s\n",active); 
    }
    
}