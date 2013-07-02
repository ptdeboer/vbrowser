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

package nl.esciencecenter.vlet.gui.dnd;

import java.awt.Component;
import java.awt.Point;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vlet.gui.data.ResourceRef;
import nl.esciencecenter.vlet.gui.view.VComponent;

/** 
 * Generic DropAction object.  
 * Instead of using the (limited) Swing DnD interface, Drag 'n Drops
 * between VComponents can be handled by using this 'DropAction' object.
 * <p> 
 * When a drop on a VComponent is performed the MasterBrowser is called 
 * to perform the actual Drop. 
 * 
 * @author P.T. de Boer
 *
 */
public class DropAction
{
	public static final String COPY_ACTION="Copy"; 
	public static final String MOVE_ACTION="Move"; 
	public static final String LINK_ACTION="Link"; 
	
    public ResourceRef sources[]=null;
    
    public VComponent destination=null;
    
    // public boolean isMove=false;
    // public boolean isAltDrop;       // ALT modifier selected.-> use modifiers
    
    /** component to perform interactive popupmenu. If NULL no Interactive Drop !! */
	public Component component=null;
	
	public Point point=null;
	
	public boolean interactive=true;   // defaults
	
	public String dropAction;
    
	public DropAction(VComponent dropTarget,ResourceRef[] dropSources) 
	{
        sources=dropSources; 
        destination=dropTarget;  
	}

	
    public DropAction(VComponent dropTarget,ResourceRef dest)
    {
        sources=new ResourceRef[1]; 
        sources[0]=dest;
        destination=dropTarget;
    }

	public boolean isLink()
	{
		return StringUtil.equals(dropAction,LINK_ACTION); 
	}
	
	public boolean isMove()
	{
		return StringUtil.equals(dropAction,MOVE_ACTION); 
	}
	
	public boolean isCopy()
	{
		return StringUtil.equals(dropAction,COPY_ACTION); 
	}

}
