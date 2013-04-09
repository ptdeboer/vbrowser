package nl.esciencecenter.vbrowser.vb2.ui.browser;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import nl.esciencecenter.ptk.ui.object.Disposable;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNodeContainer;

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
			if (content instanceof Disposable)
				((Disposable)content).dispose(); 
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
