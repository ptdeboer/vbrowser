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

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.ResourceReadAccessDeniedException;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.tasks.VRSTaskMonitor;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.vlet.vrs.vfs.VUnixFileAttributes;


/**
 * Local File System implementation of VDir.
 */
public class LDir extends nl.esciencecenter.vlet.vrs.vfs.VDir implements VUnixFileAttributes
{
    /**
     * The local path into the local filesystem, this MIGHT differ from the URI
     * path !
     */
    private String path = null;

    /** In Java a directory is implemented as a file also */
    private java.io.File _file = null;

    private StatInfo statInf;

    // public boolean ignoreErrors=false;
    private LocalFilesystem localfs;

    // =================================================================
    // Constructors
    // =================================================================

    /**
     * Contructs new local LDir reference (Not the directory itself).
     * 
     * @throws VrsException
     * @throws VrsException
     */
    private void init(File file) throws VrsException
    {
        // under windows: will return windows path
        String path = file.getAbsolutePath();

        //
        // Forward Flip backslashes !
        // Do this ONLY for the local filesystem !
        //

        if (File.separatorChar != URIFactory.URI_SEP_CHAR)
            path = URIFactory.uripath(path, true, File.separatorChar);

        // under widows: will convert windows path to URI path !
        setLocation(new VRL(VRS.FILE_SCHEME, null, path));
        this.path = getPath(); // use URI path !

        _file = file;
    }

    public LDir(LocalFilesystem local, String path) throws VrsException
    {
        // VRL creation is done in init as well
        super(local, new VRL(VRS.FILE_SCHEME + ":///" + URIFactory.uripath(path, true, java.io.File.separatorChar)));
        this.localfs = local;
        _file = new java.io.File(path);
        init(_file);
    }

    public LDir(LocalFilesystem local, java.io.File file) throws VrsException
    {
        super(local, new VRL(file.toURI()));
        this.localfs = local;
        init(file);
    }

    private StatInfo getStat() throws VrsException
    {
        synchronized (_file)
        {
            if (statInf == null)
            {
                statInf = this.localfs.stat(_file);
            }
        }

        return statInf;
    }

    public boolean sync()
    {
        this.statInf = null;
        return true;
    }

    // *** Instance Attributes ***

    /**
     * @throws VrsException
     * @see nl.uva.vlet.vfs.localfs.i.VNode#getParent()
     */
    public VDir getParentDir() throws VrsException
    {
        // Debug("LDir:Getting parent of:"+_file.getPath());

        String parentpath = null;

        if (_file.getPath().compareTo("/") == 0)
        {
            // Root of root is root. _file.getParent returns NULL otherwise
            // Optional provide root of root as root:
            // path="/";
        }
        else
            parentpath = _file.getParent();

        if (parentpath == null)
            return null;

        return new LDir(localfs, parentpath);
    }

    /** Returns all default attributes names */
    public String[] getAttributeNames()
    {
        String superNames[] = super.getAttributeNames();

        if (localfs.isUnixFS())
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
        if (_file == null)
            return 0;

        String list[] = _file.list();

        if (list != null)
            return list.length;

        return 0;
    }

    public VFSNode[] list() throws VrsException
    {
        String list[] = _file.list();

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
            java.io.File subFile = new java.io.File(path + URIFactory.URI_SEP_CHAR + list[i]);

            if (subFile.isDirectory() == true)
            {
                nodes[i] = new LDir(localfs, path + URIFactory.URI_SEP_CHAR + list[i]);
            }
            else
            {
                nodes[i] = new LFile(localfs, path + URIFactory.URI_SEP_CHAR + list[i]);
            }
        }

        return nodes;
    }

    // *** Instance Attributes ***

    public boolean exists()
    {
        return _file.isDirectory();
    }

    public boolean isReadable()
    {
        return _file.canRead();
    }

    public boolean isWritable()
    {
        return _file.canWrite();
    }

    public boolean create(boolean ignoreExisting) throws VrsException
    {
        VDir dir = this.localfs.createDir(this.path, ignoreExisting);
        return (dir != null);
    }

    public boolean delete(boolean recurse) throws VrsException
    {
        // Debug("Deleting local directory:"+this);

        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Deleting (local) directory:" + this.getPath(), 1);

        boolean result = true;

        // delete children first.
        if (recurse == true)
            this.getVRSContext().getTransferManager().recursiveDeleteDirContents(monitor,this, true); 

        // delete myself
        result = result && _file.delete();

        if (result)
        {
             //* sendNotification(new VRSEvent(this,VRSEvent.NODE_DELETED));
        }
        else
        {
            //Global.warnPrintf(this, "Deletion returned FALSE for:%s\n", this);
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
        return _file.isHidden();
    }

    /** Must overide isLocal() since a Local Directory is accessable locally ! */
    public boolean isLocal()
    {
        return true;
    }

    public void setMode(int mode) throws VrsException
    {
        this.localfs.setMode(getPath(), mode);
        sync();
    }

    // ===
    // Misc.
    // ===

    @Override
    public boolean isSymbolicLink() throws VrsException
    {
        return this.getStat().isSoftLink();
    }

    @Override
    public String getSymbolicLinkTargetPath() throws VrsException
    {
        if (isSymbolicLink() == false)
        {
            // not a link
            return null;
        }

        // windows lnk or shortcut (.lnk under *nix is also windows link!)
        /*
         * Directories can not be .lnks if ((Global.isWindows()) ||
         * (getPath().endsWith(".lnk"))) return
         * localfs.getWindowsLinkTarget(this._file); else
         */
        if (localfs.isUnixFS())
            return localfs.getSoftLinkTarget(this.getPath());

        //Global.warnPrintf(this, "getLinkTarget(): could not resolve local filesystem's link:%s\n", this);

        return null;
    }

    public String getGid() throws VrsException
    {
        return this.getStat().getGroupName();
    }

    public String getUid() throws VrsException
    {
        return this.getStat().getUserName();
    }

    public int getMode() throws VrsException
    {
        return this.getStat().getMode();
    }

    public long getModificationTime() throws VrsException
    {
        return this.getStat().getModTime();
    }

    public String getPermissionsString() throws VrsException
    {
        return this.getStat().getPermissions();
    }
}
