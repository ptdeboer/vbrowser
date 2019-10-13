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

package nl.esciencecenter.vlet.vrs.vrms;

import java.util.Vector;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.ResourceTypeMismatchException;
import nl.esciencecenter.vlet.vrs.VCompositeNode;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;


/**
 * Logical Folder node. Super class for ResourceFolder(s); 
 * Contains thread save folder which holds <T extends VNode> Tree structure in memory. 
 * 
 * @author Piter T. de Boer 
 */
public abstract class LogicalFolderNode<T extends VNode> extends VCompositeNode implements VLogicalFolder
{
	 /** Thread save Vector -> use as mutex during manipulation */  
	protected Vector<T> childNodes=new Vector<T>(); 
	
	protected LogicalFolderNode<? extends VNode> logicalParent;

    private boolean isEditable=true; 
    
    protected AttributeSet attributes=new AttributeSet(); 

	public LogicalFolderNode(VRSContext context, VRL vrl) 
	{
		super(context, vrl);
	}

	public LogicalFolderNode(VRSContext context) 
	{
		super(context, null);
	}
	
	public String[] getAttributeNames()
	{
	    StringList list=new StringList(super.getAttributeNames()); 
	    list.merge(attributes.createKeyStringList());
	    // No MimeTypes for folders!
	    list.remove(VAttributeConstants.ATTR_MIMETYPE); 
	    return list.toArray(); 
	}
	
	@Override
    public String getIconURL()
    {
        return this.attributes.getStringValue(VAttributeConstants.ATTR_ICONURL); 
    }
    
    public void setIconURL(String iconUrl)
    {
        this.attributes.set(VAttributeConstants.ATTR_ICONURL,iconUrl);  
    }
	
	public Attribute getAttribute(String name) throws VrsException
	{
	    Attribute attr=this.attributes.get(name);
	    
	    if (attr!=null)
	    {
	        attr.setEditable(this.isEditable); 
	        return attr;
	    }
	    
	    return super.getAttribute(name); 
	}
	
	
	public boolean setAttribute(Attribute attr) throws VrsException
    {
	    return setAttribute(attr,false); 
    }
	
	/**
	 * Set attribute if the attribute is already in the AttributeSet. 
	 * If setIfNoteSet==true the value will always be updated. 
	 */
	public boolean setAttribute(Attribute attr, boolean setIfNotSet) throws VrsException
	{
	    // only set if already specified in attributes; 
	    if ((setIfNotSet==true) || (this.attributes.containsKey(attr.getName())))
	    {
	        this.attributes.put(attr);
	        return true; 
	    }
	    
	    return false; 
	    // super.setAttribute(attr); 
    }
	
	// @Override
    public boolean setAttributes(Attribute[] attrs) throws VrsException
    {
        boolean all=true; 
        for (Attribute attr:attrs)
        {
            boolean result=this.setAttribute(attr);
            all=all&&result; 
        }
        
        return all;  
    }
    
    public void setEditable(boolean val)
    {
        this.isEditable=val; 
    }
    
    public boolean isEditable()
    {
        return this.isEditable;  
    }
    
	public void setLogicalLocation(VRL newRef) throws VrsException
	{
		this.setLocation(newRef); 
	}

	@SuppressWarnings("unchecked")
	public void setLogicalParent(VNode node)
			throws ResourceTypeMismatchException
	{
		if (node instanceof LogicalFolderNode)
			throw new ResourceTypeMismatchException("LogicalNode can only be stored in logical Folders"); 
		
		this.logicalParent=(LogicalFolderNode<VNode>)node; 
	}
	
	 
	public VNode findNode(VRL location,boolean recurse) throws VrsException
	{
	    //debug("Find subnode:"+this+" checking: "+location);
		
		VNode[] nodes = this.getNodes(); 
		
		for (VNode node:nodes)
		{
			if (node.getLocation().compareTo(location)==0)
			{
				//debug("Find subnode: Found:"+location); 
				return node;
			}
			else if (recurse && node.getLocation().isParentOf(location))
			{
				  // go into recursion: 
                if (node instanceof VLogicalFolder)
                {
                    return ((VLogicalFolder)node).findNode(location,true); 
                }
                else
                {
                	// recurse myself ?
                }
                
			}
		}
		return null;
	}
	
