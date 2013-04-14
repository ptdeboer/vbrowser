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

package nl.nlesc.vlet.gui.icons;

import java.awt.Component;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import javax.swing.JPopupMenu;

import nl.nlesc.vlet.gui.GuiSettings;

/** For event ON the icons Panel (not the ButtonIcon componenets) */
class IconsPanelListener implements MouseListener,MouseWheelListener, FocusListener

{
	/**
     * 
     */
    private final IconsPanel iconsPanel;
   
	
	IconsPanelListener(IconsPanel panel)
	{
         iconsPanel=panel;
	}
	
    public void mouseClicked(MouseEvent e)
    {
        //Debug("IconsPanel mouse clicked:"+e);
        
        boolean combine=((e.getModifiersEx()&MouseEvent.CTRL_DOWN_MASK)!=0);
        boolean shift=((e.getModifiersEx()&MouseEvent.SHIFT_DOWN_MASK)!=0);

        // Click ON Canvas 
        if (GuiSettings.isSelection(e))
        {
            // click on canvas nullifies current selection !
            if ((combine==false) && (shift==false))
            {
                iconsPanel.selectAll(false); // unselect previous
                iconsPanel.masterBrowser.performSelection(null);
            }
        }
    }

    // Canvas Click: 
    public void mousePressed(MouseEvent e)
    {
        //Debug("IconsPanel mouse pressed:"+e);
        // update focus ! 
    	this.iconsPanel.requestFocus();
    	
    	boolean combine=((e.getModifiersEx()&MouseEvent.CTRL_DOWN_MASK)!=0);
    	boolean shift=((e.getModifiersEx()&MouseEvent.SHIFT_DOWN_MASK)!=0);

    	// canvas click unselect all 
    	if ((combine==false) && (shift==false))
    	{
    		// unselect before action menu popup
    		iconsPanel.selectAll(false); // toggleSelection(null,false); // unselect previous 
    	}
    	
        if (GuiSettings.isPopupTrigger(e))
        {
            JPopupMenu menu=iconsPanel.masterBrowser.getActionMenuFor(iconsPanel);
            menu.show((Component)e.getSource(),e.getX(),e.getY()); 
        }
        
       
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    	// follow mouse (unfocus IconLabel)
    	this.iconsPanel.requestFocusInWindow(); 
    	//this.iconsPanel.requestFocus(); EVIL ! 
    }

    public void mouseExited(MouseEvent e)
    {
        //Debug("IconsPanel mouseExited:"+e);
    }
   
    public void mouseWheelMoved(MouseWheelEvent e)
    {
        //Debug("IconsPanel mousewheel moved:"+e);
    }

	public void focusGained(FocusEvent e)
	{
       // Debug("IconsPanel focusGained:"+e);
        //this.iconsPanel.setBackground(new Color(128,128,255)); 
	}

	public void focusLost(FocusEvent e)
	{
        //Debug("IconsPanel focusLost:"+e);      
       // this.iconsPanel.setBackground(new Color(128,128,128));
	}
}