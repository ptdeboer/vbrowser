package nl.esciencecenter.vbrowser.vb2.ui.iconspanel;

import javax.swing.JPopupMenu;

import nl.esciencecenter.vbrowser.vb2.ui.actionmenu.Action;
import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserInterface;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNodeActionListener;

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
