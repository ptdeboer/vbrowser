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

package nl.nlesc.vlet.vrs.vdriver.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Proxy;

import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.VResourceSystem;
import nl.nlesc.vlet.vrs.io.VStreamProducer;
import nl.nlesc.vlet.vrs.vrl.VRL;

public class HTTPRS implements VResourceSystem, VStreamProducer
{
    public static final String DEFAULT_HTTPRS_SERVERID = "httprs";

    static public synchronized HTTPRS getClientFor(VRSContext context, ServerInfo info, VRL location)
            throws VlException
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
    public VNode openLocation(VRL location) throws VlException
    {
        return new HTTPNode(this, location);
    }

    public InputStream createInputStream(VRL location) throws VlException
    {
        try
        {
            return new HTTPNode(this, location).createInputStream();
        }
        catch (IOException e)
        {
            throw new VlIOException(e); 
        }
    }
    
    public OutputStream createOutputStream(VRL location) throws VlException
    {
        try
        {
            return new HTTPNode(this, location).createOutputStream();
        }
        catch (IOException e)
        {
          throw new VlIOException(e); 
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
        return sourceVrl.resolve(path);
    }

}
