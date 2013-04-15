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
import java.io.OutputStream;

import nl.nlesc.vlet.exception.VlException;


/**
 * Interface for stream writable resources. 
 * Default method creates an OutputStream to which can be written. 
 */
public interface VStreamWritable
{
    /** 
     * Create OutputStream to this file/object to write to. <p>
     * This method will start to write at the beginning of the file. 
     * File length is not decreased. Old data is just overwritten! 
     * Appending is currently not supported since the Jargon (SRB) implementation 
     * doesn't support this.
     *  
     * @see java.io.OutputStream
     * @return java.io.OutputStream object
     * @throws VlException
     */
    public OutputStream getOutputStream() throws IOException; 
    //OutputStream getOutputStream(boolean append) throws VlException;
    
}
