package nl.nlesc.vlet.grid.ssl;

import java.util.Properties;

import nl.esciencecenter.ptk.ssl.CertificateStore;
import nl.nlesc.vlet.net.ssl.ExtSSLSocketFactory;
import nl.nlesc.vlet.net.ssl.SSLContextManager;
import nl.nlesc.vlet.vrs.VRSContext;

public class GssUtil
{

    public static ExtSSLSocketFactory createGSSSocketFactory(VRSContext context) throws Exception
    {
        // ---
        // try to create GSS compatible SSL Socket Factory.
        // ---

        Properties sslProps = new Properties();
        sslProps.setProperty(SSLContextManager.PROP_SSL_PROTOCOL, "SSLv3");
        // if identification is needed.
        sslProps.setProperty(SSLContextManager.PROP_USE_PROXY_AS_IDENTITY, "true");

        // sslProps.setProperty(nl.uva.vlet.grid.ssl.SSLContextWrapper.PROP_INIT_PROXY_PRIVATE_KEY,
        // "false");
        // sslProps.setProperty("axis.socketSecureFactory",
        // "org.glite.security.trustmanager.axis.AXISSocketFactory");
        // sslProps.setProperty(nl.uva.vlet.grid.ssl.SSLContextWrapper.PROP_INIT_PROXY_PRIVATE_KEY,
        // "false");

        // sslProps.setProperty("axis.socketSecureFactory",
        // "org.glite.security.trustmanager.axis.AXISSocketFactory");
        // if needed:
        String proxyFilename = context.getConfigManager().getProxyFilename();
        CertificateStore cacert = context.getConfigManager().getCertificateStore();

        sslProps.setProperty(SSLContextManager.PROP_CACERTS_LOCATION, cacert.getKeyStoreLocation());
        sslProps.setProperty(SSLContextManager.PROP_CREDENTIALS_GRID_PROXY_FILE, proxyFilename);

        // init context and return ssl factory.
        SSLContextManager ctxManager = new SSLContextManager(sslProps);
        ctxManager.initSSLContext();

        return ctxManager.getSocketFactory();
    }
}
