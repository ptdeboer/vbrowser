/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package nl.esciencecenter.vlet.grid.proxy;


import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.Vector;

import javax.net.ssl.SSLContext;

import nl.esciencecenter.ptk.crypt.Secret;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.ssl.CertUtil;
import nl.esciencecenter.ptk.ssl.CertificateStore;
import nl.esciencecenter.ptk.util.ResourceLoader;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.exception.ConfigurationError;
import nl.esciencecenter.vlet.exception.InitializationException;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.grid.voms.VO;
import nl.esciencecenter.vlet.grid.voms.VOServer;
import nl.esciencecenter.vlet.net.ssl.SSLContextManager;
import nl.esciencecenter.vlet.net.ssl.VrsSslUtil;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vrms.ConfigManager;


/**
 * Grid Proxy Util class.
 * It is a wrapper class around a globus credential. 
 * It provides convenient methods in handling grid proxies.
 * The Grid Proxy class needs a "GridCredentialProvider" which is the credential (proxy) factory class. 
 * To allow for different proxy schemes, the GridCredentialProvider is a abstract interface. 
 * It is necessary to register a GridCredentialProvider first before the GridProxy class can create
 * proxies. 
 * @see VGridCredential 
 * @see VGridCredentialProvider 
 */
public class GridProxy
{
    private static ClassLogger logger; 

    /** Default Grid Credential Type */ 
    public static final String GLOBUS_CREDENTIAL_TYPE="GlobusCredential";
    
    /** Default Grid Credential Type */ 
    public static final String GSS_CREDENTIAL_TYPE="GSSCredential"; 
    
    public static final String ATTR_SUBJECT  = "proxySubject"; 
    public static final String ATTR_VOINFO   = "proxyVOInfo"; 
    public static final String ATTR_VALIDITY = "proxyValidity"; 
    public static final String ATTR_ENDTIME  = "proxyEndtime";


    /** Not used */ 
    //private static GSSCredential hostGSSCredential;
    
    private static VGridCredentialProvider defaultCredentialProvider=null; 
    
    private static String defaultCredentialType=GLOBUS_CREDENTIAL_TYPE;
    
    private static Map<String,VGridCredentialProvider> providers; 
    
    static
    {
        logger=ClassLogger.getLogger(GridProxy.class); 
        //logger.setLevelToDebug();
        providers=new Hashtable<String,VGridCredentialProvider>();
    }
    

    public static void registerProvider(String credentialType, VGridCredentialProvider credProvider)
    {
        synchronized(providers)
        {
            String types[]=credProvider.getCredentialTypes(); 

            providers.put(credentialType,credProvider); 
            // aliasses: 
            for (String type:types)
            {
                logger.infoPrintf("Registering CredentialProvider:%s\n",type); 
                providers.put(type,credProvider); 
            }      
            // use providers as mutex for following fields as well: 
            if (defaultCredentialProvider==null)
            {
                defaultCredentialProvider = credProvider;
                defaultCredentialType=credentialType;
            }
        }
    }

    /** Find credential provider for the specified type */ 
    public static VGridCredentialProvider findProvider(String credentialType) 
    {
       VGridCredentialProvider credProv = providers.get(credentialType);
       
       if (defaultCredentialProvider!=null) 
           if (defaultCredentialProvider.canCreateCredentialType(credentialType)==true)
               return credProv; 
       
       //Fail:
       return null; //throw new VlInternalError("Cannot get Provider for Credential Type:"+credentialType); 
    }
    
    // ======================================================================== //
    
    // ======================================================================== //
    
    
    /**
     * @deprecated static proxy creation deprecated. Please specify VRSContext. 
     * See: {@link #loadFrom(VRSContext, String)} 
     */   
    public static GridProxy loadFrom(String proxyFile) throws VrsException
    {
        GridProxy proxy=new GridProxy(VRSContext.getDefault());
        
        proxy.loadProxy(proxyFile);
        // after loading
        logger.infoPrintf("Proxy loaded from:%s\n",proxyFile); 
        
        return proxy;
    }

    /**
     * Return default Grid Proxy using default (user) credentials. 
     */ 
    public static GridProxy getDefault()
    {
        return VRSContext.getDefault().getGridProxy(); 
    }

    /**
     * Load proxy file using VRSContext and specified filename 
     * @throws VrsException 
     */ 
    public static GridProxy loadFrom(VRSContext context,String proxyFile) throws VrsException
    {
        GridProxy proxy=new GridProxy(context);

        // fails if proxy not created yet: 
        proxy.loadProxy(proxyFile);
            
        return proxy;
    }
    
    /**
     * Find and load proxy given the VRS Context
     * Always returns new GridProxy object, but credentials
     * may not be valid yet, if the proxy hasn't been created. 
     */ 
    public static GridProxy createProxy(VRSContext context)
    {
        return new GridProxy(context);
    }
    
