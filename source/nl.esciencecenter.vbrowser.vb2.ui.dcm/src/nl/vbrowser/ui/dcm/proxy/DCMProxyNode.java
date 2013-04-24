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

package nl.vbrowser.ui.dcm.proxy;


import java.io.IOException;

import nl.esciencecenter.medim.dicom.DicomUtil;
import nl.esciencecenter.medim.dicom.DicomWrapper;
import nl.esciencecenter.ptk.data.LongHolder;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.ui.presentation.UIPresentation;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeType;

import org.dcm4che2.data.Tag;


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