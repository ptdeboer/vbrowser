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

package nl.esciencecenter.vlet.vrs.vdriver.localfs;

import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_UNIX_FILE_MODE;

import java.io.File;
import java.io.IOException;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.io.local.LocalFSNode;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.ResourceAlreadyExistsException;
import nl.esciencecenter.vlet.exception.ResourceReadAccessDeniedException;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.tasks.VRSTaskMonitor;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.vlet.vrs.vfs.VUnixFileAttributes;


/**
 * Local File System implementation of VDir.
 */
public class LDir extends nl.esciencecenter.vlet.vrs.vfs.VDir implements VUnixFileAttributes
{
    private LocalFilesystem localfs;
    private LocalFSNode fsNode; 
    
    // =================================================================
    // Constructors
    // =================================================================

//    /**
//     * Constructs new local LDir reference (Not the directory itself).
//     * 
//     * @throws VrsException
//     * @throws VrsException
//     */
//    private void init(File file) throws VrsException
//    {
//        // under windows: will return windows path
//        String path = file.getAbsolutePath();
//
//        //
//        // Forward Flip backslashes !
//        // Do this ONLY for the local filesystem !
//        //
//
//        if (File.separatorChar != URIFactory.URI_SEP_CHAR)
//            path = URIFactory.uripath(path, true, File.separatorChar);
//
//        // under widows: will convert windows path to URI path !
//        setLocation(new VRL(VRS.FILE_SCHEME, null, path));
//        this.path = getPath(); // use URI path !
//
//        _file = file;
//    }

    public LDir(LocalFilesystem local, LocalFSNode node) throws VrsException
    {
        super(local, new VRL(node.getURI())); 
        init(node);
        this.localfs=local;
    }

    private void init(LocalFSNode node)
    {
        fsNode=node; 
    }

    public boolean sync()
    {
        return fsNode.sync(); 
    }

    public VDir getParentDir() throws VrsException
    {
        return new LDir(localfs,fsNode.getParent()); 
    }

    /** Returns all default attributes names */
    public String[] getAttributeNames()
    {
        String superNames[] = super.getAttributeNames();

        if (localfs.hasPosixFS())
        {
            StringList list = new StringList(superNames);
            list.add(LocalFSFactory.unixFSAttributeNames);
            return list.toArray();
        }

        return superNames;
    }

    public Attribute getAttribute(String name) throws VrsException
    {
        if (name == null)
            return null;

        // Check if super class has this attribute
        Attribute supervalue = super.getAttribute(name);

        // Super class has this attribute, and since I do not overide
        // any attribute, return this one:
        if (supervalue != null)
            return supervalue;

        if (name.compareTo(ATTR_UNIX_FILE_MODE) == 0)
            return new Attribute(name, Integer.toOctalString(getMode()));

        // return null;
        return null; //
    }

    public long getNrOfNodes()
    {
        if (fsNode == null)
            return 0;

        String list[];
        try
        {
            list = fsNode.list();
            if (list != null)
                return list.length;
        }
        catch (IOException e)
        {
            return 0; 
        }
        
        return 0;
    }

    public VFSNode[] list() throws VrsException
    {
        LocalFSNode[] list;
        try
        {
            list = fsNode.listNodes();
        }
        catch (IOException e)
        {

            throw new VrsException(e.getMessage(),e); 
        }

        if (list == null)
        {
            if (isReadable() == false)
                throw new ResourceReadAccessDeniedException("Cannot read path:" + getPath());
            else
                return null; // empty dir
        }

        VFSNode nodes[] = new VFSNode[list.length];

        for (int i = 0; i < list.length; i++)
        {
            LocalFSNode subNode=list[i];
            if (subNode.isDirectory() == true)
            {
                nodes[i] = new LDir(localfs, subNode);
            }
            else
            {
                nodes[i] = new LFile(localfs, subNode);
            }
        }

        return nodes;
    }

    // *** Instance Attributes ***

    public boolean exists()
    {
        return fsNode.exists() && fsNode.isDirectory();
    }

    public boolean isReadable()
    {
        return fsNode.toJavaFile().canRead();
    }

    public boolean isWritable()
    {
        return fsNode.toJavaFile().canWrite();
    }

    public boolean create(boolean ignoreExisting) throws VrsException
    {
        if (fsNode.exists())
        {
        	if (fsNode.isDirectory()==false) 
        	{
        		throw new ResourceAlreadyExistsException("Path already exists, but is a file:"+this); 
        	}
        	
            if (ignoreExisting)
            {
                return true;
            }
            else
            {
                throw new ResourceAlreadyExistsException("Directory already exists:"+this); 
            }
        }
        
        try
        {
            fsNode.mkdir();
            return true;
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }

    }

    public boolean delete(boolean recurse) throws VrsException
    {
        // Debug("Deleting local directory:"+this);

        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Deleting (local) directory:" + this.getPath(), 1);

        boolean result = true;

        // delete children first.
        if (recurse == true)
        {
            this.getVRSContext().getTransferManager().recursiveDeleteDirContents(monitor,this, true); 
        }
        
        // delete myself
        try
        {
            fsNode.delete();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }

        return result;

    }

    public VRL rename(String newname, boolean nameIsPath) throws VrsException
    {
        File newFile = localfs.renameTo(this.getPath(), newname, nameIsPath);

        if (newFile != null)
        {
            return new VRL(newFile.toURI());
        }

        return null;
    }

    public boolean delNode(VNode node) throws VrsException
    {
        return ((VFSNode) node).delete();
    }

    public boolean isHidden()
    {
        return fsNode.isHidden();
    }
    
    public boolean isLocal()
    {
        return true; // by definition.
    }

    public void setMode(int mode) throws VrsException
    {
        try
        {
            fsNode.setUnixFileMode(mode);
            sync();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    // ===
    // Misc.
    // ===

    @Override
    public boolean isSymbolicLink() throws VrsException
    {
        return fsNode.isSymbolicLink(); 
    }

    @Override
    public String getSymbolicLinkTargetPath() throws VrsException
    {
        if (isSymbolicLink() == false)
        {
            return null;
        }
        
        try
        {
            LocalFSNode targetNode = fsNode.getSymbolicLinkTarget();
            return targetNode.getPathname();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    public String getGid() throws VrsException
    {
        try
        {
            return fsNode.getGroupName(); 
        }
        catch (IOException e)
        {
           throw new VrsException(e.getMessage(),e); 
        }   
    }

    public String getUid() throws VrsException
    {
        try
        {
            return fsNode.getOwnerName();
        }
        catch (IOException e)
        {
           throw new VrsException(e.getMessage(),e); 
        } 
    }

    public int getMode() throws VrsException
    {
        try
        {
            return fsNode.getUnixFileMode();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    public long getModificationTime() throws VrsException
    {
        try
        {
            return fsNode.getModificationTime();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    public String getPermissionsString() throws VrsException
    {
        return VFS.modeToString(this.getMode(), true); 
    }
}
