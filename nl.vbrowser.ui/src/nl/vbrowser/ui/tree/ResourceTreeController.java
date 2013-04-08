package nl.vbrowser.ui.tree;

import javax.swing.JPopupMenu;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.TreePath;

import nl.nlesc.ptk.util.logging.ClassLogger;
import nl.vbrowser.ui.actionmenu.Action;
import nl.vbrowser.ui.browser.BrowserInterface;
import nl.vbrowser.ui.model.ViewNode;
import nl.vbrowser.ui.model.ViewNodeActionListener;

public class ResourceTreeController implements TreeExpansionListener, ViewNodeActionListener
{
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(ResourceTreeController.class); 
    }
    
	private ResourceTree tree;
    private BrowserInterface browser;

	public ResourceTreeController(BrowserInterface browser,ResourceTree resourceTree,
			ResourceTreeModel model) 
	{
		this.tree=resourceTree; 
		this.browser=browser; 
	}
	
	public void handleNodeActionEvent(ViewNode node,Action action)
	{
	    this.browser.handleNodeAction(node,action); 
	}

	// From TreeExpansionListener
    public void treeExpanded(TreeExpansionEvent evt)
    {
        logger.debugPrintf("TreeExpansionHandler.treeExpanded()\n");
        
        TreePath path = evt.getPath();
        if (evt.getSource().equals(tree)==false)
        {
            logger.errorPrintf("***Received event from different tree!\n"); 
            return ; 
        }
        // Get the last component of the path and
        // arrange to have it fully populated.
        ResourceTreeNode node = (ResourceTreeNode) path.getLastPathComponent();

        if (node.isPopulated() == false)
            tree.populate(node); 
        // else update ? 
    }
    
    // From TreeExpansionListener
    public void treeCollapsed(TreeExpansionEvent evt)
    {
        logger.debugPrintf("TreeExpansionHandler.treeCollapsed()\n"); 
    }

    /** Package protected debug handler */
	void debugPrintf(Object source,String format, Object... args) 
	{
		//logger.debugPrintf(ClassLogger.object2classname(source)+":"+format,args); 
	}

	public BrowserInterface getMasterBrowser()
	{
		return this.browser; 
	}

}
