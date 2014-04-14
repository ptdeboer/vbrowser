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

package nl.esciencecenter.vlet.vrs.vfs;

import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.*;

import java.io.IOException;
import java.util.Vector;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeUtil;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.io.VRandomAccessable;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.exception.NotImplementedException;
import nl.esciencecenter.vlet.exception.ResourceTypeMismatchException;
import nl.esciencecenter.vlet.vrs.VDeletable;
import nl.esciencecenter.vlet.vrs.VEditable;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRenamable;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;

/**
 * Super class of VDir and VFile. 
 * 
 * Represents shared methods for (Virtual) Directories and Files.
 * 
 * @see VFSNode
 * @see VFile 
 * @see VDir
 * @see VFSClient 
 * 
 * @author P.T. de Boer
 */
public abstract class VFSNode extends VNode implements VRenamable, VEditable, VDeletable,VACL
{
    // ========================================================================
    // Class Fields: list of default VFS attributes.
    // ========================================================================

    /** Default attributes names for all VFSNodes */

    public static final String[] attributeNames =
    { 
    	ATTR_RESOURCE_TYPE,
    	ATTR_NAME,
    	ATTR_SCHEME,
    	ATTR_HOSTNAME,
    	ATTR_PORT,
        ATTR_MIMETYPE, 
        ATTR_ISREADABLE, 
        ATTR_ISWRITABLE, 
        ATTR_ISHIDDEN,
        ATTR_ISFILE, 
        ATTR_ISDIR, 
        ATTR_NRCHILDS, 
        ATTR_FILE_SIZE,
        // minimal time wich must be supported
        ATTR_MODIFICATION_TIME,
        // stringifying is now done in GUI !  
        //ATTR_MODIFICATION_TIME_STRING, 
        ATTR_PARENT_DIRNAME,
        // not all implementation support the creation time attribute
        // ATTR_CREATION_TIME_STRING,
        // not all implementation support the OWNER attribute
        //ATTR_OWNER, 
        ATTR_ISSYMBOLIC_LINK,
        ATTR_PERMISSIONSTRING // implementation specific permissions string
    };

    public static final  String linkAttributeNames[]=
    {
            ATTR_SYMBOLICLINKTARGET
    };

    //private VFSTransfer transferInfo=null;



    /** Default buffer for streamCopy to use */ 
    public static int defaultStreamBufferSize = 2 * 1024 * 1024;

    // ========================================================================
    // Static helper methods 
    // ========================================================================

    public static VFSNode[] returnAsArray(Vector<VFSNode> nodes)
    {
        if (nodes==null)
            return null; 

        VFSNode array[]=new VFSNode[nodes.size()];
        array=nodes.toArray(array); 


        return array; 
    }

  

    // ========================================================================
    // Constructor
    // ========================================================================
    
    VFileSystem vfsSystem=null;

    public VFSNode(VFileSystem vfs, VRL vrl)
    {
        super(vfs.getVRSContext(), vrl);
        vfsSystem=vfs;
    }
    
    /** Returns File System this VFSNode belongs to */ 
    public VFileSystem getFileSystem()
    {
        return vfsSystem;
    }
    
    // ========================================================================
    // VFS Transfer Interface
    // ========================================================================
    
    final protected VRSTransferManager getTransferManager()
    {
        return vrsContext.getTransferManager(); 
    }
    
    // ========================================================================
    // VFSNode interface 
    // ========================================================================
    
    /**
     * Resolve relative or absolute path against this resource. 
     * @throws VRISyntaxException 
     * 
     * @throws VrsException
     */
    public VRL resolvePath(String subPath) throws VrsException
    {
        return getVRL().resolvePath(subPath); 
    }  
   
    /**
     * Resolve path against this VRL and return resolved filesystem path as String. 
     * Only matches path elements!
     */
    public String resolvePathString(String path) throws VrsException
    {
        return resolvePath(path).getPath();  
    }

    /**
     * Return basename with or without extension. 
     * call getVRL().getBasename(withExtension)  
     */ 
    public String getBasename(boolean withExtension)
    {
        return getVRL().getBasename(withExtension); 
    }
    
    public VFSNode getNode(String path) throws VrsException
    {
        return getPath(path); 
    }
    
    /**
     * Create this file or directory. 
     * @see #create(boolean) 
     *
     * @return true if resource was created or already existed   
     */
    public boolean create() throws VrsException
    {
        return create(true); 
    }
    
    /** Returns root directory of this directory/file system */ 
    public VDir getRoot() throws VrsException
    {
        VNode node=getPath("/"); 
        
        if (node instanceof VDir)
            return (VDir)node;
            
        throw new ResourceTypeMismatchException("Root path is not a directory:"+node); 
    }

