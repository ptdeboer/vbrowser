package nl.vbrowser.ui.tasks;

import nl.nlesc.ptk.task.ActionTask;
import nl.nlesc.ptk.task.ITaskSource;


public abstract class UITask extends ActionTask
{

    public UITask(ITaskSource taskWatcher, String taskName)
    {
        super(taskWatcher, taskName);
    }
    
    public UITask(String taskName)
    {
        // Global UI Task Source ? 
        super(null, taskName);
    }
	

}
