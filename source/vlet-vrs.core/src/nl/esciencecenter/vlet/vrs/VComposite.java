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
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

/**
 * The Composite interface for VNodes which have 'child nodes' 
 * (for example: VDir). 
 * <p>
 * The method VNode.isComposite() can be used to check whether a node is composite 
 * (it can have child nodes) and implements this interface.  
 * <p>
 * @author P.T. de Boer 
 */
public interface VComposite 
{
    /** 
     * Returns allowed resource types which this node can 
     * have as child and/or create.
     * Only if a Resource is Composite and the source type is in this list the node can be 
     * added to this resource using addNode().
     * <p>  
     * <strong>VBrowser notes:</strong><br>
     * This method is also used by the VBrowser to check for valid 'drop' targets.
     * Types "File" and "Directory"  can be dropped on "Directory" types since these are the
     * valid ResourceTypes.
     * @see nl.esciencecenter.vlet.vrs.data.VAttributeConstants#ATTR_RESOURCE_TYPES   
     */ 
    public String[] getResourceTypes();
    
    /** 
     * Returns number of child nodes.  
     * @throws VrsException */ 
    public long getNrOfNodes() throws VrsException; /// Tree,Graph, Composite etc.

    /**
     * Returns Child Nodes. 
     * <p>
     * <b>implementation note:</b><br>
     * For large Composite nodes, override method {@link #getNodes(int, int, IntegerHolder)} as well.  
     * @throws VrsException 
     */
    public VNode[] getNodes() throws VrsException; /// Tree,Graph, Composite etc.

    /**
     * Returns range of Child Nodes starting at offset upto maxNodes. 
     * The IntegerHolder totalNumNodes returns the total amount of 
     * nodes in this resource. 
     * Multiple getNodes(...) calls may occure. It is up to the resource 
     * to make sure the index of the each returned node matches 
     * the actual stored nodes.  
     *   
     * @throws VrsException */
    public VNode[] getNodes(int offset,int maxNodes,IntegerHolder totalNumNodes) throws VrsException; /// Tree,Graph, Composite etc.
    
    /**
     * Returns Child Node. 
     * @throws VrsException */
    public VNode getNode(String name) throws VrsException; /// Tree,Graph, Composite etc.
    
    /**
     * Add a node to the underlying Resource.
     * <p>
     * For optimization the isMove determines if it is a move, 
     * so the implementation can optimize local movements for example on the 
     * same filesystems or on the same SRB Server.  
     * 
     * @param node
     * @param isMove
     * @return new created VNode
     * @throws VrsException
     */
    public VNode addNode(VNode node,boolean isMove) throws VrsException;
    
    /**
     * Add a node to the underlying Resource with a optional new name. 
     * <p>
     * For optimization the isMove determines if it is a move, 
     * so the implementation can optimize local movements for example on the 
     * same filesystems or on the same SRB Server.  
     *  
     * @param node
     * @param isMove
     * @return new created VNode
     * @throws VrsException
     */ 
    public VNode addNode(VNode node,String newName,boolean isMove) throws VrsException;
    
    /**
     * Add specified nodes to the Resource. A drag and drop from the user interface
     * might call this method to add or 'drop' resource nodes to this composite
     * Node. 
     * <p>
     * For optimization the isMove determines if it is a move, 
     * so the implementation can optimize local movements for example on the 
     * same filesystems. 
     */    
    public VNode[] addNodes(VNode[] nodes,boolean isMove) throws VrsException;
    
    /** 
     * VRS method to delete specified resource Node.
     * The method delNode() unlinks the child node from this composite node. 
     * To avoid circular calling of child.delete() -> parent.delNode() and back, 
     * the recommended way is to delete the child node first by calling child.delete() 
     * and the child calls the parent: (this)getParent().delNode(this).
     */ 
    public boolean delNode(VNode node) throws VrsException;
    
    /** VRS method to delete specified resource Nodes */
    public boolean delNodes(VNode[] nodes) throws VrsException;

    // Child Methods     
    
    /**
     * VNode method to create new Child.
     * 
     * @param type must be on of the types getResourceTypes() returns.  
     * @param name may be null. The implementation might choose a default name 
     *        or prompt the user. 
     * @param force means to create the child even if it already exists. 
     * @throws VrsException  
     * */
    public VNode createNode(String type,String name,boolean force) throws VrsException;
     
    /** 
     * Recursive delete. If recurse==true, then delete all child nodes then delete itself. 
     * If is recurse==false delete this resource only if it has no child nodes. 
     *   
     * @param recurse whether to delete its children also. 
     * @return Returns true upon success.  
     * 
     * @throws VlException  
     */
    //public boolean delete(boolean recurse) throws VlException;

    //public boolean isDeletable() throws VlException;
    
    /** Checks whether this node has a child with the specified name */ 
    public boolean hasNode(String name) throws VrsException;
  
    /**
     * Override this method if your directory can be accessible
     * but not be 'readable'. 
     * For example a the contents of unix directory may not
     * be 'read' (list() will fails), but each individual 
     * file may be accessible (mode= --x). 
     *   
     * @throws VrsException 
     */
    public boolean isAccessable() throws VrsException;
   
    //public abstract VAttribute[][] getChildAttributes(VNode[] nodesSet, String[] attrNames); 
}
