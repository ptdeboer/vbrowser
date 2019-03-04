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


import java.io.IOException;
import java.net.InetAddress;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;


import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vlet.net.ssl.SSLContextManager;

import org.apache.axis.MessageContext;
import org.apache.axis.components.net.BooleanHolder;
import org.apache.axis.transport.http.HTTPSender;
import org.apache.axis.transport.http.SocketHolder;

/**
 * SSLHTTPSender uses configurable SSLContextManager to create custom SSLContexts.  
 * Currently needed for Glite SSL setup. 
 */ 
public class SSLHTTPSender extends HTTPSender
{
    // ======================================================================== //
    
    private static final long serialVersionUID = -5297557587240691388L;

    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(SSLHTTPSender.class);
        //logger.setLevelToDebug();
    }
    
    // ======================================================================== //
    
    // ======================================================================== //
    
    private Properties sslConfig;
    private SSLContextManager contextWrapper;
    private SSLSocketFactory socketFactory;

    public SSLHTTPSender(Properties properties) throws Exception
    {
        sslConfig = null;
        sslConfig = properties;
        contextWrapper = new SSLContextManager(sslConfig);

        // re-initialize context with new proxy credentials 
        contextWrapper.initSSLContext(); 
        socketFactory = contextWrapper.getSocketFactory();
    }

    protected void getSocket(SocketHolder sockHolder, MessageContext msgContext, String protocol, String host, int port, int timeout, StringBuffer otherHeaders, 
            BooleanHolder useFullURL)
        throws IOException, GeneralSecurityException, Exception
    {
        logger.debugPrintf("Connecting to: %s:%d\n",host,port); 
        
        if(protocol.equalsIgnoreCase("https"))
        {
            SSLSocket socket;
           
            if(timeout >= 0)
            {
                socket = (SSLSocket)socketFactory.createSocket(host,port);
                socket.setSoTimeout(timeout);
            }
            else
            {
                socket = (SSLSocket)socketFactory.createSocket(InetAddress.getByName(host), port);
            }
            
            // set protocols
            {
                socket.setEnabledProtocols(new String[]{
                    contextWrapper.getContext().getProtocol()
                    });
            }
            
            socket.setUseClientMode(true);
            sockHolder.setSocket(socket);
        } 
        else
        {
            super.getSocket(sockHolder, msgContext, protocol, host, port, timeout, otherHeaders, useFullURL);
        }
    }


}
