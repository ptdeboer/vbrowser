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

package nl.nlesc.vlet.gui.proxynode.impl.direct;

import java.lang.ref.WeakReference;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.Icon;

import nl.esciencecenter.ptk.ui.icons.IconProvider;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.presentation.UIPresentable;
import nl.esciencecenter.vbrowser.vb2.ui.presentation.UIPresentation;

import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.data.VAttributeConstants;
import nl.nlesc.vlet.exception.NotImplementedException;
import nl.nlesc.vlet.exception.ResourceCreationFailedException;
import nl.nlesc.vlet.exception.ResourceException;
import nl.nlesc.vlet.exception.ResourceLinkIsBorkenException;
import nl.nlesc.vlet.exception.ResourceNotEditableException;
import nl.nlesc.vlet.exception.ResourceTypeMismatchException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlInternalError;
import nl.nlesc.vlet.gui.Messages;
import nl.nlesc.vlet.gui.UIGlobal;
import nl.nlesc.vlet.gui.UILogger;
import nl.nlesc.vlet.gui.presentation.VRSPresentation;
import nl.nlesc.vlet.gui.proxyvrs.ProxyNode;
import nl.nlesc.vlet.gui.view.ViewFilter;
import nl.nlesc.vlet.vrs.LinkNode;
import nl.nlesc.vlet.vrs.ResourceEvent;
import nl.nlesc.vlet.vrs.VComposite;
import nl.nlesc.vlet.vrs.VCompositeDeletable;
import nl.nlesc.vlet.vrs.VDeletable;
import nl.nlesc.vlet.vrs.VEditable;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRenamable;
import nl.nlesc.vlet.vrs.util.VRSSort;
import nl.nlesc.vlet.vrs.vfs.VACL;
import nl.nlesc.vlet.vrs.vfs.VDir;
import nl.nlesc.vlet.vrs.vfs.VFSClient;
import nl.nlesc.vlet.vrs.vfs.VFSNode;
import nl.nlesc.vlet.vrs.vrl.VRL;
import nl.nlesc.vlet.vrs.vrms.LogicalResourceNode;
import nl.nlesc.vlet.vrs.vrms.MyVLe;
import nl.nlesc.vlet.vrs.vrms.VLogicalResource;
import nl.nlesc.vlet.vrs.vrms.VResourceLink;




/**
 * Proxy Object for the VRS VNodes. 
 * <p>
 * This class is the direct connection of the GUI components and the VRS/VNode
 * implementations.  
 * <p>
 * This class captures most of the functionality of viewed 'VNodes'.
 * VNode is the handler or reference object, but does not store any data. The
 * ProxyVNode holds the stored (cached) data of the VNode and other Graphical
 * information like menus icons and other convenient methods etc. 
 */
