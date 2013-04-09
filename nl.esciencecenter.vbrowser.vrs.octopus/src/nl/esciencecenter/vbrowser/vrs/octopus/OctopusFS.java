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
import java.net.URISyntaxException;
import java.util.List;

import nl.esciencecenter.octopus.exceptions.AttributeNotSupportedException;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.files.FileAttributes;
import nl.esciencecenter.octopus.files.Path;
import nl.esciencecenter.octopus.files.PathAttributes;
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
    public OctopusDir newDir(VRL pathVrl) throws VlException
    {
    	// VDir factory method: 
    	// new VDir object: path doesn't have to exist, just create the (VDir) object. 
        return new OctopusDir(this,null,newPath(pathVrl)); 
    }

    public Path newPath(VRL pathVrl) throws VlException
    {
        try
        {
            return octoClient.newPath(pathVrl.toEncodedURI());
        }
        catch (OctopusException | URISyntaxException e)
        {
            throw new VlException(e.getMessage(),e); 
        }
    }

    @Override
    public OctopusFile newFile(VRL pathVrl) throws VlException
    {
    	// VFile factory method: 
    	// new VFile object: path doesn't have to exist, just create the (VFile) object. 
        return new OctopusFile(this,null,newPath(pathVrl)); 
    }
    
    @Override
    public OctopusDir openDir(VRL pathVrl) throws VlException
    {
    	// Open filepath and return new VDir object. 
    	// (remote) directory must exist. 
    	OctopusDir dir=newDir(pathVrl); 
        
        // openDir() must return existing directory: 
        if (dir.exists()==false)
            throw new nl.uva.vlet.exception.ResourceNotFoundException("Directory doesn't exists:"+dir); 
        
        return dir; 
    }

    @Override
    public OctopusFile openFile(VRL pathVrl) throws VlException
    {
    	// Open filepath and return new VFile object. 
    	// (remote) file must exist.  
        OctopusFile file=newFile(pathVrl);
        
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
	    // openLocation: remote object must exist. 
	    try
	    {
    	    Path path = this.octoClient.newPath(vrl.toURI()); 
    	    FileAttributes attrs = this.octoClient.statPath(path); 
    
    	    return newVFSNode(path,attrs); 
    	    
	    }
	    catch (OctopusException e)
	    {
	        throw new VlException(e.getMessage(),e); 
	    }
        catch (URISyntaxException e)
        {
            throw new VlException(e.getMessage(),e); 
        }
	    
		// throw new nl.uva.vlet.exception.ResourceNotFoundException("Don't know what this is:"+vrl);
	}

	private VFSNode newVFSNode(Path path,FileAttributes optFileattrs) throws OctopusException
    {
	    System.out.println("NEW path     :"+path.toUri()); 
	    System.out.println("NEW abs path :"+path.toAbsolutePath());
	    
	    // check ? 
        if (optFileattrs.isDirectory())
        {
            return new OctopusDir(this,optFileattrs,path);
        }
        else
        {
            // default to file: 
            return new OctopusFile(this,optFileattrs,path);
        }

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
	
	/** Convert Octopus path to VRL */ 
    public VRL createVRL(Path path)
    {
        return new VRL(path.toUri()); 
    }

    public VFSNode[] listNodes(Path octoPath) throws VlException
    {
        List<Path> paths=null; 
        
        try
        {
            paths = this.octoClient.list(octoPath);
            if ((paths==null) || (paths.size()==0))
                    return null; 
                
            VFSNode nodes[]=new VFSNode[paths.size()]; 
            
            for (int i=0;i<paths.size();i++)
            {
                Path path=paths.get(i); 
                nodes[i]=newVFSNode(path,null);
            }
            
            return nodes; 
        }
        catch (OctopusException e)
        {
            throw new VlException(e.getMessage(),e); 
        } 
        
    }

    public VFSNode[] listNodesAttrs(Path octoPath) throws VlException
    {
        List<PathAttributes> paths=null; 
        
        try
        {
            paths = this.octoClient.statDir(octoPath);
            if ((paths==null) || (paths.size()==0))
                    return null; 
                
            VFSNode nodes[]=new VFSNode[paths.size()]; 
            
            for (int i=0;i<paths.size();i++)
            {
                PathAttributes pathAttrs=paths.get(i); 
                nodes[i]=newVFSNode(pathAttrs.path(),pathAttrs.attributes()); 
            }
            
            return nodes; 
        }
        catch (OctopusException e)
        {
            throw new VlException(e.getMessage(),e); 
        } 
        
    }
}
