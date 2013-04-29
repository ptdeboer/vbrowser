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

import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vfs.FileSystemNode;
import nl.nlesc.vlet.vrs.vfs.VDir;
import nl.nlesc.vlet.vrs.vfs.VFSNode;
import nl.nlesc.vlet.vrs.vfs.VFile;
import nl.nlesc.vlet.vrs.vrl.VRL;

/**
 * Example Skeleton FileSystemServer implementation See Super Class
 * FileSystemNode methods for default implementation.
 */
public class SkelFS extends FileSystemNode
{
    // ========================================================================
    // Instance
    // ========================================================================

    public SkelFS(VRSContext context, ServerInfo info, VRL location)
    {
        super(context, info);

        // Optionally perform extra initaliation here.
        // get grid proxy:
        // context.getGridProxy()
    }

    @Override
    public VDir newDir(VRL path) throws VrsException
    {
        // VDir factory method:
        // new VDir object: path doesn't have to exist, just create the (VDir)
        // object.
        return new SkelDir(this, path);
    }

    @Override
    public VFile newFile(VRL path) throws VrsException
    {
        // VFile factory method:
        // new VFile object: path doesn't have to exist, just create the (VFile)
        // object.
        return new SkelFile(this, path);
    }

    @Override
    public SkelDir getDir(VRL path) throws VrsException
    {
        // Open filepath and return new VDir object.
        // (remote) directory must exist.
        SkelDir dir = new SkelDir(this, path);

        // openDir() must return existing directory:
        if (dir.exists() == false)
            throw new nl.nlesc.vlet.exception.ResourceNotFoundException("Directory doesn't exists:" + dir);

        return dir;
    }

    @Override
    public SkelFile getFile(VRL path) throws VrsException
    {
        // Open filepath and return new VFile object.
        // (remote) file must exist.
        SkelFile file = new SkelFile(this, path);

        // openFile() must return existing file:
        if (file.exists() == false)
            throw new nl.nlesc.vlet.exception.ResourceNotFoundException("File doesn't exists:" + file);

        return file;
    }

    public void connect() throws VrsException
    {
        // connect if not connected yet, or ignore if not applicable.
        // multiple connect() calls are possible. Ignore if this happens.
    }

    public void disconnect() throws VrsException
    {
        // disconnect if applicable or ignore.
        // multiple disconnect() are allow. Ignore if this happens.
    }

    public boolean isConnected()
    {
        return true;
    }

    @Override
    public VFSNode openLocation(VRL vrl) throws VrsException
    {
        // Master 'openLocation' which connects to remote resource.
        if (isFile(vrl.getPath()))
        {
            return new SkelFile(this, vrl);
        }
        else if (isDir(vrl.getPath()))
        {
            return new SkelDir(this, vrl);
        }

        throw new nl.nlesc.vlet.exception.ResourceNotFoundException("Don't know what this is:" + vrl);
    }

    // ========================================================================
    // Filesystem helper methods:
    // ========================================================================

    public long getLength(String path)
    {
        return 0;
    }

    public long getModificationTime(String path)
    {
        // -1=unknown. Return in seconds after EPOCH.
        return -1;
    }

    public boolean isFile(String path)
    {
        // Helper method to check whether the path is a directory.
        return false;
    }

    public boolean isDir(String path)
    {
        return false;
    }

    public boolean delete(String path, boolean force, boolean recurse)
    {
        return false;
    }

    public boolean exists(String path, boolean isDirectory)
    {
        // check whether path exists and is a directory.
        return false;
    }

    public boolean hasReadAccess(String path)
    {
        // Return whether current user can read and/or has access ot this
        // this file or directory.
        return false;
    }

    public boolean hasWriteAccess(String path)
    {
        // return whether current user can write this file/directory
        return false;
    }

    public VRL rename(String originalPath, String newPath, boolean renameFullPath)
    {
        return null;
    }

    public boolean mkdir(String path, boolean force)
    {
        return false;
    }

    public boolean createFile(String path, boolean ignoreExisting)
    {
        // create empty file.
        return false;
    }

    public InputStream createNewInputstream(String path)
    {
        return null;
    }

    public OutputStream createNewOutputstream(String path)
    {
        return null;
    }

}
