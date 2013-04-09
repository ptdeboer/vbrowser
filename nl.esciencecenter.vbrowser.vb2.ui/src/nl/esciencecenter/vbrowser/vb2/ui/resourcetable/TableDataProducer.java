package nl.esciencecenter.vbrowser.vb2.ui.resourcetable;

import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;

public interface TableDataProducer 
{
	/** Initialize table */ 
	void createTable(boolean initHeaders,boolean createData) throws ProxyException;

	/** Update (optional new) Column by name */ 
	void updateColumn(String newName) throws ProxyException;

}
