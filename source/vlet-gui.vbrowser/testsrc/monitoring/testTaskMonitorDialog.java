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

package monitoring;

import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.ui.panels.monitoring.TaskMonitorDialog;

public class testTaskMonitorDialog
{
    // === Static === 
    public static ActionTask createTestTask()
    {
        return new ActionTask(null,"StatusPanel background Task")
        {
            public void doTask()
            {
                int max=1000; 
                int dif=10; 
                
                ITaskMonitor monitor = this.getTaskMonitor(); 
                
                monitor.startTask("Main Tester Task",max); 
                                 
                for (int i=0;i<=max;i++)
                {
                    if (this.isCancelled())
                    {
                        monitor.logPrintf("\n***\n***CANCELLED***\n****\n"); 
                        break;  
                    }

                    monitor.logPrintf("<.........[%d]..........>\n",i);
                    
                    String subTaskName="Subtask:"+i;
                    if ((i%10)==0)
                    {
                        monitor.startSubTask(subTaskName,dif);
                    }
                    
                    if ( ((i%100)==0) && (i/100<5)) 
                    {
                        monitor.logPrintf("\n<<<loggingText>>>\n");  
                        // Dummy Monitor! 
                        //monitor.addSubMonitor(new VRSTaskMonitor());
                    }
                    
                    monitor.updateSubTaskDone(subTaskName,i%dif); 
                    
                    // panel.setProgress(i); 
                    monitor.updateTaskDone(i); // panel.setProgress((double)i/1000.0); 
                    
                    try
                    {
                        Thread.sleep(60-(i*50)/max);
                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
                
                monitor.endTask("Main Tester Task"); 
                
            }

            @Override
            public void stopTask()
            {
            }
        };
    }
    
    /**
     * Auto-generated main method to display this JDialog
     */
     public static void main(String[] args)
     {
         ActionTask task=createTestTask(); 
         task.startTask(); 
         TaskMonitorDialog inst = new TaskMonitorDialog(null,task); 
         inst.setDelay(1000);
         inst.start(); 
     }
     
     
    
}
