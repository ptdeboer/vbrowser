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

package nl.nlesc.vlet.vrs.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.ptk.io.RingBufferStreamTransferer;
import nl.esciencecenter.ptk.io.StreamUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.vlet.exception.NotImplementedException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.io.VStreamReadable;
import nl.nlesc.vlet.vrs.io.VStreamWritable;
import nl.nlesc.vlet.vrs.vfs.VFS;
import nl.nlesc.vlet.vrs.vfs.VFSTransfer;

/**
 * Unbuffered StreamUtil helper class.
 *  
 * See methods for explenation 
 */ 
public class VRSStreamUtil extends StreamUtil
{
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(VRSStreamUtil.class); 
        //logger.setLevelToDebug(); 
    }
    
      
//    /**
//     * Actual Method which does the stream copying. 
//     * 
//     * @param transferInfo  VFSTransferInfo object which hols statistic about the transfer progress. 
//     * @param bufSize   Circular Buffer size 
//     * @param nrBytes   number of bytes to transfer. Specify -1 = transfer all 
//     * @param input     InputStream to read from 
//     * @param output    OuputStream to write to
//     * @throws VlException 
//     */
//    
//    public static long copyStreams(VFSTransfer transferInfo,int bufSize, long nrBytes,InputStream input,OutputStream output) throws VlException
//    {
//        // message being said in CircularStreamBufferTransferer. 
//        //transferInfo.startSubTask("Performing stream copy:"+ ((nrBytes>0)?nrBytes:("Unknown length")),nrBytes); 
//
//        RingBufferStreamTransferer cBuffer=new RingBufferStreamTransferer(bufSize,input,output);
//        cBuffer.setTaskMonitor(transferInfo); 
//        cBuffer.startTransfer(nrBytes);
//        return cBuffer.getTotalWritten(); 
//    }
  
    
//    /** 
//     * Calls streamCopy bytes copies all the bytes available. Doesn't check for 
//     * an actual length.
//     *
//     * @see {@link #streamCopy(VFSTransfer, VNode, VNode, long, int)}
//     * @see CircularStreamBufferTransferer
//     */ 
//    public static void streamCopy(VFSTransfer transfer,
//            VNode sourceNode, 
//            VNode destNode, 
//            int bufferSize) throws VlException
//    {
//        // specify -1 to indicate "copy all" 
//        streamCopy(transfer,sourceNode,destNode,-1,bufferSize); 
//    }
//    
   /**
    * Default VNode to VNode stream Copy. 
    * Performs and monitors controlled stream copy.  
    * Is currently used as default stream copy method by VFile. 
    *
    * @param soourceNode streamreadable source node 
    * @param destNode streamwritable source node
    * @param nrToTransfer explicit number of bytes which must be copied 
    *        method throws an exception if it can't copy the exact nr of bytes. 
    * @param bufferSize buffer size to use to perform stream copy 
    * 
    * @see CircularStreamBufferTransferer
    */ 
   public static long streamCopy(VFSTransfer transfer,
           VNode sourceNode, 
           VNode destNode,
           long nrToTransfer,
           int bufferSize)throws IOException
   
   {
        // Prevention: 
        // Extra check that new child is NOT  same as sourceNode ! 
        // this should already have been checked before...
        // = Regression Test of previous bugs! 
        
        if (destNode.compareTo(sourceNode)==0)
            throw new IOException("Cannot copy resource to itself:"+sourceNode); 
        
        // === 
        // Create InputStream 
        // ===
        InputStream istr=null;
        if (sourceNode instanceof VStreamReadable)
        {
            istr = ((VStreamReadable) sourceNode).createInputStream(); // read stream
            // bugs,bugs,bugs
            if (istr==null)
                throw new IOException("Read Error: source returned NULL InputStream:"+sourceNode); 
        }
        else
        {
            throw new IOException(
                    "Read methods not implemented/available for source:"
                    + sourceNode);
        }

        // === 
        // Create OutputStream 
        // === 
        OutputStream ostr = null;  
        if (destNode instanceof VStreamWritable)
        {
            ostr = ((VStreamWritable)destNode).createOutputStream();  // create new empty file
            // bugs,bugs,bugs
            if (ostr==null)
                throw new IOException("Write Error: destination returned NULL OutputStream to write to:"+destNode); 
        }
        else
        {
            throw new IOException(
                    "Write methods not implemented/available for source:"
                    + sourceNode);
        }
        
        logger.infoPrintf("Performing sequential stream copy for:%s to %s\n",sourceNode,destNode);
        
        //
        // Setup & Initiate Stream Copy: 
        // 
        try
        {
            transfer.startSubTask("Performing stream copy",nrToTransfer); 

            // do not allocate buffer size bigger than than file size
            if ((nrToTransfer>0) && (nrToTransfer<bufferSize)) 
                bufferSize=(int)nrToTransfer;

            // Use CirculareStreamBuffer to copy from InputStream => OutputStream
            RingBufferStreamTransferer cbuffer=new RingBufferStreamTransferer(bufferSize);

            // update into this object please:
            cbuffer.setTaskMonitor(transfer);
            //
            // nrToTransfer=-1 -> then UNKNOWN !


            // ***
            // SFTP-WRITE-OUTPUTSTREAM-32000
            // Bug in SFTP. The OutputStream has problems when writing 
            // chunks > 32000.
            // *** 

            if (destNode.getScheme().compareTo(VRS.SFTP_SCHEME)==0)
            {
                cbuffer.setMaxWriteChunkSize(32000);  
            }
            else
            {
                cbuffer.setMaxWriteChunkSize(VFS.DEFAULT_STREAM_WRITE_CHUNK_SIZE);

                // check optimal read buffer size. 
                int optimalWriteChunkSize=VFS.getOptimalWriteBufferSizeFor(destNode);

                if (optimalWriteChunkSize>0)
                    cbuffer.setMaxReadChunkSize(optimalWriteChunkSize);

            }

            cbuffer.setMaxReadChunkSize(VFS.DEFAULT_STREAM_READ_CHUNK_SIZE);
            // check optimal read buffer size. 
            int optimalReadChunkSize=VFS.getOptimalReadBufferSizeFor(sourceNode);

            if (optimalReadChunkSize>0)
                cbuffer.setMaxReadChunkSize(optimalReadChunkSize);

            logger.debugPrintf(" + streamCopy transferSize   =%d\n",nrToTransfer);
            logger.debugPrintf(" + streamCopy readChunkSize  =%d\n",cbuffer.getReadChunkSize()); 
            logger.debugPrintf(" + streamCopy writeChunkSize =%d\n",cbuffer.getWriteChunkSize()); 
            logger.debugPrintf(" + streamCopy buffer size    =%d\n",cbuffer.getCopyBufferSize()); 
                        
            // start background writer: 
            cbuffer.setInputStream(istr); 
            cbuffer.setOutputstream(ostr);

            // ====================================
            // Transfer !  
            // ====================================
           
            // will end when done
            // startTransfer will close the streams and updates transferMonitor !  
            cbuffer.startTransfer(nrToTransfer); 

            // ====================================
            // POST Chunk Copy Loop 
            // ====================================
           
            try 
            { 
                // writer task done or Exception : 
            	ostr.flush();
            	ostr.close();
            } 
            catch (Exception e)
            {
                logger.warnPrintf("Warning: Got error when flushing and closing outputstream:%s\n",e);
            } 
            
            try 
            { 
                //istr.flush(); 
            	istr.close();
            } 
            catch (Exception e)
            {
                logger.warnPrintf("Warning: Got exception when closing inputstream (after read):%s\n",e);
            } 
            
            long numTransferred=cbuffer.getTotalWritten(); 
            transfer.updateSubTaskDone(numTransferred); 
            transfer.endSubTask("Performing stream copy");
            return numTransferred; 
        }
        catch (Exception ex)
        {
            transfer.endSubTask("Performing stream copy: Error!"); 

            if (ex instanceof IOException)
            {
                throw (IOException)ex; 
            }
            else
            {
                throw new IOException("Could not copy file:" + sourceNode
                    +"\n Message="+ex.getMessage(), ex);
            }
        }
        finally
        {

        }
    }



