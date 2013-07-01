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

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.vrs.vfs.VDir;
import nl.nlesc.vlet.vrs.vfs.VFSNode;

/**
 * Minimal implementation of the VDir class.
 */
public class SkelDir extends VDir
{

    public SkelDir(SkelFS skelfs, VRL vrl)
    {
        super(skelfs, vrl);
    }

    public SkelDir(SkelFS skelfs, String path) throws VrsException
    {
        this(skelfs, skelfs.resolvePath(path));
    }

    @Override
    public boolean create(boolean force) throws VrsException
    {
        return this.getFS().mkdir(this.getPath(), force);
    }

    @Override
    public boolean exists() throws VrsException
    {
        return this.getFS().exists(this.getPath(), true);
    }

    @Override
    public VFSNode[] list() throws VrsException
    {
        // >>>
        // This method creates a dummy list of nodes!
        // >>>

        VFSNode nodes[] = new VFSNode[3];

        String[] paths = this.getVRL().getPathElements();

        nodes[0] = new SkelDir(this.getFileSystem(), this.resolvePath("SK-Dir#" + paths.length));
        nodes[1] = new SkelFile(this.getFileSystem(), this.resolvePath("SK-file1"));
        nodes[2] = new SkelFile(this.getFileSystem(), this.resolvePath("SK-file2"));

        return nodes;
    }

    public SkelFS getFileSystem()
    {
        // Downcast from VFileSystem interface to actual FileSystem object.
        return (SkelFS) super.getFileSystem();
    }

    @Override
    public long getModificationTime() throws VrsException
    {
        // Example: Return current time (for testing), replace with actual
        // modification time
        // of file.
        return System.currentTimeMillis();
    }

    @Override
    public boolean isReadable() throws VrsException
    {
        // check user accessibility.
        return this.getFS().hasReadAccess(this.getPath());
    }

    @Override
    public boolean isWritable() throws VrsException
    {
        // check user whether user has the rights to change this file.
        return this.getFS().hasWriteAccess(this.getPath());
    }

    public long getNrOfNodes() throws VrsException
    {
        // count number of nodes. Faster implementation is recommended.
        VFSNode[] files = this.list();

        if (files == null)
            return 0;

        return files.length;
    }

    public VRL rename(String newName, boolean renameFullPath) throws VrsException
    {
        // Perform rename. This can be a full path rename or a relative (path)
        // rename.
        return this.getFS().rename(getPath(), newName, renameFullPath);
    }

    public boolean delete(boolean recurse) throws VrsException
    {
        return this.getFS().delete(this.getPath(), true, recurse);
    }

    protected SkelFS getFS()
    {
        return ((SkelFS) this.getFileSystem());
    }

}
