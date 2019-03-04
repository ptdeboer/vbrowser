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

import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;



/**
 * Add this to your component if this component can
 * be a DRAG source. 
 */
public class VDragSourceListener implements  DragSourceListener
{
	  /** jdk recommends one dragsource listener per JVM */ 
	  private static VDragSourceListener dragSourceListener=new VDragSourceListener();
	  
	  public static VDragSourceListener getDefault() 
	  {
		return dragSourceListener;
	  }
	  
	  // === //
	  
	  /** Class Object */ 
	  
	  VDragSourceListener()
	  {
          // PLAF depend drag source. Represents OS 
		  DragSource dragSource = DragSource.getDefaultDragSource();
		  // install one-for-all drag source listener...
		  dragSource.addDragSourceListener(this); 
          
	  }
	  
	  // Is called when ANY drag is initiated 
	  public void dragEnter(DragSourceDragEvent dsde)
      {
	        //UIGlobal.debugPrintln(this, "dragEnter:" + dsde);
	        //DragSourceContext dse = dsde.getDragSourceContext();
	        //Transferable t = dse.getTransferable();
            //DragSourceContext dsc = dsde.getDragSourceContext();            
      }
	  
	  public void dragOver(DragSourceDragEvent dsde)
	  {
	      //Global.debugPrintln(this, ">>> dragSource dragOver:" + dsde);
          //DragSourceContext dsc = dsde.getDragSourceContext();
          //dsc.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR)); 
	  }
	  
	  public void dropActionChanged(DragSourceDragEvent dsde)
	  {
          int mods = dsde.getGestureModifiers();          
          //Global.debugPrintln(this, "dropActionChanged:" + dsde);
          //Global.debugPrintln(this, "gesture modifiers=:"+mods); 
          DragSourceContext dsc = dsde.getDragSourceContext();
          //dsc.setCursor()
	  }

	  public void dragExit(DragSourceEvent dse)
	  {
	        //Global.debugPrintln(this, "dragExit:" + dse);
	  }

	  public void dragDropEnd(DragSourceDropEvent dsde)
	  {
	        //Global.debugPrintln(this, "dragDropEnd:" + dsde);
	        DragSourceContext sourceContext = dsde.getDragSourceContext();

	  }

}
