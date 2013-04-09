/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.data;

/** 
 * Alternative to clone() which supports Generics. 
 */
public interface Duplicatable<Type> 
{
	/** 
	 * Return whether shallow copies are supported. 
	 * If shallow copies are supported, the duplicate(true) method
	 * will always return a shallow copy. 
	 * By default duplicate() returns a full (non-shallow) copy.
	 */  
	public boolean shallowSupported(); 
	
	/** Return copy (clone) of object */ 
	public Type duplicate(); 
	
	/**
	 * Returns copy of object. Specify whether shallow copy is allowed. 
	 * If shallow==true, the duplicate method might still return 
	 * a non shallow copy or throw an exception if shallowSupported()==false. 
	 */ 
	public Type duplicate(boolean shallow);
	
}
