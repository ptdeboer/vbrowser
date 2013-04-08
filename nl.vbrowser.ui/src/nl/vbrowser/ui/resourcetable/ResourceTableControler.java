package nl.vbrowser.ui.resourcetable;

import nl.vbrowser.ui.browser.BrowserInterface;

public class ResourceTableControler
{

	private ResourceTable table;
	
	private BrowserInterface browserController;

	public ResourceTableControler(ResourceTable resourceTable,
			BrowserInterface browserController) 
	{
		this.table=resourceTable;
		this.browserController=browserController; 
	}

	public void handle(Throwable e) 
	{
		browserController.handleException(e);
		
	}
	
	

}
