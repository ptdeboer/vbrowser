/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.nlesc.vlet.vrs.vrms;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.data.BooleanHolder;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.ssl.CertificateStore;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.exception.InitializationException;
import nl.nlesc.vlet.vrs.VRSContext;

/** 
 * The Configuration Manager is a VRSContext aware configuration manager. 
 * It checks global settings against VRSContext settings and provides
 * more automagical configuration. 
 * The idea is to use the ConfigManager post VRSContext() initialization. <br>
 * For pre VRSContext and global settings, see: {@link nl.nlesc.vlet.VletConfig}. 
 * <p> 
 * @author Piter T. de Boer 
 */
public class ConfigManager
{
    private static ClassLogger logger;
    
//	/** Property configuration from VLET_INSTALL/etc/vletrc.prop */ 
//    public static int CONFIG_INSTALL=0x01<<0;
//    
//    /** Property configuration from environment variable ${ENV_VAR}  */ 
//    public static int CONFIG_ENVVAR =0x01<<1;
//    
//    /** Property configuration from ~/.vletrc/vletrc.prop */ 
//    public static int CONFIG_USER   =0x01<<2;
//    
//    /**
//     * Property configuration from System.getProperty() 
//     * (specified on command line at startup) 
//     */ 
//    public static int CONFIG_SYSTEM =0x01<<3; 
//    
//    /** Property configuration from VRSContext. Set programmatically */ 
//    public static int CONFIG_CONTEXT=0x01<<4; 

    static
    {
        logger=ClassLogger.getLogger(ConfigManager.class);
    }

    /** Simple HOSTNAME:PORT container */ 
    public static class HostPortInfo
    {
        String host;
        int port; 
        
        public HostPortInfo(String hostStr,String portStr)
        {
            host=hostStr; 
            port=Integer.parseInt(portStr);  
        }
        
        public HostPortInfo(String hostPortStr,int defaultPort)
        {
            String strs[]=hostPortStr.split(":"); 
            this.host=strs[0];
            if (strs.length>1) 
                this.port=Integer.parseInt(strs[1]);
            else
                this.port=defaultPort; 
        }

         public String toString()
        {
            return host+":"+port; 
        }
        
        /** Parse a string containing a "host[:port],host[:port]" setting */   
        public static List<HostPortInfo> parse(String hostsStr,int defaultPort)
        {
            if ((hostsStr==null) || (hostsStr=="")) 
                return null;
            
            String strs[]=hostsStr.split("[,; \t]");
            ArrayList<HostPortInfo> infos=new ArrayList<HostPortInfo>(); 
            
            for (String str:strs)
            {
                // skip garbage and whitespace:
                if (StringUtil.isWhiteSpace(str)==false) 
                    infos.add(new HostPortInfo(str,defaultPort));
            }
            
            return infos; 
            
        }
    }
    // ========================================================================
    // Constructor/Initializors 
    // ========================================================================
    
    /** The VRSContext to manage! */ 
    private VRSContext vrsContext=null; 
    
    public ConfigManager(VRSContext vrsContext)
    {
        this.vrsContext=vrsContext; 
        init(); 
    }

    private void init()
    {
        initWebProxy(); 
    }
    
    private void initWebProxy()
    {
        String proxyHost=GlobalProperties.getStringProperty(VletConfig.HTTP_PROXY_HOST); 
        String proxyPort=GlobalProperties.getStringProperty(VletConfig.HTTP_PROXY_PORT);
        
        // String proxyEnabled=Global.getProperty(GlobalConfig.HTTP_PROXY_ENABLED);
        //        
        // if (StringUtil.isTrue(proxyEnabled)==false)
        //   return; 

        if (StringUtil.isEmpty(proxyHost))
            return;
        
        logger.warnPrintf("Setting GLOBAL http(s) proxy to:%s:%d\n",proxyHost,proxyPort); 
        
        // Enables GLOBAL PROXY SETTINGS ! 
        System.setProperty("http.proxySet","true"); 
        System.setProperty("http.proxyHost", proxyHost);
        if (StringUtil.isEmpty(proxyPort)) 
            proxyPort="3128"; 
        
        System.setProperty("http.proxyPort", proxyPort);
    }

    // ========================================================================
    // Initializers/Global configuration   
    // ========================================================================

    
    // ========================================================================
    // Generic Attribute get/set Property methods.  
    // ========================================================================
    
