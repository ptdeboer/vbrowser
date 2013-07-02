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

package nl.esciencecenter.vlet.vfs.gftp;



/**
 * Simulates an InputStream from a Grid FTP file. 
 * permforms a get with a DataSink and masks inputStream methods
 * from this dataSink 
 */
/*
public class GftpStreamReader extends InputStream
{
    
    private GftpServer server=null; 
    private String filepath=null; 
    private long fileOffset=0; 
    private long size=0; 
    
    public GftpStreamReader(GftpServer server,String filepath) throws VlException
    {
        Global.debugPrintln("GftpStreamReader","Creating StreamReader:"+filepath);
        
        this.server=server;
        this.filepath=filepath;
        this.fileOffset=0; // 'current pointer' into file 
        
        this.size=server.getSize(filepath); 
        
        Global.debugPrintln("GftpStreamReader","size="+size); 
    }
    
    public void close() throws IOException
    {
        // nothing to be closed 
    }
    
    @Override
    public int available() throws IOException
    {
       return 0; // syncRead blocks  // size-fileOffset; 
    }
    
    
    
    public boolean markSupported()
    {
        return false; 
    }
    
    public int read(byte[] b) throws IOException
    {
        return read(b,0,b.length); 
    }
    
    public int read(byte[] b, int off, int len) throws IOException
    {
        try
        {
            //No EOF, return 0 
            if (fileOffset>=size) 
                return 0; 
            
            int numread=server.syncRead(filepath,fileOffset,b,off,len);
            fileOffset+=numread;// move inputStream pointer... 
            return numread; 
            
        }
        catch (VlException e)
        {
            // nice, original exception could be IO exception wrapped 
            // as VlException which is now wrapped again as IO exception...
            throw new IOException("GFTP VlException:"+e.getMessage()); 
        }
    }
    
    public void reset() throws IOException
    {
        fileOffset=0; 
    }
    
    public long skip(long n) throws IOException
    {
        return this.fileOffset+=n;  
    }
    
    public int read() throws IOException
    {
        byte buffer[]=new byte[1]; 
        read(buffer,0,1);
    	System.err.println("read="+buffer[0]);
    	if (buffer[0]<0) 
    		return 256+buffer[0];
        return (buffer[0]); 
    }
}
*/