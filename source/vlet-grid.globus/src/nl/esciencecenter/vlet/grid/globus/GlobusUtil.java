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

import java.io.PrintStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;

import nl.esciencecenter.ptk.crypt.Secret;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.exception.AuthenticationException;
import nl.esciencecenter.vlet.exception.UnknownCAException;
import nl.esciencecenter.vlet.grid.proxy.GridProxy;
import nl.esciencecenter.vlet.grid.proxy.VGridCredential;
import nl.esciencecenter.vlet.vrs.VRSContext;

import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.X509ExtensionSet;
import org.globus.gsi.bc.BouncyCastleCertProcessingFactory;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;

public class GlobusUtil
{
    // static initializer:
    static
    {
        GlobusCredentialProvider.init();

        // Use GSI StreamHandler Factory
        // VRLStreamHandlerFactory factory =
        // VRLStreamHandlerFactory.getDefault();
        // factory.setHTTPGUrlHandlerClass(GlobusHTTPSHandler.class);
    }

    public static void init()
    {
        // dummy method, but will trigger static {...} code !
    }

    /**
     * Inspect Globus Exception. Returns nested VlException which provides extra
     * information, if recognized. Returns NULL of no usefull exception can be
     * returned.
     */
    public static VrsException checkException(String message, Throwable cause)
    {
        Throwable originalCause = cause;
        Throwable prevCause = null;

        while ((cause != null) && (prevCause != cause))
        {
            prevCause = cause;

            // Global.infoPrintf(GlobusUtil.class,"checkException: inspecting:%s\n",cause);

            // ===
            // Inspect Cause:
            // ===

            String msgstr = cause.getMessage();
            if (msgstr == null)
                msgstr = "";

            msgstr = msgstr.toLowerCase();

            if (cause instanceof org.globus.gsi.gssapi.GlobusGSSException)
            {
                if ((msgstr != null) && (msgstr.contains("unknown ca")))
                    return new UnknownCAException(message + "\nReason=Unknown CA.\n"
                            + "Update your system certificates or copy the CA root certificate(s) to '" + VletConfig.getUserConfigDir()
                            + "/certificates'."
                            , originalCause);
            }

            // ===
            // Next Cause in Exception Stack
            // Important: Globus has it's own Exeption Chaining methods !
            // (Because pre java 1.6 IOExceptions didn't supported Chained
            // Exceptions!)
            // ===

            // New Exception: Detect Unknown CA Exceptions !
            if (cause instanceof org.globus.common.ChainedIOException)
            {
                // Globus has it's own Exception Chaining!
                // Dereference:
                Throwable cause2 = ((org.globus.common.ChainedIOException) cause).getException();

                if (cause2 != null)
                    cause = cause2;
            }
            else if (cause instanceof org.globus.common.ChainedException)
            {
                // Globus has it's own Exception Chaining!
                // Dereference:
                Throwable cause2 = ((org.globus.common.ChainedException) cause).getException();

                if (cause2 != null)
                    cause = cause2;
            }
            else
            {
                // Next:
                cause = cause.getCause();
            }

        }

        return null;
    }

    public static GlobusCredential getGlobusCredential(GridProxy proxy)
    {
        GlobusCredentialWrapper credWrapper = (GlobusCredentialWrapper) proxy.getCredential(GridProxy.GLOBUS_CREDENTIAL_TYPE);

        if (credWrapper == null)
            return null;

        return credWrapper.getGlobusCredential();
    }

    /**
     * If Grid Proxy represents a GSS Credential, return it. To create a GSS
     * Credential from a non GSS Credential, use createGSSCredential(). A Globus
     * Proxys is NOT a GSS Credential, but can be used to create one.
     */
    public static GSSCredential getGSSCredential(GridProxy proxy)
    {
        GSSCredentialWrapper credWrapper = (GSSCredentialWrapper) proxy.getCredential(GridProxy.GSS_CREDENTIAL_TYPE);

        if (credWrapper == null)
            return null;

        return credWrapper.getGSSCredential();
    }

    public static GridProxy createGridProxy(VRSContext vrsContext, GlobusCredential gc)
    {
        GridProxy proxy = new GridProxy(vrsContext);

        GlobusCredentialProvider provider = GlobusCredentialProvider.getDefault();
        GlobusCredentialWrapper credWrap = new GlobusCredentialWrapper(provider, gc);

        // update
        proxy.setCredential(credWrap);

        return proxy;

    }

