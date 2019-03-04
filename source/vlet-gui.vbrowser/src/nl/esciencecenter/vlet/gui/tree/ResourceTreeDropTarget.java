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

package nl.esciencecenter.vlet.gui.tree;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;

import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.vlet.gui.UILogger;
import nl.esciencecenter.vlet.gui.dnd.VTransferData;
import nl.esciencecenter.vlet.gui.dnd.VTransferHandler;

/**
 * Swing/AWT Compatible DnD Support. 
 * The Swing DnD isn't completely implemented.
 *  
 * Currently the MyTransferHandler is broken and the DropTarget.drop() methods 
 * do the action. 
 * 
 * @author P.T. de Boer. 
 *
 */
public class ResourceTreeDropTarget extends DropTarget
{
	ResourceTree resourceTree; 
	
	public ResourceTreeDropTarget(ResourceTree tree)
	{
		this.resourceTree=tree; 
		setComponent(tree);
	}


	public void dragOver(DropTargetDragEvent dtde) 
	{
		Debug("dragOver:" + dtde);

		Component source = dtde.getDropTargetContext().getComponent();

		if ((source instanceof ResourceTree) == false) 
		{
			UILogger.errorPrintf(this, "Drag Source object not a ResourceTree!!!\n");
			return;
		}

		ResourceTree tree=(ResourceTree)source; 
		Point p = dtde.getLocation();
		ResourceTreeNode node=getRTNode(source,p);
		tree.scrollTo(p); // autoscroll
		tree.setMouseOverPoint(p);
		//tree.setFocusNode(node); 	

 
		if (node == null)
		{
			// === No ResourceTreeNode under pointer ! ===
			// Don't reject: once a reject is issued the whole resourceTree is
			// considered Rejected ! (This is a potentiel CanvasDrop) 
			// dtde.rejectDrag();
			
			// Do NOT unselect selection paths:: tree.setSelectionPath(null);
			return;
		}

		TreeNode[] treeNodes = node.getPath();

		TreePath treePath = new TreePath(treeNodes);
		// make sure selectio is visible 
		//tree.setSelectionPath(treePath);
		
		// accept in dragEnter
		// dtde.acceptDrag (DnDConstants.ACTION_COPY_OR_MOVE);
		
	}
	
	public void dragEnter(DropTargetDragEvent dtde)
    {
        Debug("dragEnter:" + dtde);

        Component source = dtde.getDropTargetContext().getComponent();
    	Point p = dtde.getLocation();
		ResourceTreeNode node=getRTNode(source,p);  
		
        dtde.acceptDrag (DnDConstants.ACTION_COPY_OR_MOVE); 
    }
      
	/** Drop On Node ! */ 
	
    public void drop(DropTargetDropEvent dtde)
    {
        // Global.debugPrintln(this,"Dropping:"+dtde); 
    	
    	DropTargetContext dtc = dtde.getDropTargetContext();
    	Component comp = dtc.getComponent();
    	Point p=dtde.getLocation();
    	Transferable data = dtde.getTransferable();
    	
    	ResourceTreeNode rtnode = getRTNode(comp,p); 
    	
    	// check dropped data: 
    	if (VTransferData.canConvertToVRLs(data))
    	{
    		 // I: accept drop: 
            dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);

        	// supply ResourceTree as (J)Component but TreeNode as VComponent ! 
        	VTransferHandler.getDefault().interActiveDrop(comp,rtnode,p,data);
            // III: complete the drag ! 
            dtde.getDropTargetContext().dropComplete(true);
    	}
    	else
    		dtde.rejectDrop(); 
    	
    }
    
    public void dropActionChanged(DropTargetDropEvent dtde)
    {
    	Debug("dropActionChanged:"+dtde); 
    }
    
    private void Debug(String msg)
	{
		UILogger.debugPrintf(this,"%s\n",msg); 
		//Global.errorPrintln(this,msg); 
	}


	static ResourceTreeNode getRTNode(Component comp,Point p)
	{
		if ((comp instanceof ResourceTree) == false) 
		{
		    UILogger.errorPrintf(ResourceTreeDropTarget.class, "Source object not a ResourceTree!!!\n");
			return null;
		}
		
		ResourceTree tree = ((ResourceTree) comp);

		ResourceTreeNode node = tree.getNodeUnderPoint(p);
		return node; 
	}
    
}
