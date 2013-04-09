
package nl.esciencecenter.ptk.io;

import java.io.IOException;

/**
 * Combination of RandomReadable and RandomWritable.
 */
public interface RandomAccessable extends RandomReadable,RandomWritable
{
    // Explicit inheritance from RandomReadable and RandomWritable

    @Override
    long getLength() throws IOException;

    @Override
    public int readBytes(long fileOffset, byte buffer[], int bufferOffset,
            int nrBytes) throws IOException;

    @Override
    public void writeBytes(long fileOffset, byte buffer[], int bufferOffset,
            int nrBytes) throws IOException;

}
