package nl.esciencecenter.vlet.vfs.gftp;

import java.io.IOException;

import nl.esciencecenter.ptk.io.RandomReadable;
import nl.esciencecenter.ptk.io.RandomWritable;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

public class GftpRandomAccessable implements RandomReadable, RandomWritable
{

    private GftpFileSystem server;
    private String path;

    public GftpRandomAccessable(GftpFileSystem gftpServer, String path)
    {
        this.server=gftpServer; 
        this.path=path; 
    }

    public int readBytes(long fileOffset, byte[] buffer, int bufferOffset, int nrBytes) throws IOException
    {
        return this.server.syncRead(path, fileOffset, buffer, bufferOffset, nrBytes);
    }

    public void writeBytes(long fileOffset, byte[] buffer, int bufferOffset, int nrBytes) throws IOException
    {
        try
        {
            this.server.syncWrite(path, fileOffset, buffer, bufferOffset, nrBytes);
        }
        catch (VrsException e)
        {
            throw new IOException(e.getMessage(), e);
        }
    }

    @Override
    public long getLength() throws IOException
    {
        // un cached getSize()! 
        try
        {
            return server.getSize(path);
        }
        catch (VrsException e)
        {
            throw new IOException(e.getMessage(),e); 
        } 
    }

    @Override
    public void close() throws Exception
    {
        // 
    }

}
