package nl.nlesc.vlet.exception;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

public class VRLSyntaxException extends VrsException
{
    private static final long serialVersionUID = -470903262898186919L;

    public VRLSyntaxException(String message, Throwable cause)
    {
        super(message,cause,"VRL Syntax Exception."); 
    }

    public VRLSyntaxException(Throwable cause)
    {
        super(cause.getMessage(),cause,"VRL Syntax Exception."); 
    }
    
}
