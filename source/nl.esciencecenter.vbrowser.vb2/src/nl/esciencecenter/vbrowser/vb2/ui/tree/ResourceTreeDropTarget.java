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

package nl.esciencecenter.vbrowser.vb2.ui.tree;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.util.List;

import nl.esciencecenter.vbrowser.vb2.ui.dnd.DnDData;
import nl.esciencecenter.vbrowser.vb2.ui.dnd.ViewNodeDropTarget;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;


/**
 * Swing/AWT Compatible DnD Support. 
 * 
 * Node in a JTree can have DropTarget's. So the Parent component (JTree) handle the drops. 

 * @author P.T. de Boer. 
 */
public class ResourceTreeDropTarget extends ViewNodeDropTarget
{
    private static final long serialVersionUID = -9095804562165852802L;

    // === // 
    
    //ResourceTree resourceTree; 
	
	public ResourceTreeDropTarget(ResourceTree tree)
	{
		//this.resourceTree=tree; 
		super(tree);
	}

//	public void dragEnter(DropTargetDragEvent dtde)
//	{
//	    super.dragEnter(dtde); 
//	    debugPrintf("dragEnter:%s\n",dtde);
//	    
//	    Component source = dtde.getDropTargetContext().getComponent();
//	    Point p = dtde.getLocation();
//	    ResourceTreeNode node=getRTNode(source,p);  
//	    
//	    dtde.acceptDrag (DnDConstants.ACTION_COPY_OR_MOVE); 
//    }
	  
//    public void dragExit(DropTargetEvent dte)
//    {
//        super.dragExit(dte); 
//        //Component source = dte.getDropTargetContext().getComponent(); 
//        DnDUtil.debugPrintf("dragExit:%s\n",dte);
//    }
    
//	public void dragOver(DropTargetDragEvent dtde) 
//	{
//	    super.dragOver(dtde); 
//	    
//		debugPrintf("dragOver:%s\n",dtde);
//
//		Component source = dtde.getDropTargetContext().getComponent();
//
//		if ((source instanceof ResourceTree) == false) 
//		{
//			errorPrintf("Drag Source object not a ResourceTree!!!\n");
//			return;
//		}
//
//		ResourceTree tree=(ResourceTree)source; 
//		Point p = dtde.getLocation();
//		ResourceTreeNode node=getRTNode(source,p);
////		tree.scrollTo(p); // autoscroll
////		tree.setMouseOverPoint(p);
//		//tree.setFocusNode(node); 	
//
// 
//		if (node == null)
//		{
//			// === No ResourceTreeNode under pointer ! ===
//			// Don't reject: once a reject is issued the whole resourceTree is
//			// considered Rejected ! (This is a potentiel CanvasDrop) 
//			// dtde.rejectDrag();
//			
//			// Do NOT unselect selection paths:: tree.setSelectionPath(null);
//			return;
//		}
//
//		TreeNode[] treeNodes = node.getPath();
//
//		TreePath treePath = new TreePath(treeNodes);
//		// make sure selectio is visible 
//		//tree.setSelectionPath(treePath);
//		
//		// accept in dragEnter
//		// dtde.acceptDrag (DnDConstants.ACTION_COPY_OR_MOVE);
//		
//	}
	
//	public void dropActionChanged(DropTargetDropEvent dtde)
//	{
//	   // super.dropActionChanged(dtde); 
//	     debugPrintf("dropActionChanged:%s\n",dtde); 
//    }
	 
	      
	/** Drop On Node ! */ 
    public void drop(DropTargetDropEvent dtde)
    {
        // todo: check super: drop() ! 
        // Global.debugPrintln(this,"Dropping:"+dtde); 
    	
    	DropTargetContext dtc = dtde.getDropTargetContext();
    	Component comp = dtc.getComponent();
    	Point p=dtde.getLocation();
    	Transferable data = dtde.getTransferable();
    	
    	ResourceTreeNode rtnode = getRTNode(comp,p); 
    	
    	// check dropped data: 
    	if (DnDData.canConvertToVRIs(data))
    	{
    		 // I: accept drop: 
            dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);

            List<VRL> vris;
            
            try
            {
                vris = DnDData.getVRIsFrom(data);
            }
            catch (Exception e)
            {
                logException(e,"Couldn't parse VRIs from data:"+data); 
                dtde.rejectDrop(); 
                return; 
            }
            
            debugPrintf(">>> Actual drop on %s\n",rtnode.getVRI());
            for (int i=0;i<vris.size();i++)
            {
                debugPrintf(" - vri[%d]=%s\n",i,rtnode.getVRI());
            }
        	// supply ResourceTree as (J)Component but TreeNode as VComponent ! 
        	//DnDTransferHandler.getDefault().interActiveDrop(comp,rtnode,p,data);
            // III: complete the drag ! 
            dtde.getDropTargetContext().dropComplete(true);
    	}
    	else
    		dtde.rejectDrop(); 
    }
 
	private void logException(Exception e, String msg,Object... args)
    {
        errorPrintf(msg,args); 
        errorPrintf("Exception=%e\n",e); 
    }

    static ResourceTreeNode getRTNode(Component comp,Point p)
	{
		if ((comp instanceof ResourceTree) == false) 
		{
			errorPrintf("Source object not a ResourceTree!!!\n");
			return null;
		}
		
		ResourceTree tree = ((ResourceTree) comp);

		ResourceTreeNode node = tree.getRTNodeUnderPoint(p);
		return node; 
	}
	

    static private void debugPrintf(String format,Object... args)
    {
        System.err.printf(format,args);
    }

    static private void errorPrintf(String format,Object... args)
    {
        System.err.printf(format,args);
    }
    
}
