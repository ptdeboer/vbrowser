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

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import nl.esciencecenter.octopus.exceptions.AttributeNotSupportedException;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.exceptions.OctopusIOException;
import nl.esciencecenter.octopus.files.FileAttributes;
import nl.esciencecenter.octopus.files.AbsolutePath;
import nl.esciencecenter.octopus.files.FileSystem;
import nl.esciencecenter.octopus.files.PathAttributesPair;
import nl.esciencecenter.octopus.files.PosixFilePermission;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vfs.FileSystemNode;
import nl.nlesc.vlet.vrs.vfs.VFS;
import nl.nlesc.vlet.vrs.vfs.VFSNode;

/**
 * Octopus Meta VFileSystem adaptor. 
 */  
public class OctopusFS extends FileSystemNode
{
    private static ClassLogger logger=null; 
    
    static 
    {
        logger=ClassLogger.getLogger(OctopusFS.class);
        logger.setLevelToDebug();
    }
    
	// ========================================================================
	// Instance
	// ========================================================================
	
	protected OctopusClient octoClient;
	
	protected FileSystem octoFS;

    private AbsolutePath entryPath;


    public OctopusFS(VRSContext context, ServerInfo info,VRL location) throws VrsException 
	{
		super(context, info);
		
		boolean isSftp="sftp".equals(location.getScheme());
        boolean isLocal="file".equals(location.getScheme());

		String fsUriStr=null;
		
		String vrlUser=location.getUsername(); 
	    String configeredUser=info.getUsername(); 
        
		if (isSftp)
		{
            if (StringUtil.isEmpty(configeredUser))
                configeredUser=context.getConfigManager().getUserName(); 
            
            fsUriStr=location.getScheme()+"://"+configeredUser+"@"+location.getHostname()+"/";
		}
		else
		{
		    fsUriStr="file:/";
		}
		
		try
        {
		    URI fsUri=new URI(fsUriStr);
		    
		    // create optional shared client. 
		    octoClient=OctopusClient.createFor(context,info); 
		    
		    if ("sftp".equals(location.getScheme()))
		    {
		        info.getUserinfo(); 
	            octoFS=octoClient.createFileSystem(fsUri,octoClient.createSSHCredentials(info));
		    }
		    else
		    {
		        octoFS=octoClient.createFileSystem(fsUri);
		    }
		    
		    this.entryPath = octoFS.getEntryPath();
		    
        }
        catch (OctopusIOException | OctopusException | URISyntaxException e)
        {
          throw new VrsException(e.getMessage(),e); 
        } 
	}

    /** 
     * Resolve VRL against this FileSystem 
     */ 
    public AbsolutePath createPath(VRL vrl) throws VrsException
    {
        try
        {
            // resolve path against FileSystem
            return octoClient.resolvePath(octoFS,vrl.getPath());
        }
        catch (OctopusIOException | OctopusException e)
        {
          throw new VrsException(e.getMessage(),e); 
        }  
    }
    
    /** 
     * Convert Octopus path to (absolute) VRL 
     */ 
    public VRL createVRL(AbsolutePath path)
    {
        FileSystem fs = path.getFileSystem(); 
        String pathstr=path.getPath(); 
        
        VRL fsVrl=new VRL(fs.getUri()); 
        return fsVrl.replacePath(pathstr); 
    }
    
   
    @Override
    public OctopusDir newDir(VRL pathVrl) throws VrsException
    {
    	// VDir factory method: 
    	// new VDir object: path doesn't have to exist, just create the (VDir) object. 
        return new OctopusDir(this,null,createPath(pathVrl)); 
    }   

    @Override
    public OctopusFile newFile(VRL pathVrl) throws VrsException
    {
    	// VFile factory method: 
    	// new VFile object: path doesn't have to exist, just create the (VFile) object. 
        return new OctopusFile(this,null,createPath(pathVrl)); 
    }
    
    @Override
    public OctopusDir getDir(VRL pathVrl) throws VrsException
    {
    	// Open filepath and return new VDir object. 
    	// (remote) directory must exist. 
    	OctopusDir dir=newDir(pathVrl); 
        
        // openDir() must return existing directory: 
        if (dir.exists()==false)
            throw new nl.nlesc.vlet.exception.ResourceNotFoundException("Directory doesn't exists:"+dir); 
        
        return dir; 
    }

    @Override
    public OctopusFile getFile(VRL pathVrl) throws VrsException
    {
    	// Open filepath and return new VFile object. 
    	// (remote) file must exist.  
        OctopusFile file=newFile(pathVrl);
        
        // openFile() must return existing file: 
        if (file.exists()==false)
            throw new nl.nlesc.vlet.exception.ResourceNotFoundException("File doesn't exists:"+file); 
        
        return file; 
    }

	public void connect() throws VrsException 
	{
	    
	}

	public void disconnect() throws VrsException
	{
	    // could destroy FileSystem object here. 
	}

