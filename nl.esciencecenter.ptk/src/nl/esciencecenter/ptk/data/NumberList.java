/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.data;

import java.util.ArrayList;
import java.util.List;

public class NumberList extends ArrayList<Number>
{
    
    private static final long serialVersionUID = -741767804216375724L;

    public NumberList(List<? extends Number> ints)
    {
       super(ints); 
    }

}
