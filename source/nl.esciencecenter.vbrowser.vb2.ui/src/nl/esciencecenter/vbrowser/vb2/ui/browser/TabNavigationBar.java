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

package nl.esciencecenter.vbrowser.vb2.ui.browser;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import nl.esciencecenter.vbrowser.vb2.ui.actionmenu.ActionMethod;

public class TabNavigationBar extends JPanel
{
	public static final String NEW_TAB_ACTION="newTab";
	
	public static final String CLOSE_TAB_ACTION="closeTab";
	
	private static final long serialVersionUID = 9083254464933216390L;
	private JButton closeBut;

	private JButton cloneBut;

	public TabNavigationBar()
	{
		initGUI(); 
	}

	private void initGUI() 
	{
		this.setLayout(new FlowLayout()); 
		{
			closeBut = new JButton("x"); 
			closeBut.setSize(16,16); 
			this.add(closeBut); 
			closeBut.setActionCommand(ActionMethod.CLOSE_TAB.toString());
			
			cloneBut = new JButton("+"); 
			cloneBut.setSize(16,16); 
			this.add(cloneBut); 
			cloneBut.setActionCommand(ActionMethod.NEW_TAB.toString());
		}
	}

	public void addActionListener(ActionListener listener) 
	{	
		this.closeBut.addActionListener(listener); 
		this.cloneBut.addActionListener(listener); 
	}
	

	public void removeActionListener(ActionListener listener) 
	{	
		this.closeBut.removeActionListener(listener); 
		this.cloneBut.removeActionListener(listener); 
	}

}
