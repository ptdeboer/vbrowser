package nl.esciencecenter.vbrowser.vb2.ui.tree;

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;

/**
 * ResourceTreeNode holds the childs in the ResourceTree. 
 * All updates and node manipulations hould go through the ResourceTreeModel 
 */
public class ResourceTreeNode implements TreeNode
{
    private ViewNode viewNode;  
    private ResourceTreeNode parent=null;
	private boolean isRoot=false;
	private boolean isPopulated=false;
    private Vector<ResourceTreeNode> childs=new Vector<ResourceTreeNode>();
    private boolean hasFocus;   
	
    
    public ResourceTreeNode(ResourceTreeNode parent,ViewNode item,boolean isRoot)
    {
        this.viewNode=item;
        this.parent=parent; 
        this.isRoot=isRoot; 
    }

    public ResourceTreeNode(ResourceTreeNode parent,ViewNode item)
    {
        if (parent==null)
            throw new NullPointerException("NULL parent not allowed for non root node.");

        this.viewNode=item;
        this.parent=parent; 
        this.isRoot=false; 
        
    }

    @Override
    public ResourceTreeNode getChildAt(int childIndex)
    {
        return childs.get(childIndex);  
    }

    public boolean isRoot()
    {
    	return isRoot;  
    }
    
    /** Creates new TreePath to this node */ 
	public TreePath getTreePath()
	{
		return new TreePath(this.getPath()); 
	}
	
	 public ResourceTreeNode[] getPath()
	 {
		 Vector<ResourceTreeNode> paths=new Vector<ResourceTreeNode>(); 
	        
		 ResourceTreeNode current=this; 
		 while(current!=null)
		 {
			 paths.add(current);  
	            
			 if (current.isRoot())
				 break;
	            
			 ResourceTreeNode prev=current; 
			 current=prev.getParent();
	            
			 if (current==null)
				 throw new NullPointerException("*** NULL Parent for non root node:"+this);
	            
			 // Grr.Arrgg.
			 if (current.equals(prev))
				 throw new Error("*** Parent points to itself:"+current);
		 }
		 
		 ResourceTreeNode _arr[]=new ResourceTreeNode[paths.size()];
		 int len=paths.size(); 
	        
		 //inverse path
		 for (int i=0;i<len;i++)
			 _arr[len-i-1]=paths.get(i); 
    	return _arr; 
	}
	
    @Override
    public int getChildCount()
    {
    	synchronized(this.childs)
    	{
    		return childs.size();  
    	}
    }
    
    @Override
    public ResourceTreeNode getParent()
    {
        return parent;   
    }

    @Override
    public int getIndex(TreeNode node)
    {
        return getIndex((ResourceTreeNode)node);   
    }
        
    public int getIndex(ResourceTreeNode node)
    {
    	synchronized(this.childs)
    	{
    		return this.childs.indexOf(node); 
    	}
    }

    @Override
    public boolean getAllowsChildren()
    {
    	// getAllowsChildren() means if this node can have childs at all... 
        return viewNode.isComposite(); 
    }

    @Override
    public boolean isLeaf()
    {
    	// isLeaf() => node has children.  
    	// Return false also, if node hasn't been populated but can have children!
    	// this will trigger the expansion "+" sign on the node ! 
    	if (this.isPopulated==false) 
    		return (viewNode.isComposite()==false);
    	else
    		return (this.hasChildren()==false);  
    }

    @Override
    public Enumeration<ResourceTreeNode> children()
    {
        return this.childs.elements(); 
    }

    public List<ResourceTreeNode> getChilds()
    {
        return this.childs; // vector implements List interface
    }
    
    public String getName()
    {
        return this.viewNode.getName(); 
    }

    public ViewNode getViewItem()
    {
        return viewNode; 
    }

	public boolean hasChildren()
	{
		return (this.childs.size()>0);
	}

	public boolean isPopulated() 
	{
		return this.isPopulated; 
	}

	/** Clears childs and set isPopulated to FALSE ! */ 
	protected void clear()
	{	
		this.childs.clear(); 
		this.isPopulated=false; 
	}

	public ResourceTreeNode getNode(VRI locator) 
	{
		// use hash ? 
		for (ResourceTreeNode node:this.childs)
			if (node.getVRI().equals(locator)) 
					return node;
		
		return null; 
	}

	public VRI getVRI() 
	{
		return this.viewNode.getVRI(); 
	}

	protected void setViewNode(ViewNode iconItem) 
	{
		this.viewNode=iconItem; 
	}

	protected void setPopulated(boolean val) 
	{
		this.isPopulated=val; 
	}

	/** Atomic add childe node. Returns index of new childs */ 
	protected int addNode(ResourceTreeNode rtnode) 
	{
		synchronized(this.childs)
		{
			int index=this.childs.size(); 
			this.childs.add(rtnode);
			return index; 
		}
	}

	public String toString()
	{
		return "{<ResourceTreeNode>:"+getVRI().toString()+"}"; 
	}

    public void setHasFocus(boolean value)
    {
        this.hasFocus=value; 
    }

    public boolean hasFocus()
    {
        return this.hasFocus; 
    }

    /** Atomic remove child. Returns index of deleted child */
    protected int removeChild(ResourceTreeNode node)
    {
    	synchronized(this.childs)
    	{
    		int index=this.childs.indexOf(node); 
    		if (index>=0) 
    			this.childs.remove(index);
    		
    		return index; 
    	}
    }
}