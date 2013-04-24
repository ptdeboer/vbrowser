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

package nl.nlesc.vlet.vfs.gftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.io.VRandomAccessable;
import nl.nlesc.vlet.vrs.io.VStreamReadable;
import nl.nlesc.vlet.vrs.io.VStreamWritable;
import nl.nlesc.vlet.vrs.io.VZeroSizable;
import nl.nlesc.vlet.vrs.vfs.VChecksum;
import nl.nlesc.vlet.vrs.vfs.VDir;
import nl.nlesc.vlet.vrs.vfs.VFSTransfer;
import nl.nlesc.vlet.vrs.vfs.VFile;
import nl.nlesc.vlet.vrs.vfs.VFileActiveTransferable;
import nl.nlesc.vlet.vrs.vrl.VRL;


import org.globus.ftp.MlsxEntry;

/**
 * Implementation of GridFTP File.
 * 
 * @author P.T. de Boer
 */
public class GftpFile extends VFile implements VStreamReadable, VStreamWritable, VRandomAccessable, VZeroSizable,
        VFileActiveTransferable, VChecksum // VSizeAdjustable
{
    /** GridServer handler */
    protected GftpFileSystem gftpServer = null;

    /**
     * Cached MslxEntry: use only for optimization
     */
    private MlsxEntry _entry = null;

    protected GftpFile(GftpFileSystem server, String path, MlsxEntry entry) throws VlException
    {
        super(server, server.getServerVRL().replacePath(path));
        init(server, path, entry);
        // keep private copy of filesystemserver:
        this.gftpServer = server;
    }

    protected GftpFile(GftpFileSystem server, String path) throws VlException
    {
        this(server, path, null);
    }

    private void init(GftpFileSystem server, String path, MlsxEntry entry) throws VlException
    {
        this.gftpServer = server;
        this._entry = entry;
    }

    @Override
    protected void downloadTo(VFSTransfer transfer, VFile targetFile) throws VlException
    {
        // extra check:
        if (targetFile.isLocal() == false)
        {
            throw new nl.nlesc.vlet.exception.ResourceTypeMismatchException("Destination is NOT a local Directory:"
                    + targetFile);
        }

        gftpServer.downloadFile(transfer, this.getPath(), targetFile.getPath());
    }

    /** Globus GridFTP has efficient upload methods */
    @Override
    public void uploadFrom(VFSTransfer transfer, VFile file) throws VlException
    {
        //debugPrintf("uploadFrom:'%s' to '%s'\n", file, this);

        // Paranoia:
        if (file.isLocal() == false)
        {
            throw new VlException("Internal error uploadFrom didn't receive a local file:" + file);
        }

        String localpath = file.getPath();
        String destPath = this.getPath();

        // perform upload
        gftpServer.uploadFile(transfer, localpath, destPath);

        // Update !!
        updateMlsxEntry();
    }

    public boolean exists() throws VlException
    {
        return this.gftpServer.existsFile(this.getPath());
    }

    public boolean isReadable() throws VlException
    {
        // nasty, to check whether directory is readable,
        // we have to know whether we are remote owner, group or nothing !

        // a directory is 'Readable' when it can r-x permissions !
        return GftpFileSystem._isReadable(getMlsxEntry());
    }

    @Override
    public boolean isWritable() throws VlException
    {
        return GftpFileSystem._isWritable(getMlsxEntry());
    }

    @Override
    public VRL rename(String newName, boolean nameIsPath) throws VlException
    {
        String path = gftpServer.rename(this.getPath(), newName, nameIsPath);
        return this.resolvePath(path);
    }

    /**
     * returns new GridFTPClient !
     */
    public VDir getParentDir() throws VlException
    {
        return gftpServer.getParentDir(this.getPath());
    }

    public boolean delete() throws VlException
    {
        return gftpServer.delete(false, this.getPath());
    }

    @Override
    public long getLength() throws IOException
    {
        try
        {
            updateMlsxEntry();
            return GftpFileSystem._getLength(getMlsxEntry());
        }
        catch (VlException e)
        {
            throw new IOException(e.getMessage(),e); 
        }
    }

    public long getModificationTime() throws VlException
    {
        updateMlsxEntry();
        return GftpFileSystem._getModificationTime(getMlsxEntry());
    }

    public InputStream createInputStream() throws IOException
    {
        return gftpServer.createInputStream(this.getPath());
    }

    public OutputStream createOutputStream() throws IOException
    {
        return gftpServer.createOutputStream(this.getPath());
    }

    // testing purposes
    public GftpFileSystem getServer()
    {
        return this.gftpServer;
    }

    public boolean create(boolean force) throws VlException
    {
        String filePath = getPath();
        OutputStream outps = null;

        try
        {

            // create outputstream to write to:
            outps = createOutputStream();
            outps.write(new byte[0]);
            outps.close();
            outps=null;
        }
        catch (Exception e)
        {
            // Close InputStream !
            if (outps != null)
            {
                try
                {
                    outps.close();
                }
                catch (IOException e1)
                {
                    ; // ignore 
                }
            }

            // Post Creation Exception Handling to satisfy JUnit test !
            if (this.gftpServer.existsDir(filePath))
            {
                throw new nl.nlesc.vlet.exception.ResourceAlreadyExistsException(
                        "Resource already exists but is a directory:" + getPath(), e);
            }

            if (e instanceof IOException)
                throw new VlIOException((IOException) e);
            else if (e instanceof VlException)
                throw ((VlException) e);
            else
                throw new VlException(e);
        }

        // Update !
        this.updateMlsxEntry();
        return true;
    }

    public void setLengthToZero() throws IOException
    {
        try
        {
            this.delete();
            this.create();
            this._entry = null; // clear entry!
        }
        catch (VlException e)
        {
            throw new IOException(e.getMessage(),e); 
        }
    }

    public int readBytes(long fileOffset, byte[] buffer, int bufferOffset, int nrBytes) throws IOException
    {
        return this.gftpServer.syncRead(this.getPath(), fileOffset, buffer, bufferOffset, nrBytes);
    }

    public void writeBytes(long fileOffset, byte[] buffer, int bufferOffset, int nrBytes) throws IOException
    {
        try
        {
            this.gftpServer.syncWrite(this.getPath(), fileOffset, buffer, bufferOffset, nrBytes);
            this.updateMlsxEntry();
        }
        catch (VlException e)
        {
            throw new IOException(e.getMessage(),e);
        }
    }

    public String[] getAttributeNames()
    {
        String superNames[] = super.getAttributeNames();

        if (this.gftpServer.protocol_v1)
            return superNames;

        return StringList.merge(superNames, GftpFSFactory.gftpAttributeNames);

    }

    @Override
    public VAttribute getAttribute(String name) throws VlException
    {
        return getAttribute(this.getMlsxEntry(), name);
    }

    // Optimized implementation.
    // updated mslxEntry only once when getting multiple attributes

    @Override
    public VAttribute[] getAttributes(String names[]) throws VlException
    {
        VAttribute attrs[] = new VAttribute[names.length];

        // Optimized getAttribute: use single entry for all
        MlsxEntry entry = this.getMlsxEntry();

        for (int i = 0; i < names.length; i++)
        {
            attrs[i] = getAttribute(entry, names[i]);
        }

        return attrs;
    }

    /**
     * Optimized method. When fetching multiple attributes, do not refetch the
     * mlsxentry for each attribute.
     * 
     * @param name
     * @param update
     * @return
     * @throws VlException
     */
    public VAttribute getAttribute(MlsxEntry entry, String name) throws VlException
    {
        // is possible due to optimization:

        if (name == null)
            return null;

        // get Gftp specific attribute and update
        // the mslxEntry if needed

        VAttribute attr = GftpFileSystem.getAttribute(entry, name);

        if (attr != null)
            return attr;

        return super.getAttribute(name);
    }

    protected MlsxEntry updateMlsxEntry() throws VlException
    {
        _entry = this.gftpServer.mlst(this.getPath());
        return _entry;
    }

    public MlsxEntry getMlsxEntry() throws VlException
    {
        if (_entry == null)
            updateMlsxEntry();
        return _entry;
    }

    public boolean canTransferTo(VRL remoteLocation, StringHolder explanation) throws VlException
    {
        String remoteScheme = remoteLocation.getScheme();

        remoteScheme = this.vrsContext.getDefaultScheme(remoteScheme);

        if (StringUtil.compareIgnoreCase(remoteScheme, VRS.GFTP_SCHEME) == 0)
        {
            explanation.value = "Can perform Third Party transfers between GridFTP locations.";
            return true;
        }
        else
        {
            explanation.value = "Can only perform Third Party transfers between GridFTP locations.";
            return false;
        }
    }

    public boolean canTransferFrom(VRL remoteLocation, StringHolder explanation) throws VlException
    {
        String remoteScheme = remoteLocation.getScheme();

        remoteScheme = this.vrsContext.getDefaultScheme(remoteScheme);

        if (StringUtil.compareIgnoreCase(remoteScheme, VRS.GFTP_SCHEME) == 0)
        {
            explanation.value = "Can perform Third Party transfers between GridFTP locations.";
            return true;
        }
        else
        {
            explanation.value = "Can only perform Third Party transfers between GridFTP locations.";
            return false;
        }
    }

    public VFile activePartyTransferTo(ITaskMonitor monitor, VRL remoteDestination) throws VlException
    {
        //logger.infoPrintf(this, ">>> Performing VThirdPartyTransfer(): %s ==> %s\n", this, remoteDestination);

        return this.gftpServer.do3rdPartyTransferToOther(monitor, this, remoteDestination);
    }

    public VFile activePartyTransferFrom(ITaskMonitor monitor, VRL remoteSource) throws VlException
    {
        //logger.infoPrintf(this, ">>> Performing VThirdPartyTransfer(): %s <<= %s\n", this, remoteSource);
        return this.gftpServer.do3rdPartyTransferFromOther(monitor, remoteSource, this);
    }

    public String getChecksum(String algorithm) throws VlException
    {
        String[] types = getChecksumTypes();
        
        for (int i = 0; i < types.length; i++)
        {
            if (algorithm.equalsIgnoreCase(types[i]))
            {

                algorithm = algorithm.toUpperCase();
                try
                {
                    return gftpServer.getChecksum(algorithm, 0, this.getLength(), this.getPath());
                }
                catch (IOException e)
                {
                    throw new VlException(e.getMessage(),e); 
                }
            }
        }
        
        throw new nl.nlesc.vlet.exception.NotImplementedException(algorithm + " Checksum algorithm is not implemented ");

    }

    public String[] getChecksumTypes()
    {
        return new String[]
        { VChecksum.MD5 };
    }
}
