/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.data;

/** 
 * String holder class for VAR String types 
 */   
public class StringHolder  implements VARHolder<String>
{
	public String value=null; 
	
	public StringHolder()
	{
		this.value=null; 
	}
	
	public StringHolder(String str)
	{
		this.value=str;  
	}
	
	public String toString()
	{
		return value; 
	}
	
	public synchronized void dispose()
	{
	    this.value=null;
	}
	
	 public String get()
	 {
	     return this.value; 
	 }
	    
	 public void set(String val)
	 {
	     this.value=val;  
	 }
	
	 public boolean isSet()
	 {
	     return (value!=null);  
	 }

} 