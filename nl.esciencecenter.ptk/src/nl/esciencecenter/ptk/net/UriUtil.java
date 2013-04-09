package nl.esciencecenter.ptk.net;

import java.net.URI;
import java.net.URISyntaxException;

import nl.esciencecenter.ptk.exceptions.VRISyntaxException;

/**
 * Bridge between VRI and URI 
 */
public class UriUtil
{
    public static URI updateUserinfo(URI uri,String username) throws URISyntaxException
    {
        return new VRI(uri).replaceUserinfo(username).toURI(); 
    }

    public static URI newURI(String uri) throws URISyntaxException
    {
        try
        {
            return new VRI(uri).toURI();
        }
        catch (VRISyntaxException e)
        {
            throw new URISyntaxException(uri,e.getMessage()); // no chaining possible
        }
        
    }
    

}
