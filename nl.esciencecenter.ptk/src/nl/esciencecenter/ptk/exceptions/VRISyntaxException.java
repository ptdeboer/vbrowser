/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.exceptions;

public class VRISyntaxException extends Exception
{
    private static final long serialVersionUID = 3938340227693163772L;
    
    public VRISyntaxException(String message)
    {
        super(message);
    }

    public VRISyntaxException(Exception e)
    {
        super(e); 
    }

    public VRISyntaxException(String message,Throwable t)
    {
        super(message,t); 
    }

}
