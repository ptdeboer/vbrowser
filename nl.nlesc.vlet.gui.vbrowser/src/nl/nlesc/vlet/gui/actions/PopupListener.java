/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: PopupListener.java,v 1.2 2013/01/22 23:58:52 piter Exp $  
 * $Date: 2013/01/22 23:58:52 $
 */ 
// source: 

package nl.nlesc.vlet.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import nl.esciencecenter.ptk.Global;
import nl.nlesc.vlet.gui.vbrowser.BrowserController;
import nl.nlesc.vlet.gui.view.VComponent;

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
