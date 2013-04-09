/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.data;

import nl.esciencecenter.ptk.crypt.Secret;

/** 
 * Secret Holder class. 
 */   
public class SecretHolder  implements VARHolder<Secret>
{
	public Secret value=null; 
	
	/** Wrap Holder around secret chars, source characters are cleared! */ 
	public SecretHolder(char[] secret)
	{
		this.value=new Secret(secret,true); // auto clear source!
	}
	
	public SecretHolder(Secret secret)
	{
		this.value=secret;  
	}
	
	public SecretHolder()
    {
    }

    public String toString()
	{
		return "<SecretHolder>";  
	}
	
	public synchronized void dispose()
	{
	    if (value!=null)
	        value.dispose(); 
	    this.value=null;
	}
	
	 public Secret get()
	 {
	     return this.value; 
	 }
	  
	 public char[] getChars()
	 {
	     if (this.value==null)
	         return null;
	     return this.value.getChars(); 
	 }
	 
	 public void set(Secret val)
	 {
	     this.value=val;  
	 }
	
	 public boolean isSet()
	 {
	     return (value!=null);  
	 }

} 