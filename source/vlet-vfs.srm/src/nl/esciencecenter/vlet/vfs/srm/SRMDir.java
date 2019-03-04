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

package nl.esciencecenter.vlet.vfs.srm;

import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;

import java.io.OutputStream;
import java.util.ArrayList;

import nl.esciencecenter.ptk.data.IntegerHolder;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.NodeFilter;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.vlet.vrs.vfs.VFile;
import nl.esciencecenter.vlet.vrs.vfs.VUnixFileMode;


/**
 * SRMDir
 * 
 * @author Piter T. de Boer
 */
public class SRMDir extends VDir implements VUnixFileMode
{
    // private PathDetail srmDetails;
    private TMetaDataPathDetail srmDetails = null;

    private SRMFileSystem srmfs;

    private DirQuery dirQuery=null; 

    public SRMDir(SRMFileSystem client, TMetaDataPathDetail details, DirQuery dirQ)
    {
        super(client, (VRL) null);
        this.srmfs = client;
        this.srmDetails = details;
        this.dirQuery=dirQ;
        // recreate Dir VRL ! 
        VRL vrl = client.createPathVRL(details.getPath(),null);
        this.setLocation(vrl);
    }

    public SRMDir(SRMFileSystem client, VRL vrl)
    {
        super(client, (VRL) null);
        this.srmfs = client;

        // parse dir query or return null object! 
        this.dirQuery=DirQuery.parseDirQuery(vrl);
        
        // update VRL !
        VRL parsedVrl = client.createPathVRL(vrl.getPath(),dirQuery);
        this.setLocation(parsedVrl);
    }

    public boolean create(boolean force) throws VrsException
    {
        VDir dir = srmfs.createDir(getPath(), force);
        return (dir != null);
    }

    public VFile getFile(String name) throws VrsException
    {
        VFSNode node = this.srmfs.getPath(this.getPath() + "/" + name);

        if (node instanceof VFile)
            return (VFile) node;

        throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Path is not a file:" + node);
    }

    public VDir getDir(String name) throws VrsException
    {
        VFSNode node = this.srmfs.getPath(this.getPath() + "/" + name);

        if (node instanceof VDir)
            return (VDir) node;

        throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Path is not a dir:" + node);
    }

    @Override
    public VFSNode[] list() throws VrsException
    {
        if (this.dirQuery!=null)
        {
            debugPrintf("SRMDir:list() >>> offset=%d,count=%d\n",dirQuery.getOffset(),this.dirQuery.getCount()); 
            VFSNode nodes[]=this.srmfs.list(this.getPath(),this.dirQuery.getOffset(),dirQuery.getCount());
            debugPrintf("SRMDir:list() dirQuery resulted in:%s nodes !\n",(nodes==null)?"0":nodes.length);
            
            return nodes; 
        }
        else
        {
            return this.srmfs.list(this.getPath());
        }
    }
    
    // override core method of VDir 
    public VFSNode[] list(NodeFilter filter,int offset,int maxNodes,IntegerHolder totalNumNodes) throws VrsException
    {
        // use incremental listing ! 
        VFSNode[] nodes = this.srmfs.list(this.getPath(),offset,maxNodes);
        
        if (totalNumNodes!=null)
            totalNumNodes.set(-1); // not supported!
        
        if (filter!=null)
        {
            nodes=NodeFilter.filterNodes(nodes,filter); 
        }
        
        return nodes; 
    }

    @Override
    public boolean exists() throws VrsException
    {
        if (srmfs.pathExists(getPath()) == false)
            return false;

        // path exists, now check for directory type !
        return this.srmfs.isDirectory(this.fetchFullDetails().getType());

    }

    @Override
    public boolean isReadable() throws VrsException
    {
        if (getPermissionsString().toLowerCase().charAt(1) == 'r')
        {
            return true;
        }
        return true;
    }

    @Override
    public boolean isWritable() throws VrsException
    {
        if (getPermissionsString().toLowerCase().charAt(1) == 'w')
        {
            return true;
        }
        return true;
    }

    public boolean delete(boolean recurse) throws VrsException
    {
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("Deleting (SRM) directory:" + this.getPath(), 1);
        monitor.logPrintf("Deleting SRM Directory:" + this + "\n");
        return srmfs.rmdir(getPath(), recurse);
    }

    public long getNrOfNodes() throws VrsException
    {
        VFSNode[] nodes = getNodes();
        if (nodes == null)
            return 0;

        return nodes.length;
    }

    public VRL rename(String newName, boolean renameFullPath) throws VrsException
    {
        String newPath = null;

        if ((renameFullPath == true) || (newName.startsWith("/")))
            newPath = newName;
        else
            newPath = getVRL().getDirname() + "/" + newName;

        return srmfs.mv(getPath(), newPath);
    }

    @Override
    public long getModificationTime() throws VrsException
    {
        // is included in the simple query
        return this.srmfs.createModTime(srmDetails);

    }

    private TMetaDataPathDetail fetchFullDetails() throws VrsException
    {
        // check if full details have been fetched. If not fetch them.
        if (srmDetails == null)
        {
            srmDetails = srmfs.queryPath(getPath());
        }

        return srmDetails;
    }

    // ===
    // Upload and Storage Methods
    // ==

    public OutputStream createFileOutputStream(String fileName, boolean force) throws VrsException
    {
        String srmFilePath = resolvePathString(fileName);
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("createFileOutputStream:" + srmFilePath, -1);

        // // use absolute path:
        // if (fileName.startsWith("/")==true)
        // {
        // srmFilePath=fileName;
        // }
        // else
        // {
        // // create full path:
        // srmFilePath=this.getPath()+"/"+fileName;
        // }
        // TODO: force=true means, overwrite existing first !
        return this.srmfs.createNewOutputStream(monitor, srmFilePath, force);
    }

    // bulk method to fetch all transport VRLs at once;
    public VRL[] getTransportVRLsForChildren() throws VrsException
    {
        // list full details:
        VFSNode[] nodesArray = list(); 

        ArrayList<String> filePathsList = new ArrayList<String>();
        // get file paths
        for (int i = 0; i < nodesArray.length; i++)
        {
            if (nodesArray[i] instanceof SRMFile)
            {
                filePathsList.add(nodesArray[i].getPath());
            }
            // else skip directories!
        }

        // get transfer urls
        String[] filePathsArray = new String[filePathsList.size()];
        filePathsArray = filePathsList.toArray(filePathsArray);

        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor("getTransportVRLsForChildern", -1);
        VRL[] tVrls = srmfs.getTransportVRLs(monitor, filePathsArray);

        return tVrls;
    }

    public int getMode() throws VrsException
    {
        fetchFullDetails();
        return this.srmfs.getUnixMode(srmDetails);
    }

    public void setMode(int mode) throws VrsException
    {
        fetchFullDetails();
        this.srmfs.setUnixMode(getLocation(), this.srmDetails, mode);
    }

    public void setDirQuery(String[] qstrs)
    {
        this.dirQuery=new DirQuery(qstrs);
    }
    
    private void debugPrintf(String format,Object... args)
    {
        SRMFileSystem.getLogger().debugPrintf("SRMDir:"+format,args); 
        //System.err.printf(format,args); 
    }
}
