package nl.esciencecenter.vbrowser.vb2.ui.resourcetable;

import nl.esciencecenter.vbrowser.vb2.ui.browser.BrowserInterface;

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
