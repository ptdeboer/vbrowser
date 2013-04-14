package nl.nlesc.vlet.vfs.gftp;

import nl.nlesc.vlet.exception.VlIOException;

public class GftpException extends VlIOException
{
    private static final long serialVersionUID = -57064076580488986L;

    protected GftpException(String message, Throwable cause)
    {
        super("GftpException", message, cause);
    }

    protected GftpException(String name,String message, Throwable cause)
    {
        super("GftpException:"+name, message, cause);
    }
    
}
