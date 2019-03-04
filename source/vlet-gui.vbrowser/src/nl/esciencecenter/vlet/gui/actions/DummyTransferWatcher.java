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

import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskSource;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vlet.gui.UILogger;

/** 
 * Dummmy TransferWatcher is a dedicated File Transfer watcher
 * It monitors ongoing backgrounded transfers, so the main 
 * gui thread, and other action tasks, are not bothered 
 * by ongoing transfers. 
 * 
 * @author P.T. de Boer
 */
public class DummyTransferWatcher implements ITaskSource
{
    static private DummyTransferWatcher backgroundTransferWatcher=new DummyTransferWatcher(); 
    
    /** Global background task watcher for ALL VBrowser instances ! */ 
    public static DummyTransferWatcher getBackgroundWatcher()
    {
        return backgroundTransferWatcher;
    }

    // === ///
    
    private boolean haveTasks=false;
    
    public DummyTransferWatcher()
    {
    }

    public void setHasTasks(boolean val)
    {
        this.haveTasks=val; 
    }
    
    public boolean getHasTasks()
    {
        return haveTasks;
    }
    public String getID()
    {
        return "VBrowser Transfer Manager";
    }

    public void messagePrintln(String str)
    {
        UILogger.infoPrintf(this,"%s\n",str);
    }

    @Override
    public void notifyTaskException(ActionTask task,Throwable ex)
    {
        UILogger.logException(this,ClassLogger.ERROR,ex,"Exception:%s\n",ex);
    }

    @Override
    public void registerTask(ActionTask actionTask)
    {
    }

    @Override
    public void notifyTaskStarted(ActionTask actionTask)
    {
    }

    @Override
    public void notifyTaskTerminated(ActionTask actionTask)
    {
    }
   
    @Override
    public void unregisterTask(ActionTask actionTask)
    {
    }

    public String getTaskSourceName()
    {
        return "DummyTransferWatcher";  
    }
}
