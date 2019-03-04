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

package nl.esciencecenter.vlet.gui.util;

import javax.swing.JPopupMenu;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskSource;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.MasterBrowser;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.gui.dialog.ExceptionForm;
import nl.esciencecenter.vlet.gui.dnd.DropAction;
import nl.esciencecenter.vlet.gui.proxyvrs.ProxyNode;
import nl.esciencecenter.vlet.gui.view.VComponent;
import nl.esciencecenter.vlet.gui.view.ViewModel;
import nl.esciencecenter.vlet.gui.viewers.ViewerEvent;

/** 
 * Simple File Sector.
 *   
 */
public class FileSelectorController implements MasterBrowser, ITaskSource
{
	final FileSelector fileSelector;
	ProxyNode selectionNode;

	static
	{
        GlobalProperties.init();
        // ProxyVNodeFactory.initPlatform();
	}
	
	public FileSelectorController(FileSelector selector)
	{
		this.fileSelector=selector; 
	}
	
	public void exit()
	{
		Message("openLocation="+this.selectionNode.getVRL().toString()); 	
		this.fileSelector.dispose(); 
	}

	public JPopupMenu getActionMenuFor(VComponent comp)
	{
		return null;
	}

	public ViewModel getViewModel() 
	{
		return ViewModel.getDefault(); 
	}
	

	public void performDragAndDrop(DropAction action) 
	{
	}
	
	public void notifyHyperLinkEvent(ViewerEvent event) {}

	public String getID() 
	{
		return "FileSelector";
	}

	

	public void messagePrintln(String str) 
	{
		
	}

	public void setHasTasks(boolean b)
	{
		
	}

	public void setRoot(final VRL vrl)
	{
		// open location in background: 
		
		ActionTask openTask=new ActionTask(this,"Open location:"+vrl)
		{

			@Override
			protected void doTask() throws VrsException
			{
				ProxyNode node;
				
				if (vrl==null)
					node=UIGlobal.getProxyVRS().getProxyNodeFactory().openLocation(UIGlobal.getProxyVRS().getVirtualRootLocation());
				else
					node=UIGlobal.getProxyVRS().getProxyNodeFactory().openLocation(vrl); 
				
				fileSelector.resourceTree.setRootNode(node);
			}

			@Override
			public void stopTask()
			{
				
			}
		};
		openTask.startTask(); 
	}

	public void setRootNode(ProxyNode node) throws VrsException 
	{
		this.fileSelector.resourceTree.setRootNode(node); 
	}
	
	public void Message(String msg)
	{
		System.out.println("FileSelector:"+msg); 
	}

	public void startNewWindow(VRL vrl)
	{
		// ignore 
	}
	
	public void refresh() throws VrsException
	{
		if(selectionNode!=null)
		{
			_asyncRefresh(selectionNode);
		}
	}
	
	private void _asyncRefresh(final ProxyNode node)
	{
		  // go background: 
	     ActionTask refreshTask=new ActionTask(this,"refresh")
	     {
	     	public void doTask() throws VrsException
	     	{
	     		node.refresh(); 
	     	}

	     	@Override
	     	public void stopTask()
	     	{
	     	}
	     };
	     
	     refreshTask.startTask(); 
	}

	public void performSelection(VComponent comp) 
	{
	    if (comp==null)
	    {
	        // clear selection
	        this.selectionNode=null;
	        return; 
	    }
	    
		// resolve logical resource nodes:
		final nl.esciencecenter.vlet.gui.data.ResourceRef ref=comp.getResourceRef();
		
		if (ref==null)
		    return;
		
        final VRL vrl=ref.getVRL();
        
        if (vrl==null)
            return; 
        
		ActionTask updateTask=new ActionTask(this,"FileSelection.openLocation")
		{
		    public void doTask()
		    {
		        try
		        {
		            ProxyNode pnode=ProxyNode.getProxyNodeFactory().openLocation(vrl,true);
		            if (pnode!=null)
		                updateSelectionNode(pnode); 
		        }
		        catch (Exception e) 
		        {
		            notifyTaskException(this,e); 
		        }   
		    }

            @Override
            public void stopTask()
            {
                
            }
		};
		
		updateTask.startTask();
	}

	protected void updateSelectionNode(ProxyNode pnode)
	{
        this.selectionNode=pnode;           
        String path= pnode.getVRL().toString();
        this.fileSelector.locationTextField.setText(path);
	}
	
	public void performAction(VComponent comp)
	{
		performSelection(comp);
	}

	public void perform2Action(VComponent comp)
	{
		fileSelector.bpvalid.doClick();
	}

    @Override
    public void registerTask(ActionTask actionTask)
    {
    }

    @Override
    public void notifyTaskStarted(ActionTask actionTask)
    {
    }

    @Override
    public void notifyTaskTerminated(ActionTask actionTask)
    {
    }

    @Override
    public void unregisterTask(ActionTask actionTask)
    {
    }

    @Override
    public void notifyTaskException(ActionTask actionTask, Throwable t)
    {
        ExceptionForm.show(this.fileSelector,t,true);       
    }
	
    public String getTaskSourceName()
    {
        return "FileSelectorController";  
    }

    
}