    /**
     * Set configuration attributes. Boolean holder redresh specifies
     * whether the configuration specific resources must be refreshed after setting the attribute. 
     * 
     */
    public boolean setAttribute(Attribute attr,BooleanHolder refresh)
    {
        if (attr==null)
            return false; 
        
        String name=attr.getName();
        String strval=attr.getStringValue(); 
        
        if (name==null)
            return false;
        
        if (refresh==null)
            refresh=new BooleanHolder(); 
        
        if (name.compareTo(VletConfig.PROP_INCOMING_FIREWALL_PORT_RANGE)==0)
        {
            refresh.value=true; 
            return setIncomingFireWallPortRange(strval); 
        }
        else if (name.compareTo(VletConfig.PROP_SKIP_FLOPPY_SCAN)==0)
        {
            vrsContext.setUserProperty(VletConfig.PROP_SKIP_FLOPPY_SCAN,attr.getStringValue());
            refresh.value=true; 
            return true; 
        }
        else if (name.compareTo(VletConfig.PROP_PASSIVE_MODE)==0)
        {
            refresh.value=true;  
            vrsContext.setUserProperty(VletConfig.PROP_PASSIVE_MODE,attr.getStringValue()); 
            return true; 
        }
        else if (name.compareTo(VletConfig.PROP_BDII_HOSTNAME)==0)
        {
            refresh.value=true;  
            vrsContext.setUserProperty(VletConfig.PROP_BDII_HOSTNAME,attr.getStringValue()); 
            return true; 
        }
        else if (name.compareTo(VletConfig.PROP_BDII_PORT)==0)
        {
            refresh.value=true;  
            vrsContext.setUserProperty(VletConfig.PROP_BDII_PORT,attr.getStringValue()); 
            return true; 
        }
        else if (name.compareTo(VletConfig.PROP_USER_CONFIGURED_VOS)==0)
        {
            refresh.value=true;  
            vrsContext.setUserProperty(VletConfig.PROP_USER_CONFIGURED_VOS,attr.getStringValue()); 
            return true; 
        }
        
        return false; 
    }

    public Attribute getAttribute(String name) throws InitializationException
    {
        Attribute attr=null; 
        
        if (name.compareTo(VletConfig.PROP_VLET_VERSION)==0)
            return new Attribute(name,VletConfig.getVletVersion());
        else if (name.compareTo(VletConfig.PROP_VLET_INSTALL)==0)
            return new Attribute(name,VletConfig.getInstallBaseDir().getPath());
        else if (name.compareTo(VletConfig.PROP_VLET_LIBDIR)==0)
            return new Attribute(name,VletConfig.getInstallationLibDir().getPath());
        else if (name.compareTo(VletConfig.PROP_VLET_SYSCONFDIR)==0)
            return new Attribute(name,VletConfig.getInstallationConfigDir().getPath());
        else if (name.compareTo(VletConfig.PROP_SKIP_FLOPPY_SCAN)==0)
        {
            attr=new Attribute(name,VletConfig.getBoolProperty(VletConfig.PROP_SKIP_FLOPPY_SCAN,true));
            attr.setEditable(true);
            //attr.setHelpText(MessageStrings.TXT_SKIP_FLOPPY_SCAN_WINDOWS);
            return attr; 
        }
        else if (name.compareTo(VletConfig.PROP_INCOMING_FIREWALL_PORT_RANGE)==0)
        {
            attr = new Attribute(name,VletConfig.getFirewallPortRangeString());
            attr.setEditable(true);
            //attr.setHelpText(MessageStrings.TXT_ALLOWED_INCOMING_PORTRANGE); 
            return attr; 
        }
        else if (name.compareTo(VletConfig.PROP_PASSIVE_MODE)==0)
        {
            attr = new Attribute(name,VletConfig.getPassiveMode());
            attr.setEditable(true);
            //attr.setHelpText(MessageStrings.TXT_GLOBAL_PASSIVE_MODE); 

            return attr; 
        }
        else if (StringUtil.equals(name,VletConfig.PROP_BDII_HOSTNAME))
        {
            attr=new Attribute(name,this.getBdiiHostInfo()); 
            attr.setEditable(true);
            return attr; 
        }
        else if (StringUtil.equals(name,VletConfig.PROP_BDII_PORT))
        {
            attr=new Attribute(name,this.getBdiiPort()); 
            attr.setEditable(true);
            return attr; 
        }
        else if (StringUtil.equals(name,VletConfig.PROP_USER_CONFIGURED_VOS))
        {
            attr=new Attribute(name,vrsContext.getStringProperty(VletConfig.PROP_USER_CONFIGURED_VOS));
            attr.setEditable(true);
            return attr; 
        }
        else 
        {
            String val=vrsContext.getStringProperty(name);
            if (val!=null) 
                return new Attribute(name,val); 
        }

        return null; 
        
    }
    
