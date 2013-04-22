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

package nl.nlesc.vlet.vfs.lfc;

import java.util.List;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.glite.lfc.LFCConfig;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.exception.NotImplementedException;
import nl.nlesc.vlet.exception.ResourceCreationFailedException;
import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.exception.VlConfigurationError;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.util.bdii.BdiiUtil;
import nl.nlesc.vlet.util.bdii.ServiceInfo;
import nl.nlesc.vlet.vfs.lfc.LFCFSConfig.ReplicaCreationMode;
import nl.nlesc.vlet.vfs.lfc.LFCFSConfig.ReplicaSelectionMode;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrl.VRLList;
import nl.nlesc.vlet.vrs.ResourceEvent;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vfs.FileSystemNode;
import nl.nlesc.vlet.vrs.vfs.VDir;
import nl.nlesc.vlet.vrs.vfs.VFSNode;
import nl.nlesc.vlet.vrs.vfs.VFile;



public class LFCFileSystem extends FileSystemNode
{
    
    private static ClassLogger logger; 
    {
        logger=ClassLogger.getLogger(LFCFileSystem.class); 
    }
    
    // ===
    // Class
    // ===
//    public static LFCFileSystem getServerNodeFor(VRSContext context,
//            VRL location) throws VlException
//    {
//        debug("Getting node for: " + location);
//
//        String serverID = ServerNode.createServerID(location);
//
//        LFCFileSystem lfcServerNode = (LFCFileSystem) context.getServerInstance(
//                serverID, LFCFileSystem.class);
//
//        if (lfcServerNode == null)
//        {
//            // store new client
//            ServerInfo lfcInfo = context.getServerInfoFor(location, true);
//            
//            LFCFSConfig.updateURIAttributes(lfcInfo,location.getQueryAttributes()); 
//            lfcInfo.store(); 
//            
//            lfcServerNode = new LFCFileSystem(context, lfcInfo);
//            lfcServerNode.setID(serverID);
//            context.putServerInstance(lfcServerNode);
//        }
//
//        return lfcServerNode;
//    }

    // ===
    // Instance
    // ===

    private LFCClient lfcClient;

    public LFCFileSystem(VRSContext context, ServerInfo info,VRL location)
            throws VlException
    {
        super(context, info);
        
        //Update check server info: 
        LFCFSConfig.updateURIAttributes(info,location.getQueryAttributes()); 
        info.store(); 
       
        this.lfcClient = new LFCClient(this, info.getHostname(), info
                .getPort());
        
        LFCConfig lfcConfig=new LFCConfig(); 
        lfcConfig.timeout=context.getConfigManager().getSocketTimeOut(); 
        lfcClient.setLFCConfig(lfcConfig); 
    }

    @Override
    public VFSNode openLocation(VRL loc) throws VlException
    {
        return this.lfcClient.openLocation(loc);
    }

    public void connect() throws VlException
    {
        this.lfcClient.connect();
    }

    public void disconnect() throws VlException
    {
        this.lfcClient.disconnect();
    }

    public boolean isConnected()
    {
        return this.lfcClient.isConnected();
    }
    
    /**
     * Returns list of hostname of preferred Storage Elements. 
     * Optional VRLs are parsed and only the hostname is returned. 
      */  
    public StringList getPreferredSEHosts()
    {
        // fetch NEW attribute!
        VAttribute listAttr = this.getServerInfo().getAttribute(LFCFSConfig.ATTR_PREFERREDSSES);

        if (listAttr == null)
            return new StringList(); 

        String listStr = listAttr.getStringValue();
        // emptry string "" results in String[0] list ! 
        if (StringUtil.isEmpty(listStr))
            return new StringList();

        String vals[] = listStr.split(",");
        StringList hosts=new StringList(); 
        
        for (String val:vals)
        {
            // heuristic to detect VRLs 
            if (val.contains(":"))
            {
                try
                {
                    VRL vrl=new VRL(val);
                    // use hostname from VRL: 
                    val=vrl.getHostname();
                    hosts.add(val); 
                }
                catch (VRLSyntaxException e)
                {
                   logger.warnPrintf("Not a vrl:%s\n",val); 
                } 
            }
            else
            {
                // cleanup:
                hosts.add(StringUtil.stripWhiteSpace(val));
            }
        }
                           
        return hosts;
    }
    
    /**
     * Returns VRLs of full specified SRM locations specified in the PreferedSEList.
     * Might be empty or null if nothing specified. SRM locations must match URI syntax to be parsed
     * and returned in this list.  
     * If an Preferred SE entry is NOT an URI then it is a normal 'hostname' and will be returned
     * in the getPreferredSEHosts() method.  
     * The hostname of the VRLs will be returned in that method as well. 
     */  
    public VRLList getPreferredSEVRLs()
    {
        // fetch NEW attribute!
        VAttribute listAttr = this.getServerInfo().getAttribute(LFCFSConfig.ATTR_PREFERREDSSES);

        if (listAttr == null)
            return null;

        String listStr = listAttr.getStringValue();
        // emptry string "" results in String[0] list ! 
        if (StringUtil.isEmpty(listStr))
            return null; 

        String vals[] = listStr.split(",");
        VRLList vrls=new VRLList(); 
        
        for (String val:vals)
        {
            // heuristic to detect VRLs 
            if (val.contains(":"))
            {
                try
                {
                    VRL vrl=new VRL(val);
                    // use hostname from VRL: 
                    vrls.add(vrl);  
                }
                catch (VRLSyntaxException e)
                {
                   logger.warnPrintf("Not a vrl:%s\n",val); 
                } 
            }
        }
                           
        return vrls;  
    }

