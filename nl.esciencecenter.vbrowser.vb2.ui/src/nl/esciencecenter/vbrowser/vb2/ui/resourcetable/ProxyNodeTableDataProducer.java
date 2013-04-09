package nl.esciencecenter.vbrowser.vb2.ui.resourcetable;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.data.Attribute;
import nl.esciencecenter.vbrowser.vb2.ui.data.AttributeSet;
import nl.esciencecenter.vbrowser.vb2.ui.model.UIViewModel;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNodeDataSource;
import nl.esciencecenter.vbrowser.vb2.ui.resourcetable.ResourceTableModel.RowData;
import nl.esciencecenter.vbrowser.vb2.ui.tasks.UITask;

public class ProxyNodeTableDataProducer implements TableDataProducer 
{
	private static ClassLogger logger;

	public static final String RESOURCE="Resource"; 

	static
	{
		logger=ClassLogger.getLogger(ProxyNodeTableDataProducer.class); 
		logger.setLevelToDebug();
	}

	// ========================================================================
	// Instance 
	// ========================================================================


	private ProxyNodeDataSource dataSource;

	private ResourceTableModel tableModel;

	private UIViewModel uiModel;

	private ViewNode rootNode;

	public ProxyNodeTableDataProducer(ProxyNode pnode,
			ResourceTableModel resourceTableModel) 
	{
		this.dataSource=new ProxyNodeDataSource(pnode.getProxyFactory(),pnode); 
		this.tableModel=resourceTableModel; 
		this.uiModel=UIViewModel.createTableModel();
	}

	public ProxyNodeTableDataProducer(ProxyNodeDataSource dataSource,
			ResourceTableModel resourceTableModel) 
	{
		this.dataSource=dataSource; 
		this.tableModel=resourceTableModel; 
		this.uiModel=UIViewModel.createTableModel();
	}

	public void createTable(boolean headers,boolean data) throws ProxyException
	{
		this.tableModel.setRootViewNode(dataSource.getRoot(uiModel));
		
		if (headers)
			initHeaders();
		
		if (data)
			updateData(); 
	}

	public void initHeaders() throws ProxyException
	{
		if (dataSource==null)
		{
			tableModel.setHeaders(new String[0]); 
			return; 
		}

		// dummy tabel: 
		StringList headers=new StringList();

		
		// Custom Presentation: 
		Presentation pres=null;
		
		try 
		{
			pres = dataSource.getPresentation();
		}
		catch (ProxyException e)
		{
			handle(e,"Couldn't get presentation\n"); 
		}
		
		ViewNode rootNode=getRootViewNode(); 
		
		if (pres==null)
		{
			pres= Presentation.getPresentationForSchemeType(rootNode.getVRI().getScheme(),
					rootNode.getResourceType(),false);
		}

		// set default attributes
		if (pres!=null)
		{
			String[] names;
			names=pres.getChildAttributeNames();

			for (String name:names)
				headers.add(name);
		}
		else
		{
			// ViewNode (ProxyNode) defaults 
			headers.add("Type"); 
//			headers.add(MetaDataConstants.HOSTNAME); 
//			headers.add(MetaDataConstants.PORT);  
//			headers.add(MetaDataConstants.PATH);  
		}

		filterHeaders(headers); 

		tableModel.setHeaders(headers.toArray()); 
		tableModel.setAllHeaders(headers); 
	}

	protected ViewNode getRootViewNode() throws ProxyException 
	{
		if (rootNode==null)	
			rootNode=this.dataSource.getRoot(uiModel); 
		
		return rootNode; 
	}
	
	private VRI getRootVRI() throws ProxyException 
	{
		return getRootViewNode().getVRI(); 
	}

	private void filterHeaders(StringList headers) 
	{
		// Combine "Icon+Name" into "Resource"; 
		headers.insert(0,RESOURCE); 
		// combined into "Resource";  
		headers.remove("Name"); 
		headers.remove("Icon"); 

		for (String name:headers.toArray())
		{
			// MetaAttribute seperators (legacy); 
			if (name.startsWith("["))
				headers.remove(name); 
		}

	}

	public int insertHeader(String headerName, String newName, boolean insertBefore)
	{
		int index=tableModel.insertHeader(headerName, newName, insertBefore); 
		// will update data model, Table View will follow AFTER TableStructureEvent 
		// has been handled. 
		fetchAttribute(newName);
		return index; 
	}

