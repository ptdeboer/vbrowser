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

package nl.uva.vlet.tasks;

import nl.nlesc.ptk.task.ActionTask;
import nl.nlesc.ptk.task.ITaskMonitor;
import nl.nlesc.ptk.task.MonitorAdaptor;

/**
 * Default VRS TaskMonitor Adaptor. 
 */
public class VRSTaskMonitor extends MonitorAdaptor 
{
    // Task Registery for VRSTasks:
    private static int instanceCounter = 0;



   

    // =========================================================================
    //
    // =========================================================================

    public TaskStats getCurrentSubTask()
    {
        taskStats = super.getSubTaskStats(super.getCurrentSubTaskName());

        if (taskStats == null)
            taskStats = this.subTaskStats.get("");

        if (taskStats == null)
            taskStats = super.createSubTask("", -1);

        return taskStats;
    }

    //@Override
    public String getSubTask()
    {
        return super.getCurrentSubTaskName();
    }

   // @Override
    public long getSubTaskDone()
    {
        TaskStats stats = getCurrentSubTask();
        if (stats == null)
            return -1;
        return stats.done;
    }

    //@Override
    public long getSubTaskTodo()
    {
        TaskStats stats = getCurrentSubTask();
        if (stats == null)
            return -1;
        return stats.todo;
    }

    //@Override
    public void updateSubTaskDone(long workDone)
    {
        TaskStats stats = getCurrentSubTask();
        if (stats == null)
            return;
        stats.updateDone(workDone);
    }

    //@Override
    public long getSubTaskStartTime()
    {
        TaskStats stats = getCurrentSubTask();
        if (stats == null)
            return -1;
        return stats.startTimeMillies;
    }

    //@Override
    public long getSubTaskDoneLastUpdateTime()
    {
        TaskStats stats = getCurrentSubTask();
        if (stats == null)
            return -1;
        return stats.doneLastUpdateTimeMillies;
    }

    public long getTotalWorkDoneLastUpdateTime()
    {
        TaskStats stats = super.getTaskStats();
        if (stats == null)
            return -1;
        return stats.doneLastUpdateTimeMillies;
    }

    public void updateWorkDone(long i)
    {
        super.updateTaskDone(i);
    }

    public void setTotalWorkTodo(long size)
    {
        this.taskStats.todo = size;
    }

    public void setTotalWorkDone(long size)
    {
        super.updateTaskDone(size);
    }

    public void setSubTaskTodo(long size)
    {
        TaskStats stats = getCurrentSubTask();
        if (stats == null)
            return;
        stats.todo = size;
    }

    public long getTime()
    {
        if (isDone())
        {
            return this.taskStats.stopTimeMillies-this.taskStats.startTimeMillies;
        }
        else
        {
            return System.currentTimeMillis() - this.taskStats.startTimeMillies;
        }
    }
 
}