//    /**
//     * Synchronized read loop which performs several readBytes() calls to 
//     * fill buffer.
//     * Helper method for read() method with should be done in a loop. 
//     * (This since File.read() or InputStream reads() don't always return 
//     * the desired nr. of bytes. This method keeps on reading until either
//     * End Of File is reached (EOF) or the desired nr of bytes is read. 
//     * <p> 
//     * Returns -1 when EOF is encountered, or actual nr of bytes read. 
//     * If the return value doesn't match the nrBytes wanted, no extra bytes
//     * could be read so this method doesn't have to be called again. 
//     */
//    public static int syncReadBytes(VRandomReadable source,long fileOffset, byte[] buffer,int bufferOffset, long nrBytes) throws VlException
//    {
//    	int totalRead=0; 
//    	int numRead=0;
//    	int nullReads=0; 
//    	int timeOut=60*1000; // 60 secs timeout. 
//    	
//    	// ***
//    	// read loop
//    	// read as much as possible until EOF occures or nrOfBytes is read. 
//    	// ***
//    	
//    	do
//    	{
//    		long numToRead=(nrBytes-totalRead);
//    		
//    		// will encounter out-of-mem anyway, but who knows, be robuust here ! 
//    		if (numToRead>Integer.MAX_VALUE) 
//    			numToRead=Integer.MAX_VALUE;
//    		
//    		numRead = source.readBytes(fileOffset+totalRead, buffer,totalRead, (int)numToRead);
//    		
//    		if (numRead>0)
//    		{
//    			totalRead+=numRead;
//    		}
//    		else if (numRead==0)
//    		{
//    			nullReads++;
//    			
//    			try
//    			{
//    				Thread.sleep(100);
//    			}
//    			catch (InterruptedException e)
//    			{
//    				e.printStackTrace();
//    			}
//    			
//    			// 100 seconds time out 
//    			if (nullReads*100==timeOut)
//    				throw new nl.uva.vlet.exception.VlIOException("Time out when reading from:"+source);
//    		}
//    		else if (numRead<0)
//    		{
//    			// EOF ! 
//    			if (totalRead>0)
//    			{
//    				break;// stop & return nr of bytes actual read !
//    			}
//    			else
//    			{
//    				logger.debugPrintf("Warning:got EOF after read():%s\n",source);
//    				return -1; // signal EOF without reading any bytes ! 
//    			}
//    		}
//    			
//    		//	throw new nl.uva.vlet.exception.VlIOException("EOF Exception when reading from:"+file); 
//    	
//    		logger.debugPrintf("syncReadBytes: Current numRead/totalRead=%d/%d\n",numRead,totalRead);
//    	} while((totalRead<nrBytes) && (numRead>=0));
//    	
//    	logger.debugPrintf("syncReadBytes: Finished totalRead=%d\n",totalRead);
//    	
//    	return totalRead; 
//    }
//    
}
