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

package nl.esciencecenter.vlet.grid.globus;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Vector;

import nl.esciencecenter.ptk.crypt.Secret;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.ssl.CertificateStore;
import nl.esciencecenter.ptk.ssl.CertificateStoreException;
import nl.esciencecenter.ptk.util.ResourceLoader;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.exception.AuthenticationException;
import nl.esciencecenter.vlet.exception.InternalError;
import nl.esciencecenter.vlet.grid.proxy.GridProxy;
import nl.esciencecenter.vlet.grid.proxy.VGridCredential;
import nl.esciencecenter.vlet.grid.proxy.VGridCredentialProvider;
import nl.esciencecenter.vlet.grid.voms.VO;
import nl.esciencecenter.vlet.grid.voms.VomsProxyCredential;
import nl.esciencecenter.vlet.grid.voms.VomsUtil;

import org.globus.common.CoGProperties;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.TrustedCertificates;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.tools.proxy.DefaultGridProxyModel;
import org.globus.tools.proxy.GridProxyModel;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

public class GlobusCredentialProvider implements VGridCredentialProvider
{
    // ==================
    // Static  
    // ==================

    private static ClassLogger logger;
    private static GlobusCredentialProvider instance=null;
    
    static
    {
        logger=ClassLogger.getLogger(GlobusCredentialProvider.class); 
        //logger.setLevelToDebug(); 
        
        // ======================
        // Cog-JGlobus-1.6/1.6 fix 
        // If not set explicitly, disable by default (unless explicitly turned on)  
        // =======================
        
        CoGProperties props = CoGProperties.getDefault();
        
        String val=props.getProperty(VletConfig.COG_ENFORCE_SIGNING_POLICY);
        
        if ((val==null) || (val.equals("")))
        {
            props.setProperty(VletConfig.COG_ENFORCE_SIGNING_POLICY,"false");
        }

        GridProxy.registerProvider(GridProxy.GLOBUS_CREDENTIAL_TYPE,getDefault());
        
        logger.infoPrintf("--- GlobusCredentialProvider initialized ---\n"); 
    }
    
    public static void init()
    {
        // trigger class initialization; 
    }

    public static GlobusCredentialProvider getDefault() 
    {
        if (instance==null)
            instance=new GlobusCredentialProvider(); 
        
         return instance;  
    }

    /** The Globus Grid Proxy Model is actually a static object ! */ 
    private static GridProxyModel staticModel; 
    
    
    // ==================
    // Instance   
    // ==================

    /** Cached Globus certificates */ 
    private TrustedCertificates trustedCertificates;
    
    private String defaultProxyFilename;
    
    private int defaultLifetime;
    
    private String defaultVOName;
    
    private String defaultVORole;
    
    private Boolean enableVoms;
    
    private String defaultUserCertFile;
        
    private String defaultUserKeyFile;
        
    private StringList rootCAdirectories;
    
    private boolean useUserCertificateStore=true; 
    
    public GlobusCredentialProvider()
    {
        initDefaults(); 
    }
    
    private void initDefaults()
    {
        // initialize default from Globus Proxy Model 
        staticModel=staticGetModel(false);
        CoGProperties props = staticModel.getProperties(); 
        
        this.defaultUserCertFile=props.getUserCertFile(); 
        this.defaultUserKeyFile=props.getUserKeyFile();
        this.defaultProxyFilename=props.getProxyFile();
        this.defaultLifetime=props.getProxyLifeTime(); 
     
        logger.infoPrintf("default userCertFile =%s\n",defaultUserCertFile); 
        logger.infoPrintf("default userKeyFile  =%s\n",defaultUserKeyFile); 
        logger.infoPrintf("default proxy file   =%s\n",defaultProxyFilename); 
        logger.infoPrintf("default lifetime     =%d\n",defaultLifetime); 
            
    }
    
    private static GridProxyModel staticGetModel()
    {
        return staticGetModel(false);
    }

    private static GridProxyModel staticGetModel(boolean usePKCS11Device)
    {
        if (staticModel != null)
            return staticModel;

        if (usePKCS11Device)
        {
            try
            {
                // Do We Need: PKCS11 ???
                Class<?> iClass = Class.forName(VletConfig.PKCS11_MODEL);
                staticModel = (GridProxyModel) iClass.newInstance();
            }
            catch (Exception e)
            {
                staticModel = new DefaultGridProxyModel();
            }
        }
        else
        {
            staticModel = new DefaultGridProxyModel();
        }
        return staticModel;
    }
    
