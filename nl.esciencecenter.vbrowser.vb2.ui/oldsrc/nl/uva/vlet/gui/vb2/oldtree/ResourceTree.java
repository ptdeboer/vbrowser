/*
 * Copyright 2006-2010 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: ResourceTree.java,v 1.1 2012/11/18 13:20:35 piter Exp $  
 * $Date: 2012/11/18 13:20:35 $
 */ 
// source: 

package nl.uva.vlet.gui.vb2.oldtree;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import nl.uva.vlet.Global;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.exception.VlInternalError;
import nl.uva.vlet.gui.MasterBrowser;
import nl.uva.vlet.gui.UIGlobal;
import nl.uva.vlet.gui.VComponent;
import nl.uva.vlet.gui.VContainer;
import nl.uva.vlet.gui.data.ResourceRef;
import nl.uva.vlet.gui.dnd.VDragGestureListener;
import nl.uva.vlet.gui.dnd.VTransferHandler;
import nl.uva.vlet.gui.proxymodel.ViewItem;
import nl.uva.vlet.gui.proxynode.ProxyNode;
import nl.uva.vlet.gui.vbrowser.BrowserController;
import nl.uva.vlet.tasks.ActionTask;
import nl.uva.vlet.vrl.VRL;

/**
 * The ResourceTree presents an abstract representation of a Resource which is
 * ordered in a tree like structure (for example a File System). This is a Subclassed
 * Swing bean which combines the abstract VNode interface and the feature rich
 * GUI component JTree (=View)
 * 
 * It handles both the GUI component (JTree) as well as the underlaying
 * resource. All the specified VNode/VRL stuff is done in ResourceTreeNode. 
 * 
 * @see javax.swing.JTree,nl.uva.vlet.vrs.VNode,nl.uva.vlet.ResourceHandler
 * 
 * @author P.T. de Boer
 */

public class ResourceTree extends JTree implements VContainer
{
    /** Instance counter for statistics and debuggering */
    private static long resourceTreeCounter = 0;

    private static final long serialVersionUID = 200173418039849226L;
    
    // ================
    // === Instance ===
    // ================
    
    /** this instance's id */
    private long id = resourceTreeCounter++;

    private MasterBrowser masterBrowser;

    /** Always keep root node visible */

    private boolean keepRootVisible = true;

    private ResourceTreeListener resourceTreeListener;

	private DragSource dragSource;

	private VDragGestureListener dgListener;

	private AutoScoller autoScroller;

	private int mouseOverRow;

	//private ResourceTreeNode focusNode;
  
	private ResourceTreeUpdater treeDataProducer;

    /**
     * Set the new root, but expand the childs until the pathNode is found. 
     * This method it used when creating a new Resource Tree with 
     * a rootNode which must show the subtree until pathNode 
     */

    public void setRootNode(ProxyNode rootNode, ProxyNode pathNode)
            throws VlException
    {
        if (rootNode != null)
            // first set new root: 
            setRootNode(rootNode);

        // expand childs until the pathNode is found 
        asyncFindAndSelectPath(pathNode);
    }
    
    /** search path and expand+scroll to it */ 
    
    public void viewNode(ProxyNode viewedNode)
    {
        // expand childs until the pathNode is found 
        asyncFindAndSelectPath(viewedNode);
    }
    
