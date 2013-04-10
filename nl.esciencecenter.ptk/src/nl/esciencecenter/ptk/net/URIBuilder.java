package nl.esciencecenter.ptk.net;

import java.net.URI;

public class URIBuilder
{
    private String scheme;
    
    private String hostname;

    private int port;
    
    private String userInfo;
    
    /**
     * Contains path or scheme specific part without authorization,query or fragment
     */ 
    private String pathOrReference;
    
    private String query;
    
    private String fragment;

    // === Field guards === // 
    private boolean hasAuthority=false;

    private boolean isReference;
    /** 
     * Create Empty URI 
     */  
    public URIBuilder()
    {
        
    }
    
    /** 
     * Parse URI Encoded String
     */ 
    public URIBuilder(String encodedUriString)
    {
        parseEncodedURIString(encodedUriString); 
    }
    
    public URIBuilder setEncodedURI(String encodedUriString)
    {
        parseEncodedURIString(encodedUriString); 
        return this; 
    }
    
    /** Use specified relative or absolute URI as base uri */ 
    public URIBuilder(java.net.URI uri)
    {
        parse(uri); 
    }
    
    // === Getters/Setters === 
    
    // === implementation === 
    
    private void parse(URI uri)
    {
        
    }

    protected void parseEncodedURIString(String encodedUriString)
    {
        
    }

    /** Construct actual URI */ 
    public java.net.URI toURI()
    {
        return null; 
    }
}
