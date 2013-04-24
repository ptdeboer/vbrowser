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

package nl.nlesc.vlet.vfs.lfc;

import java.util.List;

import nl.esciencecenter.ptk.data.BooleanHolder;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.glite.lfc.internal.FileDesc;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.exception.ResourceAlreadyExistsException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrs.VCommentable;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.tasks.VRSTaskMonitor;
import nl.nlesc.vlet.vrs.vfs.VDir;
import nl.nlesc.vlet.vrs.vfs.VFSNode;
import nl.nlesc.vlet.vrs.vfs.VFile;
import nl.nlesc.vlet.vrs.vfs.VReplicatableDirectory;
import nl.nlesc.vlet.vrs.vfs.VUnixFileAttributes;
import nl.nlesc.vlet.vrs.vfs.VUnixFileMode;
import nl.nlesc.vlet.vrs.vrl.VRL;


public class LFCDir extends VDir 
    implements VUnixFileAttributes,ILFCLocation,VCommentable,VReplicatableDirectory
{
    private LFCClient lfcClient;
    private FileDescWrapper fileDisc=null;

    public LFCDir(LFCFileSystem lfcServer, VRL vrl)
    {
        super(lfcServer, vrl);
        this.lfcClient = lfcServer.getLFCClient();
    }

    public LFCDir(LFCFileSystem lfcServer, FileDescWrapper wrapper)
            throws VlException
    {
        super(lfcServer, (VRL) null);
        this.lfcClient = lfcServer.getLFCClient();
        this.fileDisc = wrapper;
        //this.context = lfcClient.getVRSContext();

        VRL vrl = this.lfcClient.createPathVRL(wrapper.getPath());
        this.setLocation(vrl);
    }

    public boolean create(boolean ignoreExisting) throws VlException
    {
        debug("Will create: " + this);
        FileDesc dir = lfcClient.mkdir(this.getPath(), ignoreExisting);
        this.fileDisc=new FileDescWrapper(dir,this.getPath()); 
        return (dir != null);
    }

    public String[] getAttributeNames()
    {
        // ===
        // PTDB: VBrowser optimization: create name list here
        // and return direct ! 
        // === 
        
        StringList attrNames = new StringList(VFile.attributeNames); 
        attrNames.merge(VFile.linkAttributeNames); 
        attrNames.merge(LFCClient.lfcDirAttributeNames);
        return attrNames.toArray();
    }

    public VAttribute getAttribute(String name) throws VlException
    {
        VAttribute attr = null;

        // check my attributes:
        attr=this.fileDisc.getAttribute(name);
                
        // check lfc attributes: 
        if (attr != null)
            return attr;
        else
            // return super attribute;
            return super.getAttribute(name);
        
    }

    public VAttribute[] getAttributes(String names[]) throws VlException
    {
        // do smart caching/checkig
        return super.getAttributes(names);
    }

    @Override
    public VDir createDir(String name, boolean ignoreExisting)
            throws VlException
    {
        name = resolvePathString(name);

        debug("Will create: " + name);
        lfcClient.mkdir(name, ignoreExisting);

        return (VDir) lfcClient.getPath(name);
    }

    @Override
    public VFile createFile(String name, boolean ignoreExisting)
            throws VlException
    {
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Create file:"+this,1);
        
        name = resolvePathString(name);
        debug("Will create: " + name);

        // if (!ignoreExisting)
        // {
        // if (existsFile(name))
        // throw new nl.uva.vlet.exception.ResourceAlreadyExistsException("Will
        // not recreate existing file:"+name);
        // }

        // create EMPTY lfc entry *here* (null replicas)
        try
        {
            lfcClient.registerEntry(name);
        }
        catch (VlException ex)
        {
            if (ex instanceof ResourceAlreadyExistsException && ignoreExisting)
            {
                lfcClient.unregister(monitor,(ILFCLocation) this.getFile(name), false);
                return createFile(name, ignoreExisting);
            }
            else
            {
                throw ex;
            }
        }

        VFile file = (VFile) lfcClient.getPath(name);
        return file;
    }

    @Override
    public boolean existsDir(String fileName) throws VlException
    {
        // check relative vs absolute path names
        fileName = resolvePathString(fileName);

        BooleanHolder isDir = new BooleanHolder();

        if (lfcClient.exists(fileName, isDir) == false)
            return false;

        return isDir.booleanValue();
    }

    @Override
    public boolean existsFile(String fileName) throws VlException
    {
        fileName = resolvePathString(fileName);

        BooleanHolder isDir = new BooleanHolder();

        if (!lfcClient.exists(fileName, isDir))
            return false;
        
        // return true if file exists AND is a file 
        if (lfcClient.exists(fileName, isDir)) 
        	if  (isDir.booleanValue()==false)
        		return true;
        	else
        		return false; // exists but is not a File !
        else
        	return false;
    }

    @Override
    public VFSNode[] list() throws VlException
    {
        //Global.infoPrintln(this, "list():"+this); 
        
        debug("=========== listing: " + this.getPath());
        return lfcClient.listNodes(this);
    }

    private void debug(String msg)
    {
        ClassLogger.getLogger(LFCDir.class).debugPrintf("%s\n",msg); 
    }

    @Override
    public boolean exists() throws VlException
    {
        BooleanHolder isDir = new BooleanHolder();
        if (lfcClient.exists(getPath(), isDir) == false)
            return false;
        return isDir.booleanValue();
    }

    @Override
    public long getModificationTime() throws VlException
    {
        return getWrapperDesc().getFileDesc().getCDate().getTime();
    }

    @Override
    public String getPermissionsString() throws VlException
    {
        // return permissions string as-is.
        return getWrapperDesc().getFileDesc().getPermissions();
    }

    @Override
    public boolean isReadable() throws VlException
    {
        return getWrapperDesc().isReadableByUser();

    }

    @Override
    public boolean isWritable() throws VlException
    {
        return getWrapperDesc().isWritableByUser();
    }

    public long getNrOfNodes() throws VlException
    {
//        if (fileDisc.getNumOfNodes() == -1)
//        {
//            VFSNode[] nodes = lfcClient.list(getPath());
//            fileDisc.setNumOfNodes(nodes.length);
//        }
        return fileDisc.getFileDesc().getULink();//fileDisc.getNumOfNodes();
    }

    public VRL rename(String newName, boolean renameFullPath)
            throws VlException
    {
        String newPath = null;
        VRL thisPath = null;
        if ((renameFullPath == true) || (newName.startsWith("/")))
            newPath = newName;
        else
        {
//            newPath = resolvePath(newPath);//this returns the same path    
            thisPath = new VRL(getPath());
            newPath = resolvePathString(thisPath.getParent().getPath()+URIFactory.SEP_CHAR+newName);    
        }
        
        return lfcClient.mv(getPath(), newPath);
    }

    public boolean delete(boolean recurse) throws VlException
    {
        ITaskMonitor minitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Delete:"+this,1);
        return lfcClient.rmDir(minitor,this, recurse, false);
    }
    
    public boolean forceDelete() throws VlException
    {
        ITaskMonitor minitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("ForceDelete:"+this,1);
        return lfcClient.rmDir(minitor,this, true,true);
    }
    
    @Override
    public String getSymbolicLinkTarget() throws VlException
    {
        // do not query remote server if this isn't a link: 
        if (getWrapperDesc().getFileDesc().isSymbolicLink()==false)
            return null; 
        
        // PTdB: experimental 
        return this.lfcClient.getLinkTarget(getPath()); 
    }

    @Override
    public boolean isSymbolicLink() throws VlException
    {
        return getWrapperDesc().getFileDesc().isSymbolicLink();
    }

    public String getGUID() throws VlException
    {
        return getWrapperDesc().getFileDesc().getGuid(); 
    }

    public int getMode() throws VlException
    {
        return getWrapperDesc().getFileDesc().getFileMode();
    }

    public void setMode(int mode) throws VlException
    {
        lfcClient.setMode(this.getPath(), mode);
        this.fileDisc = null;
    }

    public FileDescWrapper getWrapperDesc() throws VlException
    {
        if (fileDisc==null)
            fileDisc = this.lfcClient.queryPath(getPath(),true); // normal stat
        
        return this.fileDisc;
    }
    
    public FileDesc getFileDesc() throws VlException
    {
        return this.getWrapperDesc().getFileDesc(); 
    }
    
    public String getComment() throws VlException
    {
        return getWrapperDesc().getFileDesc().getComment();
    }

    public void setComment(String comment) throws VlException
    {
        getWrapperDesc().getFileDesc().setComment(comment);
        lfcClient.setComment(getPath(), comment);        
    }
    
    @Override
    public String getUid() throws VlException
    {
        // return cached or fetch new:
        FileDesc desc = getFileDesc();

        return ""+desc.getUid(); 
    }
    @Override
    public String getGid() throws VlException
    {
        // return cached or fetch new:
        FileDesc desc = getFileDesc();

        return ""+desc.getGid();
    }
    
    @Override
    public LFCFileSystem getFileSystem()
    {
        return (LFCFileSystem)super.getFileSystem(); 
    }
    
    @Override
    public void replicateTo(ITaskMonitor monitor, List<String> listSEs) throws VlException
    {
        this.getFileSystem().replicateDirectory(monitor,this,listSEs); 
    }

}
