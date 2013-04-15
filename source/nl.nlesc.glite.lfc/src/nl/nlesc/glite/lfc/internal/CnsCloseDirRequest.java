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

package nl.nlesc.glite.lfc.internal;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import nl.nlesc.glite.lfc.LFCServer;
import nl.nlesc.glite.lfc.Messages;




/**
 * Encapsulates LFC server CLOSEDIR command request. <br>
 * Then receives and returns response.
 * @see CnsCloseDirResponse  
 */
public class CnsCloseDirRequest extends AbstractCnsRequest {

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param output  stream that request will be written to
   * @param in      stream that response will be read from 
   * @return        Response object
   * @throws IOException in case of any I/O problem
   * @see CnsCloseDirResponse
   */
  public CnsCloseDirResponse sendTo( final DataOutputStream output,
                                     final DataInputStream in )
    throws IOException  {

    CnsCloseDirResponse result = new CnsCloseDirResponse();
    LFCServer.staticLogIOMessage( Messages.lfc_log_send_closedir ); 
    this.sendHeader( output,
                     CnsConstants.CNS_MAGIC2,
                     CnsConstants.CNS_CLOSEDIR,
                     12 );
    
    output.flush();
    result.readFrom( in );
    return result;
    
  }
}
