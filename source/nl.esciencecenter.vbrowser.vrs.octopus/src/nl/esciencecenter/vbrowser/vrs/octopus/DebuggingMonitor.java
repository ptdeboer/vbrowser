package nl.esciencecenter.vbrowser.vrs.octopus;

import nl.esciencecenter.ptk.task.TaskMonitorAdaptor;

public class DebuggingMonitor extends TaskMonitorAdaptor
{

    public DebuggingMonitor(String taskStr,long todo)
    {
       super(taskStr,todo);
    }

    public void logPrintf(String format, Object... args)
    {
        super.logPrintf(format, args); 
        System.err.printf(format,args); 
    }
}
