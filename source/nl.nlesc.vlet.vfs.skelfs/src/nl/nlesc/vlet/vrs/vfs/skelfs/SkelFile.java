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

package nl.nlesc.vlet.vrs.vfs.skelfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrs.vfs.VFSTransfer;
import nl.nlesc.vlet.vrs.vfs.VFile;
import nl.nlesc.vlet.vrs.vrl.VRL;

/**
 * Example Skeleton VFile.
 * 
 */
public class SkelFile extends VFile
{
    public SkelFile(SkelFS fs, VRL vrl)
    {
        super(fs, vrl);
    }

    public SkelFile(SkelFS skelfs, String path) throws VlException
    {
        this(skelfs, skelfs.resolvePath(path));
    }

    public boolean create(boolean ignoreExisting) throws VlException
    {
        return getFS().createFile(getPath(), ignoreExisting);
    }

    @Override
    public long getLength() throws IOException
    {
        return getFS().getLength(this.getPath());
    }

    @Override
    public long getModificationTime() throws VlException
    {
        return getFS().getModificationTime(this.getPath());
    }

    @Override
    public boolean isReadable() throws VlException
    {
        return this.getFS().hasReadAccess(this.getPath());
    }

    @Override
    public boolean isWritable() throws VlException
    {
        return this.getFS().hasWriteAccess(this.getPath());
    }

    public InputStream createInputStream() throws IOException
    {
        return this.getFS().createNewInputstream(getPath());
    }

    public OutputStream createOutputStream() throws IOException
    {
        return this.getFS().createNewOutputstream(getPath());
    }

    public VRL rename(String newName, boolean renameFullPath) throws VlException
    {
        return this.getFS().rename(getPath(), newName, renameFullPath);
    }

    public boolean delete() throws VlException
    {
        return this.getFS().delete(this.getPath(), true, false);
    }

    @Override
    public boolean exists() throws VlException
    {
        return this.getFS().exists(this.getPath(), false);
    }

    // ===
    // Optimization methods
    // Override the following methods if this File implementation
    // can do them faster
    // ===
    protected void uploadFrom(VFSTransfer transferInfo, VFile localSource) throws VlException
    {
        // localSource is a file on the local filesystem.
        super.uploadFrom(transferInfo, localSource);

    }

    protected void downloadTo(VFSTransfer transfer, VFile targetLocalFile) throws VlException
    {
        // copy contents into local file:
        super.downloadTo(transfer, targetLocalFile);
    }

    // explicit downcast:
    protected SkelFS getFS()
    {
        // downcast from VFileSystem interface to actual (Skeleton) FileSystem
        // object.
        return ((SkelFS) this.getFileSystem());
    }
}
