/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package panels;

import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.ui.panels.monitoring.ProgresPanel;

public class TestProgressPanel
{

    /**
     * Auto-generated main method to display this 
     * JPanel inside a new JFrame.
     */
     public static void main(String[] args) 
     {
         JFrame frame = new JFrame();
         final ProgresPanel panel=new ProgresPanel(); 
         frame.getContentPane().add(panel);
         
         ActionTask task=new ActionTask(null,"StatusPanel tester")
         {
             public void doTask()
             {
                 //Presentation pres=Presentation.createDefault(); 

                 panel.setTotal(1000);
                 
                 for (int i=0;i<=1000;i++)
                 {
                     // panel.setProgress(i); 
                     panel.setProgress((double)i/1000.0); 
                     panel.setProgressText(" "+i+" out of:"+1000); 
                     
                     
                     Date dateTime=Presentation.createDate(System.currentTimeMillis());
                     panel.setTimeText(Presentation.relativeTimeString(dateTime)); 
                     
                     try
                     {
                         Thread.sleep(50);
                     }
                     catch (InterruptedException e)
                     {
                         e.printStackTrace();
                     }
                     
                     if (this.isCancelled())
                     {
                         return; 
                     }
                 }

             }

             @Override
             public void stopTask()
             {
                 
             }
         };
         
         task.startTask();
         
         frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
         frame.pack();
         frame.setVisible(true);
         
     }
     
}