public final class ProxyVNode extends ProxyNode
{
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(ProxyVNode.class);
        //logger.setLevelToDebug();
    }
    
	// private static ReferenceQueue<ProxyTNode> garbageQueue= new ReferenceQueue<ProxyTNode>(); 
	
	public static class WeakProxyTNodeRef extends WeakReference<ProxyVNode> 
	{
		public WeakProxyTNodeRef(ProxyVNode referent)
		{
			// super(referent,garbageQueue);
			super(referent);
		}
	} 
	
    private static VFSClient vfs;
    
    //static VRSContext vrsContext;
    
	private static ProxyVNodeFactory proxyFactory=null;

	@SuppressWarnings("unused")
    private static ProxyVNodeCacheUpdater cacheUpdater; 
	 
    // ========================================================================
    // Instance Inner Class
    // ========================================================================
 
    
    /** Cach class object for VNode attributes */ 
    private class Cache
    {
    	// === Prefetched Attributes === 
    	
        public String[] resourceTypes=null;
        
        public Boolean isComposite=null;
        
        // == Default VNode Attributes === 
        
        /** VAttribute Name usually don't change */
        private String[] attributeNames = null;
        
        //public Boolean isReadable = null;
        
        //public Boolean isAccessable = null;
        
        /** Thread save HashishTable. so put and get should be thread save ! */
        private Hashtable<String, VAttribute> attributeHash = new Hashtable<String, VAttribute>();
        
        //public Boolean isWritable=null;
        
        // == VComposite Attributes === 
        
        /** Childs vector also as weak references ? */
        private Vector<ProxyVNode> childsv =null; 
        
        
        // == Icon/Presentation Attribuets == 
        public String iconUrl=null;
        
        //public ImageIcon imageIcon=null;

        public UIPresentation presentation=null; 

        // == Various Interfaces == 
        
        //public Boolean isRenamable=null;
        
        //public Boolean isDeletable=null;
        
        public Boolean isHidden=null;
        

        // == LinkNode/ResourceNode == 
        public LogicalResourceNode resourceNode=null;
        
        private ProxyVNode linkTarget=null;

        /** The parent pointer: the schoolbook example of a weakrefence */ 
        private WeakProxyTNodeRef proxyNodeParentRef = null;
        
        private boolean hasGetChildsException=false;

       // public VRL resolvedLocation; 
        
        // == methods === 
        
        public VAttribute getAttribute(String name)
        {
            VAttribute attr = null;
            
            Object o = attributeHash.get(name);
            
            if (o != null)
                attr = ((VAttribute) o);
            else
                attr = null;
          
            return attr;
        }
        
        public void storeAttributes(VAttribute[] uncachedAttrs)
        {
            if (uncachedAttrs==null)
                return; 
            
            for (VAttribute attr : uncachedAttrs)
            {
                
                if ((attr != null) && (attr.getName()!=null))
                    //thread save put
                    this.attributeHash.put(attr.getName(), attr);
                // else NULL attributes are allowed !
            }
        }
        
        public void storeAttribute(VAttribute attr)
        {
            this.attributeHash.put(attr.getName(), attr);
        }
        
        // destructor: clear cached attributes 
        public void dispose()
        { 
            // clear stored attributes etc.
            // nullification not needed since use of weakpointers ! 
        	// 
        	if (childsv!=null)
        		childsv.removeAllElements(); 
        	
            this.childsv = null;
            //this.isReadable = null;
            this.proxyNodeParentRef = null;
            this.linkTarget = null;
            this.attributeNames = null;
            
            attributeHash.clear();
        }
        
//        public void clearParentChildList()
//        {
//            if (this.proxyNodeParentRef != null)
//            {
//                ProxyTNode node = this.proxyNodeParentRef.get();
//                if (node!=null)
//                	node.clearCache(); 
//            }
//            else
//            {
//            	// see if parent is in parent cache 
//                VRL parentVRL=getVRL().getParent();
//                ProxyTNode parentNode = proxyFactory.getFromCache(parentVRL); 
//                if (parentNode!=null)
//                {
//                    parentNode.clearCache(); 
//                    // found parent in cache: update parent refence: 
//                    this.proxyNodeParentRef=new WeakProxyTNodeRef(parentNode);
//                }
//            }
//            
//        }
//        
//        // Added new node to the childs[] list:
//        public void addChilds(ProxyTNode[] newNodes)
//        {
//            if (this.childsv==null)
//            	childsv=new Vector<ProxyTNode>(); 
//            
//            // synchronized(pnode.cache.childs)
//            // can't sync around childs, object will be replaced.
//            
//            if (newNodes==null)
//            	return; 
//            
//            synchronized(childsv)
//            {
//                for (ProxyTNode node:newNodes)
//                    this.childsv.add(node); 
//            }
//            
//        }
        
        /** Returns UNFiltered Childs !*/ 
        public ProxyVNode[] getChilds()
        {
        	if (childsv==null)
        		return null; 
        	
        	synchronized(this.childsv)
        	{
        		ProxyVNode childs[]=new ProxyVNode[childsv.size()]; 
        		childs=childsv.toArray(childs);
        		return childs; 
        	}
        }

		public void setParent(ProxyVNode node)
		{
			this.proxyNodeParentRef=new WeakProxyTNodeRef(node); 
		}

		public void setChilds(ProxyVNode[] childs)
		{
			if (childs==null)
			{
				if (childsv==null)
				{
					return; // null == null 
				}
				else
				{
					childsv.clear(); 
				    childsv=null;
				    return; 
				}
			}

			if (childsv==null)
				childsv=new Vector<ProxyVNode>();
	            
			synchronized(childsv)
			{				
				childsv.clear();
				
				for (ProxyVNode node:childs)
					this.childsv.add(node);  
			}
		}

		public ProxyVNode getProxyNodeParent()
		{
			if (proxyNodeParentRef!=null)
				return this.proxyNodeParentRef.get(); 
			
			return null; 
		}
		
		public ProxyVNode getLinkTarget()
		{
			return linkTarget; 
		}

		public void setLinkTarget(ProxyVNode pnode)
		{
			this.linkTarget=pnode;
		}

		public boolean hasGetChildsException()
		{
			return hasGetChildsException; 
		}

		public void setHasGetChildsException(boolean val)
		{
			hasGetChildsException=val; 
		}
    }
       
    static
    {
    	initClass();
    }
    
    private static void initClass()
    {
    	// static instances moved to UIGlobal 
    	vfs=UIGlobal.getVFSClient(); 
    	//vrsContext=UIGlobal.getVRSContext(); 
    	//guiSettings=UIGlobal.getGuiSettings(); 
    	
    	cacheUpdater = new ProxyVNodeCacheUpdater(); 
    	
    	proxyFactory=ProxyVNodeFactory.getInstance(); 
    } 
    // ========================================================================
    // Class Methods
    // ========================================================================
    
    /**
    * Private Constructor, do not use this one, but use static proxyFactory.createProxyTNode
    * to ensure cache consistency.
    * 
    * @see proxyFactory.createProxyTNode
    */
   protected ProxyVNode()
   {
       super(); 
       vnode = null;
   }
   
   static void assertNotGuiThread(VRL loc) throws VlInternalError
   {
    	UIGlobal.assertNotGuiThread("Cannot perform this action during gui event thread. Must open location first:"+loc); 
   }

    
    // ========================================================================
    // Instance Fields
    // ========================================================================
    
    /** One ProxyTNode always matches one VNode */
    VNode vnode = null;
    
    /**
     * Inner class for cached attributes. <br>
     * All the attributes, properties etc, are stored in a cache. If an
     * attribute is null it needs to be fetched. if cache==null the ProxyTNode
     * is invalid ! Currentty no synchronization is done, since Hashtable is
     * threadsave
     */
    
    /** VAttribute cache */
    private Cache cache = new Cache();
    
    private int busyCounter = 0;
    
    boolean mustrefresh=false; 
 
    void prefetchAttributes()
    {
        try
        {
        	// 
            // Pre Load Link Nodes ! 
        	//
        	
        	if (this.isResourceLink()==true)
        	{
        		loadResourceLink();
        		
        		//
            	// Needed by the GUI, so must prefetch it
            	// but only if the linkTarget has already been resolved. 
            	// (If not, at startup it will take a long to  for
            	// MyVLe to show up ! 
            	// 
        		
        		if (this.cache.linkTarget!=null)
            	{
            		getResourceTypes();
            	}
        	}
        	else
        	{
        		getResourceTypes();	
        	}
        	
        	//this.getMimeType(); // updates cache:
        	
        }
        catch (VlException e)
        {
            logger.logException(ClassLogger.ERROR,e,"Error while prefetching Attributes from:%s\n",vnode);  
        }
    }
   
    /**
     * Get *The* VNode!
     * use sparsely
     */ 
    protected VNode getVNode()
    {
        return vnode;
    }
    
    public String getName()
    {
        // debug+checking
        
        if (vnode == null)
            return ("*** NULL VNODE ***");
        else if (vnode.getLocation() == null)
            return ("*** NULL LOCATION ***");
        
        return vnode.getName();
    }
    
    /** Returns VRL of this Node */ 
    public VRL getVRL()
    {
    	// can happen during openLocation, this is not the place to except.  
        if (vnode!=null)
            return vnode.getLocation();
     
        throw new NullPointerException("Internal VNode is NULL");  
    }
    
    public String getType()
    {
        return vnode.getResourceType();
    }
    
    
    /** Returns mimetype of object, does not resolve LinkNodes */
    public String getMimeType()
    {
        VAttribute attr = this.cache.getAttribute(VAttributeConstants.ATTR_MIMETYPE); 
        if (attr!=null)
            return attr.getStringValue(); 
        
        try
        {
            String mimeStr=vnode.getMimeType();
            attr = new VAttribute(VAttributeConstants.ATTR_MIMETYPE,mimeStr);
            cache.storeAttribute(attr);
            return mimeStr;
        }
        catch (VlException e)
        {
            logger.logException(ClassLogger.ERROR,e,"Error getting mimetype from:%s\n",vnode); 
            return null;
        }
        
    }
    
    // ========================================================================
    // Busy Methods 
    // ========================================================================
    
    public void enterBusy()
    {
        boolean prevBusy= busyCounter>0; 
        // integer acces is atomic 
        this.busyCounter++;
        
        // notify event when changing from not busy to busy. 
        if (prevBusy == false)
        {
            ResourceEvent event = ResourceEvent.createBusyEvent(this
                    .getVRL(), true);
            fireGlobalEvent(event);
        }
    }
    
    public void exitBusy()
    {
        boolean isBusy = false;
        
        this.busyCounter--; // atomic ? 
        isBusy = busyCounter>0; 
        
        // update listeners this node is not busy anymore...
        
        if (isBusy == false)
        {
            ResourceEvent event = ResourceEvent.createBusyEvent(this
                    .getVRL(), false);
            fireGlobalEvent(event);
        }
    }
    
    public boolean isBusy()
    {
        // atomic ? 
        return (this.busyCounter > 0);
    }
    
    // ========================================================================
    // Composite Methods
    // ========================================================================
    
    /** Dummy FINAL object for getChilds method mutex to synchronize multiple getChilds 
     */
    private final Boolean getChildsMutex = new Boolean(true); 
    
    /** 
     * Get childs from node or the linktarget. 
     * This method resolves link nodes 
     * @return
     * @throws VlException
     */
    public ProxyVNode[] getChilds(ViewFilter viewFilter) throws VlException
    {
    	synchronized (getChildsMutex)
    	{
    		if (cache.hasGetChildsException()==true)
    			return null;
    		
    		Throwable t;
    		
    		try
    		{
    			enterBusy();
    			 
    			ProxyVNode[] nodes = _getChilds(viewFilter);
    			cache.setHasGetChildsException(false);
    			exitBusy();
    			return nodes; 
    		}
    		catch (VlException ex1)
    		{
    		    logger.logException(ClassLogger.WARN,ex1,"_getChilds() got exception=%s\n",ex1); 
    			t=ex1; 
    		}
    		catch (Throwable ex2)
    		{
        		logger.logException(ClassLogger.ERROR,ex2,"Internal Error");
    			t=ex2; 
    		}
    		
    		//finally:
    		
    		exitBusy();
    		
			cache.setHasGetChildsException(true);
			
			if (t instanceof VlException)
				throw (VlException)t; 
		
			throw new VlInternalError(t.getMessage(),t); 
    	}
    }
    
    /** 
     * Get childs from node or the linktarget. 
     * This method resolves link nodes 
     * @return
     * @throws VlException
     */
    private ProxyVNode[] _getChilds(ViewFilter viewFilter) throws VlException
    {
    	  
    	// Enter O) 
    	logger.debugPrintf("*** --- --- getChilds() ENTERING for:%s\n",this);
    	
        // mutex for this method: invoking it twice makes no sense:
        synchronized (getChildsMutex)
        {
        	// Pass I) 
        	
            logger.debugPrintf("*** --- +++ getChilds() PASS getChildsMutex (I) for:%s\n",this); 
        	 
        	ProxyVNode childs[]=cache.getChilds(); 
        	
        	// cache must be checked while in Critical Zone
            if (childs != null)
            {
                logger.debugPrintf("*** --- +++ getChilds() RETURNING getChildsMutex (Ia) for:%s\n",this); 
            	return filterChilds(childs,viewFilter); 
            }
            
            //
            // >>> Not Gui After Here <<< 
            //
            
            assertNotGuiThread(this.getVRL()); 

            // resolve Links >>>here<<<  
            
            if (vnode instanceof VResourceLink)
            {
                    VRL linkLoc = ((VResourceLink) vnode).getTargetLocation();
                    ProxyVNode linkNode = proxyFactory.openLocation(linkLoc, false); // do
                    // not
                    // double
                    // resolve
                    // links
                    // !
                    
                    if (linkNode == null) // NOT in hash: unknown
                    {
                        logger.debugPrintf("*** --- +++ getChilds() RETURNING NULL LinkNode (Ib) for:%s\n",this); 
                        return null;
                    }
                    
                    logger.debugPrintf("*** --- +++ getChilds() calling LinkNode (Ic) for:%s\n",this); 
                    
                    return linkNode.getChilds(viewFilter); 
            }
            
            // do actual getChilds(); 
            
            VNode targetNode=this.vnode; 
            
            if ((targetNode instanceof VComposite)==false) 
            {
                logger.debugPrintf("*** --- --- getChilds() RETURNING NULL for non-vcomposite (Id) for:%s\n",this);
               	return null; 
            }
            else
            {
                logger.debugPrintf("*** --- --- getChilds() ENTERING VCompsite (II) for :%s\n",this);
            	VNode nodes[] = ((VComposite) targetNode).getNodes();

            	// do not sort for MyVLe !
            	UIPresentation pres=this.getPresentation(); 
 
            	boolean doSort=false;
                String sortFields[]=null; 
 
                // check viewFilter for defaults: 
                if (viewFilter!=null)
                {
                    sortFields=viewFilter.getSortFields();
                    doSort=viewFilter.doSort(); 
                }
                
                //Presentation can overrule default View Filter: 
                if ((pres!=null) && (pres.getAutoSort()))
            	{
                    if (pres.getSortFields()!=null)
                        sortFields=pres.getSortFields().toArray();
                    doSort=true; //getAutoSort()==true  
            	}
            	
                // actual sort: 
        		if (doSort)
        		    if (sortFields!=null)
        		        VRSSort.sortVNodes(nodes,sortFields,true);
        		    else
        		        VRSSort.sortVNodesByTypeName(nodes,true,true); //faster
                   
            	if (nodes == null)
            	{
            	    logger.debugPrintf("*** --- --- getChilds() RETURNING NULL childs node (IIa) for:%s\n",this); 
            		return null;
            	}
                    
            	int len = nodes.length;
            	
            	synchronized (cache)
            	{
            	    logger.debugPrintf("*** --- +++ getChilds() ENTERING cache MUTEX (III) for:%s\n",this); 
            		
            		childs = new ProxyVNode[len];
            		int index=0; 
                        
            		for (int i = 0; i < len; i++)
            		{
            			VNode node = nodes[i];
                            
            			/*
            			 * // resolve LinkNodes if ((node.isVLink()) &&
            			 * ((node instanceof LinkNode)==false) )
            			 * node=LinkNode.loadFrom(node);
            			 */
            			
            			if (node!=null) // arg, check for buggy implementations !
            			{
            				childs[index] = proxyFactory.createProxyTNode(node);
            				childs[index].cache.setParent(this);
            				index++;
            			}
                            // else will be filter'd out 
            		}
                                    
            		cache.setChilds(childs);
            		logger.debugPrintf("*** --- --- getChilds() LEAVING cache MUTEX (IIIa) for:%s\n",this); 
            	}// synchronized (cache)
            }//  if (targetNode instanceof VComposite)
           
        }// synchronized(this)
         
        // Passed MUTEX: childs are in cache: 
        logger.debugPrintf("*** --- --- getChilds() LEAVING cache MUTEX (Ie) for:%s\n",this); 
        return filterChilds(cache.getChilds(),viewFilter); 
    }
    
    private ProxyVNode[] filterChilds(ProxyVNode[] childs, ViewFilter viewFilter)
    {
        if (childs==null) 
            return null; 
        
        // No viewFilter ? Return childs as-is. 
        
        if (viewFilter==null) 
            return childs; 
        
        ProxyVNode filtered[]=new ProxyVNode[childs.length]; 
        int index=0; 
        
        for (ProxyVNode node:childs)
        {
            try
            {
                if (node==null)
                {
                    logger.debugPrintf("Need cache update: NULL child node for this:%s\n",this); 
                }
                //
                // Web Service: optimizalization : first operand must be filterHidden or else
                // isHidden() is called when it is not needed
                
                else if ((viewFilter.filterHidden==true) && (node.isHidden()==true))
                {
                    ;//  do not add 
                }
                else
                {
                    filtered[index++]=node; 
                }
            }
            catch (VlException e)
            {
                filtered[index++]=node; 
                logger.debugPrintf("ProxyTNode","isHidden() returned: Exception:%s\n",e); 
            }
        }
        
        ProxyVNode filtered2[]=new ProxyVNode[index];
        
        for (int i=0;i<index;i++) 
            filtered2[i]=filtered[i];
        
        return filtered2; 
        
    }
    
    public boolean isHidden() throws VlException
    {
        if (cache.isHidden==null)
        {
            VAttribute attr=null;
            
            attr = vnode.getAttribute(VAttributeConstants.ATTR_ISHIDDEN);
            
            if (attr==null) 
                return false;
            
            cache.isHidden=new Boolean(attr.getBooleanValue());
        }
        
        return cache.isHidden; 
    }
    
    public int getNrOfChilds(ViewFilter filter) throws VlException
    {
        ProxyVNode childs[] = this.getChilds(filter);
        
        if (childs == null)
            return 0;
        else
            return childs.length;
    }
    
    /** check whether the node OR the linktarget is composite ! */
    public boolean isComposite(boolean resolve)
    {
    	if (this.cache!=null) 
    		if (this.cache.isComposite!=null) 
    			return cache.isComposite; 
    			 
    	// 
    	// FIXME:  Race Condition when openLocation is called, 
    	// the vnode might still be null ! 
    	// cannot lock/wait for pnode since this will block the gui. 
    	// 
    	
    	if (vnode==null) 
    		return false; //
    	
    	boolean result=true; 
    	
    	// 
    	// Check Links: 
    	// 
        if (resolve && isResourceLink())
        {
        	LogicalResourceNode lnode;
        	
			try
			{
				lnode = this.loadResourceLink();
				
	        	if (lnode!=null)	
	        		result=lnode.getTargetIsComposite(true); 
			}
			catch (VlException e)
			{
				logger.infoPrintf("Could not load link node:%s,Exception=%s\n",this,e); 
				
			} 
        }
        else
        {
        	result=vnode.isComposite(); 
        }
        
        this.cache.isComposite=new Boolean(result); 
        return this.cache.isComposite;
    }
    
    /**
     * Private helper method to invalidize this ProxyTNode and fire an delete
     * Event
     */
    public void disposeAndFireNodeDeleted() throws VlException
    {
        if (cache == null)
        {
            // Still possible? 
            logger.warnPrintf("disposeAndFireNodeDeleted(): Warning: node already disposed:%s\n",this);
            //debugPrintf("*** WARNINIG: _disposeAndNotifyDelete called on already disposed node !!");
        }
        else
        {
        	// clean parents child cache, but this should not be neccessary anymore
        	if (cache.getProxyNodeParent()!=null)
        		if (cache.getProxyNodeParent().cache!=null)
        			this.cache.getProxyNodeParent().cache.setChilds(null);
        }
        
        // notify delete
        fireNodeDeleted(this); 
        // really dispose myself:
        this.dispose();
    }
    
    public void fireChildAddedEvent(ProxyVNode node)
    {
        // clear child list!:
        this.cache.setChilds(null); // null list retriggers getChilds();
        fireChildAdded(this,node); 
    }
    
    public ProxyVNode getParent() throws VlException
    {
        if (cache.getProxyNodeParent() == null)
        {
            VNode parentvnode = vnode.getParent();
            
            if (parentvnode != null)
            {
            	// changed:
            	ProxyVNode parent=proxyFactory.getFromCache(parentvnode.getLocation());
            	
            	// new parent: 
                if (parent == null)
                {
                    // create and store new ProxyTNode ...
                    cache.setParent(proxyFactory.createProxyTNode(parentvnode)); 
                }
                else
                {
                	// reuse already proxyfied parent node: 
                	cache.setParent(parent); 
                }
            }
            // else: this is probably the root node: root has no parent!
        }
        
        // parent is in cache: 
        return cache.getProxyNodeParent(); 
    }
    
    public String[] getAttributeNames()
    {
        if (cache.attributeNames == null)
            cache.attributeNames = vnode.getAttributeNames();
        
        return cache.attributeNames;
    }
    
    /**
     * fetch attributes from remote VNode.
     * 
     * @param attrNames
     * @return
     * @throws VlException
     */
    public VAttribute[] getAttributes(final String[] attrNames) throws VlException
    {
        VAttribute attrs[] = new VAttribute[attrNames.length];
        
        // new attribute names to fetch: 
        
        Vector<String> newAttrs=new Vector<String>();
        
        logger.debugPrintf("getAttributes:%s\n",this);
        
        // synchronized GetAttribute requests ?
        
        // 
        // Check cache first, then try the VNode
        //
        
        for (int index=0;index<attrNames.length;index++) 
        {
        	String name=attrNames[index]; 
        	// filter out ICON for now:
            if ((name.compareTo(VAttributeConstants.ATTR_ICON) == 0)) 
            {
                attrs[index] = new VAttribute(attrNames[index], getIconURL(48));
            }
            else if ((attrs[index] = cache.getAttribute(name)) == null)
            {
            	// fetch new Attribute: 
                newAttrs.add(name); 
            }
        }
        
        int numUncachedAttributes=newAttrs.size(); 
        
        logger.debugPrintf("#numUncachedAttributes = %d\n", numUncachedAttributes);
        
        // fectch new attributes: 
        if (numUncachedAttributes > 0)
        {
            VAttribute uncachedAttrs[] = null;
            String newAttrs_arr[]=new String[numUncachedAttributes];
            newAttrs_arr=newAttrs.toArray(newAttrs_arr); 
           
            // fetch missing attributes, note that null is allowed a attrname!
            uncachedAttrs = vnode.getAttributes(newAttrs_arr);
            // UPDATE  in cache
            cache.storeAttributes(uncachedAttrs);
        }
        
        // merge new fetched attributes with cached ones (all should be in cache now)
        for (int i = 0; i < attrNames.length; i++)
        {
        	// all atribute should now be in cache: 
        	attrs[i]=cache.getAttribute(attrNames[i]); 
        }
        
        return attrs;
    }
   
    public String toString()
    {
        return "<ProxyTNode id='#" + getID() + "' type="+getType()+" vrl='" + this.getVRL() + "'/>";
    }
     
    /**
     * getDefaultIcon() tries to get an icon (mimetype or otherwise). 
     * 
     * + Non Mimetype: 
     *   - $HOME/.vletrc/icons/				  ; Custom user icons 
     *   - $VLET_INSTALL/icons/               ; Is on CLASSPATH, thus covered by ResouceLoader.getIcon())
     *   
     * + Mimetype icons:  
     * - $HOME/.vletrc/icons/mimetypes        ; For custom user mimetype icons 
     * - $VLET_INSTALL/lib/icons/mimetypes/ 	  ; For custom system mimetype icons
     * - $VLET_INSTALL/lib/icons/<mimetype path>/ ; Mimetype Icons from 'theme' 
     * 
     * + Resource (GUI/menu/swing components, use ResourceLoader directly !): 
     * - $CLASSPATH                           ; For resource icons, is covered by ResourceLoader.getIcon() 
     * - URL/VRL: 'file:///..'			      ; Is covered by ResourceLoader.getIcon() 
     * 
     * I) linknode/resourcenode with (optional) private iconURL
     * II) mimetype 
     * III) Default/backup icon
     * IV) broken image ? 
     * 
     * @param preferredSize 
     *            try to find icon with this size. Return bigger icon if
     *            preferred size is not found !
     * @return the prefed Icon, size, might not match.
     * @throws VlException
     */
    public Icon getDefaultIcon(int prefSize,boolean selected)
    {
    	VNode iconNode=vnode; 
    	
    	try
    	{
    		// load Resource Link Description: 
    		
    		if (this.isResourceLink()) 
    			iconNode=this.loadResourceLink();
    		
    		if (iconNode==null)
    		    iconNode=vnode; 
    	}
    	catch (Exception e)
    	{
    	    logger.warnPrintf("Could not resolve Resourcenode:%s\n",vnode);
    	}
    	
    	// New Icon Provider!: 
    	return createDefaultIcon(iconNode, prefSize, selected); 
    }
   
    /** 
     * Try to figure out which icon (URL) to return. 
     * This method also resolves linknodes and uses optional
     * stored link iconUrl as icon Url. 
     * @param size 
     */
    public String getIconURL(int size)
    {
        String iconurl = null;
 
        
        if (this.cache.iconUrl != null)
            return this.cache.iconUrl;

        VNode iconNode=this.vnode; 
        
        
        // Resolve/Load LinkIcons and use stored iconUrl/mimetype
        
        if (isResourceLink())
        {
        	try 
        	{
				iconNode=this.loadResourceLink();
			} 
        	catch (VlException e) 
			{
				logger.infoPrintf("***Warning: Couldn't load resourceLink:%s\n",this);
				// use default icon 
			}
        }
        
        if (iconNode!=null)
        	iconurl = iconNode.getIconURL();
        
        this.cache.iconUrl=iconurl; 
        
        return iconurl; 
    }
   
    public ProxyVNode create(String resourceType, String name) throws VlException
    {
        
        VComposite vcomp=null;
        ProxyVNode parent=null;
        
        // resolve linkNode
        if (this.isResourceLink())
        {
            parent=getTargetPNode();
        }
        else
        {
            parent=this; 
        }
        
        VNode targetNode=parent.getVNode();
        
        if (targetNode instanceof VComposite)
        {
            vcomp=(VComposite)targetNode;
        }
        else
        {
            throw new NotImplementedException("No create method available for:"+this); 
        }
        
        VNode newNode = vcomp.createNode(resourceType,name,true); 
        
        ProxyVNode pnode=null; 
        
        if (newNode != null)
        {
            // use static method to add to hash !
            parent.fireChildAddedEvent(pnode=proxyFactory.createProxyTNode(newNode));
        }
        else
        {
            throw new ResourceCreationFailedException(
                    "Could not create new Resource:" + resourceType);
        }
        
        return pnode;    
    }
      
    /**
     * Clears all cached attributes. Sent an event that resource listeners
     * should check/refetch their attributes.
     */
    public void refresh()
    {
    	final VRL vrl=getVRL(); 
               
    	// trigger refetching:
    	mustrefresh=true;
                    
    	//PRE refresh: clear cache:
    	this.clearCache(); 
    	
    	// trigger refresh of linktargetNode 
    	if ((cache!=null) && (cache.getLinkTarget()!=null))
    		cache.getLinkTarget().mustrefresh=true;
                    
    	try
    	{
    		proxyFactory.openLocation(getVRL()); 
    	}
    	catch (Exception e)
    	{
    		
    	}
    	
    	// Notify ProxyNode listeners that this node has refreshed itself ! 
    	
    	fireGlobalEvent(ResourceEvent.createRefreshEvent(vrl)); 
    }
     
    void clearCache()
    {
        cache = new Cache(); // clean ALL
    }
    
    // Must be called AFTER a refresh event is received. 
    // When performing a refresh on this node, an event will be send also!
    //
	public void handleRefresh()
	{
		this.clearCache(); 
	}
	
    /**
     * Help Garbage collector: Cleanup resources and delete from the node hash
     */   
    synchronized void dispose()
    {
        // Debug("disposing:" + getLocation());
    	// remove cache 
        if (cache != null)
        {
            cache.dispose();
        }
        else
        {
            logger.debugPrintf(">>>\n***\n*** Error: cache already empty !-> double dispose() ???\n***\n");
        }
        
        proxyFactory.hashRemove(this);
        
        //this.cache = null;
        //this.vnode=null;
        //this.disposed = true;
    }
    
    public int compareTo(ProxyNode node)
    {
        if (node == null)
            return 1; // null < this
        
        if (this.getID() == node.getID())
            return 0;
        
        int result = getVRL().compareTo(node.getVRL());
        
        if (result == 0)
        {
            // ToDo: ProxyTNode Cache optimization !!
            // this is possible !
            logger.warnPrintf("Warning: duplicate ProxyTNode with same Location\n");
        }
        
        return result;
    }
    
    /**
     * If this node is a VLink (or other ResourceNode) 
     * this node will load the ResourceNode and return it. 
     * Once a ResourceLink (VLink) is loaded it is equivalent
     * with a LogicalResourceNode.  
     * @throws VlException 
     */
    public LogicalResourceNode loadResourceLink() throws VlException 
    {
    	LogicalResourceNode lnode = null;
        
        if (this.cache.resourceNode != null)
            return this.cache.resourceNode;
        
        // I AM LINKNODE
        if (this.vnode instanceof LogicalResourceNode)
        {
            lnode = (LogicalResourceNode) this.vnode; // I am already a LinkNode
        }
        else if (this.getVRL().isVLink() == true)
        {
            assertNotGuiThread(this.getVRL()); 
            
            // LOAD LINKNODE 
                lnode = LinkNode.loadFrom(vnode.getVRSContext(),this.getVRL());
        }
        else
        {
            throw new ResourceTypeMismatchException(
                    "VNode is not a LinkNode or of saved '.vlink' type:"
                    + vnode);
        }
        
        this.cache.resourceNode = lnode; // cache it
        
        return lnode;
    }
   
    /**
     * Resolves a LinkNode, ResourceLocation or a .vlink resource and 
     * returns the TARGET ProxyNode
     */
    public ProxyVNode getTargetPNode() throws VlException
    {
    	// Global.printMethodCallEntry(3); 
    	
        if (this.cache.getLinkTarget() != null) 
            return cache.getLinkTarget(); 
        
        ProxyVNode pnode = null;
        LogicalResourceNode lnode = null;
        
        if ((this.vnode instanceof LogicalResourceNode) == false)
        {
            if (this.getVRL().isVLink() == true)
            {
            	assertNotGuiThread(this.getVRL());
              	
                // unresolved link node !
                lnode = LinkNode.loadFrom(vnode.getVRSContext(),this.getVRL()); // get linkNode
            }
            else
            {
                throw new ResourceTypeMismatchException(
                        "VNode is not a LinkNode or of saved '.vlink' type:"
                        + vnode);
            }
        }
        else
        {
            lnode = (LogicalResourceNode) this.vnode; // vnode is already of LinkNode type
        }
        
        VRL linkTarget = lnode.getTargetVRL();
        logger.debugPrintf("getLinkTarget:linkTarget=%s\n",linkTarget);
        
        if (linkTarget!=null)
        {
        	pnode=proxyFactory.getFromCache(linkTarget);
        	
        	// pass refresh flag to linkNode: 
        	if ((pnode!=null) && (mustrefresh))
        	{
        		pnode.mustrefresh=mustrefresh; 
        		pnode=null;// triger openLocation+refresh
        	}
            
            if (pnode==null)
            {
            	assertNotGuiThread(linkTarget); 
            	
                // no further resolving: openlocation with resolvelinks=false!
                pnode = proxyFactory.openLocation(linkTarget, false);
            }
        }
        else
        {
            throw new ResourceLinkIsBorkenException("LinkNode has NULL link target location");
        }
        
        this.cache.setLinkTarget(pnode); 
        
        return pnode;
    }
    
    public VRL getTargetVRL() throws VlException
    {
        if (this.isResourceLink()==false)
            return null; 
        
        return loadResourceLink().getTargetVRL();
    }
