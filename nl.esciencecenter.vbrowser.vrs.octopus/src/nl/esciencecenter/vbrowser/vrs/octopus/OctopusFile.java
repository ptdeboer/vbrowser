/*
 * (C) 2013 Netherlands eScience Center. 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 */ 
// source: 

package nl.esciencecenter.vbrowser.vrs.octopus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.octopus.exceptions.AttributeNotSupportedException;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.files.FileAttributes;
import nl.esciencecenter.octopus.files.Path;
import nl.uva.vlet.exception.ResourceAlreadyExistsException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFSTransfer;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;

/** 
 * 
 */
public class OctopusFile extends VFile
{
    private FileAttributes fileAttrs;
    private Path octoPath;

    public OctopusFile(OctopusFS octopusFS, FileAttributes attrs, Path path)
    {
       super(octopusFS,octopusFS.createVRL(path));
       this.fileAttrs=attrs; 
       this.octoPath=path;
    }

    /** 
     * Get file attributes, if file does not exists
     * @param update
     * @return
     * @throws VlException 
     */
    public FileAttributes getAttrs(boolean update) throws VlException
    {
        try
        {
            if ((fileAttrs==null) || (update==true))
            {
                fileAttrs=getOctoClient().getFileAttributes(octoPath);
            }
            return fileAttrs; 
        }
        catch (OctopusException e)
        {
            // Check for File Not Found Here !
            throw new VlException(e.getMessage(),e); 
        } 
    }
    
    public boolean sync() throws VlException
    {
        // Caching: do not sync non-existing files 
        if (this.fileAttrs==null) 
            if (exists()==false)
                return true; 
        
        getAttrs(true); 
        return (this.fileAttrs!=null); 
    }
    
    public boolean create(boolean ignoreExisting) throws VlException
	{
        // existing attributes -> file exist. 
        if (exists()==true) 
        {
            if (ignoreExisting)
            {
                return true; // existing file. 
            }
            else
            {
                throw new ResourceAlreadyExistsException("File already exists:"+this); 
            }
        }
            
		try
        {
	        // Path is immutable, update it here ? 
            Path newPath = this.getOctoClient().createFile(octoPath);
            return true;
        }
        catch (OctopusException e)
        {
           throw new VlException(e.getMessage(),e); 
        }
	}
	
	@Override
	public long getLength() throws IOException 
	{
	    try
        {
            return getFS().getLength(getAttrs(false),-1);
        }
        catch (VlException e)
        {
            throw new IOException(e.getMessage(),e); 
        } 
	}

	@Override
	public long getModificationTime() throws VlException
	{
	    return getFS().getModificationTime(getAttrs(false),System.currentTimeMillis());
	}

	@Override
	public boolean isReadable() throws VlException 
	{
	    try
        {
            return getAttrs(false).isReadable();
        }
        catch (AttributeNotSupportedException e)
        {
            throw new VlException(e.getMessage(),e); 
        }
	}

	@Override
	public boolean isWritable() throws VlException
	{
	    return getFS().isWritable(getAttrs(false),false); 
	}

	public InputStream getInputStream() throws IOException
	{
		return this.getOctoClient().createInputStream(octoPath); 
	}

	public OutputStream getOutputStream() throws IOException 
	{
	    return this.getOctoClient().createOutputStream(octoPath); 
	}

	public VRL rename(String newName, boolean renameFullPath)
		throws VlException
	{
	    throw new VlException("Not Implemented:rename"); 
	}

	public boolean delete() throws VlException
	{
		try
        {
            boolean result = this.getOctoClient().deleteFile(octoPath,true);
            // clear attributes to indicate non existinf file! 
            this.fileAttrs=null; 
            return result; 
        }
        catch (OctopusException e)
        {
            throw new VlException(e.getMessage(),e); 
        } 
	}		

	@Override
	public boolean exists() throws VlException
	{
	    try
        {
	        // Caching: if file attributes are already fetched, the file exists. 
	        if (this.fileAttrs!=null)
	        {
	            return fileAttrs.isRegularFile();
	        }
	        else
	        {
	            // call exists, do not fetch file attributes from a non existing file
	            // as this might throw an error.  
	            return this.getOctoClient().exists(octoPath); 
	        }
        }
	    catch (AttributeNotSupportedException e)
	    {
	        throw new VlException(e.getMessage(),e); 
	    }
        catch (OctopusException e)
        {
            throw new VlException(e.getMessage(),e); 
        }
	}

	// === 
	// Protected implementation 
	// === 
	
    protected void uploadFrom(VFSTransfer transferInfo, VFile localSource) throws VlException 
    {
        // localSource is a file on the local filesystem. 
        super.uploadFrom(transferInfo,localSource);          
    }

    protected void downloadTo(VFSTransfer transfer,VFile targetLocalFile)
    	throws VlException
    {
        // copy contents into local file:
        super.downloadTo(transfer, targetLocalFile); 
    }
    
    // explicit downcast: 
    protected OctopusFS getFS()
    {
    	// downcast from VFileSystem interface to actual (Skeleton) FileSystem object. 
    	return ((OctopusFS)this.getFileSystem()); 
    }
    
    protected OctopusClient getOctoClient()
    {
        return this.getFS().octoClient; 
    }

}
