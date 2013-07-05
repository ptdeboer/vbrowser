/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package nl.esciencecenter.vlet.gui;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.gui.GuiSettings;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.gui.UILogger;
import nl.esciencecenter.vlet.gui.dialog.ExceptionForm;
import nl.esciencecenter.vlet.gui.vbrowser.BrowserController;
import nl.esciencecenter.vlet.gui.vbrowser.VBrowserFactory;
import nl.esciencecenter.vlet.gui.vbrowser.VBrowserInit;
import nl.esciencecenter.vlet.gui.viewers.ViewerPlugin;
import nl.esciencecenter.vlet.gui.viewers.ViewerRegistry;

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
    catch (VrsException e)
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

  