    /**
     * 
     * Asynchronous method to expand the current tree until
     * the pathNode is found. Currently used to recreate a 
     * (sub)tree when opening a node in a new Window. 
     */
    private void asyncFindAndSelectPath(final ProxyNode pathNode)
    {
        if (pathNode==null)
        {
            return; // unselect current ? 
            
        }
        ResourceTreeNode current = this.getSelectedNode();
 
        if (current != null)
        {
            ProxyLocator currentLoc = current.getVRL();
            ProxyLocator pathLoc = pathNode.getVRL();

            debug("currentLoc=" + currentLoc);
            debug("find pathLoc=" + pathLoc);

            // if current selected one is tha one, return 
            if (currentLoc.compareTo(pathLoc) == 0)
            {
                return;
            }
        }

        final ResourceTreeNode rootNode = getRootNode();
        final ResourceTree tree = this;

        // first wait until the rootnode is populated (in the case of a new tree) 
        ActionTask task = new ActionTask(this.masterBrowser,
                "find tree path")
        {

            boolean dontstop = true;

            @Override
            public void doTask()
            {
                // init: root might not be populated yet:
                rootNode.waitForPopulated();

                Vector<ProxyLocator> parentLocs = new Vector<ProxyLocator>();

                ProxyLocator loc = pathNode.getVRL();
                ProxyLocator prevLoc = null;

                // add node itself to end of path
                parentLocs.insertElementAt(loc, 0);

                ResourceTreeNode lastNode = null;

                // browse up to root path (and avoid deadloc) 
                while ((loc.isRootPath() == false)
                        && (loc.compareTo(prevLoc) != 0))
                {
                    prevLoc = loc;
                    loc = loc.getParent(); // insert parentLoc  
                    parentLocs.insertElementAt(loc, 0);

                    // stop the search if the parent node is found in the current tree: 
                    if (findNodeWithLocation(loc, true) != null)
                        break;
                }

                // now loop from top to down and expand the path   
                for (ProxyLocator pLoc : parentLocs)
                {
                    Global.debugPrintln("ResourceTree", "Auto Expand path:"
                            + pLoc);
                    // check if parent node is visible: 

                    ResourceTreeNode node = tree.findNodeWithLocation(pLoc,
                            true);

                    if (dontstop == false)
                        return;

                    // set and expand: 

                    if (node != null)
                    {
                        if (node.isPopulated() == false)
                            asyncPopulate(node);

                        // wait until it is populated: 
                        node.waitForPopulated();

                        //tree.setSelection(node,false); 

                        Global.debugPrintln("ResourceTree",
                                "findPath:setSelection to:" + node);
                        lastNode = node; // keep last update node 
                    }
                    else
                    {
                        Global.debugPrintln("ResourceTree",
                                "findPath:couldn't find:" + pLoc);
                    }

                }

                if (lastNode != null)
                {
                    final ResourceTreeNode finalNode = lastNode;

                    finalNode.waitForPopulated();

                    // after last populate: set selection 
                    Runnable runT = new Runnable()
                    {
                        public void run()
                        {

                            tree.setSelection(finalNode, true);
                        }
                    };

                    SwingUtilities.invokeLater(runT);

                }

            }

            public void stopTask()
            {
                dontstop = false;
            }
        };

        task.startTask();

    }

    /** Set New Root Resource */

    public void setRootNode(ProxyNode pnode) throws VlException
    {
    	Global.infoPrintln(this,"New RootNode:"+pnode); 
    	
        if (pnode == null)
            return;
        
        // resolve links ! 

        if (pnode.isResourceLink())
        {
        	pnode = pnode.getTargetPNode();
        }
        
        this.treeDataProducer.updateRootnode(pnode); 
    }
        
    protected void setTreeDataProducer(
			ResourceTreeUpdater producer)
	{
    	this.treeDataProducer=producer;
	}
    
    protected ResourceTreeUpdater getTreeDataProducer()
	{
    	return treeDataProducer;
	}

	/**
     * Keep jigloo happy: define void contructor with PUBLIC Modifier so
     * that Jigloo is able to create a dummy one !
     * @param controller 
     */
    public ResourceTree()
    {
        super((TreeModel) null);// Create the JTree itself
        init(null);
    }

