package nl.esciencecenter.ptk.net;

import java.net.URI;
import java.net.URISyntaxException;

import junit.framework.Assert;


import org.junit.Test;

public class Test_URIFactory
{

    @Test
    public void test_URIFactoryChaining1() throws URISyntaxException
    {
        String baseUri="file:/base";
        
        URI result=new URIFactory(baseUri)
                       .setScheme("http")
                       .setUserInfo("henk")
                       .setPort(8080)
                       .setHostname("remote")
                       .appendPath("service")
                       .uriResolve("?query#index")
                       .toURI();  
        
        Assert.assertEquals("Chainged URI does not match expected","http://henk@remote:8080/base/service?query#index",result.toString()); 
        
    }
  
    
    @Test
    public void testConstructors() throws Exception
    {
        // NOTE: use normalized URI strings here:
        testConstructor("file:/path");
        testConstructor("http://host:9909/path");
        testConstructor("https://server:1234/path?urlAttribute=value");

        // normalized: use decoded strings:
        testConstructor("gfile://HOST:1234/path");

        // normalized: use decoded strings:
        testConstructor("gfile://HOST:1234/A path");

        testConstructor("Constructor URIFactory(\"file\",null,\"/etc\") does not match", new URIFactory("file", null,0, "/etc"),
                "file:/etc");
        testConstructor("Constructor URIFactory(\"file\",null,\"/etc\") does not match", new URIFactory("file", null,0, "etc"),
                "file:etc");

        URIFactory local = new URIFactory("file", null, 0,"dirname/etc");

        // if (local.toString().compareTo("file://localhost/etc")!=0)
        Assert.assertEquals("Constructor URIFactory(\"file\",null,\"/etc\") does not match", "file:dirname/etc",
                local.toString());

        local = new URIFactory("file", null, 0, null);
        Assert.assertEquals("Constructor URIFactory(\"file\",null,null) does not match", "file:", local.toString());

        local = new URIFactory("file:///");
        Assert.assertEquals("local file URIFactory does not match", "file:/", local.toString());

        local = new URIFactory("file:/");
        Assert.assertEquals("local file URIFactory does not match", "file:/", local.toString());

        // test scheme with ":" appended
        local = new URIFactory("file:", null,0, "/etc");
        if (local.toString().compareTo("file://localhost/etc") != 0)
            Assert.assertEquals("added colon is not ignored.", "file:/etc", local.toString());

        // negative port must be filtered out.
        local = new URIFactory("file", null, -1, "/etc");
        if (local.toString().compareTo("file://localhost/etc") != 0)
            Assert.assertEquals("Negative port number is not ignored.", "file:/etc", local.toString());

        // zero port must be filtered out.
        local = new URIFactory("file", null, 0, "/etc");
        if (local.toString().compareTo("file://localhost/etc") != 0)
            Assert.assertEquals("Zero port number is not ignored.", "file:/etc", local.toString());
    }

    private void testConstructor(String uristr) throws URISyntaxException
    {
        URIFactory uriFactory=new URIFactory(uristr);
        Assert.assertEquals("String representation of URI must match original",uristr,uriFactory.toString()); 
        
        //URI uri=new URI(uristr);
        //Assert.assertEquals("URI constructed from String, must match URIFactory.toURI()",uriFactory.toURI(),uri);
    }
    
    private void testConstructor(String message, URIFactory uriFactory, String uristr)
    {
        Assert.assertEquals(message, uristr, uriFactory.toString());
    }
    
}
