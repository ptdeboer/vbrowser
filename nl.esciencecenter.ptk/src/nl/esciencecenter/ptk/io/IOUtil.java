/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.io;

import java.io.IOException;
import java.io.InputStream;

import nl.esciencecenter.ptk.util.logging.ClassLogger;


public class IOUtil
{
    public static final String CHARSET_UTF8="UTF-8";
    
    private static ClassLogger logger;
    
    static
    {
        logger=nl.esciencecenter.ptk.util.logging.ClassLogger.getLogger(IOUtil.class);
    }
    
    /**
     * Synchronized read helper method.
     * Since some read() method only read small chunks each time, 
     * this method tries to read until either EOF is reached, or 
     * the desired nrOfBytes has been read. 
     * 
     */
    public static int syncReadBytes(InputStream inps, 
            long fileOffset, 
            byte[] buffer, 
            int bufferOffset, 
            int nrOfBytes) throws IOException
            //boolean closeInput) throws IOException
    {
        // basic checks 
        if (inps==null)
            return -1; 
        
        if (nrOfBytes<0)  
            return 0; 
        
        if (nrOfBytes==0) 
            return 0;
            
        // actual read
        try
        {
            if (fileOffset > 0)
                inps.skip(fileOffset);
            
            int nrRead=0;
            int result=0; 
            
            // actual read loop:            
            while ((nrRead<nrOfBytes) && (result>=0))
            {
               result=inps.read(buffer,bufferOffset+nrRead, nrOfBytes-nrRead);

               if (result<0)
               {
                   // EOF ! either return -1, or return nr of actual read bytes. 
                   logger.debugPrintf("Warning InputStream.read(): EOF when reading %d bytes\n",(nrOfBytes-nrRead));
                   logger.debugPrintf("> total nrRead/nrToRead=%d/%d\n",nrRead,nrOfBytes);

                   // EOF while nothing has been read: return -1; 
                   if (nrRead==0) 
                       return -1; 
                   else
                       // break out of read loop and return actual nr. of read bytes. 
                       break ; // negative result should take care of it
               }
               else
               {
                   nrRead+=result; 
               }
            }          

            return nrRead;
        }
        catch (IOException e)
        {
            throw new IOException("Got IO Exception during read !\n"+ e.getMessage(), e);
        }
    }
    
    /**
     * Reads String from InputStream. Does not close InputStream.
     */
    public static String readString(InputStream inps,int maxSize) throws IOException
    {
        return readString(inps,maxSize,CHARSET_UTF8);
    }
    
    public static String readString(InputStream inps,int maxSize,String charset) throws IOException
    {
        byte buffer[]=new byte[maxSize]; 
        int numRead=IOUtil.syncReadBytes(inps,0,buffer,0,maxSize);
        
        if (numRead<=0)
            return null; 
        
        String newStr=new String(buffer,0,numRead,charset); 
        return newStr; 
    }
    
   
}
