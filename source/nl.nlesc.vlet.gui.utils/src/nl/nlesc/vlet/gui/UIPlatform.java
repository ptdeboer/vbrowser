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

package nl.nlesc.vlet.gui;

import nl.nlesc.vlet.gui.GuiSettings.LookAndFeelType;

/** 
 * UI Platform. Typically one instance per application. 
 */
public class UIPlatform 
{
	private static UIPlatform instance; 
	
	public static synchronized UIPlatform getPlatform()
	{
		if (instance==null)
			instance=new UIPlatform();
		
		return instance; 
	}
	
	// ========================================================================
	// Instance 
	// ========================================================================
	
	private BrowserFactory browserFactory=null;
    private WindowRegistry windowRegistry=null;
	private boolean appletMode=false;
	
	// ========================================================================
    // Pre INIT 
    // ========================================================================
	
	protected UIPlatform()
	{
		init(); 
	}
	
	protected void init()
	{
	    windowRegistry=new WindowRegistry(); 
	}
	
	/** Set applet mode. Must be one of the first method to be called. */ 
    public void setAppletMode(boolean val)
    {
       this.appletMode=val; 
    }
    
    public boolean getAppletMode()
    {
        return appletMode; 
    }
    
	public void registerBrowserFactory(BrowserFactory factory)
	{
	    if (browserFactory!=null)
	        throw new Error("registerBrowserFactory(): Sorry can only register one browserFactory per (UI)Platform"); 
	    this.browserFactory=factory;
	}
	
	// ========================================================================
	// Post INIT 
	// ========================================================================
	
	public BrowserFactory getBrowserFactory()
	{
	    return browserFactory; 
	}
	
	public WindowRegistry getWindowRegistry()
	{
	    return windowRegistry; 
	}	
	
	// ========================================================================
	// UI Stuff 
	// ========================================================================
	
	public GuiSettings getGlobalGuiSettings()
	{
	    return GuiSettings.getDefault(); 
	}

    public void switchLookAndFeel(String lafstr)
    {
        switchLookAndFeel(LookAndFeelType.valueOf(lafstr),true); 
    }   

	/** Switch LAF */ 
	public void switchLookAndFeel(LookAndFeelType newtype,boolean permanent)
	{
	    //String oldval=getGlobalGuiSettings().getProperty(GuiPropertyName.GLOBAL_LOOK_AND_FEEL); 
	    GuiSettings.switchLookAndFeelType(newtype); // switch
	    if (permanent)
	        getGlobalGuiSettings().setProperty(GuiPropertyName.GLOBAL_LOOK_AND_FEEL,newtype.toString()); 
	}       
    
	/** Start Custom Look and Feel is defined */ 
	public void startCustomLAF()
    {
        String lafstr=getGlobalGuiSettings().getProperty(GuiPropertyName.GLOBAL_LOOK_AND_FEEL);
        
        if (lafstr!=null)   
            GuiSettings.switchLookAndFeelType(lafstr); 
    }

	
}
