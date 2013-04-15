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

import java.security.PrivateKey;
import java.security.cert.Certificate;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.util.grid.GridProxy;
import nl.nlesc.vlet.util.grid.VGridCredential;
import nl.nlesc.vlet.util.grid.VGridCredentialProvider;

import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

public class GSSCredentialWrapper implements VGridCredential
{
    private GSSCredential credential;
    private GlobusCredentialProvider provider;
    
    public GSSCredentialWrapper(GlobusCredentialProvider provider, GlobusGSSCredentialImpl gssCred)
    {   
        this.credential=gssCred;
        this.provider=provider; 
    }


    @Override
    public String getIssuer()
    {
        return null; 
    }

    @Override
    public long getTimeLeft()
    {
        try
        {
            return credential.getRemainingLifetime();
        }
        catch (GSSException e)
        {
            return -1; 
            // e.printStackTrace();
        }
    }

    @Override
    public boolean isType(String credentialType)
    {
        return StringUtil.endsWith(credentialType,GridProxy.GSS_CREDENTIAL_TYPE);
    }

    @Override
    public boolean saveCredentialTo(String path) throws VlException
    {
        throw new nl.nlesc.vlet.exception.NotImplementedException("Can't save a GSS Credential"); 
    }

    @Override
    public String getUserDN()
    {
        return null;
    }

    @Override
    public String getCredentialFilename()
    {
        return null;
    }

    @Override
    public VGridCredentialProvider getProvider()
    {
        return this.provider;  
    }

    @Override
    public String getUserCertFile()
    {
        return null;
    }

    @Override
    public String getUserKeyFile()
    {
        return null;
    }

    @Override
    public String getUserSubject()
    {
        return null;
    }

    @Override
    public String getVOName()
    {
        return null;
    }
    
    @Override
    public String getVORole()
    {
        return null;
    }
    
    @Override
    public String getVOGroup()
    {
        return null;
    }

    public GSSCredential getGSSCredential()
    {
        return this.credential; 
    }


    @Override
    public Certificate[] getProxyCertificateChain() throws VlException
    {
        return null;
    }


    @Override
    public PrivateKey getProxyPrivateKey() throws VlException
    {
        return null;
    }

}
