package nl.esciencecenter.vlet.grid.globus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import nl.esciencecenter.ptk.crypt.Secret;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

import org.globus.gsi.CertUtil;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.X509ExtensionSet;
import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;
import org.globus.gsi.proxy.ext.ProxyCertInfo;
import org.globus.util.Util;

/**
 * Alternative Create Proxy Util. Based on ProxyInit from JGlobus.<br>
 * Use <code>GSIConstants.GSI_2_PROXY</code> to create legacy Globus Proxies.
 * 
 * @author Piter T. de Boer
 */
public class ProxyInit
{
    private static ClassLogger logger = ClassLogger.getLogger(ProxyInit.class);

    private PrivateKey userKey = null;

    protected X509Certificate[] userCertificates;

    protected int bits = 512;

    protected int lifetime = 3600 * 12;

    protected ProxyCertInfo proxyCertInfo = null;

    /**
     * Default type is "old" Globus Legacy proxy.
     */
    protected int proxyType = GSIConstants.GSI_2_PROXY;

    protected boolean debug = false;

    protected GlobusCredential proxy = null;

    /** 
     * Non static Globus certificate utility class. 
     */
    protected CertUtil certUtil;

    public ProxyInit()
    {
        certUtil = new CertUtil();
    }

    public X509Certificate getCertificate()
    {
        return this.userCertificates[0];
    }

    /**
     * @param bits
     *            - Size of proxy certificate in bits.
     */
    public void setBits(int bits)
    {
        this.bits = bits;
    }

    /**
     * @return size of proxy certificate in bits.
     */
    public int getBits()
    {
        return bits;
    }

    /**
     * @param lifetime - lifetime to be created proxy in seconds. 
     */
    public void setLifetime(int lifetime)
    {
        this.lifetime = lifetime;
    }

    public int getLifetime()
    {
        return lifetime;
    }

    /**
     * Specify Globus proxy type, for example
     * <code>GSIConstants.GSI_2_PROXY</code> for legacy globus proxies.
     * 
     * @see GSIConstants
     * @param proxyType
     */
    public void setProxyType(int proxyType)
    {
        this.proxyType = proxyType;
    }

    /**
     * Globus proxy type, for example <code>GSIConstants.GSI_2_PROXY</code> for
     * legacy globus proxies.
     * 
     * @see GSIConstants
     */
    public int getProxyType()
    {
        return proxyType;
    }

    public void setProxyCertInfo(ProxyCertInfo proxyCertInfo)
    {
        this.proxyCertInfo = proxyCertInfo;
    }

    public void setDebug(boolean debug)
    {
        this.debug = debug;
    }

    /**
     * Factory method to create globus proxy credentials. 
     * @param certFile
     *            - user public certificate file, for example "usercert.pem"
     * @param keyFile
     *            - user private key file, for example "userkey.pem"
     * @param passphrase
     *            - passphrase to decode private key file
     * @param verify
     *            verify user key and certificate file.
     * @param globusStyle
     *            - currently not used.
     * @param proxyFile
     *            - optional file to save new created proxy to.
     * @return Created GlobusCredential object.
     * @throws Exception
     */
    public GlobusCredential createProxy(String certFile,
            String keyFile,
            Secret passphrase,
            boolean verify,
            boolean globusStyle,
            String proxyFile) throws Exception
    {
        loadUserCertificates(certFile);
        loadUserKey(keyFile, passphrase);

        if (verify)
        {
            verify();
        }

        if (debug)
        {
            logger.infoPrintf("Using " + bits + " bits for private key\n");
        }

        logger.infoPrintf("Creating proxy, please wait...\n");

        create();

        logger.infoPrintf("Your proxy is valid until: %s\n", proxy.getCertificateChain()[0].getNotAfter());

        if (proxyFile == null)
        {
            logger.debugPrintf("NOT Saving proxy file \n");
        }
        else
        {
            saveTo(proxyFile);
        }

        return proxy;
    }

    private void saveTo(String proxyFile) throws IOException
    {
        logger.debugPrintf("Saving proxy to: %s\n", proxyFile);

        OutputStream out = null;

        try
        {
            File file = Util.createFile(proxyFile);
            // set read only permissions
            if (!Util.setOwnerAccessOnly(proxyFile))
            {
                logger.errorPrintf("Warning: Please check file permissions for your proxy file:%s!\n", proxyFile);
            }
            out = new FileOutputStream(file);
            // write the contents
            proxy.save(out);
        }
        catch (IOException e)
        {
            logger.errorPrintf("Failed to save proxy to file: %s!\n", proxyFile);
            throw e;
        }
        finally
        {
            if (out != null)
            {
                try
                {
                    out.close();
                }
                catch (Exception e)
                {
                    ; // ignore
                }
            }
        }
    }

    /**
     * Verify public and private key pair.
     * 
     * @throws Exception
     *             if public key and private key do no match.
     */
    public void verify() throws Exception
    {
        RSAPublicKey pkey = (RSAPublicKey) getCertificate().getPublicKey();
        RSAPrivateKey prkey = (RSAPrivateKey) userKey;

        if (!pkey.getModulus().equals(prkey.getModulus()))
        {
            throw new Exception("Certificate and private key specified do not match");
        }
    }

    public void loadUserCertificates(String arg) throws IOException, GeneralSecurityException
    {
        userCertificates = CertUtil.loadCertificates(arg);
    }

    public void loadUserKey(String file, Secret pwd) throws Exception
    {
        userKey = GlobusUtil.getPrivateKey(file, pwd);
    }

    public void create() throws GeneralSecurityException
    {
        BouncyCastleCertProcessingFactory factory = BouncyCastleCertProcessingFactory.getDefault();

        // No Extensions for legacy proxies:
        // Extensions can be used for example to create VOMS enabled proxies.
        X509ExtensionSet extSet = null;

        proxy = factory.createCredential(userCertificates,
                userKey,
                bits,
                lifetime,
                proxyType,
                extSet);
    }

}
