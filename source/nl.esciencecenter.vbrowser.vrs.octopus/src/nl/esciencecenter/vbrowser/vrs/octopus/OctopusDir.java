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

package nl.esciencecenter.vbrowser.vrs.octopus;

import nl.esciencecenter.octopus.exceptions.OctopusIOException;
import nl.esciencecenter.octopus.files.FileAttributes;
import nl.esciencecenter.octopus.files.AbsolutePath;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.exception.ResourceAlreadyExistsException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrs.vfs.VDir;
import nl.nlesc.vlet.vrs.vfs.VFSNode;
import nl.nlesc.vlet.vrs.vrl.VRL;

/**
 * Minimal implementation of the VDir class. 
 */
public class OctopusDir extends VDir
{
	private FileAttributes fileAttrs;
    private AbsolutePath octoPath;

    public OctopusDir(OctopusFS vfs, FileAttributes attrs, AbsolutePath path)
	{
		super(vfs, vfs.createVRL(path));
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
        catch (OctopusIOException e)
        {
            // Check for File Not Found Here !
            throw new VlException(e.getMessage(),e); 
        } 
    }
    	
	@Override
	public boolean create(boolean force) throws VlException
	{
	    boolean exists=exists(); 
	        
	    if (exists)
	    {
	        if ((force==true) && (exists))
	        {
	            return true; //already exist. Ok. 
	        }
	        else
	        {
	            // exist, but force==false may not ignore existing. 
	            throw new ResourceAlreadyExistsException("Directory already exists:"+this); 
	        }
	    }
		try
        {
            this.getOctoClient().mkdir(octoPath);
        }
        catch (OctopusIOException e)
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
        catch (OctopusIOException e)
        {
            throw new VlException(e.getMessage(),e); 
        }
	}
	
	@Override
	public VFSNode[] list() throws VlException 
	{
	    //return this.getFS().listNodes(octoPath); 
        return getFileSystem().listNodesAndAttrs(octoPath); 
	}
	
	public OctopusFS getFileSystem()
	{
	    // Downcast from VFileSystem interface to actual FileSystem object. 
	    return (OctopusFS)super.getFileSystem(); 
	}
	
	public VRL rename(String newName, boolean renameFullPath)
            throws VlException
    {
        VRL vrl=getFileSystem().rename(octoPath,true,newName,renameFullPath);
        this.fileAttrs=null; // clear cached attributes!
        return vrl; 
    }

    public boolean delete(boolean recurse) throws VlException
    {
        if (recurse)
        {
            // my recursive delete 
            ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Deleting Octopus Directory:" + this.getPath(), 1);
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
        catch (OctopusIOException e)
        {
            throw new VlException(e.getMessage(),e);  
        } 
    }
    
	// ===
    // Attributes 
    // ===
    public String[] getAttributeNames()
    {
        StringList list=new StringList(super.getAttributeNames());
        list.add("octoDir"); 
        return list.toArray(); 
    }
    
    public VAttribute getAttribute(String name) throws VlException
    {
        if ("octoDir".equals(name))
            return new VAttribute (name,true); 
        else
            return super.getAttribute(name); 
    }
    
	@Override
	public long getModificationTime() throws VlException
	{
	    return getFileSystem().getModificationTime(getAttrs(false),System.currentTimeMillis());
	}
	
	@Override
	public String getPermissionsString() throws VlException
	{
	    return getFileSystem().createPermissionsString(getAttrs(false),true); 
    }
	   
	@Override
	public boolean isReadable() throws VlException 
	{
	    return getFileSystem().isReadable(getAttrs(false),true);
	}

	@Override
	public boolean isWritable() throws VlException
	{
		return this.getFileSystem().isWritable(getAttrs(false),false); 
	}

	public long getNrOfNodes() throws VlException
	{
		// count number of nodes. Faster implementation is recommended. 
		VFSNode[] files = this.list();
		
		if (files==null)
			return 0; 
		
		return files.length; 
	}

	// ===
	// Protected 
	// === 
    
    protected OctopusClient getOctoClient()
    {
        return this.getFileSystem().octoClient; 
    }
}
