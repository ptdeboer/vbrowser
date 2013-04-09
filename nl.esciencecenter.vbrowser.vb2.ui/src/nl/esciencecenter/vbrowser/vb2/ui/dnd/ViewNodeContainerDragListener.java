/*
 * 
 */
package nl.esciencecenter.vbrowser.vb2.ui.dnd;

import java.awt.Cursor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNodeComponent;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNodeContainer;

/**
 * Drag Gesture listener for ViewNodeComponents. 
 * Drag Recognized event comes from the Global Drag Gesture Recognizer.
 * 
 * This Drag listener checks and selects the actual selected ViewNode inside a ViewNodeContainer. 
 *  
 * @author Piter T. de Boer 
 */
public class ViewNodeContainerDragListener implements DragGestureListener
{
	
	public ViewNodeContainerDragListener()
	{
	    // no parent needed as this is installed on ViewNodeComponent only.
	    // source Component of DragGestureEvent (getComponent) MUST be of this type. 
	}
	
    public void dragGestureRecognized(DragGestureEvent dge)
    {
    	// DnD Stuff: 
    	
        //Global.debugPrintln(this, "dragGestureRecognized:" + dge);
        int action=dge.getDragAction(); 
        // Use DragSource ? 
    
        InputEvent trigger=dge.getTriggerEvent();
        // non swing not supported 
        JComponent comp = (JComponent)dge.getComponent();
        
        //Global.debugPrintln(this, "source comp="+comp);
        
        boolean multi=((dge.getTriggerEvent().getModifiersEx() & InputEvent.CTRL_DOWN_MASK)!=0);
         
        if ((comp instanceof ViewNodeComponent)==false)
        {
            DnDUtil.errorPrintf(this.getClass()+":Actual component is not a ViewNode:%s\n",comp); 
            return; 
        }
        
        // 
        { 
            ViewNodeComponent node=(ViewNodeComponent)comp; 
            // node could be the parent itself! 
            ViewNodeContainer parent = node.getViewContainer(); 
    
            DnDUtil.debugPrintf("Drag on ViewNode!:%s\n",node.getViewNode().getVRI()); 
            
            // Redirect Drag Selection to Parent to enable multi-select. 
            if (parent!=null)
            {
                if (multi==true)
                // When starting a multi selected drage: Include CURRENT Selection. 
                    parent.setNodeSelection(node.getViewNode(), true); 
                else
                {
                    // Unselect ALL if a drag is started WIHTOUT the CTRL modifier
                    parent.clearNodeSelection(); 
                    // update to single selection: 
                    parent.setNodeSelection(node.getViewNode(), true); 
                }
            }
            else
            {
                // single stand-alone component: 
                
            }
        }
       
        VRI vri=((ViewNodeComponent)comp).getViewNode().getVRI(); 

        DnDUtil.debugPrintf("Drag Vri:%s\n",vri); 
        
       	// Swing way to initiate a Drag: 
       	TransferHandler trans = comp.getTransferHandler(); 
       	if (trans!=null)
       	    trans.exportAsDrag(comp,trigger, DnDConstants.ACTION_COPY);
       	else
       	    DnDUtil.errorPrintf("***Fatal: NULL TransferHandler for:%s\n"+comp);
      
     }
    
    private Cursor selectCursor(int action)
    {
        return (action == DnDConstants.ACTION_MOVE) ? DragSource.DefaultMoveDrop
                : DragSource.DefaultCopyDrop;
    }
    
}
