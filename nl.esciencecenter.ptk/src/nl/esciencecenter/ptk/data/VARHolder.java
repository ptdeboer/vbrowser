/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.data;

/** Common VAR Holder interface */
public interface VARHolder<T>
{
    /** Whether value is set */ 
    boolean isSet(); 
    
    /** Set value */ 
    void set(T value);
    
    /** Get value */ 
    T get(); 
}
