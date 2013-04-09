package nl.esciencecenter.vbrowser.vb2.ui.tasks;

import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskSource;


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
