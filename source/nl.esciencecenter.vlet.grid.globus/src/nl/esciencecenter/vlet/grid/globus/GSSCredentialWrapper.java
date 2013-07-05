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

import java.security.PrivateKey;
import java.security.cert.Certificate;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.grid.proxy.GridProxy;
import nl.esciencecenter.vlet.grid.proxy.VGridCredential;
import nl.esciencecenter.vlet.grid.proxy.VGridCredentialProvider;

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
    public boolean saveCredentialTo(String path) throws VrsException
    {
        throw new nl.esciencecenter.vlet.exception.NotImplementedException("Can't save a GSS Credential"); 
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
    public Certificate[] getProxyCertificateChain() throws VrsException
    {
        return null;
    }


    @Override
    public PrivateKey getProxyPrivateKey() throws VrsException
    {
        return null;
    }

}
