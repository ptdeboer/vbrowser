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
public class LongHolder  implements VARHolder<Long>
{
	public Long value=null;

	public LongHolder(Long val)
	{
		this.value=val; 
	}

	public LongHolder()
	{
		value=new Long(0); 
	}

	public long longValue()
	{
		if (value!=null)
			return value;
		
		throw new NullPointerException("Value in IntegerHolder is NULL");
		
	}
	
	/**
	 * Returns Holder value or defVal if holder does not contain any value 
	 */ 
	public long longValue(long defVal)
	{
		if (value!=null)
			return value;
	
		return defVal;  
	}
	
	/** Whether value was specified */ 
	public boolean isNull()
	{
		return (value==null);  
	}
	

	 public Long get()
     {
         return this.value; 
     }
        
     public void set(Long val)
     {
         this.value=val;  
     }
    
     public boolean isSet()
     {
         return (value!=null);  
     }
}



