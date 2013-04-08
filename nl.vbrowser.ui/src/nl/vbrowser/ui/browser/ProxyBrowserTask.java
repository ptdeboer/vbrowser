package nl.vbrowser.ui.browser;

import nl.vbrowser.ui.tasks.UITask;

public abstract class ProxyBrowserTask extends UITask 
{
	private ProxyBrowser browserController=null;

    public ProxyBrowserTask(ProxyBrowser browserController,String taskName) 
	{
		super(browserController.getTaskWatcher(),taskName);
		this.browserController=browserController; 
	}
	
	@Override
	protected void stopTask() throws Exception
	{
	    browserController.messagePrintf(this,"StopTask NOT implemented for:%s\n",this); 
	}

}
