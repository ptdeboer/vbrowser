package nl.vbrowser.ui.iconspanel;

import javax.swing.JPopupMenu;

import nl.vbrowser.ui.actionmenu.Action;
import nl.vbrowser.ui.browser.BrowserInterface;
import nl.vbrowser.ui.model.ViewNode;
import nl.vbrowser.ui.model.ViewNodeActionListener;

public class IconsPanelController implements ViewNodeActionListener
{
	private BrowserInterface browser;
	
	private IconsPanel iconsPanel;

	public IconsPanelController(BrowserInterface browser, IconsPanel iconsPanel) 
	{
		this.browser=browser; 
		this.iconsPanel=iconsPanel;
	}

    @Override
    public void handleNodeActionEvent(ViewNode node, Action action)
    {
        this.browser.handleNodeAction(node,action); 
    }

	

}
