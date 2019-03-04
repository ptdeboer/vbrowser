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

package nl.esciencecenter.vlet.vrs;

import nl.esciencecenter.ptk.data.IntegerHolder;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.error.ParameterError;
import nl.esciencecenter.vlet.exception.NotImplementedException;
import nl.esciencecenter.vlet.vrs.events.ResourceEvent;

/**
 * Super Class of all "Composite" Nodes. Extends VNode class by adding 
 * VComposite methods. 
 * 
 * @author P.T. de Boer
 */
public abstract class VCompositeNode extends VNode implements VComposite// ,VCompositeDeletable
{
    public VCompositeNode(VRSContext context, VRL vrl)
	{
		super(context, vrl);
	}

    @Override
    public VNode[] getParents() throws VrsException
    {
        VNode parents[]=new VNode[1]; 
        parents[1]=getParent();  
        return parents; 
    }

    public long getNrOfNodes() throws VrsException
    {
        VNode nodes[] = getNodes();

        if (nodes != null)
            return nodes.length;

        return 0;
    }

    public VNode getNode(String name) throws VrsException
    {
        if (name == null)
            return null;

        VNode nodes[] = getNodes();

        if (nodes == null)
            return null;

        for (VNode node : nodes)
            if (name.compareTo(node.getName()) == 0)
                return node;

        return null;
    }
    
    public VNode addNode(VNode node) throws VrsException
    {
        return addNode(node, null, false);
    }
    
    public VNode addNode(VNode node, boolean isMove) throws VrsException
    {
        return addNode(node, null, isMove);
    }

    public VNode[] addNodes(VNode[] nodes, boolean isMove) throws VrsException
    {
        if (nodes == null)
            return null;

        VNode[] newNodes = new VNode[nodes.length];

        for (int i = 0; i < nodes.length; i++)
        {
            newNodes[i] = addNode(nodes[i], isMove);
        }

        return newNodes;

    }

    public boolean delNode(VNode node) throws VrsException
    {
        if (node instanceof VDeletable)
            return ((VDeletable) node).delete();
        else
            throw new NotImplementedException("Resource cannot be deleted:"
                    + node);

    }

    public boolean delNodes(VNode[] nodes) throws VrsException
    {
        boolean status = true;

        for (VNode node : nodes)
            status &= delNode(node);

        return status;
    }

    public boolean hasNode(String name) throws VrsException
    {
        if (getNode(name) != null)
            return true;

        return false;
    }

    public Attribute[][] getNodeAttributes(String childNames[],
            String names[]) throws VrsException
    {
        VNode nodes[] = new VNode[childNames.length];

        Attribute attrs[][] = new Attribute[nodes.length][];

        for (int i = 0; i < childNames.length; i++)
        {
            nodes[i] = getNode(childNames[i]);

            if (nodes[i] != null)
                attrs[i] = nodes[i].getAttributes(names);
            else
                attrs[i] = null;
        }

        return attrs;
    }

    public Attribute[][] getNodeAttributes(String names[]) throws VrsException
    {
        VNode nodes[] = getNodes();

        Attribute attrs[][] = new Attribute[nodes.length][];

        for (int i = 0; i < nodes.length; i++)
        {
            if (nodes[i] != null)
                attrs[i] = nodes[i].getAttributes(names);
            else
                attrs[i] = null;
        }

        return attrs;
    }

    public boolean isDeletable() throws VrsException
    {
        return true;
    }
    
    public boolean delete() throws VrsException
    {
        return delete(false); 
    }

    //@Override
    public boolean isAccessable() throws VrsException
    {
        return true;
    }
    
    public VNode createNode(String type, String name, boolean force)
            throws VrsException
    {
        throw new NotImplementedException("Cannot create child type:" + type);
    }

    public boolean delete(boolean recurse) throws VrsException
    {
        throw new NotImplementedException("Delete not implemented for Resource:"+this); 
    }
    
    public VNode addNode(VNode node, String newName, boolean isMove)
            throws VrsException
    {
        throw new NotImplementedException("Not Implemented. Can not add node:"+node); 
    }

    /**
     * Returns subSet of VNode array starting from offset upto offset+maxNodes. 
     * Total length of returned array might be smaller or equal to maxNodes; 
     * Override this method for optimized contents query. 
     * <p> 
     * Resource which have child nodes > 1000 should override this method !  
     *
     * @param offset         Starting offset
     * @param maxNodes       Maximum length of returned array. Actual size might be smaller  
     * @param totalNumNodes  Size of total contents. 
     * @return subSet of VNode[] array. 
     */
    
    public VNode[] getNodes(int offset,int maxNodes,IntegerHolder totalNumNodes) throws VrsException /// Tree,Graph, Composite etc.
    {
    	VNode allNodes[]=getNodes(); 
    	return nodesSubSet(allNodes,offset,maxNodes,totalNumNodes); 
    }
        
    /**
     * Returns subSet of VNode[] array starting from offset to offset+maxNodes. 
     * Total length of returned array is smaller or equal to maxNodes; 
     * 
     * @param nodes          Full VNode array
     * @param offset         Starting offset
     * @param maxNodes       Maximum length of returned array. If -1 = return ALL !   
     * @param totalNumNodes  Size of original VNode array 
     * @return
     */
    public static VNode[] nodesSubSet(VNode nodes[],int offset,int maxNodes,IntegerHolder totalNumNodes)
    {
    	// NiNo: 
    	if (nodes==null)
    		return null; 

    	if (offset<0) 
    		throw new ParameterError("Offset can not be negative:"+offset); 
    	
    	// original length 
    	int len=nodes.length; 
    	
    	// if maxNodes==-1 return ALL in array ! 
    	if (maxNodes<0)
    	   maxNodes=len; 
    	
    	// set VAR totalNumNodes: 
    	if (totalNumNodes!=null)
    		totalNumNodes.value=len;
    	
    	int start=offset;         // start in source array
    	int end=offset+maxNodes;  // end in source array
    	
    	// start beyond real length: 
    	if (start>=len)
    		return null; 
    
    	// truncate end; 
    	if (end>=len) 
    		end=len; 
    	
    	// start beyond truncated end 
    	if (start>=end) 
    		return null;
    	
    	// realsize 
    	int size=end-start; 
    	
    	//
    	// Dynamic Array allocation :
    	// Allocate new Array with most Specific Class Type
    	// to allow for downcasting to original (subclass) VNode type !
    	// 
    	VNode subNodes[] = (VNode[]) java.lang.reflect.Array.newInstance(
						nodes[0].getClass(), size);
    	
    	for (int i=0;i<size;i++)
    	{
    		subNodes[i]=nodes[offset+i]; 
    	}
    	
    	return subNodes; 
    }
    
    protected void fireChildAdded(VRL vrl)
    {
        this.vrsContext.fireEvent(ResourceEvent.createChildAddedEvent(this.getVRL(),vrl)); 
    }
    
    protected void fireSetChilds(VRL vrls[])
    {
        this.vrsContext.fireEvent(ResourceEvent.createSetChildsEvent(this.getVRL(),vrls)); 
    }
    
    protected void fireChildDeleted(VRL vrl)
    {
        this.vrsContext.fireEvent(ResourceEvent.createDeletedEvent(vrl)); 
    }
    
    protected void fireRefresh()
    {
        this.vrsContext.fireEvent(ResourceEvent.createRefreshEvent(this.getVRL())); 
    }
    

}
