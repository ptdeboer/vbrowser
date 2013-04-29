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

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

public class RandomReader implements VRandomReadable
{
    private VRandomReadable source;

    private long fileIndex;

    public long getLength() throws IOException
    {
        return this.source.getLength();
    }

    /**
     * Reads <code>nrBytes</code> from file starting to read from
     * <code>fileOffset</code>. Data is stored into the byte array buffer[]
     * starting at bufferOffset.
     * 
     * @throws VrsException
     * @see java.io.RandomAccessFile#readBytes
     */
    public int readBytes(long fileOffset, byte[] buffer, int bufferOffset, int nrBytes) throws IOException
    {
        fileIndex = fileIndex + nrBytes;
        return this.source.readBytes(fileOffset, buffer, bufferOffset, nrBytes);
    }

    public RandomReader(VRandomReadable source)
    {
        this.source = source;
    }

    public void close() throws IOException
    {
        fileIndex = 0;
        this.source = null;
        try
        {
            this.getInputStream().close();
        }
        catch (IOException e)
        {
            throw e; // new IOException(e);
        }
        return;
    }

    /**
     * Reads <code>signatureBytes.length</code> bytes from this file into the
     * byte array, starting at the current file pointer. This method reads
     * repeatedly from the file until the requested number of bytes are read.
     * This method blocks until the requested number of bytes are read, the end
     * of the stream is detected, or an exception is thrown.
     * 
     * @param signatureBytes the buffer into which the data is read.
     * @throws VrsException 
     * @exception EOFException
     *                if this file reaches the end before reading all the bytes.
     * @exception IOException
     *                if an I/O error occurs.
     */
    public void readFully(byte[] signatureBytes) throws IOException
    {
        this.source.readBytes(fileIndex, signatureBytes, 0, signatureBytes.length);
        fileIndex = fileIndex + signatureBytes.length;
    }

    /**
     * Returns the length of this file.
     * 
     * @return the length of this file, measured in bytes.
     * @throws VrsException
     * @exception IOException if an I/O error occurs.
     */
    public int length() throws IOException
    {
        return (int) this.source.getLength();
    }

    /**
     * Sets the file-pointer offset, measured from the beginning of this file,
     * at which the next read or write occurs. The offset may be set beyond the
     * end of the file. Setting the offset beyond the end of the file does not
     * change the file length. The file length will change only by writing after
     * the offset has been set beyond the end of the file.
     * 
     * @param pos
     *            the offset position, measured in bytes from the beginning of
     *            the file, at which to set the file pointer.
     * @exception IOException
     *                if <code>pos</code> is less than <code>0</code> or if an
     *                I/O error occurs.
     */
    public void seek(long off)
    {
        this.fileIndex = off;
    }

    /**
     * Reads a byte of data from this file. The byte is returned as an integer
     * in the range 0 to 255 (<code>0x00-0x0ff</code>). This method blocks if no
     * input is yet available.
     * <p>
     * Although <code>RandomAccessFile</code> is not a subclass of
     * <code>InputStream</code>, this method behaves in exactly the same way as
     * the {@link InputStream#read()} method of <code>InputStream</code>.
     * 
     * @return the next byte of data, or <code>-1</code> if the end of the file
     *         has been reached.
     * @throws VrsException
     *             if an I/O error occurs. Not thrown if end-of-file has been
     *             reached.
     */
    public int read() throws IOException
    {
        byte[] buffer = new byte[1];
        this.source.readBytes(fileIndex, buffer, 0, 1);
        fileIndex = fileIndex + 1;
        return buffer[0];
    }

    /**
     * Attempts to skip over <code>lenToSkip</code> bytes of input discarding
     * the skipped bytes.
     * <p>
     * 
     * This method may skip over some smaller number of bytes, possibly zero.
     * This may result from any of a number of conditions; reaching end of file
     * before <code>n</code> bytes have been skipped is only one possibility.
     * This method never throws an <code>EOFException</code>. The actual number
     * of bytes skipped is returned. If <code>n</code> is negative, no bytes are
     * skipped.
     * 
     * @param n
     *            the number of bytes to be skipped.
     * @return the actual number of bytes skipped.
     * @throws VrsException
     *             if an I/O error occurs.
     */
    public int skipBytes(int lenToSkip) throws IOException
    {
        long pos;
        long len;
        long newpos;

        if (lenToSkip <= 0)
        {
            return 0;
        }
        pos = this.fileIndex;
        len = length();
        newpos = pos + lenToSkip;
        if (newpos > len)
        {
            newpos = len;
        }
        seek(newpos);

        /* return the actual number of bytes skipped */
        return (int) (newpos - pos);
    }

    /**
     * Reads up to <code>len</code> bytes of data from this file into an array
     * of bytes. This method blocks until at least one byte of input is
     * available.
     * <p>
     * Although <code>RandomAccessFile</code> is not a subclass of
     * <code>InputStream</code>, this method behaves in exactly the same way as
     * the {@link InputStream#read(byte[], int, int)} method of
     * <code>InputStream</code>.
     * 
     * @param b
     *            the buffer into which the data is read.
     * @param off
     *            the start offset in array <code>b</code> at which the data is
     *            written.
     * @param len
     *            the maximum number of bytes read.
     * @return the total number of bytes read into the buffer, or
     *         <code>-1</code> if there is no more data because the end of the
     *         file has been reached.
     * @throws VrsException
     *             If the first byte cannot be read for any reason other than
     *             end of file, or if the random access file has been closed, or
     *             if some other I/O error occurs.
     */
    public int read(byte[] b, int off, int len) throws IOException
    {
        return readBytes(fileIndex, b, off, len);
    }

    public InputStream getInputStream()
    {
        return this.getInputStream();
    }

    public int read(byte[] buffer) throws IOException
    {
        return read(buffer, 0, buffer.length);
    }

}
