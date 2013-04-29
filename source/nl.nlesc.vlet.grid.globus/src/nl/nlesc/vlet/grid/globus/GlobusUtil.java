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

package nl.nlesc.vlet.grid.globus;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;

import org.globus.gsi.GlobusCredential;
import org.globus.gsi.OpenSSLKey;
import org.globus.gsi.bc.BouncyCastleOpenSSLKey;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.exception.AuthenticationException;
import nl.nlesc.vlet.exception.UnknownCAException;
import nl.nlesc.vlet.grid.proxy.GridProxy;
import nl.nlesc.vlet.grid.proxy.VGridCredential;
import nl.nlesc.vlet.vrs.VRSContext;

public class GlobusUtil
{
    // static initializer: 
    static
    {
        GlobusCredentialProvider.init();
        
        // Use GSI StreamHandler Factory 
        //VRLStreamHandlerFactory factory = VRLStreamHandlerFactory.getDefault(); 
        //factory.setHTTPGUrlHandlerClass(GlobusHTTPSHandler.class); 
    }
    
    public static void init()
    {
        // dummy method, but will trigger static {...} code ! 
    }
    
    /** 
     * Inspect Globus Exception. 
     * Returns nested VlException which provides extra information, if recognized. 
     * Returns NULL of no usefull exception can be returned.
     */ 
    public static VrsException checkException(String message, Throwable cause)
    {
        Throwable originalCause=cause; 
        Throwable prevCause=null; 
        
        while ((cause!=null) && (prevCause!=cause))
        { 
            prevCause=cause; 
            
            //Global.infoPrintf(GlobusUtil.class,"checkException: inspecting:%s\n",cause); 
            
            // ===
            // Inspect Cause:
            // === 
            
            String msgstr=cause.getMessage();
            if (msgstr==null)
                msgstr="";
            
            msgstr=msgstr.toLowerCase();
                
            if (cause instanceof org.globus.gsi.gssapi.GlobusGSSException)
            {
                if ((msgstr!=null) && (msgstr.contains("unknown ca")))
                    return new UnknownCAException(message+"\nReason=Unknown CA.\n"
                            +"Update your system certificates or copy the CA root certificate(s) to '"+VletConfig.getUserConfigDir()+"/certificates'."
                            ,originalCause); 
            }
                

            // === 
            // Next Cause in Exception Stack
            // Important: Globus has it's own Exeption Chaining methods !
            // (Because pre java 1.6 IOExceptions didn't supported Chained Exceptions!) 
            // === 
            
            // New Exception: Detect Unknown CA Exceptions !
            if (cause instanceof org.globus.common.ChainedIOException)
            {
                // Globus has it's own Exception Chaining!
                // Dereference:
                Throwable cause2 = ((org.globus.common.ChainedIOException)cause).getException();  
                
                if (cause2!=null)
                   cause=cause2;
            }
            else if (cause instanceof org.globus.common.ChainedException)
            {
                // Globus has it's own Exception Chaining!
                // Dereference:
                Throwable cause2 = ((org.globus.common.ChainedException)cause).getException();  
                
                if (cause2!=null)
                   cause=cause2;
            }
            else
            {
                // Next: 
                cause=cause.getCause(); 
            }
         
        }
        
        return null; 
    }
    
    public static GlobusCredential getGlobusCredential(GridProxy proxy)
    {   
        GlobusCredentialWrapper credWrapper=(GlobusCredentialWrapper)proxy.getCredential(GridProxy.GLOBUS_CREDENTIAL_TYPE);
        
        if (credWrapper==null)
            return null;
        
        return credWrapper.getGlobusCredential();
    }
    
    /** 
     * If Grid Proxy represents a GSS Credential, return it.
     * To create a GSS Credential from a non GSS Credential, use createGSSCredential().
     * A Globus Proxys is NOT a GSS Credential, but can be used to create one.  
     */
    public static GSSCredential getGSSCredential(GridProxy proxy)
    {   
        GSSCredentialWrapper credWrapper=(GSSCredentialWrapper)proxy.getCredential(GridProxy.GSS_CREDENTIAL_TYPE);
        
        if (credWrapper==null)
            return null;
        
        return credWrapper.getGSSCredential();
    }
    
    public static GridProxy createGridProxy(VRSContext vrsContext, GlobusCredential gc)
    {
        GridProxy proxy=new GridProxy(vrsContext); 
        
        GlobusCredentialProvider provider=GlobusCredentialProvider.getDefault(); 
        GlobusCredentialWrapper credWrap=new GlobusCredentialWrapper(provider,gc);
        
        // update 
        proxy.setCredential(credWrap); 
        
        return proxy; 
        
    }
    
    public static GridProxy createGridProxy(VRSContext context, String proxyStr) throws Exception
    {
        GridProxy proxy=new GridProxy(context); 

        GlobusCredentialProvider provider=GlobusCredentialProvider.getDefault(); 
        VGridCredential credWrap = provider.createCredentialFromString(proxyStr); 

        proxy.setCredential(credWrap);
        
        return proxy; 
        
    }
    
    
    /**
     * Decode userkey.pem and return it. 
     * <pre>
     * *** WARNING *** this return the DECRYPTED key. do not store or kep it around in memory. 
     * </pre>
     * @throws Exception 
     */
    public static PrivateKey getPrivateKey(String filename,String passprut) throws Exception
    {
        // X509Certificate userCert = CertUtil.loadCertificate(this.getDefaultUserCertLocation());
        OpenSSLKey key = new BouncyCastleOpenSSLKey(filename); 
        
        if(key.isEncrypted())
            try
            {
                key.decrypt(passprut);
            }
            catch(GeneralSecurityException e)
            {
                throw new Exception("Wrong password or other security error");
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
            GlobusGSSCredentialImpl gssCred = createGSSCredential( ((GlobusCredentialWrapper)sourceCred).getGlobusCredential());
            return gssCred; 
        }
        else
        {
            throw new Exception("Cannot convert non GlobusCredentials to GSS Credentials:"+sourceCred.getClass()); 
        }
    }

    
}
