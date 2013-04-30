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

package nl.nlesc.vlet.vfs.srm;

import gov.lbl.srm.v22.stubs.TFileStorageType;
import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;
import gov.lbl.srm.v22.stubs.TRetentionPolicyInfo;

import java.io.IOException;
import java.io.InputStream;

import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.vrs.data.VAttributeConstants;
import nl.nlesc.vlet.vrs.vfs.VChecksum;
import nl.nlesc.vlet.vrs.vfs.VFile;
import nl.nlesc.vlet.vrs.vfs.VFileActiveTransferable;
import nl.nlesc.vlet.vrs.vfs.VTransportable;
import nl.nlesc.vlet.vrs.vfs.VUnixFileMode;

import org.apache.axis.types.UnsignedLong;

/**
 * SRM File 
 * 
 * @author Piter T. de Boer
 */
public class SRMFile extends VFile implements VFileActiveTransferable,
        VUnixFileMode, VChecksum, VTransportable 
{
    // private PathDetail srmDetails;
    private TMetaDataPathDetail srmDetails;
    
    private SRMFileSystem srmfs;
    
    private boolean detailsFetched=false; 

    public SRMFile(SRMFileSystem srmfs, TMetaDataPathDetail info)
            throws VRLSyntaxException
    {
        super(srmfs, (VRL) null);
        this.srmDetails = info;
        this.srmfs = srmfs;
        this.detailsFetched=(srmDetails!=null); 
        
        VRL vrl = srmfs.createPathVRL(info.getPath(),null);
        this.setLocation(vrl);
    }

    public SRMFile(SRMFileSystem client, String path)
            throws VRLSyntaxException
    {
        super(client, (VRL) null);
        this.detailsFetched=(srmDetails!=null);
        this.srmfs=client; 
        
        VRL vrl = srmfs.createPathVRL(path,null);
                this.srmfs = client;
        this.setLocation(vrl);
    }
    
    @Override
    public boolean create(boolean force) throws VrsException
    {
        VFile file = srmfs.createFile(getVRL(), force);
        return (file != null);
    }
    
    @Override
    public String[] getAttributeNames()
    {
        String superList[] = super.getAttributeNames();
        StringList attrList = new StringList(superList);

        attrList.add(SRMConstants.ATTR_SRM_RETENTION_POLICY);
        attrList.add(SRMConstants.ATTR_SRM_STORAGE_TYPE); 
        
        attrList.add(VAttributeConstants.ATTR_TRANSPORT_URI);

        return attrList.toArray();
    }

    @Override
    public Attribute[] getAttributes(String names[]) throws VrsException
    {
        Attribute[] attrs = new Attribute[names.length];

        // optional caching:
        for (int i = 0; i < names.length; i++)
        {
            if (names[i] != null)
                attrs[i] = getAttribute(names[i]);
            else
                attrs[i] = null;
        }

        return attrs;
    }

    @Override
    public boolean setAttribute(Attribute attr) throws VrsException
    {
        String name = attr.getName();

        if (name.compareTo(SRMConstants.ATTR_SRM_RETENTION_POLICY) == 0)
        {
            // update policy
        }

        return false;
    }
    
    @Override
    public Attribute getAttribute(String name) throws VrsException
    {
        Attribute attr = null;

        if (name.compareTo(SRMConstants.ATTR_SRM_RETENTION_POLICY) == 0)
        {
            attr = new Attribute(name, this.getRetentionPolicy());
            //Not Now: it is possible to set this attribute using setAttribute() method.
           // attr.setEditable(true);
        }
        else if (name.compareTo(SRMConstants.ATTR_SRM_STORAGE_TYPE) == 0)
        {
            attr = new Attribute(name, this.getFileStorageType());
        }
        else if (name.compareTo(VAttributeConstants.ATTR_TRANSPORT_URI) == 0)
        {
            // A VRL is an URI.
            attr = new Attribute(name, this.getTransportVRL());
        }
        // else if (name.startsWith(VAttributeConstants.ATTR_CHECKSUM))
        // {
        // attr = new VAttribute(name, this.getChecksum(getChecksumTypes()[0]));
        // }
        else
        {
            // call super:
            attr = super.getAttribute(name);
        }

        return attr;
    }

    private String getRetentionPolicy() throws VrsException
    {
        fetchFullDetails();

        TRetentionPolicyInfo info = this.srmDetails.getRetentionPolicyInfo();
        
        if (info == null)
            return "";

        return info.getRetentionPolicy().getValue();

    }

    private String getFileStorageType() throws VrsException
    {
       
        fetchFullDetails();

        TFileStorageType storageType = this.srmDetails.getFileStorageType();
        
        if (storageType == null)
        {
            //logger.warnPrintf("StorageTYpe==null => Requery Type for:%s\n",this);
            // requery: ls doesn't always return storage type 
            this.srmDetails=null; 
            this.fetchFullDetails();
        }
        
        storageType = this.srmDetails.getFileStorageType();
        if (storageType == null)
        {
            //Global.warnPrintln(this,"Really no StorageType for"+this);
            return "";
        }
        
        String valstr=storageType.getValue();
        //Global.debugPrintln(this,"StorageType for"+this+"="+valstr); 
        return valstr; 
    }
    
    @Override
    public long getLength() throws IOException
    {
        try
        {
            fetchFullDetails();
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(),e); 
        }
        
        UnsignedLong value = this.srmDetails.getSize();

        if (value == null)
            return -1;

        return value.longValue();
    }

    private TMetaDataPathDetail fetchFullDetails() throws VrsException
    {
        if (srmDetails==null)
        {
            srmDetails = srmfs.queryPath(getPath());
        }
        return srmDetails; 
    }

    @Override
    public boolean exists() throws VrsException
    {
    	if (srmfs.pathExists(getPath())==false)
    		return false;
    	
    	// path exists, now check for file type ! 	
    	return this.srmfs.isFile(this.fetchFullDetails().getType());  
    }

    @Override
    public boolean isReadable() throws VrsException
    {

        if (getPermissionsString().charAt(1) == 'R')
        {
            return true;
        }
        return false;
    }

    @Override
    public boolean isWritable() throws VrsException
    {
        if (getPermissionsString().charAt(2) == 'W')
        {
            return true;
        }
        return false;
    }
    
    @Override
    public InputStream createInputStream() throws IOException
    {
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                "getInputStream:" + this, -1);
        try
        {
            return srmfs.createInputStream(monitor, this.getPath());
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(),e); 
        }

    }

    public VRL getTransportVRL() throws VrsException
    {
        ITaskMonitor monitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                "getInputStream:" + this, -1);
        return srmfs.getTransportVRL(monitor, this.getPath());
    }
    
    @Override
    public SRMOutputStream createOutputStream() throws IOException
    {
        ITaskMonitor minitor = getVRSContext().getTaskWatcher().getCurrentThreadTaskMonitor(
                "getOutputStream:" + this, -1);

        String orgPath = getPath();
        long val = SRMFileSystem.fileRandomizer.nextLong();
        String randStr = null;
        // Beautification: avoid '-' before number
        if (val < 0)
            randStr = "9" + (-val);
        else
            randStr = "" + val;

        String tmpPath = orgPath + "." + randStr;

        try
        {
            SRMOutputStream outps = srmfs.createNewOutputStream(minitor,
                    tmpPath, true);
            // replace current file after closing of outputstream with new
            // created file
            outps.setFinalPathAfterClose(tmpPath, orgPath);
            
            
            //the file size will change. Get rid of the cache 
            detailsFetched = false;
            srmDetails = null;

            return outps;
        }
        catch (Exception e)
        {
            throw new IOException(e.getMessage(),e); 
        }
        
     }
    
    @Override
    public VRL rename(String newName, boolean renameFullPath)
            throws VrsException
    {
        if (renameFullPath)
        {
            return srmfs.mv(getPath(), newName);
        }
        return srmfs.mv(getPath(), getVRL().getParent().getPath() + "/"
                + newName);
    }
    
    @Override
    public boolean delete() throws VrsException
    {
        return this.srmfs.deleteFile(this.getPath());
    }

    @Override
    public long getModificationTime() throws VrsException
    {
        // no full details needed since the default details
        // already contain the modification time.
        return this.srmfs.createModTime(srmDetails);

    }

    @Override
    public VFile activePartyTransferFrom(ITaskMonitor monitor,
            VRL remoteSourceLocation) throws VrsException
    {
        return this.srmfs.doActiveTransfer(monitor, remoteSourceLocation, this);
    }

    @Override
    public VFile activePartyTransferTo(ITaskMonitor monitor,
            VRL remoteTargetLocation) throws VrsException
    {
        return this.srmfs.doTransfer(monitor, this, remoteTargetLocation);
    }

    @Override
    public boolean canTransferFrom(VRL remoteLocation, StringHolder explanation)
            throws VrsException
    {
        return this.srmfs.checkTransferLocation(remoteLocation,
                explanation, false);
    }

    @Override
    public boolean canTransferTo(VRL remoteLocation, StringHolder explanation)
            throws VrsException
    {
        return this.srmfs.checkTransferLocation(remoteLocation,
                explanation, true);
    }
    
    @Override
    public SRMFileSystem getFileSystem()
    {
        return (SRMFileSystem) super.getFileSystem();
    }
    
    @Override
    public int getMode() throws VrsException
    {
        fetchFullDetails();
        return this.srmfs.getUnixMode(srmDetails);
    }
    
    @Override
    public void setMode(int mode) throws VrsException
    {
        fetchFullDetails();
        this.srmfs.setUnixMode(getLocation(), this.srmDetails, mode);
    }

    @Override
    public String getChecksum(String algorithm) throws VrsException
    {
        fetchFullDetails();
        String checksum = null;
        String srmChecksumType = srmDetails.getCheckSumType();
        
        // can we get the requested algorithm??
        if (srmChecksumType!=null && srmChecksumType.equalsIgnoreCase(algorithm))
        {
            //Debug("Got checksum from SRM service");
            checksum = srmDetails.getCheckSumValue();
        }
        if (checksum == null)
        {            
            //Debug("Gouldn't get checksum from SRM service, trying from transfer VRL.");

            VRL tVRL = getTransportVRL();
            //Debug("Got transfer VRL: " + tVRL);
            VFile file = srmfs.getVFSClient().newFile(tVRL);
            //Debug("Got VFile: ");
            if (file instanceof VChecksum)
            {
                checksum = ((VChecksum) file).getChecksum(algorithm);
            }
        }
        return checksum;
    }

    @Override
    public String[] getChecksumTypes() throws VrsException
    {
        fetchFullDetails();
        
        //add srm service types 
        StringList types = new StringList();  
        String type =  srmDetails.getCheckSumType();
                
        if(type!=null){
            types.add(type);
        }
//        //and tVRL types 
//        VRL tVRL = getTransportVRL();
//        VFile file = srmfs.getVFSClient().newFile(tVRL);
//        if (file instanceof VChecksum)
//        {
//            types.add(((VChecksum) file).getChecksumTypes());
//        }
        
        return types.toArray();
    }

}
