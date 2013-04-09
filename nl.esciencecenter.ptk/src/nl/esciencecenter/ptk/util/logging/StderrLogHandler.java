/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.util.logging;

import java.io.PrintStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Handler for stderr output.  
 */ 
public class StderrLogHandler extends Handler
{
    PrintStream stderrStream=null; 
    
    public StderrLogHandler(PrintStream err)
    {
        stderrStream=err; 
    }

    @Override
    public void close() throws SecurityException
    {

    }

    @Override
    public void flush()
    {
        stderrStream.flush();
    }

    @Override
    public void publish(LogRecord record)
    {
        Level level = record.getLevel();
        String name=record.getLoggerName(); 
        String format=record.getMessage();
        Object args[]=record.getParameters();
        
        String lvlStr; 
        
        // Convert some Level to more appropriate names: 
        if (level==Level.FINE)
            lvlStr="DEBUG"; 
        else if (level==Level.FINER)
            lvlStr="DEBUG2"; 
        else if (level==Level.FINEST)
            lvlStr="DEBUG3"; 
        else if (level==Level.WARNING)
            lvlStr="WARN "; 
        else if (level==Level.SEVERE)
            lvlStr="ERROR"; 
        else 
            lvlStr=level.toString(); 
        
        // has message ?  
        if (format!=null)
            stderrStream.printf(lvlStr+":"+name+":"+format,args);
        
        /// print stacktrace of Exception 
        Throwable t=record.getThrown();
        if (t!=null)
        {
            //stderrStream.println("--- StackTrace ---"); 
            t.printStackTrace(stderrStream);
        }
        
    }

}