    /** 
     * Return BDII Infos as single comma seperated string 
     * @throws InitializationException */ 
    public String getBdiiHostInfo() throws InitializationException
    {
        List<HostPortInfo> infos = this.getBdiiServiceInfos();
        
        if ((infos==null) || (infos.size()<=0))
            return null; 
        
        String infoStr="";
        for (int i=0;i<infos.size();i++)
        {
            infoStr+=infos.get(i).host+":"+infos.get(i).port; 
            if (i+1<infos.size())
                infoStr+=",";
        }
        
        return infoStr; 
    }

    public Properties getUserProperties()
    {
        VRL loc = getUserPropertiesLocation();

        try
        {
            // Persistance Properties Always Reload ! 
            return VletConfig.staticLoadProperties(loc); 
        }   
        catch (VrsException e)
        {
            // handle(e); 
            logger.debugPrintf("Warning. Error when loading persistant properties:%s\n",e);
        }   

        return new Properties(); // return empty properties; 
    }

    public int getIntProperty(String name, int defaultVal)
    {
        return vrsContext.getIntProperty(name,defaultVal); 
    }
    
    // ========================================================================
    //    
    // ========================================================================

    /**
     * Returns list of BDII ServiceInfos.
     */ 
    public List<HostPortInfo> getBdiiServiceInfos() throws InitializationException
    {
        // ====================================================
        // Part (I): Check installation and System defaults !   
        // ====================================================

        String bdiiInfostr=null;
        int defaultPort=2170;
        
        // First fetch default values from installation vletrc.prop (can be NULL!) 
        bdiiInfostr = VletConfig.getInstallationProperty(VletConfig.PROP_BDII_HOSTNAME,bdiiInfostr);
       
        // ================================================================================
        // Allow for LFC_GFAL_INFOSYS environment variable! 
        // Check Environment LCG_GFAL_INFOSYS overrides Installation Configuration HERE: 
        // The environment variable overrides installation defaults, but can be overriden
        // by user configuration.!
        // =================================================================================
        String envstr=vrsContext.getSystemEnv(VletConfig.ENV_LCG_GFAL_INFOSYS);
        if (envstr!=null)
        {
            logger.infoPrintf("Using BDII information from environment variable %s=%s\n",VletConfig.ENV_LCG_GFAL_INFOSYS,envstr);
            // insert before defaults ! 
            bdiiInfostr=envstr;
        }
        
        // Now check user configuration  
        bdiiInfostr = VletConfig.getUserProperty(VletConfig.PROP_BDII_HOSTNAME,bdiiInfostr);
        
        // Now check System configuration  
        bdiiInfostr = VletConfig.getSystemProperty(VletConfig.PROP_BDII_HOSTNAME,bdiiInfostr);

        // Context overides system and user:    
        Object val = vrsContext.getProperty(VletConfig.PROP_BDII_HOSTNAME,false);
        if (val!=null)
            bdiiInfostr=val.toString();
        
        // ====================================================
        // Part (II): Parse user settings, insert before system defaults:  
        // ====================================================
        
        List<HostPortInfo> infos = HostPortInfo.parse(bdiiInfostr,defaultPort);
       
        // merge: 
//        int index=0;
//        if (infos!=null) 
//            for (HostPortInfo info:infos)
//                if (info!=null)
//                    bdiiInfos.add(index++,info);  //inserts before system settings, but keep order. 
        
        int index=0; 
        if (infos!=null) 
            for (HostPortInfo info:infos)
                logger.debugPrintf(" - using bdiiInfo[%d]=%s\n",index++,info);
        
        return infos;
    }
    
