package nl.vbrowser.ui.resourcetable;

import nl.vbrowser.ui.proxy.ProxyException;

public interface TableDataProducer 
{
	/** Initialize table */ 
	void createTable(boolean initHeaders,boolean createData) throws ProxyException;

	/** Update (optional new) Column by name */ 
	void updateColumn(String newName) throws ProxyException;

}
