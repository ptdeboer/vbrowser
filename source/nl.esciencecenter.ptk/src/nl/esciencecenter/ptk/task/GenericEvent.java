package nl.esciencecenter.ptk.task;

public class GenericEvent
{
    protected Object source; 
    
    public GenericEvent(Object source)
    {
        this.source=source; 
    }
    
    
    public Object getSource()
    {
        return source; 
    }
    
}
