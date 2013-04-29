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

import javax.swing.JPopupMenu;

import nl.esciencecenter.ptk.task.ITaskSource;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.nlesc.vlet.gui.dnd.DropAction;
import nl.nlesc.vlet.gui.view.VComponent;
import nl.nlesc.vlet.gui.view.ViewModel;
import nl.nlesc.vlet.vrs.vrl.VRL;


/** 
 * Combined Interface for MasterBrowser and ITaskSource.  
 * 
 * This is mainly an interface for the BrowserController,
 * but created to shield other components from dependency
 * of the BrowserController. 
 * Other mini browsers might implement this interface like the FileSelector.
 * 
 * @author P.T. de Boer
 */
public interface MasterBrowser extends HyperLinkListener, ITaskSource
{
	/** Get current viewfilter */ 
    ViewModel getViewModel();

    /** Notify Drag'n Drop action */ 
    void performDragAndDrop(DropAction action);

    /** Get action menu for specified location */ 
    JPopupMenu getActionMenuFor(VComponent comp);

    /** Selection Event, if resource==null this means an UNSELECT! */ 
    void performSelection(VComponent comp);
    
    /**
     * Perform default Action (user double click's on resource) 
     * What the default actions is, depends on the Selected Resource. 
     * 
     */ 
    void performAction(VComponent comp);
    
    /**
     * Start New (Master) Browser Window if TAB mode is ON
     * the new window will appear as new Tab. 
     */ 
    void startNewWindow(VRL vrl);
   
}
