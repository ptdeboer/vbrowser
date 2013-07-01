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

import nl.nlesc.glite.lfc.IOUtil;
import nl.nlesc.glite.lfc.LFCError;
import nl.nlesc.glite.lfc.LFCServer;




/**
 * Single String Parameter response. 
 * Used by ReadLinkRequest. 
 *  
 * @author Piter T. de Boer 
 *
 */
public class CnsSingleStringResponse extends AbstractCnsResponse
{
	public  String stringResult=null; 
  
  /**
   * @return received return code
   */
  public int getReturnCode() 
  {
    return this.size;
  }

  @Override
  public void readFrom( final DataInputStream input ) throws IOException 
  {
    LFCServer.staticLogIOMessage( "receiving STRING response..." ); //$NON-NLS-1$
    
    // Header
    super.receiveHeader(input);
    
    // check for response type 
    if ( this.type == CnsConstants.CNS_RC ) 
    {
      // received RESET CONTEXT request!
      // we have an error!
      LFCServer.staticLogIOMessage( "ERROR: " + LFCError.getMessage( this.size ) ); //$NON-NLS-1$
    }
    else
    {
        stringResult=IOUtil.readString(input);
    }
    
    LFCServer.staticLogIOMessage( "Read String result ="+stringResult);  

  }

  public String getString()
  {
      return stringResult;
  }
  
  
}
