package nl.esciencecenter.vbrowser.vb2.ui.model;

import java.awt.Point;

import javax.swing.JPopupMenu;

/**
 * Interface for any (J)Component which can contain ViewNodes.
 * A ViewNodeContainer in itself is also a ViewNodeComponent. 
 */
public interface ViewNodeContainer  extends ViewNodeComponent
{
	ViewNode getNodeUnderPoint(Point p);

	// === Menu === // 
    JPopupMenu createNodeActionMenuFor(ViewNode node, boolean canvasMenu);
    
	// === Selection Model === 
	void clearNodeSelection();

	ViewNode[] getNodeSelection();

	/** Toggle selection */ 
	void setNodeSelection(ViewNode node, boolean isSelected);
	
	/** Toggle selection of range */ 
    void setNodeSelectionRange(ViewNode firstNode, ViewNode lastNode,boolean isSelected);

    /** Request focus for child. Return true if it has focus. */ 
    boolean requestNodeFocus(ViewNode node, boolean value);
	
}
