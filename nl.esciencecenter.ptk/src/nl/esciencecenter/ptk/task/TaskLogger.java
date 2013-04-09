/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.task;

import java.util.logging.Level;

import nl.esciencecenter.ptk.util.logging.FormattingLogger;
import nl.esciencecenter.ptk.util.logging.RecordingLogHandler;

/** 
 * Custom logger class for the TaskMonitor.
 * Use java.logging compatible (sub)class. 
 */
public class TaskLogger extends FormattingLogger
{
    private RecordingLogHandler handler;
    
    private Level defaultLevel=INFO; 
    
    public TaskLogger(String name)
    {
        super(name);
        this.handler=new RecordingLogHandler(); 
        this.addHandler(handler);
        // default level=info, but default logPrintf uses "ALL" anyway: 
        this.setLevel(defaultLevel);  
    } 

    /** Default logPrintf for TaksLogger */ 
    public void logPrintf(String format, Object... args)
    {
        // Default log level for task logger is INFO. 
        log(defaultLevel,format,args); 
    }

    public String getLogText(boolean incremental)
    {
        return handler.getLogText(incremental); 
    }
    
}