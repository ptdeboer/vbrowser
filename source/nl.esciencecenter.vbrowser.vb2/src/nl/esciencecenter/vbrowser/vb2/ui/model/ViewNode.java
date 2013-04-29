/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.esciencecenter.vbrowser.vb2.ui.model;

import java.util.Hashtable;
import java.util.Map;

import javax.swing.Icon;

import nl.esciencecenter.vbrowser.vrs.net.VRL;

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
    final VRL locator;
    
    boolean isComposite;
    
    String name;
    
    Map<String,Icon> iconMapping=new Hashtable<String,Icon>();

    private String resourceType; 
    
    protected ViewNode(VRL locator)
    {
        this.locator=locator; 
    }
    
    public ViewNode(VRL locator, Icon icon, String name, boolean isComposite)
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
    
    public VRL getVRI()
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