    /** Load Globus Crendentials (= Grid Proxy) */ 
    public GlobusCredentialWrapper createCredentialFromFile(String path)
            throws Exception
    {
        try
        {
            GlobusCredentialWrapper cred=new GlobusCredentialWrapper(this,new GlobusCredential(path));
            // Update Path!
            cred.setProxyFile(path); 
            updateGlobusModelProxyfilepath(path); 
            
            return cred; 
        }
        catch (GlobusCredentialException e)
        {
            throw new AuthenticationException(e.getMessage(),e); 
        }
    }

    /**
     * This is a kludge: since some globus applications use the default proxy location, this
     * location needs to be updated in the default proxy staticModel.
     * This way an alternative loaded proxy will still be available as "default". 
     * This will only "remember" the latest location a proxy has been loaded from ! 
     * @param path
     */
    public void updateGlobusModelProxyfilepath(String proxyfilename)
    {
        CoGProperties props = staticGetModel().getProperties(); 
        props.setProxyLifeTime(this.getDefaultProxyLifetime());
        props.setProxyFile(proxyfilename); 
    }

    @Override
    public boolean canCreateCredentialType(String type)
    {
        if (StringUtil.equals(type,GridProxy.GLOBUS_CREDENTIAL_TYPE))
            return true;
        
        if (StringUtil.equals(type,GridProxy.GSS_CREDENTIAL_TYPE))
            return true; 
        
        return false; 
    }

    @Override
    public String[] getCredentialTypes()
    {
        String types[]=new String[2];
        
        types[0]=GridProxy.GLOBUS_CREDENTIAL_TYPE; 
        types[1]=GridProxy.GSS_CREDENTIAL_TYPE;

        return types; 
    }
    
    @Override
    public VGridCredential createFromObject(Object globusCredential) throws Exception
    {
        if ((globusCredential instanceof GlobusCredential)==false)
            throw new InternalError("Object is not of GlobusCredential Type!");
            
        return new GlobusCredentialWrapper(this, (GlobusCredential)globusCredential); 
    }

    /** Return default directory name which contains userkey and usercert */ 
    public String getDefaultUserKeyLocation()
    {
        if (this.defaultUserKeyFile!=null)
            return this.defaultUserKeyFile; 
     
        // return normalized path 
        return URIFactory.uripath(new DefaultGridProxyModel().getProperties().getUserKeyFile());   
    }
    
    /** Return default directory name which contains userkey and usercert */ 
    public String getDefaultUserCertLocation()
    {
        if (this.defaultUserCertFile!=null)
            return this.defaultUserCertFile;
        
        // return normalized path 
        return URIFactory.uripath(new DefaultGridProxyModel().getProperties().getUserCertFile());   
    }

    public int getDefaultLifetimeSeconds()
    {
        return getDefaultLifetime()*60*60; 
    }
    
    public int getDefaultLifetime()
    {
        return getDefaultProxyLifetime();
    }
    
    public int getDefaultProxyLifetime()
    {
        if (this.defaultLifetime>=0)
            return this.defaultLifetime; 
        
        return new DefaultGridProxyModel().getProperties().getProxyLifeTime();   
     }
    
    public synchronized void loadCertificates()
    {
        this.loadCertificates(this.rootCAdirectories.toArray());
    }

