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

package nl.esciencecenter.vlet.gui.dnd;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;


/**
 * Default VComponent Drag Gesture listener.d 
 */
public class ComponentDragGestureListener implements DragGestureListener
{
    JComponent orgComp; 
	
	public ComponentDragGestureListener(JComponent orgComp)
	{
	    this.orgComp=orgComp; 
	}
	
    public void dragGestureRecognized(DragGestureEvent dge)
    {
        //Global.debugPrintf(this, "dragGestureRecognized:%s\n",dge);
        
        // int action=dge.getDragAction();
        // boolean ctrl=((dge.getTriggerEvent().getModifiersEx() & InputEvent.CTRL_DOWN_MASK)!=0);
    	 
    	// Use DragSource ? 
    
        InputEvent trigger=dge.getTriggerEvent();
        JComponent jcomp= (JComponent)dge.getComponent();
        
        //Global.debugPrintf(this, "source comp=%s\n",jcomp);
        
       	// Swing way to initiate a Drag: 
       	TransferHandler trans = jcomp.getTransferHandler(); 
      	trans.exportAsDrag(jcomp,trigger, DnDConstants.ACTION_COPY);
      
     }
    
//    private Cursor selectCursor(int action)
//    {
//        return (action == DnDConstants.ACTION_MOVE) ? DragSource.DefaultMoveDrop
//                : DragSource.DefaultCopyDrop;
//    }
    
}
