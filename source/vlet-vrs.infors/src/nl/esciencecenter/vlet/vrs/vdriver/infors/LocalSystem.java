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

package nl.esciencecenter.vlet.vrs.vdriver.infors;

import java.io.File;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.data.BooleanHolder;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.LinkNode;
import nl.esciencecenter.vlet.vrs.VEditable;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;
import nl.esciencecenter.vlet.vrs.vdriver.infors.net.NetworkNode;
import nl.esciencecenter.vlet.vrs.vrms.ConfigManager;

public class LocalSystem extends CompositeServiceInfoNode<VNode> implements VEditable
{
	
	public LocalSystem(VRSContext context)
	{
		super(context, new VRL(InfoConstants.INFO_LOCALSYSTEM_SCHEME,null,"/"));
		
		try
        {
            initChilds();
        }
        catch (VrsException e)
        {
            VletConfig.getRootLogger().logException(ClassLogger.ERROR,e,"LocalSystem:Failed to initialize childs:"); 
        } 
		
		//	serverInstances=new ServerInstanceGroup(vrsContext); 
		//gridServices=new LoVOGroupsNode(this,vrsContext);       
		//VNode nodes[]=new VNode[1];
	        
		//nodes[0]=gridServices; 
		//nodes[1]=serverInstances;
	        
		// update super class internal array! 
		//setChilds(nodes); 

		this.setEditable(true); 
	} 

    @Override
    public String getResourceType()
    {
        return InfoConstants.LOCALSYSTEM_TYPE; 
    }
    
    @Override
    public String getName()
    {
        return InfoConstants.LOCALSYSTEM_NAME;  
    }

	public String getIconURL()
	{
	    return "system-128.png"; 
	}
	
	public String getMimeType(){return null;} 
	

	public String[] getResourceTypes()
	{
	      return null; 
	}

	public VRL getDescriptionLocation() throws VrsException
	{
		return null; 
	}

    public VRL createChildVRL(String childName) 
    {
        return new VRL(this.getScheme(),null,null,-1,getPath()+"/"+childName);
        
    }

    public VNode createNode(String type, String name, boolean force)
        throws VrsException
    {
        if (StringUtil.equals(type,InfoConstants.NETWORK_INFO))
        {
            return createNetworkNode(name); 
        }
        
        throw new nl.esciencecenter.vlet.exception.ResourceTypeNotSupportedException("Cannot create resource of type:"+type);
    }

    private NetworkNode createNetworkNode(String name) throws VrsException
    {
        if ((name==null) || (name.equals("")))
            name="New Network"; 
        
        VRL childVRL=this.createChildVRL(name);
        NetworkNode node=new NetworkNode(this.getVRSContext(),childVRL);
        // add to internal vector 
        this.addSubNode(node); 
        return node; 
        
    }
    
    private void initChilds() throws VrsException
    {
    	this.clearChilds(); 
    	
    	VRL userHome=vrsContext.getUserHomeLocation();
    	
     // start with userHome:
        addPathNode(userHome,"Home","default/home_folder.png");
        // debugging:
        if (VletConfig.getRootLogger().isLevelDebug());
            addPathNode(vrsContext.getConfigManager().getUserConfigDir(),"VletRC","config-folder-48.png");
        addFilesystemRoots(); 
    }

    private void addPathNode(VRL targetPath,String logicalname,String iconUrl) throws VrsException
    {
        VRL childVRL=this.createChildVRL(logicalname);
        LinkNode lnode = LinkNode.createLinkNode(vrsContext,childVRL,targetPath); 
        lnode.setName(targetPath.getPath()); 
        // do not show the shortcut image
        lnode.setShowShortCutIcon(false); 
        lnode.setLogicalParent(this); 

        lnode.setIconURL(iconUrl);
        // hardcoded node:
        lnode.setEditable(false); 
        this.addSubNode(lnode); 
    }

