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

package nl.esciencecenter.vbrowser.vrs.xenon;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Set;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.FileSystemNode;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.files.FileAttributes;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.PathAttributesPair;
import nl.esciencecenter.xenon.files.PosixFilePermission;

/**
 * Octopus Meta VFileSystem adaptor. 
 */  
public class XenonFS extends FileSystemNode
{
    private static ClassLogger logger=null; 
    
    static 
    {
        logger=ClassLogger.getLogger(XenonFS.class);
        logger.setLevelToDebug();
    }
    
	// ========================================================================
	// Instance
	// ========================================================================
	
	protected XenonClient octoClient;
	
	protected FileSystem octoFS;

    private Path entryPath;


    public XenonFS(VRSContext context, ServerInfo info,VRL location) throws VrsException 
	{
		super(context, info);
		
		boolean isSftp="sftp".equals(location.getScheme());
		boolean isGftp="gsiftp".equals(location.getScheme()) || "gftp".equals(location.getScheme());
        boolean isLocal="file".equals(location.getScheme());

		String fsUriStr=null;
		
		String vrlUser=location.getUsername(); 
	    String configeredUser=info.getUsername(); 
        
		if (isSftp)
		{
            if (StringUtil.isEmpty(configeredUser))
            {
                configeredUser=context.getConfigManager().getUserName(); 
            }
            
            fsUriStr=location.getScheme()+"://"+configeredUser+"@"+location.getHostname()+"/";
		}
		else if (isGftp)
		{
		    fsUriStr=location.getScheme()+"://"+location.getHostname()+"/";
		    
		}
		else
		{
		    fsUriStr="file:/";
		}
		
		try
        {
		    URI fsUri=new URI(fsUriStr);
		    
		    // create optional shared client. 
		    octoClient=XenonClient.createFor(context,info); 
		    
		    if (isSftp)
		    {
		        info.getUserinfo(); 
	            octoFS=octoClient.createFileSystem(fsUri,octoClient.createSSHCredentials(info));
		    }
		    else if (isGftp)
            {
                info.getUserinfo(); 
                octoFS=octoClient.createGftpFileSystem(fsUri,octoClient.createGftpCredentials(info));
            }
		    else
		    {
		        octoFS=octoClient.createFileSystem(fsUri);
		    }
		    
		    this.entryPath = octoFS.getEntryPath();
		    
        }
        catch (Exception e)
        {
          throw new VrsException(e.getMessage(),e); 
        } 
	}

    /** 
     * Resolve VRL against this FileSystem 
     */ 
    public Path createPath(VRL vrl) throws VrsException
    {
        try
        {
            // resolve path against FileSystem
            return octoClient.resolvePath(octoFS,vrl.getPath());
        }
        catch (Exception e)
        {
          throw new VrsException(e.getMessage(),e); 
        }  
    }
    
    /** 
     * Convert Octopus path to (absolute) VRL 
     */ 
    public VRL createVRL(Path path) throws VrsException 
    {
        VRL fsVrl=this.getVRL(); 
        String pathstr=path.getRelativePath().getAbsolutePath();
        return fsVrl.replacePath(pathstr); 
    }
       
    @Override
    public XenonDir newDir(VRL pathVrl) throws VrsException
    {
    	// VDir factory method: 
    	// new VDir object: path doesn't have to exist, just create the (VDir) object. 
        return new XenonDir(this,null,createPath(pathVrl)); 
    }   

    @Override
    public XenonFile newFile(VRL pathVrl) throws VrsException
    {
    	// VFile factory method: 
    	// new VFile object: path doesn't have to exist, just create the (VFile) object. 
        return new XenonFile(this,null,createPath(pathVrl)); 
    }
    
    @Override
    public XenonDir getDir(VRL pathVrl) throws VrsException
    {
    	// Open filepath and return new VDir object. 
    	// (remote) directory must exist. 
    	XenonDir dir=newDir(pathVrl); 
        
        // openDir() must return existing directory: 
        if (dir.exists()==false)
            throw new nl.esciencecenter.vlet.exception.ResourceNotFoundException("Directory doesn't exists:"+dir); 
        
        return dir; 
    }

    @Override
    public XenonFile getFile(VRL pathVrl) throws VrsException
    {
    	// Open filepath and return new VFile object. 
    	// (remote) file must exist.  
        XenonFile file=newFile(pathVrl);
        
        // openFile() must return existing file: 
        if (file.exists()==false)
            throw new nl.esciencecenter.vlet.exception.ResourceNotFoundException("File doesn't exists:"+file); 
        
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
    	    Path path = createPath(vrl); 
    	    FileAttributes attrs = octoClient.statPath(path); 
    
    	    return newVFSNode(path,attrs); 
    	    
	    }
	    catch ( XenonException e)
	    {
	        throw new VrsException(e.getMessage(),e); 
	    }
	}

	protected VFSNode newVFSNode(Path path,FileAttributes optFileattrs) throws VrsException
    {
	    try
	    {
            if ((optFileattrs!=null) && optFileattrs.isDirectory())
            {
                return new XenonDir(this,optFileattrs,path);
            }   
            else if ((optFileattrs!=null) && optFileattrs.isSymbolicLink())
            {
                // resolve links here: 
                return new XenonFile(this,optFileattrs,path);
            }
            else
            {
                // default to file: 
                return new XenonFile(this,optFileattrs,path);
            }
	    }
	    catch (Throwable e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    // ========================================================================
	// Filesystem helper methods: 
	// ========================================================================
	

    /** List nodes without fetching file attributes. All node are 'VFile' */ 
    public VFSNode[] listNodes(Path octoPath) throws VrsException
    {
        List<Path> paths=null; 
        
        try
        {
            paths = octoClient.listDir(octoPath);
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
        catch (Throwable e)
        {
            throw new VrsException(e.getMessage(),e); 
        } 
    }

    public VFSNode[] listNodesAndAttrs(Path octoPath) throws VrsException
    {
        List<PathAttributesPair> paths=null; 
        
        try
        {
            paths = octoClient.statDir(octoPath);
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
        catch (Throwable e)
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
        catch (Throwable e)
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
        catch (Throwable e)
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
        catch (Throwable e)
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
        catch (Throwable e)
        {
            throw new IOException(e.getMessage(),e); 
            // return defaultVal;
        }
    }

    public VRL rename(Path octoPath, boolean isDir, String newName, boolean renameFullPath) throws VrsException
    {
        Path newPath=null; 
        VRL baseVRL=createVRL(octoPath); 
        VRL newVRL; 
        
        if (renameFullPath==false)
        {
            newVRL=baseVRL.getParent().resolvePath(newName);
        }
        else
        {
            // resolve against root: 
            VRL oldVRL=createVRL(octoPath); 
            newVRL= oldVRL.replacePath(newName); 
        }
        
        newPath=createPath(newVRL); 
        
        try
        {
            octoClient.rename(octoPath,newPath);
            return createVRL(newPath);
        }
        catch (Throwable e)
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
        catch (Throwable e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
        

    }

   
}
