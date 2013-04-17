package nl.esciencecenter.ptk.crypt;

public enum CryptScheme
{
    DESEDE_ECB_PKCS5("DESede","DESede/ECB/PKCS5Padding",24),
    DES_ECB_PKCS5("DES","DES/ECB/PKCS5Padding",16)
    ;
    
    // === //
    
    private String schemeName;
    
    private String configString;
    
    private int minimalKeyLength; 
    
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