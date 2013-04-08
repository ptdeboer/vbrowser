package nl.vbrowser.ui.tree;

import java.util.List;

import nl.nlesc.ptk.net.VRI;
import nl.nlesc.ptk.util.logging.ClassLogger;
import nl.vbrowser.ui.model.DataSource;
import nl.vbrowser.ui.model.UIViewModel;
import nl.vbrowser.ui.model.ViewNode;
import nl.vbrowser.ui.proxy.ProxyException;
import nl.vbrowser.ui.proxy.ProxyNodeEvent;
import nl.vbrowser.ui.proxy.ProxyNodeEventListener;
import nl.vbrowser.ui.tasks.UITask;

/**
 * Gets relevant data from DataSource and updates the ResourceTreeModel.  
 */
public class ResourceTreeUpdater implements ProxyNodeEventListener 
{
	private static ClassLogger logger;

	static
	{
		logger=ClassLogger.getLogger(ResourceTreeUpdater.class); 
	}
	  
	private ResourceTree tree;

	private DataSource viewNodeSource;

	private ViewNode rootItem;     

    public ResourceTreeUpdater(ResourceTree tree,DataSource viewNodeSource) 
    {
    	this.tree=tree;
    	setDataSource(viewNodeSource,false); 
    }
    
    public ResourceTreeModel getModel()
    {
        return tree.getModel(); 
    }
    
    public UIViewModel getUIModel()
    {
        return tree.getUIViewModel(); 
    }
    
    public void setDataSource(DataSource viewNodeSource,boolean update)
    {
        // unregister previous 
        if (viewNodeSource!=null)
            viewNodeSource.removeDataSourceListener(this); 
        
        this.viewNodeSource=viewNodeSource;
        
        // register me as listener 
        if (viewNodeSource!=null)
        {
        	viewNodeSource.addDataSourceListener(this); 
        }
        if (update)
        	updateRoot(); 
    }
    
    public void updateRoot()
    {
    	logger.debugPrintf("updateRoot():\n");
    	
    	UITask task=new UITask("update resource tree model for node"+rootItem)
        {
            @Override
            public void doTask()
            {
            	_updateRoot();                
            }

            @Override
            public void stopTask()
            {
            }
        }; 
        
        task.startTask(); 
    }

    /** Get or repopulate childs nodes */
	public void updateChilds(final ResourceTreeNode node) 
	{
		logger.debugPrintf("updateChilds():%s\n",node.getVRI());
    	
        UITask task = new UITask("update resource tree model for node"+rootItem)
        {
            @Override
            public void doTask()
            {
            	_updateChilds(node);                
            }

            @Override
            public void stopTask()
            {
            }
        }; 
        
        task.startTask(); 
	}
	
    // ========================================================================
    // Actual Update Tasks
    // ========================================================================
    
    private void _updateRoot()
    {
        try
        {
        	this.rootItem=viewNodeSource.getRoot(getUIModel());
        	logger.debugPrintf("updateRoot():%s\n",rootItem.getVRI());
        	
        	logger.debugPrintf("_updateRoot():%s\n",rootItem.getVRI());
    		
        	ResourceTreeNode rtRoot=new ResourceTreeNode(null,rootItem,true); 
        	getModel().setRoot(rtRoot);
        	
        	// pre fetch childs and populate root in advance: 
        	
        	ViewNode[] childs = viewNodeSource.getChilds(getUIModel(),rootItem.getVRI(),0,-1,null); 
        	
        	if (childs==null) 
        	{
        		rtRoot.clear();
        		rtRoot.setPopulated(true); 
        	}
        	else
        	{
        		getModel().setChilds(rtRoot,childs); 
        	}
        }
        catch (ProxyException e)
        {
            handle(e); 
        } 
    }

	private void _updateChilds(final ResourceTreeNode node) 
	{
		ViewNode[] childs;
		
        try
        {
            childs = this.viewNodeSource.getChilds(getUIModel(),node.getVRI(),0,-1,null);
            getModel().setChilds(node,childs);
        }
        catch (ProxyException e)
        {
            handle(e); 
        } 
 
	}

	private void handle(Throwable e)
    {
	    this.tree.getMasterBrowser().handleException(e); 
    }

    @Override
	public void notifyDataSourceEvent(ProxyNodeEvent e) 
	{
        logger.debugPrintf("notifyDataSourceEvent:%s\n",e);
        
        VRI parent = e.getParent(); 
        VRI[] sources = e.getResources();
        
        switch(e.getType())
        {
            case RESOURCES_DELETED:
                deleteNodes(sources); 
                break; 
            case REFRESH_RESOURCES:
                refreshNodes(sources); 
                break; 
            case RESOURCES_ADDED:
                addNodes(parent,sources); 
                break; 
        }
	}

    private void deleteNodes(VRI[] sources)
    {
        for (VRI loc:sources)
        {
            List<ResourceTreeNode> nodes = getModel().findNodes(loc);
            
            if (nodes!=null)
            {
                for (ResourceTreeNode node:nodes)
                {
                    getModel().deleteNode(node,true); 
                }
            }
        }
    }
    
  
    private void refreshNodes(VRI[] sources)
    {
        for (VRI loc:sources)
        {
            List<ResourceTreeNode> nodes = getModel().findNodes(loc);
            
            if (nodes!=null)
            {
                for (ResourceTreeNode node:nodes)
                {
                    updateChilds(node); // does background refetch   
                }
            }
            
        }
    }
    
    private void addNodes(final VRI parent,final VRI[] sources)
    {
        logger.debugPrintf("addNodes():%s\n",parent);
        
        UITask task = new UITask("update resource tree model for node"+rootItem)
        {
            @Override
            public void doTask()
            {
                _addNodes(parent,sources);                
            }

            @Override
            public void stopTask()
            {
            }
        }; 
        
        task.startTask(); 
    }
    
    private void _addNodes(final VRI parent,final VRI[] sources)
    {
        try
        {
            ViewNode childs[]; 
            
            childs=viewNodeSource.getNodes(getUIModel(),sources); 
            
            List<ResourceTreeNode> nodes = getModel().findNodes(parent);
            
            if (nodes!=null)
                for (ResourceTreeNode node:nodes)
                    getModel().addNodes(node,childs); 
                
        }
        catch (ProxyException e)
        {
            handle(e); 
        } 
    }


}
