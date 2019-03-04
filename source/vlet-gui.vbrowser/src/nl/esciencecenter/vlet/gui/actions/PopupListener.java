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

package nl.esciencecenter.vlet.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nl.esciencecenter.vlet.gui.vbrowser.BrowserController;
import nl.esciencecenter.vlet.gui.view.VComponent;

/**
 * 
 * PopupListener 
 * 
 * @author P.T. de Boer
 */

public class PopupListener implements ActionListener
{
    BrowserController browserController=null;
    VComponent vcomp=null; 
    
        
    public PopupListener(BrowserController bc,VComponent comp)
    {
        this.browserController=bc;     
        this.vcomp=comp;     
    }
    
    public void actionPerformed(ActionEvent e) 
    {
        //Global.debugPrintf(this,"PopupListener Action:%s\n",e);
        //Global.debugPrintf(this,"PopupListener node:%s\n",vcomp);
        
        String cmdstr=e.getActionCommand();
        ActionCommand cmd=ActionCommand.fromString(cmdstr);
        
        // Redirect action to browsercontroller !!! 
       	browserController.performAction(vcomp,cmd);
    }
    
    /*public void handle(VlException e)
    {
       TermGlobal.errorPrintln("PopupListener","---Exception---");
       e.printStackTrace(TermGlobal.getErrorStream()); 
    }*/

}
