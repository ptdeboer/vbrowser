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

package nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs;


import java.net.URISyntaxException;

import nl.esciencecenter.ptk.data.LongHolder;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeType;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.ui.presentation.UIPresentable;
import nl.esciencecenter.vbrowser.vrs.ui.presentation.UIPresentation;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.presentation.VRSPresentation;
import nl.esciencecenter.vlet.vrs.VComposite;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.vrms.LogicalResourceNode;
import nl.esciencecenter.vlet.vrs.vrms.VResourceLink;



/** 
 * VRS ProxyNode 
 */
public class VRSProxyNode extends ProxyNode
{
    private VNode vnode;

    public VRSProxyNode(VRSProxyFactory vrsProxyFactory, VNode vnode,VRL locator) throws ProxyException, URISyntaxException
    {
        super(vrsProxyFactory,locator);
        this.vnode=vnode; 
    }
    
    protected VRSProxyFactory factory()
    {
        return (VRSProxyFactory)this.getProxyFactory(); 
    }

    protected void doPrefetchAttributes() throws ProxyException
    {
        super.doPrefetchAttributes(); 
        
       
        if (vnode instanceof VResourceLink)
        {
        	// assume true for now: 
        	 this.cache.setIsComposite(true);  
        }
    }
    
    protected boolean isLogicalNode()
    {
    	return (this.vnode instanceof LogicalResourceNode);
    }
    
    @Override
    protected VRSProxyNode doGetParent() throws ProxyException
    {
        VNode parent;
        
        try
        {
            parent = vnode.getParent();
            if (parent==null)
                return null; 
           
            return new VRSProxyNode(this.getProxyFactory(),parent,new VRL(parent.getVRL().toURI()));
        }
        catch (Exception e)
        {
            throw createProxyException("Couldn't get parent of:"+locator,e); 
        } 
    }

    @Override
    protected ProxyNode[] doGetChilds(int offset, int range,LongHolder numChildsLeft) throws ProxyException
    {
    	debug("doGetChilds:"+this); 
    	 
        try
        {
        	// check links first: 
        	if (isResourceLink())
        	{
        		return resolve().doGetChilds(offset,range,numChildsLeft);  
        	}
        	else
        	{ 
        		if ((vnode instanceof VComposite)==false)  
        			return null; 

        		VNode nodes[] = ((VComposite)vnode).getNodes();
            	return subrange(createNodes(nodes),offset,range); 
        	}
        	
        }
        catch (VrsException e)
        {
        	throw createProxyException("Couldn't get childs of:"+locator,e); 
        }
         
    }
    
    protected VRSProxyNode resolve() throws ProxyException
    {
    	debug("Resolving:"+this.vnode); 
    	
    	if ((vnode instanceof VResourceLink)==false)
    		return this; 
    	
    	nl.esciencecenter.vbrowser.vrs.vrl.VRL vrl;
    	
		try 
		{
			vrl = ((VResourceLink)vnode).getTargetLocation();
	    	VRSProxyNode node = factory()._openLocation(vrl); 
	    	debug("Resolved to:"+node);
	    	return node; 
		}
		catch (VrsException e) 
		{
			throw createProxyException("Failed to resolve node:"+this.vnode,e); 
		}
    	
    }
    
    protected VRSProxyNode[] createNodes(VNode[] nodes) throws ProxyException
    {
    	if (nodes==null)
    		return null; 
    	
        int len=nodes.length;  
        
        VRSProxyNode pnodes[]=new VRSProxyNode[len];
        for (int i=0;i<len;i++)
        {
            pnodes[i]=createNode(nodes[i]); 
        }
        return pnodes; 
    }

    protected VRSProxyNode createNode(VNode node) throws ProxyException
    {
        try
        {
            return new VRSProxyNode(factory(),node,new VRL(node.getVRL().toURI()));
        }
        catch (Exception e)
        {
            throw createProxyException("Error creating proxy node from:"+node,e);  
        }
    }
    
    @Override
    public String getIconURL(String status,int size) throws ProxyException
    {
    	if (isLogicalNode())
    	{
    		return ((LogicalResourceNode)vnode).getTargetIconURL();   
    	}
    	else
    	{
    		return vnode.getIconURL(size);  
    	}
    }

	@Override
	public VRSProxyFactory getProxyFactory()
	{
		return (VRSProxyFactory)super.getProxyFactory();  
	}
	
	protected boolean isResourceLink()
    {
    	if (vnode instanceof VResourceLink)
    		return true; 
        
        if (vnode.getVRL()==null) 
        	return false; 

        // All .vlink AND .vrsx files are ResourceLinks ! 
        if (vnode.getVRL().isVLink() == true)
            return true;
        
        return false;
    }
	
	protected void debug(String msg)
	{
		System.err.println("VRSProxyNode:"+msg); 
	}
	
	public String toString()
	{
		return "<ProxyNode>"+locator.toString(); 
	}

	@Override
	protected String doGetMimeType() throws ProxyException
	{
	    String mimeType=null; 
	    try 
        {
	        mimeType=vnode.getMimeType(); 
        }
        catch (VrsException e) 
        {
            throw new ProxyException("Couldn't determine mime type of:"+vnode,e); 
        } 
        
        if (vnode instanceof LogicalResourceNode)
        {
            LogicalResourceNode lnode=(LogicalResourceNode)vnode; 
            mimeType=lnode.getTargetMimeType(); 
        }
        
        return mimeType; 
	}

	@Override
	protected boolean doGetIsComposite() throws ProxyException 
	{
	    boolean isComposite=vnode.isComposite();
	    
	    if (vnode instanceof LogicalResourceNode)
        {
            LogicalResourceNode lnode=(LogicalResourceNode)vnode; 
            try 
            {
                isComposite=lnode.getTargetIsComposite(true);
            }
            catch (VrsException e)
            {
                throw createProxyException("Error checking LogicalResourceNode:"+lnode,e); 
            }
        }
	    
	    return isComposite; 
	}

    @Override
    protected String[] doGetChildTypes() throws ProxyException
    {
        if (vnode instanceof VComposite)
        {
            return ((VComposite)vnode).getResourceTypes(); 
        }
        
        return null; 
    }

	// ========================================================================
	// Misc 
	// ========================================================================
	
	private ProxyException createProxyException(String msg, Exception e) 
	{
	    return new ProxyException(msg+"\n"+e.getMessage(),e); 
    }

    @Override
    protected String doGetName()
    {
    	return vnode.getName(); 
    }
    
    @Override
    protected String doGetResourceType()
    {   
        return vnode.getResourceType(); 
    }

    @Override
    protected String doGetResourceStatus() throws ProxyException
    {
        try 
        {
            return this.vnode.getResourceStatus();
        }
        catch (VrsException e)
        {
            throw createProxyException("Couldn't get status of:"+vnode,e);  
        }
    }

    @Override
    protected String[] doGetAttributeNames() throws ProxyException
    {
        return vnode.getAttributeNames(); 
    }
    
	@Override
    protected Attribute[] doGetAttributes(String[] names) throws ProxyException
    {
        try
        {
            return vnode.getAttributes(names);
        }
        catch (VrsException e)
        {
            throw new ProxyException("Couldn't get attributes\n",e); 
        } 
   }
    
    @Override
    protected UIPresentation doGetPresentation()
    {
        if (vnode instanceof UIPresentable)
        {
            return ((UIPresentable) vnode).getPresentation();
        }
        
        String type=vnode.getResourceType();  
        
        return VRSPresentation.getPresentationFor(vnode.getVRL(),type, true);
        
    }

}