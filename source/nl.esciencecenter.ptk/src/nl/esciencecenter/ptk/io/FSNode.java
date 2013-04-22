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

package nl.esciencecenter.ptk.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.VRI;

/** 
 * Abstract FileSystem Node of local or remote Filesystem. 
 * Can be File or Directory. 
 * Uses URI based location.  
 */
public abstract class FSNode 
{
    public static final String FILE_TYPE = "File";
    
    public static final String DIR_TYPE = "Dir";

	public static final String FILE_SCHEME = "file"; // file:///blah 
    
    // ===
    // 
    // ===
    
    private VRI vri;

    public FSNode(URI uri)
    {
        this.vri=new VRI(uri); 
    }

    protected void setVRI(VRI vri)
    {
    	this.vri=vri; 
    }
    
    public FSNode(VRI uri)
    {
        this.vri=uri; 
    }

    public URI getURI() throws URISyntaxException
    {
        return vri.toURI(); 
    }

    public URL getURL() throws MalformedURLException 
    {
        return vri.toURL(); 
    }
    
    public VRI getVRI()
    {
        return vri;  
    }

    public FSNode getNode(String relpath) throws VRISyntaxException, Exception
    {
        return newFile(resolvePath(relpath));  
    } 
        
    /**
     * Whether this file points to a local file. 
     * Only LocalFile.isLocal() returns true.
     */ 
    public boolean isLocal()
    {
       return false; 
    }

    public String[] getPathElements() 
	{
		return vri.getPath().split("/"); 
	}		
    
    /** Returns absolute (normalized) URI path indepenend of local file systems paths */ 
    public String getPath()
    {
    	return vri.getPath(); 
    }
    
	public String getBasename() 
	{
		return vri.getBasename(); 
	}

	public String getExtension()
	{
	    return vri.getExtension(); 
	}
	
	public boolean hasExtension(String ext,boolean matchCase)
	{
	    return this.vri.hasExtension(ext,matchCase); 
	}
	
	public String getDirname() 
	{
		return vri.getDirname(); 
	}
	
    public String getHostname()
    {
    	return vri.getHostname(); 
    }
    
    public int getPort()
    {
    	return vri.getPort(); 
    }

    // =======================================================================
    // IO Methods 
    // =======================================================================

    public InputStream createInputStream() throws IOException
    {
    	throw new Error("Not supported: createInputStream() of:"+this); 
    }
    
    public OutputStream createOutputStream() throws IOException
    {
    	throw new Error("Not supported: createOutputStream() of:"+this); 
    }

	public FSNode createDir(String subdir) throws Exception 
	{
		FSNode dir=newFile(resolvePath(subdir)); 
		dir.mkdir(); 
		return dir; 
	}

	public FSNode createFile(String filepath) throws Exception 
	{
		FSNode file=newFile(resolvePath(filepath)); 
		file.create();  
		return file;
	}
	
	public String resolvePath(String relPath) throws VRISyntaxException
	{
		return resolvePathVRI(relPath).getPath();
	}
	
	public VRI resolvePathVRI(String relPath) throws VRISyntaxException
	{
		return vri.resolvePath(relPath);
	}

	public boolean create() throws IOException 
	{
		byte bytes[]=new byte[0]; 
		
		// create file by writing zero bytes: 
		OutputStream outps=this.createOutputStream(); 
		outps.write(bytes); 
		outps.close(); 
		
		return true; 
	}
	
	public String toString()
	{
	    return "(FSNode)"+this.getVRI().toString(); 
	}
	
    // =======================================================================
    // Delete/Modify structure
    // =======================================================================

	/** Perform an (optional recursive) delete in this node */ 
	public boolean delete(boolean recursive) throws IOException
	{
	    if (isFile())
	        return delete(); 
	    // directory;

	    FSNode[] nodes = this.listNodes();
	    if (recursive==false)
	    {
	        if  ((nodes==null) || (nodes.length<=0))
	        {
	            return delete(); // delete empty directory ! 
	        }
	        else
	        {
	            throw new IOException("Directory is not empty"); 
	        }
	    }
	    else
	    {
	        for (FSNode node:nodes)
	            node.delete(true);
	        return this.delete(); 
	    }
	}
	
    // =======================================================================
    // Abstract Interface 
    // =======================================================================

	// === File/Directory methods === // 
	
	/** FSNode factory method, optionally resolves path against parent FSNode.*/ 
	public abstract FSNode newFile(String path) throws Exception; 

    /** Whether file/directory exists. */  
	public abstract boolean exists(); 
    
    /** Is a regular file. */ 
    public abstract boolean isFile();
  
    /** Is a regular directory. */ 
    public abstract boolean isDirectory(); 

    /** Size in bytes. */ 
    public abstract long length();
    
    /** Modification time in milli seconds since epoch. */ 
    public abstract long getModificationTime();

    /** Logical parent */ 
    public abstract FSNode getParent(); 

    /** Delete file or empty directory. */
    public abstract boolean delete() throws IOException;
    
    // === Directory methods === // 
    
    /** Return contents of directory. */ 
    public abstract String[] list() throws IOException;  
    
    /** Return contents of directory as FSNode objects .*/ 
    public abstract FSNode[] listNodes()throws IOException;   

    /** Create last path element as (sub)directory, parent directory must exist. */ 
    public abstract void mkdir() throws IOException;  

    /** Create full directory path. */
    public abstract void mkdirs() throws IOException;
    
}
