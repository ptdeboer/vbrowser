/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.ui.panels.monitoring;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;

import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskSource;
import nl.esciencecenter.ptk.task.MonitorStats;
import nl.esciencecenter.ptk.task.TaskWatcher;
import nl.esciencecenter.ptk.task.TransferMonitor;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;


public class TransferMonitorDialog extends javax.swing.JDialog implements ActionListener
{
    private static final long serialVersionUID = -8463719389609233817L;
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(TransferMonitorDialog.class);
        logger.setLevelToDebug();
    }
    
    // === --- === //
    
    private JPanel buttonPanel;
    private JButton okButton;
    private TransferMonitorPanel transferPanel;
    private JButton cancelButton;
    private JTextArea logText;
    private TransferMonitor vfsTransferInfo;
    private JScrollPane logScrollPane;
    private long delay;
    
    private boolean suspended=false;
    ActionTask updateTask = null; 
    
    // Not yet: 
    // private ITaskSubTaskMonitor vfsTransferInfo; 
    //private BrowserController browserController;

    Presentation presentation=Presentation.createDefault();
    // new monitor statistics object to move shared 
    // code between VFSTransfer and default Task monitor 
    private MonitorStats monitorStats=null;

    //private BrowserController browserController;  
    
    public TransferMonitorDialog(JFrame frame) 
    { 
        super(frame);
        init();
    }
       
    public TransferMonitorDialog(JFrame frame, TransferMonitor transfer)
    {
        super(frame);
        
        this.vfsTransferInfo=transfer;
        this.monitorStats=new MonitorStats(transfer);
        
        init();
    }
    
    public TransferMonitorDialog(TransferMonitor transfer)
    {
        this.vfsTransferInfo=transfer;
        this.monitorStats=new MonitorStats(transfer);
        //this.browserController=bc; 
        
        init();
    }
    
    private void init()
    {
        //UIPlatform.getPlatform().getWindowRegistry().register(this);
    	this.setLocationRelativeTo(null); 
    	initGUI();
    	// initial update: 
        update(); 
        //
        initUpdateTask();
    }
  
    
    protected void initUpdateTask()
    {
        updateTask=new ActionTask(TaskWatcher.getTaskWatcher(),"TransferMonitorDialog.updateTask") 
        {
            public void doTask() 
            {
                while (vfsTransferInfo.isDone()==false)
                {
                    if (suspended==true)
                    {
                        logger.infoPrintf("Dialog Suspended for: %s\n",vfsTransferInfo.toString()); 
                        return; 
                    }   
                    
                    // if()
                    try
                    {
                       update();
                    
                       // delayed dialog: 
                       if ((monitorStats.getTimeRunning()>delay) && (isVisible()==false)) 
                       {
                           setVisible(true);
                       }
                    
                       if (vfsTransferInfo.isDone())
                       {
                          
                           if (vfsTransferInfo.hasError())
                           {
                               handle("Transfer Exception!",vfsTransferInfo.getException());
                           }
                        
                           okButton.setEnabled(true);
                           cancelButton.setEnabled(false); 
                       }
                    }
                    catch (Throwable t)
                    {
                        // bugs in update(): 
                        logger.logException(ClassLogger.FATAL,t,"Exception during update:%s\n",t); 
                    }
                       
                   try
                   {
                       Thread.sleep(100);// 10fps 
                   }
                   catch (InterruptedException e)
                   {
                       logger.logException(ClassLogger.ERROR,e,"Sleep Interrupted!"); 
                   } 
                }// while()
                
                logger.infoPrintf("Post updateLoop for (done) transfer:%s\n",vfsTransferInfo.toString()); 

                // task done: final update to show statistics when job is done!
                update();
                
            }
    
            @Override
            public void stopTask()
            {
                logger.infoPrintf("stopTask() called for:%s\n",vfsTransferInfo.toString()); 
                // stop THIS task, not the actual Transfer Task ! 
                // (must use start() again to the dialog update!  
                suspended=true; 
            }
        };
        
    }    
  
    protected void handle(String action,Throwable e)
    {
       logger.logException(ClassLogger.ERROR,e,"%s\n",action,e); 
    }

    /** Restart the update task */ 
    public synchronized void start()
    {
        String title="Transfering:"+vfsTransferInfo.getSource()+" to "+vfsTransferInfo.getDestination().getHostname()
                ;//+":"+vfsTransferInfo.getStatus(); 
            
        this.setTitle(title);
            
        suspended=false; 
        updateTask.startTask(); 
    }
    
    
    protected void update()
    {
        logger.debugPrintf(">>> Before Update\n"); 
        
        this.transferPanel.update(); 
        
        logger.debugPrintf("Update total  :'%s'\n",transferPanel.getTotalTimeText());
        logger.debugPrintf("Update subTask:'%s'\n",transferPanel.getSubTimeText());
        
        // only do incremental updates: 
        String newText=vfsTransferInfo.getLogText(true); 
        if (StringUtil.isEmpty(newText)==false)  
        {
            this.logText.append(newText);  
            //this.logText.revalidate(); 
            //this.logScrollPane.revalidate(); // trigger update of scrollbars!
        }
                
        if (this.vfsTransferInfo.isDone())
        {
            this.cancelButton.setEnabled(false); 
            this.okButton.setEnabled(true);
        }
    }
    
    private void initGUI() 
    {
        try 
        {
            BorderLayout thisLayout = new BorderLayout();
            thisLayout.setHgap(8);
            thisLayout.setVgap(8);
            getContentPane().setLayout(thisLayout);
            {
                transferPanel = new TransferMonitorPanel(this.vfsTransferInfo); 
                getContentPane().add(transferPanel, BorderLayout.NORTH);
            }
            {
                logScrollPane = new JScrollPane();
                getContentPane().add(logScrollPane, BorderLayout.CENTER);
                {
                    logText = new JTextArea();
                    logScrollPane.setViewportView(logText);
                    logText.setText("Logger");
                    //logText.setPreferredSize(new java.awt.Dimension(343, 58));
                    logText.setEditable(false);
                    logText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                    //NOT logText.setPreferredSize(new java.awt.Dimension(306, 19));
                }
            }
            {
                buttonPanel = new JPanel();
                getContentPane().add(buttonPanel, BorderLayout.SOUTH);
                {
                    okButton = new JButton();
                    buttonPanel.add(okButton);
                    okButton.setText("OK");
                    okButton.addActionListener(this);
                    okButton.setEnabled(false); 
                }
                {
                    cancelButton = new JButton();
                    buttonPanel.add(cancelButton);
                    cancelButton.setText("Cancel");
                    cancelButton.addActionListener(this);
                }
            }
            
            this.pack();
            this.setSize(new Dimension(600,400)); 
            
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        this.addWindowListener(new DialogCloseListener()); 
    }
    
    public class DialogCloseListener implements WindowListener
    {
        @Override
        public void windowActivated(WindowEvent e) {}
        
        @Override
        public void windowClosed(WindowEvent e) {}

        @Override
        public void windowClosing(WindowEvent e)
        {
            TransferMonitorDialog.this.stop(); 
        }

        @Override
        public void windowDeactivated(WindowEvent e) {}

        @Override
        public void windowDeiconified(WindowEvent e) {}

        @Override
        public void windowIconified(WindowEvent e) {}

        @Override
        public void windowOpened(WindowEvent e) {}
        
    }
   
     
    public static TransferMonitorDialog showTransferDialog(ITaskSource taskSource,TransferMonitor transfer,long delay)
    {
        return showTransferDialog(transfer,delay);
    }
    
    public static TransferMonitorDialog showTransferDialog(TransferMonitor transfer,long delay)
    {
        // for very short transfers do not show the dialog: 
        
        TransferMonitorDialog dialog = new TransferMonitorDialog(transfer);
        dialog.setDelay(delay); 
        
        if (delay<=0)
        {
            dialog.setVisible(true);
            dialog.requestFocus(); 
        }
        
        dialog.start();
        return dialog; 
    }

    private void setDelay(long millis)
    {
        this.delay=millis;
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource()==this.okButton)
        {
            dispose();
        }
        
        if (e.getSource()==this.cancelButton)
        {
        	// stop already initiated
        	if (vfsTransferInfo.isCancelled()==true)
        		cancelButton.setEnabled(false); 
        	
            this.vfsTransferInfo.setIsCancelled();
        }
    }

