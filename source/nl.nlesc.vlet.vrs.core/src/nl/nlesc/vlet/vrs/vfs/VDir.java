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

package nl.nlesc.vlet.vrs.vfs;


import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.Vector;
import java.util.regex.Pattern;

import nl.esciencecenter.ptk.data.IntegerHolder;
import nl.nlesc.vlet.exception.ResourceTypeNotSupportedException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.NodeFilter;
import nl.nlesc.vlet.vrs.VComposite;
import nl.nlesc.vlet.vrs.VCompositeDeletable;
import nl.nlesc.vlet.vrs.VCompositeNode;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRenamable;
import nl.nlesc.vlet.vrs.io.VStreamReadable;
import nl.nlesc.vlet.vrs.util.VRSSort;

/**
 * Super class of the VFS Directory implementation. 
 * Represents an abstract interface to a Directory implementation. 
 *  
 * @see VFSNode
 * @see VFile 
 * @see VDir
 * @see VFSClient 
 * @author P.T. de Boer
 */
public abstract class VDir extends VFSNode implements VComposite,VRenamable,
      VCompositeDeletable
{
    protected static Random dirRandomizer=new Random();
    
    static
    {
        dirRandomizer.setSeed(System.currentTimeMillis());
    }
        
    protected static String[] childTypes={VFS.FILE_TYPE,VFS.DIR_TYPE};
    
    public static VFSNode[] sortVNodes(VFSNode[] nodes,boolean typeFirst,boolean ignoreCase)
    {
        VRSSort.sortVNodesByTypeName(nodes,typeFirst,ignoreCase);
        return nodes; 
    }
    
    /** Class method to filter out childs of type VNode */ 
    public static VNode[] applyFilter(VNode[] nodes, NodeFilter filter)
    {
        if (nodes==null)
            return null;
        
        if (filter==null)
            return nodes; 
        
        Vector<VNode>filtered=new Vector<VNode>();
        
        for (VNode node:nodes) 
            if (filter.accept(node))
                filtered.add(node); 

        VNode _nodes[]=new VNode[filtered.size()];
        _nodes=filtered.toArray(_nodes); 
        return _nodes; 
    }
    
    //  ==========================================================================
    //  Instance
    //  ==========================================================================
     
    public VDir(VFileSystem vfsSystem, VRL vrl)
    {
        super(vfsSystem, vrl);
    }
    
    @Override
    public String getResourceType()
    {
        return VFS.DIR_TYPE;
    }
    
    /**
     * By default directories do not have a mimetype and this method will return null. 
     */
    @Override
    public String getMimeType()
    {
        return null; 
    }
    
    /**
     * Returns allowed child types for VDir.<br>
     * <br>
     * The default types for VDir are 'File' and 'Dir' type<br>
     */
    public String[] getResourceTypes()
    {
        return childTypes; 
    }
    
    /**
     * For unix fileystem this means the 'x' bit should be enabled. 
     */
    public boolean isAccessable() throws VlException
    {
         return isReadable();
    }
       
    /** 
     * The length() attribute for directories is system depended and really 
     * not usuable in a Virtual environment. 
     * This method will return -1 if not implemented by the File System 
     * On unix filesystems this method provides the size of the directory
     * object needed to store the file information.
     */
    public long getLength() throws VlException 
    {
        return -1; 
    }
    
    //  ==========================================================================
    //  VComposite Interface 
    //  ==========================================================================
    
    /** 
     * Add (VFS)Node to this directory location. 
     * Node must be of type VFSNode since this method calls addNode(VFSNode...) 
     */  
    public VFSNode addNode(VNode node,boolean isMove) throws VlException
    {
        return addNode(node,(String)null,isMove); 
    }
    
    public VFSNode addNode(VNode node,String optNewName,boolean isMove) throws VlException
    {
        if (node instanceof VFSNode) 
        {
            return addNode((VFSNode)node,optNewName,isMove);
        }
        else if (node instanceof VStreamReadable)
        {
            return putAnyNode(node,optNewName,isMove); 
        }
        else
        {
            throw new ResourceTypeNotSupportedException("Type of Resource not supported:"+node);
        }
    }
    
    /** 
     * Create new VFile and copy contents from (VStreamReadable) vnode.
     * Tries to smartly create a new file based on the source Node. 
     * <br>
     * Custom method currently used by the VBrowser to 'drop' just any kind of node
     * into a Directory. 
     *  
     */ 
    public VFile putAnyNode(VNode sourceNode, String optNewName, boolean isMove) throws VlException
    {
        return getTransferManager().putAnyNode(this,sourceNode,optNewName,isMove); 
    }

    public VFSNode addNode(VFSNode node,String optNewName,boolean isMove) throws VlException
    {
        return getTransferManager().doCopyMove(node,this,optNewName,isMove);
    }
    
    /** 
     * Add multiple (VFS)Nodes to this directory location. 
     * Nodes must be of type VFSNode since this method calls addNodes(VFSNode[]...) 
     */  
    public VNode[] addNodes(VNode[] nodes,boolean isMove) throws VlException
    {
        if (nodes==null)
            return null;
        
        // downcast to VFSNodes. Currenlty Only VFSNodes are supported.
        
        if (nodes instanceof VFSNode[])
        {
            return addNodes((VFSNode[])nodes,isMove);
        }
        else
        {
            // just try to add them all 
            VNode[] results=new VNode[nodes.length];
              
            for (int i=0; i<nodes.length; i++)
            {
                 results[i]=addNode(((VFSNode)nodes[i]),null,isMove); 
            }
              
            return results; 
        }
    }

    /** Delete node. Node must be of type VFSNode */ 
    public boolean delNode(VNode childNode) throws VlException
    {
        if (childNode instanceof VFSNode)
            return ((VFSNode)childNode).delete();
        else
        {
            throw new ResourceTypeNotSupportedException("Type of Resource not supported:"+childNode);
        }
    }

    /** Delete nodes. Nodes must be of type VFSNode */ 
    public boolean delNodes(VNode[] childNodes) throws VlException
    {
        boolean status=true; 
        
        for (int i=0;i<childNodes.length;i++)
        {
            if (childNodes[i] instanceof VFSNode)
                status&= ((VFSNode)childNodes[i]).delete();
            else
            {
                throw new ResourceTypeNotSupportedException("Type of Resource not supported:"+childNodes[i]);
            }
        }
        
        return status; 
    }
 
    public boolean hasNode(String name) throws VlException
    {
        // todo: more efficient method 
        if (existsFile(resolvePathString(name))==true)
            return true; 
        if (existsDir(resolvePathString(name))==true)
            return true; 

        return false; 
    }
    
    public VFSNode getNode(String path) throws VlException
    {
        return vfsSystem.openLocation(resolvePath(path)); 
    }
    
    /** 
     * VDir implements createChild by calling createFile or createDir,
     * depending on the type.  
     */ 
    public VFSNode createNode(String type,String name, boolean ignoreExisting) throws VlException
    {
        if (type==null)
        {
            throw new ResourceTypeNotSupportedException("Cannot create child type: NULL");
        }
        
        if (type.equalsIgnoreCase(VFS.FILE_TYPE))
        {
            return createFile(name,ignoreExisting);
        }
        else if (type.equalsIgnoreCase(VFS.DIR_TYPE))
        {
            return createDir(name,ignoreExisting);
        }
        else
        {
            throw new ResourceTypeNotSupportedException("Cannot create child type:"+type);
        }
    }
    
    /** 
     * Default implementation calls the VDir method list() 
     */ 
    public VFSNode[] getNodes() throws VlException
    {
        return list();
    }
    
    public VNode[] getNodes(int offset,int maxNodes,IntegerHolder totalNumNodes) throws VlException /// Tree,Graph, Composite etc.
    {
        return list(offset,maxNodes,totalNumNodes); 
    }
    
    //  ==========================================================================
    //  VFSNode interface 
    //  ==========================================================================
        
    /** return true if the VFSNode is a (V)File */
    public boolean isFile()
    {
        return false;
    };
    
    /** return true if the VFSNode is a (V)Directory */ 
    public boolean isDir()
    {
        return true;
    };
       
    /**
     * Returns new VFile object. Path may or may not exist. 
     * This is not checked. 
     * Call VFileSystem.newFile(); 
     * @param path absolute or relative path for new VFile object 
     * @return new VFile object.
     * @throws VlException 
     * @see VFileSystem#newFile(VRL); 
     */
    public VFile newFile(String path) throws VlException
    {
        return getFileSystem().newFile(resolvePath(path)); 
    }
    
    /**
     * Returns new VDir object. Path may or may not exist. 
     * This is not checked. 
     * Call VFileSystem.newDir(); 
     * @param path absolute or relative path for new VDir object 
     * @return new VDir object.
     * @throws VlException 
     * @see VFileSystem#newFile(VRL); 
     */
    public VDir newDir(String path) throws VlException
    {
        return getFileSystem().newDir(resolvePath(path)); 
    }
    
    /**
     * Create new file in this directory. After creation the file 
     * will exist on this filesystem.
     * To get a new VFile object: use newFile()
     */  
    public VFile createFile(String name) throws VlException
    {
        return createFile(resolvePathString(name),true);   
    }
    
    /**
     * Create sub directory in this directory or use full path 
     * to create a new directory.  
     * @throws VlException
     */
    public VDir createDir(String dirName) throws VlException
    {
    	VDir dir=getFileSystem().newDir(resolvePath(dirName)); 
    	dir.create(true);
    	return dir; 
    }
     
    /**
     * Copy to specified parent directory location.  
     * @throws VlException 
     */
    public final VDir copyTo(VDir parentDir) throws VlException
    {
        return (VDir)getTransferManager().doCopyMove(this,parentDir,null,false); 
    }
    
    /**
     * Copy to specified parent directory. 
     * @param destinationDir new parent directory. New Directory 
     *        will be created as subdirectory of this parent. 
     * @param optNewName optional newname. If null basenames 
     *        of source directory will be used.   
     * @throws VlException 
     */
    public final VDir copyTo(VDir parentDir,String newName) throws VlException
    {
        return (VDir)getTransferManager().doCopyMove(this,parentDir,newName,false); 
    }
    
    /**
     * Move to specified parent directory.
     * @see #moveTo(VDir, String); 
     */
    public final VDir moveTo(VDir parentDir) throws VlException
    {
        return (VDir)getTransferManager().doCopyMove(this,parentDir,null,true); 
    }
    
    /**
     * Move to specified VDir location.
     * @param parentDir - new parent directory. New Directory 
     *        will be created as sub-directory of this parent. 
     * @param optNewName - optional newname. If null basename 
     *        of source directory will be used.   
     */
    public final VDir moveTo(VDir parentDir,String optNewName) throws VlException
    {
        return (VDir)getTransferManager().doCopyMove(this,parentDir,null,true); 
    }
    
    /**
     * Non-recursive Delete.<br>
     * Calles recursive delete from VComposite with resurse=false.  
     */
    public boolean delete() throws VlException
    {
        return delete(false);
    }
    
    /** Deletes file */ 
    public boolean deleteFile(String name) throws VlException
    {
        return getFile(name).delete(); 
    }
    
    /** Deleted (sub)directory */ 
    public boolean deleteDir(String name,boolean recursive) throws VlException
    {
        return getDir(name).delete(recursive); 
    }
    
    /**
     * Get existing subdirectory or if dirname is absolute get the directory using
     * the absolute path.
     */  
    public VDir getDir(String dirname) throws VlException
    {
        return vfsSystem.getDir(resolvePath(dirname)); 
    }

    /** Get exsiting file in this directory using the relative or absolute path. */   
    public VFile getFile(String filename) throws VlException
    {
        return vfsSystem.getFile(resolvePath(filename)); 
    }
    
    //  ==========================================================================
    //  VDir.list(...)
    //  ==========================================================================
    
    /**
     * List the chidren and sort them. 
     * @param typeFirst  if true return directories first, then files. 
     * @param ignoreCase ignore case when sorting 
     * @return Sorted VFSNode[] array
     * @throws VlException 
     */
    public VFSNode[] listSorted(boolean typeFirst,boolean ignoreCase) throws VlException
    {
        return sortVNodes(list(),typeFirst,ignoreCase); 
    }
    
    /**
     * Returns filtered childs with specified wildcard pattern.
     * <p>
     * This method calls {@link #list(NodeFilter, int, int, IntegerHolder)}. 
     * See that method for details.
     */ 
    public VFSNode[] list(String pattern) throws VlException
    {
        return list(new NodeFilter(pattern,false),0,-1,null); 
    }
    
    /**
     * Returns filtered childs with specified wildcard pattern or
     * Regular Expression.  
     * <p>
     * This method calls {@link #list(NodeFilter, int, int, IntegerHolder)}. 
     * See that method for details.
     */ 
    public VFSNode[] list(String pattern,boolean isRegularExpression) throws VlException
    {
         return list(new NodeFilter(pattern,isRegularExpression),0,-1,null); 
    }
   
    /**
     * This method calls {@link #list(NodeFilter, int, int, IntegerHolder)}. 
     * See that method for details.
     */
    public VFSNode[] list(Pattern pattern,int offset,int maxNodes,IntegerHolder totalNumNodes) throws VlException
    {
        return list(new NodeFilter(pattern),offset,maxNodes,totalNumNodes); 
    }
    
    /**
     * This method calls {@link #list(NodeFilter, int, int, IntegerHolder)}. 
     * See that method for details.
     */ 
    public VFSNode[] list(int offset,int maxNodes,IntegerHolder totalNumNodes) throws VlException /// Tree,Graph, Composite etc.
    {
        return list((NodeFilter)null,offset,maxNodes,totalNumNodes); 
    }
    
    /**
     * Returns subset of list() starting at offset with a maximum of maxNodes. 
     * Implement this method to allow for really long listings and optimized filtering !
     *  
     * @param filter          NodeFilter  
     * @param offset          Starting offset
     * @param maxNodes        Maximum size of returned node array
     * @param totalNumNodes   Total number of nodes returned by list(). Value of -1 means not known or not supported!  
     * @return                Subset of list(). 
     * @throws VlException
     */
    public VFSNode[] list(NodeFilter filter,int offset,int maxNodes,IntegerHolder totalNumNodes) throws VlException
    {
        // use default list() ! 
        VFSNode nodes[] =list();

        //Global.debugPrintf(this,"listFiltered(): nr of UNfiltered nodes=%d\n",nodes.length);
        if (filter!=null)
        {
            nodes=NodeFilter.filterNodes(nodes,filter); 
        }
            
        //Global.debugPrintf(this,"listFiltered(): nr of filtered nodes=%d\n",nodes.length); 
        if (totalNumNodes==null)
            totalNumNodes=new IntegerHolder(); 

        // method nodesSubSet allocated correct (VFSNodes[]) Array Type: 
        return (VFSNode[])VCompositeNode.nodesSubSet(nodes,offset,maxNodes,totalNumNodes); 
    }

    // ========================================================================
    // IO
    // ========================================================================
    
    public VDir createUniqueDir(String prefix, String postfix) throws VlException
    {
        if (prefix==null) 
            prefix="";
        
        if (postfix==null) 
            postfix="";
        
        String dirName=null;
        
        // 
        // synchronize: Race Condition if two thread execute the following
        // code during the SAME system milli second ! 
        // 
        synchronized (this)
        {
            do
            {
                String randStr=""+dirRandomizer.nextLong(); 
                dirName= prefix+ randStr+postfix;
            }
            
            while (existsDir(dirName)==true);
            
            return createDir(dirName);
        }
    }
    
    /**
     * Create new file and return OutputStream to write to.  
     * Call: createNewFileOutputStream()
     */
    public OutputStream putFile(String fileName) throws VlException 
    {
        return createFileOutputStream(fileName,true); 
    } 
    
    public OutputStream putFile(String fileName,boolean force) throws VlException 
    {
        return createFileOutputStream(fileName,force); 
    } 
    
    /**
     * Create new File object and return outputstream to write to. 
     * 
     * @param  fileName relative or absolute path to new file.   
     * @param  force overwrite existing or create new file if it doesn't exists. 
     * @return  OutputStream to the new VFile.  
     * @throws VlException
     */
    public OutputStream createFileOutputStream(String fileName, boolean force) throws VlException
    {
        try
        {
            VFile file=getFileSystem().newFile(resolvePath(fileName));
            return file.getOutputStream(); 
        }
        catch (IOException e)
        {
            throw new VlIOException(e); 
        }
    }

    /**
     * Create new directory or subdirectory.  
     * 
     * @param name filename to create. If the path is absolute (starting with '/') 
     *         the full path is be used to create the new directory.
     * @param ignoreExisting if ignoreExisting==false this methods will throw an Exception when a file 
     *         already exists.<br>
     *         If ignoreExisting==true, ignore existing file. 
     *        
     * @return new created or existing VDir 
     * 
     * @throws VlException
     */
    public VDir createDir(String name, boolean ignoreExisting)
            throws VlException
    {
         VDir dir=getFileSystem().newDir(resolvePath(name));
         dir.create(ignoreExisting);
         return dir;
    }
    
    /**
     * Create file in this directory or create the full (absolute) path if fileName 
     * is an absolute path. 
     * 
     * @param fileName the filename to create. If the path is absolute (starting with '/') 
     *                 the full path is be used to create the new file.
     * @param ignoreExisting if ignoreExisting==false this methods will throw an Exception when a directory 
     *        already exists.<br>
     *        If ignoreExisting==true, ignore existing file. 
     */
    public VFile createFile(String fileName, boolean ignoreExisting) throws VlException
    {
    	VFile file=getFileSystem().newFile(resolvePath(fileName));
    	file.create(ignoreExisting);
    	return file; 
    }
    
    // ========================================================================
    //
    // ========================================================================
    
    /** 
     * Returns true whether (child) filename exists and is a VFile.
     * Parameter fileName can be an absolute path or relative path starting from this 
     * directory.   
     */ 
    public boolean existsFile(String fileName) throws VlException
    {
        return getFileSystem().newFile(resolvePath(fileName)).exists();  
    }
    
    /**
     * Returns true whether (child) directory exists and is a VDir.
     * Parameter dirName can be an absolute path or relative path starting from this 
     * directory.   
     */
    public boolean existsDir(String dirName) throws VlException
    {
        return getFileSystem().newDir(resolvePath(dirName)).exists(); 
    }

    // ========================================================================
    // Abstract Interface Methods 
    // ========================================================================
         
    /**
     * Return listed contents of Directory.
     * <p> 
     * For large Directories and for optimized filtering it is recommended that
     * the method {@link #list(NodeFilter, int, int, IntegerHolder)} is also
     * overriden. 
     * 
     * @return array of VFSNodes using default filtering.  
     * @throws VlException
     */
    public abstract VFSNode[] list() throws VlException;
 
}

