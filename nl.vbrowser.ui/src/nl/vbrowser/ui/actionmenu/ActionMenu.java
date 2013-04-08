package nl.vbrowser.ui.actionmenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;


import nl.nlesc.ptk.net.VRI;
import nl.vbrowser.ui.model.ViewNode;
import nl.vbrowser.ui.model.ViewNodeContainer;

public class ActionMenu extends JPopupMenu
{ 
	private static final long serialVersionUID = 7948518745148426493L;

	public static ActionMenu createSimpleMenu(ActionMenuListener actionListener,
			ViewNodeContainer container, 
			ViewNode viewNode, 
			boolean canvasMenu)
	{
		
		VRI locator = viewNode.getVRI(); 
		ActionMenu menu=new ActionMenu(viewNode,actionListener); 

    	ViewNode[] selected=null;
    	if (container!=null)
    		selected= container.getNodeSelection();
    	
    	boolean hasSelection=((selected!=null) && (selected.length>0)); 
    	boolean multiSelection=((selected!=null) && (selected.length>1)); 
		
		if (canvasMenu)
		{
			JMenuItem menuItem=new JMenuItem("CanvasMenu");
			menu.add(menuItem);
			menuItem.setEnabled(false); 
			JSeparator sep=new JSeparator(); 
			menu.add(sep); 
		}
		else
		{
			JMenuItem menuItem=new JMenuItem("NodeMenu:"+viewNode.getName());
			menu.add(menuItem); 
			menuItem.setEnabled(false); 
			
			JSeparator sep=new JSeparator(); 
			menu.add(sep); 
		}
		
		JSeparator sep=new JSeparator(); 
		menu.add(sep); 
		menu.add(menu.createItem(viewNode,"Open",ActionMethod.OPEN_LOCATION)); 
		menu.add(sep); 
        menu.add(menu.createItem(viewNode,"Create",ActionMethod.CREATE)); 
        if (multiSelection)
        	menu.add(menu.createItem(viewNode,"Delete All",ActionMethod.DELETE_SELECTION));
        else 
        	menu.add(menu.createItem(viewNode,"Delete",ActionMethod.DELETE));
        
        menu.add(menu.createItem(viewNode,"Rename",ActionMethod.RENAME));
        
        sep=new JSeparator();
    	menu.add(sep);
    	

   		JMenuItem item;
   		String name="Copy"; 
   		if (multiSelection)
   			name="Copy All"; 
   		
		menu.add(item=menu.createItem(viewNode,name,ActionMethod.COPY_SELECTION)); 

		// enable copy in cancas menu only when there is something selected
		if (canvasMenu)
			if (hasSelection) 
				item.setEnabled(true);
			else
				item.setEnabled(false); 
    	
        menu.add(menu.createItem(viewNode,"Paste",ActionMethod.PASTE)); 
        sep=new JSeparator();
    	menu.add(sep); 
		menu.add(menu.createItem(viewNode,"Refresh",ActionMethod.REFRESH)); 
		
		{
			sep=new JSeparator(); 
			menu.add(sep); 

			JMenu subMenu=new JMenu("Location"); 
			{
				JMenuItem menuItem=new JMenuItem(locator.toString()); 
				subMenu.add(menuItem); 
				menu.add(subMenu); 
			}
			{
				menu.add(menu.createItem(viewNode,"Properties",ActionMethod.PROPERTIES)); 
			}
		}

	
		return menu; 

	}
	
	/** Translates pop action events to MenuActions */ 
	public class PopupHandler implements ActionListener
	{
		private ActionMenuListener menuActionListener;
        private ViewNode viewNode;

		public PopupHandler(ViewNode viewNode, ActionMenuListener listener) 
		{
			this.menuActionListener=listener; 
			this.viewNode=viewNode;
		}

		@Override
		public void actionPerformed(ActionEvent e) 
		{
			String cmdStr=e.getActionCommand(); 
			Action theAction=Action.createFrom(viewNode,cmdStr); 
			menuActionListener.handleMenuAction(theAction); 

		}
	}
	
	// ========================================================================
	// Instance 
	// ========================================================================
	
	PopupHandler popupHandler; 
	
	public ActionMenu(ViewNode viewNode, ActionMenuListener actionListener)
	{
		init(viewNode,actionListener); 
	}
	
	public void init(ViewNode viewNode,ActionMenuListener actionListener)
	{
		this.popupHandler=new PopupHandler(viewNode,actionListener); 
	}
	
	protected JMenuItem createItem(ViewNode viewNode,String name,ActionMethod actionMeth)
	{
		 JMenuItem mitem = new JMenuItem();
         mitem.setText(name);  
         mitem.setActionCommand(new Action(viewNode,actionMeth).toString()); 
         mitem.addActionListener(this.popupHandler);
         
         return mitem;
	}
	
}
