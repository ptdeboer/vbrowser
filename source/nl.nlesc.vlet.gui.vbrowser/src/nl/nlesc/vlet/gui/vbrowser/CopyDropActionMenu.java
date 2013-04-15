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

package nl.nlesc.vlet.gui.vbrowser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import nl.nlesc.vlet.gui.MasterBrowser;
import nl.nlesc.vlet.gui.dnd.DropAction;
import static nl.nlesc.vlet.gui.dnd.DropAction.*;

/** 
 * Lightweight CopyDrop ActionMenu 
 * 
 * @author P.T. de Boer
 *
 */
public class CopyDropActionMenu extends JPopupMenu implements ActionListener
{
	private DropAction dropAction;
	private MasterBrowser masterBrowser;

	public CopyDropActionMenu(MasterBrowser bc,DropAction drop)
	{
		JMenuItem item=null;
		this.masterBrowser=bc;
		
		add(item=new JMenuItem(DropAction.COPY_ACTION)); 
		item.addActionListener(this); 
		add(item=new JMenuItem(DropAction.MOVE_ACTION)); 
		item.addActionListener(this); 
		add(item=new JMenuItem(DropAction.LINK_ACTION)); 
		item.addActionListener(this); 
		this.dropAction=drop; 
		
	}

	public static JPopupMenu createFor(MasterBrowser bc,DropAction drop)
	{
		return new CopyDropActionMenu(bc,drop); 
		
	}

	public void actionPerformed(ActionEvent e)
	{
		String cmd=e.getActionCommand();
		
		dropAction.dropAction=cmd.toString();
			
		// clear interactive: perform real DnD
		dropAction.interactive=false; 
		this.masterBrowser.performDragAndDrop(dropAction); 
		
	}

	
}