    public synchronized void loadCertificates(String caCertificateDirs[])
    {
        if (caCertificateDirs==null)
            return; 
        
        //
        // get CoG defaults:
        //
        Vector<X509Certificate> allCerts=new Vector<X509Certificate>();
        
        logger.debugPrintf(" +++ Loading Default (Globus) Certificates +++\n");

        try
        {
            TrustedCertificates defCerts = null;
            X509Certificate[] defXCerts=null;
            defCerts=TrustedCertificates.getDefault();
            defXCerts = defCerts.getCertificates();
         
            if (defXCerts!=null)
                for (X509Certificate cert:defXCerts)
                {
                    logger.debugPrintf(" + loaded default grid certificate: %s\n",cert.getSubjectDN());
                    allCerts.add(cert);
                }
        }
        // Bug in Globus! 
        catch (NullPointerException e)
        {
            logger.logException(ClassLogger.WARN,e,"Exception When calling TrustedCertificates.getDefault(): NullPointerException:\n"); 
        }
        

        logger.infoPrintf("Got #%d default certificates\n",allCerts.size());  
        
        //
        // Load VLET_INSTALL/etc/certificates 
        //
        
        for (String certPath:caCertificateDirs)
        {
            //Global.infoPrintf(this," + Checking extra certificates from:%s\n",certPath);
            
            // check path: avoid errors in eclipse: 
            File file=new File(certPath);
            if (file.exists())
            {
                logger.debugPrintf(" +++ Loading Extra Certificates from:%s +++\n",certPath);

                TrustedCertificates extraCerts = TrustedCertificates.load(certPath); 
                X509Certificate extraXCertsArr[] = extraCerts.getCertificates();

                if ((extraXCertsArr==null) || (extraXCertsArr.length<=0)) 
                {
                    logger.debugPrintf(" - No certificates found in: %s\n",certPath);
                }
                
                for (X509Certificate cert : extraXCertsArr)
                {
                    logger.debugPrintf(" + loaded extra certificate: %s\n",cert.getSubjectDN());
                    allCerts.add(cert); 
                }
            }
            else
            {
                logger.warnPrintf("***Warning: skipping non-exising certificate directory:%s\n",certPath);
            }
        }
        
        if (this.useUserCertificateStore)
        {
            try
            {
                logger.debugPrintf(" +++ Loading Java KeyStore 'cacerts' +++\n");
                
                CertificateStore cacerts=getCertStore(); 
                
                X509Certificate[] certs = cacerts.getX509Certificates();
                for (X509Certificate cert:certs)
                {
                    logger.debugPrintf(" + loaded extra CaCert : %s\n",cert.getSubjectDN());
                    allCerts.add(cert); 
                }
            }
            catch (Exception e)
            {
                ;
            }
        }
        
        X509Certificate[] newXCerts=new X509Certificate[allCerts.size()]; 
        newXCerts=allCerts.toArray(newXCerts);
        
        this.trustedCertificates=new TrustedCertificates(newXCerts); 
        
        TrustedCertificates.setDefaultTrustedCertificates(trustedCertificates);

        // Printout used certificates 
        // check: debug
        //
        TrustedCertificates certs = TrustedCertificates.getDefault(); // getDefaultTrustedCertificates();
        if (certs!=null)
        {
            X509Certificate[] xcerts = certs.getCertificates();

            for (X509Certificate xcert : xcerts)
            {
                logger.debugPrintf(" - using certificate: %s\n",xcert.getSubjectX500Principal());
            }
        }
        //
        // end debug
        // 
    }
    
    private CertificateStore getCertStore() throws CertificateStoreException
    {
        String cacertsLoc=VletConfig.getDefaultUserCACertsLocation(); 
        return CertificateStore.loadCertificateStore(cacertsLoc, new Secret(CertificateStore.DEFAULT_PASSPHRASE.toCharArray()), true,false); 
    }

    public X509Certificate[] getTrustedCertificates()
    {
        return this.trustedCertificates.getCertificates(); 
    }

    @Override
    public VGridCredential createCredentialFromString(String proxyString) throws Exception
    {
        // read string a bytes !
        ByteArrayInputStream inps;
        GlobusCredential credential;
        
        inps = new ByteArrayInputStream(proxyString.getBytes(ResourceLoader.DEFAULT_CHARSET));
        credential = new GlobusCredential(inps);
        return new GlobusCredentialWrapper(this,credential); 
    }

    public static GlobusGSSCredentialImpl createGSSCredential(GlobusCredential globusCred) throws AuthenticationException
    {
        GlobusGSSCredentialImpl cred;
        
        try
        {
            cred = new GlobusGSSCredentialImpl(
                    globusCred, GSSCredential.DEFAULT_LIFETIME);
            
            return cred;
        }
        catch (GSSException e)
        {
            throw new AuthenticationException(
                    "Couldn't create GSS Credential(s).\n"+e.getMessage(),e);
        }
    }

    @Override
    public boolean canCreateCredential(VGridCredential sourceCred, String type)
    {
        if (StringUtil.equals(GridProxy.GLOBUS_CREDENTIAL_TYPE,type))
                return true;

        if (StringUtil.equals(GridProxy.GSS_CREDENTIAL_TYPE,type))
                return true; 
        
        return false;
    }

    @Override
    public VGridCredential convertCredential(VGridCredential sourceCred, String type) throws Exception
    {
        if (sourceCred instanceof GlobusCredentialWrapper) 
        {  
            if (StringUtil.equals(GridProxy.GSS_CREDENTIAL_TYPE,type))
            {
                GlobusGSSCredentialImpl gssCred = createGSSCredential( ((GlobusCredentialWrapper)sourceCred).getGlobusCredential());
                return new GSSCredentialWrapper(this,gssCred); 
            }
        }
        
        return null; 
    }

    @Override
    public String getDefaultProxyFilename()
    {
        if (this.defaultProxyFilename!=null)
            return this.defaultProxyFilename;
        
        // return normalized path 
        return URIFactory.uripath(new DefaultGridProxyModel().getProperties().getProxyFile());   
    }

    @Override
    public boolean getEnableVoms()
    {
        if (this.enableVoms==null)
            return false;
     
        return this.enableVoms; 
    }

    @Override
    public void setDefaultProxyFilename(String filename)
    {
        this.defaultProxyFilename=filename; 
    }

