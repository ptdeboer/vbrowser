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

package nl.esciencecenter.vlet.vrs.vfs;

import java.io.IOException;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.io.VSize;
import nl.esciencecenter.vlet.vrs.io.VStreamAccessable;

/**
 * The Virtual File Interface. An abstract representation of a File.
 * Exposes common File methods.  
 * <p>
 * To get an VFile object, use a {@link VFSClient} and use {@link VFSClient#getFile(VRL)}.
 * 
 * @see VFSClient
 * @see VFSNode
 * @see VDir
 * @see VFileSystem
 * 
 * @author P.T. de Boer
 */
public abstract class VFile extends VFSNode implements VSize,VStreamAccessable //, VRandomAccessable
{
    
    public VFile(VFileSystem vfs,VRL vrl)
    {
        super(vfs,vrl);
    }

    @Override 
    public String getResourceType()
    {
        return VFS.FILE_TYPE;
    }

    /**
     * Returns true.
     * @see VFSNode#isFile 
     */
    public boolean isFile()
    {
        return true;
    };

    /**
     * Returns false. 
     * @see VFSNode#isDir
     */
    public boolean isDir()
    {
        return false;
    };
    
    /**
     * Copy this file to the remote directory. Method will overwrite existing destination file.  
     * @throws VrsException
     */
    final public VFile copyTo(VDir parentDir) throws VrsException
    {
      //return (VFile)doCopyMoveTo(parentDir,null,false /*,options*/);
        return (VFile)getTransferManager().doCopyMove(this,parentDir,null,false); 
    }
    
    /**
     * Copy this file to the designated TargetFile. 
     * @throws VrsException
     */
    final public VFile copyTo(VFile targetFile) throws VrsException
    {
      //return (VFile)doCopyMoveTo(parentDir,null,false /*,options*/);
        return (VFile)getTransferManager().doCopyMove(this,targetFile,false);  
    }

    /**
     * Move this file to the designated TargetFile. 
     * @throws VrsException
     */
    final public VFile moveTo(VFile targetFile) throws VrsException
    {
        return (VFile)getTransferManager().doCopyMove(this,targetFile,true);  
    }
    
    /**
     * Copy to remote directory. Method will overwrite existing destination file.  
     * Parameter newName is optional new name of remote file.
     * @throws VrsException
     */
    final public VFile copyTo(VDir parentDir,String newName) throws VrsException
    {
        return (VFile)getTransferManager().doCopyMove(this,parentDir,newName,false);
        //return (VFile)doCopyMoveTo(parentDir,newName,false /*,options*/);
    }

    /**
     * Move files to remote directory. Will overwrite existing files. 
     * @throws VrsException
     */
    final public VFile moveTo(VDir parentDir) throws VrsException
    {
        return (VFile)getTransferManager().doCopyMove(this,parentDir,null,true); 
        //return (VFile)doCopyMoveTo(parentDir, null,true);
    }

    /**
     * Move file top remote directory.  Method will overwrite existing destination file.  
     * Parameter newName is optional new name of remote file.
     * @throws VrsException
     */
    final public VFile moveTo(VDir parentDir,String newName) throws VrsException
    {
        return (VFile)getTransferManager().doCopyMove(this,parentDir,newName,true); 
        //return (VFile)doCopyMoveTo(parentDir, newName,true);
    }
  
    /** 
     * Default method to upload a file from a local location. 
     * Override this method if the implementation can provide a optimized
     * upload method (striped upload and/or bulk mode transfers) 
     * 
     * @param transferInfo
     * @param localSource   local file to upload from.  
     * @throws VrsException
     */
    protected void uploadFrom(VFSTransfer transferInfo, VFile localSource) throws VrsException 
    {
        // copy contents into this file. 
        getTransferManager().doStreamCopy(transferInfo, localSource,this); 
    }

    /**
     * Default method to download this file to a (the) local destination. 
     * <br>
     * Sub classes are encouraged to override this method if they have
     * their own (better) methods. for example bulk mode or striped file
     * transfers. 
     *
     * @see VFile#uploadFrom(VFSTransfer, VFile)
     * @param targetLocalFile The local destination file. 
     */ 

    protected void downloadTo(VFSTransfer transfer,VFile targetLocalFile)
            throws VrsException
    {
        // copy contents into local file:
        getTransferManager().doStreamCopy(transfer,this,targetLocalFile);  
    }

    // ========================================================================
    // Extra VFile Abstract Interface Methods 
    // ========================================================================

    // Explicit inheritance definitions from VFSNode.  
    abstract public boolean exists() throws VrsException; 

    // Explicit inheritance definitions from VFSNode.  
    public abstract long getLength() throws IOException;


}