    public URI getBdiiServiceURI() throws InitializationException
    {
        List<HostPortInfo> infos = this.getBdiiServiceInfos();
        
        if ((infos==null) || (infos.size()<=0))
        {
            throw new nl.nlesc.vlet.exception.InitializationException("No BDII service information found!");
        }
        
        try
        {
            // get first:
            HostPortInfo bdii = infos.get(0); 
            URI geller = new URI("ldap://"+bdii.host+":"+bdii.port);
            return geller; 
        }
        catch (URISyntaxException e)
        {
            throw new InitializationException("Syntax Error. Cannot resolve BDII service information",e); 
        }
    }        


    public String getBdiiHost() throws InitializationException
    {
        return this.getBdiiServiceURI().getHost(); 
    }
    
    public int getBdiiPort() throws InitializationException 
    {
        return this.getBdiiServiceURI().getPort();  
    }
   
    /**
     * Configured User homeLocation.
     * By default returns Global UserHome 
     */ 
    public VRL getUserHomeLocation()
    {
        return VletConfig.getUserHomeLocation(); 
    }
    
    /**
     * Configured User configuration location. 
     * By default returns Global UserHome+".vletrc"
     */ 
    public VRL getUserConfigDir()
    {
        VRL homeLoc = getUserHomeLocation();
        return homeLoc.appendPath(VletConfig.USER_VLETRC_DIRNAME);
    }
    
    public boolean getUsePersistantUserConfiguration() 
    {
        // If Service Environment == true => default value must be false !
        // Although it can be overriden...
        boolean defVal=(isServiceEnvironment()==false);  
        
        return vrsContext.getBoolProperty(VletConfig.PROP_PERSISTANT_USER_CONFIGURATION,defVal);
    }
    
    public VRL getServerRegistryLocation()
    {
        return this.getMyVLeLocation().appendPath("serverreg.srvx"); 
    }

    /** 
     * Whether there is a UI available. This method is called by for example
     * the VBrowser.
     *  
     * @param val
     */
    public void setHasUI(boolean val)
    {
        VletConfig.setHasUI(val); 
    }

    /**
     * Whether user configuration should be saved, for example
     * the 'MyVle' working environment and server configurations. 
     * Set to FALSE in server environment !  
     */
    public boolean setPersistantUserConfiguration(boolean val)
    {
        boolean prev=this.getUsePersistantUserConfiguration(); 
        vrsContext.setProperty(VletConfig.PROP_PERSISTANT_USER_CONFIGURATION,val);
        return prev; 
    }
    
    /**
     * Static save properties method. Can be used during startup when the registry 
     * hasn't been properly intialized yet. 
     */
    public void saveUserProperties(Properties properties)
    {
        try
        {
            VletConfig.staticSaveProperties(getUserPropertiesLocation(), "VLET user properties:  vletrc.prop",properties);
        }   
        catch (Throwable e)
        {
            // when .vletrc hasn't been properly configured or this is a read only
            // enviroment, saving user properties might not always be possible. 
            logger.warnPrintf("Error when saving User Properties. Exception:%s",e); 
        }
    }
  
    /**
     * Set user settings and write to $HOME/.vletrc/vletrc.prop. 
     * If value is empty ("") or NULL the settings entry will be removed ! 
     */ 

    public void setUserProperty(String name,String value)
    {
        Properties props = getUserProperties();

        boolean clear=false;

        //null means unset property:
        if ((value==null) || (value.compareTo("")==0))
        {
            clear=true; 
            value="";
        }

        // Secure: nullify first (clear memory)
        props.setProperty(name,value);

        if (clear)
        {
            // remove empty object 
            props.remove(name);
        }

        saveUserProperties(props); 
    }

    public boolean setIncomingFireWallPortRange(String rangestr)
    {
        int range[]=VletConfig.portRange(rangestr,null);

        if ((range==null) || (range.length<2))
            // unset
            rangestr=""; 

        setUserProperty(VletConfig.PROP_INCOMING_FIREWALL_PORT_RANGE,rangestr);

        return true; 
    }
    
    /** Returns path to the User Properties: $HOME/.vletc/vletrc.prop */
    public VRL getUserPropertiesLocation()
    {
        return VletConfig.getUserPropertiesLocation(); 
    }
    
    /** Returns location of ~/.vletrc/myvle */ 
    public VRL getMyVLeLocation()
    {
        VRL homeLoc = vrsContext.getUserHomeLocation(); 
        return homeLoc.appendPath(VletConfig.USER_VLETRC_DIRNAME+URIFactory.SEP_CHAR_STR+MyVLe.MYVLE_SUBDIR_NAME);
    }

