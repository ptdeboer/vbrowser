/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.task;

import nl.esciencecenter.ptk.net.VRI;

/** 
 * Transfer Specific Monitor. 
 * 
 * @author Piter T. de BOer. 
 */
public class TransferMonitor extends MonitorAdaptor
{
    private static int transferCounter=0; 
    
    private int transferId=0; 
    
    private VRI source;

    private VRI dest;

    private String actionStr;

    private int sourcesDone;

    private int totalSources;

    public TransferMonitor(String action, VRI sourceVri, VRI destVri)
    {
        this.transferId=transferCounter++; 
        this.actionStr=action; 
        this.source=sourceVri;
        this.dest=destVri; 
    }

    public String getID()
    {
        return ""+transferId; 
    }
    
    public VRI getDestination()
    {
        return dest;
    }

    public VRI getSource()
    {
        return source;
    }

    public int getTotalSources()
    {
        return totalSources;
    }
    
    public void setTotalSources(int sources)
    {
        this.totalSources=sources;
    }

    public int getSourcesDone()
    {
        return sourcesDone; 
    }

    public void updateSourcesDone(int done)
    {
       this.sourcesDone=done; 
    }

    public String getActionType()
    {
        return actionStr;
    }
    
    
}
