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

package nl.nlesc.vlet.vfs.jcraft.ssh;

import java.util.Vector;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrs.tasks.VRSTaskMonitor;
import nl.nlesc.vlet.vrs.vfs.VDir;
import nl.nlesc.vlet.vrs.vfs.VFSNode;
import nl.nlesc.vlet.vrs.vfs.VUnixFileAttributes;
import nl.nlesc.vlet.vrs.vrl.VRL;

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
    public VFSNode[] list() throws VlException
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
              String filepath=this.getPath()+VRL.SEP_CHAR+childs[i];
              nodes.add(server.getPath(filepath)); 
            }
        }
        
        return VFSNode.returnAsArray(nodes); 
    }

    public boolean create(boolean ignoreExisting) throws VlException
    {
    	VDir dir=this.server.createDir(this.getPath(),ignoreExisting);
    	return (dir!=null); 
    }
    
    @Override
    public boolean exists() throws VlException
    {
    	return server.existsPath(this.getPath(),true);
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
    
    public long getNrOfNodes() throws VlException
    {
        return getNodes().length; 
    }

    public boolean delete(boolean recurse) throws VlException
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
    
    public VRL rename(String newName, boolean nameIsPath) throws VlException
    {
        String newpath=server.rename(getPath(),newName,nameIsPath); 
        return this.resolvePath(newpath); 
    }
    
    @Override
    public VAttribute[][] getACL() throws VlException
    {
        return server.getACL(getPath(),true); 
    }
    @Override
    public void setACL(VAttribute acl[][]) throws VlException
    {
        server.setACL(getPath(),acl,true); 
    }
    
    /** Returns all default attributes names */ 
    public String[] getAttributeNames()
    {
        String superNames[]=super.getAttributeNames();
       
        return StringList.merge(superNames,SftpFSFactory.sftpDirAttributeNames); 
    }
   
    
    public VAttribute getAttribute(String name) throws VlException
    {
        if (name==null) 
            return null;
        
        // update attributes: 
        VAttribute  attr=this.getStaticAttribute(name); 
        if (attr!=null)
            return attr; 
        
        // update attributes: 
        attr=server.getAttribute(this,getSftpAttributes(),name,false,true);
        
        if (attr!=null)
            return attr; 
        
        return super.getAttribute(name); 
    }
    
    public VAttribute[] getAttributes(String names[]) throws VlException
    {
        if (names==null) 
            return null; 
        
        // optimized Gftp Attributes: 
        
        VAttribute[] vattrs=server.getAttributes(this,getSftpAttributes(),names,false);
        
        for (int i=0;i<names.length;i++)
        {
            // get attribute which SftpAttrs don't have: 
            if (vattrs[i]==null)
                vattrs[i]=super.getAttribute(names[i]);
        }
        
        return vattrs; 
    }
    
    @Override
    public long getModificationTime() throws VlException
    {
        return this.server.getModificationTime(getSftpAttributes()); 
    }

	public void setMode(int mode) throws VlException
	{
		;
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
        // just nullify attributes. Will be fetched again. Nothing to be written. 
        this._attrs=null;
        return true; 
    }
}
