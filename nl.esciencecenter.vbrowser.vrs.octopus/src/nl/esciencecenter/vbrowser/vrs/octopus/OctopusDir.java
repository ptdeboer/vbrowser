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

import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.files.FileAttributes;
import nl.esciencecenter.octopus.files.Path;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;

/**
 * Minimal implementation of the VDir class. 
 */
public class OctopusDir extends VDir
{
	private FileAttributes fileAttrs;
    private Path octoPath;

    public OctopusDir(OctopusFS skelfs, FileAttributes attrs, Path path)
	{
		super(skelfs, skelfs.createVRL(path));
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
    	
	@Override
	public boolean create(boolean force) throws VlException
	{
		try
        {
            this.getOctoClient().mkdir(octoPath,force);
        }
        catch (OctopusException e)
        {
            throw new VlException(e.getMessage(),e); 
        } 
		return true; // no exception -> true; 
	}
	
	@Override
	public boolean exists() throws VlException
	{
	    try
        {
            // if file attributes are already fetched the path exists, now check attributes. 
            if (this.fileAttrs!=null)
                return fileAttrs.isDirectory();
            else
            {
                // call exists, do not fetch file attributes from a non existing file
                // as this might throw an error.  
                return this.getOctoClient().exists(octoPath); 
            }
        }
        catch (OctopusException e)
        {
            throw new VlException(e.getMessage(),e); 
        }
	}
	
	@Override
	public VFSNode[] list() throws VlException 
	{
	    //return this.getFS().listNodes(octoPath); 
        return getFS().listNodesAndAttrs(octoPath); 
	}
	
	public OctopusFS getFileSystem()
	{
	    // Downcast from VFileSystem interface to actual FileSystem object. 
	    return (OctopusFS)super.getFileSystem(); 
	}
	
	@Override
	public long getModificationTime() throws VlException
	{
	    return getFS().getModificationTime(getAttrs(false),System.currentTimeMillis());
	}

	@Override
	public boolean isReadable() throws VlException 
	{
	    return getFS().isReadable(getAttrs(false),true);
	}

	@Override
	public boolean isWritable() throws VlException
	{
		return this.getFS().isWritable(getAttrs(false),false); 
	}

	public long getNrOfNodes() throws VlException
	{
		// count number of nodes. Faster implementation is recommended. 
		VFSNode[] files = this.list();
		
		if (files==null)
			return 0; 
		
		return files.length; 
	}

	public VRL rename(String newName, boolean renameFullPath)
			throws VlException
	{
		throw new VlException("Not implemented");
	}

	public boolean delete(boolean recurse) throws VlException
	{
	    if (recurse)
	    {
	        // my recursive delete 
	        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Deleting Octopuse Directory:" + this.getPath(), 1);
	        getTransferManager().recursiveDeleteDirContents(monitor, this,true); 
	    }
	    
	    // delete single empty directory:
		try
        {
            this.getOctoClient().rmdir(octoPath);
            // clear attributes to indicate non existing dir! 
            this.fileAttrs=null; 
            return true; 
        }
        catch (OctopusException e)
        {
            throw new VlException(e.getMessage(),e);  
        } 
	}
	
	// ===
	// Protected 
	// === 
	
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
