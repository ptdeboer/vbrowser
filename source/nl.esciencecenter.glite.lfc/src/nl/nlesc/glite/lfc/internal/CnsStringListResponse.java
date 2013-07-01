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
import java.util.ArrayList;

import nl.nlesc.glite.lfc.IOUtil;
import nl.nlesc.glite.lfc.LFCError;
import nl.nlesc.glite.lfc.LFCServer;

public class CnsStringListResponse extends AbstractCnsResponse
{
    private ArrayList<String> stringList=null;
    private short eol; 
    
    @Override
    public void readFrom( final DataInputStream input ) throws IOException
    {
      LFCServer.staticLogIOMessage( "Receiving STRINGLIST response..." ); 
       
      int nrItems=0; 
      
      // Header
      super.readFrom( input );
      
      // Data
      // check for response type 
      if ( this.type == CnsConstants.CNS_RC )
      {
        // received RESET CONTEXT request!
        // we have an error!
        LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) ); //$NON-NLS-1$
      }
      else 
      {
          nrItems = input.readShort();
        
          if ( nrItems > 0 ) 
          {    
              this.stringList = new ArrayList<String>( nrItems );
          }
        
          int index=0;
          LFCServer.staticLogIOMessage( " -  Size of StringList:"+nrItems); 
          
          while ( index<nrItems )
          {
              String str= IOUtil.readString(input); 
              this.stringList.add( str );
              LFCServer.staticLogIOMessage( " - String [#"+index+"]:='" + str+"'" );
              index++; 
          }
        
        eol = input.readShort();
        LFCServer.staticLogIOMessage( "End of List:" +eol ); 
        this.size = super.receiveHeader( input );
      }
    }

    public ArrayList<String> getStringList() 
    {
      return this.stringList;
    }
    
    /** End Of List: More entries are available */ 
    public int getEOL()
    {
        return eol; 
    }

}
