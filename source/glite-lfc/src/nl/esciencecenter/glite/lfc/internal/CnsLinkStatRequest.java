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
import java.io.DataOutputStream;
import java.io.IOException;

import nl.esciencecenter.glite.lfc.IOUtil;
import nl.esciencecenter.glite.lfc.LFCServer;
import nl.esciencecenter.glite.lfc.Messages;



/**
 * <p>
 * Encapsulates LFC server LINKSTAT command request. Then receives and returns
 * response.
 * </p>
 * <p>
 * Sends 12 byte header.
 * </p>
 * @see CnsLinkStatResponse
 */
public class CnsLinkStatRequest extends AbstractCnsRequest {
  
  private int uid;
  private int gid;
  private long cwd;
  private long sth;
  private String path;

  /**
   * Creates request for link detailed information.
   * 
   * @param path link for which information is requested
   */
  public CnsLinkStatRequest( final String path ) {
    this.path = path;
    this.uid = 0;
    this.gid = 0;
    this.cwd = 0;
    this.sth = 0;
  }

  /**
   * <p> Sets local user id </p>
   * <p> Probably can be set to anything you want, but not sure </p>
   * 
   * @param uid local user id
   */
  public void setUid( final int uid ) {
    this.uid = uid;
  }

  /**
   * <p> Sets local group id </p>
   * <p> Probably can be set to anything you want, but not sure </p>
   * @param gid local group id
   */
  public void setGid( final int gid ) {
    this.gid = gid;
  }
  
  /**
   * <p>Sets cwd parameter</p>
   * <p>I have no idea what it's for<br>
   * Use 0 (zero)</p>
   * 
   * @param cwd CWD - Change Working Directory? I have no idea.
   */
  public void setCwd( final long cwd ) {
    this.cwd = cwd;
  }
  
  /**
   * <p> Sets sth parameter</p>
   * <p> I have no idea what it's for<br>
   * Use 0 (zero)</p>
   * @param sth named as something - I have no idea.
   */
  public void setSth( final long sth ) {
    this.sth = sth;
  }
  
  /**
   * <p>Sets new path value</p>
   * <p>This parameter is also set by constructor</p>
   * @param path link for which detailed information will be fetched
   */
  public void setPath( final String path ) {
    this.path = path;
  }

  /**
   * <p>Sends prepared request to the output stream and then fetch the response</p>
   * 
   * @param out output stream to which request will be written
   * @param in input stream from which response will be read
   * @return object that encapsulates response
   * @throws IOException in case of any I/O problem
   * @see CnsLinkStatResponse
   */
  public CnsLinkStatResponse sendTo( final DataOutputStream out, final DataInputStream in )
    throws IOException  {
    
    LFCServer.staticLogIOMessage( String.format( Messages.lfc_log_send_lstat, this.path ) ); 
    this.sendHeader( out,
                     CnsConstants.CNS_MAGIC2,
                     CnsConstants.CNS_LSTAT,
                     37 + IOUtil.byteSize(path) );
    
    out.writeInt( this.uid );  // user id
    out.writeInt( this.gid );  // group id
    out.writeLong( this.cwd ); // I have no idea what is this
    out.writeLong( this.sth ); // I have no idea what is this
    
//    out.write( this.path.getBytes(), 0, this.path.length() );
//    out.writeByte( 0x0 );
    IOUtil.writeString(out,path); 
    out.flush();
    
    CnsLinkStatResponse result = new CnsLinkStatResponse();
    result.readFrom( in );
    return result;
    
  }
}
