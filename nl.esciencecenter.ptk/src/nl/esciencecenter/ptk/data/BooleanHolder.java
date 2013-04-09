/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.data;

/** 
 * Boolean holder class for VAR Boolean types.
 */ 
public class BooleanHolder implements VARHolder<Boolean>
{
	public Boolean value=null;  
	
	public BooleanHolder(boolean val)
	{
		value=val;
	}

	public BooleanHolder()
	{
		value=new Boolean(false);  
	}
	
	public boolean booleanValue()
	{
		if (value!=null)
			return value;
		
		throw new NullPointerException("Value in IntegerHolder is NULL");
		
	}
	
	/** Returns Holder value or defVal if holder does not contain any value */ 
	public boolean booleanValue(boolean defVal)
	{
		if (value!=null)
			return value;
	
		return defVal;  
	}
	
	/** Whether value was specified */ 
	public boolean isSet()
	{
		return (value!=null);  
	}
	
    public void set(Boolean val)
    {
      this.value=val;
    }
    
    public Boolean get()
    {
        return value;
    } 
} 

