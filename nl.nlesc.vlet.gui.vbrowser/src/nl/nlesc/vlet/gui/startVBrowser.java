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

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.gui.GuiSettings;
import nl.nlesc.vlet.gui.UIGlobal;
import nl.nlesc.vlet.gui.UILogger;
import nl.nlesc.vlet.gui.dialog.ExceptionForm;
import nl.nlesc.vlet.gui.vbrowser.BrowserController;
import nl.nlesc.vlet.gui.vbrowser.VBrowserFactory;
import nl.nlesc.vlet.gui.vbrowser.VBrowserInit;
import nl.nlesc.vlet.gui.viewers.ViewerPlugin;
import nl.nlesc.vlet.gui.viewers.ViewerRegistry;
import nl.nlesc.vlet.vrl.VRL;

/**
 * VBrowser Start Class.
 */
public class startVBrowser
{
  /** 
   * Main  
   */ 
  public static void main(String args[]) 
  {
      try
      {
          VletConfig.parseArguments(args); 

          // does platform init!
          VBrowserInit.initPlatform();
          VBrowserFactory factory=VBrowserFactory.getInstance(); 
          
        // start browser(s)
      	{
            int urls=0; 
            
            if (args!=null) for (String arg:args)
            {
                UILogger.debugPrintf(startVBrowser.class,"arg=%s\n",arg);
                
                // assume that every non-option is a VRL:
                
                if (arg.startsWith("-")==false)
                {
                    // urls specfied:
                    urls++; 
                    factory.createBrowser(arg);
                }
                else
                {
                  if (arg.compareTo("-noblock")==0)
                   {
                       BrowserController.setDummyMode(false);
                   }
                   else if (arg.compareTo("-notree")==0)
                   {
                	   GuiSettings.setShowResourceTree(false); 
                   }
                }
            }
            
            // no urls specified, open default window:
            if (urls==0) 
            {
                // get home LOCATION: Can also be gftp/srb/....
                // BrowserController.performNewWindow(TermGlobal.getUserHomeLocation());
                factory.createBrowser(UIGlobal.getVirtualRootLocation());
            }
      	}
    }
    catch (VlException e)
    {
        UILogger.logException(null,ClassLogger.ERROR,e,"***Error: Exception:%s\n",e); 
        ExceptionForm.show(e); 
         
    }
  }
  
  /** Create VBrowser instance */ 
  public static BrowserController createVBrowser(VRL vrl)
  { 
 	 return VBrowserFactory.getInstance().createBrowser(vrl); 
  }
  
  /** Register viewer */ 
  public static void registerViewer(String clazzname)
  {
	  ViewerRegistry.getRegistry().registerViewer(clazzname); 
  }
  
  /** Register viewer class */ 
  public static void registerViewer(Class<? extends ViewerPlugin> clazz)
  {
	  ViewerRegistry.getRegistry().registerViewer(clazz); 
  }
  

}

  