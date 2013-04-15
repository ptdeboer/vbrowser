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

import nl.esciencecenter.ptk.io.RandomReadable;
import nl.esciencecenter.ptk.io.StreamUtil;
import nl.nlesc.vlet.exception.ResourceToBigException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.io.ResourceReader;
import nl.nlesc.vlet.vrs.io.VRandomReadable;
import nl.nlesc.vlet.vrs.io.VStreamReadable;

public class FileReader extends ResourceReader
{
    public FileReader(VFile file)
    {   
        super(file); 
    }
    
    public long getLength() throws IOException
    {
        return ((VFile)source).getLength();
    }
    // *** File Read Methods ***

    /**
     * Read the whole contents and return in byte array. Current implementation
     * is to let readBytes to return an array this might have to change to some
     * ByteBuffer placeholder class.
     * 
     * @throws VlException
     */
    public byte[] getContents() throws VlException
    {
        long len;
        try
        {
            len = getLength();
        }
        catch (IOException e)
        {
            throw new VlIOException (e); 
        }
        // 2 GB files cannot be read into memory !

        // zero size optimization ! 

        if (len==0) 
        {
            return new byte[0]; // empty buffer ! 
        }

        if (len > ((long) VRS.MAX_CONTENTS_READ_SIZE))
            throw (new ResourceToBigException(
                    "Cannot read complete contents of a file greater then:"
                    + VRS.MAX_CONTENTS_READ_SIZE));

        int intLen = (int) len;
        return getContents(intLen);

    }

    /** Reads first <code>len</cod> bytes into byte array */
    public byte[] getContents(int len) throws VlException
    {
        byte buffer[] = new byte[len];

        // Warning: reading more then max int bytes
        // is impossible, but a file can be greater then that !
        // TODO: Check for out-of-memory etc ...

        int ret = readBytes(0, buffer,0,len); 

        if (ret != len)
            throw new VlIOException(
                    "Couldn't read requested number of bytes (read,requested)="
                    + ret + "," + len);

        return buffer;
    }
    
    /**
     * Read contents and return as single String. This method will fail if the
     * VFile doesn't implement the VStreamReadable interface !
     * 
     * @throws VlException
     */
    public String getContentsAsString(String charSet) throws VlException
    {
        byte contents[] = getContents();

        String str;

        try
        {
            if (charSet==null)
                str = new String(contents); // use default charSet
            else
                str = new String(contents, charSet);
        }
        catch (UnsupportedEncodingException e)
        {
//            Global.errorPrintf(this,"Exception:%s\n",e);
//            Global.debugPrintStacktrace(e); 

            throw (new VlException("charSet enconding:'"+charSet+"' not supported", e));
        }

        return str;
    }

    /**
     * Return contents as String. Used default Character set (utf-8)
     * to decode the contents. 
     * 
     * @return Contents as String. 
     * @throws VlException
     */
    public String getContentsAsString() throws VlException
    {
        return getContentsAsString("UTF-8");
    }
    
    /**
     * Synchronized read bytes until buffer is full. 
     * 
     * @see FileReader#read(long, byte[], int, int) 
     * 
     * @param buffer - the buffer to fill
     * @return nr of bytes read. This method keep on reading until all bytes are read.  
     * @throws VlException
     */
    public int read(byte buffer[])
            throws VlException
    {
        return readBytes(0,buffer,0,buffer.length); 
    }
    
    /**
     * Read from a (remote) VFile.<br>
     * Method tries to use the RandomAccessable interface 
     * or the  InputStream from VStreamReasable to read from.
     * Both can be used, but which method is more efficient depends
     * on the implementation and the usage. 
     * @param offset  - offset into file
     * @param nrOfBytes - nr of bytes to read
     * @param bufferOffset -  offset into buffer 
     * @param buffer - byte buffer to read in
     * @return number of read bytes 
     * @throws VlException
     *             if interface does not support remote read access.
     */
    public int read(long offset, byte buffer[],int bufferOffset,int nrOfBytes)
            throws VlException
    {
        return readBytes(offset,buffer,bufferOffset,nrOfBytes);
    }
    
    // ===================
    // Protected interface 
    // ===================
    
    /** Actual read method */ 
    protected int readBytes(long offset, byte buffer[],int bufferOffset,int nrOfBytes)
                throws VlException
    {
        boolean forceUseStreamRead=false; //true; // default value  

//        // when reading the first bytes, streamread is faster 
//        if (offset==0) 
//            forceUseStreamRead=true; 

        // Try Random Accessable Interface ! 
        if ((source instanceof VRandomReadable) && (forceUseStreamRead==false))
        {
            // use Sync Read ! 
            try
            {
                return StreamUtil.syncReadBytes((RandomReadable)source,offset,buffer,bufferOffset,nrOfBytes);
            }
            catch (IOException e)
            {
                throw new VlIOException(e);
            }
        }
        // else try StreamReadable interface 
        else if (source instanceof VStreamReadable)
        {
            //sync stream Read ! 
            return streamRead(offset,buffer,bufferOffset,nrOfBytes);
        }
        else
        {
            throw new nl.nlesc.vlet.exception.ResourceTypeMismatchException(
            "File type does not support (remote) read access");
        }
    }
 
    
}
