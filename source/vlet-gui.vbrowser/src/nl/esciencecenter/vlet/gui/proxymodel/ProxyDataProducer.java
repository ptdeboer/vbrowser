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

package nl.esciencecenter.vlet.gui.proxymodel;

import java.util.ArrayList;

import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskSource;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.UILogger;
import nl.esciencecenter.vlet.gui.proxyvrs.ProxyNode;
import nl.esciencecenter.vlet.gui.proxyvrs.ProxyNodeFactory;
import nl.esciencecenter.vlet.gui.view.ViewFilter;
import nl.esciencecenter.vlet.gui.view.ViewModel;

/** 
 * Data Producer for the ProxyModel classes. 
 * Contains common code for fetching ProxyNodes and other data. 
 */
public abstract class ProxyDataProducer 
{
    
	public abstract class ProxyActionTask extends ActionTask
	{
		public ProxyActionTask(String name)
		{
			super(ProxyDataProducer.this.getTaskSource(),name); 
		}
	}

	// === 
	// 
	// ===
	
	final private ProxyNodeFactory nodeFactory;
	
	final private ViewModel viewModel; 
	
	private ITaskSource	taskSource;
	
	public ProxyDataProducer(ITaskSource source,ProxyNodeFactory factory,ViewModel model)
	{
		this.nodeFactory=factory;
		this.viewModel=model;
		this.taskSource=source;
	}
	
	public ITaskSource getTaskSource()
	{
		return taskSource;
	}
	
	protected void doBackground (ActionTask task)
	{
		task.startTask(); 
	}
	
	/** 
	 * Starts a background task to get the childs of parentVRL.
	 * When done udpateChildNodeFor() is called 
	 */ 
	public void bgGetChildsFor(final VRL parentVrl)
	{
	
//		if (getModel()==null)
//			throw new NullPointerException("Model can not be null"); 
		
		ProxyActionTask fetchTask=new ProxyActionTask("Fetching child nodes of:"+parentVrl)
		{
			private boolean mustStop=false; 
			
			@Override
			protected void doTask() throws VrsException
			{
				ProxyNode parent=nodeFactory.openLocation(parentVrl);

				if (mustStop==true)
				{
					UILogger.warnPrintf(this,"Aborting Action:%s\n",this);
					return; 
				}

				//IProxyModel model = getModel(); 
				try
				{
				    ProxyNode nodes[] = parent.getChilds(getViewFilter());
	                updateChildNodesFor(parent,nodes,false); 
				}
				catch (VrsException ex)
				{
				    if (taskSource!=null)       
				        taskSource.notifyTaskException(this,ex); 
				    else
				    {
				        UILogger.logException(this,ClassLogger.ERROR,ex,"Exception during getChilds()");
				    }
				    
				    mustStop=true; 
				    return;  
				}
				
				if (mustStop==true)
				{
					UILogger.warnPrintf(this,"Aborting Action:%s\n",this);
					return; 
				}
				
			}

			@Override
			public void stopTask() 
			{
				mustStop=true; 
			}
		};
		
		// 
		// VlExceptions will be forwarded to ITaskSource which is 
		// BrowserController ! 
		// 
		
		doBackground(fetchTask);
	}
	
	public ViewFilter getViewFilter()
	{
		return getViewModel().getViewFilter(); 
	}
	
	public ViewModel getViewModel()
	{
		return  this.viewModel;
	}

	/**
	 * Starts a background task to get the child locations. 
	 * The parentLocation specifies which VRL must be used as parent 
	 * When done udpateChildNodeFor() is called.
	 */ 
	protected void bgGetNodesFor(final VRL parentLoc, final VRL locations[],final boolean cumulative) 
	{
	    if ((locations==null || locations.length<=0)) 
	        return;  
	      
		if (locations[0]==null)
		    throw new NullPointerException("null VRL in bgGetNodesFor:"+parentLoc);
		
		ProxyActionTask fetchTask=new ProxyActionTask("Fetching child nodes of:"+parentLoc)
		{
			private boolean mustStop=false; 
			
			
			@Override
			protected void doTask() throws VrsException
			{
				ProxyNode parent=nodeFactory.openLocation(parentLoc);
				
				//IProxyModel model = getModel(); 
				int len=locations.length;
				ArrayList<ProxyNode> nodes = new ArrayList<ProxyNode>(len); 
				
				for (int i=0;i<len;i++)
				{
					if (mustStop==true)
					{
						UILogger.warnPrintf(this,"Aborting Action:%s\n",this);
						return; 
					}
					
					if (locations[i]==null)
					{
					    // STILL A BUG 
					    UILogger.errorPrintf(this, "getNodesFor(): NULL location #"+i+" for parent:%s\n",parentLoc);
					}
					else
					    nodes.add(nodeFactory.openLocation(locations[i])); 
				}
				
				updateChildNodesFor(parent,ProxyNode.toArray(nodes),cumulative); 
			}

			@Override
			public void stopTask() 
			{
				mustStop=true; 
			}
		};
		
		// 
		// VlExceptions will be forwarded to ITaskSource which is 
		// BrowserController ! 
		// 
		
		doBackground(fetchTask);
	}
	
	/** 
	 * When new child nodes have been produced by backgroundGetNodeFor,
	 * this method is called.  
	 */
	
	public abstract void updateChildNodesFor(ProxyNode parent, ProxyNode childs[],boolean cumulative); 
	
}
