package nl.esciencecenter.ptk.io;

import java.io.IOException;

/** 
 * Random Writable interface for atomic writes().   
 */
public interface RandomWritable 
{
    /**
     * Writes <code>nrBytes</code> to the file starting  
     * at position fileOffset in the file. Data is read ng 
     * from byte array buffer[bufferOffset].
     * 
     * @see java.io.RandomAccessFile#writeBytes
     */
    public void writeBytes(long fileOffset, byte buffer[], int bufferOffset,
            int nrBytes) throws IOException;
}
