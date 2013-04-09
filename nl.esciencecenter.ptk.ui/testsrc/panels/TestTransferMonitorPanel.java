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
import nl.esciencecenter.ptk.ui.panels.monitoring.TransferMonitorPanel;

public class TestTransferMonitorPanel
{

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
         
         TransferMonitorPanel inst = new TransferMonitorPanel(transfer);
        
         frame.add(inst); 
         frame.pack(); 
         frame.setVisible(true); 
         
         
         int max=1000*1024; 
         int dif=50*1024;
         int step=1024; 
         
         transfer.startTask("TransferTask",max); 
         transfer.setTotalSources(max/dif); 
         
         for (int i=0;i<=max;i+=step)
         {
             if (transfer.isCancelled())
             {
                 transfer.logPrintf("\n*** CANCELLED ***\n"); 
                 break; 
             }
                
             if ((i%dif)==0)
             {
                 transfer.startSubTask("Transfer #"+i/dif, dif); 
                 transfer.logPrintf("--- New Transfer ---\n -> nr="+i/dif+"\n");  
             }
             
             transfer.updateTaskDone(i);
             transfer.updateSourcesDone(i/dif); 
             transfer.updateSubTaskDone(transfer.getCurrentSubTaskName(), i%dif); 
             // do update self!
             inst.update(); 
             
             try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } 
         }
         
         transfer.endTask(transfer.getTaskName());
         
     }
     
}
