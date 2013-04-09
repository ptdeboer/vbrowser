/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package panels;

import javax.swing.JFrame;

import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.task.TransferMonitor;
import nl.esciencecenter.ptk.ui.panels.monitoring.TransferMonitorDialog;

public class TestTransferMonitorDialog
{
 // === Static === 
    
    /**
     * Auto-generated main method to display this JDialog
     */
     public static void main(String[] args)
     {
         JFrame frame = new JFrame();
         // dimmy:
         TransferMonitor transfer=new TransferMonitor("Transfer", 
                 new VRI("file","host","/source"),
                 new VRI("file","host","/dest"));
         
         TransferMonitorDialog inst = new TransferMonitorDialog(transfer);
         inst.setModal(false); 
         inst.setVisible(true);
         inst.start();
         
         int max=1000*1024; 
         int dif=50*1024;
         int step=1024; 
         
         transfer.startTask("TransferTask",max); 
         transfer.setTotalSources(max/dif); 

         String subTask="?";

         for (int i=0;i<=max;i+=step)
         {
             if (transfer.isCancelled())
             {
                 transfer.logPrintf("\n*** CANCELLED ***\n"); 
                 break; 
             }
                
             if ((i%dif)==0)
             {
                 subTask="Transfer #"+i/dif;
                 transfer.startSubTask(subTask, dif); 
                 transfer.logPrintf("--- New Transfer:%s ---\n -> nr="+i/dif+"\n",subTask);  
             }
             
             transfer.updateTaskDone(i);
             transfer.updateSourcesDone(i/dif); 
             transfer.updateSubTaskDone(subTask,i%dif); 
             
             try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            } 
         }
         
         transfer.endTask(null); 
         
     }
}
