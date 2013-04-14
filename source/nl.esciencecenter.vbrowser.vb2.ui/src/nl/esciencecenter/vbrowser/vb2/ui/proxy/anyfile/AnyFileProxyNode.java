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

package nl.esciencecenter.vbrowser.vb2.ui.proxy.anyfile;

import java.io.IOException;

import nl.esciencecenter.ptk.data.LongHolder;
import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.util.MimeTypes;
import nl.esciencecenter.vbrowser.vb2.ui.presentation.UIPresentation;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyFactory;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.dummy.DummyProxyFactory;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;



public class AnyFileProxyNode extends ProxyNode
{
	FSNode file;
	private AnyFileAttributes metaFile; 
	
    protected AnyFileProxyNode createChild(String childname) throws ProxyException
    {
        return new AnyFileProxyNode(this,getVRI().appendPath(childname)); 
    }
    
    public AnyFileProxyNode(VRI loc) throws ProxyException
    {
        super(loc); 
        file=FSUtil.getDefault().newLocalFSNode(loc.getPath()); 
        init(); 
    } 
    
    public AnyFileProxyNode(VRI loc,FSNode file) throws ProxyException
    {
        super(loc); 
        this.file=file; 
        init(); 
    }
    
    protected AnyFileProxyNode(AnyFileProxyNode parent,VRI locator) throws ProxyException
    {
        super(locator);
        init(); 
    }

    private void init() throws ProxyException
    {
        this.metaFile=new AnyFileAttributes(file); 
    	//super.prefetch(); 
    }
    
    @Override
    public boolean isBusy()
    {
        return false;
    }

    @Override
    public String getName()
    {
        return file.getBasename();  
    }

    @Override
    public boolean hasChildren()
    {   
       if (file.isFile())
    	   return false; 
       
       try 
       {
    	   return (file.list()!=null);
       }
       catch (IOException e) 
       {
    	   return false;
       }
    }

    @Override
    protected ProxyNode doGetParent() throws ProxyException
    {
        return DummyProxyFactory.getDefault().doOpenLocation(this.locator.getParent());
    }
    
   
    @Override
    public ProxyNode[] doGetChilds(int offset, int range,LongHolder numChildsLeft) throws ProxyException
    {
    	FSNode[] files;
		try 
		{
			files = file.listNodes();
		} 
		catch (IOException e) 
		{
			throw new ProxyException("Couldn't list contents:"+this,e);
		}
		
    	if (files==null)
    		return null; 
    	
    	AnyFileProxyNode nodes[]=new AnyFileProxyNode[files.length];
    	
    	
    	for (int i=0;i<files.length;i++)
    		nodes[i]=new AnyFileProxyNode(files[i].getVRI(),files[i]); 
        
    	return subrange(nodes,offset,range);  
    }

    public static AnyFileProxyNode getRoot() throws ProxyException
    {
        try
        {
            return new AnyFileProxyNode(new VRI("file:/"));
        }
        catch (VRISyntaxException e)
        {
            throw new ProxyException("VRISyntaxException",e); 
        } 
    }

	@Override
	public ProxyFactory getProxyFactory()
	{
		return AnyFileProxyFactory.getDefault(); 
	}

	@Override
	protected String doGetMimeType() throws ProxyException 
	{
		return MimeTypes.getDefault().getMimeType(file.getPath());  
	}

	@Override
	protected boolean doGetIsComposite() throws ProxyException
	{
		return this.file.isDirectory();
	}

    @Override
    protected String doGetName() 
    {
        return this.getVRI().getBasename(); 
    }

    @Override
    protected String doGetResourceType() 
    {
    	if (file.isFile())
    		return FSNode.FILE_TYPE;
    	else
    		return FSNode.DIR_TYPE; 
    }

    @Override
    protected String doGetResourceStatus() 
    {
        return null;
    }

    @Override
    protected String[] doGetChildTypes() 
    {
        return new String[]{FSNode.FILE_TYPE,FSNode.DIR_TYPE}; 
    }

    @Override
    protected String[] doGetAttributeNames() throws ProxyException
    {
        return metaFile.getAttributeNames(); 
    }

    @Override
    protected Attribute[] doGetAttributes(String[] names) throws ProxyException
    {
        return metaFile.getAttributes(names); 
    }

    @Override
    protected UIPresentation doGetPresentation()
    {
        // redirect to meta file 
        return metaFile.getPresentation();
    }

}
