/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.util;

/**
 * The interface comparer is used by the QSort class.  
 */
public interface Comparer<Type>
{
    int compare(Type o1, Type o2);
}