//    /** return progress information */ 
//    public String getTotalProgressText()
//    {
//        VFSTransfer info=vfsTransferInfo;
//        
//        String progstr="";
//        
//        if (info.getTotalSources()>0) 
//            progstr+="Transfer "+info.getSourcesDone()+" of "+info.getTotalSources(); 
//        
//        String speedStr=sizeString((int)monitorStats.getTotalSpeed())+"B/s"; 
//        String amountStr=sizeString(info.getTotalWorkDone())+"B (of "+sizeString(info.getTotalWorkTodo())+"B)";
//        
//        if (info.isDone())
//        {
//            if (info.hasError())
//            {
//                return "Error!"; 
//            }
//            
//            // Final Times. no progress strings 
//            String finalStr="Done:"+amountStr; 
//            
//            finalStr+=" ("+speedStr+")"; 
//            
//            finalStr+=" in "+presentation.timeString(monitorStats.getTotalDoneTime(),false);
//            
//            return finalStr; 
//        }
//       
//        progstr+=", "+amountStr; 
//        
//        // TransferSpeed ONLY for VFS Transfers !
//        progstr+=" ("+speedStr+")";
//        
//        return progstr;
//    }
    
//    /** return progress information */ 
//    public String getCurrentProgressText()
//    {
//        VFSTransfer info=vfsTransferInfo;
//        
//        String progstr="";
//        
//        if (info.isDone())
//        {
//            return "Done.";
//        }
//        
//        // Print Current transfer info:
//        
//        String doneStr="?"; 
//        String todoStr="?";
//        
//        if (info.getSubTaskTodo()>=0)
//        {
//            todoStr=""+info.getSubTaskTodo();
//        }
//        
//        if (info.getSubTaskDone()>=0)
//        {
//            doneStr=""+info.getSubTaskDone();
//        }
//        
//        progstr= doneStr+"B ("+todoStr+"B)";
//        
//        progstr+=" ("+sizeString((int)this.monitorStats.getSubTaskSpeed())+"B/s)";  
//                 
//        return progstr;
//    }


    private String sizeString(long size)
    {
    	if (size<0) 
    		return "?";
    	
        return presentation.sizeString(size,true,1,1);
    }
    
    /**
     * Stop the update task. The running Thread is stopped. Use start() again to
     * restart the update task */ 
    public synchronized void stop()
    {
        suspended=true;
        
        if (this.updateTask!=null)
        {
            if (this.updateTask.isAlive())  
                this.updateTask.signalTerminate(); 
        }
    }
    
    public void dispose()
    {
        stop();
        this.transferPanel.dispose(); 
        super.dispose(); 
    }
    
}