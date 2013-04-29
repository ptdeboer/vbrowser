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

package nl.nlesc.vlet.vrs.vfs;

import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.task.TransferMonitor;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.vlet.vrs.data.VAttribute;
import nl.nlesc.vlet.vrs.vrl.VRL;

/**
 * VFSTransfer class. Keeps record of ongoing transfers. 
 * VFSS classes should update this transfer information when 
 * given as an argument when requested to perform an (file) transfer. 
 * <p>
 * The minimum use is: <pre> 
 *   startTask("Task") // Start Stransfer <br> 
 *   // dostuff...<br> 
 *   endTask()  // End Transfer <br>
 * </pre>
 * It is recommend to set and update the current transfer size
 * to allow transfer statistics to be calculated. <br>
 * Update the transfer info as follows:<pre>
 *   startTask("Task") // Start Stransfer <br> 
 *   setTotalWorkTodo(fileSize)
 *   while(transferDone==false)
 *   {
 *   	// do transfer ..<br>
 *      updateWorkDone(bytesTransferred)
 *   }
 *   endTask() // End Transfer <br>
 * </pre>
 * 
 * @author P.T. de Boer
 */
public class VFSTransfer extends TransferMonitor
{    
    // ========================================================================
    // instance
    // ========================================================================

    // VFS Transfer Fields 
    private String resourceType="";
    
    /** Whether this transfer is move */  
    private final boolean isMove;
  
    private boolean multiTransfer=false;

    private VFSActionType actionType=VFSActionType.UNKNOWN; 
  
    // not source but current (sub)resource; 
    private VRL currentSource; 
    
    // instance methods
    public VFSTransfer(ITaskMonitor parentMonitor, String resourceType,VRL source, VRL destination,boolean isMove)
    {
        super("VFSTransfer", source.toURINoException(), destination.toURINoException());
        
        setParent(parentMonitor); // add this transfer to parent monitor 
        this.resourceType=resourceType; 
        this.isMove=isMove; 
        this.setTotalSources(-1);  
    }

    /** Specify what kinf of action this transfer is */ 
    public void setVFSTransferType(VFSActionType type)
    {
        this.actionType=type; 
        
    }
    
    // =======================================================================
    // VFSTransfer methods 
    // =======================================================================
    
    public void printReport(ClassLogger logger)
    {
        logger.debugPrintf("--- VFSTransfer report ---");
        logger.debugPrintf(" transfer ID   =%s\n",this.getID());
        logger.debugPrintf(" source        =%s\n",this.getSource());
        logger.debugPrintf(" destination   =%s\n",this.getDestination()); 
        logger.debugPrintf(" type          =%s\n",this.actionType); 
        logger.debugPrintf(" is move       =%s\n",StringUtil.boolString(this.isMove));  
        logger.debugPrintf(" transfertime  =%f(s)\n",(double)this.getTime()/1000.0); 
        logger.debugPrintf(" transfer size =%d\n",+getSubTaskTodo()); 
        logger.debugPrintf(" Exception     =%s\n",(this.getException()==null?getException():"no")); 
    }
      
    public VAttribute[] getAttributes()
    {
        VAttribute attrs[]= 
          {
             new VAttribute("transferID",getID()),  
             new VAttribute("type",resourceType),  
             new VAttribute("method",(isMove?"Move":"Copy")),
             new VAttribute("source",getSource()),  
             new VAttribute("destination",getDestination()),  
             new VAttribute("done",isDone()),  
             new VAttribute("exception",getException().toString())  
          };
        
        return attrs; 
    }
   
    public String toString()
    {
        return "transferID  ="+this.getID()+"\n"
                +"resourceType="+this.resourceType+"\n"
                +"method      ="+(isMove?"move":"copy")+"\n"
                +"source      ="+this.getSource()+"\n"
                +"destination ="+this.getDestination()+"\n"
                +"exception   ="+(this.getException()==null?"none":getException().getClass().getName());
    }
           
    /**
     * @return Returns the current source
     */
    public VRL getCurrentSource()
    {
        return currentSource; 
    }
    
    public void setCurrentSource(VRL vrl)
    {
        currentSource=vrl;
    }
  
    /**
     * @return Returns the isMove.
     */
    public boolean isMove()
    {
        return isMove;
    }
    
    /** Return current transfer speed string in KB/s */
    public String getCurrentSpeedString()
    {
        String speedstr="";
        
        // only print current transfer is have it:
        
        if (getSubTaskDone()<=0) 
            speedstr+="";
        else
            speedstr+=getSubTaskDone()/(getSubTaskDoneLastUpdateTime()-getStartTime()+1)+"/";
        
        if (getTotalWorkDone()<=0) 
            speedstr+="(?)KB/s";
        else
            speedstr+=getTotalWorkDone()/(getTotalWorkDoneLastUpdateTime()-getStartTime()+1)+"KB/s"; 
        
                
        return speedstr; 
    }
   
    public void endTask(String taskName)
    {
        //Global.warnPrintf(this,"Ending VRS Transfer:%s\n",taskName); 
        
        super.endTask(taskName); 
        
        // bug: after recursive directory copy: the stats 
        // might be wrong 
        if (this.getTotalWorkTodo()<=0)
        {
            // match done with todo:
            long size=this.getTotalWorkDone(); 
            this.setTotalWorkTodo(size);
            this.setTotalWorkDone(size);
        }
    }

    public void setMultiTransfer(boolean value)
    {
        this.multiTransfer=value; 
    }
    
    public boolean isMultiTransfer()
    {
        return multiTransfer;  
    }

    /** Return information about the current source */ 
    public String getSourceText()
    {
        String source="?";
        
        if (isMultiTransfer())
            source="(Multi) "; 
        else
            source="";
         
        if (this.currentSource!=null)
            source+=currentSource;    
       
        return source; 
    }

    public void addSourcesDone(int i)
    {
        this.updateSourcesDone(getSourcesDone()+i);  
    }   
    
    // ----------------------
    // Legacy Methods: Todo
    // -----------------------
    
    public void updateSubTaskDone(long numTransferred)
    {
        super.updateSubTaskDone(this.getCurrentSubTaskName(), numTransferred);
    }

    public void setTotalWorkTodo(long i)
    {
        this.taskStats.todo=i; 
    }

    public void updateWorkDone(long i)
    {
        super.updateTaskDone(i);
    }
    
    public void setTotalWorkDone(long size)
    {
        super.updateTaskDone(size);
    }
        
    public TaskStats getCurrentSubTask()
    {
        taskStats = super.getSubTaskStats(super.getCurrentSubTaskName());

        if (taskStats == null)
            taskStats = this.subTaskStats.get("");

        if (taskStats == null)
            taskStats = super.createSubTask("", -1);

        return taskStats;
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
    
    private long getSubTaskTodo()
    {
        return this.getCurrentSubTask().todo; 
    }
    
    private long getTotalWorkDoneLastUpdateTime()
    {
        return this.taskStats.doneLastUpdateTimeMillies;
    }


    private long getSubTaskDoneLastUpdateTime()
    {
        return this.getCurrentSubTask().doneLastUpdateTimeMillies; 
    }

    private long getSubTaskDone()
    {
        return this.getCurrentSubTask().done; 
    }
  
}