    public String getProperty(String name)
    {
        return this.vrsContext.getStringProperty(name);
    }

    /** @see nl.nlesc.vlet.VletConfig#isService() */ 
    public boolean isServiceEnvironment()
    {
        return VletConfig.isService(); 
    }

    /** Loads default ServerInfo Registry. */
    public void loadPersistantServerConfiguration()
    {
        vrsContext.getServerInfoRegistry().reload(); 
    }

    /**
     * Return TCP (socket) timeout in Milli Seconds(!)
     * This time is used as timeout parameter when creating a (TCP) socket
     * to a server. This is not the same as a Server Request timeout.
     * This value is only used when setting up a (tcp) connection for the first time.  
     */  
    public int getSocketTimeOut()
    {
        String value=getProperty(VletConfig.TCP_CONNECTION_TIMEOUT);
        try
        {
            if (value!=null)
                return Integer.parseInt(value);
        }
        catch(Throwable e)
        {
            e.printStackTrace(); 
        }
        return 30*1000; // default to 360 seconds; 
    }

    /** 
     * Return Server Request(s) timeout in Milli Seconds (!). 
     * Returns the timeout settings for how long a client will wait 
     * for a request. 
     * This is different then the TCP Socket time out as that value is only 
     * used when setting up a connection for the first time. 
     */ 
    public int getServerRequestTimeOut()
    {
        String value=getProperty(VletConfig.SERVER_REQUEST_TIMEOUT);
        try
        {
            if (value!=null)
                return Integer.parseInt(value);
        }
        catch(Throwable e)
        {
            e.printStackTrace(); 
        }
        return 30*1000; // default to 30 seconds; 
    }
    
    /** See: getHTTPProxy(boolean isHTTPS).
     * @see ConfigManager#getHTTPProxy(boolean) */  
    public Proxy getHTTPProxy()
    {
        return getHTTPProxy(false); 
    }
    
    /** See: getHTTPProxy(boolean isHTTPS).
     * @see ConfigManager#getHTTPProxy(boolean) */  
    public Proxy getHTTPSProxy()
    {
        return getHTTPProxy(true); 
    }
    
    /**
     * Always returns a Proxy Object. If no proxy has been defined
     * it returns a Proxy with type DIRECT, which mean a direct connection
     * and no Proxy. 
     * This way this method can always be used. 
     * @return
     */
    public Proxy getHTTPProxy(boolean isHTTPS)
    {
        String proxyHost;
        int proxyPort;
        Proxy proxy=null; 
        
        if (isHTTPS==false)
        {
            proxyHost=getHTTPProxyHost();
            proxyPort=getHTTPProxyPort();
            
            if (StringUtil.isEmpty(proxyHost)==true)
                proxy=Proxy.NO_PROXY; 
            else
                proxy=new Proxy(Type.HTTP, new InetSocketAddress(proxyHost,proxyPort));
        }
        else
        { 
            proxyHost=getHTTPSProxyHost();
            proxyPort=getHTTPSProxyPort();
                
            if (StringUtil.isEmpty(proxyHost)==true)
                proxy=Proxy.NO_PROXY; 
            else
                proxy=new Proxy(Type.HTTP, new InetSocketAddress(proxyHost,proxyPort));
        }
        
        return proxy;  
    }
   
    public int getHTTPProxyPort()
    {
        return vrsContext.getIntProperty(VletConfig.HTTP_PROXY_PORT,80); 
    }

    public String getHTTPProxyHost()
    {
        return vrsContext.getStringProperty(VletConfig.HTTP_PROXY_HOST);
    }
    
    public int getHTTPSProxyPort()
    {
        return vrsContext.getIntProperty(VletConfig.HTTPS_PROXY_PORT,80); 
    }

    public String getHTTPSProxyHost()
    {
        return vrsContext.getStringProperty(VletConfig.HTTPS_PROXY_HOST);
    }

    /** Whether to use SSH tunnel(s) for specified destination */ 
    public boolean getUseSSHTunnel(String scheme, String host,int port)
    {
        if (scheme==null) 
            return false;
        
        // use ssh.tunnel.<scheme>.default.port=<tunnel port> as default for that scheme.
        
        if (host==null)
            host="default";  
      
        // -1 and 0 are defaults. 
        if (port<0)
            port=0; 
        
        //return true; 
       return (vrsContext.getStringProperty(
               "ssh.tunnel."+scheme.toLowerCase()+"."+host.toLowerCase()+"."+port)!=null);
    }