    /** 
     * Fetch any VFSNode (VFile or VDir) with the specified absolute
     * or relative path
     */ 
    public VFSNode getPath(String path) throws VrsException
    {
        // resolve absolute or relative path: 
        VRL loc=getLocation().resolvePath(path); 
        return this.getFileSystem().openLocation(loc); 
    }

    /**
     * Optional method for filesystems who support hidden files. 
     * Note that the implementation of hidden files on filesystems
     * might differ!
     * Default implemententation is to return true for 'dot' files. 
     * @throws VrsException 
     */
    public boolean isHidden() throws VrsException
    {
        if (getBasename().startsWith("."))
            return true;

        return false; 
    }

    /**
     * Optional method for filesystems who support symbolic links
     * or File Aliases (LFC). 
     * Default this method return false. 
     * Note that implementations of links on filesystems might differ!
     * @throws VrsException 
     */
    public boolean isSymbolicLink() throws VrsException
    {
        return false;
    };

    /**
     * Optional method to resolve  links if this VFS Implementation
     * supports it. Use isSymbolicLink() first to check whether this file is a
     * (soft) link or a windows shortcut. 
     * Filesystem implementations might differ how they handle symbolic links.  
     * @throws VrsException 
     */
    public VFSNode getSymbolicLinkTarget() throws VrsException
    {
        String path=this.getSymbolicLinkTargetPath();
        if (path==null)
            return null;
        
        return this.getNode(path); 
    };

    /**
     * Optional method to resolve  links if this VFS Implementation
     * supports it. Use isSymbolicLink() first to check whether this file is a
     * (soft) link or a windows shortcut. 
     * Filesystem implementations might differ how they handle symbolic links.  
     * @throws VrsException 
     */
    public String getSymbolicLinkTargetPath() throws VrsException
    {
        return null;
    };
        
    /**
     * Whether the location points to a local available path ! <br>
     * To get the actual local path, do a getPath().<br>
     */
    public boolean isLocal()
    {
        return false;
    }
    
    public String[] getResourceAttributeNames()
    {   
        return null; //getVFSAttributeNames()
    }
    
    /** Returns all default attributes names */
    public String[] getAttributeNames()
    {
        StringList list=new StringList(super.getAttributeNames()); 

        boolean isSoftlink=false;

        try
        {
            isSoftlink=isSymbolicLink();
        }
        catch (VrsException e1)
        {
            // Could not be determined. Assume not. 
            //Global.debugPrintf(this,"***Error: isSymbolicLink() Exception:%s\n",e1); 
        } 

        list.merge(attributeNames);
        if (isSoftlink)
            list.merge(linkAttributeNames); 
        
        if (this instanceof VChecksum)
        {
            list.add(VAttributeConstants.ATTR_CHECKSUM);
            list.add(VAttributeConstants.ATTR_CHECKSUM_TYPE);
            list.add(VAttributeConstants.ATTR_CHECKSUM_TYPES);
        }
        return list.toArray(); 
    }
    