    // ========================================================================
    // Instance
    // ========================================================================

    /** Listeners who want to know about the status of this proxy */
    private Vector<GridProxyListener> listeners = new Vector<GridProxyListener>();
    
    // Grid Credential Wrapper Object 
    private VGridCredential credential=null;
    
    private VRSContext vrsContext;

    private VGridCredentialProvider credentialProvider;

    // Private User Key Stuff // 
    private boolean autoUpdatePrivateKey=true; 
       
    private String privateUserKeyAlias="grid-private-userkey";

    private Secret privateKeystorePasswd=new Secret("internal".toCharArray());  
    
    private SSLContextManager sslCtxManager=null;

    // cached values: 
    private boolean prevValid=false;
    
    private long prevEndtime=-1;

    private String prevVOInfo; 
    
    // ========================================================================
    // Instance Methods
    // ========================================================================

    // ========================================================================
    // Constructors/Initializers 
    // ========================================================================
    
    /**
     * Create default Grid Proxy. Tries to load default proxy if it exists. 
     */
    public GridProxy(VRSContext context)
    {
        this.vrsContext=context;
        
        // update provider using VLET defaults: 
        this.initProvider();
        
        loadCertificates();
        
        try
        {
            // try to read default and update proxy values, if previous proxy exists. 
            loadProxy(); 
        }
        catch (VrsException e)
        {
            logger.logException(ClassLogger.INFO,e,"Couldn't load default Proxy (will use invalid proxy for now)\n"); 
        }
    }
    

    /**
     * Update Provider with Configuration Settings. 
     * Takes properties from VLET_INSTALL/etc/vletrc.prop and ~/.vletrc/vletrc.prop and 
     * updates the Proxy Provider. 
     */
    private void initProvider()
    {
        // Update Global VLET configuration into *current* CredentialProvider. 
        VGridCredentialProvider provider=this._getProvider();  
        
        // Allowed for debugging purposes: 
        if (provider==null)
            return; 
        
        String val;
        int intVal;  
        ConfigManager confMan = vrsContext.getConfigManager();
        
        // ===
        // Proxy life time 
        // ===
        val=confMan.getProperty(VletConfig.PROP_GRID_PROXY_LIFETIME);
        logger.debugPrintf("using %s='%s'\n",VletConfig.PROP_GRID_PROXY_LIFETIME,val);
        
        if ((val!=null) && (val.length()>0))
        {   
            try
            {
                intVal=Integer.parseInt(val); 
                provider.setDefaultLifetime(intVal);
            }
            catch (Throwable t)
            {
                logger.logException(ClassLogger.ERROR,t,"Invalid integer value for '%s'='%s'\n",
                        VletConfig.PROP_GRID_PROXY_LIFETIME,val); 
            }
        }

        // ===
        // enable voms
        // ===
        val=confMan.getProperty(VletConfig.PROP_GRID_PROXY_ENABLE_VOMS);
        logger.debugPrintf("using %s='%s'\n",VletConfig.PROP_GRID_PROXY_ENABLE_VOMS,val);
        
        if ((val!=null) && (val.length()>0))
        {   
            provider.setEnableVoms(new Boolean(val)); 
        }       

        // ===
        // voms name+role
        // ===
        val=confMan.getProperty(VletConfig.PROP_GRID_PROXY_VO_NAME);
        logger.debugPrintf("using %s='%s'\n",VletConfig.PROP_GRID_PROXY_VO_NAME,val);
        
        if ((val!=null) && (val.length()>0))
        {   
            provider.setDefaultVOName(val); 
        }
        
        val=confMan.getProperty(VletConfig.PROP_GRID_PROXY_VO_ROLE);
        logger.debugPrintf("using %s='%s'\n",VletConfig.PROP_GRID_PROXY_VO_ROLE,val);

        if ((val!=null) && (val.length()>0))
        {   
            provider.setDefaultVORole(val); 
        }
        
        // ===
        // check proxy filepath (/tmp/x509...) 
        // ===
        
        // I) Check VRContext "grid.proxy.location=..." 
        // get optional relative Grid Proxy Location 
        val=confMan.getProperty(VletConfig.PROP_GRID_PROXY_LOCATION);
        logger.debugPrintf("using %s='%s'\n",VletConfig.PROP_GRID_PROXY_LOCATION,val);

        String proxyFilename=null; 
        
        if (val!=null)
        {
            try
            {
                proxyFilename=vrsContext.getConfigManager().getUserHomeLocation().resolvePath(val).getPath(); 
            }
            catch (Exception e)
            {
                logger.logException(ClassLogger.ERROR,e,"Faile to resolve local filepath:%s\n",val); 
                proxyFilename=val;
            }
        }
        
            
        // II) Check X509_USER_PROXY environment: UI RUntime !! 
        if (StringUtil.isEmpty(proxyFilename))
        {
            proxyFilename=vrsContext.getStringProperty(VletConfig.ENV_X509_USER_PROXY);
            if (StringUtil.notEmpty(proxyFilename))
                logger.infoPrintf("Using proxy filename from environment:%s=%s\n",VletConfig.ENV_X509_USER_PROXY,proxyFilename); 
        }

        // Update provider to use default file name: 
        if (StringUtil.isEmpty(proxyFilename)==false)
        {
            String defPath=provider.getDefaultProxyFilename();
            if (StringUtil.equals(defPath,proxyFilename)==false)
                logger.infoPrintf("Using different proxy filename then default:%s\n",proxyFilename); 
            
            provider.setDefaultProxyFilename(proxyFilename);
        }
        
        // ===
        // check location of user certificates ($HOME./globus/) 
        // ===
        
        val=vrsContext.getStringProperty(VletConfig.PROP_GRID_CERTIFICATE_LOCATION);
        String userCertDir=null; 
        
        if (StringUtil.isEmpty(val)==false) 
        {
            userCertDir=resolve(val);  
        }
        
        // fetch default from model 
        if (StringUtil.isEmpty(userCertDir)==false) 
        {
            if (VletConfig.isApplet()==false)
            {
                // will update provider: 
                this.setUserCertificateDirectory(userCertDir); 
            }
        }
    }


