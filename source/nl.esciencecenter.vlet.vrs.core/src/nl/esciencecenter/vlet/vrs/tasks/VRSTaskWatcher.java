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

package nl.esciencecenter.vlet.vrs.tasks;

import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.task.TaskWatcher;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

/**
 * Task watcher for threaded VRS tasks. 
 * Use VRS.getTaskWatcher() for the default VRS Task Watchers.
 */ 
public class VRSTaskWatcher extends TaskWatcher 
{
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(VRSTaskWatcher.class); 
    }
    
    // ===
    // Instance  
    // ===

    
    public VRSTaskWatcher(String idStr)
    {
        super(idStr);
    }
    
    public void notifyTaskException(ActionTask task,Throwable e)
    {
        // Source ? 
        logger.logException(ClassLogger.ERROR,this,e,"Exception:%s\n",e);
    }

    /**
     * Check Action Task Context and get current task monitor or create
     * new one with the specified taskName and amount of work. <br>
     */ 
    public ITaskMonitor getCurrentThreadTaskMonitor(String taskName, long todo)
    {
        // check if executed during action task:
        ActionTask task = getCurrentThreadActionTask();
        ITaskMonitor monitor = null;

        if (task != null)
        {
            monitor = task.getTaskMonitor();
        }

        if (monitor == null)
        {
            monitor = new VRSTaskMonitor();
            monitor.startTask(taskName, todo);
        }

        return monitor;
    }
    
    public void notifyTaskStarted(ActionTask actionTask)
    {
        super.notifyTaskStarted(actionTask);
    }

}
