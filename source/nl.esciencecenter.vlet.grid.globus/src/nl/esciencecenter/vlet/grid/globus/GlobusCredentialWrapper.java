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

package nl.esciencecenter.vlet.grid.globus;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.grid.proxy.GridProxy;
import nl.esciencecenter.vlet.grid.proxy.VGridCredential;
import nl.esciencecenter.vlet.grid.proxy.VGridCredentialProvider;
import nl.esciencecenter.vlet.grid.voms.VOMSAttributeCertificate;
import nl.esciencecenter.vlet.grid.voms.VomsUtil;

import org.bouncycastle.asn1.x509.AttributeCertificate;
import org.globus.gsi.CertUtil;
import org.globus.gsi.GlobusCredential;
import org.globus.util.Util;


/**
 * GlobusCredentail wrapper
 */
public class GlobusCredentialWrapper implements VGridCredential
{
    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(GlobusCredentialWrapper.class);
        // logger.setLevelToDebug();
    }

    private GlobusCredential credential = null;

    private GlobusCredentialProvider provider;

    private String userCertFile;

    private String userKeyFile;

    private String proxyFilename;

    public GlobusCredentialWrapper(GlobusCredentialProvider globusCredProvider, GlobusCredential globusCredential)
    {
        logger.infoPrintf("New GlobusCredential:%s\n", globusCredential.getSubject());

        this.credential = globusCredential;
        this.provider = globusCredProvider;
    }

    public String getVOName()
    {
        // try to parse VO information from Credential !
        ArrayList<VOMSInfo> vos = this.getVoInfoFromProxy();
        if ((vos != null) && (vos.size() > 0))
            return vos.get(0).vo;

        return null;
    }

    public String getVOGroup()
    {
        // try to parse VO information from Credential !
        ArrayList<VOMSInfo> vos = this.getVoInfoFromProxy();
        if ((vos != null) && (vos.size() > 0))
            return vos.get(0).group;

        return null;
    }

    public String getVORole()
    {
        // try to parse VO information from Credential !
        ArrayList<VOMSInfo> vos = this.getVoInfoFromProxy();
        if ((vos != null) && (vos.size() > 0))
            return vos.get(0).role;

        return null;
    }

    @Override
    public VGridCredentialProvider getProvider()
    {
        return this.provider;
    }

    @Override
    public String getIssuer()
    {
        return credential.getIssuer();
    }

    @Override
    public long getTimeLeft()
    {
        return this.credential.getTimeLeft();
    }

    @Override
    public boolean isType(String credentialType)
    {
        return StringUtil.equals(GridProxy.GLOBUS_CREDENTIAL_TYPE, credentialType);
    }

    @Override
    public boolean saveCredentialTo(String path) throws VrsException
    {
        try
        {
            java.io.File jFile = new java.io.File(path);

            if (jFile.isDirectory())
                throw new nl.esciencecenter.vlet.exception.ResourceAlreadyExistsException(
                        "Location is a directory, must be a file:" + path);

            OutputStream out = null;
            out = new FileOutputStream(path);

            credential.save(out);

            Util.setFilePermissions(path, 600);
            out.flush();
            out.close();

            // Update Path when successful:
            this.proxyFilename = path;

            return true;
        }
        catch (FileNotFoundException e)
        {
            throw new nl.esciencecenter.vlet.exception.ResourceNotFoundException("File creation error (File not found)."
                    + "While trying to save proxy to file:" + path, e);
        }
        catch (IOException e)
        {
            throw new NestedIOException("Couldn't write credential file to:" + path, e);
        }
    }

    @Override
    public String getUserDN()
    {
        return this.credential.getIdentity();
    }

    @Override
    public String getCredentialFilename()
    {
        return this.proxyFilename;
    }

    @Override
    public String getUserCertFile()
    {
        return this.userCertFile;
    }

    @Override
    public String getUserKeyFile()
    {
        return this.userKeyFile;
    }

    public String getUserSubject()
    {
        return CertUtil.toGlobusID(credential.getSubject());
    }

    /**
     * Try to get some Voms info from current credential. Since there can be
     * more then one VOMS attributes, return a list.
     */
    public ArrayList<VOMSInfo> getVoInfoFromProxy()
    {
        if (this.credential == null)
            return null;

        X509Certificate[] certs = credential.getCertificateChain();

        ArrayList<AttributeCertificate> vomsACs = VomsUtil.extractVOMSACs(certs);

        if ((vomsACs == null) || (vomsACs.size() <= 0))
            return null;

        // get first:
        VOMSAttributeCertificate vomsAC = new VOMSAttributeCertificate(vomsACs.get(0));

        ArrayList<VOMSInfo> vos = new ArrayList<VOMSInfo>();

        try
        {
            ArrayList<String> strs = vomsAC.getVOMSFQANs();
            for (String str : strs)
            {
                VOMSInfo info = VOMSInfo.parse(str);

                if (info != null)
                    vos.add(info);
            }
            return vos;
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.WARN, e, "Couldn't get VOMS Attributes from VOMS Certificate\n");
            e.printStackTrace();
            return null;
        }
    }

    public GlobusCredential getGlobusCredential()
    {
        return this.credential;
    }

    public void setUserKeyFile(String keyFile)
    {
        this.userKeyFile = keyFile;
    }

    public void setUserCertFile(String certFile)
    {
        this.userCertFile = certFile;
    }

    public void setProxyFile(String proxyFile)
    {
        this.proxyFilename = proxyFile;
    }

    @Override
    public Certificate[] getProxyCertificateChain() throws VrsException
    {
        return this.credential.getCertificateChain();
    }

    @Override
    public PrivateKey getProxyPrivateKey() throws VrsException
    {
        return this.credential.getPrivateKey();
    }

}
