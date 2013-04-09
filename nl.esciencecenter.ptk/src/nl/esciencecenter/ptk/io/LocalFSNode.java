/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.io;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.ptk.Global;
import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.VRI;

/** 
 * Local file implementation of FSNode.  
 */ 
public class LocalFSNode extends FSNode implements StreamReadable,StreamWritable 
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
	    VRI vri=Global.getGlobalUserHomeVRI().resolve(path);
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