    /**
     * Returns single File Resource Attribute. 
     * For optimized fetching of Attributes use getAttributes(String names[]) 
     */ 
    public Attribute getAttribute(String name) throws VrsException
    {
        if (name==null) 
            return null; 

        // Check if super class has this attribute
        Attribute supervalue = super.getAttribute(name);

        // Super class has this attribute, and since I do not overide
        // any attribute, return this one:
        if (supervalue != null)
            return supervalue;

        if (name.compareTo(ATTR_EXISTS) == 0)
            return new Attribute(name, exists());
        else if (name.compareTo(ATTR_PARENT_DIRNAME) == 0)
            return new Attribute(name, getLocation().getDirname());
        else if (name.compareTo(ATTR_PATH) == 0)
            return new Attribute(name, getLocation().getPath());
        else if (name.compareTo(ATTR_ISDIR) == 0)
            return new Attribute(name, isDir());
        else if (name.compareTo(ATTR_ISFILE) == 0)
            return new Attribute(name, isFile());
        else if (name.compareTo(ATTR_FILE_SIZE) == 0)
        {
            if (this instanceof VFile)
            {
                try
                {
                    return new Attribute(name, ((VFile) this).getLength());
                }
                catch (IOException e)
                {
                  throw new NestedIOException(e); 
                }
            }
            // getLength for VDir not supported
            return new Attribute(name, 0);
        }
        else if (name.compareTo(ATTR_UNIX_GROUPID) == 0)
        {
            if (this instanceof VUnixGroupMode)
                return new Attribute(name, ((VUnixGroupMode) this).getGid());
            // getLength for VDir not supported
            return new Attribute(name, "");
        }
        else if (name.compareTo(ATTR_UNIX_USERID) == 0)
        {
            if (this instanceof VUnixUserMode)
                return new Attribute(name, ((VUnixUserMode) this).getUid());
            // getLength for VDir not supported
            return new Attribute(name, "");
        }
        else if (name.compareTo(ATTR_ISREADABLE) == 0)
            return new Attribute(name, isReadable());
        /* Poor man's Permissions: */
        else if (name.compareTo(ATTR_ISWRITABLE) == 0)
            return new Attribute(name, isWritable());
        else if (name.compareTo(ATTR_ISHIDDEN) == 0)
            return new Attribute(name, isHidden());

        // VComposite attributes
        else if (name.compareTo(ATTR_NRCHILDS) == 0)
        {
            if (this instanceof VDir)
            {
                VDir vdir = (VDir) this;
                return new Attribute(name, vdir.getNrOfNodes());
            }
            return new Attribute(name, 0);
        }
        else if (name.compareTo(ATTR_MODIFICATION_TIME) == 0)
        {
            // New TIME Type ! 
            return AttributeUtil.createDateFromMilliesSinceEpoch(name,getModificationTime());
        }
        /*else if (name.compareTo(ATTR_MODIFICATION_TIME_STRING) == 0)
        {
            return new VAttribute(name,millisToDateTimeString(getModificationTime())); 
        }*/
        else if (name.compareTo(ATTR_PERMISSIONSTRING) == 0)
        {
            return new Attribute(name,getPermissionsString()); 
        }
        else if (name.compareTo(ATTR_ISSYMBOLIC_LINK) == 0)
            return new Attribute(name, isSymbolicLink()); 
        else if (name.compareTo(ATTR_SYMBOLICLINKTARGET) == 0)
            return new Attribute(name, getSymbolicLinkTargetPath());
     
        else if ( (name.compareTo(ATTR_CHECKSUM) == 0) &&   (this instanceof VChecksum) )
        {
        	String types[]=((VChecksum)this).getChecksumTypes();
        	// return first checksum type; 
        	if ((types==null) || (types.length<=0))
        		return null; 
        	
    		return new Attribute(name, ((VChecksum)this).getChecksum(types[0]));
        }
        else if ( (name.compareTo(ATTR_CHECKSUM_TYPE) == 0) &&   (this instanceof VChecksum) )
        {
            String types[]=((VChecksum)this).getChecksumTypes();
            if ((types==null) || (types.length<=0))
                return null; 
            
            return new Attribute(name,types [0]);  
        }
        else if ( (name.compareTo(ATTR_CHECKSUM_TYPES) == 0) &&   (this instanceof VChecksum) )
        {
            String types[]=((VChecksum)this).getChecksumTypes();
            return new Attribute(name,new StringList(types).toString(","));  
        }
        /*
         * java support for local filesystem attributes is rather limited. Maybe
         * a JNI implemention in C++ is required.
         */
        // return null;
        return null; // 
    }

    /**
     * Returns Permissions in Unix like String.  
     * For example "-rwxr-xr-x" for a linux file.
     * This method checks whether this resource implements VUnixFileMode 
     * and used the Unix File Mode to generate the permissions string. 
     * @see nl.esciencecenter.vlet.vrs.vfs.VUnixFileMode
     */ 
    public String getPermissionsString() throws VrsException
    {
        if (this instanceof VUnixFileMode)
        {
            int mode=((VUnixFileMode)this).getMode();
            return VFS.modeToString(mode, isDir()); 
        }
        
        String str = (isDir() ? "d" : "-")
                + (isReadable() ? "r" : "-")
                + (isWritable() ? "w" : "-")
                // append extra Non-Unix attributes/permissions
                + " [" 
                     + (isHidden() ? "H" : "") 
                     + (isSymbolicLink() ? "L" : "")
                 + "]";// + "?";

        return str;
    }

    /**
     * VFSnode implements getParent by calling VFSNode.getParentDir
     * 
     * @throws VrsException
     */

    public VDir getParent() throws VrsException
    {
        VFSNode vfsnode = this.getFileSystem().openLocation(getParentLocation());
        
        if (vfsnode instanceof VDir) 
            return (VDir)vfsnode;
            
        if (vfsnode.isSymbolicLink())
        {
            VFSNode target=vfsnode.getSymbolicLinkTarget();
            if (target instanceof VDir)
                return (VDir)target; 
        }
        
        throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Parent of VFSNode is not of VDir type:"+this);
    }

