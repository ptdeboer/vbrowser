/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package nl.esciencecenter.vlet.vfs.ssh.jcraft;

import java.util.Vector;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.tasks.VRSTaskMonitor;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.vlet.vrs.vfs.VUnixFileAttributes;

import com.jcraft.jsch.SftpATTRS;

public class SftpDir extends VDir implements VUnixFileAttributes
{
    private SftpFileSystem server=null;
    private SftpATTRS _attrs=null;
    
    public SftpDir(SftpFileSystem server,VRL vrl)
    {
        super(server,vrl);
        this.server=server;
        _attrs=null; 
    }
    
    @Override
    public VFSNode[] list() throws VrsException
    {
        String childs[]=this.server.list(this.getPath());
       
        if (childs==null) 
            return null;
        
        Vector<VFSNode> nodes=new Vector<VFSNode>(); 
        
        for (int i=0;i<childs.length;i++)
        {
            if ( (childs[i]==null) 
                 || (childs[i].compareTo(".")==0) 
                 || (childs[i].compareTo("..")==0) )
            {
                
            }
            else
            {
              String filepath=this.getPath()+URIFactory.URI_SEP_CHAR+childs[i];
              nodes.add(server.getPath(filepath)); 
            }
        }
        
        return VFSNode.returnAsArray(nodes); 
    }

    public boolean create(boolean ignoreExisting) throws VrsException
    {
    	VDir dir=this.server.createDir(this.getPath(),ignoreExisting);
    	return (dir!=null); 
    }
    
    @Override
    public boolean exists() throws VrsException
    {
    	return server.existsPath(this.getPath(),true);
    }

    @Override
    public boolean isReadable() throws VrsException
    {
        return server.isReadable(getPath());  
    }

    @Override
    public boolean isWritable() throws VrsException
    {
        return server.isWritable(getPath());  
    }

    
    @Override
    public boolean isSymbolicLink() throws VrsException
    {
        return server.isLink(getPath()); 
    }

    @Override
    public String getSymbolicLinkTarget() throws VrsException
    {
    	return server.getLinkTarget(getPath()); 
    }
    
    public long getNrOfNodes() throws VrsException
    {
        return getNodes().length; 
    }

    public boolean delete(boolean recurse) throws VrsException
    {
    	ITaskMonitor  monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Deleting (SFTP) directory:"+this.getPath(),1); 

        boolean result = true;

        // delete children first.
        if (recurse == true)
            this.getVRSContext().getTransferManager().recursiveDeleteDirContents(monitor,this, true); 

        if (result==true) 
            return server.delete(getPath(),true);
        else
            return false; 
    }
    
    public VRL rename(String newName, boolean nameIsPath) throws VrsException
    {
        String newpath=server.rename(getPath(),newName,nameIsPath); 
        return this.resolvePath(newpath); 
    }
    
    @Override
    public Attribute[][] getACL() throws VrsException
    {
        return server.getACL(getPath(),true); 
    }
    @Override
    public void setACL(Attribute acl[][]) throws VrsException
    {
        server.setACL(getPath(),acl,true); 
    }
    
    /** Returns all default attributes names */ 
    public String[] getAttributeNames()
    {
        String superNames[]=super.getAttributeNames();
       
        return StringList.merge(superNames,SftpFSFactory.sftpDirAttributeNames); 
    }
   
    
    public Attribute getAttribute(String name) throws VrsException
    {
        if (name==null) 
            return null;
        
        // update attributes: 
        Attribute  attr=this.getStaticAttribute(name); 
        if (attr!=null)
            return attr; 
        
        // update attributes: 
        attr=server.getAttribute(this,getSftpAttributes(),name,false,true);
        
        if (attr!=null)
            return attr; 
        
        return super.getAttribute(name); 
    }
    
    public Attribute[] getAttributes(String names[]) throws VrsException
    {
        if (names==null) 
            return null; 
        
        // optimized Gftp Attributes: 
        
        Attribute[] vattrs=server.getAttributes(this,getSftpAttributes(),names,false);
        
        for (int i=0;i<names.length;i++)
        {
            // get attribute which SftpAttrs don't have: 
            if (vattrs[i]==null)
                vattrs[i]=super.getAttribute(names[i]);
        }
        
        return vattrs; 
    }
    
    @Override
    public long getModificationTime() throws VrsException
    {
        return this.server.getModificationTime(getSftpAttributes()); 
    }

	public void setMode(int mode) throws VrsException
	{
		;
	}
	
    private SftpATTRS getSftpAttributes() throws VrsException
    {
        if (_attrs==null)
        {
            _attrs = server.getSftpAttrs(getPath());
        }
        return _attrs; 
    }
    
    public String getPermissionsString() throws VrsException
    {
        return server.getPermissionsString(getSftpAttributes(),false); 
    }

    public int getMode() throws VrsException
    {
         return server.getPermissions(getSftpAttributes()); 
    }
    
    
    public String getGid() throws VrsException
    {
        return ""+getSftpAttributes().getGId();  
    }

    public String getUid() throws VrsException
    {
        return ""+getSftpAttributes().getUId();  
    }
    
    public boolean sync() throws VrsException 
    {
        // just nullify attributes. Will be fetched again. Nothing to be written. 
        this._attrs=null;
        return true; 
    }
}
