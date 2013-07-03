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

package nl.esciencecenter.vlet.vfs.gftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.vrs.io.VRandomAccessable;
import nl.esciencecenter.vlet.vrs.io.VStreamReadable;
import nl.esciencecenter.vlet.vrs.io.VStreamWritable;
import nl.esciencecenter.vlet.vrs.vfs.VChecksum;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFSTransfer;
import nl.esciencecenter.vlet.vrs.vfs.VFile;

import org.globus.ftp.MlsxEntry;

/**
 * Implementation of GridFTP File.
 * 
 * @author P.T. de Boer
 */
public class GftpFile extends VFile implements VStreamReadable, VStreamWritable, VRandomAccessable, 
       VChecksum // VSizeAdjustable
{
    /** GridServer handler */
    protected GftpFileSystem gftpServer = null;

    /**
     * Cached MslxEntry: use only for optimization
     */
    private MlsxEntry _entry = null;

    protected GftpFile(GftpFileSystem server, String path, MlsxEntry entry) throws VrsException
    {
        super(server, server.getServerVRL().replacePath(path));
        init(server, path, entry);
        // keep private copy of filesystemserver:
        this.gftpServer = server;
    }

    protected GftpFile(GftpFileSystem server, String path) throws VrsException
    {
        this(server, path, null);
    }

    private void init(GftpFileSystem server, String path, MlsxEntry entry) throws VrsException
    {
        this.gftpServer = server;
        this._entry = entry;
    }

    @Override
    protected void downloadTo(VFSTransfer transfer, VFile targetFile) throws VrsException
    {
        // extra check:
        if (targetFile.isLocal() == false)
        {
            throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Destination is NOT a local Directory:"
                    + targetFile);
        }

        gftpServer.downloadFile(transfer, this.getPath(), targetFile.getPath());
    }

    /** Globus GridFTP has efficient upload methods */
    @Override
    public void uploadFrom(VFSTransfer transfer, VFile file) throws VrsException
    {
        //debugPrintf("uploadFrom:'%s' to '%s'\n", file, this);

        // Paranoia:
        if (file.isLocal() == false)
        {
            throw new VrsException("Internal error uploadFrom didn't receive a local file:" + file);
        }

        String localpath = file.getPath();
        String destPath = this.getPath();

        // perform upload
        gftpServer.uploadFile(transfer, localpath, destPath);

        // Update !!
        updateMlsxEntry();
    }

    public boolean exists() throws VrsException
    {
        return this.gftpServer.existsFile(this.getPath());
    }

    public boolean isReadable() throws VrsException
    {
        // nasty, to check whether directory is readable,
        // we have to know whether we are remote owner, group or nothing !

        // a directory is 'Readable' when it can r-x permissions !
        return GftpFileSystem._isReadable(getMlsxEntry());
    }

    @Override
    public boolean isWritable() throws VrsException
    {
        return GftpFileSystem._isWritable(getMlsxEntry());
    }

    @Override
    public VRL rename(String newName, boolean nameIsPath) throws VrsException
    {
        String path = gftpServer.rename(this.getPath(), newName, nameIsPath);
        return this.resolvePath(path);
    }

    /**
     * returns new GridFTPClient !
     */
    public VDir getParentDir() throws VrsException
    {
        return gftpServer.getParentDir(this.getPath());
    }

    public boolean delete() throws VrsException
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
        catch (VrsException e)
        {
            throw new IOException(e.getMessage(),e); 
        }
    }

    public long getModificationTime() throws VrsException
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

    public boolean create(boolean force) throws VrsException
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
                throw new nl.esciencecenter.vlet.exception.ResourceAlreadyExistsException(
                        "Resource already exists but is a directory:" + getPath(), e);
            }

            if (e instanceof IOException)
                throw new NestedIOException((IOException) e);
            else if (e instanceof VrsException)
                throw ((VrsException) e);
            else
                throw new VrsException(e);
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
        catch (VrsException e)
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
        catch (VrsException e)
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
    public Attribute getAttribute(String name) throws VrsException
    {
        return getAttribute(this.getMlsxEntry(), name);
    }

    // Optimized implementation.
    // updated mslxEntry only once when getting multiple attributes

    @Override
    public Attribute[] getAttributes(String names[]) throws VrsException
    {
        Attribute attrs[] = new Attribute[names.length];

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

    protected MlsxEntry updateMlsxEntry() throws VrsException
    {
        _entry = this.gftpServer.mlst(this.getPath());
        return _entry;
    }

    public MlsxEntry getMlsxEntry() throws VrsException
    {
        if (_entry == null)
            updateMlsxEntry();
        return _entry;
    }

   


    public String getChecksum(String algorithm) throws VrsException
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
                    throw new VrsException(e.getMessage(),e); 
                }
            }
        }
        
        throw new nl.esciencecenter.vlet.exception.NotImplementedException(algorithm + " Checksum algorithm is not implemented ");

    }

    public String[] getChecksumTypes()
    {
        return new String[]
        { VChecksum.MD5 };
    }
}
