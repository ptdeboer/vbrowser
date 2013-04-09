package nl.vbrowser.vlet.proxy.vrs;


import java.util.Map;

import nl.nlesc.ptk.data.LongHolder;
import nl.nlesc.ptk.exceptions.VRISyntaxException;
import nl.nlesc.ptk.net.VRI;
import nl.nlesc.ptk.presentation.IPresentable;
import nl.nlesc.ptk.presentation.Presentation;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.data.VAttributeType;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.presentation.VRSPresentation;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrms.LogicalResourceNode;
import nl.uva.vlet.vrms.VResourceLink;
import nl.uva.vlet.vrs.VComposite;
import nl.uva.vlet.vrs.VNode;
import nl.vbrowser.ui.data.Attribute;
import nl.vbrowser.ui.presentation.UIPresentable;
import nl.vbrowser.ui.presentation.UIPresentation;
import nl.vbrowser.ui.proxy.ProxyException;
import nl.vbrowser.ui.proxy.ProxyNode;


/** 
 * VRS ProxyNode 
 */
public class VRSProxyNode extends ProxyNode
{
    private VRSProxyFactory factory;

    private VNode vnode;

    public VRSProxyNode(VRSProxyFactory vrsProxyFactory, VNode vnode,VRI locator) throws ProxyException
    {
        super(locator);
        this.vnode=vnode; 
        this.factory=vrsProxyFactory;
    }

    protected void doPrefetch() throws ProxyException
    {
        super.doPrefetch(); 
        
       
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
           
            return new VRSProxyNode(factory,parent,new VRI(parent.getURI()));
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
        catch (VlException e)
        {
        	throw createProxyException("Couldn't get childs of:"+locator,e); 
        }
         
    }
    
    protected VRSProxyNode resolve() throws ProxyException
    {
    	debug("Resolving:"+this.vnode); 
    	
    	if ((vnode instanceof VResourceLink)==false)
    		return this; 
    	
    	VRL vrl;
    	
		try 
		{
			vrl = ((VResourceLink)vnode).getTargetLocation();
	    	VRSProxyNode node = factory.openLocation(vrl); 
	    	debug("Resolved to:"+node);
	    	return node; 
		}
		catch (VlException e) 
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
            return new VRSProxyNode(factory,node,new VRI(node.getURI()));
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
		return this.factory; 
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
        catch (VlException e) 
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
            catch (VlException e)
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
        catch (VlException e)
        {
            throw createProxyException("Couldn't get status of:"+vnode,e);  
        }
         
    }

    @Override
    protected String[] doGetAttributeNames() throws ProxyException
    {
        return convertAttrNames(vnode.getAttributeNames(),true); 
    }

    private String[] convertAttrNames(String[] names,boolean firstToUpperCase) 
    {
    	String newnames[]=new String[names.length]; 
    	
    	for (int i=0;i<names.length;i++)
    	{
    		newnames[i]=convertAttrName(names[i],firstToUpperCase);
    	}
    	return newnames; 
	}
    

	private static String convertAttrName(String name,boolean firstToUpperCase) 
	{
		if (name==null)
			return null;
		
		if (name.length()<=0)
			return name; 
		
		String fstr=""+name.charAt(0);
		if(firstToUpperCase)
			fstr=fstr.toUpperCase();
		else
			fstr=fstr.toLowerCase();
		
		return ""+fstr+name.substring(1); 
	}

	@Override
    protected Attribute[] doGetAttributes(String[] names) throws ProxyException
    {
        VAttribute[] vattrs;
        
        try
        {
            vattrs = vnode.getAttributes(convertAttrNames(names,false));
            return convert(vattrs);
        }
        catch (VlException e)
        {
            throw new ProxyException("Couldn't get attributes\n",e); 
        } 
   }
    
    public static Attribute[] convert(VAttribute vattrs[])
    {
        Attribute[] attrs=new Attribute[vattrs.length]; 
        
        for (int i=0;i<vattrs.length;i++)
        {
            attrs[i]=convert(vattrs[i]);
        }
        
        return attrs; 
        
    }

    public static Attribute convert(VAttribute vattr)
    {
        if (vattr==null)
            return null;
        
        VAttributeType type = vattr.getType();
        String name=convertAttrName(vattr.getName(),true);
        
        switch(type)
        {
            case BOOLEAN:
                return new Attribute(name,vattr.getBooleanValue()); 
            case INT:
                return new Attribute(name,vattr.getIntValue());
            case LONG:
                return new Attribute(name,vattr.getLongValue());
            case FLOAT:
                return new Attribute(name,vattr.getFloatValue());
            case DOUBLE:
                return new Attribute(name,vattr.getDoubleValue());
            case ENUM:
                return new Attribute(name,vattr.getEnumValues(),vattr.getStringValue()); 
            case VRL:
                try
                {
                    return new Attribute(name,new VRI(vattr.getStringValue()));
                }
                catch (VRISyntaxException e)
                {
                    return new Attribute(name,vattr.getStringValue());
                } 
            case TIME: 
                return new Attribute(name,vattr.getDateValue());
            case STRING:
            default: 
                return new Attribute(name,vattr.getStringValue());
                
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