package nl.esciencecenter.vbrowser.vb2.ui.browser;

import nl.esciencecenter.ptk.task.TaskWatcher;

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
