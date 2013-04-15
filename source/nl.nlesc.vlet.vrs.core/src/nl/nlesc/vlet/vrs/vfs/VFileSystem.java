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

package nl.nlesc.vlet.vrs.vfs;

import java.io.InputStream;
import java.io.OutputStream;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VResourceSystem;
import nl.nlesc.vlet.vrs.io.VStreamProducer;


/**
 * VFileSystem is a factory class for VFSNodes. 
 * Its purpose is to create new VFile or VDir objects. 
 * The interface is limited to factory methods. Use the returned VFile and VDir object(s)
 * for further file and directory manipulation. 
 * <br>
 * Use:<br>
 * <lu>
 *  <li> getFile() and getDir() to fetch existing files or directories.  
 *  <li> newFile and newDir() to create new VFile and VDir objects which may 
 *          point to existing locations or not. 
 *  <li> Use exists() and/or create() method from VDir and VFile to check existance
 *          and/or creation of the actual resources. 
 * </lu>
 *  
 * @author P.T. de Boer 
 * 
 * @see nl.nlesc.vlet.vrs.vfs.VFS
 * @see nl.nlesc.vlet.vrs.vfs.VFileSystem
 * @see VFSNode
 * @see VFile 
 * @see VDir
 * @see VFSClient 
 */
public interface VFileSystem extends VResourceSystem, VStreamProducer
{
    /** 
     * Resolve relative path or URI part to this FileSystem and return Absolute VRL.
     */ 
    public VRL resolvePath(String path) throws VlException;
    
    // Same as VResourceSystem but returns VFSNode type.  
    public VFSNode openLocation(VRL vrl) throws VlException;
    
    /**
     * Open filesystem path and return new VFile object. 
     * The (remote) file must exist.
     */
    public VFile getFile(VRL fileVRL) throws VlException;
    
    /**
     * Open filesystem path and return new VDir object. 
     * The (remote) directory must exist.
     */
    public VDir getDir(VRL dirVRL) throws VlException;
  
    /**
     * Generic VFile (object) constructor: Create new VFile object linked to this resource system. 
     * The actual file may or may not exist on the remote filesystem.<br>
     * Use VFile.exists() to check whether it exists or VFile.create() to create the actual
     * file on the (remote) resource. 
     */
    public VFile newFile(VRL fileVRL) throws VlException;

    /**
     * Generic VDir constructor: Create new VDir object linked to this (remote) filesystem. 
     * This object may exist or may not exist on the remote resource,<br>
     * Use openDir() to get an existing directory.  
     */
    public VDir newDir(VRL dirVRL) throws VlException;
    
    // Explicit declaration from VInputStreamProducer
    public InputStream openInputStream(VRL location) throws VlException; 

    // Explicit declaration from VOutputStreamProducer
    public OutputStream openOutputStream(VRL location) throws VlException; 

}
