package nl.esciencecenter.vbrowser.vb2.ui.proxy;

/**
 * Nested ProxyException to wrap implementation Exceptions. 
 */
public class ProxyException extends Exception 
{
    private static final long serialVersionUID = -4239291561460167355L;

    public ProxyException(Throwable cause)
    {
        super(cause);
    }
    
    public ProxyException(String message,Throwable cause)
    {
        super(message,cause);
    }
    
    
    
}
