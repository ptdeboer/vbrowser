package nl.nlesc.vlet.vrs.io;

import java.io.IOException;
import java.io.OutputStream;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;

public class ResourceWriter
{
    protected VStreamWritable source;

    public ResourceWriter(VStreamWritable node)
    {
        this.source=node; 
    }
 
    /**
     * Write bytes to stream. 
     */
    public void streamWrite(byte[] buffer,int bufferOffset,int nrOfBytes) throws VlException
    {
        try
        {
            OutputStream ostr = source.getOutputStream(); // do not append
            ostr.write(buffer, bufferOffset, nrOfBytes);
            
            try
            {
                ostr.flush(); 
                ostr.close(); // Close between actions !
            }
            catch (IOException e)
            {
                ; // 
            }
        }
        catch (IOException e)
        {
            throw new VlIOException("Failed to write to file:" + this, e);
        }
    }

}
