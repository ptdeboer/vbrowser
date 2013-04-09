/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.data;

/** 
 * Integer holder class for VAR Integer types.
 */ 
public class IntegerHolder implements VARHolder<Integer>
{
	public Integer value=null;

	public IntegerHolder(Integer val)
	{
		this.value=val; 
	}

	public IntegerHolder()
	{
	}

	public int intValue()
	{
		if (value!=null)
			return value;
		
		throw new NullPointerException("Value in IntegerHolder is NULL");
		
	}
	
	/** Returns Holder value or defVal if holder does not contain any value */ 
	public int intValue(int defVal)
	{
		if (value!=null)
			return value;
	
		return defVal;  
	}
	
	public boolean isSet()
	{
		return (value!=null);  
	}
	
    public Integer get()
    {
        return this.value; 
    }
    
    public void set(Integer val)
    {
        this.value=val;  
    }

}