    /** get local tunnel port for specified destination */ 
    public int getSSHTunnelLocalPort(String scheme, String remoteHost, int remotePort)
    {
        return 10000+remotePort; 
    }

    /**
     * Check wether a SSH tunnel configuration exists for the specified {scheme,host,port} resource. 
     * The name for the property is: <br> s
     * ssh.tunnel.${scheme}.${host}.${port} and the value is specifies is a hostname with optional port,
     * for example:<br>
     * ssh.tunnel.bdii.bdii.grid.sara.nl.2170=ui.grid.sara.nl
     * 
     */
    public String getSSHTunnelRemoteHost(String scheme, String host, int port)
    {
        //return "elab.science.uva.nl"; 
        return vrsContext.getStringProperty("ssh.tunnel."+scheme.toLowerCase()+"."+host.toLowerCase()+"."+port);   
    }

    /**
     * Return whether user interaction is allowed. 
     * This method check global settings and context depended settings. 
     * VRSContext settings override Global Settings.  
     * @see nl.nlesc.vlet.VletConfig#PROP_ALLOW_USER_INTERACTION PROP_ALLOW_USER_INTERACTION
     */   
	public boolean getAllowUserInteraction()
	{
		// get Explicit Global Settings: 
		boolean val=VletConfig.getBoolProperty(VletConfig.PROP_ALLOW_USER_INTERACTION,true);
		// use as default for Context settings: 
		return this.vrsContext.getBoolProperty(VletConfig.PROP_ALLOW_USER_INTERACTION,val); 
	}
 
	/**
	 * Set default value for GLOBAL (ALL) contexts. 
	 * This set the GLobal System property which can be overriden by the context setting. 
	 */  
	public void setGlobalAllowUserInteraction(boolean val)
	{
		VletConfig.setSystemProperty(VletConfig.PROP_ALLOW_USER_INTERACTION,""+val); 
	}
	
	/** 
	 * Specify whether user interaction is allowed only for this context.
	 * Setting overrides Global specification 
	 */ 
	public void setAllowUserInteraction(boolean val)
	{
		this.vrsContext.setProperty(VletConfig.PROP_ALLOW_USER_INTERACTION,""+val); 
	}

	CertificateStore certificateStore; 
	
	/** 
	 * Returns CertificateStore from (defaul) location ~/.vletrc/cacerts 
	 */ 
    public CertificateStore getCertificateStore() throws VrsException
    {
        String usercerts = VletConfig.getDefaultUserCACertsLocation();
        
        try
        {
            if (this.certificateStore==null)
            {
                certificateStore=CertificateStore.loadCertificateStore(usercerts, CertificateStore.DEFAULT_PASSPHRASE, true,true); 
    
                VRL[] certDirs = VletConfig.getCACertificateLocations(true);
                java.net.URL[] urls=new URL[certDirs.length];
                
                for (int i=0;i<certDirs.length;i++)
                    urls[i]=certDirs[i].toURL(); 
                
                certificateStore.setCustomCerticateDirectories(urls); 
                certificateStore.reloadCustomCertificates(); 
                certificateStore.setIsPersistant(true);
                
            }
        }
        catch (Exception e)
        {
            throw new VrsException("Failed to load/create CertificateStore:"+usercerts+"\n"
                    +e.getMessage(),e); 
        }
        
        return certificateStore;
    }

    public void addCACertificate(X509Certificate cert, boolean save) throws Exception
    {
        CertificateStore certstore = this.getCertificateStore(); 
        
        certstore.addCertificate(cert.getIssuerDN().getName(), cert, save);
        // synchronize Certificates with cacerts! 
        // make sure certstore and grid proxy from this context are the same.  
        this.vrsContext.getGridProxy().loadCertificates(); 
    }

    public String getProxyFilename()
    {
        return this.vrsContext.getGridProxy().getProxyFilename();
    }

    public String getUserCACertsLocation()
    {
        return VletConfig.getDefaultUserCACertsLocation(); 
    }

    public String getUserName()
    {
       return VletConfig.getUserName();
    }
    
}
