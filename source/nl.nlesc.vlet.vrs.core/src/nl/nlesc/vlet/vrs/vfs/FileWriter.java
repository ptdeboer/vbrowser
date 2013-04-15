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

package nl.nlesc.vlet.vrs.vfs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import nl.nlesc.vlet.exception.NotImplementedException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;
import nl.nlesc.vlet.vrs.io.ResourceWriter;
import nl.nlesc.vlet.vrs.io.VRandomAccessable;

public class FileWriter extends ResourceWriter
{
    public FileWriter(VFile file)
    {   
        super(file); 
    }
    
    /**
     * Write buffer to (remote) File. An offset can be specified into the file 
     * as well into the buffer. 
     * <p> 
     * Use isRandomAccessable() first to determine whether this file can be
     * randomly written to. <br>
     * <br>
     * 
     * @see VRandomAccessable
     */
    public void write(long offset, byte buffer[], int bufferOffset, int nrOfBytes) throws VlException
    {
        writeBytes(offset,buffer,bufferOffset,nrOfBytes);
    }
    
    /** Write complete buffer to beginning of file */ 
    public void write(byte buffer[], int bufferOffset,int nrOfBytes) throws VlException
    {
        write(0,buffer,bufferOffset,nrOfBytes);
    }

    /** Write specified number of bytes from buffer to the beginning of the file. */ 
    public void write(byte buffer[],int nrOfBytes) throws VlException
    {
        write(0,buffer,0,nrOfBytes);
    }
    
    /**
     * Set contents using specified String and encoding.
     * 
     * @param contents 
     *            - new String Contents
     * @param encoding 
     *            - charset to use
     * @throws VlException if contents can not be set somehow
     */
    public void setContents(String contents, String encoding) throws VlException
    {
        byte[] bytes;

        try
        {
            bytes = contents.getBytes(encoding);
            setContents(bytes);
            return;
        }
        catch (UnsupportedEncodingException e)
        {
            throw (new VlException("Encoding not supported:" + encoding, e));
        }
    }

    /**
     * Replace or create File contents with data from the bytes array. The new
     * file length will match the byte array lenth thus optionally truncating or
     * extend an existing file.
     */
    public void setContents(byte bytes[]) throws VlException
    {
        this.streamWrite(bytes,0,bytes.length); 
    }

    /**
     * Set contents using specified String. Note that the default encoding
     * format is 'UTF-8'.
     * 
     * @param contents
     *            -  new Contents String
     * @throws VlException
     * @see #setContents(String contents, String encoding) to specify the coding
     */
    public void setContents(String contents) throws VlException
    {
        setContents(contents, "UTF-8");
        return;
    }
    
    // ===================
    // Protected interface 
    // ===================
    
    /** Actual write method */
    protected void writeBytes(long offset, byte buffer[], int bufferOffset, int nrOfBytes) throws VlException
    {
        // writing as a single stream usually is faster:
        if (offset==0) 
        {
            this.streamWrite(buffer,bufferOffset,nrOfBytes); 
        }
        else if (source instanceof VRandomAccessable)
        {
            try
            {
                ((VRandomAccessable) source).writeBytes(offset, buffer, bufferOffset,
                        nrOfBytes);
            }
            catch (IOException e)
            {
               throw new VlIOException(e); 
            }
        }
        else
        {
            throw new NotImplementedException(
                    "This resource is not Random Accessable (interface VRandomAccessable not implemented):"
                    + this);
        }
    }
   
}