	protected void handle(Throwable t)
	{
		logger.logException(ClassLogger.ERROR,t,"Exception:%s\n",t);  
		t.printStackTrace();
	}

	protected void handle(Throwable t,String format,Object... args)
	{
		logger.logException(ClassLogger.ERROR,t,format,args); 
		t.printStackTrace();
	}

	@Override
	public void updateColumn(String newName) 
	{
		this.fetchAttribute(newName);
	}

	// ========================================================================
	// Background Data Fetchers 
	// ========================================================================

	private void updateData()
	{
		tableModel.clearData();

		// allowed at init time! 
		if (dataSource==null)
			return; 


		UITask task=new UITask(null,"Test get ProxyNode data")
		{
			boolean mustStop=false; 

			public void doTask()
			{
				try
				{
					ViewNode nodes[];

					try
					{
						nodes = getChilds();  
					}
					catch (Exception e)
					{
						handle(e,"Couldn't fetch childs\n"); 
						return; 
					}

					for (ViewNode node:nodes)
					{
						if (mustStop==true)
							return; 

						createRow(node); 
					}

					StringList allAttributes=new StringList(); 

					for (ViewNode node:nodes)
					{
						if (mustStop==true)
							return; 
						try 
						{
							String hdrs[] = tableModel.getHeaders();   
							updateNodeAttributes(node,hdrs);
							
							allAttributes.add(dataSource.getAttributeNames(node.getVRI()),true); 

						}
						catch (ProxyException e)
						{
							handle(e,"Couldn't update node attributes of:"+node); 
						} 
					}

					filterHeaders(allAttributes); 
					tableModel.setAllHeaders(allAttributes);

				}
				catch(Throwable t)
				{
					handle(t,"Failed to fetch table data\n");
				}
			}



			public void stopTask()
			{

			}
		};

		task.startTask();
	}

	public ViewNode[] getChilds() 
	{
		try 
		{
			return dataSource.getChilds(uiModel,getRootVRI(),0,-1,null);
		}
		catch (ProxyException e)
		{
			handle(e,"Couldn't get childs\n"); 
		}
		return null;
	}

	
	protected void fetchAttribute(String attrName)
	{
		fetchAttributes(new String[]{attrName}); 
	}

	private void fetchAttributes(final String[] attrNames)
	{
		if (dataSource==null)
			return; 
		
		final ResourceTableModel model=tableModel; 
		
		UITask task=new UITask(null,"Test get ProxyNode data")
		{
			boolean mustStop=false; 

			public void doTask()
			{
				// 
				// Iterate over current Rows
				// 
				final String[] keys = model.getRowKeys(); 

				for (String rowKey:keys)
				{
					if (mustStop==true)
						return; 
					
					ViewNode node=model.getViewNode(rowKey); 
					
					try 
					{
						if (node!=null)
							updateNodeAttributes(node,attrNames);
					}
					catch (ProxyException e)
					{
						handle(e,"Couldn't update node attributes of:"+node); 
					} 
				}
			}

			public void stopTask()
			{
				this.mustStop=true; 
			}
		};

		task.startTask();
	}


	private void updateNodeAttributes(ViewNode viewNode,String[] attrNames) throws ProxyException 
	{
		Attribute[] attrs;
		
		attrs=dataSource.getAttributes(viewNode.getVRI(),attrNames); 
		
		RowData row=tableModel.getRow(viewNode.getVRI().toString());
		if (row==null)
			return; 

		row.setValues(attrs);

		// Todo Icon Attributes: 

			for (String name:attrNames)
			{
				if (name==RESOURCE) 
				{
					//row.setViewNode(viewNode); 
					row.setValue(name,viewNode); // prefill icon with resource;
					row.setViewNode(viewNode); 
					break; 
				} 
			}
	}

	private void createRow(ViewNode node) throws ProxyException
	{
		AttributeSet set=new AttributeSet();

		int index=tableModel.addRow(node.getVRI().toString(),set);
		RowData row = tableModel.getRow(index); 

		row.setValue(RESOURCE,node); 
	}

	public ProxyNode getRootProxyNode() 
	{
		return this.dataSource.getRootNode(); 
	}

}
