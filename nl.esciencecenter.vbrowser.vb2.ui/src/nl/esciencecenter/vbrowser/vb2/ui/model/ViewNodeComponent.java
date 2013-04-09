package nl.esciencecenter.vbrowser.vb2.ui.model;

/**
 * Interface for any (J)Component which can contain a single ViewNode.
 * Super interface of ViewNodeContainer
 */
public interface ViewNodeComponent
{
    /** UIViewModel */
    UIViewModel getUIViewModel();
    
    /** Returns actual ViewNode or RootNode for Containers */ 
    ViewNode getViewNode(); 

    /** Request focus for this component. Returns true if succesful. */
    boolean requestFocus(boolean value); 
    
    /** Parent of this ViewNodeComponent */ 
    ViewNodeContainer getViewContainer(); 
    
}
