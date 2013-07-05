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

package nl.esciencecenter.vlet.vfs.gftp;

import java.util.Vector;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.tasks.VRSTaskMonitor;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;

import org.globus.ftp.MlsxEntry;

/**
 * Implementation of GftpDir
 * 
 * @author P.T. de Boer
 */
public class GftpDir extends VDir
{
    // private GFTP handler object to this resource.
    // private GridFTPClient gftpClient = null;

    private MlsxEntry _entry = null;

    // Package protected !:
    GftpFileSystem server = null;

    /**
     * @param client
     * @throws VrsException
     */
    GftpDir(GftpFileSystem server, String path, MlsxEntry entry) throws VrsException
    {
        super(server, server.getServerVRL().replacePath(path));
        init(server, path, entry);
    }

    GftpDir(GftpFileSystem server, String path) throws VrsException
    {
        this(server, path, null);
    }

    private void init(GftpFileSystem server, String path, MlsxEntry entry) throws VrsException
    {
        this._entry = entry;
        this.server = server;
    }

    @Override
    public boolean exists()
    {
        return server.existsDir(this.getPath());
    }

    public boolean create(boolean force) throws VrsException
    {
        VDir dir = this.server.createDir(getPath(), force);
        updateEntry();
        return (dir != null);
    }

    /**
     * Reload MLST entry
     * 
     * @throws VrsException
     */
    private MlsxEntry updateEntry() throws VrsException
    {
        this._entry = this.server.mlst(getPath());
        return _entry;
    }

    @Override
    public boolean isReadable() throws VrsException
    {
        return GftpFileSystem._isReadable(getMlsxEntry());
    }

    @Override
    public boolean isAccessable() throws VrsException
    {
        return GftpFileSystem._isAccessable(getMlsxEntry());
    }

    @Override
    public boolean isWritable() throws VrsException
    {
        return GftpFileSystem._isWritable(getMlsxEntry());
    }

    @Override
    public VRL rename(String newName, boolean nameIsPath) throws VrsException
    {
        String path = server.rename(this.getPath(), newName, nameIsPath);
        return this.resolvePath(path);
    }

    public VDir getParentDir() throws VrsException
    {
        return server.getParentDir(this.getPath());
    }

    public long getNrOfNodes()
    {
        try
        {
            Object list[] = list();

            if (list != null)
                return list.length;
        }
        catch (VrsException e)
        {
            ;
        }

        return 0;
    }

    public VFSNode[] list() throws VrsException
    {
        Vector<?> list = null;

        String path = this.getPath();

        list = server.mlsd(path);

        if (list == null)
            return null;

        Vector<VFSNode> nodes = new Vector<VFSNode>();

        for (Object o : list)
        {
            MlsxEntry entry = ((MlsxEntry) o);
            String name = entry.getFileName();
            name = URIFactory.basename(name);
            // Debug("fileOnfo=" + fileInfo);

            String remotePath = path + "/" + name;

            if (GftpFileSystem._isFile(entry))
                nodes.add(new GftpFile(server, remotePath, entry));
            else if (GftpFileSystem._isXDir(entry))
            {
                // Skip '.' and '..'
                // nodes[j] = null; // new GftpDir(server,remotePath,entry);
                ;
            }
            else if (GftpFileSystem._isDir(entry))
                nodes.add(new GftpDir(server, remotePath, entry));
            /*
             * else if (fileInfo.isSoftLink()) nodes[i] = new
             * GftpFile(server,remotePath,fileInfo);
             */
            else
            {
                // DEFAULT: add as file could be anything (link?)
                nodes.add(new GftpFile(server, remotePath, entry));
                // ; // nodes[j] = null;
            }
        }

        VFSNode nodeArray[] = new VFSNode[nodes.size()];
        nodeArray = nodes.toArray(nodeArray);

        return nodeArray;
    }

    public boolean delete(boolean recurse) throws VrsException
    {
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Deleting (GFTP) directory:" + this.getPath(), 1);

        // Delete children first:
        if (recurse == true)
            this.getVRSContext().getTransferManager().recursiveDeleteDirContents(monitor,this, true); 

        return server.delete(true, this.getPath());
    }

    /** Check if directory has child */
    public boolean existsFile(String name) throws VrsException
    {
        String newPath = resolvePathString(name);
        return server.existsFile(newPath);
    }

    public boolean existsDir(String dirName) throws VrsException
    {
        String newPath = resolvePathString(dirName);
        return server.existsDir(newPath);
    }

    public long getModificationTime() throws VrsException
    {
        // doesnot work for directories: return
        // server.getModificationTime(this.getPath());
        return -1;
    }

    public String[] getAttributeNames()
    {
        String superNames[] = super.getAttributeNames();

        if (this.server.protocol_v1)
            return superNames;

        return StringList.merge(superNames, GftpFSFactory.gftpAttributeNames);
    }

    @Override
    public Attribute[] getAttributes(String names[]) throws VrsException
    {
        if (names == null)
            return null;

        Attribute attrs[] = new Attribute[names.length];

        // Optimized getAttribute: use single entry for all
        MlsxEntry entry = this.getMlsxEntry();

        for (int i = 0; i < names.length; i++)
        {
            attrs[i] = getAttribute(entry, names[i]);
        }

        return attrs;
    }

    @Override
    public Attribute getAttribute(String name) throws VrsException
    {
        return getAttribute(this.getMlsxEntry(), name);
    }

    /**
     * Optimized method. When fetching multiple attributes, do not refetch the
     * mlsxentry for each attribute.
     * 
     * @param name
     * @param update
     * @return
     * @throws VrsException
     */
    public Attribute getAttribute(MlsxEntry entry, String name) throws VrsException
    {
        // is possible due to optimization:

        if (name == null)
            return null;

        // get Gftp specific attribute and update
        // the mslxEntry if needed

        Attribute attr = GftpFileSystem.getAttribute(entry, name);

        if (attr != null)
            return attr;

        return super.getAttribute(name);
    }

    public MlsxEntry getMlsxEntry() throws VrsException
    {
        if (_entry == null)
            _entry = updateEntry();
        return _entry;
    }

}
