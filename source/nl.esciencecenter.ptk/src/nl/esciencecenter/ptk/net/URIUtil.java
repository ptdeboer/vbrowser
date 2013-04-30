package nl.esciencecenter.ptk.net;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** 
 * URI Factory methods. 
 */
public class URIUtil
{

    public static URI replacePath(URI uri, String newPath) throws URISyntaxException
    {
        return new URIFactory(uri).changePath(newPath).toURI();
    }

    public static URI replaceScheme(URI uri, String newScheme) throws URISyntaxException
    {
        return new URIFactory(uri).changePath(newScheme).toURI();
    }

    public static URI replaceHostname(URI uri, String newHostname) throws URISyntaxException
    {
        return new URIFactory(uri).changeHostname(newHostname).toURI();
    }

    public static URI resolvePath(URI uri, String relativePath) throws URISyntaxException
    {
        return new URIFactory(uri).resolvePath(relativePath).toURI();
    }

    public static URI appendPath(URI uri, String path) throws URISyntaxException
    {
        return new URIFactory(uri).appendPath(path).toURI();
    }

    public static URI replaceUserinfo(URI uri, String userInfo) throws URISyntaxException
    {
        return new URIFactory(uri).changeUserInfo(userInfo).toURI();
    }

    public static List<URI> parseURIList(String urilist, String pathSeperator)
    {
        Scanner scanner = new Scanner(urilist.trim());
        scanner.useDelimiter(pathSeperator);

        List<URI> uris = new ArrayList<URI>();

        while (scanner.hasNext())
        {
            String lineStr = scanner.next();

            try
            {
                uris.add(new URIFactory(lineStr).toURI());
            }
            catch (URISyntaxException e)
            {
                ; // skip ?
            }
        }
        scanner.close();
        return uris;
    }

}
