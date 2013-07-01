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

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

/**
 * Random Readable interface. 
 */
public interface VRandomReadable 
{
	 /**
     * Reads <code>nrBytes</code> from file starting to read from 
     * <code>fileOffset</code>. Data is stored into the byte array
     * buffer[] starting at bufferOffset.
     *   
     * @throws VrsException
     * @see java.io.RandomAccessFile#readBytes
     */
    public int readBytes(long fileOffset, byte buffer[], int bufferOffset,
            int nrBytes) throws IOException;
  
    /**
     * Return (maximum) length of random readable resource. 
     * fileOffset+nrBytes &lt; length of resource. 
     */ 
    long getLength() throws IOException;
}
