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

package nl.esciencecenter.vbrowser.vb2.ui.dnd;

import java.awt.Component;
import java.awt.Point;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNodeComponent;
import nl.esciencecenter.vbrowser.vrs.net.VRL;

/**
 * ViewNodeDropTarget handler, handles 'drop' on ViewNode Components. 
 * Install this DropTargetListener to support drops on the component. 
 * 
 * Swing/AWT Compatible DnD Support. 
 * 
 * @author P.T. de Boer. 
 *
 */

public  class ViewNodeDropTarget extends DropTarget //, DragSourceListener,
{
    private static final long serialVersionUID = 1985854014807809151L;

    public ViewNodeDropTarget(Component comp) 
    {
		this.setComponent(comp); 
	}

	public void dragEnter(DropTargetDragEvent dtde)
    {
	    super.dragEnter(dtde); 
	    
	    DnDUtil.debugPrintf("dragEnter:%s\n",dtde);

        Component source = dtde.getDropTargetContext().getComponent(); 
        
        // As a ViewNodeDropTarget except only ViewNodeComponents!
        if (source instanceof ViewNodeComponent)
        {
            //  update GUI: select the JButton
            //((VComponent)source).setMouseOver(true);
            // one of my component, so I accept this drop:
            dtde.acceptDrag (DnDConstants.ACTION_COPY_OR_MOVE);
            //DropTargetContext dtc = dtde.getDropTargetContext();
            
            // Accept all DnD actions: 
            //dtde.acceptDrag(dtde.getSourceActions());
            //showUnderDrag(true);
            
        }
        else
        {
        	// dtde.rejectDrag(); // NOT here!
        }
    }
    
    public void dragOver(DropTargetDragEvent dtde)
    {
        super.dragOver(dtde); 
    	DnDUtil.debugPrintf("dragOver:%s\n",dtde);
    	// done in super: 
    	// Component source = dtde.getDropTargetContext().getComponent();
    	// source.requestFocus(); 
    }

    public void dropActionChanged(DropTargetDragEvent dtde)
    {
        super.dropActionChanged(dtde);
        DnDUtil.debugPrintf("dropActionChanged:%s\n",dtde);
    }
   
    public void dragExit(DropTargetEvent dte)
    {
        super.dragExit(dte); 
        //Component source = dte.getDropTargetContext().getComponent(); 
        DnDUtil.debugPrintf("dragExit:%s\n",dte);
    }

    // Actual Drop!
    public void drop(DropTargetDropEvent dtde)
    {
        handleDrop(dtde);
    }
    
    public void handleDrop(DropTargetDropEvent dtde)
    {
        // todo: see super drop() which uses delegates!
        // for now: 
        
        DnDUtil.debugPrintf("drop:%s\n",dtde);
    	
    	DropTargetContext dtc = dtde.getDropTargetContext();
    	Component comp = dtc.getComponent();
    	Point p=dtde.getLocation();
    	
        // II : get data: 
    	Transferable data = dtde.getTransferable();
    	TransferHandler handler=null;
    	
    	if (comp instanceof JComponent)
    	{
    	    JComponent jcomp=((JComponent)comp);
    	    handler = jcomp.getTransferHandler(); 
    	}
    	
    	// check dropped data: 
    	if (DnDData.canConvertToVRIs(data))
    	{
    		// I: accept drop: 
            dtde.acceptDrop (DnDConstants.ACTION_COPY_OR_MOVE);
         
            //II: Do Drop: 
            doInterActiveDrop(comp,p,data); 
            
            // III: complete the drag ! 
            dtde.getDropTargetContext().dropComplete(true);
    	}
    	else
    	{
    		dtde.rejectDrop(); 
    	}
    	
    }
    
    // ========================================================================================
    // Temp
    // ========================================================================================

    /** Interactive drop on JComponent */ 
    public static boolean doInterActiveDrop(Component comp, Point point,Transferable data) 
    {
        DnDUtil.debugPrintf("interActiveDrop():%s\n",comp);

        if ((comp instanceof ViewNodeComponent)==false)
        {
            DnDUtil.errorPrintf("interActiveDrop() Cannot import data to non ViewNodeComponent:%s\n",comp); 
            return false;// ignore mis drop! 
        }
            
        ViewNodeComponent node=(ViewNodeComponent)comp; 
        ViewNode viewNode=node.getViewNode();
            
        // this.interActiveDrop((ViewNodeComponent)comp,point,data); 
        
        if (DnDData.canConvertToVRIs(data)==false)
        {
            DnDUtil.errorPrintf("interActiveDrop(): Unsupported Data/Flavor:%s\n",data);
            return false;
        }
        try
        {
            List<VRL> vris = DnDData.getVRIsFrom(data); 
            DnDUtil.debugPrintf(">>> Actual Drop on ViewNode:%s\n",viewNode); 
            for (int i=0;i<vris.size();i++)
                DnDUtil.debugPrintf(" -vri[#%d]=%s\n",i,vris.get(i));
        }
        catch (Exception e)
        {
            //TODO: Handle!
            DnDUtil.logException(e,"interActiveDrop(): Couldn't get VRIs from data:%s\n",data);
        }
            
        return true; // Handled! 
    }
  
}
