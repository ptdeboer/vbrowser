/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.task;

public class MonitorEvent
{
    public static enum MonitorEventType
    {
       StatusChanged,ChildMonitorAdded,ChildMonitorRemoved;
    };
    
    public ITaskMonitor source=null;
    public MonitorEventType type=null; 
    public ITaskMonitor childMonitor=null;
    
    public MonitorEvent(ITaskMonitor source)
    {
        this.source=source; 
    }
    
    public MonitorEvent(ITaskMonitor source,MonitorEventType eventType)
    {
        this.source=source;
        this.type=eventType;
    }
    
    public void setChild(ITaskMonitor child)
    {
        this.childMonitor=child; 
    }
    
    public static MonitorEvent createChildAddedEvent(ITaskMonitor source,ITaskMonitor child)
    {
        MonitorEvent event=new MonitorEvent(source,MonitorEventType.ChildMonitorAdded);
        event.setChild(child);
        return event; 
    }
    
    public static MonitorEvent createChildRemovedEvent(ITaskMonitor source,ITaskMonitor child)
    {
        MonitorEvent event=new MonitorEvent(source,MonitorEventType.ChildMonitorRemoved);
        event.setChild(child);
        return event; 
    }
    
    public static MonitorEvent createStatusEvent(ITaskMonitor source)
    {
        return new MonitorEvent(source,MonitorEventType.StatusChanged); 
    }
    
    
}
