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
        // implementation moved to generic StreamUtil: 
        try
        {
            InputStream istr = source.getInputStream();

            if (istr==null)
                return -1; 
            
            return StreamUtil.syncReadBytes(istr,offset,buffer,bufferOffset,nrOfBytes);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        } 

    }
}
