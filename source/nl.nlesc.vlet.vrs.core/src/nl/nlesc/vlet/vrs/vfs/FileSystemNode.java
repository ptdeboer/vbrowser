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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.ResourceSystemNode;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;

/** 
 * FileSystemNode is a {@link VFileSystem} adaptor class, which extends the 
 * {@link ResourceSystemNode} and adds VFileSystem methods. 
 * It is a 'browsable' node which is linked to an actual FileSystem implementation. 
 * It is recommended that VFileSystem implementations extends this FileSystemNode. 
 * 
 * @author Piter T. de Boer
 */
public abstract class FileSystemNode extends ResourceSystemNode implements VFileSystem
{

	public FileSystemNode(VRSContext context, ServerInfo info)
	{
		super(context, info);
	}
	
	public VFSNode getNode(VRL vrl) throws VlException
	{
		return this.openLocation(vrl); 
	}
	
	public VFSNode openPath(VRL fileVrl) throws VlException
	{
		return getNode(fileVrl); 
	}
	
	public VFSNode getNode(String path) throws VlException
	{
		return openLocation(resolvePath(path));   
	}
	

	public VFSNode getPath(String path) throws VlException
	{
		return getNode(path); 
	}
	
	/**
     * Resolve relative or absolute path against this resource. Uses
     * VRL.resolvePaths(this.getPath(),subPath) as default implementation.
	 * @throws VlException,VRISyntaxException 
     * 
     * @throws VlException
     */
    public String resolvePathString(String path) throws VlException
    {
        return getVRL().resolvePathToVRL(path).getPath(); 
    }
   
    /**
     * Resolve path against this VRL and return resolved VRL. 
     * Only match path elements!
     */
    public VRL resolvePath(String path) throws VlException
    {
        return getLocation().resolvePathToVRL(path);
    }
    
	// implementations are encourage to override this method for speed
	public VDir getDir(VRL dirVrl) throws VlException
	{
		VFSNode node=this.getNode(dirVrl); 
		
		if (node==null)
			throw new nl.nlesc.vlet.exception.ResourceNotFoundException(" Couldn't get: "+dirVrl); 
		
		if ((node instanceof VDir)==false) 
			throw new nl.nlesc.vlet.exception.ResourceTypeMismatchException(" Resource is not a directory: "+ node); 
	   
		return (VDir)node; 
	}
	
	// implementations are encourage to override this method for speed
	public VFile getFile(VRL fileVrl) throws VlException
	{
		VFSNode node=this.getNode(fileVrl);
		
		if (node==null)
			throw new nl.nlesc.vlet.exception.ResourceNotFoundException(" Couldn't get: "+fileVrl); 
		
		if ((node instanceof VFile)==false) 
			throw new nl.nlesc.vlet.exception.ResourceTypeMismatchException(" Resource is not a file:"+fileVrl); 
	   
		return (VFile)node; 
	}
	
	/** Check whether the (remote) path exists and is actual an directory.*/ 
	public boolean existsDir(VRL dirVrl) throws VlException
	{
	    VlException e1=null; 
	    
	    try
	    {
	        return newDir(dirVrl).exists(); 
	    }
	    catch (VlException e)
	    {
	        e1=e; 
	    }

        // darn: could throw ResourceMismatch
        // try file: 

	    try
	    {
	        if (newFile(dirVrl).exists())
	            return false;
	    }
	    catch (VlException e)
	    {
	        ; //ignore 
	    }
	    
	    throw e1; // throw original exception  
	}
	
	/** Check wether the (remote) path exists and in an actual file.*/ 
	public boolean existsFile(VRL fileVrl) throws VlException
	{
		return newFile(fileVrl).exists(); 
	}

	/** Check wether the remote path exists. */ 
	public boolean existsPath(VRL fileVrl) throws VlException
	{
		if (newFile(fileVrl).exists())
			return true;
		
		if (newDir(fileVrl).exists())
			return true;
		
		return false; 
	}

	public InputStream openInputStream(VRL location) throws VlException
	{
		try
        {
            return newFile(location).getInputStream();
        }
		catch (IOException e)
		{
		    throw new VlIOException(e); 
        }
	}
	
	public OutputStream createOutputStream(VRL location) throws VlException
    {
        return openOutputStream(location);
    }

    public OutputStream openOutputStream(VRL location) throws VlException
	{
		try
        {
            return newFile(location).getOutputStream();
        }
		catch (IOException e)
        {
            throw new VlIOException(e); 
        }
	}

    /** Create Directory on this filesystem. */ 
	public VDir createDir(VRL dirVrl, boolean ignoreExisting) throws VlException
	{
		VDir dir=newDir(dirVrl); 

		if (dir.exists()==true)
		{
			if (ignoreExisting==false) 
			{
				throw new nl.nlesc.vlet.exception.ResourceAlreadyExistsException("Directory already exists:"+dir);
			}
			else
			{
				return dir; 
			}
		}
		// create new dir: 
		dir.create(ignoreExisting); 
		return dir; 
	}

	/** Create file on this filesystem */ 
	public VFile createFile(VRL fileVrl, boolean ignoreExisting) throws VlException
	{
		VFile file=newFile(fileVrl); 

		if (file.exists()==true)
		{
			if (ignoreExisting==false) 
			{
				throw new nl.nlesc.vlet.exception.ResourceAlreadyExistsException("File (or path) already exists:"+file);
			}
			else
			{
				return file;  
			}
		}
		// create new dir: 
		file.create(ignoreExisting); 
		return file;  
	}
	 
	public VFile newFile(String path) throws VlException
	{
		return newFile(resolvePath(path)); 
	}
	
	public VDir newDir(String path) throws VlException
	{
		return newDir(resolvePath(path)); 
	}
	
	public void dispose()
	{
	    
	}
	
	// ==================
	// Abstract interface 
	// ==================

	// Explicit declaration from VFileSystem the only method to implement
	abstract public VFile newFile(VRL fileVrl) throws VlException;

	// Explicit declaration from VFileSystem
	abstract public VDir newDir(VRL dirVrl) throws VlException;
	
	/** 
	 * Open Location and return VFS Resource. 
	 * The location must exist.
	 */
	public abstract VFSNode openLocation(VRL vrl) throws VlException;

}
