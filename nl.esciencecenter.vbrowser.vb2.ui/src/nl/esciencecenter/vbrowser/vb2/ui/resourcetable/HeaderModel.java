package nl.esciencecenter.vbrowser.vb2.ui.resourcetable;

import javax.swing.AbstractListModel;

import nl.esciencecenter.ptk.data.StringList;

public class HeaderModel extends AbstractListModel 
{
    private static final long serialVersionUID = -4513306632211174045L;
    
    private StringList values;

    private boolean isEditable=true; 

    public HeaderModel(StringList entries)
    {
        this.values=entries.duplicate(); 
    }
    
    public HeaderModel()
    {
        values=new StringList(); // empty list
    }
    
    public HeaderModel(String values[])
    {
        init(values);  
    }

    @Override
    public String getElementAt(int index)
    {
        return values.get(index); 
    }

    @Override
    public int getSize()
    {
        return values.size();
    }
    
    public void setValues(String vals[])
    {
        init(vals);
        this.fireContentsChanged(this,0,values.size()-1); 
    }
    
    private void init(String vals[])
    {
        this.values=new StringList(vals); 
    }
    
    public void setValues(StringList vals)
    {
        init(vals); 
        this.fireContentsChanged(this,0,values.size()-1);
    }
    
    private void init(StringList vals)
    {
        this.values=vals.duplicate();  
    }

    public String[] toArray()
    {
        return this.values.toArray(); 
    }

    public int indexOf(String name)
    {
        return this.values.indexOf(name); 
    }

    /** Inserts newHeader after 'header' of before 'header'.
     * Fires intervalAdded event */  
    public int insertHeader(String header, String newHeader,
            boolean insertBefore)
    {
        int index=-1; 
        
        if (insertBefore)
            index=this.values.insertBefore(header,newHeader); 
        else
            index=this.values.insertAfter(header,newHeader);
        
        this.fireIntervalAdded(this,index,index);
        
        return index; 
    }

    public int remove(String value)
    {
        int index=this.indexOf(value); 
        if (index<0)
            return -1;
        
        this.values.remove(index); 
        this.fireIntervalRemoved(this,index,index); 
        return index; 
    }

    public boolean isEditable()
    {
        return isEditable; 
    }

    public void setEditable(boolean val)
    {
        this.isEditable=val; 
    }

    public boolean contains(String name)
    {
        return values.contains(name); 
    }

    
}
