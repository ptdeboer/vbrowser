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

package nl.esciencecenter.vlet.grid.ssl;

import java.util.Properties;

import nl.esciencecenter.ptk.ssl.CertificateStore;
import nl.esciencecenter.vlet.net.ssl.ExtSSLSocketFactory;
import nl.esciencecenter.vlet.net.ssl.SSLContextManager;
import nl.esciencecenter.vlet.vrs.VRSContext;

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