	public boolean unlinkNodes(VNode nodes[])
	{
		boolean result=true; 
		
		for (VNode node:nodes)
		{
			boolean val=unlinkNode(node);
			result=result&&val;
		}
		
		return result; 
	}
	 
	public VNode[] getNodes() throws VrsException 
	{
	    return _getNodes();
	}
	
	protected VNode[] _getNodes() throws VrsException
	{
		if ((childNodes==null) || (childNodes.size()<=0))
			return null; 
		
		synchronized(childNodes)
		{
			VNode arr[]=new VNode[childNodes.size()]; 
			//todo cannot convert to T[] array 
			return childNodes.toArray(arr); 
		}
	}
	
	public T[] toArray(T array[])
    {
		if(this.childNodes==null)
			return null;
	
		return this.childNodes.toArray(array); 
	}
	
	/**
	 * Deletes node from current resource group. 
	 * The behaviour is similar to unlink(). 
	 */
	public boolean delNode(VNode node)
	{
		return unlinkNode(node);
	}

	public boolean unlinkNode(VNode node)
	{
		return delSubNode(node);
	}
	
	/** Protected implementation which deletes the specified node from the internal vector */ 
	protected boolean delSubNode(VNode node)
	{
		if (node==null)
			return false; 
		
		boolean hasNode=false; 
		
		synchronized(childNodes)
		{
			hasNode=childNodes.remove(node);
			
		    // check by VRL: equals() method might not return true in all cases!  
			T other=this.getSubNode(node.getVRL()); 
			if (other!=null)
			{
				childNodes.remove(other);
				hasNode=true; 
			}
		}
		
		save();
		
		return hasNode; 
	}
	
	/**
	 * Protected implementation which added the specified node from the internal vector. 
	 * If the exact node (equals()==true) already exists nothing changes. 
	 * If a VNode with the SAME VRL already exists, it will be removed!
	 * @throws VrsException 
	 * 
	 */ 
    protected void addSubNode(T node) throws VrsException
    {
        if (node==null)
            return; 
        
        boolean hasNode=false; 
        
        synchronized(childNodes)
        {
            // A Vector DOES allow duplicates, but we all node shoulds be unique:
            if (childNodes.contains(node))
            {
                //childNodes.remove(node);
                return; 
            }
            
            // check by VRL: equals() method might not return true in all cases!  
            VNode other=this.getSubNode(node.getVRL()); 
            if (other!=null)
            {
                childNodes.remove(other);
                hasNode=true; 
            }
            
            this.childNodes.add(node); 
        }
        // for persistant nodes: 
        save();
        // return hasNode; 
    }
	
	/** Check internal child node array */ 
	protected T getSubNode(VRL vrl)
	{
		synchronized(this.childNodes)
		{
			for (T child:childNodes)
			{
				if (child.getVRL().equals(vrl))
					return child; 
			}
		}
		
		return null; 
	}

//	public VNode getNode(VRL vrl) throws VlException
//	{
//		// use array to loop over 
//		for (VNode child:getNodes())
//		{
//			if (child.getVRL().compareTo(vrl)==0) 
//				return child; 
//		}
//		return null; 
//	}
	
	/**
	 * Store childs in internal Vector. Current Childs vector is cleared then all
	 * nodes are added. 
	 */
	protected void setChilds(T nodes[])
	{
		synchronized(childNodes)
		{
			this.childNodes.clear(); 
			for (T node:nodes)
			{
				this.childNodes.add(node); 
			}
		}
	}
	
	/** Clear internal childs vector. */ 
	protected void clearChilds() 
	{
		synchronized(childNodes)
		{
			this.childNodes.clear(); 
		}
	}
	
	// =======================================================================
	// Abstract interface 
	// =======================================================================
	
	public boolean save() {return false;} // by default not persistant! 
	
	public VRL getStorageLocation() throws VrsException {return null;}  

	// Minimum of Methods ? 
	abstract public String getResourceType();  

	abstract public String[] getResourceTypes(); 	
}
