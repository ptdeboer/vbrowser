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

package nl.nlesc.vlet.vfs.jcraft.ssh;

import nl.nlesc.vlet.exception.NestedInterruptedException;
import nl.nlesc.vlet.vrs.vfs.VFSTransfer;

import com.jcraft.jsch.SftpProgressMonitor;

public class SftpTransferMonitor implements SftpProgressMonitor
{

    private VFSTransfer transfer;
    long count=0; 
    
    public SftpTransferMonitor(VFSTransfer info)
    {
        this.transfer=info;
    }
    
    public void init(int start, String source, String dest, long size)
    {
        count=0; 
        
        transfer.updateSubTaskDone(0);
        //System.err.println("arg0="+start); 
        //System.err.println("arg1="+source); 
        //System.err.println("arg2="+dest); 
        //System.err.println("arg3="+arg3);
        transfer.startSubTask("SFTP Transfer",size);
    }

    public boolean count(long val)
    {
        count+=val; // jSch updates in intervals;
        transfer.updateSubTaskDone(count);
        // nice jSch has a preemeptive abort. 
        // return value==true means continue!
        
        if (transfer.isCancelled())
        {
            transfer.setException(new NestedInterruptedException("SFTP has been Cancelled!"));
            return false; //abort
        }
        return true; // continue
    }

    public void end()
    {
        // setDone is for the full transfer
        // not 'current'
        transfer.endSubTask("SFTP Transfer");  
    }

}
