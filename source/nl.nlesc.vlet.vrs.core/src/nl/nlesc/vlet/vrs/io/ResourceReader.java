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

package nl.nlesc.vlet.vrs.io;

import java.io.IOException;
import java.io.InputStream;

import nl.esciencecenter.ptk.io.StreamUtil;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;

public class ResourceReader
{
    protected VStreamReadable source;

    public ResourceReader(VStreamReadable node)
    {
        this.source=node; 
    }

    /**
     * Perform a synchronized read on the Resource. 
     * Implementation tries to read as much as possible. 
     * @param offset - optional offset into the InputStream
     * @param buffer - buffer to read to 
     * @param bufferOffset - offset into buffer
     * @param nrOfBytes - actual number of bytes to read. This method will continue until these number 
     *                     of bytes have been read. 
     */
    public int streamRead(long offset, byte[] buffer, int bufferOffset, int nrOfBytes) throws VlException
    {
        InputStream istr=null;
        
        // implementation moved to generic StreamUtil: 
        try
        {
            istr = source.getInputStream();

            if (istr==null)
                return -1; 
            
            int val=StreamUtil.syncReadBytes(istr,offset,buffer,bufferOffset,nrOfBytes);

            return val;
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        } 
        finally
        {
            if (istr!=null)
            {
                try {istr.close(); } catch (Exception e) { ; } 
            }
        }
    }
}
