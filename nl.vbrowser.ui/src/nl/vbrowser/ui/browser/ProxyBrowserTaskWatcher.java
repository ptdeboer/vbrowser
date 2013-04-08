package nl.vbrowser.ui.browser;

import nl.nlesc.ptk.task.TaskWatcher;

public class ProxyBrowserTaskWatcher extends TaskWatcher
{
	private ProxyBrowser browserController; 
     
	public ProxyBrowserTaskWatcher(ProxyBrowser browser)  
	{
		browserController=browser;
	}

	
    @Override
    public void setHasActiveTasks(boolean active)
    {
        browserController.updateHasActiveTasks(active); 
    }

}
