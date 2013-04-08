package nl.vbrowser.ui.browser;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import nl.vbrowser.ui.actionmenu.ActionMethod;

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
