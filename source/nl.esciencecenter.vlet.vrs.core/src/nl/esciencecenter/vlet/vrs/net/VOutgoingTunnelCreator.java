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

package nl.esciencecenter.vlet.vrs.net;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

public interface VOutgoingTunnelCreator
{
    /**
     * Create outgoing tunnel to remote host and port. Returned port number
     * is the port to connect to on the local host. 
     * Typically this is implemented by a ResourceSystem, like the SftpResouceSystem. 
     * 
     * @param remoteHost remote host to connect to on the other end of the resource.
     * @param remotePort remote port to connect to on the other end of the resource.
     * @return local port 
     * @throws VrsException
     */
    public int createOutgoingTunnel(String remoteHost, int remotePort) throws VrsException; 
    
}