    public ResourceTree(MasterBrowser controller)
    {
        super((TreeModel) null);// Create the JTree itself
        this.masterBrowser = controller;
        init(controller);
    }
    /** Set default settings */
    public void init(MasterBrowser controller)
    {
    	//
    	// Set ResourceTree properties 
    	//

        // Use horizontal and vertical lines
        putClientProperty("JTree.lineStyle", "Angled");
        // nr of clicks to c
        this.setToggleClickCount(3);
        
        // some settings: 
        putClientProperty("JTree.lineStyle", "Angled");
        this.setScrollsOnExpand(true);
        this.setExpandsSelectedPaths(true);
        // Keep generating scroll events even when draggin out of window ! 
        this.setAutoscrolls(true); 
        this.setExpandsSelectedPaths(true);
        this.setRootVisible(this.keepRootVisible); 
        
        TreeSelectionModel selectionModel=new DefaultTreeSelectionModel(); 
        
        this.setSelectionModel(selectionModel); 
        // DND
        initDND();
        
        // empty model !
        ResourceTreeModel treeModel=new ResourceTreeModel(controller.getViewModel(),null,true);
        this.setModel(treeModel);
        
        ResourceTreeUpdater dataProducer=new ResourceTreeUpdater(this,ProxyNode.getProxyNodeFactory(),treeModel);
        setTreeDataProducer(dataProducer);
        
        ResourceTreeCellRenderer renderer = new ResourceTreeCellRenderer();
        setCellRenderer(renderer);

        //
        // LAST: initialize listeners: 
        // 
        resourceTreeListener = new ResourceTreeListener(controller,this);
        
        // Listen for Tree Selection Events
        addTreeExpansionListener(new TreeExpansionHandler());


        this.addFocusListener(this.resourceTreeListener);
        this.addMouseListener(resourceTreeListener);
        this.addMouseMotionListener(resourceTreeListener);
        this.setFocusable(true); 

        //this.setBorder(new BevelBorder(BevelBorder.RAISED));
    }
    
    private void initDND()
    {

        //  DropTarget AND Tranferhandler need to be set ! 
        this.setDropTarget(new ResourceTreeDropTarget(this)); 
        
        this.setTransferHandler(VTransferHandler.getDefault());
        
        this.dragSource = DragSource.getDefaultDragSource();
        this.dgListener = new VDragGestureListener();
        //this.dsListener = MyDragSourceListener.getDefault(); 
        // component, action, listener
        this.dragSource.createDefaultDragGestureRecognizer(
                this, DnDConstants.ACTION_COPY_OR_MOVE, this.dgListener );
    }
    
    /**
     * @return Returns the rootVNode. Never returns null.
     */
    public ViewItem getRootViewItem()
    {
    	return getModel().getRootViewItem(); 
    }

    public ResourceTreeModel getModel()
    {
    	return (ResourceTreeModel)super.getModel(); 
    }
    
    // return single path element of node pointer by complete TreePath path 
    public String getPathName(TreePath path)
    {
        Object o = path.getLastPathComponent();

        if (o instanceof ProxyNode)
        {
            // return ((VNode) o).getLocation();
            return ((ProxyNode) o).getName();
        }
        return null;
    }

    public ResourceTreeNode getNode(TreePath path)
    {
        Object o = path.getLastPathComponent();

        if (o == null)
        {
            debug("No Node found at tree path:" + path);
        }
        else if (o instanceof ResourceTreeNode)
        {
            // instanceof returns false when o==null, so here o exists.
            return (ResourceTreeNode) o;
        }

        return null;
    }

    /**
     * Inner class that handles Tree Expansion Events
     */
    protected class TreeExpansionHandler implements TreeExpansionListener
    {

        public void treeExpanded(TreeExpansionEvent evt)
        {
            debug("treeExpanded:" + evt);

            TreePath path = evt.getPath();// The expanded path JTree tree =
            JTree tree = (JTree) evt.getSource();// The tree

            // Get the last component of the path and
            // arrange to have it fully populated.
            ResourceTreeNode node = (ResourceTreeNode) path
                    .getLastPathComponent();

            if (node.isPopulated() == false)
                asyncPopulate(node);
            else
                notifySizeChange(); // resync is enough
        }
        
        public void treeCollapsed(TreeExpansionEvent evt)
        {
            // Nothing to do
            notifySizeChange();
        }
    }

    /**
     * Explicitly update size. 
     * Somehow the parent container doens't see the size changes. PreferredSize
     * is used by the ViewPort to determine the ViewPort size, but the JTRee
     * only updates maximumsize.
     * 
     */
    protected void notifySizeChange()
    {
        setPreferredSize(getMaximumSize());
    }
    
