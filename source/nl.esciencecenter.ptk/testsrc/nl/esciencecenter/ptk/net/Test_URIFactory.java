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
  
    
}
