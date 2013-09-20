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

package nl.esciencecenter.vlet.gui.viewers.grid.replicaviewer;

import java.io.IOException;
import java.util.ArrayList;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.util.bdii.BdiiUtil;
import nl.esciencecenter.vlet.util.bdii.StorageArea;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;
import nl.esciencecenter.vlet.vrs.io.VResizable;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.vlet.vrs.vfs.VFile;
import nl.esciencecenter.vlet.vrs.vfs.VLogicalFileAlias;
import nl.esciencecenter.vlet.vrs.vfs.VReplicatable;


public class ReplicaManager
{
    private VRSContext vrsContext; 
    private VFSClient vfsClient; 
    
    private VRL vrl;
    private VFSNode node; 
    
    public ReplicaManager(VRSContext context)
    {
        this.vrsContext=context;
        this.vfsClient=new VFSClient(vrsContext); //private client 
    }

    public StringList getStorageAreasForVo(String vo) throws VrsException 
    {
        StringList seList=new StringList(); 
        
        ArrayList<StorageArea> prefSAs = BdiiUtil.getBdiiService(vrsContext).getSRMv22SAsforVO(vo); 
        for (StorageArea sa:prefSAs)
        {
            seList.addUnique(sa.getHostname()); 
        }
        
        // sort in memory! ignore case
        seList.sort(true); 
        return seList;
    }
    
    public void setVRL(VRL vrl)
    {
        this.vrl=vrl;
        this.node=null; // clear cached node!
    }
    
    public VRL[] getReplicaVRLs() throws VrsException
    {
        this.node=getNode(); 
            
        if ((node instanceof VReplicatable)==false) 
        {   
            return null; 
        }
                
        VRL replicas[]=((VReplicatable)node).getReplicas();
        return replicas;          
    }
    
//    public ArrayList<ReplicaInfo> getReplicaInfos() throws VlException
//    {
//        ArrayList<ReplicaInfo> repInfos=new ArrayList<ReplicaInfo>(); 
//        boolean getTransportURIs=false; 
//        StringList replicaHosts=new StringList();
//        
//        VRL replicas[]=getReplicaVRLs(); 
//                
//        for (VRL replica:replicas)
//        {
//            ReplicaInfo repInfo=new ReplicaInfo(replica); 
//            StringList attrNames=new StringList();
//            attrNames.add(VAttributeConstants.ATTR_LENGTH);
//            
//            if (getTransportURIs)
//            {
//                attrNames.add(VAttributeConstants.ATTR_TRANSPORT_URI); 
//            }
//            // catch IN LOOP exceptions  
//            try
//            {
//                VNode repNode=vfsClient.getNode(replica); 
//                VAttributeSet attrSet=repNode.getAttributeSet(attrNames.toArray());
//                repInfo.setLength(attrSet.getLongValue(VAttributeConstants.ATTR_LENGTH)); 
//                repInfo.setTransportURI(attrSet.getVRLValue(VAttributeConstants.ATTR_TRANSPORT_URI));
//                if (repNode.exists()==false)
//                {
//                    repInfo.setError(true);  
//                    repInfo.setExists(false);   
//                }
//                else
//                {
//                    repInfo.setError(false); 
//                    repInfo.setExists(true);  
//                }
//
//            }
//            catch (Exception e)
//            {
//                repInfo.setError(true); 
//                repInfo.setException(e);   
//            }
//            
//            repInfos.add(repInfo); 
//        }//for
//        
//        return repInfos;        
//    }

    /** Get Default Replica Attributes */ 
    public AttributeSet getReplicaAttributes(VRL repVRL, boolean checksumInfo) throws VrsException
    {
        StringList attrNames=new StringList(); 
        attrNames.add(VAttributeConstants.ATTR_TRANSPORT_URI);
        attrNames.add(VAttributeConstants.ATTR_FILE_LENGTH);
        attrNames.add(VAttributeConstants.ATTR_EXISTS);
        
        if (checksumInfo)
        {
            attrNames.add(VAttributeConstants.ATTR_CHECKSUM);
            attrNames.add(VAttributeConstants.ATTR_CHECKSUM_TYPE);
            attrNames.add(VAttributeConstants.ATTR_CHECKSUM_TYPES);
        }
        
        return getReplicaAttributes(repVRL,attrNames); 
    }
    
    public AttributeSet getReplicaAttributes(VRL repVrl,StringList attrs) throws VrsException
    {
        VNode repNode=vrsContext.openLocation(repVrl);  
        AttributeSet attrSet=repNode.getAttributeSet(attrs.toArray());
        
        if (attrs.contains(VAttributeConstants.ATTR_EXISTS))
        {
            // in case ATTR_EXISTS isn't supported/doesn't work! 
            attrSet.set(VAttributeConstants.ATTR_EXISTS,node.exists());
        }
        
        return attrSet; 
    }

    public void addReplica(ITaskMonitor monitor, String se) throws VrsException
    {
        VReplicatable rep=getRepFile();
        rep.replicateTo(monitor,se); 
    }

    private VReplicatable getRepFile() throws VrsException
    {
        this.node=getNode(); 
        
        if  ((node instanceof VReplicatable)==false)
            throw new VrsException("Resource doesn't have replicas:"+vrl);
        
        return (VReplicatable)node;  
    }
    
    private VFSNode getNode() throws VrsException
    {
        if (this.node==null)
            this.node=vfsClient.getVFSNode(vrl);
        
        return node; 
    }

    public void deleteReplica(ITaskMonitor monitor, String se) throws VrsException
    {
        VReplicatable rep=getRepFile();
        rep.deleteReplica(monitor,se); 
    }
    
    public void unregisterReplica(ITaskMonitor monitor, String se) throws VrsException
    {
        VReplicatable rep=getRepFile();
        VRL reps[]=rep.getReplicas(); 
        
        for (VRL vrl:reps)
        {
            if (vrl.hasHostname(se))
                rep.unregisterReplicas(new VRL[]{vrl}); 
        }
    }

    public long getFileSize() throws Exception
    {
        this.node=getNode(); 
    
        if (node instanceof VFile) 
            return ((VFile)node).getLength(); 
        return -1; 
    }

    public void updateLFCFileSize(long size) throws Exception
    {
        this.node=getNode(); 
        // Special interface for LFC files. 
        if (node instanceof VLogicalFileAlias) 
            ((VLogicalFileAlias)node).updateFileSize(size); 
        else if (node instanceof VResizable) 
            ((VResizable)node).setLength(size); 
        else
            throw new NestedIOException("File size can't be set for:"+node.getClass()+":"+node); 
    }
    
}