    /**
     * Returns array whith one parent. 
     * 
     * @throws VrsException
     */
    public VNode[] getParents() throws VrsException
    {
        VNode parents[] = new VNode[1];
        parents[0] = getParent();
        return parents;
    }

    /**
     * RandomAccessable methods:
     */
    public boolean isRandomAccessable()
    {
        return (this instanceof VRandomAccessable);
    }

    //=========================================================================
    // VEditable interface
    //=========================================================================

    /**
     * Default File implementions has editable attributes.
     * Default return value is true; 
     * This differs from isWritable as file persmissions are 'editable' 
     * even if the file is not writable. 
     */
    public boolean isEditable() throws VrsException
    {
        return true;
    }

    public boolean isDeletable() throws VrsException
    {
        return true; 
    }

    public boolean setAttributes(Attribute[] attrs) throws VrsException
    {
        boolean result = true;

        for (Attribute attr:attrs) 
        {
            Boolean res2=true;

            if (attr==null) 
                continue;  // filter out null attributes 

            // filter out non-editable attributes ! 
            if (attr.isEditable()==true)
            {
                res2 = setAttribute(attr);
            }
            else
            {
                //Global.warnPrintf(this,"*** Warning: VFSNode.setAttributes(): Received non-editable attribute:%s\n",attr);
            }

            result = result && res2;
        }

        return result;
    }

    /**
     * Set Attribute. Not much attributes can be set currently by the VFSNode
     * super class. To add extra attributes in a subclass do a
     * super.setAttributes(attr) first check the return value and if it is false
     * add your own. For example:
     * 
     * <pre>
     *  SubClass.setAttribute(VAtribute attr) 
     *  {
     *     if (super.setAttribute(attr)==true) 
     *         return true;
     *     
     *     if (isMyAttribute(attr)) 
     *        return setMyAttribute(attr); 
     *     else   
     *        return false; 
     *  } 
     *  </pre>
     * 
     */
    public boolean setAttribute(Attribute attr) throws VrsException
    {
        String name = attr.getName();

        if (name.compareTo(ATTR_NAME) == 0)
            return renameTo(attr.getStringValue(), false); // default Name
        // attribute = Basename
        // !
        else if (name.compareTo(ATTR_PATH) == 0)
            return renameTo(attr.getStringValue(), true); // default Name attribute =
        // Basename !
        else
            return false;
    }

    //=========================================================================
    // VRenamable interface
    //=========================================================================

    public boolean isRenamable() throws VrsException
    {
        return isWritable(); 
    }

    // ========================
    // ACL interface : Under Construction  
    // =========================
    public void setACL(Attribute[][] acl) throws VrsException
    {
        if (this instanceof VUnixFileMode)
        {
             setUXACL(acl); 
        }
        else
        {
            throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Resource doensn't support ACL:"+this); 
        }
    }
    
    /**
     * Universal Access Control List to support multiple permission schemes.
     * The returned matrix ACL[N][M] is a list of N entities specifying M permissions
     * for Entity N. 
     * For more details {@linkplain VACL}.  
     * @see VACL 
     */ 
    public Attribute[][] getACL() throws VrsException
    {
        if (this instanceof VUnixFileMode)
        {
            return getUXACL(); 
        }
        else
        {
            // default user readable writable list:
            Attribute attrs[][]=new Attribute[1][]; 
            attrs[0]=new Attribute[3]; 

            attrs[0][0]=new Attribute(ATTR_USERNAME,"current");
            attrs[0][0].setEditable(false);
            attrs[0][1]=getAttribute(ATTR_ISREADABLE); 
            attrs[0][1].setEditable(false); 
            attrs[0][2]=getAttribute(ATTR_ISWRITABLE);
            attrs[0][2].setEditable(false);

            return attrs;
        }
    }

    public Attribute[][] getUXACL() throws VrsException
    {
        if ((this instanceof VUnixFileMode)==false)
        {
            throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Resource doensn't support unix style file permissions:"+this); 
        }
        else
        {
            int mode = ((VUnixFileMode)this).getMode(); 
            return VFS.convertFileMode2ACL(mode, isDir());
        }
    }
    
    /**
     * Converts the "user,group,other" permissions attribute list 
     * to a Unix file mode and changes this if this file supported
     * Unix style permission rights.
     *   
     * @see VFS.convertACL2FileMode
     * 
     * @throws VrsException
     */
    public void setUXACL(Attribute[][] acl)
                throws VrsException
    {
        if ((this instanceof VUnixFileMode)==false)
        {
            throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Resource doensn't support unix style file permissions:"+this); 
        }
        else
        {
            int mode = VFS.convertACL2FileMode(acl, isDir());

            if (mode < 0)
                throw new VrsException("Error converting ACL list");
            
            ((VUnixFileMode)this).setMode(mode); 
        }
    }

