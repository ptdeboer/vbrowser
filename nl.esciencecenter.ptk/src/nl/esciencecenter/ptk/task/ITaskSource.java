/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.task;

public interface ITaskSource 
{
    void registerTask(ActionTask actionTask);

    void unregisterTask(ActionTask actionTask); 
    
    /** Is called by ActionTask when task has actually started*/ 
	void notifyTaskStarted(ActionTask actionTask);

	/** Is called by ActionTask when task has terminated */
	void notifyTaskTerminated(ActionTask actionTask);

    void notifyTaskException(ActionTask actionTask, Throwable t);

    void setHasActiveTasks(boolean active);

}
