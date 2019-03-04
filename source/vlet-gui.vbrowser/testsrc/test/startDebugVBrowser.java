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

package test;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.gui.UILogger;
import nl.esciencecenter.vlet.gui.UIPlatform;
import nl.esciencecenter.vlet.gui.dialog.ExceptionForm;
import nl.esciencecenter.vlet.gui.vbrowser.VBrowserFactory;
import nl.esciencecenter.vlet.gui.vbrowser.VBrowserInit;

/**
 * 
 * Simple VBrowser Start Class.
 *  
 * Will be called by the 'StartVBrowser' in bootstrapper.
 *  
 *  
 */

public class startDebugVBrowser
{

  public static void main(String args[])
  {
      try
      {
        args=VletConfig.parseArguments(args); 
        
        ClassLogger.getRootLogger().setLevelToDebug();

        UIPlatform plat = VBrowserInit.initPlatform(); 

        // Option --native ? :
  		//GuiSettings.setNativeLookAndFeel();
        
        // shiny swing metal look:
  		plat.startCustomLAF(); 
  		
        // Filter out property arguments like -Duser=jan
  		 
        // start browser(s)
      	{
            int urls=0; 
            
            for (String arg:args)
            {
                UILogger.debugPrintf(startDebugVBrowser.class,"arg=%s\n",arg);
                
                // assume that every non-option is a VRL:
                
                if (arg.startsWith("-")==false)
                {
                    // urls specified:
                    urls++; 
                    VBrowserFactory.getInstance().createBrowser(arg);
                }
                else
                {
                   if (arg.compareTo("-debug")==0)
                       ClassLogger.getRootLogger().setLevelToDebug(); 
                }
            }
            
            // no urls specified, open default window:
            if (urls==0) 
            {
                // get home LOCATION: Can also be gftp/srb/....
                // BrowserController.performNewWindow(TermGlobal.getUserHomeLocation());
                
                VBrowserFactory.getInstance().createBrowser((UIGlobal.getVRSContext().getVirtualRootLocation())); 
            }
 
      	}
      	
    }
    catch (Exception e)
    {
        UILogger.logException(startDebugVBrowser.class,ClassLogger.FATAL,e,"Exception:%s\n",e);  
        ExceptionForm.show(e); 
         
    }

  }

}

  