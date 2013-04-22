package nl.esciencecenter.ptk.crypt;

/**
 * Pre configured encryption schemes.  
 * <p>
 * Jackson POJO directives: enum classes are automatically converted. 
 */
public enum CryptScheme
{
    /** Triple DES, Electronic Coockbook and PKC5 Padding. */ 
    DESEDE_ECB_PKCS5("DESede","DESede/ECB/PKCS5Padding",24),
    
    /**
     * Single DES, Electronic Coockbook and PKC5 Padding. 
     * @deprecated Do not use single DES
     */ 
    DES_ECB_PKCS5("DES","DES/ECB/PKCS5Padding",16)
    ;
    
    // === //
    
    protected String schemeName;
    
    protected String configString;
    
    protected int minimalKeyLength; 
    
    private CryptScheme(String name,String configName,int minimalKeyLength)
    {
        this.schemeName=name; 
        this.configString=configName;
        this.minimalKeyLength=minimalKeyLength; 
    }
    
    public String getSchemeName()
    {
        return schemeName; 
    }
    
    public String getConfigString()
    {
        return configString; 
    }
    
    public int getMinimalKeyLength()
    {
        return minimalKeyLength;
    }
}