package nl.esciencecenter.ptk.io;

import java.io.IOException;

public interface RandomReadable
{
    /**
     * Return (maximum) length of random readable resource. 
     * fileOffset+nrBytes &lt; length of resource. 
     */ 
    long getLength() throws IOException;

    /**
     * Reads <code>nrBytes</code> from file starting to read from 
     * <code>fileOffset</code>. Data is stored into the byte array
     * buffer[] starting at bufferOffset.
     *   
     * @throws VlException
     * @see java.io.RandomAccessFile#readBytes
     */
    public int readBytes(long fileOffset, byte buffer[], int bufferOffset,
            int nrBytes) throws IOException;

}