    public void asyncPopulate(ResourceTreeNode node)
    {
        debug("asyncPopulate:"+this);  

        // claim();
        if (node.isPopulated() == false)
        {
            // remove dummy/invalid children
        	node.removeAllChildren();
        }
       
        final ProxyLocator vrl =node.getVRL(); 
       
        if (vrl == null)
        {
            Global.warnPrintln(this,"Warning: ResourceTreeNode has no ProxyTNode");
            return; 
            // release();
        }
         
        final MasterBrowser bc=getMasterBrowser();
        
        if (bc instanceof BrowserInterface)
        {
        	if (((BrowserInterface)bc).interactiveCheckAuthenticationFor(vrl)==false)
        		return;
        }
        
         // update in background using Data Producer ! 
        
        treeDataProducer.bgGetChildsFor(node.getVRL()); 
    
    }
    
    private void debug(String str)
    {
        //Global.errorPrintln(this, str);
        Global.debugPrintln(this, str);
    }

    /**
     * @return
     */
    public ResourceTreeNode getRootNode()
    {
        if (getModel() == null)
            return null;

        return (ResourceTreeNode) getModel().getRoot();
    }

    /**
     * Set selection of ResourceTee, if node NOT found, update rootNode if resourceTree
     * @param node
     * @param newroot    ; set this node as root is not found in tree
     * @throws VlException 
     * @throws VlException 
     */
    public void setSelection(ProxyNode pnode, boolean newRoot)
            throws VlException
    {
        debug("setSelection pnode:" + pnode);

        // tricky, ProxyNode has to be found in current resource tree 
        ResourceTreeNode node = getNodeFromProxy(pnode, false);

        if (node == null)
            node = getNodeFromProxy(pnode, true); // check symlinks as well 

        // if node is NOT found in current tree, completely update resourcetree

        if ((node == null) && (newRoot == true) && (keepRootVisible == false))
        {
            try
            {
                this.setRootNode(pnode);
            }
            catch (VlException e)
            {
                Global.errorPrintStacktrace(e); 
            }
        }

        debug("setSelection node:" + pnode);

        setSelection(node, true);
    }

    private ResourceTreeNode getNodeFromProxy(ProxyNode pnode,
            boolean checkLinkNodes)
    {
        if (pnode == null)
            return null;

        return findNodeWithLocation(pnode.getVRL(), checkLinkNodes);
    }

    /** 
     * The current method to find the matching resource tree from a VRL 
     * or ProxyNode is by doing a depth-first treewalk.
     *  
     * @param pnode
     * @return
     */
    ResourceTreeNode findNodeWithLocation(ProxyLocator loc,
            boolean checkLinkTargets)
    {
        //debug(">>> findNode:"+loc); 
    	int level=0; 
    	ResourceTreeNode node = _findNodeWithLocation(level,loc,checkLinkTargets);
    	return node; 
    }
    
    private ResourceTreeNode _findNodeWithLocation(int level,ProxyLocator loc,boolean checkLinkTargets)
    {
        debug(">> find:"+loc);  
        
     	ResourceTreeNode selected = this.getSelectedNode();

    	if (selected != null)
    	{
    	    if (compareLocations(selected,loc,checkLinkTargets)) 
    	    {
    	        debug(" - found (I):"+selected); 
    			return selected;
    	    }
    	}
        

        ResourceTreeNode current = this.getRootNode();// start with root 
        ResourceTreeNode node = null;
        int childindex = 0;

        // parent heap for depth-first searching the tree
        Vector<ResourceTreeNode> heap = new Vector<ResourceTreeNode>();
        int depth = 0;
               
        while (current != null)
        {
            
            if (compareLocations(current,loc,checkLinkTargets)) 
    		{
                debug(" - found (II):"+current); 
    		    node = current; // FOUND !
                current = null;
                break;
            }

            // go into subtree  
            if (current.isLeaf() == false)
            {
                // keep parent, go into subtree 
                if (depth >= heap.size())
                {

                    heap.add(current); // increment depth level
                    depth = heap.size();
                }
                else
                {
                    heap.set(depth++, current);
                }

                current = (ResourceTreeNode) current.getFirstChild();
            }
            else
            {
                // next sibling, or go up, and keep going up if node has no next sibling 
                //              fetch next sibling
                current = (ResourceTreeNode) current.getNextSibling();

                while ((current == null) && (depth > 0))
                {
                    // fetch parent sibling (uncle?)
                    current = (ResourceTreeNode) heap.elementAt(--depth)
                            .getNextSibling();
                }

            }

        }
        
        if (node==null)
            debug("NOT found:"+loc); 
        //System.err.println("find Node with location returning:"+node); 

        return node;
    }

