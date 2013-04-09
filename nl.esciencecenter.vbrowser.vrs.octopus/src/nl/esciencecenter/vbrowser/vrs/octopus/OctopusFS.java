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

import java.io.InputStream;
import java.io.OutputStream;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.FileSystemNode;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vfs.VFile;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;

/**
 *  Example Skeleton FileSystemServer implementation 
 *  See Super Class FileSystemNode methods for default implementation. 
 */  
public class OctopusFS extends FileSystemNode
{
	// ========================================================================
	// Instance
	// ========================================================================
	
	protected OctopusClient octoClient;

    public OctopusFS(VRSContext context, ServerInfo info,VRL location) throws VlException 
	{
		super(context, info);
		octoClient=OctopusClient.createFor(context,info,location); 
		
	}

    @Override
    public VDir newDir(VRL path) throws VlException
    {
    	// VDir factory method: 
    	// new VDir object: path doesn't have to exist, just create the (VDir) object. 
        return new OctopusDir(this,path); 
    }

    @Override
    public VFile newFile(VRL path) throws VlException
    {
    	// VFile factory method: 
    	// new VFile object: path doesn't have to exist, just create the (VFile) object. 
        return new OctopusFile(this,path); 
    }
    
    @Override
    public OctopusDir openDir(VRL path) throws VlException
    {
    	// Open filepath and return new VDir object. 
    	// (remote) directory must exist. 
    	OctopusDir dir=new OctopusDir(this,path);
        
        // openDir() must return existing directory: 
        if (dir.exists()==false)
            throw new nl.uva.vlet.exception.ResourceNotFoundException("Directory doesn't exists:"+dir); 
        
        return dir; 
    }

    @Override
    public OctopusFile openFile(VRL path) throws VlException
    {
    	// Open filepath and return new VFile object. 
    	// (remote) file must exist.  
        OctopusFile file=new OctopusFile(this,path);
        
        // openFile() must return existing file: 
        if (file.exists()==false)
            throw new nl.uva.vlet.exception.ResourceNotFoundException("File doesn't exists:"+file); 
        
        return file; 
    }

	public void connect() throws VlException 
	{
		// connect if not connected yet, or ignore if not applicable. 
		// multiple connect() calls are possible. Ignore if this happens. 
	}

	public void disconnect() throws VlException
	{
		// disconnect if applicable or ignore. 
		// multiple disconnect() are allow. Ignore if this happens. 
	}

	public boolean isConnected() 
	{
		return true;
	}
	
	@Override
	public VFSNode openLocation(VRL vrl) throws VlException
	{
		// Master 'openLocation' which connects to remote resource. 
		if (isFile(vrl.getPath()))
		{
			return new OctopusFile(this,vrl);			
		}
		else if (isDir(vrl.getPath())) 
		{
			return new OctopusDir(this,vrl);
		}
 
		throw new nl.uva.vlet.exception.ResourceNotFoundException("Don't know what this is:"+vrl);
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
