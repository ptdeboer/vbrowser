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

package nl.esciencecenter.vlet.vrs.vdriver.infors.grid;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VCompositeDeletable;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vdriver.infors.CompositeServiceInfoNode;
import nl.esciencecenter.vlet.vrs.vdriver.infors.InfoConstants;
import nl.esciencecenter.vlet.vrs.vrms.ResourceFolder;

/**
 *  VO Resource Folder 
 */
public class VONode extends CompositeServiceInfoNode<VNode> implements VCompositeDeletable
{
    // ===  Class === // 
    private ResourceFolder seLocations; 
    private String vo=null;
    private VOGroupsNode parentServiceNode=null;
    private ResourceFolder lfcLocations;
    private ResourceFolder wmsLocations;
    private ResourceFolder lbLocations; 
    
    boolean showWms=true; 
    boolean showLbs=true; 
    
    public static VONode createVOGroup(VOGroupsNode groupsParentNode, String vo) throws VRLSyntaxException
    {
        VRL vrl=groupsParentNode.getVRL().appendPath(vo); 
         
        VONode vogrp=new VONode(groupsParentNode.getVRSContext(),vrl);
        vogrp.vo=vo; 
        vogrp.parentServiceNode=groupsParentNode;
      
        return vogrp;
    }
   
    // === Instance === //
    
    public VONode(VRSContext context, VRL vrl)
    {
        super(context, vrl);
        this.setEditable(false);
    }

    @Override
    public String getResourceType()
    {
       return InfoConstants.VO_TYPE;  
    }

   
    
    public String getIconURL(int prefSize)
    {
        String vostr=vrsContext.getGridProxy().getVOName();
        
        if (StringUtil.compareIgnoreCase(vo,vostr)==0)
            if (prefSize>32)
                return "triperson-check-128.png";
            else
                return "triperson-check-32.png";
        
        return "triperson-128.png";
    }
    
    public synchronized VNode[] getNodes() throws VrsException
    {
        initChilds(); 
        
        int numN=2+(showWms?1:0)+(showLbs?1:0); 
       
        VNode nodes[]=new VNode[numN]; 
        int index=0; 
        
        nodes[index++]=lfcLocations;
        nodes[index++]=seLocations;
        if (showWms) 
            nodes[index++]=wmsLocations; 
        if (showLbs) 
            nodes[index++]=lbLocations; 
        
    	// update super class internal array! 
		setChilds(nodes); 
		
        return nodes; 
    }
    
    private void initChilds() throws VrsException
    {
        if (lfcLocations==null)
            initLFCs(); 

        if (seLocations==null) 
            initSEs();
        
        if ( (wmsLocations==null) && (showWms))
            initWMSs();
        
        if ( (this.lbLocations==null) && (showLbs))
            initLBs(); 
    }

    public String[] getResourceTypes()
    {
        return null;
    }

    private void initSEs() throws VrsException
    {
        this.seLocations=parentServiceNode.createSEFolderForVO(this.getVRL(),vo); 
    }

    private void initLFCs() throws VrsException
    {
        this.lfcLocations=parentServiceNode.createLFCFolderForVO(this.getVRL(),vo); 
    }
    
    private void initWMSs() throws VrsException
    {
        this.wmsLocations=parentServiceNode.createWMSFolderForVO(this.getVRL(),vo); 
    }
    
    private void initLBs() throws VrsException
    {
        this.lbLocations=parentServiceNode.createLBFolderForVO(this.getVRL(),vo); 
    }

    public boolean delete()
    {
    	return this.parentServiceNode.deleteVOGroup(this); 
    }
    
    public boolean delete(boolean recurse)
    {
    	return delete(); 
    }
}