    private boolean compareLocations(ResourceTreeNode node, ProxyLocator loc, boolean checkLinkTargets)
    {
        ViewItem item=node.getViewItem(); 
        // plain VRL compare:
        if (item.equalsLocation(loc,checkLinkTargets)==true)
            return true;
        
        // Todo Fix: Cached method to resolve VRL aliases !  
        // For example target Vrl (=alias) could be : gftp://host/~/path"
        // but actual (resolveD) target Vrl could be: gftp://host/home/user/path" 
        // or even a 'full' hostname like           : gftp://host.domain.org/home/user/path
        // If an node is openened which has an alias the cached ProxyNode has these VRLs! 
        
        if (checkLinkTargets==false)
            return false; 
        
        // Check cache for 'resolved' aliases 
        ProxyNode pnode = ProxyNode.getProxyNodeFactory().getFromCache(item.getTargetVRL());
        
        if (pnode!=null)
        {
            // get ALL 'equivalent' VRLs ! 
            ProxyLocator vrls[]=pnode.getAliasVRLs();
            if (vrls==null) 
                return false;
            
            // check all aliases ! 
            for (ProxyLocator vrl:vrls)
               if (loc.equals(vrl))
                   return true;
        }
        
        return false;  
    }

    public void setSelection(ResourceTreeNode node, boolean expand)
    {
        debug(">>> setSelection:" + node);
        // System.err.println(">>> setSelection:"+node);

        // NiNo
        if (node == null)
            return;

        TreeNode[] treeNodes = node.getPath();
        TreePath treePath = new TreePath(treeNodes);
        this.setSelectionPath(treePath);
        
        if (node.isPopulated())
        {
            asyncPopulate(node);
        }

        this.setExpandsSelectedPaths(true);

        if (expand)
            expandPath(treePath); // expand
        else
            makeVisible(treePath); // show path

        this.setSelectionPath(treePath);
        // scroll to node: 
        scrollRowToVisible(getRowForPath(treePath));

    }

    public ResourceTreeNode getSelectedNode()
    {
        TreePath path = getSelectionPath();

        if (path == null)
            return null;

        return (ResourceTreeNode) path.getLastPathComponent();
    }

    public MasterBrowser getMasterBrowser()
    {
        return this.masterBrowser;
    }

    public void dispose()
    {
    	if (this.treeDataProducer!=null)
    	{
    		this.treeDataProducer.dispose();
    		this.treeDataProducer=null; 
    	}
    	
    }

    /** Used for Mouse Events : */
    public ResourceTreeNode getNodeUnderPoint(Point p)
    { 
    	if (p==null)
    		return null;
    	
        int clickRow = getRowForLocation(p.x, p.y);

        TreePath path = getPathForLocation(p.x, p.y);

        if ((path == null) || (clickRow < 0))
        {
            // no node under mouse click 
            return null;
        }

        return getNode(path);
    }
    
    // ========================================================
    // VContainer Interface
    // ========================================================
    
	public ResourceRef[] getSelection()
	{
		TreePath paths[] = getSelectionPaths();
		if (paths==null) 
			return null; // no selection 
		ResourceRef vrls[]=new ResourceRef[paths.length];
		
		int index=0; 
		for (TreePath path:paths)
		{
			ResourceTreeNode node = this.getNode(path);
			String type=node.getResourceType(); 
			
			if (node!=null)
				vrls[index++]=node.getResourceRef(); 
		}
		
		return vrls; 
	}
	
	public ProxyLocator getVRL()
	{
		// nullpointer; 
		if (getRootNode()==null)
			return null; 
		
		return this.getRootViewItem().getVRL(); 
	}

	public ResourceRef getResourceRef()
	{
		return this.getRootViewItem().getResourceRef(); 
	}

