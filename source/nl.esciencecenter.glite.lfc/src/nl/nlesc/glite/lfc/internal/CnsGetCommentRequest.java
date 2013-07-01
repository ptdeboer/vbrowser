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

import nl.nlesc.glite.lfc.IOUtil;
import nl.nlesc.glite.lfc.LFCServer;



/**
 * GetComment Request. 
 * 
 * @author Piter T. de Boer 
 */

public class CnsGetCommentRequest
{
  private int uid=0;
  private int gid=0;
  private long cwd=0;
  private String path=null; 


  
  /**
   * Creates request for  get commnent.
   */
  public CnsGetCommentRequest( final String path) 
  {
    this.path = path;
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
  }


  public CnsSingleStringResponse sendTo( final DataOutputStream output, final DataInputStream input )
    throws IOException  
    {
         LFCServer.staticLogIOMessage(  "Sending GetComment information request for: " + this.path );
         
         CnsMessage msg= CnsMessage.createSendMessage(CnsConstants.CNS_MAGIC,
                 CnsConstants.CNS_GETCOMMENT);
         
         DataOutputStream dataOut = msg.createBodyDataOutput(4096); 
     
         dataOut.writeInt( this.uid ); //+4 
         dataOut.writeInt( this.gid ); //+4
         dataOut.writeLong(this.cwd);  //+8
                                       //+=28 
         IOUtil.writeString(dataOut,path);  //+1+length()
         // no need to flush databuffer not close it.
         
         // finalize and send ! 
         int numSend=msg.sendTo(output);
         output.flush(); // sync 
         msg.dispose(); 
         
         CnsSingleStringResponse response=new CnsSingleStringResponse(); 
         // output.flush();
         response.readFrom( input );
         return response; 
  }
  
}
