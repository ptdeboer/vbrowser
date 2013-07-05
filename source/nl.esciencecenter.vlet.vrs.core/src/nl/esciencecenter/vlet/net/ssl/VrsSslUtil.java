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

package nl.esciencecenter.vlet.net.ssl;

import java.net.InetSocketAddress;
import java.net.URLStreamHandler;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import nl.esciencecenter.ptk.ssl.CertificateStore;
import nl.esciencecenter.ptk.ssl.ImportCertificates;
import nl.esciencecenter.ptk.ssl.SslUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.vrs.VRSContext;

/**
 * SslUtil class for all global SSL configuration methods.
 */
public class VrsSslUtil extends SslUtil
{
    private static ClassLogger logger;
    
    static
    {
        logger = ClassLogger.getLogger(VrsSslUtil.class);
    }
    
    /** 
     * Dummy Trustmanager. Use this one to accept all connections.  
     */
    protected static class DummyTrustManager implements javax.net.ssl.X509TrustManager
    {
        public java.security.cert.X509Certificate[] getAcceptedIssuers()
        {
            return null;
        }

        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType)
        {
        }

        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType)
        {
        }
    }

    public static void setDefaultHttpsSslContext(SSLContext context)
    {
        // Might not work in custom http/https context: 
        // Check web service compatibility here: 
        try
        {
            sun.net.www.protocol.https.HttpsURLConnectionImpl.setDefaultSSLSocketFactory(new ExtSSLSocketFactory(
                    context, context.getSocketFactory()));
            // sun.net.www.protocol.https.HttpsURLConnectionImpl.setDefaultSSLSocketFactory(context.getSocketFactory());
            // sun.net.www.protocol.https.HttpsURLConnectionImpl.setDefaultAllowUserInteraction(true);
        }
        catch (Throwable e)
        {
            logger.logException(ClassLogger.ERROR,e,"Failed to initialize SSLSocketFactory\n");
        }
    }

    // com.sun.net.ssl.internal.www.protocol.https.HttpsURLConnectionImpl.setDefaultHostnameVerifier(hv);

    /**
     * Create HttpsHandler which conforms to the classes used in the above
     * methods
     */
    public static URLStreamHandler createHttpsHandler()
    {
        // return class same as initialized above!
        return new sun.net.www.protocol.https.Handler();
    }

    public static SSLSocket openSSLv3SocketNoR4(SSLContext context, String host, int port, int timeout)
            throws KeyStoreException, NoSuchAlgorithmException, CertificateException, VrsException, Exception
    {
        SSLSocketFactory sslFactory = context.getSocketFactory();

        int i;

        logger.debugPrintf("create(%s:%d)", host, port);

        // create SSL socket
        SSLSocket socket = (SSLSocket) sslFactory.createSocket();

        // enable only SSLv3
        socket.setEnabledProtocols(new String[]
        { "SSLv3" }); // SSLv2Hello, SSLv3,TLSv1
        // enable only ciphers without RC4 (some bug, probably in older globus)
        String[] ciphers = socket.getEnabledCipherSuites();
        ArrayList<String> al = new ArrayList<String>(ciphers.length);
        for (i = 0; i < ciphers.length; i++)
        {
            if (ciphers[i].indexOf("RC4") == -1)
                al.add(ciphers[i]);
        }
        socket.setEnabledCipherSuites((String[]) al.toArray(new String[al.size()]));
        // connect as client
        socket.setUseClientMode(true);

        if (timeout < 0)
            timeout = 10000;

        socket.setSoTimeout(timeout); // read timeout
        socket.connect(new InetSocketAddress(host, port), 3000); // connect
                                                                 // timeout

        return socket;
    }
    
    public static void interactiveImportCertificate(VRSContext context,String host,int port) throws Exception
    {
        CertificateStore certStore=context.getConfigManager().getCertificateStore();
        // check+install certificate: 
        ImportCertificates.interactiveImportCertificate(certStore,host,port); 
        
    }
    
}
