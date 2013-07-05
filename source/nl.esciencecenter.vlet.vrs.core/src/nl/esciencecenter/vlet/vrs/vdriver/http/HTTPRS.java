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

package nl.esciencecenter.vlet.vrs.vdriver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;

import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.VResourceSystem;
import nl.esciencecenter.vlet.vrs.io.VStreamProducer;

public class HTTPRS implements VResourceSystem, VStreamProducer
{
    public static final String DEFAULT_HTTPRS_SERVERID = "httprs";

    static public synchronized HTTPRS getClientFor(VRSContext context, ServerInfo info, VRL location)
            throws VrsException
    {
        // ===========================================
        // Only One HTTP/HTTPS Resource per Context for all hosts.
        // ===========================================
        String serverid = DEFAULT_HTTPRS_SERVERID;

        HTTPRS server = (HTTPRS) context.getServerInstance(serverid, HTTPRS.class);

        if (server == null)
        {
            // create new server
            server = new HTTPRS(context, info);

            // Hashtable is
            // servers.put(serverid,server);
            context.putServerInstance(server);
        }

        return server;
    }

    // ========================================================================
    //
    // ========================================================================

    private VRSContext vrsContext;

    private Proxy httpProxy;

    private Proxy httpsProxy;

    private VRL sourceVrl;

    public HTTPRS(VRSContext context, ServerInfo info)
    {
        this.vrsContext = context;
        this.sourceVrl=info.getServerVRL(); 
        // this.cache=new HTTPCache(context);
    }

    public String getID()
    {
        return DEFAULT_HTTPRS_SERVERID;
    }

    // @Override
    public VNode openLocation(VRL location) throws VrsException
    {
        return new HTTPNode(this, location);
    }

    public InputStream createInputStream(VRL location) throws VrsException
    {
        try
        {
            return new HTTPNode(this, location).createInputStream();
        }
        catch (IOException e)
        {
            throw new NestedIOException(e); 
        }
    }
    
    public OutputStream createOutputStream(VRL location) throws VrsException
    {
        try
        {
            return new HTTPNode(this, location).createOutputStream();
        }
        catch (IOException e)
        {
          throw new NestedIOException(e); 
        }
    }

    public VRSContext getVRSContext()
    {
        return this.vrsContext;
    }

    /**
     * Always return Proxy Object. If no proxy has been defined it returns a
     * Proxy.NO_PROXYtype which means no proxy. This way you can always use
     * getProxy();
     * 
     * @param isHTTPS
     * @return
     */
    public Proxy getHTTPProxy(boolean isHTTPS)
    {
        if (isHTTPS == false)
        {
            if (httpProxy == null)
                // check ServerInfo for this resource ?
                httpProxy = this.vrsContext.getConfigManager().getHTTPProxy();
            return httpProxy;
        }
        else
        {
            if (httpsProxy == null)
                // check ServerInfo for this resource ?
                httpsProxy = this.vrsContext.getConfigManager().getHTTPSProxy();
            return httpsProxy;
        }
    }

    @Override
    public void connect()
    {
    }

    @Override
    public void disconnect()
    {
    }

    @Override
    public void dispose()
    {
    }

    @Override
    public VRL getVRL()
    {
        return this.sourceVrl; 
    }

    @Override
    public VRL resolve(String path) throws VRLSyntaxException 
    {
        return sourceVrl.uriResolve(path);
    }

}
