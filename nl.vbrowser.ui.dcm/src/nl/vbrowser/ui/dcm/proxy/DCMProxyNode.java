package nl.vbrowser.ui.dcm.proxy;


import java.io.IOException;
import java.util.Map;

import org.dcm4che2.data.Tag;

import nl.nlesc.medim.dicom.DicomUtil;
import nl.nlesc.medim.dicom.DicomWrapper;
import nl.nlesc.ptk.data.LongHolder;
import nl.nlesc.ptk.data.StringList;
import nl.nlesc.ptk.exceptions.VRISyntaxException;
import nl.nlesc.ptk.io.FSNode;
import nl.nlesc.ptk.net.VRI;
import nl.vbrowser.ui.data.Attribute;
import nl.vbrowser.ui.data.AttributeType;
import nl.vbrowser.ui.presentation.UIPresentation;
import nl.vbrowser.ui.proxy.ProxyException;
import nl.vbrowser.ui.proxy.ProxyNode;


/** 
 * VRS ProxyNode 
 */
public class DCMProxyNode extends ProxyNode
{
    private DCMProxyFactory factory;

    private FSNode fsNode;

    private UIPresentation dcmPresentation; 
    
    public DCMProxyNode(DCMProxyFactory vrsProxyFactory, FSNode fsNode,VRI locator) throws ProxyException
    {
        super(locator);
        this.fsNode=fsNode; 
        this.factory=vrsProxyFactory;
    }

    protected void doPrefetch() throws ProxyException
    {
        super.doPrefetch(); 
    }
    
    protected boolean isLogicalNode()
    {
    	return false;
    }
    
    @Override
    protected DCMProxyNode doGetParent() throws ProxyException
    {
        FSNode parent;
        
        try
        {
            parent = fsNode.getParent();
            if (parent==null)
                return null; 
           
            return new DCMProxyNode(factory,parent,new VRI(parent.getURI()));
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
            if (fsNode.isDirectory()==false)
                return null; 

		    FSNode[] nodes = fsNode.listNodes(); 
		    return subrange(createNodes(nodes),offset,range); 
        	
        }
        catch (IOException e)
        {
        	throw createProxyException("Couldn't get childs of:"+locator,e); 
        }
         
    }
    
    protected DCMProxyNode resolve() throws ProxyException
    {
    	return this; 
    }
    
    protected DCMProxyNode[] createNodes(FSNode[] nodes) throws ProxyException
    {
    	if (nodes==null)
    		return null; 
    	
        int len=nodes.length;  
        
        DCMProxyNode pnodes[]=new DCMProxyNode[len];
        for (int i=0;i<len;i++)
        {
            pnodes[i]=createNode(nodes[i]); 
        }
        return pnodes; 
    }

    protected DCMProxyNode createNode(FSNode node) throws ProxyException
    {
        try
        {
            return new DCMProxyNode(factory,node,new VRI(node.getURI()));
        }
        catch (Exception e)
        {
            throw createProxyException("Error creating proxy node from:"+node,e);  
        }
    }
    
    @Override
    public String getIconURL(String status,int size) throws ProxyException
    {
        return null; 
    }

	@Override
	public DCMProxyFactory getProxyFactory()
	{
		return this.factory; 
	}
	
	protected boolean isResourceLink()
    {
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
	    if (fsNode.isDirectory())
	        return null;
	    
	    return "application/dicom"; 
	}

	@Override
	protected boolean doGetIsComposite() throws ProxyException 
	{
	    return fsNode.isDirectory(); 
	}

    @Override
    protected String[] doGetChildTypes() throws ProxyException
    {
        return new String[]{"DCMNode"}; 
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
    	return fsNode.getBasename(); 
    }
    
    @Override
    protected String doGetResourceType()
    {   
        return "DCMNode"; 
    }

    @Override
    protected String doGetResourceStatus() throws ProxyException
    {
        return null; 
    }

    @Override
    protected String[] doGetAttributeNames() throws ProxyException
    {
        if (fsNode.isDirectory())
            return null; 
        
        try
        {
            return DCMProxyUtil.getDicomTagNames(fsNode,true); 
        }
        catch (Exception e)
        {
            throw createProxyException("Error getting tag names from proxy node from:"+fsNode,e);  
        }
    }   

	@Override
    protected Attribute[] doGetAttributes(String[] names) throws ProxyException
    {
	    if (fsNode.isDirectory())
	        return null; 
	        
	    try
        {
	        Attribute attrs[]=new Attribute[names.length]; 
	        DicomWrapper wrap = new DicomWrapper(DicomUtil.readDicom(fsNode.getPath()));

	        for (int i=0;i<names.length;i++)
	        {
	            if (DicomUtil.isTagField(names[i])==false)
	            {
                    attrs[i]=new Attribute(AttributeType.STRING,names[i],"?"); 
	            }
	            else
	            {
	                String value=wrap.getValueAsString(DicomUtil.getTagField(names[i])); 
	                attrs[i]=new Attribute(AttributeType.STRING,names[i],value);
	            }
	        }
	        
	        return attrs; 
        }
        catch (Exception e)
        {
            throw new ProxyException("Couldn't get attributes\n",e); 
        } 
   }
    
    @Override
    protected UIPresentation doGetPresentation()
    {
        if (dcmPresentation==null)
        {
            dcmPresentation=new UIPresentation();
            StringList list=new StringList(); 
            
            list.add(DicomUtil.getTagName(Tag.PatientName)); 
            list.add(DicomUtil.getTagName(Tag.PatientID)); 
            list.add(DicomUtil.getTagName(Tag.PatientAge)); 
            // uid
            list.add(DicomUtil.getTagName(Tag.SeriesInstanceUID)); 
            list.add(DicomUtil.getTagName(Tag.SeriesNumber)); 
            
            dcmPresentation.setChildAttributeNames(list.toArray()); 
        }
        
        return dcmPresentation; 
        
    }

}