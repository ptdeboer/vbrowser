package nl.esciencecenter.vbrowser.vb2.ui.browser;

import nl.esciencecenter.vbrowser.vb2.ui.tasks.UITask;

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
