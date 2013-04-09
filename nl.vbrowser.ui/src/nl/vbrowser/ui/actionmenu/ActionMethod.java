package nl.vbrowser.ui.actionmenu;

/**
 * The action method is an enum, but enums can't be extended. 
 * So ActionMethods are global (static) actions which are recognized by the ProxyBrowser and it's classes. 
 * For extended actions: use DynamicAction. 
 * These can be defined by subclasses and plugins. 
 */
public enum ActionMethod 
{
	// UI Actions: Single Click, Double Click. 
	// Right click is not an "direct" action, it triggers the popup menu. 
    SELECTION_ACTION("SelectionAction"),
    DEFAULT_ACTION("DefaultAction"), 
    // Menu actions 
    DELETE("Delete"), 
	PROPERTIES("Properties"),
    CREATE("Create"),
    RENAME("Rename"),
    COPY("Copy"),
    PASTE("Paste"),
	// Navigation actions 
    REFRESH("Refresh"), 
    CREATE_NEW_WINDOW("CreateNewWindow"), 
    OPEN_LOCATION("OpenLocation"), 
    OPEN_IN_NEW_WINDOW("OpenInNewWindow"), 
    // Nav Bar 
    BROWSE_BACK("BrowseBack"),
    BROWSE_FORWARD("BrowseForward"),
    BROWSE_UP("BrowseUp"), 
    VIEW_AS_ICONS("ViewAsIcons"), 
    VIEW_AS_ICON_LIST("ViewAsList"), 
    VIEW_AS_TABLE("ViewAsTable"),
    // Tab Nav
    NEW_TAB("NewTab"),
    OPEN_IN_NEW_TAB("OpenInNewTab"),
    CLOSE_TAB("CloseTab"),
    // Selections
    DELETE_SELECTION("DeleteSelection"),
    COPY_SELECTION("CopySelection")
    ;
    
	// === Instance === 
	
	private String methodName; 
	
	private ActionMethod(String method) 
	{
		this.methodName=method; 
	}
	
	public String getMethodName()
	{
		return this.methodName; 
	}

	public static ActionMethod createFrom(String methodStr) 
	{
	    if (methodStr==null)
	        return null; // null in -> null out.  
	    
	    for (ActionMethod meth:ActionMethod.values())
	    {
	        // check both enum name and method string 
            if (methodStr.equalsIgnoreCase(meth.toString()))
                return meth;
            
            if (methodStr.equals(meth.methodName))
                return meth; 
	    }

		return null;
	}
	
	public String toString()
	{
	    return this.methodName; 
	}

}