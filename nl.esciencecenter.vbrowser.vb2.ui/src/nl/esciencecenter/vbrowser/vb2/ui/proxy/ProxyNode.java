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

package nl.esciencecenter.vbrowser.vb2.ui.proxy;

import javax.swing.Icon;

import nl.esciencecenter.ptk.data.LongHolder;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.ui.icons.IconProvider;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.UIGlobal;
import nl.esciencecenter.vbrowser.vb2.ui.model.UIViewModel;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.presentation.UIPresentation;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;


/**
 * ProxyNode is abstract interface to Resource Nodes. It represents a "Proxy" of the actual viewed Node. 
 */ 
public abstract class ProxyNode
{
    private static int idCounter=0; 
    
    private static ClassLogger logger; 
    {
    	logger=ClassLogger.getLogger(ProxyNode.class); 
    }
    
    // ========================================================================
    // helpers
    // ========================================================================
    
     // Get subrange of array nodes [offset:offset+range] . 
    public ProxyNode[] subrange(ProxyNode nodes[],int offset,int range)
    {
    	// no change:
    	if ((offset<=0) && (range<0)) 
    		return nodes;  
    
    	if (offset<0)
    		throw new Error("subrange(): Parameter 'offset' can't be negative. Use 0 for 'don't care'"); 
    	
    	if (nodes==null) 
    		return null; 

    	int len=nodes.length; 

    	// no more nodes after len: 
    	if (offset>=len)
    		return null; 
    	
    	// range=-1 means all 
    	if (range<0) 
    		range=len;  
    	
    	// overflow, requested more then there are. 
    	if (offset+range>len) 
    		range=len-offset; // remainder 

    	if (range==0)
    		return null; 
    	
    	// Assert: 0 <= offset < len 
    	// Assert: 0 <= range <= (len - offset)

    	ProxyNode subnodes[]=new ProxyNode[range];
    	for (int i=0;i<range;i++)
    		subnodes[i]=nodes[offset+i];
    	return subnodes; 
    }
    
    public static int newID()
    {
        return idCounter++;
    }
    
    // ========================================================================
    // Cache ! 
    // ========================================================================
    
    class Childs
    {
    	ProxyNode[] nodes=null;

    	long getChildsTime=-1; 
    }
    
    /** Cache for Object attributes ! */ 
    public class Cache
    {
    	String name=null; 
        
    	Boolean is_composite=null; 
 
		String mime_type=null; 

		String resource_type=null;

		String resource_status=null;

		Childs childs=new Childs(); 
		
		ProxyNode parent=null;

        public String[] child_types;


		public void setName(String newName) 
		{
			this.name=newName; 
		}
		
		public void setResourceType(String value) 
		{
			this.resource_type=value; 
		} 
		
		public void setIsComposite(boolean val) 
		{
			this.is_composite=val; 
		}

		public void setMimeType(String mimeType) 
		{
			this.mime_type=mimeType; 
		}

		public String getMimeType() 
		{
			return this.mime_type; 
		}

		public boolean getIsComposite() 
		{
			return this.is_composite; 
		}
    }
    
 	// ========================================================================
    //
    // ========================================================================
    
    protected final int id;
    
    protected final VRI locator; 
    
    protected Cache cache=new Cache(); // default empty 
    
    protected ProxyNode(VRI proxyLocation) 
    {
        id=newID();  
        this.locator=proxyLocation;  
    }
    
    public int getID()
    {
        return id; 
    }
    
    public VRI getVRI()
    {
        return  locator; 
    }
    
    /** Called by node factory to prefill core attributes */ 
    protected void doPrefetch() throws ProxyException 
    {
        synchronized(cache)
        {
        	// update name. 
            this.cache.name=doGetName();  

            try { this.cache.is_composite=doGetIsComposite(); } catch(Exception e){ handle(e);}  

            try { this.cache.resource_type=doGetResourceType(); } catch(Exception e){ handle(e);}

            // might be null anyway
            try { this.cache.resource_status=doGetResourceStatus(); } catch(Exception e){ handle(e);}

			// might be null anyway. 
            try { this.cache.mime_type=doGetMimeType(); }  catch(Exception e){ handle(e);}
    		
            try { this.cache.child_types=doGetChildTypes(); }  catch(Exception e){ handle(e);} 
        }
    
    }
    
    private void handle(Exception e)
    {
        e.printStackTrace(); 
    }

    public Icon getIcon(UIViewModel model) throws ProxyException
    {
        return getIcon(model.getIconSize()); 
    }
    
    public Icon getIcon(int size) throws ProxyException
    {
        IconProvider provider=UIGlobal.getIconProvider();
        
        String mimeType=this.getMimeType(); 
        
        String iconUrl=this.getIconURL(getResourceStatus(),size);  
        
        return provider.createDefaultIcon(iconUrl,
                this.isComposite(),
                false, // islink
                mimeType,
                size,
                false); //grey out 
    }

    public String getName() // no throw: name should already be fetched 
    {
    	if (this.cache.name==null)
    	{
    		logger.warnPrintf("getName(): name NOT prefetched:%s\n",this);
    		try 
    		{
				this.cache.name=doGetName();
			}
    		catch (ProxyException e)
    		{
    			handle(e); 
    			this.cache.name=getVRI().getBasename(); 
			} 
    	}
    	
        return this.cache.name; 
    }


	public String getResourceStatus()  
    {
		// could be prefetched or not. NULL could also mean no status info.  
        return this.cache.resource_status;  
    }
    
    public String getMimeType() throws ProxyException
    {
    	if (this.cache.mime_type==null)
    	{
    		logger.warnPrintf("getMimeType(): mime_type NOT prefetched:%s\n",this);
    		this.cache.mime_type=doGetMimeType(); 
    	}
    	
        return this.cache.mime_type; 
    }
    
