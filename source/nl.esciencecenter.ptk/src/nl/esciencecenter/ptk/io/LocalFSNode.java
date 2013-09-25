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
import java.net.URI;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/** 
 * Local file implementation of FSNode based on java.nio.Files;   
 */ 
public class LocalFSNode extends FSNode
{
    // nio ! 
	private Path _path;
    private BasicFileAttributes basicAttrs;
    private PosixFileAttributes posixAttrs;
	
    public LocalFSNode(Path path)
    {
        super(path.toUri());
        init(path); 
    }
	
	private void init(Path path)
	{
	    setURI(path.toUri()); 
		this._path=path;   
	}
	
	@Override
	public boolean sync()
	{
	    this.basicAttrs=null;
	    this.posixAttrs=null;
	    return true; 
	}
	
	public LocalFSNode(URI loc) 
	{
		super(loc); 
		FileSystem fs = FileSystems.getDefault();
		init(fs.getPath(loc.getPath()));
	}

	@Override
	public boolean exists(LinkOption... linkOptions) 
	{
		return Files.exists(_path, linkOptions); 
	}

	@Override
	public boolean isDirectory(LinkOption... linkOptions)
	{
		return Files.isDirectory(_path, linkOptions); 
	}
	
	@Override
	public String[] list() throws IOException 
	{
		DirectoryStream<Path> dirStream = Files.newDirectoryStream(_path);
		Iterator<Path> dirIterator = dirStream.iterator(); 
		ArrayList<String> list = new ArrayList<String>(); 
		
		while(dirIterator.hasNext()) 
		{
		    list.add(dirIterator.next().getFileName().toString());   
		}
		   
		return list.toArray(new String[0]); 
	}
	
	@Override
	public LocalFSNode[] listNodes() throws IOException 
	{
	    DirectoryStream<Path> dirStream = Files.newDirectoryStream(_path);
        Iterator<Path> dirIterator = dirStream.iterator(); 
        ArrayList<LocalFSNode> list = new ArrayList<LocalFSNode>(); 
        
        while(dirIterator.hasNext()) 
        {
            list.add(new LocalFSNode(dirIterator.next()));    
        }
           
        return list.toArray(new LocalFSNode[0]); 
	}

	public void delete() throws IOException
	{
	    Files.delete(_path); 
	}
	
	@Override
	public long length() throws IOException 
	{
		return (Long)Files.getAttribute(_path, "size");   
	}
	
	@Override
	public boolean isFile(LinkOption... linkOptions)
	{
		return Files.isRegularFile(_path,linkOptions);  
	}
	
	@Override
	public void mkdir() throws IOException
	{
	    Files.createDirectory(_path); 
	}
	
	@Override
	public void mkdirs() throws IOException
	{
	    Files.createDirectories(_path);
	}
	
	@Override
	public OutputStream createOutputStream() throws IOException 
	{
		return Files.newOutputStream(_path); // OpenOptions..
	}
	
	@Override
	public InputStream createInputStream() throws IOException 
	{
		return Files.newInputStream(_path);
	}
	
	@Override
	public String getPathname() 
	{
		return _path.toUri().getPath();  
	}
	
	@Override
	public LocalFSNode getParent() 
	{
		return new LocalFSNode(_path.getParent()); 
	}

	@Override
	public long getModificationTime() throws IOException 
	{
	    FileTime value = getBasicAttributes().lastModifiedTime();
	    return value.toMillis(); 
	}

	@Override
	public long getCreationTime() throws IOException 
	{
	    FileTime value = getBasicAttributes().creationTime();
	    return value.toMillis(); 
    }

	@Override
	public long getAccessTime() throws IOException 
	{
	    FileTime value = getBasicAttributes().lastAccessTime();
	    return value.toMillis(); 
    }

	@Override
	public LocalFSNode newFile(String path) throws FileURISyntaxException 
	{
		LocalFSNode lfile=new LocalFSNode(resolvePathURI(path));  
		return lfile; 
	}
	
	public java.io.File toJavaFile()
	{
	    return _path.toFile(); 
	}

    @Override
    public boolean isSymbolicLink()
    {
        return Files.isSymbolicLink(_path); 
    }

    /** 
     * Returns symbolic link target or NULL 
     */ 
    public LocalFSNode getSymbolicLinkTarget() throws IOException
    {
        if (this.isSymbolicLink()==false)
            return null;
        
        Path target = Files.readSymbolicLink(_path);
        
        return new LocalFSNode(target); 
    }
    
    public BasicFileAttributes getBasicAttributes() throws IOException
    {
        if (basicAttrs==null)
        {
            basicAttrs= Files.readAttributes(_path, BasicFileAttributes.class);
        }
        return basicAttrs; 
    }
    
    public PosixFileAttributes getPosixAttributes() throws IOException
    {
        if (posixAttrs==null)
        {
            posixAttrs= Files.readAttributes(_path, PosixFileAttributes.class);
        }
        return posixAttrs; 
    }

    public boolean isReadable()
    {
        return this.toJavaFile().canRead();
    }

    public boolean isWritable()
    {
        return this.toJavaFile().canWrite(); 
    }

    public boolean isHidden()
    {
        return this.getBasename().startsWith("."); 
    }

    public int getUnixFileMode() throws IOException
    {
         Set<PosixFilePermission> perms = getPosixAttributes().permissions(); 
         return toUnixFileMode(perms); 
    }

    public static int toUnixFileMode(Set<PosixFilePermission> perms)
    {
        int mode=0; 
        
        if (perms.contains(PosixFilePermission.OWNER_READ))
            mode|=0400;
        if (perms.contains(PosixFilePermission.OWNER_WRITE))
            mode|=0200;
        if (perms.contains(PosixFilePermission.OWNER_EXECUTE))
            mode|=0100; 
        if (perms.contains(PosixFilePermission.GROUP_READ))
            mode|=0040;
        if (perms.contains(PosixFilePermission.GROUP_WRITE))
            mode|=0020;
        if (perms.contains(PosixFilePermission.GROUP_EXECUTE))
            mode|=0010; 
        if (perms.contains(PosixFilePermission.OTHERS_READ))
            mode|=0004;
        if (perms.contains(PosixFilePermission.OTHERS_WRITE))
            mode|=0002;
        if (perms.contains(PosixFilePermission.OTHERS_EXECUTE))
            mode|=0001; 

        return mode; 
    }

    public static Set<PosixFilePermission> fromUnixFileMode(int mode)
    {
        Set<PosixFilePermission> perms=new HashSet<PosixFilePermission>();
        
        if ((mode&0400)>0)
            perms.add(PosixFilePermission.OWNER_READ); 
        if ((mode&0200)>0)
            perms.add(PosixFilePermission.OWNER_WRITE); 
        if ((mode&0100)>0)
            perms.add(PosixFilePermission.OWNER_EXECUTE); 

        if ((mode&0040)>0)
            perms.add(PosixFilePermission.GROUP_READ); 
        if ((mode&0020)>0)
            perms.add(PosixFilePermission.GROUP_WRITE); 
        if ((mode&0010)>0)
            perms.add(PosixFilePermission.GROUP_EXECUTE); 

        if ((mode&0004)>0)
            perms.add(PosixFilePermission.OTHERS_READ); 
        if ((mode&0002)>0)
            perms.add(PosixFilePermission.OTHERS_WRITE); 
        if ((mode&0001)>0)
            perms.add(PosixFilePermission.OTHERS_EXECUTE); 

        return perms; 
    }
    
    public void setUnixFileMode(int mode) throws IOException
    {
        Files.setPosixFilePermissions(_path, fromUnixFileMode(mode));
    }
    
}
