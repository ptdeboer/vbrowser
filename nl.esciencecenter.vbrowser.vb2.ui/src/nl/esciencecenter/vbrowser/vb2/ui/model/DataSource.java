package nl.esciencecenter.vbrowser.vb2.ui.model;

import nl.esciencecenter.ptk.data.LongHolder;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNodeEventListener;


/**
 * Data Source for ViewNodes. 
 */ 
public interface DataSource
{
	/** 
	 * Register listener to receive data source update events.
	 * Listeners received events about created ViewNodes
	 */ 
	void addDataSourceListener(ProxyNodeEventListener listener); 
	
	void removeDataSourceListener(ProxyNodeEventListener listener); 
	
	/** 
	 * Toplevel resource or root node. 
	 * @throws ProxyException 
	 */ 
	ViewNode getRoot(UIViewModel uiModel) throws ProxyException;  // throws ProxyException;

	/** 
	 * Get childs of specified resource. 
	 * @param uiModel - the UIModel 
	 * @param locator - location of resource
	 * @param offset - get childs starting from this offset 
	 * @param range - maximum number of childs wanted. Use -1 for all. 
	 */ 
	ViewNode[] getChilds(UIViewModel uiModel,VRI locator,int offset, int range,LongHolder numChildsLeft) throws ProxyException;
	
	/** 
	 * Open locations and create ViewNodes.
	 *  
	 * @param uiModel - the UIModel to use
	 * @param locations - resource locations 
	 * @return created ViewNodes
	 */
	ViewNode[] getNodes(UIViewModel uiModel,VRI locations[]) throws ProxyException; 
 
	
}
