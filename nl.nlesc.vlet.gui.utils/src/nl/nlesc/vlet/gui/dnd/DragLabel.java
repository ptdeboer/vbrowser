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

package nl.nlesc.vlet.gui.dnd;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;

import javax.swing.JFrame;
import javax.swing.JLabel;


import nl.nlesc.vlet.gui.MasterBrowser;
import nl.nlesc.vlet.gui.data.ResourceRef;
import nl.nlesc.vlet.gui.view.VComponent;
import nl.nlesc.vlet.gui.view.VContainer;
import nl.nlesc.vlet.vrl.VRL;

/**
 * Simple Lable which can export a VRL as transferable. 
 * 
 * @author P.T. de Boer
 */
public class DragLabel extends JLabel implements VComponent
{
	private static final long serialVersionUID = -4836113854851253405L;
	
	private DragSource dragSource;
	private DragGestureListener dgListener;
 	private VRL vrl; 
	
	public void setVRL(VRL vrl)
	{
		this.vrl=vrl; 
	}
	
	public DragLabel(String s) 
	{
	    this.setText(s);
	    this.dragSource = DragSource.getDefaultDragSource();
	    this.dgListener = new VDragGestureListener();
	    //this.dsListener = MyDragSourceListener.getDefault(); 

	    // component, action, listener
	    this.dragSource.createDefaultDragGestureRecognizer(
	      this, DnDConstants.ACTION_COPY_OR_MOVE, this.dgListener );
	  }
	  
	  public static void main(String args[])
	  {
	      //ClassLog.getLogger().setLevelToDebug(); 
		  
		  JFrame frame=new JFrame(); 
		  DragLabel dlabel=new DragLabel("Hello World");
		  
		  frame.add(dlabel); 
		  frame.pack(); 
		  frame.setVisible(true);
	  }

	public MasterBrowser getMasterBrowser()
	{
		return null;
	}

	public void setMouseOver(boolean b)
	{		
	}

	public VContainer getVContainer()
	{
		return null;
	}

	public ResourceRef getResourceRef()
	{
		return new ResourceRef(this.vrl,null,null); 
	}
}

