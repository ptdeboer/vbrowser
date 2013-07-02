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

package nl.esciencecenter.vlet.vfs.lfc;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import nl.esciencecenter.glite.lfc.internal.FileDesc;
import nl.esciencecenter.glite.lfc.internal.ReplicaDesc;
import nl.esciencecenter.ptk.data.BooleanHolder;
import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.InternalError;
import nl.esciencecenter.vlet.exception.ResourceLinkIsBorkenException;
import nl.esciencecenter.vlet.exception.ResourceNotFoundException;
import nl.esciencecenter.vlet.vrs.VCommentable;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;
import nl.esciencecenter.vlet.vrs.util.VRSIOUtil;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSTransfer;
import nl.esciencecenter.vlet.vrs.vfs.VFile;
import nl.esciencecenter.vlet.vrs.vfs.VFileActiveTransferable;
import nl.esciencecenter.vlet.vrs.vfs.VGlobalUniqueID;
import nl.esciencecenter.vlet.vrs.vfs.VLinkListable;
import nl.esciencecenter.vlet.vrs.vfs.VLogicalFileAlias;
import nl.esciencecenter.vlet.vrs.vfs.VReplicatable;
import nl.esciencecenter.vlet.vrs.vfs.VUnixFileAttributes;


public class LFCFile extends VFile implements VLogicalFileAlias, VUnixFileAttributes,
        ILFCLocation, VReplicatable, VGlobalUniqueID,
        VLinkListable, VCommentable// , VChecksum
{
    private FileDescWrapper wrapperDesc;
    private LFCClient lfcClient;

    public LFCFile(LFCFileSystem lfcServer, FileDescWrapper wrapper)
            throws VrsException
    {
        super(lfcServer, (VRL) null);
        this.setWrapperDesc(wrapper);
        this.lfcClient = lfcServer.getLFCClient();

        VRL vrl = lfcClient.createPathVRL(wrapper.getPath());

        this.setLocation(vrl);
    }

    public String[] getAttributeNames()
    {
        // ===
        // PTdB: VBrowser optimization: create name list here
        // and return direct !
        // ===

        StringList attrNames = new StringList(VFile.attributeNames);
        attrNames.merge(VFile.linkAttributeNames);
        attrNames.merge(LFCClient.lfcFileAttributeNames);

        if (VRS.isGUID(getScheme()))
        {
            attrNames.add(LFCFSFactory.ATTR_LOGICAL_FILENAME);
        }
        String superNames[] = super.getAttributeNames();
        attrNames.merge(superNames);

        return attrNames.toArray();
    }

    // LFC Creat is register new entry;
    @Override
    public boolean create(boolean force) throws VrsException
    {
        String fileName = getPath();
        FileDescWrapper desc = lfcClient.registerEntry(fileName);

        // update entry !
        this.setWrapperDesc(desc);

        if ((desc != null) && (desc.getFileDesc().isFile() == true))
            return true;

        return false;
    }

    public Attribute getAttribute(String name) throws VrsException
    {
        Attribute attr = null;

        // Check non-mutable first to speed up attribute fetchings. 
        attr=this.getStaticAttribute(name); 
        if (attr!=null)
            return attr; 
        
        // file might not exist: check attribute first  
        if (name.equals(VAttributeConstants.ATTR_EXISTS))
        {
            return new Attribute(name,exists());  
        }
        
        // Now check my attributes:
        attr = this.getWrapperDesc().getAttribute(name);

        if (attr == null)
        {
            if (StringUtil.equals(name, LFCFSFactory.ATTR_NUM_REPLICAS))
            {
                ReplicaDesc[] reps = this.getReplicaDescriptions();
                if (reps != null)
                    attr = new Attribute(name,
                            this.getReplicaDescriptions().length);
            }
            else if (StringUtil.equals(name, LFCFSFactory.ATTR_LFC_REPLICAHOSTS))
            {
                // use CACHED descriptions!
                ReplicaDesc[] reps = this.getReplicaDescriptions();
                StringBuffer buf = new StringBuffer();
                if (reps != null)
                {
                    for (int i = 0; i < reps.length; i++){
                      buf.append(reps[i].getHost());
                        if (i + 1 < reps.length)
                            buf.append(",");
                    }
                }
                attr = new Attribute(name, buf.toString());
            }

            else if (StringUtil.equals(name, LFCFSFactory.ATTR_LOGICAL_FILENAME))
            {
                attr = new Attribute(name, this.getLogicalVRL().getPath());
            }
            
            else if (StringUtil.equals(name, LFCFSFactory.ATTR_LFC_COMMENT))
            {
                attr = new Attribute(name, getComment());
            }
        }

        // check lfc attributes:
        if (attr != null)
            return attr;
        else
            // return super attribute;
            return super.getAttribute(name);
    }

    // downcast:
    public LFCFileSystem getFileSystem()
    {
        return (LFCFileSystem) super.getFileSystem();
    }

    public String getPermissionsString() throws VrsException
    {
        return this.getWrapperDesc().getFileDesc().getPermissions();
    }

    public Attribute[] getAttributes(String names[]) throws VrsException
    {
        // do smart caching/checking/stripping names,etc.
        return super.getAttributes(names);
    }

    private void debug(String msg)
    {
        //Global.debugPrintln(this, msg);
    }

    private void info(String msg)
    {
        //Global.infoPrintln(this, msg);
    }

    @Override
    public long getLength() throws IOException
    {
        try
        {
            // return cached or fetch new:
            FileDesc desc = getFileDesc();
            return desc.getFileSize();
        }
        catch (VrsException e)
        {
            throw new IOException(e.getMessage(),e); 
        }
    }
    
    @Override
    public String getUid() throws VrsException
    {
        // return cached or fetch new:
        FileDesc desc = getFileDesc();

        return ""+desc.getUid(); 
    }
    @Override
    public String getGid() throws VrsException
    {
        // return cached or fetch new:
        FileDesc desc = getFileDesc();

        return ""+desc.getGid();
    }

    @Override
    public boolean exists() throws VrsException
    {
        BooleanHolder isDir = new BooleanHolder();
        if (!lfcClient.exists(getPath(), isDir))
        {
            return false;
        }

        return !isDir.booleanValue();
    }

    @Override
    public long getModificationTime() throws VrsException
    {
        return getWrapperDesc().getFileDesc().getCDate().getTime();
    }

    @Override
    public boolean isReadable() throws VrsException
    {
        return getWrapperDesc().isReadableByUser();
    }

    @Override
    public boolean isWritable() throws VrsException
    {
        return getWrapperDesc().isWritableByUser();
    }

    public InputStream createInputStream() throws IOException
    {
        // fetch or create TaskMonitor
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                "getInputStream:" + this, -1);
        try
        {
            return lfcClient.getInputStream(monitor, this);
        }
        catch (VrsException e)
        {
            throw new IOException(e.getMessage(),e);
        }
    }

    public OutputStream createOutputStream() throws IOException
    {
        // fetch or create TaskMonitor
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                "getOutputStream to:" + this, -1);
        try
        {
            return lfcClient.createOutputStream(monitor, this);
        }
        catch (VrsException e)
        {
            throw new IOException(e.getMessage(),e);
        }
        
    }

    public VRL rename(String newName, boolean renameFullPath)
            throws VrsException
    {
        if (renameFullPath)
        {
            return lfcClient.mv(getPath(), newName);
        }
        return lfcClient.mv(getPath(), getVRL().getParent().getPath() + "/"
                + newName);
    }

    public boolean delete() throws VrsException
    {
        debug("delete:" + this);
        // check if executed during actiontask or create new Task Monitor:
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                "Deleting:" + this, 1);

        // PTdB symbolic link deletion is always unregister
        // this could be an option
        if (this.isSymbolicLink())
        {
            return lfcClient.unregister(monitor, this, false);
        }
        else
        {
            return lfcClient.recurseDelete(monitor, this,false);
        }
    }
    
    public boolean forceDelete() throws VrsException
    {
        debug("forceDelete:" + this);
        // check if executed during actiontask or create new Task Monitor:
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                "ForceDeleting:" + this, 1);
        return this.lfcClient.recurseDelete(monitor,this,true);
    }

    @Override
    public String getSymbolicLinkTarget() throws VrsException
    {
        // do not query remote server if this isn't a link:
        if (isSymbolicLink() == false)
            return null;

        // check cached value:
        String targetPath = this.wrapperDesc.getResolvedLinkPath();

        if (targetPath == null)
        {
            // query
            targetPath = this.lfcClient.getLinkTarget(getPath());
            // set cached path: will not change during the lifetime
            // of this object:
            wrapperDesc.setResolvedLinkPath(targetPath);
        }

        if (targetPath == null)
        {
            throw new ResourceNotFoundException(
                    "Couldn't resolved link target path:" + this);
        }

        return targetPath;

    }

    @Override
    public boolean isSymbolicLink() throws VrsException
    {
        debug("isSymbolicLink:" + this);

        boolean val = false;

        if (getWrapperDesc().getFileDesc() != null)
        {
            // Link Handling:
            // For now FileDesc should have correct information
            val = getWrapperDesc().getFileDesc().isSymbolicLink();
        }
        else
        {
            // getWrapperDesc() should now auto-load description !
            throw new InternalError("File description not loaded yet:" + this);
        }
        // ===
        // Not done: wrapperDesc should now have correct information !
        // ===

        // PTdB: extra link checking:
        // set to true to do extra checking but this slowdowns link detection
        // because
        // for each file which wasn't a link: queryPath is called again to make
        // sure !

        boolean extraLinkChecking = false; // extra option ?

        if (extraLinkChecking == false)
            return val;

        // else
        {
            // Extra link checking:
            // Link Handling:
            // if FileDesc holds unresolved link description then isSymbolicLink
            // holds correct information.

            if (val == true)
            {
                return true;
            }
            else
            {
                // perform extra LinkStat to double check !
                // check linkstat and return true value of isLink !
                FileDescWrapper desc = this.lfcClient.queryPath(getPath(),
                        false);
                return desc.getFileDesc().isSymbolicLink();
            }
        }
    }

    // ===
    // Alias Interface
    // ===

    /**
     * Warning: An LGCG "alias" is implemented as an LFC "symbolic link" !
     * 
     */
    public boolean isAlias() throws VrsException
    {
        return isSymbolicLink();
    }

    public VRL addAlias(VRL newAlias) throws VrsException
    {
        ILFCLocation lfcloc = lfcClient.createSymLink(this, newAlias);
        // return possible updated VRL
        return lfcloc.getVRL();
    }

    /**
     * The alias 'target' is the symbolic link target. For LFC this means the
     * "linked-to" path and NOT the GUID. Use getGuid() to get the Guid of the
     * file or link.
     */
    public VRL getAliasTarget() throws VrsException
    {
        // return as VRL
        return getVRL().replacePath(getSymbolicLinkTarget());
    }

    /**
     * Returns GUID of file, or in the case of a Symbolic Link the GUID of the
     * Target File ! This method tries to resolve links and does extra broken
     * link detection !
     * 
     * @throws ResourceNotFoundException
     *             if the GUID can not be resolved.
     */
    // Override
    public String getGUID() throws VrsException
    {
        debug("getGuid()" + this);

        FileDescWrapper wrap = getWrapperDesc();
        String guid = wrap.getFileDesc().getGuid();

        if (StringUtil.isEmpty(guid) == false)
            return guid;

        // 
        // Link handling or missing GUID.
        // be robust here: (re)query path if necessary ...
        // 

        FileDesc resolvedDesc = null;

        if (this.isSymbolicLink())
        {
            // check if link already has been resolved:
            resolvedDesc = wrap.getResolvedLinkFileDesc();

            // resolve link:
            if (resolvedDesc == null)
            {
                // cache resolved FileDesc:
                FileDescWrapper ldesc = this.lfcClient.queryPath(getPath(),
                        true);
                wrap.setResolvedLinkFileDesc(ldesc.getFileDesc());

                if (ldesc == null)
                    throw new ResourceLinkIsBorkenException(
                            "Couldn't stat target of symblic link. Broken Link:"
                                    + this);

                resolvedDesc = ldesc.getFileDesc();
            }

            guid = resolvedDesc.getGuid();

            return guid;
        }
        else
        {
            // Missing guid from FileDesc.
            // This can happen when a linkstat has been done
            // on a file which is not a link since that method
            // sadly never returns the GUID.
            // Must query path again and update FileDesc !

            FileDescWrapper gdesc = this.lfcClient.queryPath(getPath(), true);
            wrap.setFileDesc(gdesc.getFileDesc());
            guid = gdesc.getFileDesc().getGuid();

            if (StringUtil.isEmpty(guid) == true)
            {
                // Assert: May not happen !
                throw new ResourceNotFoundException(
                        "Cannot resolve GUID of this file:" + this);
            }

            return guid;
        }
    }

    // ===
    // Unix File Mode Interface
    // ===

    public int getMode() throws VrsException
    {
        return getWrapperDesc().getFileDesc().getFileMode();
    }

    public void setMode(int mode) throws VrsException
    {
        lfcClient.setMode(this.getPath(), mode);
        this.wrapperDesc = null;
    }

    // ===
    //
    // ===
    public void setWrapperDesc(FileDescWrapper wrapperDesc)
    {
        this.wrapperDesc = wrapperDesc;
    }

    public FileDescWrapper getWrapperDesc() throws VrsException
    {
        // if this is a new file, or the Description hasn't been fetched
        // get it now:
        if ((wrapperDesc == null) || (wrapperDesc.getFileDesc() == null))
        {
            // LinkHandling: Do not resolve links here.
            // if this path really is a link: use linkstat so that
            // IsSymbolicLink really returns true for links, else
            // this file will contain resolved link information and the
            // isSymbolic Link detected will fail !
            this.wrapperDesc = this.lfcClient.queryPath(getPath(), false);
        }

        return wrapperDesc;
    }

    public FileDesc getFileDesc() throws VrsException
    {
        return getWrapperDesc().getFileDesc();
    }

    public ReplicaDesc[] getReplicaDescriptions() throws VrsException
    {
        FileDescWrapper wrap = getWrapperDesc();

        // Use Cached: ?
        ReplicaDesc[] reps = wrap.getReplicas();

        // replica changing happens ONLY in listReplicas
        if (reps == null)
        {
            reps = lfcClient.listReplicasByGuid(getGUID());
            // keep cached replicas ?
            wrap.setReplicas(reps);
        }

        return reps;
    }

    protected void clearCachedReplicas()
    {
        if (wrapperDesc != null)
        {
            wrapperDesc.clearCachedReplicas();
        }
    }

    public VRL getSelectedReplicaVRL(ITaskMonitor monitor, int tryNr)
            throws VrsException
    {
        ReplicaDesc[] replicaDesc = getReplicaDescriptions();
        
        if ((replicaDesc==null) || (replicaDesc.length<=0))
        {
            throw new nl.esciencecenter.vlet.exception.ResourceReadException("File doesn't have any replicas:"+this); 
        }
        
        VRL replicaVRL = this.lfcClient.replicaSelection(monitor, replicaDesc,
                tryNr);

        return replicaVRL;
    }

    /**
     * Returns new Replica File using Selected Storage Element for writing.
     * Replica file itself doesn't exists yet. !
     */
    public VFile generateNewReplicaInSE(ITaskMonitor monitor, int tryNr)
            throws VrsException
    {
        return this.lfcClient.generateNewReplica(monitor, getBasename(), tryNr);
    }

    public VRL[] getReplicas() throws VrsException
    {
        ReplicaDesc[] descs = this.getReplicaDescriptions();
        VRL vrls[] = new VRL[descs.length];

        for (int i = 0; i < descs.length; i++)
        {
            String sfn = descs[i].getSfn();
            vrls[i] = new VRL(sfn);
            vrls[i]=this.lfcClient.lfcServerNode.updateSRMV22location(vrls[i]); 
        }

        return vrls;
    }

   

    /** Add Existing Replica file to this file */
    // public void addReplica(VFile replica,boolean updateReplicaMetaData)
    // throws VlException
    // {
    // if (replica.exists()==false)
    // throw new
    // nl.uva.vlet.exception.ResourceNotFoundException("Cannot add non existing replica file:"+replica);
    //        
    // this.lfcClient.addReplica(this,replica,updateReplicaMetaData);
    // }
    public LFCClient getLFCClient()
    {
        return this.lfcClient;
    }

    // @Override
    public VRL getLogicalVRL() throws VrsException
    {
        // resolve GUID to LFN:
        if (VRS.isGUID(getScheme()))
        {
            return this.lfcClient.getLFNVRL(this);
        }
        else
        {
            return getVRL();
        }
    }

    /**
     * Return list of links to this file, including the original LFN itself !
     */
    public ArrayList<String> getLinkPathsTo() throws VrsException
    {
        return this.lfcClient.getLinksTo(this);
    }

    /**
     * Return list of links to this file, including the original LFN itself !
     */
    public VRL[] getLinksTo() throws VrsException
    {
        ArrayList<String> paths = lfcClient.getLinksTo(this);

        if (paths == null)
            return null;

        VRL vrls[] = new VRL[paths.size()];

        for (int i = 0; i < paths.size(); i++)
        {
            vrls[i] = this.resolvePath(paths.get(i));
        }

        return vrls;
    }

    // update VRL to guid:... VRL (post construction!)
    protected void setGUIDVRL(VRL vrl)
    {
        this.setLocation(vrl);
    }

    @Override
    public boolean registerReplicas(VRL[] vrls) throws VrsException
    {
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                "registerReplicas", -1);
        return this.lfcClient.registerReplicas(monitor, this, vrls);
    }

    @Override
    public boolean unregisterReplicas(VRL[] vrls) throws VrsException
    {
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                "unregisterReplicas", -1);
        return this.lfcClient.unregisterReplicas(monitor, this, vrls);
    }

    public String getComment() throws VrsException
    {

        String comment = null;
        // the file description might not be featched
        if (wrapperDesc.getFileDesc() == null)
        {
            wrapperDesc.setFileDesc(lfcClient.queryPath(this.getPath(), true)
                    .getFileDesc());
        }
        // look in cache
        comment = wrapperDesc.getFileDesc().getComment();
        if (comment == null)
        {
            comment = lfcClient.getComment(this.getPath());
            if (comment == null)
                comment = "";
            wrapperDesc.getFileDesc().setComment(comment);
        }
        return comment;
    }

    public void setComment(String comment) throws VrsException
    {
        // if comment is the same don't set it agin
        // if (!comment.equals(wrapperDesc.getFileDesc().getComment()))
        // {
        getWrapperDesc().getFileDesc().setComment(comment);
        lfcClient.setComment(getPath(), comment);
        // clearMetaData(); this.wrapperDesc=null;
        // }
    }

    @Override
    public VRL replicateTo(ITaskMonitor monitor, String storageElement)
            throws VrsException
    {
        if (monitor == null)
            monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                    "Replicate to Storage Element:" + storageElement, -1);

        VFile file = this.lfcClient.replicateFile(monitor, this, storageElement);

        if (file == null)
            return null;

        return file.getVRL();
    }

    @Override
    public boolean deleteReplica(ITaskMonitor monitor, String storageElement)
            throws VrsException
    {
        if (monitor == null)
            monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                    "Deleting Replicate as Storage Element:" + storageElement,
                    -1);

        return this.lfcClient.deleteReplica(monitor, this, storageElement);
    }

    public ReplicaDesc getReplicaDescription(String storageElement)
            throws VrsException
    {
        ReplicaDesc[] reps = this.getReplicaDescriptions();

        for (ReplicaDesc rep : reps)
        {
            if (StringUtil.equalsIgnoreCase(rep.getHost(), storageElement))
                return rep;
        }

        return null;
    }

    // checksum depends on srm
    // @Override
    public String getChecksum(String algorithm) throws VrsException
    {
        return wrapperDesc.getFileDesc().getChkSumValue();
    }

    // @Override
    public String[] getChecksumTypes() throws VrsException
    {
        return new String[] { wrapperDesc.getFileDesc().getChkSumType() };
    }

    public void setChecksum(String type, String checksum)
    {
        wrapperDesc.getFileDesc().setChkSumType(type);
        wrapperDesc.getFileDesc().setChkSumValue(checksum);
    }

    /** Explicitly set the File Size. This will NOT effect the Replicas ! */ 
    public void updateFileSize(long newSize) throws VrsException
    {
        this.lfcClient.setFileSize(this,newSize); 
        this.wrapperDesc.clearMetaData();
        // Event!
        this.fireAttributeChanged(new Attribute(VAttributeConstants.ATTR_LENGTH,newSize)); 
    }
 
    /** @Override */
    protected void downloadTo(VFSTransfer transfer,VFile targetLocalFile) throws VrsException
    {
        try
        {
           // copy contents into local file:
           // vrsContext.getTransferManager().doStreamCopy(transfer,this,targetLocalFile);  
           long len=getLength();
    
           // ====
           // Patch: If LFC File size==0 ignore length and copy the actual nr of bytes that can be 
           // read from the replica. If it really is zero it doesn't matter, but if the LFC file size
           // wasn't properly updated the actual nr of bytes will be read from the replica i.s.o 0 bytes
           // This is similar behavious as to lcg- commands.
           // ====
           if (len==0) 
                len=-1; 
           //Call stream copy direclty ignoreing file size!
           long size=VRSIOUtil.streamCopy(transfer,this,targetLocalFile,len,VFS.DEFAULT_STREAM_COPY_BUFFER_SIZE);
           //Global.debugPrintf(this,"Actual nr of bytes transferred=%d\n",size);
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }    

}