    public boolean setCredential(VGridCredential newCredential)
    {
        this.credential=newCredential;
        init(credential);
        
        return this.checkAndNotifyAttributes(); 
    }
    
    private void init(VGridCredential credential)
    {
        this.credential=credential;
        this.credentialProvider=credential.getProvider();
    }
 
    public boolean createFromString(String credentialType, String proxyStr) throws VrsException
    {
        VGridCredentialProvider prov = findProvider(credentialType);
        
        if (prov==null)
            throw new VrsException("Can get provider for:"+credentialType); 
     
        try
        {
            prov.createCredentialFromString(proxyStr);
            this.credentialProvider=prov; 
            
            this.credential=prov.createCredentialFromString(proxyStr);
            init(credential);
            
            return this.checkAndNotifyAttributes();
        }
        catch (Exception e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    public void setGlobusCredential(String globusProxyString) throws VrsException
    {
        this.createFromString(GLOBUS_CREDENTIAL_TYPE,globusProxyString); 
    }

    // =======================================================================
    // Getters/Setters 
    // ========================================================================

    protected boolean updateGridProxyValues()
    {
        return checkAndNotifyAttributes(); 
    }

    public boolean checkGridProxy(boolean autoload)
    {
        // auto (re)load:
        if ((credential == null) && (autoload))  
        {
            reload(); 
        }
        
        return checkAndNotifyAttributes();     
    }    
    
    protected boolean checkAndNotifyAttributes()
    {
        long time=0; 
        long timeleft=0;  
        long endtime=0; 
        boolean valid=false;
         
        if (credential==null)
        {
            valid = false;
        }
        else
        {
            time=System.currentTimeMillis()/1000;  
            timeleft = credential.getTimeLeft();
            endtime=time+timeleft; 
            if (timeleft <= 0)
                valid = false;
            else
                valid = true;
        }
        
        // System.err.println("> times = "+time+" + "+timeleft+" = "+endtime); 
        
        AttributeSet attrs=new AttributeSet();
        
        if (prevValid != valid)
            attrs.set(ATTR_VALIDITY,valid); 

        if (prevEndtime!=endtime)
            attrs.set(ATTR_ENDTIME,endtime);
        
        String voStr=getCredentialVOInfo();
        
        if (StringUtil.compare(voStr,prevVOInfo)!=0) 
            attrs.set(ATTR_VOINFO,voStr); 
        
        prevVOInfo=voStr; 
        prevEndtime=endtime;
        prevValid=valid;
        
        //anything changed?
        if (attrs.containsKey(ATTR_VALIDITY))
            this.fireProxyValidityChange(valid); // Separate (legacy) event;  
        
        if (attrs.size()>0) 
            this.fireProxyValuesChanged(attrs.toArray(new Attribute[0])); 
        
        return valid;
    }

    public String toString()
    {
        String str = "";

        if (isValid(false))
            str += "--- Valid (Grid) Credential ---\n";
        else
            str += "*** INVALID (GRID) Credential ***\n";

        str += "Location         :" + this.getProxyFilename() + "\n"
             + "Complete Subject :" + this.credential.getUserSubject() + "\n"
             + "Subject UserDN   :" + this.credential.getUserDN() + "\n" 
             + "Issuer           :" + this.credential.getIssuer() + "\n"
             + "Time Left        :" + this.getTimeLeftString() + "\n";

        return str;
    }

    /**
     * Returns VO Name which will be used to create the credential 
     * Strips /Role part from VOName if defined. 
     */ 
    public String getDefaultVOName()
    {
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return null; 
        
        return getProvider().getDefaultVOName();
    }
    
    /**
     * Returns optional VO Role.  
     */ 
    public String getDefaultVORole()
    {
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return null; 
        
        return getProvider().getDefaultVORole();
    }
    
    /**
     * Returns complete VOName which will be used to create the credential
     * including /Role part.  
     */ 
    public String getDefaultVOCommand()
    {
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return null; 
        
        String voName=getProvider().getDefaultVOName();
        String voRole=getProvider().getDefaultVORole();
        if (voRole!=null)
            voName=voName+":"+voRole;
        
        return voRole; 
    }
    
    /** Checks wether current proxy has a VO Attribute.*/ 
    public boolean isVOEnabled()
    {
        if (credential!=null)
            return (StringUtil.isEmpty(credential.getVOName())==false);
        
        return false; 
    }

    /** Whether VOMS proxies are enabled. This doesn't mean the current proxy is VO enabled */ 
    public boolean getEnableVOMS()
    {
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return false; 
             
        return getProvider().getEnableVoms(); 
    }
    
    public void setEnableVOMS(boolean val)
    {
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return;
        
        getProvider().setEnableVoms(new Boolean(val)); 
        
        if (getUsePersistantUserSettings()) 
            saveUserSettings(); 
    }
    
    public boolean getUsePersistantUserSettings()
    {
        return VletConfig.getUsePersistantUserConfiguration();
    }
    
    /**
     * Create proxy by using password, 
     * 
     * @param passwdstr
     * @return
     * @throws VrsException
     */
    public synchronized boolean createWithPassword(final Secret passwd) throws VrsException
    {
        VGridCredentialProvider provider;
        
        // Used for testing purposes only: 
        if ((provider=this._getProvider())==null)
            throw new InitializationException("No Credential Provider Registered"); 

        //String vo=provider.getDefaultVOName(); 
    	//checkImportVomsServerCertificates(vo); 

        try
        {
            this.credential=provider.createCredential(passwd);
            this.saveProxy(); 

            if (this.autoUpdatePrivateKey)
            {
                updatePrivateKey(passwd);
            }
            
            return checkAndNotifyAttributes();  
        }
        catch (Exception e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }


    /** Location of userkey.pem used to create this proxy. */ 
    public String getUserKeyFilename()
    {
        if (credential!=null)
            return this.credential.getUserKeyFile();
        
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return null; 
        
        return  this.getProvider().getDefaultUserKeyLocation(); 
    }
    
    /**
     * Location of usercert.pem used to create this proxy.
     * If credential hasn't been created it will return the default usercert location.  
     */ 
    public String getUserCertFile()
    {
        String path=null; 
        
        if (credential!=null)
            path=this.credential.getUserCertFile();
        
        if (StringUtil.notEmpty(path)) 
            return path; 
        
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return null; 
        
        return this.getProvider().getDefaultUserCertLocation(); 
    }
    
    /**
     * Location of current created proxy. 
     * If the proxy hasn't been created yet this method returns getDefaultProxyFilename.
     * If the credential has been saved to a file, that location will be returned.  
     */ 
    public String getProxyFilename()
    {
        String path=null; 
        
        if (credential!=null)
            path=this.credential.getCredentialFilename();
        
        if (path!=null)
            return path;

        return getDefaultProxyFilename(); 
    }
    
    /**
     * Return's default proxyfilename. New proxies will be created at this location.
     */
    public String getDefaultProxyFilename()
    {
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return null; 
        
        // must return default! 
        return this.getProvider().getDefaultProxyFilename();
    }
    
    /**
     * Load extra certificates from installation directory VLET_INSTALL/etc/certificates
     * and user configuration directory ~/.vletrc/certificates 
     */
    public synchronized void loadCertificates()
    {
        VRL vrls[]=VletConfig.getCACertificateLocations();
        
        StringList caCertsPaths=new StringList();
        
        for (VRL vrl:vrls)
            caCertsPaths.add(vrl.getDirPath()); 
        
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return; 
        
        this.getProvider().setRootCertificateLocations(caCertsPaths); 
        
        this.fireCertsChanged(null); // null = all 
    }

    /**
     * (re)load the proxy and it's configuration
     * Uses the VRSContext which whas supplied to 
     * the constructor to fetch user settings. 
     * @param reload: optimization parameter: check for new certificates, etc. 
     * @throws VrsException
     */
    protected synchronized void loadProxy() throws VrsException
    {
        // load default specifed by provider: 
        VGridCredentialProvider prov = _getProvider(); 
            
        if (prov==null)
            throw new InitializationException("No Credential Provider has been registered"); 
       
        String path=prov.getDefaultProxyFilename();
        
        loadProxy(path); 
    }
    
    protected synchronized boolean loadProxy(String proxypath) throws VrsException
    {
        if (VletConfig.isApplet() == true)
        {
            return false; 
        }
        
        logger.infoPrintf("Loading proxy from:%s\n",proxypath);
        
        // throw exception: 
        this.credentialProvider=this._getProvider(); 
        if (credentialProvider==null)
            throw new InitializationException("No Credential Provider has been registered"); 
        
        try
        {
            // Refresh certificates: Directory might be updated! 
            loadCertificates();
            
            this.credential=this.credentialProvider.createCredentialFromFile(proxypath); 
            
            return this.checkAndNotifyAttributes();
        }
        catch (Exception e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
        
    }

    /** 
     * Get current provider or use default provider.
     */ 
    public VGridCredentialProvider getProvider() 
    {
        VGridCredentialProvider prov = this._getProvider();
 
        if (prov!=null)
            return prov; 
        
        // throw exception since no null pointer checking is done by calling code!
        throw new Error("No (Grid) Credential Provider has been registered!"); 
    }
    
    
    // Internal helpd method to get any provider. 
    private VGridCredentialProvider _getProvider() 
    {
        if (this.credentialProvider!=null)
           return credentialProvider; 

        
        return findProvider(defaultCredentialType); 
    }

    /**
     * Save current proxy. Note that when running as web applet the save might
     * fail !
     */
    public boolean saveProxy() throws VrsException
    {
        if (VletConfig.isApplet())
            return false;
        // Use explicit proxy path or, if not specified, use default path from provider
        String path=credential.getCredentialFilename(); 
        if (StringUtil.isEmpty(path))
            path=getProvider().getDefaultProxyFilename(); 
        logger.infoPrintf("Saving proxy to:%s\n",path); 
        try
        {
            return this.credential.saveCredentialTo(path);
        }
        catch (Exception e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    /**
     * Save proxy to local path. 
     */
    public boolean saveProxyTo(String path) throws VrsException
    {
        if (this.credential==null)
            throw new nl.esciencecenter.vlet.exception.InitializationException("Can not save NULL Credential!");
        
        try
        {
            return this.credential.saveCredentialTo(path);
        }
        catch (Exception e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
 
    }

    public boolean isValid()
    {
        // always reload and check proxy
        return isValid(true); 
    }
    
    public boolean isValid(boolean reload)
    {
        // always reload and check proxy 
        return checkGridProxy(reload);
    }
    
    public VGridCredential getCredential(String credentialType)
    {
        if (credential==null)
            return null;  
        
        if (credential.isType(credentialType)==false)
            return null; 
        
        return this.credential; 
    }
    
    public VGridCredential getCredential()
    {
        return this.credential; 
    }
    
    /** Use GlobusCredential as current credential. The GlobusCredential Provider must be registered */ 
    public boolean setGlobusCredential(Object globusCredential) throws VrsException
    {  
        try
        {
            VGridCredentialProvider prov = findProvider(GLOBUS_CREDENTIAL_TYPE);
            
            if (prov==null)
                throw new ConfigurationError("No provider registed for Credential type:"+GLOBUS_CREDENTIAL_TYPE);
            
            this.credentialProvider=prov;
            
            credential=credentialProvider.createFromObject(globusCredential);
            init(credential);
            
            return this.checkAndNotifyAttributes();
        }
        catch (Exception e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }
    
    /** Default lifetime in hours */ 
    public int getDefaultLifeTime()
    {
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return -1;  
        
        return this.getProvider().getDefaultLifetime();
     }

    /** Returns parent directory of Certificate Location (of user[key|cert].pem) */
    public String getUserCertificateDirectory()
    {
        String path=null; 
        
        if (this.credential!=null)
            path=URIFactory.dirname(this.credential.getUserCertFile());
        
        if (StringUtil.notEmpty(path))
            return path;  
            
        // Used for testing purposes only: 
        if (this._getProvider()==null)
            return null; 
        
        return URIFactory.dirname(this.getProvider().getDefaultUserCertLocation()); 
    }
    
    /** Returns time left from the proxy in seconds. */ 
    public long getTimeLeft()
    {
        if (credential == null)
            return -1;

        return credential.getTimeLeft();
    }

    /** Returns time left in hours. */ 
    public long getTimeLeftHours()
    {
        if (credential == null)
            return -1;
        return (credential.getTimeLeft()/ 3600);
    }
    
    /** Returns time left in minutes. */ 
    public long getTimeLeftMinutes()
    {
        if (credential == null)
            return -1;

        // timeLeftHours = (int)(timeLeft % (24 * 3600)) / 3600;
        // timeLeftMinutes = (int)(timeLeft % 3600) / 60;
        // timeLeftSeconds = (int)(timeLeft % 60);

        return (credential.getTimeLeft()/ 60);
    }

    public long getTimeLeftSeconds()
    {
        if (credential == null)
            return -1;

        // should be seconds: 
        return credential.getTimeLeft();
    }

    /** Return time left in human readable String: "[DD days] HH:mm:ss". */ 
    public String getTimeLeftString()
    {
        if (getTimeLeft() <= -0)
            return "n/a";
        
        String timestr;
        int nrsecs=(int)(this.getTimeLeftSeconds()); 
        long days=nrsecs/(24*3600);
        // add optional days: 
        if (days>0)
            timestr=""+days+" days "; 
        else
            timestr="";
        
        int hh=(nrsecs/3600)%24; 
        int mm=(nrsecs/60)%60; 
        int ss=nrsecs%60; 
        
        timestr +=dec2str(hh) + ":"
                + dec2str(mm) + ":"
                + dec2str(ss);
        return timestr; 
    }

    private String dec2str(long val)
    {
        if (val < 10)
            return "0" + val;
        else
            return "" + val;
    }

    /** 
     * Delete proxy 
     * @throws IOException 
     */ 
    public synchronized boolean destroy() throws IOException
    {
        //boolean prev = this.valid;
        if (this.credential==null)
            return true; 
        
        FSUtil.getDefault().deleteFile(credential.getCredentialFilename()); 

        this.credential=null;
        
        return (checkAndNotifyAttributes()==false); 
    }

    public String getSubject()
    {
        if (this.credential == null)
            return null;

        return credential.getUserSubject(); 
    }

    /** 
     * Set (default) parent directory which contains certificates (usercert.pem+userkey.pem)
     * to ne used when creating new proxies. 
     */
    public void setUserCertificateDirectory(String dir)
    {
        // reset to default! 
        if (StringUtil.isEmpty(dir))
        {
            // reset settings: 
            this.getProvider().setDefaultUserCertFile(null);
            this.getProvider().setDefaultUserKeyFile(null);
            
            return;
        }
        // use absolute dir!
        dir=resolve(dir); 
        
        getProvider().setDefaultUserCertFile(dir+"/usercert.pem");  
        getProvider().setDefaultUserKeyFile(dir+"/userkey.pem");  
        
        save(); 
    }
    
    public void save()
    {
        if (getUsePersistantUserSettings()) 
            saveUserSettings(); 
    }

    // Resolve optional relative path against user home location 
    public String resolve(String path) 
    {
        try
        {
            return vrsContext.getConfigManager().getUserHomeLocation().resolvePath(path).getPath();
        }
        catch (Exception e)
        {
            return path;// return as-is; 
        }
    }

    /**
     * Sets default path of proxy filename. New proxies will be saved to this path.
     */
    public void setDefaultProxyLocation(String path)
    {
        // Null -> Reset! 
        if (StringUtil.isEmpty(path))
        {
            getProvider().setDefaultProxyFilename(null); //reset 
            path=getProvider().getDefaultProxyFilename(); 
        }
        
        getProvider().setDefaultProxyFilename(path); 
        
        save();
    }

    /** 
     * Sets the lifetime in hours of the proxy. Only applies for new created proxies.  
     * If persistantSettings==true this will save the settings 
     * to $HOME/.vletrc.prop s
     */
    public void setDefaultProxyLifetime(int time)
    {
        this.getProvider().setDefaultLifetime(time); 
        
        save();
    }
    
    public void setDefaultVOName(String name) 
    {
        logger.debugPrintf("setVOName:"+name); 

        this.getProvider().setDefaultVOName(name); 
        
        save();
    }
    
    public void setDefaultVORole(String role)
    {
        logger.debugPrintf("setVORole:"+role); 

        this.getProvider().setDefaultVORole(role);  
        
        save();
    }
    
    public String getUserDN()
    {
        if (this.credential==null)
            return null; 
        
        return this.credential.getUserDN();  
    }
    
    /**
     * Save proxy settings to $HOME/.vletrc/vletrc.prop. 
     * NOTE: this method will make the settings in this proxy
     * as the user's defaults. 
     * 
     */ 
    protected void saveUserSettings()
    {
        // regression bug!
        if (vrsContext==null)
            throw new NullPointerException("VRSContext is NULL!");
        
        // Get current provider: 
        VGridCredentialProvider provider = this.getProvider(); 
        
        // save Provider Defaults to VLET configuration ! 
        vrsContext.setUserProperty(VletConfig.PROP_GRID_PROXY_LOCATION,provider.getDefaultProxyFilename());  
        vrsContext.setUserProperty(VletConfig.PROP_GRID_CERTIFICATE_LOCATION,this.getUserCertificateDirectory());
        vrsContext.setUserProperty(VletConfig.PROP_GRID_PROXY_LIFETIME,""+provider.getDefaultLifetime());
        vrsContext.setUserProperty(VletConfig.PROP_GRID_PROXY_ENABLE_VOMS,""+provider.getEnableVoms());
        vrsContext.setUserProperty(VletConfig.PROP_GRID_PROXY_VO_NAME,provider.getDefaultVOName());
        vrsContext.setUserProperty(VletConfig.PROP_GRID_PROXY_VO_ROLE,provider.getDefaultVORole());
   }

    /**
     * Load proxy and return it as String: uses ResourceLoader.DEFAULT_CHARSET
     * (must match setGlobusCredential(String))
     * 
     * @throws VrsException
     */
    public String getProxyAsString() throws VrsException
    {
        try
        {
            VRL vrl=new VRL("file:///").resolvePath(getProxyFilename());
              
            String str = ResourceLoader.getDefault().readText(vrl.toURI()); 
            return str;
        }
        catch (Exception e)
        {
            throw VrsException.newChainedException(e);
        }
    }
    
    /**
     * Reload proxy if it exists, 
     * If no proxy has been loaded it returns withouth any exception. 
     * use isValid() after (re)load to check whether the current 
     * proxy is valid.  
     */

    public boolean reload() 
    {
        try
        {
            if (this.credential==null) 
            {
                this.loadProxy();
            }
            else
            {
                this.loadProxy(credential.getCredentialFilename()); 
            }
        }
        catch (Exception e)
        {
            this.credential=null; // reload failed ! 
            // happens when proxy hasn't been created. 
            logger.logException(ClassLogger.INFO,e,"Could not (re)load proxy\n"); 
        }
        
        return this.checkAndNotifyAttributes(); 
    }
    
    public void setVRSContext(VRSContext context)
    {
        this.vrsContext=context; 
    }
    
    public VRSContext getVRSContext()
    {
    	return vrsContext; 
    }
    
    /** Return default directory name which contains userkey and usercert */ 
    public String getDefaultUserKeyLocation()
    {
        return this.getProvider().getDefaultProxyFilename(); 
    }
    
    /** Return default directory name which contains userkey and usercert */ 
    public String getDefaultUserCertLocation()
    {
        return this.getProvider().getDefaultUserCertLocation(); 
    }

    /**
     * Return VO Name directly from credential. 
     * Might return null if no VOMS attribute can be found or credential is invalid. 
     * Uset getDefaultVOName to get the configured VO.
     */ 
    public String getCredentialVOName()
    {
        if (credential==null)
            return null; 
        
        return credential.getVOName();
    }
    
    public String getCredentialVOInfo()
    {
        return createVOInfoString(credential); 
    }
    
    protected String createVOInfoString(VGridCredential cred)
    {
        if (credential==null)
            return null;  
        
        String str=credential.getVOName(); 
        
        String group=credential.getVOGroup();
        
        if (StringUtil.isWhiteSpace(group)==false)
            str+="/"+group;
        
        String role=credential.getVORole(); 
        if (StringUtil.isWhiteSpace(role)==false)
            str+="/Role="+role;  
        
        return str; 
    }

    public String getVOName()
    {
        return this.getCredentialVOName(); 
    }
    
    /** Return default VO object for current credential provider 
     * @throws VrsException */
    public VO getDefaultVO() throws VrsException
    {
    	return findVO(this.getDefaultVOName()); 
    }
    
    public X509Certificate getUserCertificate() throws Exception
    {
        return CertUtil.loadPEMCertificate(this.getUserCertFile());
    }
    
    public void setPrivateKeystorePassword(Secret passwd)
    {
        this.privateKeystorePasswd=passwd; 
    }
    
    public void updatePrivateKey(Secret passwd) throws Exception 
    {
        try
        {
            if (this.sslCtxManager==null)
            {
                // Update with preloaded certificates cacerts from ~/.vletrc/cacerts: 
                CertificateStore certs = vrsContext.getConfigManager().getCertificateStore(); 
              
                String cacertsPath=certs.getKeyStoreLocation(); 
                Properties props=new Properties(); 
                props.put(SSLContextManager.PROP_CACERTS_LOCATION, cacertsPath); 
                this.sslCtxManager=new SSLContextManager(new Properties());
                                
                //default:
                this.sslCtxManager.setProperty(SSLContextManager.PROP_CACERTS_LOCATION,cacertsPath); 
            }
            
            // Update, location could have changed by GUI! 
            this.sslCtxManager.setProperty(SSLContextManager.PROP_CREDENTIALS_GRID_PROXY_FILE,this.getProxyFilename());
           
            // Update SSLContext with new KeyManager which contains private Key ! 
            PrivateKey privKey=this.getProvider().getUserPrivateKey(passwd);
            Certificate userCert = this.getUserCertificate(); 
            
            sslCtxManager.setPrivateKey(privKey,new Certificate[]{userCert},this.privateUserKeyAlias,this.privateKeystorePasswd); 
            // sslMan.setPrivateKeystore(keyStore,privateUserKeyAlias,privateKeystorePasswd); 

            // use manager to initialize context: 
            sslCtxManager.initSSLContext();
            SSLContext sslContext=sslCtxManager.getContext(); 
            
            {
                // String errorTxt="FIXME, cannot set default HTTPSSslContext\n";
                // logger.logException(ClassLogger.FATAL,new Exception(errorTxt),"Fatal:%s\n",errorTxt); 
                // Update Default SSL Context for URL handlers to use !
                VrsSslUtil.setDefaultHttpsSslContext(sslContext);
            }
             
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR,e,"Couldn't initialize user key\n"); 
            throw e; 
        }
    }

    /**
     * Convert proxy into password protected keystore including the proxy certificate chain.
     */ 
    public KeyStore getProxyAsKeystore(String keyAlias,Secret passwd) throws VrsException
    {
        if (this.credential==null)
            return null;
        
        try
        {           
            PrivateKey privKey = this.credential.getProxyPrivateKey(); 
            Certificate[] certChain = this.credential.getProxyCertificateChain();
            
            // Use CertificateStore as KeyStore factory 
            
            CertificateStore certStore = CertificateStore.createInternal(passwd);  
            certStore.addUserPrivateKey(keyAlias,privKey,certChain); 
            
            return certStore.getKeyStore(); 
        }
        catch (Exception e)
        {
            throw new VrsException(e.getMessage(),e); 
        }

    }

	public void load(String path) throws VrsException
	{
		// load default specifed by provider: 
        VGridCredentialProvider prov = _getProvider(); 
            
        if (prov==null)
            throw new InitializationException("No Credential Provider has been registered"); 
       
        loadProxy(path); 
	}
	
	/** Resolve VO and return VO info. Delegates to registered provider.  */ 
	public VO findVO(String name) throws VrsException
	{
	    try
	    {
    		VGridCredentialProvider provider;
    		// Used for testing purposes only: 
    		if ((provider=this._getProvider())==null)
    			throw new InitializationException("No Credential Provider Registered"); 
    	
    		return provider.getVO(name); 
	    }
	    catch(Exception e)
	    {
	        throw new VrsException(e.getMessage(),e); 
		}
	}
 
	public void checkImportVomsServerCertificates() throws VrsException
	{
		VGridCredentialProvider provider;
     
		// Used for testing purposes only: 
		if ((provider=this._getProvider())==null)
			throw new InitializationException("No Credential Provider Registered"); 

		String vo=provider.getDefaultVOName(); 
		checkImportVomsServerCertificates(vo); 
	}
	
	/** 
	 * Helper method which contact all VOMS servers for current VO and import
	 * VOMS server certificate. 	
	 */
	public void checkImportVomsServerCertificates(String voName) throws VrsException
	{
		VO vo=findVO(voName);
		if (vo==null)
			throw new VrsException("Couldn't find any VO information for:"+voName); 
		
		VOServer[] servers = vo.getServers(); 
		if (servers==null) 
			throw new VrsException("No VOMS server configuration found for:"+voName); 
		
		// check servers 
		for (VOServer serv:servers)
		{
			String host=serv.getHostname(); 
			int port=serv.getPort(); 
			
			logger.debugPrintf("Checking VOMS server for vo=%s,server=%s:%d", voName,host,port); 
			
			try 
			{
	            VrsSslUtil.interactiveImportCertificate(getVRSContext(),serv.getHostname(),serv.getPort());
			}
			catch (Exception e) 
			{
				throw new NestedIOException(e); 
			}
    	}
		
	}
	
	 // ========================================================================
    // Event methods 
    // ========================================================================

    public void addGridProxyListener(GridProxyListener listener)
    {
        listeners.add(listener);
    }

    public void removeGridProxyListener(GridProxyListener listener)
    {
        listeners.remove(listener);
    }

    protected void fireProxyValidityChange(boolean val)
    {
        if (listeners != null)
            for (GridProxyListener l : listeners)
                l.notifyProxyValidityChanged(this,val);
    }
    
    protected void fireVOValueChanged(String voInfo)
    {
        Attribute attrs[]=new Attribute[1]; 
        attrs[0]=new Attribute(ATTR_VOINFO,voInfo);
        
        if (listeners != null)
            for (GridProxyListener l : listeners)
                l.notifyProxyAttributesChanged(this,attrs); 
    }
    
    protected void fireCertsChanged(String alias)
    {
        if (listeners != null)
            for (GridProxyListener l : listeners)
                l.notifyCACertStoreUpdated(alias);   
    }
    
    protected void fireProxyValuesChanged(Attribute[] attrs)
    {
        if ((attrs==null) || (attrs.length<=0))
        {
            // Grr Arggg. 
            logger.debugPrintf("fireProxyValuesChanged: *** NULL attributes***\n");
            return; 
        }
            
        if (listeners != null)
            for (GridProxyListener l : listeners)
                l.notifyProxyAttributesChanged(this,attrs); 
    }

}
