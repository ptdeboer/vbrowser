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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.VRI;

/** 
 * Local file implementation of FSNode.  
 */ 
public class LocalFSNode extends FSNode
{
	private java.io.File _file;
	
	// === constructors === 
	public LocalFSNode(java.io.File file)
	{
	    super(new VRI(file.toURI())); 
	    init(getVRI()); 
	}
	
	// === constructors === 
	protected LocalFSNode(String path) throws VRISyntaxException
	{
	    super((VRI)null);
	    // vri not set, use GlobalUserHome to resolve path:
	    VRI vri=GlobalProperties.getGlobalUserHomeVRI().resolve(path);
	    init(vri);
	}
	
	private void init(VRI vri)
	{
	    setVRI(vri); 
		this._file=new java.io.File(vri.getPath());   
	}
	
	public LocalFSNode(VRI loc) 
	{
		super(loc); 
		init(loc); 
	}

	@Override
	public boolean exists() 
	{
		return _file.exists(); 
	}

	@Override
	public boolean isDirectory()
	{
		return _file.isDirectory(); 
	}

	@Override
	public String[] list() 
	{
		return _file.list(); 
	}
	
	@Override
	public LocalFSNode[] listNodes() 
	{
		java.io.File files[]=_file.listFiles(); 
		if (files==null)
			return null;
		int len=files.length;
		
		LocalFSNode lfiles[]=new LocalFSNode[len];
		for (int i=0;i<len;i++)
			lfiles[i]=new LocalFSNode(files[i]);
		
		return lfiles; 
	}

	public boolean delete() throws IOException
	{
		return _file.delete(); 
	}
	
	@Override
	public long length() 
	{
		return _file.length(); 
	}
	
	@Override
	public boolean isFile()
	{
		return _file.isFile(); 
	}
	
	@Override
	public void mkdir() throws IOException
	{
		_file.mkdir(); 
	}
	
	@Override
	public void mkdirs() throws IOException
	{
		_file.mkdirs(); 
	}
	
	@Override
	public OutputStream createOutputStream() throws FileNotFoundException 
	{
		return new FileOutputStream(_file); 
	}
	
	@Override
	public InputStream createInputStream() throws FileNotFoundException 
	{
		return new FileInputStream(_file); 
	}
	
	@Override
	public String getPath() 
	{
		return this._file.getAbsolutePath(); 
	}
	
	@Override
	public LocalFSNode getParent() 
	{
		return new LocalFSNode(_file.getParentFile()); 
	}

	@Override
	public long getModificationTime() 
	{
		return _file.lastModified(); 
	}

	@Override
	public LocalFSNode newFile(String path) throws Exception 
	{
		LocalFSNode lfile=new LocalFSNode(resolvePath(path));  
		return lfile; 
	}
	
	public java.io.File getJavaFile()
	{
	    return this._file; 
	}

}