//    
//    public String getTargetMimeType() throws VlException
//    {
//        LogicalResourceNode lnode = this.loadResourceLink();
//        return lnode.getTargetMimeType();
//    }
//    
    private boolean exists() throws VlException
    {
        if (vnode instanceof VFSNode)
            return ((VFSNode)vnode).exists();
        
        return true; 
    }
    
    public boolean isLogicalNode()
    {
    	if (vnode instanceof VLogicalResource)
    		return true;
    	
    	return false; 
    }
    	
    public boolean isResourceLink()
    {
    	if (vnode instanceof VResourceLink)
    		return true; 
        
        if (this.getVRL()==null) 
        	return false; 

        // All .vlink AND .vrsx files are ResourceLinks ! 
        if (this.getVRL().isVLink() == true)
            return true;
        
        return false;
    }
    
    public String[] getResourceTypes()
    {
        if (cache.resourceTypes != null)
            return cache.resourceTypes;
        
        if (vnode==null) 
        	return null; 
        
        if (vnode.isComposite())
        {
            cache.resourceTypes = ((VComposite) vnode).getResourceTypes();
        }
        else if (this.isResourceLink())
        {
            // must fetch linktarget resourceTypes
        	
            ProxyVNode node = null;
            
            try
            {
                node = this.getTargetPNode();
                if ((node!=null) && (node.isComposite()))
                {
                	cache.resourceTypes = ((VComposite) node.getVNode())
                    .getResourceTypes();
                }
                
            }
            catch (VlException e)
            {
                // this is not the place for exception handling:
                UILogger.logException(this,ClassLogger.ERROR,e,"Couldn't resolve target of:%s\n",this); 
            }
        }
        
        return this.cache.resourceTypes;
    }
    
    public VAttribute[][] getACL() throws VlException
    {
        if (vnode instanceof VACL) 
            return ((VACL)vnode).getACL();
        
        throw new NotImplementedException("Resource doesn't support ACLs");
        /*
         VAttribute attrs[][]=new VAttribute[1][]; 
         attrs[0]=new VAttribute[3]; 
         
         attrs[0][0]=new VAttribute(VAttribute.ATTR_USERNAME,"current");
         attrs[0][0].setEditable(false);
         attrs[0][1]=new VAttribute(VFSNode.ATTR_ISREADABLE,isReadable()); 
         attrs[0][1].setEditable(false); 
         attrs[0][2]=new VAttribute(VFSNode.ATTR_ISWRITABLE,isWritable());
         attrs[0][2].setEditable(false);
         
         return attrs;*/
    }
    
    public void setACL(VAttribute[][] acl) throws VlException
    {
        if (vnode instanceof VACL)
        {
            ((VACL)vnode).setACL(acl);
            return; 
        }
        
        throw new NotImplementedException("Resource doesn't support ACLs");
    }
    
    public VAttribute[] getACLEntities() throws VlException
    {
        if (vnode instanceof VACL) 
            return ((VACL)vnode).getACLEntities();
        
        throw new NotImplementedException("Resource doesn't support ACLs");
    }
    
    public VAttribute[] createACLRecord(VAttribute entity, boolean writeThrough) throws VlException
    {
        if (vnode instanceof VACL) 
            return ((VACL)vnode).createACLRecord(entity,writeThrough); 
        
        throw new NotImplementedException("Resource doesn't support ACLs");
    }
    
    public void transfer(VRL source, VRL dest) throws VlException
    {
    }

    public UIPresentation getPresentation()
    {
    	if  (cache.presentation!=null)
    		return cache.presentation; 
    	
    	//
    	// Instance Presentation object ! 
    	//
    	
    	if (vnode instanceof UIPresentable)
    	{
    		cache.presentation=((UIPresentable)vnode).getPresentation();
    		
    		if (cache.presentation!=null) 
    			return cache.presentation;
    	}
    	
    	//
    	// check default Presentation store: 
    	// (Only stores Presentation per {Scheme,Host,Type} 
    	// 
    	
    	UIPresentation pres=null; 

    	// Check existing for {Scheme,Host,Type} triple !
       	pres=VRSPresentation.getPresentationFor(
                vnode.getScheme(),
                vnode.getHostname(),
                vnode.getResourceType(),
                false);

    	// No existing: Create Default 
    	// update presentation database: 
    	//
    	if (pres==null)
    	{
           	pres=VRSPresentation.getPresentationFor(
                    vnode.getScheme(),
                    vnode.getHostname(),
                    vnode.getResourceType(),
                    true);
    	}
    		
        cache.presentation=pres;
        
        return cache.presentation;
    }
   
    // ========================================================================
    // Clas Misc.
    // ========================================================================
    
    /** Post Drag and Drop method to check whether a node was moved */
    public static void checkAndNotifyDeletion(VRL location)
    {
        // will throw exception if node not in cache: 
        // Create Delete event with this Node as parameter
        
        ProxyVNode pnode;
        
        try
        {
            pnode = proxyFactory.openLocation(location);
            if (pnode.exists() == true)
                return; // still exists... 
            
        }
        catch (VlException e)
        {
            // do nothing, node was not in cache/invalid location:
        }
        
        // node doesn exist (or doesn't exist anymore). 
        
        ResourceEvent event = ResourceEvent.createDeletedEvent(location);
        fireGlobalEvent(event);
    }
    
    public boolean isMyVLe()
    {
        if (this.vnode instanceof MyVLe)
            return true;
        
        return false; 
    }
   
	void handleChildsEvent(VRL[] childs)
	{
		cacheClearChilds();
	}

	void handleSetAttributesEvent(VAttribute[] attrs)
	{
		 cache.storeAttributes(attrs);
	}
	
	void cacheStoreAttributes(VAttribute[] attrs)
	{
		cache.storeAttributes(attrs);
	}
	    
	void cacheClearChilds()
	{
		cache.setChilds(null); 
	}
	  
	/** Final Object which can server as mutex */ 
    private final Object _deleteMutex = new Object();

    private VRL aliasVrl; 
    
	public boolean delete(boolean recursiveDelete) throws VlException
	{
		logger.debugPrintf("delete:%s (recursive=%s)\n",this,""+recursiveDelete);  
		
		boolean result=false;
		 
		synchronized (_deleteMutex)
		{
			// after synchronized this vnode might not exist anymore 
			//if (vnode.exists() == true)
			{
				if (recursiveDelete)
				{
					result = ((VCompositeDeletable) vnode).delete(true);
					if (result==false)  
						logger.warnPrintf("Warning: (VComposite) delete resulted FALSE, but NO exception was thrown for deletion of node:%s\n",vnode); 

				}
				else
				{
					if (vnode instanceof VDeletable)
					{
						result = ((VDeletable) vnode).delete();
						
						if (result==false)  
							logger.warnPrintf("Warning. Delete resulted FALSE, but NO exception was thrown for deletion of node:%s\n",vnode); 

					}
					else
					{
						throw new ResourceException("Node is not deletable:"+vnode); 
					}
				}
			}
			/*else
			{
				Global.warnPrintln(this,"Warning. Node doesn't exists anymore:"+vnode); 
				result = true; // node has been deleted already. 
			}*/		
		}	
            		
		if (result == true)
		{
			// I am not valid anymore: notify all resource listeners
			ProxyVNode.this.disposeAndFireNodeDeleted();
		}
		
		return result;  
	}
	
	protected void finalize()
	{
	    // logger.infoPrintf("Finalizing:%s\n",this); 
	}

	public VRL renameTo(String name,boolean nameIsPath) throws VlException
	{
		VRL oldLocation = vnode.getVRL().duplicate(); 
		
		VRL newLocation=null;  
   
		if (vnode instanceof VRenamable)
		{
		    newLocation = ((VRenamable) vnode).rename(name, nameIsPath);
		}
		else
		{
			throw new NotImplementedException(Messages.E_renameble_interface_not_implemented);
		}
   
		//fire rename event by using the locations! Not ProxyTNode !
		
		if (newLocation!=null)
			fireRenameEvent(oldLocation,newLocation,VRL.basename(name));
		
		return newLocation; 
	}
	
	public void setAttributes(VAttribute attrs[],boolean refresh) throws VlException
	{
	    
		if (vnode instanceof VEditable) 
		{
			// update attributes
			((VEditable) vnode).setAttributes(attrs);
			// update attributes only:
			fireGlobalEvent(ResourceEvent.createAttributesChangedEvent(this.getVRL(),attrs)); 
			
		    // CLEAR: since implementation migh change/correct
			// attributes, do NOT store attributes here !!!
			// 
			
		    this.cache.attributeHash.clear(); 
		     
			// refresh ProxyTNode (All) !
			if (refresh)
				refresh();
		}
		else
		{
			throw new ResourceNotEditableException("Resource is not editable:"	+ this);
		}	
	}
		
    /** Create in this Composite node a new LinkNode to the specified pnode */
    public void createLinkTo(ProxyNode pnode) throws VlException
    {
        VRL parentLoc = this.getVRL();
        LogicalResourceNode lnode = null;
        
        boolean isLink=false; 
        
        // resolve location:
        if (this.isResourceLink())
        {
        	parentLoc=getTargetVRL(); 
        	isLink=true; 
        }
        
        // === LinkToLink === 
        // When creating a link to a link use linktarget of source ! 
        // (Resolve one level of Indirection) 
        // 
        if (pnode.isResourceLink())
        {
            VRL loc = pnode.getTargetVRL(); 
            createLinkTo(loc); 
            return; 
        }
        
        
        //else
        {
            lnode = LinkNode.createLinkTo(UIGlobal.getVRSContext(),parentLoc, "Link to "
                    + pnode.getName(), pnode.getVRL());
        }
        
        // Extra Feature: set (default) icon url to default icon of pnode !
        lnode.setIconURL(pnode.getIconURL(48));
        lnode.save();
        
        if (lnode.getVRL()==null)
            throw new VlInternalError("Internal Error: New create LinkNode has NULL VRL when creating a link to:"+pnode);
        
            //refetch logiccal VRL, this might have changed ! 
        fireChildAdded(getVRL(),lnode.getVRL());

    	// Update Storage location also: 
        if (isLink)
        	fireChildAdded(parentLoc,lnode.getVRL());
 
        // announce new LinkNode description location.
        //VRL storageLocation=lnode.getDescriptionNode().getVRL();
        //
        //fireChildAdded(getVRL(),storageLocation);
    }
    
    // ========================================================================
    // Class Misc.
    // ========================================================================
    
    /** 
     * Disposes all resource hold by this class. 
     * To help the garbage collector and speed up the
     * termination of the JVM, this method clear the hashes and
     * dispose resources. 
     */
    public static void disposeClass()
    {
        proxyFactory.clearNodeHash();
        vfs.close();
    }
    
	 /**
     * Does a logical comparison of Locations. 
     * <p>
     * Checks in the cache whether this ProxyLocation has 'equivalents'. 
     * Current problem is that file://~/location and file://home/user/location 
     * are logical equivalents but their locations are NOT the same. 
     * This method checks the cache for resolved locations, and 
     * uses THAT location to compare whether the locations are the same.
     * <p>  
     * The resolved location is the one the vnode reports as location.
     * For file:///~, this would be the absolute path to the users home. 
     * 
     * @param loc
     * @param checkLinkTarget  Whether to compare logical locations as well
     * @return
     */
    public boolean locationEquals(VRL loc, boolean checkLinkTarget)
    {
        // this.getLocation returns already the 'resolved' location 
        // this is the absolute location as returned by the vnode ! 
        // compare to this location, or when this is a VLink, 
        // compare to the absolute & resolved (target) location of the VLink: 
        
        VRL thisLoc=this.getVRL();
        
        logger.debugPrintf("1) locationEquals,       this =%s\n",thisLoc);
        logger.debugPrintf("2) locationEquals, compare to =%s\n",loc);
        
        if (loc==null) 
            return false; 
        
        
        if (thisLoc.compareTo(loc)==0)
        {
            logger.debugPrintf("locationEquals,  found 1: location =%s\n",loc);
            return true;
        }
        
        // Check whether the location is an alias, so resolve the location. 
        // currently the cache is used for this. 
        
        ProxyNode node=proxyFactory.getFromCache(loc);
        VRL resolvedLoc=null; 
        
        if (node!=null)
            resolvedLoc=node.getVRL();
        else
            resolvedLoc=loc; // keep current;
        
        logger.debugPrintf("3) locationEquals,  resolved loc=%s\n",resolvedLoc);
        
        
        if (thisLoc.compareTo(resolvedLoc)==0)
        {
            logger.debugPrintf("locationEquals,  found 2: location alias=%s\n"+resolvedLoc);
            return true;
        }
        
        // Check Links: now it is more complicated,
        // use resolved locations:
        
        if ((checkLinkTarget) & (this.isResourceLink())) 
        {
            VRL linkLoc; 
            try
            {
                linkLoc = this.getTargetVRL();
                
                logger.debugPrintf("4 This linkTargetLoc     =%s\n",linkLoc);
                
                if (linkLoc==null) 
                    return false; 
                
                // check plain linktarget: 
                if ((linkLoc!=null) && (linkLoc.compareTo(resolvedLoc)==0)) 
                {
                    logger.debugPrintf("locationEquals,  found 3: this linktarget equals resovled link=%s\n",resolvedLoc);
                    return true;
                }
                
                // resolve this linkTargetNode:
                node=proxyFactory.getFromCache(linkLoc); 
                
                if (node!=null) 
                {
                    linkLoc=node.getVRL(); // get resolved LinkNode location:
                    logger.debugPrintf("5 This resolved linkTargetLoc     =%s\n",linkLoc);
                    
                }
                else
                {
                    ;// keep unresolved linkLoc location: already checked: 
                    return false;
                }
                
                // check against resolved loc: 
                if ((linkLoc!=null) && (linkLoc.compareTo(resolvedLoc)==0))
                {
                    logger.debugPrintf("locationEquals,  found 4: this resolved linktarget equals resovled link=%s\n",resolvedLoc);
                    return true;
                }
                
            }
            catch (VlException e)
            {
                logger.logException(ClassLogger.WARN,e,"Could resolve link\n");
            }                
        }
        // no checks left: 
        return false;    
    }

	@Override
	public boolean isEditable() throws VlException 
	{
		if (this.vnode instanceof VEditable)
		{
			return ((VEditable)vnode).isEditable(); 
		}
		
		return false; 
	}

	@Override
	public boolean instanceOf(Class<?> classOrInterface) 
	{
	    return classOrInterface.isAssignableFrom(this.vnode.getClass());   
	}

    public void setAliasVRL(VRL loc)
    {
        this.aliasVrl=loc; 
    }
    
    public VRL[] getAliasVRLs()
    {
        if (this.aliasVrl==null)
            return new VRL[]{this.aliasVrl}; 
        
        return new VRL[]{this.aliasVrl,getVRL()};
    }  
    
    // ------------------------------------------------------------------------
    // Legacy Methods:
    // -----------------------------------------------------------------------
    
    /**
     * getDefaultIcon() tries to create an icon (mimetype or otherwise). 
     * 
     * + Non Mimetype: 
     *   - $HOME/.vletrc/icons/               ; Custom user icons 
     *   - $VLET_INSTALL/icons/               ; Is on CLASSPATH, thus covered by ResouceLoader.getIcon())
     *   
     * + Mimetype icons:  
     * - $HOME/.vletrc/icons/mimetypes        ; For custom user mimetype icons 
     * - $VLET_INSTALL/lib/icons/mimetypes/       ; For custom system mimetype icons
     * - $VLET_INSTALL/lib/icons/<mimetype theme path>/ ; Mimetype Icons from 'theme' 
     * 
     * + Resource (GUI/menu/swing components, use ResourceLoader directly !): 
     * - $CLASSPATH                           ; For resource icons, is covered by ResourceLoader.getIcon() 
     * - URL/VRL: 'file:///..'                ; Is covered by ResourceLoader.getIcon() 
     * 
     * I) linknode/resourcenode with (optional) private iconURL
     * II) mimetype 
     * III) Default/backup icon
     * IV) broken image 
     * 
     * @param preferredSize 
     *            try to find icon with this size. Return bigger icon if
     *            preferred size is not found !
     * @return the prefed Icon, size, might not match.
     * @throws VlException
     */
    public Icon createDefaultIcon(VNode _vnode,int size,boolean greyOut)
    {
        if (_vnode==null)
        {
            logger.warnPrintf("*** Warning: createDefaultIcon():got NULL VNode\n");
            return null;
        }
        
        IconProvider iconProv = UIGlobal.getIconProvider();
        String mimeType=null;
        
        try
        {
            mimeType=_vnode.getMimeType(); 
        }
        catch (Exception e)
        {
            
        }
        
        logger.debugPrintf("createDefaultIcon [%d,%b] for: %s, mimeType=%s\n",size,greyOut,_vnode,mimeType); 

        
        // for plugins, must use classLoader of plugin class ! 
        ClassLoader nodeClassLoader=_vnode.getClass().getClassLoader(); 

        // =============================================================================
        // LinkNodes  
        // =============================================================================

        boolean showAsLink = false;
        boolean isComposite=false; 
        String iconURL=null;  
        //VRL targetVRL=null; 

        // Resolve LogicalResourceNodes: 
        if (_vnode instanceof LogicalResourceNode)
        {
            try
            {
                // get LogicalResourceNode attributes: 
                LogicalResourceNode lnode=(LogicalResourceNode)_vnode; 
                showAsLink=lnode.getShowShortCutIcon();
                _vnode=lnode;

                isComposite=lnode.getTargetIsComposite(true);
                //targetVRL=lnode.getTargetVRL(); 

                // be reboost when getting linktarget attributes: 

                try
                {
                    mimeType=lnode.getTargetMimeType();
                }
                catch (Exception e)
                {
                    logger.warnPrintf("Couldn't get target mimetype of linknode:%s\n",lnode); 
                    logger.warnPrintf("Exception=%s\n",e); 
                }
                
                try
                {
                    iconURL=lnode.getIconURL(size); 
                }
                catch (Exception e)
                {
                    logger.warnPrintf("Couldn't get target mimetype of linknode:%s\n",lnode); 
                    logger.warnPrintf("Exception=%s\n",e); 
                }
                // Debug("target Is Composite="+isComposite+"for:"+lnode.getTargetVRL());
            }
            catch (Exception e)
            {
                logger.errorPrintf("Error getting LogicalNode from:%s\n",_vnode); 
                logger.errorPrintf("Exception=%s\n",e); 
            }
        }
        else
        {
            // use vnode defaults: 
            isComposite=_vnode.isComposite();
            //targetVRL=vnode.getVRL(); 

            try
            {
                mimeType=_vnode.getMimeType();
            }
            catch (VlException e)
            {
                logger.warnPrintf("Could not get Mimetype of:%s\n",_vnode); 
                logger.warnPrintf("Exception =%s\n",e); 

                e.printStackTrace();
            }
            iconURL=_vnode.getIconURL(size); 
        }
        
        if (StringUtil.isEmpty(iconURL)==true)
            iconURL=null; // set empty string "" to null 
        
        if (StringUtil.isEmpty(mimeType)==true)
            mimeType=null; // set empty string "" to null 
    
        // =============================================================================
        // Resolve Icon URL   
        // =============================================================================

        if (StringUtil.isEmpty(iconURL)==false) 
        {
            logger.debugPrintf("createDefaultIcon: I)\n");
            
            Icon icon=iconProv.createIcon(nodeClassLoader,iconURL,showAsLink,size,greyOut);
         
            if  (icon!=null)
            {
                logger.debugPrintf("createDefaultIcon: I) found:%s\n",iconURL);
                return icon;
            }
            logger.debugPrintf("createDefaultIcon: I)  NULL for:%s\n",iconURL);
        }

        // =============================================================================
        // MimeType Icons 
        // =============================================================================


        if ((iconURL==null) && (mimeType!=null))
        {
            iconURL=iconProv.createMimeTypeIconPath(mimeType);
        
            // try again using full (theme) mimetype path: ./<themes path>/iconURL
            Icon icon = iconProv.createIcon(nodeClassLoader,iconURL,showAsLink,size,greyOut);
            
            if (icon!=null)
            {
                logger.debugPrintf("createDefaultIcon: using theme mimetype IIb):%s\n",iconURL);
                return icon;
            }
        }

        // still no icon: 
        if (iconURL!=null)
        {
            logger.debugPrintf("createDefaultIcon: using mimetype IIa):%s\n",iconURL);
        
            // try ./mimetypes/iconURL subpath 
            Icon icon = iconProv.createIcon(nodeClassLoader,"mimetypes/"+iconURL,showAsLink,size,greyOut);
            
            if (icon!=null)
                return icon;
        }
        
        
//        if (iconURL!=null)
//        {
//            logger.debugPrintf("createDefaultIcon: using theme mimetype IIb):%s\n",iconURL);
//        
//            // try again using full (theme) mimetype path: ./<themes path>/iconURL
//            Icon icon = renderIcon(nodeClassLoader,mime_icons_theme_path+"/"+iconURL,showAsLink,size,greyOut);
//            
//            if (icon!=null)
//                return icon;
//        }
        
        // =============================================================================
        // Default Resource Icons (File,Folder,...) 
        // =============================================================================

        logger.debugPrintf("createDefaultIcon: III):%s\n",iconURL);
        
        if (isComposite)
        {
            
            if (_vnode instanceof VDir)
            {
                if (UIGlobal.getProxyVRS().getConfigManager().getUserHomeLocation().compareTo(_vnode.getVRL()) == 0)
                    return iconProv.getHomeFolderIcon(size);
                else
                    return iconProv.getFolderIcon(size);
            }
            
            return iconProv.getFolderIcon(size);
        }
        else
        {
            return iconProv.getFileIcon(size); 
        }
        
    }
}