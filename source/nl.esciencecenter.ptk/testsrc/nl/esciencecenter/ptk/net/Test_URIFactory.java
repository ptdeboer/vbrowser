package nl.esciencecenter.ptk.net;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
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
    public void testFileConstructors() throws Exception
    {
        // NOTE: use normalized URI strings here:
        testConstructor("file:/path","file",null,-1,"/path");
        

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
    
    @Test
    public void testOtherConstructors() throws Exception
    {
        testConstructor("http://host:9909/path","http","host",9909,"/path");
        testConstructor("https://server:1234/path?urlAttribute=value","https","server",1234,"/path","urlAttribute=value",null);

        // normalized: use decoded strings:
        testConstructor("gfile://HOST:1234/path","gfile","HOST",1234,"/path");

        // normalized: use decoded strings:
        testConstructor("gfile://HOST:1234/A path","gfile","HOST",1234,"/A path");

    }
    
    private void testConstructor(String uristr,String expectedScheme,String expectedHost,int expectedPort,String expectedPath) throws URISyntaxException
    {
        testConstructor(uristr,expectedScheme,expectedHost,expectedPort,expectedPath,null,null);
    }
    
    private void testConstructor(String uristr,String expectedScheme,String expectedHost,int expectedPort,String expectedPath,
            String expectedQuery,String expectedFragment) throws URISyntaxException
    {
        URIFactory uriFactory=new URIFactory(uristr);
        Assert.assertEquals("String representation of URI must match original",uristr,uriFactory.toString()); 
        
        // not always the case: 
        //URI uri=new URI(uristr);
        //Assert.assertEquals("URI constructed from String, must match URIFactory.toURI()",uriFactory.toURI(),uri);
        
        Assert.assertEquals("Schemes don't match.",uriFactory.getScheme(),expectedScheme);
        Assert.assertEquals("Hostnames don't match.",uriFactory.getHostname(),expectedHost);
        Assert.assertEquals("Port don't match.",uriFactory.getPort(),expectedPort);
        Assert.assertEquals("Paths don't match.",uriFactory.getPath(),expectedPath);
        Assert.assertEquals("Query parts don't match.",uriFactory.getQuery(),expectedQuery);
        Assert.assertEquals("Fragment parts don't match.",uriFactory.getFragment(),expectedFragment);
        
    }
    
    private void testConstructor(String message, URIFactory uriFactory, String uristr)
    {
        Assert.assertEquals(message, uristr, uriFactory.toString());
    }
    

    @Test
    public void testResolveAgainstRootPath() throws URISyntaxException
    {
        // resolve against root: 
        URIFactory root = new URIFactory("file:/"); 
        String newPath = root.resolvePath("/test"); 
        Assert.assertEquals("Resolved path must match","/test",newPath); 
    }

    @Test
    public void testResolveAgainstRootPathWithRelativePath() throws URISyntaxException
    {
        // resolve against root: 
        URIFactory root = new URIFactory("file:/"); 
        String newPath = root.resolvePath("test"); 
        Assert.assertEquals("Resolved path must match","/test",newPath); 
    }
}
