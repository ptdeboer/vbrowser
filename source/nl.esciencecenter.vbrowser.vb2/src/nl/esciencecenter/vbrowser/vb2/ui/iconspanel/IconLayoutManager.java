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

package nl.esciencecenter.vbrowser.vb2.ui.iconspanel;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Point;
import javax.swing.JViewport;


import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.model.UIViewModel;

/** 
 * Simple icons layout manager. 
 * Starts icons in upper right and adds icons either 
 * in top down (fitting in window) and expand left to right order
 * (Horizotal Orientation).
 * Or left right (fitting in window) and expanding top - down 
 * in vertical direction (Vertical Orientation).
 *  
 * @author Piter T. de Boer.
 */
public class IconLayoutManager implements LayoutManager
{
	private static ClassLogger logger; 
	
	{
		logger=ClassLogger.getLogger(IconLayoutManager.class); 
	}
	
	private UIViewModel uiModel;
	private Dimension prefSize;
	private boolean changed=true; 
	
	public IconLayoutManager(UIViewModel model)
	{
		this.uiModel=model; 
	}

	public void setUIModel(UIViewModel model)
	{
		this.uiModel=model;
	}

	public UIViewModel getUIModel()
	{
		return this.uiModel; 
	}

	public void addLayoutComponent(String name, Component comp)
	{
		this.changed=true; 
		logger.debugPrintf(">>> addLayoutComponent:'%s' => %c <<<\n",name,comp); 
	}

	public void layoutContainer(Container parent)
	{
		checkAlignIcons(parent); 
	}
	
	private Dimension checkAlignIcons(Container parent)
	{
	    // BUGG !
	    // if (this.changed==true) 
	    this.prefSize=alignIcons(parent);
		this.changed=false; 
		return this.prefSize; 
	}
	
	public Dimension minimumLayoutSize(Container parent)
	{
		logger.debugPrintf(">>> minimumLayoutSize() <<<\n");
		return checkAlignIcons(parent);  
 
		// return alignIcons(parent); 
	}

	public Dimension preferredLayoutSize(Container parent)
	{
		logger.debugPrintf(">>> preferredLayoutSize() <<<\n");
		return checkAlignIcons(parent);  
	}
	
	public void removeLayoutComponent(Component comp)
	{
		this.changed=true; 
		logger.debugPrintf(">>> removeLayoutComponent: %s <<<\n",comp); 
	}
	
	/**
     * Custom Layout method.
     *  
     * Important: Is executed during (SWing) object lock.
     * Do not trigger new resize events to prevent an endless aligniIcons loop !  
     */

    protected Dimension alignIcons(Container container)
    {
    	logger.debugPrintf(">>> alignIcons <<<\n"); 

        int row = 0;
        int column = 0;
        int xpos = uiModel.getIconHGap(); // start with offset 
        int ypos = uiModel.getIconVGap(); // start with offset 
        int maxy = 0;
        int maxx=0; 
        
        
        Dimension targetSize=getTargetSize(container);  

        // get max width and max height 
        int cellMaxWidth=0; 
        int cellMaxHeight=0;
        
        Component[] childs = container.getComponents();
        
        // BODY 
        if ((childs==null) || (childs.length==0)) 
        {
            return new Dimension(0,0); 
        }
        
        // PASS I) Scan buttons for grid width and height 
        for (Component comp : childs)
        {
            if (comp==null)
                continue;
            Dimension size = comp.getPreferredSize(); 
            
            if (size.width>cellMaxWidth)
               cellMaxWidth=comp.getWidth(); 
            if (size.height>cellMaxHeight)
                cellMaxHeight=comp.getHeight();
        }
        
        // scan button for grid width and height 
        for (Component comp : childs)
        {
            if (comp==null)
                continue;
            
            // I) place IconLabel
            Point pos=null;
            
            if (uiModel.getIconLabelPlacement()==UIViewModel.UIDirection.VERTICAL)
            {
                 pos = new Point(xpos+cellMaxWidth/2-comp.getSize().width/2, ypos);
            }
            else
            {
                 pos = new Point(xpos, ypos); // align to left
            }
            
            comp.setLocation(pos);// set new location since GridLayout Somehow doesn't work ! 

            // use GridLayoutManager: STIL DOESN'T WORK 
            //c.gridx=column; 
            //c.gridy=row; 
            //this.add(bicon,c);
            
            // II) Current layout stats  
            int bottom = pos.y + comp.getSize().height;

        	if (bottom > maxy)
        		maxy = bottom; // lowest y coordinate of this row
        	
        	int right = pos.x + comp.getSize().width;

        	if (right > maxx)
        		maxx = right; // rightmost x coordinate of this column

        	//III) new position 
            if (this.uiModel.getIconLayoutDirection()==UIViewModel.UIDirection.HORIZONTAL)
            {
            	xpos+=cellMaxWidth+uiModel.getIconHGap();
            	column++; //next column

            	// check next position
            	if (xpos+cellMaxWidth >= targetSize.width)
            	{
            		// reset to xpos to left margin, increase new ypos. 
            		xpos = uiModel.getIconHGap();;// reset to defaul offset
            		ypos = maxy + uiModel.getIconVGap(); // next row
            		//ypos += celly + browser_icon_gap_width; // next row
            		column = 0;
            		row++;
            	}
            }
            else
            {
            	ypos+=cellMaxHeight+uiModel.getIconVGap();
            	row++;
            	//  check next position
            	if (ypos+cellMaxHeight  > targetSize.height)
            	{
            		// reset to ypos to top margin, increase new xpos. 
            		ypos = uiModel.getIconVGap();;// reset to defaul offset
            		xpos = maxx + uiModel.getIconHGap(); // next row
            		//ypos += celly + browser_icon_gap_width; // next row
            		row = 0;
            		column++;
            	}
            }
            
            if (comp.isVisible()==false)
            	comp.setVisible(true);
            
            // parent may setSize of child! 
            comp.setSize(comp.getPreferredSize()); 

            
        }// *** END for node:nodeLocations
       
        
        // IV) update sizes 
        // IMPORTANT: alignIcons is called during doLayout, so 
        // NO setSize may be called, since this retriggers a doLayout ! 
        // ScrollPane Update: 
        //  when setting the preferredSize, the ParentScrollPane 
        //  will be informed about the new size
        
        //update with real size
        Dimension size = new Dimension(maxx,maxy);
        
        //container.setSize(lastSize); 
        //container.setPreferredSize(size);
        return size; 
    }

	private Dimension getTargetSize(Container container)
	{
        Dimension targetSize=null;
     
        // get parent: 
        Container parent=container.getParent(); 
        
        // Panel is embedded in a ScrollPane or similar widget. 
		if (parent instanceof JViewport)
        {
			// get VISIBLE part of ViewPort as target size. 
		    //Container gran = container.getParent(); 
		    //if (gran instanceof JScrollPane)
        	JViewport vport=(JViewport)parent; 
            targetSize=vport.getExtentSize();
    
            //System.err.println("targetSize="+targetSize);
            //System.err.println("viewOffset="+viewOffset);
        }
        else 
        {
           targetSize = container.getSize();
        }
		
		return targetSize; 
	}

}
