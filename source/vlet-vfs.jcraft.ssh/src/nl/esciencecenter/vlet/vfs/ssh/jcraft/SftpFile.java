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

package nl.esciencecenter.vlet.vfs.ssh.jcraft;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;
import nl.esciencecenter.vlet.vrs.vfs.VFSTransfer;
import nl.esciencecenter.vlet.vrs.vfs.VFile;
import nl.esciencecenter.vlet.vrs.vfs.VUnixFileAttributes;

import com.jcraft.jsch.SftpATTRS;

public class SftpFile extends VFile implements VUnixFileAttributes
{
    /** Currently SFTP can NOT handle stream read/write > 3200 per read/write */

    private static final int sftpChunksize = 32000;

    SftpFileSystem server = null;

    // holder class so value can be changed (need better solution)
    SftpATTRS _attrs;

    private void init(SftpFileSystem server, String path)
    {
        this.server = server;
        _attrs = null;
    }

    SftpFile(SftpFileSystem server, VRL vrl)
    {
        super(server, vrl);
        init(server, vrl.getPath());
    }

    SftpFile(SftpFileSystem server, String path)
    {
        super(server, new VRL(VRS.SFTP_SCHEME, null, server.getHostname(), server.getPort(), path));
        init(server, path);
    }

    public boolean create(boolean force) throws VrsException
    {
        VFile file = this.server.createFile(getPath(), force);
        return (file != null);
    }

    @Override
    public void uploadFrom(VFSTransfer transfer, VFile source) throws VrsException
    {
        // Paranoia:
        if (source.isLocal() == false)
        {
            throw new VrsException(
                    "Internal error cmoveFromLocal didn't receive a local file:"
                            + source);
        }
        
        String sftpFilepath = this.getPath();
        String localfilepath = source.getPath();

        // perform upload

        server.uploadFile(transfer, localfilepath, sftpFilepath);
    }

    @Override
    public void downloadTo(VFSTransfer transfer, VFile localFile) throws VrsException
    {
        if (localFile.isLocal() == false)
        {
            throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException(" Destination is not local:" + localFile);
        }

        String targetPath = localFile.getPath();

        this.server.downloadFile(transfer, this.getPath(), targetPath);
    }

    @Override
    public boolean exists() throws VrsException
    {
        return server.existsPath(this.getPath(), false);
    }

    @Override
    public boolean isReadable() throws VrsException
    {
        return server.isReadable(getPath());
    }

    @Override
    public boolean isWritable() throws VrsException
    {
        return server.isWritable(getPath());
    }

    @Override
    public boolean isSymbolicLink() throws VrsException
    {
        return server.isLink(getPath());
    }

    @Override
    public String getSymbolicLinkTargetPath() throws VrsException
    {
        return server.getLinkTarget(getPath());
    }

    public VRL rename(String newName, boolean nameIsPath) throws VrsException
    {
        String newpath = server.rename(getPath(), newName, nameIsPath);
        return this.resolvePath(newpath);
    }

    public boolean delete() throws VrsException
    {
        return server.delete(this.getPath(), false);
    }

    // ========================================================================
    // VStream[Readable|Writable]
    // ========================================================================

    public InputStream createInputStream() throws IOException
    {
        // redirect to server to synchronise file access:
        try
        {
            return server.createInputStream(getPath());
        }
        catch (VrsException e)
        {
            throw new IOException(e);
        }
    }

    public OutputStream createOutputStream() throws IOException
    {
        try
        {
            // redirect to server to synchronise file access:
            return server.createOutputStream(getPath(), false);
        }
        catch (VrsException e)
        {
            throw new IOException(e);
        }
    }

    public OutputStream createOutputStream(boolean append) throws IOException
    {
        try
        {
            // redirect to server to synchronise file access:
            return server.createOutputStream(getPath(), append);
        }
        catch (VrsException e)
        {
            throw new IOException(e);
        }

    }

    // @Override
    public void setLengthToZero() throws IOException
    {
        try
        {
            server.delete(this.getPath(), false);
            server.createFile(this.getPath(), true);
        }
        catch (VrsException e)
        {
            throw new IOException(e);
        }

        // throw new NotImplementedException("Not implemented yet");
    }

    //
    // Bug: For some reason the outputstream of Jsch can only handle 32000 bytes
    //
    public void streamWriteOld(byte[] buffer, int bufferOffset, int nrBytes) throws VrsException
    {
        try
        {
            OutputStream outps = createOutputStream();

            // must currently write in 32000 byte sized chunks
            int chunksize = sftpChunksize;

            // write in chunks:
            for (int i = 0; i < nrBytes; i += chunksize)
            {
                if (i + chunksize > nrBytes)
                {
                    chunksize = nrBytes - i;
                }

                outps.write(buffer, i, chunksize);

            }

            outps.close();
        }
        catch (IOException e)
        {
            throw new NestedIOException("Couldn't write to stream:" + this, e);
        }
    }

    @Override
    public Attribute[][] getACL() throws VrsException
    {
        return server.getACL(getPath(), false);
    }

    @Override
    public void setACL(Attribute acl[][]) throws VrsException
    {
        server.setACL(getPath(), acl, true);
    }

    @Override
    public long getModificationTime() throws VrsException
    {
        return this.server.getModificationTime(getSftpAttributes());
    }

    private SftpATTRS getSftpAttributes() throws VrsException
    {
        if (_attrs == null)
        {
            _attrs = server.getSftpAttrs(getPath());
        }
        return _attrs;
    }

    public String getPermissionsString() throws VrsException
    {
        return server.getPermissionsString(getSftpAttributes(), false);
    }

    public int getMode() throws VrsException
    {
        return server.getPermissions(getSftpAttributes());
    }

    @Override
    public long getLength() throws IOException
    {
        try
        {
            return server.getLength(getSftpAttributes());
        }
        catch (VrsException e)
        {
            throw new IOException(e);
        }
    }

    /** 
     * Returns all default attributes names 
     */
    public String[] getAttributeNames()
    {
        String superNames[] = super.getAttributeNames();

        return StringList.merge(superNames, SftpFSFactory.sftpFileAttributeNames);
    }

    public Attribute getAttribute(String name) throws VrsException
    {
        if (name == null)
            return null;

        // update attributes:
        Attribute attr = this.getStaticAttribute(name);
        if (attr != null)
            return attr;

        // if EXISTS attribute is asked, do not get attributes

        if (VAttributeConstants.ATTR_EXISTS.equals(name))
        {
            return new Attribute(name, exists());
        }

        attr = server.getAttribute(this, this.getSftpAttributes(), name, false, true);

        if (attr != null)
        {
            return attr;
        }
        
        return super.getAttribute(name);
    }

    public Attribute[] getAttributes(String names[]) throws VrsException
    {
        if (names == null)
            return null;

        // optimized Gftp Attributes:

        Attribute[] vattrs = server.getAttributes(this, this.getSftpAttributes(), names, false);

        for (int i = 0; i < names.length; i++)
        {
            // get attribute which SftpAttrs don't have:
            if (vattrs[i] == null)
            {
                vattrs[i] = super.getAttribute(names[i]);
            }
        }

        return vattrs;
    }

    public void setMode(int mode) throws VrsException
    {
    }

    public String getGid() throws VrsException
    {
        return "" + getSftpAttributes().getGId();
    }

    public String getUid() throws VrsException
    {
        return "" + getSftpAttributes().getUId();
    }

    public boolean sync() throws VrsException
    {
        // just nullify attributes. Will be fetched again/
        this._attrs = null;
        return true;
    }

}