	public boolean isConnected() 
	{
		return true;
	}
	
	@Override
	public VFSNode openLocation(VRL vrl) throws VrsException
	{
	    // openLocation: remote object must exist. 
	    try
	    {
    	    AbsolutePath path = createPath(vrl); 
    	    FileAttributes attrs = octoClient.statPath(path); 
    
    	    return newVFSNode(path,attrs); 
    	    
	    }
	    catch ( OctopusIOException e)
	    {
	        throw new VrsException(e.getMessage(),e); 
	    }
	}

	protected VFSNode newVFSNode(AbsolutePath path,FileAttributes optFileattrs) throws VrsException
    {
	    try
	    {
            if ((optFileattrs!=null) && optFileattrs.isDirectory())
            {
                return new OctopusDir(this,optFileattrs,path);
            }   
            else if ((optFileattrs!=null) && optFileattrs.isSymbolicLink())
            {
                // resolve links here: 
                return new OctopusFile(this,optFileattrs,path);
            }
            else
            {
                // default to file: 
                return new OctopusFile(this,optFileattrs,path);
            }
	    }
	    catch (OctopusIOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    // ========================================================================
	// Filesystem helper methods: 
	// ========================================================================
	

    /** List nodes without fetching file attributes. All node are 'VFile' */ 
    public VFSNode[] listNodes(AbsolutePath octoAbsolutePath) throws VrsException
    {
        List<AbsolutePath> paths=null; 
        
        try
        {
            paths = octoClient.listDir(octoAbsolutePath);
            if ((paths==null) || (paths.size()==0))
                    return null; 
                
            VFSNode nodes[]=new VFSNode[paths.size()]; 
            
            for (int i=0;i<paths.size();i++)
            {
                AbsolutePath path=paths.get(i); 
                nodes[i]=newVFSNode(path,null);
            }
            
            return nodes; 
        }
        catch (OctopusIOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        } 
    }

    public VFSNode[] listNodesAndAttrs(AbsolutePath octoAbsolutePath) throws VrsException
    {
        List<PathAttributesPair> paths=null; 
        
        try
        {
            paths = octoClient.statDir(octoAbsolutePath);
            if ((paths==null) || (paths.size()==0))
                    return null; 
                
            VFSNode nodes[]=new VFSNode[paths.size()]; 
            
            for (int i=0;i<paths.size();i++)
            {
                PathAttributesPair pathAttrs=paths.get(i); 
                
                nodes[i]=newVFSNode(pathAttrs.path(),pathAttrs.attributes()); 
            }
            
            return nodes; 
        }
        catch (OctopusIOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        } 
     }

    public long getModificationTime(FileAttributes attrs, long currentTimeMillis) // throws VlException
    {
        try
        {
            return attrs.lastModifiedTime();
        }
        catch (AttributeNotSupportedException e)
        {
            // throw new VlException(e.getMessage(),e); 
            return currentTimeMillis; 
        }
    }

    public boolean isReadable(FileAttributes attrs, boolean defaultValue) throws VrsException
    {
        try
        {
            return attrs.isReadable();
        }
        catch (AttributeNotSupportedException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    public boolean isWritable(FileAttributes attrs, boolean defaultValue) throws VrsException
    {
        try
        {
            return attrs.isWritable();
        }
        catch (AttributeNotSupportedException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }
    
    public long getLength(FileAttributes attrs, long defaultVal) throws IOException
    {
        try
        {
            return attrs.size();
        }
        catch (AttributeNotSupportedException e)
        {
            throw new IOException(e.getMessage(),e); 
            // return defaultVal;
        }
    }

    public VRL rename(AbsolutePath octoAbsolutePath, boolean isDir, String newName, boolean renameFullAbsolutePath) throws VrsException
    {
        AbsolutePath newAbsolutePath=null; 
        VRL baseVRL=createVRL(octoAbsolutePath); 
        VRL newVRL; 
        
        if (renameFullAbsolutePath==false)
        {
            newVRL=baseVRL.getParent().resolvePath(newName);
        }
        else
        {
            // resolve against root: 
            VRL oldVRL=createVRL(octoAbsolutePath); 
            newVRL= oldVRL.replacePath(newName); 
        }
        
        newAbsolutePath=createPath(newVRL); 
        
        try
        {
            AbsolutePath actualAbsolutePath=octoClient.rename(octoAbsolutePath,newAbsolutePath);
            return createVRL(actualAbsolutePath);
        }
        catch (OctopusIOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        } 
    }

    public String createPermissionsString(FileAttributes attrs, boolean isDir) throws VrsException
    {
        Set<PosixFilePermission> set;
        try
        {
            set = attrs.permissions();
            int mode=octoClient.getUnixFileMode(set); 
            return VFS.modeToString(mode, isDir); 
        }
        catch (AttributeNotSupportedException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
        

    }

   
}
