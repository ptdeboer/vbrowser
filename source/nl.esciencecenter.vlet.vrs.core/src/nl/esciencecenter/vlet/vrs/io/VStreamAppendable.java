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

package nl.esciencecenter.vlet.vrs.io;

import java.io.IOException;
import java.io.OutputStream;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

/**
 * VStreamAppendable extends VStreamWritable by adding getOutputStream() method which 
 * which starts at the end of the resource.
 */
public interface VStreamAppendable extends VStreamWritable
{
    /** 
     * Create OutputStream to rewrite or continue writing to this file or resource.<p>
     * The append parameter specifies whether to begin writing at the 
     * beginning or the end of the file. 
     * The previous content is kept if the number of bytes written is less then the original file length. 
     *  
     * @see java.io.OutputStream
     * @return java.io.OutputStream object
     * @throws VrsException
     */
    public OutputStream createAppendingOutputStream(boolean append) throws IOException; 

    
}