    public static GridProxy createGridProxy(VRSContext context, String proxyStr) throws Exception
    {
        GridProxy proxy = new GridProxy(context);

        GlobusCredentialProvider provider = GlobusCredentialProvider.getDefault();
        VGridCredential credWrap = provider.createCredentialFromString(proxyStr);

        proxy.setCredential(credWrap);

        return proxy;

    }

    /**
     * Decode userkey.pem and return it.
     * 
     * <pre>
     * *** WARNING *** this return the DECRYPTED key. do not store or keep it around in memory.
     * </pre>
     * 
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String filename, Secret passprhase) throws Exception
    {
        // X509Certificate userCert =
        // CertUtil.loadCertificate(this.getDefaultUserCertLocation());
        OpenSSLKey key = new BouncyCastleOpenSSLKey(filename);
        // String charSet="UTF-8";

        if (key.isEncrypted())
        {
            try
            {
                // byte[] bytes=passprhase.toByteBuffer("UTF-8").array();
                // key.decrypt(bytes);
                key.decrypt(new String(passprhase.getChars()));
            }
            catch (GeneralSecurityException e)
            {
                throw new Exception("Wrong password or other security error");
            }
        }

        java.security.PrivateKey userKey = key.getPrivateKey();
        return userKey;
    }

    public static GlobusGSSCredentialImpl createGSSCredential(GlobusCredential globusCred) throws AuthenticationException
    {
        return GlobusCredentialProvider.createGSSCredential(globusCred);
    }

    public static GSSCredential createGSSCredential(GridProxy proxy) throws Exception
    {
        VGridCredential sourceCred = proxy.getCredential();

        if (sourceCred instanceof GlobusCredentialWrapper)
        {
            GlobusGSSCredentialImpl gssCred = createGSSCredential(((GlobusCredentialWrapper) sourceCred).getGlobusCredential());
            return gssCred;
        }
        else
        {
            throw new Exception("Cannot convert non GlobusCredentials to GSS Credentials:" + sourceCred.getClass());
        }
    }

    public static void printCredential(GlobusCredential plainProxy, PrintStream out)
    {
        // PrivateKey privateKey = plainProxy.getPrivateKey();

        X509Certificate[] certChain = plainProxy.getCertificateChain();

        for (int i = 0; i < certChain.length; i++)
        {
            out.printf("--- Certificate [%d] ---\n", i);
            out.printf("%s\n", certChain[i].toString());
        }

    }

    /**
     * Change the type of the Globus Proxy Credential with optional new
     * ExtensionSet. Older implementations and for example VOMS enabled proxies
     * must be of of GSI_2_PROXY type.
     * 
     * @param proxy
     *            - original Globus Proxy
     * @param proxyType
     *            - new Proxy Type, for example 'legacy' type:
     *            <code>GSIConstants.GSI_2_PROXY</code>
     * @param extensions
     *            - X509ExtensionSet, if null extensions will be removed.
     * @return new changed GlobusCredential.
     * @throws GeneralSecurityException
     * @see GSIConstants
     */
    public static GlobusCredential changeGlobusProxy(GlobusCredential proxy, int proxyType, X509ExtensionSet extensions)
            throws GeneralSecurityException
    {
        BouncyCastleCertProcessingFactory factory = BouncyCastleCertProcessingFactory.getDefault();

        if (proxyType <= 0)
        {
            proxyType = GSIConstants.GSI_2_PROXY;
        }

        int timeLeft = (int) proxy.getTimeLeft();
        int numBits = proxy.getStrength();
        GlobusCredential newProxy = factory.createCredential(proxy.getCertificateChain(),
                proxy.getPrivateKey(),
                numBits,
                timeLeft,
                proxyType,
                extensions,
                null);

        return newProxy;
    }

    /**
     * Change the type of the Globus Proxy Credential with optional new
     * ExtensionSet. Older implementations and for example VOMS enabled proxies
     * must be of of GSI_2_PROXY type.
     * 
     * @param proxy
     *            - original Globus Proxy
     * @param proxyType
     *            - new Proxy Type, for example 'legacy' type:
     *            <code>GSIConstants.GSI_2_PROXY</code>
     * @param extensions
     *            - X509ExtensionSet, if null extensions will be removed.
     * @return new changed GlobusCredential.
     * @throws GeneralSecurityException
     * @see GSIConstants
     */

}
