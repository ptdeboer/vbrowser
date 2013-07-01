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
import java.io.IOException;

import nl.nlesc.glite.lfc.LFCServer;
import nl.nlesc.glite.lfc.Messages;



  
/**
 *  Encapsulates receiving part of GSS context negotiations
 */
public class GSSTokenRecv extends AbstractCnsResponse {

  private byte[] token;

  /**
   * Gets GSS Token
   * @return received bytes array token
   */
  public byte[] getToken() {
    return this.token;
  }
  
  @Override
  public void readFrom( final DataInputStream input ) throws IOException 
  {
    // Header
    this.receiveHeader( input );
    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_recv_token, 
                                         new Integer( this.size ) ) );
    
    // IO ERROR ! (could be wrong server) 
    if ((size<=0) || (size>1*1024*1024)) 
       throw new IOException("Token ERROR: Wrong size:"+size); 

    this.token = new byte[ this.size ];
    int wait=100; 
    int numWait=5; // 2 pow 5 = 32 * 100 = 3200 milliseconds   
    while (( input.available() < this.size - 12 ) && (numWait-->0)) 
    {
      try 
      {
        LFCServer.staticLogIOMessage( "TOKEN wait..." );
        Thread.sleep(wait);
        wait=wait*2;  
      }
      catch( InterruptedException ex ) 
      {
        // nothing to be done
      }
    }
   
    LFCServer.staticLogIOMessage( "TOKEN read ..." );
    input.read( this.token, 0, this.token.length );
    
  }


}