    /** Scan filesystem root and add them to the rootNodes */ 
    private void addFilesystemRoots() throws VrsException
    {
        File roots[]=null; 

        // for windows this method returns the drives: 
        if (GlobalProperties.isWindows()==true)
        {
            // alt get drives to avoid annoying pop-up
            roots=VletConfig.getWindowsDrives();
        }
        else 
        {
            // should trigger initialize Security Context
            @SuppressWarnings("unused")
            SecurityManager sm = System.getSecurityManager();
            // disable ? 
            System.setSecurityManager(null);
            roots=java.io.File.listRoots();
        }

        VRL targetLoc;
        LinkNode lnode;
      
        for (int i=0;i<roots.length;i++)
        {
            if (roots[i].exists())
            {
                String path=roots[i].getAbsolutePath(); 
                String logicalName=path; 
                if (path==null || path=="/" || (path==""))
                {
                    logicalName="/Root";
                }
                
                targetLoc=new VRL("file",null,path);
                //linkPath=this.getLocation().addPath(""+index++); 
                
                VRL childVRL=this.createChildVRL(logicalName); 
                
                lnode=LinkNode.createLinkNode(vrsContext,childVRL,targetLoc); 
                lnode.setName("localhost:"+targetLoc.getPath());
                // do not show the shortcut image
                lnode.setShowShortCutIcon(false); 
                lnode.setIconURL("hdd_mount.png");
                lnode.setLogicalParent(this);
                
                // hard coded node: 
                lnode.setEditable(false); 
                this.addSubNode(lnode);    
            }
        }
        
    }
    
    
    public String[] getAttributeNames()
    {
        StringList list=new StringList(super.getAttributeNames());
        list.remove(VAttributeConstants.ATTR_MIMETYPE); 
        list.add(InfoConstants.ATTR_SYSTEMHOSTNAME);
        list.add(InfoConstants.ATTR_SYSTEMOS);
        list.add(InfoConstants.ATTR_JAVAVERSION);
        list.add(InfoConstants.ATTR_JAVAHOME);
        
        if (GlobalProperties.isWindows())
            list.add(VletConfig.PROP_SKIP_FLOPPY_SCAN);
        
        return list.toArray(); 
    }
    
    public Attribute getAttribute(String name) throws VrsException
    {
    	ConfigManager cmgr = this.vrsContext.getConfigManager(); 
    	Attribute attr=null; 
    	
        if (name.equals(InfoConstants.ATTR_SYSTEMHOSTNAME))
        {
            attr=new Attribute(name,GlobalProperties.getHostname());
        }
        else if (name.equals(InfoConstants.ATTR_SYSTEMOS))
        {
        	attr=new Attribute(name,GlobalProperties.getOsName()+" "+GlobalProperties.getOsVersion());
        }
        else if (name.equals(InfoConstants.ATTR_JAVAVERSION))
        {
        	attr=new Attribute(name,GlobalProperties.getJavaVersion()); 
        }
        else if (name.equals(InfoConstants.ATTR_JAVAHOME))
        {
        	attr=new Attribute(name,GlobalProperties.getJavaHome()); 
        }
        else if (name.equals(VletConfig.PROP_SKIP_FLOPPY_SCAN))
        {
        	attr=cmgr.getAttribute(name);
        	attr.setEditable(true); 
        	
        }
        
        if (attr!=null)
        	return attr; 

        return super.getAttribute(name); 
    }
    
    public boolean setAttribute(Attribute attr) throws VrsException
	{
		if (attr==null)
			return false;

		String name=attr.getName();
		
		if (name==null) 
			return false;
		
		ConfigManager manager=vrsContext.getConfigManager();
		boolean result=false;
		BooleanHolder refreshH=new BooleanHolder(false); 
		
		if (name.equals(VletConfig.PROP_SKIP_FLOPPY_SCAN))
        {
			// check Configuration attributes: 
			result=manager.setAttribute(attr,refreshH);
			refreshH.value=true; 
        }
		
		if (refreshH.value)
			refresh(); 
		
		return result; 
	}

	private void refresh()
	{
		try 
		{
			this.initChilds();
		}
		catch (VrsException e) 
		{
			VletConfig.getRootLogger().logException(ClassLogger.ERROR,this,e,"refresh(): got exception\n"); 
		} 
		this.fireRefresh(); 
	}
    
}

