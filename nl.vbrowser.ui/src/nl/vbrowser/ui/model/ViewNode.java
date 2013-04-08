package nl.vbrowser.ui.model;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.Icon;

import nl.nlesc.ptk.net.VRI;

/**
 * An ViewNode holds UI stuff like icons and presentation attributes. 
 * This is the UI component which is actually 'viewed'.
 * Multiple ViewNodes can be "viewing" a single resource (ProxyNode). 
 * See ProxyNode for resource attributes. 
 */  
public class ViewNode
{
    public static final String DEFAULT_ICON="defaultIcon"; 
    
    public static final String SELECTED_ICON="defaultIcon"; 
	
	/** Atomic Locator, never changes during the lifetime of this object */
    final VRI locator;
    
    boolean isComposite;
    
    String name;
    
    Map<String,Icon> iconMapping=new Hashtable<String,Icon>();

    private String resourceType; 
    
    protected ViewNode(VRI locator)
    {
        this.locator=locator; 
    }
    
    public ViewNode(VRI locator, Icon icon, String name, boolean isComposite)
    {
        this.locator=locator;
        initIcons(icon); 
        this.name=name;
        this.isComposite=isComposite; 
    }
    
    private void initIcons(Icon defaultIcon)
    {
    	this.iconMapping.clear();
    	this.iconMapping.put(DEFAULT_ICON,defaultIcon); 
    }
    
    public VRI getVRI()
    {
        return locator; 
    }
    
    public boolean isComposite()
    {
        return isComposite; 
    }

    public String getName()
    {
        return name;
    }

    public void setResourceType(String resourceType)
    {
        this.resourceType=resourceType; 
    }

    public Icon getIcon()
    {
        return getIcon(DEFAULT_ICON); 
    }
    
    /** Pre rendered selected icon */ 
    public Icon getSelectedIcon()
    {
        return getIcon(SELECTED_ICON); 
    }

    /** Returns status icon if specified */
    public Icon getIcon(String name)
    {
        return iconMapping.get(name); 
    }
    
    public boolean isBusy()
    {
        return false; // is Busy should be updated using events
    }
    
    public String toString()
    {
    	return "<ViewNode>:"+locator; 
    }

    public String getResourceType()
    {
        return this.resourceType;
    }

    // Status on ViewNode ? 
}