    @Override
    public void setDefaultLifetime(int time)
    {
        this.defaultLifetime=time; 
    }

    @Override
    public void setDefaultVOName(String name)
    {
        this.defaultVOName=name;
    }
    
    @Override
    public void setDefaultVORole(String name)
    {
        this.defaultVORole=name;
    }

    @Override
    public void setEnableVoms(Boolean val)
    {
        this.enableVoms=val;
    }

    @Override
    public String getDefaultVOName()
    {
        return this.defaultVOName;
    }
    
    @Override
    public String getDefaultVORole()
    {
        return this.defaultVORole;
    }
    
    @Override
    public void setDefaultUserCertFile(String path)
    {
        this.defaultUserCertFile=path; 
    }

    @Override
    public void setDefaultUserKeyFile(String path)
    {
        this.defaultUserKeyFile=path; 
    }
    
    public GlobusCredentialWrapper createCredential(final Secret passwd) throws Exception
    {
        GlobusCredential credential;
        
        String proxyFile=this.getDefaultProxyFilename(); 
        String certFile=this.getDefaultUserCertLocation(); 
        String keyFile=this.getDefaultUserKeyLocation(); 
 
        int lifeTime=this.getDefaultLifetimeSeconds(); 
         
        ProxyInit init = new ProxyInit();
    
        int bits = 512;
        int proxyType = GSIConstants.GSI_2_PROXY; 
    
        CertUtil.init();

        init.setBits(bits);
        init.setLifetime(lifeTime);
        init.setProxyType(proxyType);
        init.setProxyCertInfo(null);
        init.setDebug(true);

        credential=init.createProxy(certFile,
            keyFile,
            passwd,
            false,
            proxyFile);
    
        // vomsify 
        if (this.getEnableVoms())
        {
            // create new proxy and overwrite previous proxy. 
            VomsProxyCredential vomsCred = VomsUtil.vomsify(credential,
                    this.getDefaultVOName(),
                    this.getDefaultVOName(),
                    this.getDefaultVORole(),
                    this.getDefaultLifetime()*3600);  
                credential=vomsCred.getVomsProxy();
                
        }

        GlobusCredentialWrapper cred = new GlobusCredentialWrapper(this,credential);
            
        // update settings. This is the only place they are known:  
        cred.setUserKeyFile(keyFile); 
        cred.setUserCertFile(certFile); 
        cred.setProxyFile(proxyFile); 
        
        return cred; 
    }
    
    private GlobusCredentialWrapper createCredentialCoG(final Secret passwd) throws Exception
    {
        GridProxyModel staticModel = staticGetModel();
        GlobusCredential credential;
        
        String proxyFile=this.getDefaultProxyFilename(); 
        String certFile=this.getDefaultUserCertLocation(); 
        String keyFile=this.getDefaultUserKeyLocation(); 
        
            // Copy settings into static CoG properties 
        CoGProperties props = staticModel.getProperties(); 
        props.setProxyLifeTime(this.getDefaultProxyLifetime());
        props.setProxyFile(proxyFile); 
        props.setUserKeyFile(keyFile); 
        props.setUserCertFile(certFile);
        
        
        credential = staticModel.createProxy(new String(passwd.getChars()));
        
        // vomsify 
        if (this.getEnableVoms())
        {
            // create new proxy and overwrite previous proxy. 
            VomsProxyCredential vomsCred = VomsUtil.vomsify(credential,
                    this.getDefaultVOName(),
                    this.getDefaultVOName(),
                    this.getDefaultVORole(),
                    this.getDefaultLifetime()*3600);  
                credential=vomsCred.getVomsProxy();
                
        }

        GlobusCredentialWrapper cred = new GlobusCredentialWrapper(this,credential);
            
        // update settings. This is the only place they are known:  
        cred.setUserKeyFile(keyFile); 
        cred.setUserCertFile(certFile); 
        cred.setProxyFile(proxyFile); 
        
        return cred; 
    }
    


    @Override
    public List<String> getRootCertificateLocations()
    {
        return this.rootCAdirectories;
    }

    @Override
    public void setRootCertificateLocations(List<String> directories)
    {
        this.rootCAdirectories=new StringList(directories);  // Copy ?
        this.loadCertificates();
    }
    
    public VO getVO(String voName) throws Exception
    {
        return VomsUtil.getVO(voName);
    }
    
    public PrivateKey getUserPrivateKey(Secret passprhase) throws Exception
    {
        return this.getPrivateKey(this.getDefaultUserKeyLocation(),passprhase);
    }
    
    public PrivateKey getPrivateKey(String userkeyfile,Secret passprhase) throws Exception
    {
        return GlobusUtil.getPrivateKey(this.getDefaultUserKeyLocation(), passprhase);
    }
    
}