    /** 
     * Return the  subdirectory name which contains 'generated' SURLS
     * default value is 'generated'. 
     * 
     * @return
     */
    public String getGeneratedSubdirName()
    {
        VAttribute attr = this.getServerInfo().getAttribute(LFCFSConfig.ATTR_GENERATED_DIRNAME);
        if ((attr!=null) && (attr.getStringValue()!=null))
            return attr.getStringValue(); 
        
        return "generated"; 
    }
    
    /**
     *  Return 'yyyy-MM-dd' scheme. 
     *  Default scheme is 'yyyy-MM-dd' scheme.  
     */ 
    public String getGeneratedSubDirDateScheme()
    {
        VAttribute attr = this.getServerInfo().getAttribute(LFCFSConfig.ATTR_GENERATED_SUBDIR_DATE_SCHEME); 
        
        if ((attr!=null) && (attr.getStringValue()!=null))
            return attr.getStringValue(); 
        
        return "generated"; 
    }

//    public void unregister(ITaskMonitor monitor, VRL selections)
//    {
//        // this.lfcClient.unregister(sel.getPath(), recursive);
//        Global.errorPrintln(this, "FIXME: Unregister entry:" + selections);
//    }
//
//    public void unregister(ITaskMonitor monitor,VRL selections[])
//    {
//        Global.errorPrintln(this, "FIXME: Unregister entries:" + selections);
//
//    }

    public void recurseDelete(ITaskMonitor monitor, VRL sel,boolean force) throws VlException
    {
        VFSNode node = lfcClient.getPath(sel.getPath());
        
        boolean result = this.lfcClient.recurseDelete(monitor,(ILFCLocation)node,force);
        if (result)
            this.getVRSContext().fireEvent(ResourceEvent.createDeletedEvent(sel));
    }
    
    public void recurseDelete(ITaskMonitor monitor,VRL[] selections,boolean force) throws VlException
    {
        for (VRL vrl : selections)
            recurseDelete(monitor,vrl,force);
    }

    @Override
    public LFCDir newDir(VRL dirVrl) throws VlException
    {
        FileDescWrapper wrapp = new FileDescWrapper();
        wrapp.setNameAndPath(dirVrl.getPath());
        return new LFCDir(this, wrapp);
    }

    @Override
    public LFCFile newFile(VRL fileVrl) throws VlException
    {
        FileDescWrapper wrapp = new FileDescWrapper();
        wrapp.setNameAndPath(fileVrl.getPath());
        return new LFCFile(this, wrapp);
    }

    @Override // Overridden for performance and type preservation (LFCFile) 
    public LFCFile getFile(VRL fileVrl) throws VlException
    {
        FileDescWrapper wrapp = new FileDescWrapper();
        wrapp.setNameAndPath(fileVrl.getPath());
        
        LFCFile file=new LFCFile(this, wrapp);
        
        if (file.exists()==false)
            throw new nl.nlesc.vlet.exception.ResourceNotFoundException("Couldn't find LFC file:"+fileVrl);
        
        return file; 
    }
    
    @Override // Overridden for performance and type preservation (LFCDir) 
    public LFCDir getDir(VRL dirVrl) throws VlException
    {
        // create object only!
        LFCDir dir=new LFCDir(this, dirVrl); 
        
        if (dir.exists()==false)
            throw new nl.nlesc.vlet.exception.ResourceNotFoundException("Couldn't find LFC directory:"+dirVrl);
        
        return dir; 
    }

    
    public VDir createDir(VRL dirVrl, boolean ignoreExisting) throws VlException
    {
        String dirname=dirVrl.getPath(); 

        lfcClient.mkdir(dirname, ignoreExisting);
        
        VFSNode node = lfcClient.getPath(dirname);
        if( !(node instanceof VDir))
        {
            throw new ResourceCreationFailedException("Faild to create "+dirVrl);
        }
        return (VDir) node;
    }

    public VFile createFile(VRL fileVrl, boolean ignoreExisting) throws VlException
    {
        String fileName = fileVrl.getPath(); 
        lfcClient.registerEntry(fileName);
        return (LFCFile) lfcClient.getPath(fileName);
    }

    public LFCClient getLFCClient()
    {
        return lfcClient;
    }

