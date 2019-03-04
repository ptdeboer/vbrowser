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

package nl.esciencecenter.glite.lfc.internal;

import java.io.DataOutputStream;
import java.io.IOException;

import nl.esciencecenter.glite.lfc.LFCServer;
import nl.esciencecenter.glite.lfc.Messages;




/**
 *  Encapsulates sending part of GSS context negotiations
 */
public class GSSTokenSend extends AbstractCnsRequest {
  private int magic = CnsConstants.CSEC_TOKEN_MAGIC_1;
  private int type = 0x1;
  private byte[] token;
  
  
  
  /**
   * Creates new GSS Token wrapper for LFC handshake
   * @param token wrapped token
   */
  public GSSTokenSend( final byte[] token ) {
   this.token = token;
  }

  /**
   * Creates new GSS Token wrapper for LFC handshake
   * @param magic overwrite Magic Number 
   * @param type overwrite Message Type
   */
  public GSSTokenSend( final int magic, final int type) {
   this.magic = magic;
   this.type = type;
  }
  
  /**
   * Creates new GSS Token wrapper for LFC handshake
   * @param magic overwrite Magic Number 
   * @param type overwrite Message Type
   * @param token wrapped token
   */
  public GSSTokenSend( final int magic, final int type, final byte[] token ) {
   this.magic = magic;
   this.type = type;
   this.token = token;
  }
  
  /**
   * Sets new magic number value
   * @param magic new magic number 
   */
  public void setMagic( final int magic ) {
    this.magic = magic;
  }

  /**
   * Sets new message type value
   * @param type new message type
   */
  public void setType( final int type ) {
    this.type = type;
  }

  /**
   * Sets new token byte array
   * @param token new token
   */
  public void setToken( final byte[] token ) {
    this.token = token;
  }

  /**
   * Send GSS Token to the output stream
   * @param output stream where token will be written to
   * @throws IOException in case of any I/O problem
   */
  public void send( final DataOutputStream output ) throws IOException {
    
    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_send_token, 
                                         new Integer( this.token.length ) ) );
    this.sendHeader( output, this.magic, this.type, this.token.length );
    output.write( this.token, 0, this.token.length );
    output.flush();

  }
}
