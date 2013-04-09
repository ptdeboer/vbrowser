package nl.esciencecenter.vbrowser.vb2.ui.iconspanel;

import nl.esciencecenter.vbrowser.vb2.ui.model.DataSource;
import nl.esciencecenter.vbrowser.vb2.ui.model.UIViewModel;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNodeEvent;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNodeEventListener;

public class IconsPanelUpdater implements ProxyNodeEventListener 
{
	private DataSource dataSource;
	
	private IconsPanel iconsPanel;

	private ViewNode rootNode;

	public IconsPanelUpdater(IconsPanel panel,DataSource dataSource) 
	{
		this.iconsPanel=panel; 
		this.dataSource=dataSource;
	    setDataSource(dataSource,true); 
	}
	

	public ViewNode getRootNode()
	{
		return rootNode; 
	}
	
	public UIViewModel getUIModel()
	{
		return this.iconsPanel.getUIViewModel(); 		
	}
	
	public void setDataSource(DataSource dataSource,boolean update)
	{
		// unregister
		if (this.dataSource!=null)
			this.dataSource.removeDataSourceListener(this); 
		
		this.dataSource=dataSource; 
		
		//register
		if (this.dataSource!=null)
			this.dataSource.addDataSourceListener(this); 
		
		if ((update) && (dataSource!=null))
			updateRoot(); 
	}

	@Override
	public void notifyDataSourceEvent(ProxyNodeEvent e) 
	{
		System.err.println("FIXME: DataSourceEvent:"+e); 
	}

	
	protected void updateRoot()
	{
		try
		{
			this.rootNode=this.dataSource.getRoot(getUIModel()); 
			ViewNode[] childs = this.dataSource.getChilds(getUIModel(), rootNode.getVRI(),0,-1,null); 
			updateChilds(childs); 
		}
		catch (ProxyException e)
		{
			handle("Updating root location.",e); 
		}
	}

	private void updateChilds(ViewNode[] childs) 
	{
		this.iconsPanel.getModel().setChilds(createIconItems(childs));   
	}

	private IconItem[] createIconItems(ViewNode[] nodes)
	{
		if (nodes==null)
			return null; 
		
		int len=nodes.length; 
		
		IconItem items[]=new IconItem[len];
		
		for (int i=0;i<len;i++)
		{
			items[i]=createIconItem(nodes[i]); 
		}
		
		return items; 
	}

	protected IconItem createIconItem(ViewNode node)
    {
	    IconItem item=new IconItem(iconsPanel,getUIModel(),node);
	    item.initDND(iconsPanel.getPlatform().getTransferHandler(),iconsPanel.getDragGestureListener());
	    return item;
    }

    private void handle(String string, ProxyException e)
	{
    	this.iconsPanel.getMasterBrowser().handleException(e);
		
	}

	public DataSource getDataSource() 
	{
		return this.dataSource; 
	}
}