    /** 
     * Get Icon uses for specified satus and optional a prerender icon matching the specified
     * size 
     * @param status  Optional status attribute 
     * @param size    Desired size 
     * @return
     * @throws ProxyException
     */
    public String getIconURL(String status,int size) throws ProxyException
    {
        return null; 
    }

    public boolean hasChildren() throws ProxyException
    {
        ProxyNode[] childs = this.getChilds(); 
        
        if ((childs==null) || (childs.length<0))
            return false;
        
        return true; 
    }

    public ViewNode createViewItem(UIViewModel model) throws ProxyException
    {
        ViewNode viewNode=new ViewNode(locator,getIcon(model),getName(),isComposite());
        viewNode.setResourceType(this.getResourceType()); 
        return viewNode; 
    } 
    
	public boolean hasLocator(VRI locator)
	{
		return this.locator.equals(locator); 
	}

	public ProxyNode[] getChilds() throws ProxyException
	{
		return getChilds(0,-1,null); 
	}

	// ========================================================================
    // Cached methods
    // ========================================================================
   
	
	public ProxyNode[] getChilds(int offset, int range, LongHolder numChildsLeft) throws ProxyException
	{
		 synchronized(this.cache.childs)
	     {
			 if (cache.childs.nodes==null)
			 {
				 ProxyNode[] childs = doGetChilds(offset,range,numChildsLeft); 
				 
				 if ((offset>0) || (range>0)) 
				 {
				     // todo: update ranged childs into cache, but typically ranged results
				     // are used in the case the actual child list is to big or the invoker
				     // caches the data itself. 
				     return childs; // do not cache ranged results! 
				 }
				 
				 // only cache complete results! 
			     cache.childs.nodes=childs; 
			     cache.childs.getChildsTime=System.currentTimeMillis(); 

				 if (cache.childs.nodes!=null)
				     for (ProxyNode child:cache.childs.nodes)
				     {  
				         child.doPrefetch(); 
				     }
			 }
			 
			 return cache.childs.nodes;
	     }
	}
	
	public ProxyNode getParent() throws ProxyException
	{
		 synchronized(this.cache)
	     {
			 if (cache.parent==null)
			 {
				 cache.parent=doGetParent();  
			 }
			 
			 return cache.parent;
	     }
	}
	
   public VRI getParentLocation() throws ProxyException
   {
       ProxyNode parent = getParent();
       
       if (parent!=null)
           return parent.getVRI();
       
       return null;  
   }

	
	/** @deprecated to be investigated */ 
    public boolean isBusy()
    {
    	return false; 
    }
	
    /**
	 * Returns (cached) ResourceType value. 
	 * Should not throw exception since attribute must be known at creation time. 
	 * 
	 * @return
	 */
	public String getResourceType()
	{
	    if (this.cache.resource_type==null)
        {
            logger.warnPrintf("resource_type NOT prefetched!\n");
            
            try
            {
                this.cache.resource_type=doGetResourceType();
            }
            catch (ProxyException e)
            {
               handle(e); 
            } 
        }
		return this.cache.resource_type; 
	}
	
	/**
	 * Returns (cached) isComposite value. 
	 * Should not throw exception since attribute must be known at creation time. 
	 * 
	 * @return
	 */
	public boolean isComposite()
	{
		if (this.cache.is_composite==null)
		{
			logger.warnPrintf("***>>>> isComposite() NOT prefetched!\n"); 
			try
			{ 
				this.cache.is_composite=doGetIsComposite(); 
			} 
			catch(Exception e)
			{ 
				handle(e);
				return true; 
			}  
		}
		
		return this.cache.is_composite; 
	}
	
	public String[] getCreateTypes() 
	{
	    if (this.cache.child_types==null)
	    {
	        try
	        {
	            this.cache.child_types=doGetChildTypes();
	        }
	        catch(ProxyException e)
	        {
	            handle(e);
	        }
	    }
	    
	    return this.cache.child_types;
	}
	
    public String[] getAttributeNames() throws ProxyException
    {
        return doGetAttributeNames(); 
    }

    public Attribute[] getAttributes(String[] names) throws ProxyException
    {
        return doGetAttributes(names); 
    }
    
    public UIPresentation getPresentation()
    {
        return doGetPresentation(); 
    }
    
	// ========================================================================
    // public interface 
    // ========================================================================

    public abstract ProxyFactory getProxyFactory(); 
	
	// ========================================================================
    // Protected implementation interface ! 
    // ========================================================================

	// ====
	// Core Resource Attributes
	// ====
    
	protected abstract String doGetName() throws ProxyException; 
    
    protected abstract String doGetResourceType() throws ProxyException;  
    
    protected abstract String doGetResourceStatus() throws ProxyException;
  
    protected abstract String doGetMimeType() throws ProxyException;

    protected abstract boolean doGetIsComposite() throws ProxyException;
    
    // ====
    // Child/Composite Attributes
    // ====
    
	/**  Uncached doGetChilds, using optional range */  
	protected abstract ProxyNode[] doGetChilds(int offset, int range, LongHolder numChildsLeft) throws ProxyException;
    
    /** Uncached doGetParent() */  
	protected abstract ProxyNode doGetParent() throws ProxyException;
    
    /** Resource Type this node can create/contain. */ 
    protected abstract String[] doGetChildTypes() throws ProxyException;

    // ====
    // Attributes 
    // ====

    abstract protected String[] doGetAttributeNames() throws ProxyException;

    abstract protected Attribute[] doGetAttributes(String[] names) throws ProxyException;

    abstract protected UIPresentation doGetPresentation(); 
    
}
