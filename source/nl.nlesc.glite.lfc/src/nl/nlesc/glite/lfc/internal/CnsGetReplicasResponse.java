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
/*
 * Initial development of the original code was made for the
 * g-Eclipse project founded by European Union
 * project number: FP6-IST-034327  http://www.geclipse.eu/
 *
 * Contributors:
 *    Mateusz Pabis (PSNC) - initial API and implementation
 *    Piter T. de boer - Refactoring to standalone API and bugfixing.  
 *    Spiros Koulouzis - Refactoring to standalone API and bugfixing.  
 */ 
// source: 

//package org.glite.lfc.internal;
//
//import java.io.DataInputStream;
//import java.io.IOException;
//import java.util.ArrayList;
//
//import org.glite.lfc.LFCError;
//import org.glite.lfc.LFCServer;
//import org.glite.lfc.internal.AbstractCnsResponse;
//import org.glite.lfc.internal.CnsConstants;
//import org.glite.lfc.internal.ReplicaDesc;
//
//
///**
// * New GetReplicaResponse 
// */
//public class CnsGetReplicasResponse extends AbstractCnsResponse
//{
//
//  /**
//   * Empty replica list 
//   */
//  public static final ArrayList<ReplicaDesc> EMPTY_REPLICAS = new ArrayList<ReplicaDesc>(0);
//  
//  private ArrayList<ReplicaDesc> replicas;
//  
//  @Override
//  public void readFrom( final DataInputStream input ) throws IOException 
//  {
//    LFCServer.staticLogIOMessage( "Receiving LISTREPLICA response..." ); //$NON-NLS-1$
//    this.replicas = CnsGetReplicasResponse.EMPTY_REPLICAS;
//    int items;
//    
//    // Header
//    super.readFrom( input );
//    
//    // Data
//    // check for response type 
//    if ( this.type == CnsConstants.CNS_RC ) 
//    {
//      // received RESET CONTEXT request!
//      // we have an error!
//      LFCServer.staticLogIOMessage( "RESPONSE: " + LFCError.getMessage( this.size ) ); //$NON-NLS-1$
//    }
//    else 
//    {
//      items = input.readShort();
//      if ( items > 0 ) 
//      { 
//        this.replicas = new ArrayList<ReplicaDesc>( items );
//      } 
//      
//      System.err.println("Nr Replicas="+items); 
//      
//      ReplicaDesc replica;
//      while ( items-- > 0 ) 
//      {
//        replica = ReplicaDesc.getFromStream( input );
//        this.replicas.add( replica );
//        LFCServer.staticLogIOMessage( "\t\t\t Replica Host: " + replica.getHost() ); //$NON-NLS-1$
//        LFCServer.staticLogIOMessage( "\t\t\t Replica Poolname: " + replica.getPoolName() ); //$NON-NLS-1$
//        LFCServer.staticLogIOMessage( "\t\t\t Replica FS: " + replica.getFs() ); //$NON-NLS-1$
//        LFCServer.staticLogIOMessage( "\t\t\t Replica SFN: " + replica.getSfn() ); //$NON-NLS-1$
//      }
//      
//      // CHECK END OF LIST ! 
//      short eol = input.readShort();
//      System.err.println(">>> EOL="+eol); 
//      LFCServer.staticLogIOMessage( "End of List: " + items ); //$NON-NLS-1$
//      
//      this.size = super.receiveHeader( input );
//    }
//  }
//
//  public ArrayList<ReplicaDesc> getReplicasArray() {
//    return this.replicas;
//  }
//  
//  
//}