    /** 
     * Returns all possible ACL entities (users,groups, etc). 
     * Return null if not supported. 
     * @throws NestedIOException 
     */ 
    public Attribute[] getACLEntities() throws NestedIOException
    {
        return null; 
    }

    /**
     *  Create a new ACL Record for the given ACL Entry, that is, a new row
     *  in the ACL[][] matrix returned in getACL(). 
     *  The nr of- and types in this row must match. 
     *  
     * @param writeThrough 
     * @param entity
     */
    public Attribute[] createACLRecord(Attribute entity, boolean writeThrough) throws VrsException
    {
        throw new NotImplementedException("Create new ACL Record not supported");
    }

    /** Delete entry in the ACL list or set permissions to none */ 
    public boolean deleteACLEntity(Attribute entity) throws VrsException
    {
        throw new NotImplementedException("Entities can't be deleted");
    }
    
    // ========================================================================
    // Rename
    // ========================================================================
  
    public boolean renameTo(String newNameOrPath) throws VrsException
    {
        boolean fullpath=false;
        
        if (newNameOrPath.startsWith(URIFactory.URI_SEP_CHAR_STR)==true); 
            fullpath=true; 
        
       return (rename(newNameOrPath,fullpath)!=null);
    }
    
    public boolean renameTo(String newNameOrPath,boolean nameIsPath) throws VrsException
    {
        boolean fullpath=false;
        
        if (newNameOrPath.startsWith(URIFactory.URI_SEP_CHAR_STR)==true)
            fullpath=true; 
        
       return (rename(newNameOrPath,fullpath)!=null); 
    }

    /**
     * Exception-less cast to VDir. 
     * If this VFSNode is a VDir, return as VDir, else return null.
     */ 
    public VDir toDir()
    {
        if (isDir())
            return (VDir)this;
        return null; 
    }
    
    /** 
     * Exception-less cast to VFile. 
     * If this VFSNode is a VFile, return as VFile, else return null 
     */ 
    public VFile toFile()
    {
        if (isFile())
            return (VFile)this;
        return null; 
    }
    
    // ========================================================================
    // Abstract Interface Methods 
    // ========================================================================

    abstract public VRL rename(String newNameOrPath,boolean nameIsPath) throws VrsException; 

    /**
     * Create this Resource. 
     * <p>
     * If ignoreExisting==true, ignore if this path already exists.  
     * <br>
     * If ignoreExisting==false, do not ignore if this resource already exists 
     *    and throw a ResourceAlreadyExistsException to indicate to
     *    that the resource was already created. 
     *    
     * @param ignoreExisting 
     *         Safeguard whether to check if this resource was already created. 
     * @throws VrsException if resource couldn't be created or when ignoreExisting==false and resource already exist.
     * 
     */
    abstract public boolean create(boolean ignoreExisting) throws VrsException ; 
    
    /**
     * Returns true if the node is a file.
     * @see VFile 
     */
    public abstract boolean isFile();

    /**
     * Returns true if the node is a Directory 
     * @see VDir
     */
    public abstract boolean isDir();

    /**
     * Returns true if the this object represents an existing file
     * or directory on the FileSystem. 
     * @throws VrsException 
     */
    public abstract boolean exists() throws VrsException;

    /**
     * Return time of last modification in milli seconds after 'epoch'
     * epoch = (1-jan-1970 GMT). 
     * @throws VrsException
     */
    public abstract long getModificationTime() throws VrsException;

    /**
     * Returns whether the object is readable using current user credentials.
     * <br>
     * For a Directory isReadable means it must have r-x permissions !
     * 
     * @throws VrsException
     * @see exists
     * @see isWritable
     */
    public abstract boolean isReadable() throws VrsException;

    /**
     * Returns whether the object is writable using current user credentials.
     * Note that some implementations make a difference between 
     * 'deletable' 'appendable' and 'can create directories' (GridFTP). 
     *
     * @see exists
     * @see isReadable
     */
    public abstract boolean isWritable() throws VrsException;

    // === Copy/Move Interface === 

    public abstract VFSNode copyTo(VDir dest) throws VrsException;

    public abstract VFSNode copyTo(VDir dest, String newName) throws VrsException;

    public abstract VFSNode moveTo(VDir dest) throws VrsException;

    public abstract VFSNode moveTo(VDir dest, String newName) throws VrsException; 

}
