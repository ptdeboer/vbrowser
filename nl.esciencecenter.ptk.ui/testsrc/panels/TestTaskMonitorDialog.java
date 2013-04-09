/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package panels;

import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.ui.panels.monitoring.TaskMonitorDialog;

public class TestTaskMonitorDialog
{

    /**
     * Auto-generated main method to display this 
     * JPanel inside a new JFrame.
     */
     public static void main(String[] args) 
     {
         
         ActionTask task=new ActionTask(null,"TaskMonitorDailog tester")
         {
             public void doTask()
             {
                 
                ITaskMonitor monitor = this.getMonitor(); 
              
                int N=100;
                int M=10; 
                int sleep=100;
                
                monitor.startTask("DailogTest",N); 
                
                for (int i=0;i<N;i++)
                {
                    String subTask="Subtask:"+i; 
                    
                    monitor.startSubTask(subTask, M);
                    monitor.logPrintf("New subtask:%s\n",subTask);
                    
                    for (int j=0;j<M;j++)
                    {
                        
                        try
                        {
                            Thread.sleep(sleep);
                        }
                        catch (InterruptedException e)
                        {
                            e.printStackTrace();
                        }
                        
                        monitor.updateSubTaskDone(subTask, j); 
                        
                        if (this.isCancelled())
                        {
                            monitor.logPrintf("***Cancelled***\n",subTask);
                            return; 
                        }
                    }
                    
                    monitor.endSubTask(subTask); 
                    monitor.updateTaskDone(i); 
                }

                monitor.endTask(null);
                
             }

             @Override
             public void stopTask()
             {
             }
         };

         task.startTask();

         TaskMonitorDialog.showTaskMonitorDialog(null, task, 0);
         
//         
//         frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
//         frame.pack();
//         frame.setVisible(true);
         
     }
}
