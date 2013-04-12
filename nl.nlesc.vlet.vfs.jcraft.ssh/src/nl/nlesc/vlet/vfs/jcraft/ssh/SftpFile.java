/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
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
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: SftpFile.java,v 1.1 2013/01/23 10:17:16 piter Exp $  
 * $Date: 2013/01/23 10:17:16 $
 */ 
// source: 

package nl.nlesc.vlet.vfs.jcraft.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.ptk.data.StringList;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.data.VAttributeConstants;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;
import nl.nlesc.vlet.vfs.VFSTransfer;
import nl.nlesc.vlet.vfs.VFile;
import nl.nlesc.vlet.vfs.VUnixFileAttributes;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.io.VRandomReadable;
import nl.nlesc.vlet.vrs.io.VStreamAppendable;
import nl.nlesc.vlet.vrs.io.VZeroSizable;

import com.jcraft.jsch.SftpATTRS;

public class SftpFile extends VFile implements VUnixFileAttributes,
	VRandomReadable,VZeroSizable,VStreamAppendable
{
    /** Currently SFTP can NOT handle stream read/write > 3200 per read/write  */ 
    
    private static final int sftpChunksize = 32000;
    
    SftpFileSystem server=null; 
    // holder class so value can be changed (need better solution) 
    SftpATTRS _attrs;  

    private void init(SftpFileSystem server,String path)
    {
      this.server=server;
      _attrs=null; 
    }

    SftpFile(SftpFileSystem server,VRL vrl)
    {
        super(server,vrl); 
        init(server,vrl.getPath()); 
    }
    
    SftpFile(SftpFileSystem server,String path)
    {
        super(server,new VRL(VRS.SFTP_SCHEME,null,server.getHostname(), server.getPort(),path));  
        init(server,path); 
    }
    
    public boolean create(boolean force) throws VlException
    {
    	VFile file = this.server.createFile(getPath(),force);   	
    	return (file!=null); 
    } 
    
    @Override
    public void uploadFrom(VFSTransfer transfer,VFile source) throws VlException
    {
        // Paranoia:
        if (source.isLocal() == false)
            throw new VlException(
                    "Internal error cmoveFromLocal didn't receive a local file:"
                            + source);
     
        String sftpFilepath = this.getPath(); 
        String localfilepath = source.getPath();

        // perform upload 
        
        server.uploadFile(transfer,localfilepath,sftpFilepath);
    }

    @Override
    public void downloadTo(VFSTransfer transfer,VFile localFile) throws VlException
    {
        if (localFile.isLocal()==false)
        {  
            throw new nl.nlesc.vlet.exception.ResourceTypeMismatchException(" Destination is not local:"+localFile);
        }
        	
        String targetPath=localFile.getPath(); 
       
        this.server.downloadFile(transfer,this.getPath(),targetPath);
    }        

    @Override
    public boolean exists() throws VlException
    {
    	return server.existsPath(this.getPath(),false); 
    }

    @Override
    public boolean isReadable() throws VlException
    {
        return server.isReadable(getPath()); 
    }

    @Override
    public boolean isWritable() throws VlException
    {
        return server.isWritable(getPath()); 
    }
    
    @Override
    public boolean isSymbolicLink() throws VlException
    {
        return server.isLink(getPath()); 
    }

    @Override
    public String getSymbolicLinkTarget() throws VlException
    {
    	return server.getLinkTarget(getPath()); 
    }
    
    public VRL rename(String newName, boolean nameIsPath) throws VlException
    {
        String newpath=server.rename(getPath(),newName,nameIsPath); 
        return this.resolvePathVRL(newpath); 
    }

    public boolean delete() throws VlException
    {
        return server.delete(this.getPath(),false); 
    }

    
    // ========================================================================
    // VStream[Readable|Writable] 
    // ========================================================================
    

    public InputStream getInputStream() throws IOException
    {
        // redirect to server to synchronise file access:
        try
        {
            return server.createInputStream(getPath());
        }
        catch (VlException e)
        {
           throw new IOException(e);
        } 
    }


    public OutputStream getOutputStream() throws IOException
    {
        try
        {
            // redirect to server to synchronise file access:
            return server.createOutputStream(getPath(),false); 
        }
        catch (VlException e)
        {
            throw new IOException(e);
        } 
    }
    
    public OutputStream getOutputStream(boolean append) throws IOException
    {
        try
        {
            // redirect to server to synchronise file access:
            return server.createOutputStream(getPath(),append);
        }
        catch (VlException e)
        {
            throw new IOException(e);
        } 
        
    }

    // @Override 
    public void setLengthToZero() throws IOException
    {
        try
        {
            server.delete(this.getPath(),false);
            server.createFile(this.getPath(),true); 
        }
        catch (VlException e)
        {
            throw new IOException(e);
        } 
        
        //throw new NotImplementedException("Not implemented yet");
    }


    public int readBytes(long fileOffset, byte[] buffer, int bufferOffset, int nrBytes) throws IOException
    {
        // redirect to server to synchronise file access: 
        return server.readBytes(getPath(),fileOffset,buffer,bufferOffset,nrBytes);
    }

    /** 
     * Wrning: For some reason the outputstream of Jsch can only handle 32000 bytes
     * per write. 
     */ 
    //@Override 
    public void streamWrite2(byte[] buffer, int bufferOffset, int nrBytes) throws VlException
    {
        try
        {
            OutputStream outps = getOutputStream();
            
            // must currently write in 32000 byte sized chunks
            int chunksize=sftpChunksize;  

           // write in chunks:
           for (int i=0;i<nrBytes;i+=chunksize)
           {
               if (i+chunksize>nrBytes)
                  chunksize=nrBytes-i;
               
                  outps.write(buffer,i,chunksize);
             
           }
           
           outps.close(); 
        }
        catch (IOException e)
        {
            throw new VlIOException("Couldn't write to stream:"+this,e); 
        }
    }
    
    public void writeBytes(long fileOffset, byte[] buffer, int bufferOffset, int nrBytes) throws VlException
    {
        // stream write is faster= (also writeBytes is still buggy!) 
        
        //if (fileOffset==0) 
        //    streamWrite(buffer,bufferOffset,nrBytes); 
        
        // redirect to server to synchronise file access: 
        server.writeBytes(getPath(),fileOffset,buffer,bufferOffset,nrBytes);
    }
    
    @Override
    public VAttribute[][] getACL() throws VlException
    {
        return server.getACL(getPath(),false);
    }

    @Override
    public void setACL(VAttribute acl[][]) throws VlException
    {
        server.setACL(getPath(),acl,true); 
    }
    
    @Override
    public long getModificationTime() throws VlException
    {
        return this.server.getModificationTime(getSftpAttributes()); 
    }
    
    private SftpATTRS getSftpAttributes() throws VlException
    {
        if (_attrs==null)
        {
            _attrs = server.getSftpAttrs(getPath());
        }
        return _attrs; 
    }
    
    public String getPermissionsString() throws VlException
    {
        return server.getPermissionsString(getSftpAttributes(),false); 
    }

	public int getMode() throws VlException
	{
		 return server.getPermissions(getSftpAttributes()); 
	}

    
    @Override
    public long getLength() throws IOException
    {
        try
        {
            return server.getLength(getSftpAttributes());
        }
        catch (VlException e)
        {
            throw new IOException(e); 
        } 
    }

    /** Returns all default attributes names */ 
    public String[] getAttributeNames()
    {
        String superNames[]=super.getAttributeNames();
       
        return StringList.merge(superNames,SftpFSFactory.sftpFileAttributeNames); 
    }
   
    
    public VAttribute getAttribute(String name) throws VlException
    {
        if (name==null) 
            return null;
        
        // update attributes: 
        VAttribute  attr=this.getStaticAttribute(name); 
        if (attr!=null)
            return attr;
        
        // if EXISTS attribute is asked, do not get attributes 
        
        if (VAttributeConstants.ATTR_EXISTS.equals(name))
        {   
            return new VAttribute(name,exists()); 
        }
        
        attr=server.getAttribute(this,this.getSftpAttributes(),name,false,true);
        
        if (attr!=null)
            return attr; 
        
        return super.getAttribute(name); 
    }
    
    public VAttribute[] getAttributes(String names[]) throws VlException
    {
        if (names==null) 
            return null; 
        
        // optimized Gftp Attributes: 
        
        VAttribute[] vattrs=server.getAttributes(this,this.getSftpAttributes(),names,false);
        
        for (int i=0;i<names.length;i++)
        {
            // get attribute which SftpAttrs don't have: 
            if (vattrs[i]==null)
                vattrs[i]=super.getAttribute(names[i]);
        }
        
        return vattrs; 
    }

	public void setMode(int mode) throws VlException
	{
	    
	}

    public String getGid() throws VlException
    {
        return ""+getSftpAttributes().getGId();  
    }

    public String getUid() throws VlException
    {
        return ""+getSftpAttributes().getUId();  
    }

    public boolean sync() throws VlException 
    {
        // just nullify attributes. Will be fetched again/ 
        this._attrs=null;
        return true; 
    }
}