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

import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.ResourceNotFoundException;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.VResourceSystem;
import nl.esciencecenter.vlet.vrs.vdriver.infors.grid.GridNeighbourhood;

public class InfoResourceSystem extends CompositeServiceInfoNode<VNode> implements VResourceSystem
{
    private GridNeighbourhood gridRoot; 
    private LocalSystem localSystemRoot;  
    
    InfoResourceSystem(VRSContext context) throws VRLSyntaxException
    {
        super(context,new VRL("info:/"));
        initChilds();
        this.setEditable(false); 
    }
    
    private void initChilds()
    {
        gridRoot=new GridNeighbourhood(this.vrsContext);
        
        localSystemRoot=new LocalSystem(this.vrsContext); 
        
        VNode nodes[]=new VNode[2]; 
        nodes[0]=gridRoot;
        nodes[1]=localSystemRoot;
        
        this.setChilds(nodes);  
    }
    
    // IMPORTANT: One ID per context ! (singleton instance) 
    public String getID()
    {
        return InfoConstants.INFO_INSTANCE_ID;    
    }

    @Override
    public VRL resolve(String path) throws VRLSyntaxException 
    {
        return this.getVRL().resolvePath(path);
    }
    
    public VNode openLocation(VRL vrl) throws VrsException
    {
        int len=0;
        String scheme=vrl.getScheme(); 
        String path=vrl.getPath(); 
        String[] paths=vrl.getPathElements(); 
        
        if (paths!=null)
            len=paths.length; 
        
        if (scheme.equals("scheme") && ((len==0) || (paths[0].equals(""))) ) 
        {
            return this;
        }
        
        debug("Get info resource from root:"+paths[0]); 
        
        VNode node=null; 
        // len >=1
        if (StringUtil.equals(gridRoot.getVRL().getPathElements()[0],paths[0]))
        {
            if (len==1)
                return gridRoot;
            else
            {
                node=gridRoot.findNode(vrl,true);
                if (node!=null)
                    return node; 
                
                throw new ResourceNotFoundException("No information for grid resource:"+vrl);
            }
        }
        
        System.err.printf("openLocation: %s\n",vrl);
        System.err.printf("localSystemRoot: %s\n",localSystemRoot.getVRL());
        
        if (scheme.equals(localSystemRoot.getScheme()))
        {
            
            if (vrl.equals(localSystemRoot.getVRL()))
            {
                return localSystemRoot;
            }
            
            node=localSystemRoot.findNode(vrl,true);
            if (node!=null)
            {
                return node; 
            }
                
            throw new ResourceNotFoundException("No information for local resource:"+vrl);
        }
            
        throw new ResourceNotFoundException("No information for:"+vrl);
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public String getResourceType()
    {
        return InfoConstants.INFONODE_TYPE; 
    }

    private void debug(String msg)
    {
        //Global.debugPrintf(this,"%s\n",msg); 
    }
    
    @Override
    public void connect()
    {
    }
    
    @Override
    public void disconnect()
    {
    }

    @Override
    public void dispose()
    {
    }
    
}
