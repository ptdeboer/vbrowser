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
 * $Id: ViewerWatcher.java,v 1.5 2013/01/24 13:39:24 piter Exp $  
 * $Date: 2013/01/24 13:39:24 $
 */ 
// source: 

package nl.uva.vlet.gui.viewers;

import nl.nlesc.ptk.task.ActionTask;
import nl.nlesc.ptk.task.TaskWatcher;
import nl.nlesc.ptk.util.logging.ClassLogger;
import nl.uva.vlet.gui.UILogger;


/** Viewer watcher for external started viewers */ 
public class ViewerWatcher extends TaskWatcher 
{
    boolean isStandAlone=false;  
    
    public ViewerWatcher()
    {
    }
    
    //@Override
    public String getID()
    {
        return "ViewerWatcher"; 
    }

    @Override
    public void notifyTaskException(ActionTask task,Throwable e)
    {
        UILogger.logException(this,ClassLogger.ERROR,e,"Exception\n"); 
    }
   
    //@Override
    public void setHasTasks(boolean val)
    {
        // if val==false, all viewers have stopped; 
        // Global.infoPrintln(this,"has Tasks:"+val); 
    }

    
}