	public VComponent getFocusComponent()
	{
		return this.getSelectedNode(); 
	}
	
	/** Resource Tree itself is not embedded in a VContainer itself */ 
	public VContainer getVContainer()
	{
		return null;
	}

	public String getResourceType()
	{
		return this.getRootViewItem().getResourceType(); 
	}
	
	class AutoScoller implements ActionListener
	{
		ResourceTree tree; 
		Point speed=new Point(0,0); 
		private Timer timer=null;  
		int delay=20; // 50 update/s ! 
		
		public AutoScoller(ResourceTree tree, Point p)
		{
			this.tree=tree;
			setTargetPoint(p); 
		}
		
		public void setTargetPoint(Point p)
		{
			int dx=16;
			int dy=24;
			
			Rectangle outer=tree.getVisibleRect(); 
			
			if (p.y<(outer.y+dy))
				speed.y=p.y-(outer.y+dy);
			else if (p.y>(outer.y+outer.height-dy))
				speed.y=p.y-(outer.y+outer.height-dy);
			else
				speed.y=0;
			
			if (p.x<(outer.x+dx))
				speed.x=p.x-(outer.x+dx);
			else if (p.x>(outer.x+outer.width-dx))
				speed.x=p.x-(outer.x+outer.width-dx);
			else
				speed.x=0;
		}
		
		public void start()
		{
			if (timer==null)
			{
				timer=new Timer(delay,this);
				timer.start();
			}
		}
		
		public void stop()
		{
			if (timer!=null)
			{
				timer.stop();
				timer=null; 
			}
		}

		public void actionPerformed(ActionEvent e)
		{
			debug("scroll speed="+speed); 
			 
			Rectangle rect = tree.getVisibleRect(); 
			
			if ((speed.x!=0) || (speed.y!=0))
			{
				rect.x+=speed.x; 
				rect.y+=speed.y; 
				tree.scrollRectToVisible(rect);
			}
			else
			{
				debug("stopping autoscoller="+speed); 
				stop();
			}
		}
	}
	
	public void scrollTo(Point p)
	{
		startAutoScroller(p); 
	}

	
	private void startAutoScroller(Point p)
	{
		if (autoScroller==null)
		{
			autoScroller=new AutoScoller(this,p);
		}
		
		autoScroller.setTargetPoint(p);
		autoScroller.start();
	}

//	/** Update focus node and request redraw */
//	public void setFocusNode(ResourceTreeNode node)
//	{
//		ResourceTreeNode prevNode = this.focusNode; 
//		this.focusNode=node; 	 
//	
//		// redraw: 
//    	if (node!=null) 
//    		repaintNode(node);
//    	
//    	if (prevNode!=null) 
//    		repaintNode(prevNode); 
//	}
	
	public void repaintNode(ResourceTreeNode node)
	{
		if (node==null)
			return; 

		 getResourceTreeModel().nodeChanged(node);
	}
	
	public DefaultTreeModel getResourceTreeModel()
	{
		return (ResourceTreeModel)this.getModel(); 
	}

//	public ResourceTreeNode getFocusNode()
//	{
//		return focusNode; 
//	}

	public void stopAutoScroller()
	{
		if (this.autoScroller!=null)
			this.autoScroller.stop(); 
	}
 
	public String toString()
	{
		return "{ResourceTree:"+getVRL()+"}";
	}

	public VComponent getVComponent(ResourceRef ref)
	{
		return findNodeWithLocation(ref.getVRL(),false); 
	}
	
	// Used for Mouse Over Effects 
	public void setMouseOverPoint(Point p)
	{
	    int newRow=this.mouseOverRow; 
	    
	    if (p==null)
	    {
	        newRow=-1;
	    }
	    else
	    {
   	 	     newRow = getClosestRowForLocation((int)(p.getX()),(int)(p.getY()));
	    }
	    
	    if(newRow != this.mouseOverRow)
	    {
	        this.mouseOverRow=newRow; 
            repaint();
        }
	}
	
	public int getMouseOverRow()
	{
		return mouseOverRow;
	}

    @Override
    public void selectAll(boolean selectValue)
    {
        
        
    }
	

}
