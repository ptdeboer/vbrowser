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

package nl.esciencecenter.vlet.gui.icons;


public class IconViewType 
{
	// === 
	// Class fields 
	// === 

	public static enum Orientation 
	{
		HORIZONTAL, VERTICAL
	};

	final public static IconViewType ICONS64=new IconViewType(64); 
	final public static IconViewType ICONS48=new IconViewType(48); 
	final public static IconViewType ICONS32=new IconViewType(32); 
	final public static IconViewType ICONS16=new IconViewType(16); 
	final public static IconViewType LIST_VER16=new IconViewType(true,16, Orientation.VERTICAL);
	final public static IconViewType LIST_HOR16=new IconViewType(true,16, Orientation.HORIZONTAL);
	
	    
	// =====================
	// instance 
	// =====================
	
    public boolean showIcon=true; 
            
    public int cell_hgap=8; // cell horizontal gap between LabelIcons  
    public int cell_vgap=8; // cell vertical gap between LabelIcons  

    public int iconSize = 48;
    
    //public int icon_label_vgap=8; // vertical gap between icon and label  
    //public int icon_label_hgap=8; // horizontal gap between icon and label 
    
    /** Wether to place label right (Horizontal) or below (Vertical) icon */ 
    public Orientation labelOrientation = Orientation.VERTICAL;
    
    /** 
     * Horizontal = place icons left to right (horizontal) fitting in viewport width, 
     *              and expand in vertical direction.  
     * Vertical   = place icons top to down (vertical) fitting in viewport height and  
     *              and expand in horizontal direction.  
     */
    
    public Orientation iconPlacement = Orientation.HORIZONTAL;
    
    IconViewType(int size)
    {
        this.iconSize = size;
    }

    IconViewType(boolean isList,int size, Orientation iconOr)
    {
      
        this.iconSize = size;
        
        if (isList)
        {
            cell_hgap=2; 
            cell_vgap=2; 
            
            this.labelOrientation = Orientation.HORIZONTAL;
        }
        
        this.iconPlacement=iconOr; 
    }
    
    public String toString()
    {
    	return "{"+this.iconSize+","+this.labelOrientation+"}";
    }
}