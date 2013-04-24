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

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNodeContainer;
import nl.esciencecenter.vbrowser.vb2.ui.object.UIDisposable;

/**
 * Managed Tab Panel
 */
public class TabContentPanel extends JPanel
{
	private static final long serialVersionUID = -8240076131848615972L;
	
	private JPanel topPanel;
	private JScrollPane scrollPane;
	private JComponent content;

	private TabNavigationBar tabNavBar;
	
	public TabContentPanel()
	{
		super();
		initGui(); 
	}
	
	protected void initGui()
	{
		
		{
			this.setLayout(new BorderLayout()); 
			
			{
				this.topPanel=new JPanel();
				this.add(topPanel,BorderLayout.NORTH);
				{
					this.tabNavBar=new TabNavigationBar(); 
					topPanel.add(tabNavBar); 
				}
			}
			{
				this.scrollPane=new JScrollPane(); 
				this.add(scrollPane);
			}
		}
	}
	
	public void addActionListener(ActionListener listener)
	{
		this.tabNavBar.addActionListener(listener); 
	}

	public void removeActionListener(ActionListener listener)
	{
		this.tabNavBar.removeActionListener(listener); 
	}

	public void setContent(JComponent comp)
	{
		if (this.content!=null)
		{
			if (content instanceof UIDisposable)
				((UIDisposable)content).dispose(); 
		}
			
		this.scrollPane.setViewportView(comp); 
		this.content=comp;
	}

	public static TabContentPanel createTab(String name, JComponent comp, ActionListener al) 
	{
		TabContentPanel tabP=new TabContentPanel();
		tabP.setContent(comp);
		tabP.setName(name);
		tabP.scrollPane.setName(name);
		tabP.setToolTipText(name); 
		
		tabP.addActionListener(al); 
		
		return tabP;
	}

	public ViewNode getViewNode() 
	{
		if (content instanceof ViewNodeContainer)
		{
			return ((ViewNodeContainer)content).getViewNode();
		}
		
		return null; 
	}
	
	public boolean contains(Class<? extends JComponent> componentClass)
	{
		return componentClass.isInstance(content); 
	}

	public JComponent getContent() 
	{
		return this.content; 
	}
	
}
