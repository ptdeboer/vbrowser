package nl.nlesc.vlet.exception;

public class VlNetworkException extends VlIOException 
{
    private static final long serialVersionUID = 4058564395522741076L;
    
    public VlNetworkException(String message)
    {
        super("Network Exception", message,null);
    }

    public VlNetworkException(String message, Throwable lastex)
    {
        super("Network Exception", message, lastex);
    }

    public VlNetworkException(Throwable e)
    {
        super("Network Exception", e.getMessage(), e);
    }

    protected VlNetworkException(String name, String message, Throwable e)
    {
        super(name,message,e);
    }

}
