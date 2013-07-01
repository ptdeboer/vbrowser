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

import java.io.DataInputStream;
import java.io.IOException;

import nl.esciencecenter.glite.lfc.LFCError;
import nl.esciencecenter.glite.lfc.LFCServer;
import nl.esciencecenter.glite.lfc.Messages;



/**
 * Encapsulates LFC server response which returns a (java) long value. 
 *  
 * Receives 12 byte header and then a long value as 8 bytes Big Endian.
 * Created by P.T de Boer and based upon eclipse examples. 
 */
public class CnsLongResponse extends AbstractCnsResponse 
{
  private long longValue = 0 ;

  
  /**
   * @return opened directory id (as file id)
   */
  public long getFileid() 
  {
    return this.longValue;
  }

  @Override
  public void readFrom(final DataInputStream input ) throws IOException
  {
    LFCServer.staticLogIOMessage( Messages.lfc_log_recv_longvalue ); 
    // Pre Header
    super.receiveHeader( input );
    
    // check for response type
    if (this.type == CnsConstants.CNS_RC ) 
    {
      // received RESET CONTEXT request!
      // we have an error!
      LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) );
    }
    else 
    {
      // Read Data: 8 bytes long Big Endian (=java native endianity) 
      this.longValue = input.readLong();
      // Post Header: 
      this.size = super.receiveHeader( input );
    } 
}


  
  
}