    public void pastAsLink(ITaskMonitor monitor, VRL pasteDirDestination, VRL[] selections) throws VlException
    {
        monitor.logPrintf("Pasting alias into directory:"+pasteDirDestination+"\n"); 
        
        for (VRL sel:selections)
        {
            VRL aliasVRL=pasteDirDestination.appendPath(sel.getBasename()); 
            ILFCLocation orgNode = (ILFCLocation)this.getNode(sel); // will throw exception if it doesn't exists. 
            String guid=orgNode.getGUID(); 
            monitor.logPrintf("Creating symbolic link:'"+aliasVRL.getPath()+"' ==> '"+sel.getPath()+"' ('"+guid+"')\n");
            // source path mustt exist ! 
             
            if (orgNode.isFile()==true)      
            {
                aliasVRL= ((LFCFile)orgNode).addAlias(aliasVRL); 
                //
                // Dynamic Action: must update gui ourselfs.  
                //
                this.getVRSContext().fireEvent(ResourceEvent.createChildAddedEvent(pasteDirDestination, aliasVRL)); 
            }    
            else
            {
               // allowed ? 
               throw new NotImplementedException("Adding Alias to Directory not supported"); 
            }
        } 
    }

   public ReplicaSelectionMode getReplicaSelectionMode() throws VlConfigurationError 
   {
      VAttribute attr = this.getServerInfo().getAttribute(LFCFSConfig.ATTR_REPLICA_SELECTION_MODE);

      if (attr!=null)
          return ReplicaSelectionMode.createFromAttributeValue(attr.getStringValue()); 
      
      throw new VlConfigurationError("Couldn't get Replica Selection Mode"); 
 
    }
   
   public ReplicaCreationMode getReplicaCreationMode() throws VlConfigurationError 
   {
       VAttribute attr = this.getServerInfo().getAttribute(LFCFSConfig.ATTR_REPLICA_CREATION_MODE);

       if (attr!=null)
           return ReplicaCreationMode.createFromAttributeValue(attr.getStringValue()); 
       
       throw new VlConfigurationError("Couldn't get Replica Creation mode");  
    }

   public int getReplicasNrOfTries() 
   { 
       VAttribute attr = this.getServerInfo().getAttribute(LFCFSConfig.ATTR_REPLICA_NR_OF_TRIES);
       
       if (attr!=null)
           return attr.getIntValue();
       
       return 5; 
   }

public boolean getUseSimilarReplicaNames()
{
    VAttribute attr = this.getServerInfo().getAttribute(LFCFSConfig.ATTR_REPLICA_NAME_CREATION_POLICY);
    
    boolean defVal=false; 
    if (attr!=null)
    {
        String valStr=attr.getStringValue();
        if (StringUtil.equals(valStr,LFCFSConfig.REPLICA_NAME_POLICY_SIMILAR))
            defVal=true;
    }
    
    return defVal; 
}
 
 public void replicateToPreferred(ITaskMonitor monitor, VRL[] selections) throws VlException
 {
     StringList listSEs = this.getPreferredSEHosts(); 
     
     if ((listSEs==null) || (listSEs.size()<=0))
         throw new nl.nlesc.vlet.exception.VlConfigurationError("No preferred Storage Elements configured.\n"
                 +"Please set listPreferredSEs in your LFC config");
     
     getLFCClient().replicate(monitor,selections,listSEs); 
 }
 
 public void recursiveReplicateToPreferred(ITaskMonitor monitor, VRL dirVRL) throws VlException
 {
     replicateDirectory(monitor,dirVRL,this.getPreferredSEHosts());
 }

 public VFile replicateFile(ITaskMonitor monitor, VRL fileVrl, String storageElement) throws VlException
 {
	 LFCFile file=getFile(fileVrl); 
	 return getLFCClient().replicateFile(monitor, file, storageElement); 
 }
 
 public void replicateDirectory(ITaskMonitor monitor,VRL lfcDir, List<String> listSEs) throws VlException
 {
     LFCDir dir=getDir(lfcDir);
     replicateDirectory(monitor,dir,listSEs); 
 }
 
 public void replicateDirectory(ITaskMonitor monitor,LFCDir lfcDir, List<String> listSEs) throws VlException
 {
     getLFCClient().replicateDirectory(monitor, lfcDir, listSEs); 
 }
 
  //====
  // Potetential options should it be necessary to change this 
  //===

 /** 
  * Whether only Preferred Storage Element may be used, or in the case no Preferred SEs 
  * can be found, a random VO allowed SE may be used   
  * @return if true: restrict SE access to the SEs mentioned in the preferred list. 
  */
 public boolean getStrictPreferredMode()
 {
    return false;
 }

 /** 
  * Whether replicas should be checked for inconsistencies.
  * LFC File sizes might be 0 while replicas might not be zero. 
  */
 public boolean hasStrictReplicaPolicy()
 {
    return false;
 }

 public VRL updateSRMV22location(VRL vrl) 
 {
	try
	{
		// update Storage Element contact information. 
		ServiceInfo se = BdiiUtil.getBdiiService(getContext()).getSRMv22ServiceForHost(vrl.getHostname());
		
        if (se != null)
        {
            vrl=vrl.copyWithNewPort(se.getPort());
            return vrl;
        }
	}
	catch (Exception e)
	{
		ClassLogger.getLogger(LFCFileSystem.class).logException(ClassLogger.WARN,this,e,"Couldn't resolve SRM V2.2 location:%s\n", vrl);
	}
        // return default
	return vrl; 
 }

}
