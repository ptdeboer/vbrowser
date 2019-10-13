/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package nl.esciencecenter.vlet.vrs.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.ptk.io.IOUtil;
import nl.esciencecenter.ptk.io.RandomReadable;
import nl.esciencecenter.ptk.io.RandomWritable;
import nl.esciencecenter.ptk.io.BufferStreamTransferer;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.io.VRandomReadable;
import nl.esciencecenter.vbrowser.vrs.io.VRandomWritable;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.io.VStreamReadable;
import nl.esciencecenter.vlet.vrs.io.VStreamWritable;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSTransfer;

/**
 * VRS IO Util class. Extends IO Util with VRS aware methods.
 */
public class VRSIOUtil extends IOUtil
{
    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(VRSIOUtil.class);
        // logger.setLevelToDebug();
    }

    /**
     * Default VNode to VNode stream Copy. Performs and monitors controlled stream copy. Is currently used as default
     * stream copy method by VFile.
     * 
     * @param soourceNode
     *            streamreadable source node
     * @param destNode
     *            streamwritable source node
     * @param nrToTransfer
     *            explicit number of bytes which must be copied method throws an exception if it can't copy the exact nr
     *            of bytes.
     * @param bufferSize
     *            buffer size to use to perform stream copy
     * 
     * @see BufferStreamTransferer
     */
    public static long streamCopy(VFSTransfer transfer,
            VNode sourceNode,
            VNode destNode,
            long nrToTransfer,
            int bufferSize) throws IOException

    {
        // Prevention:
        // Extra check that new child is NOT same as sourceNode !
        // this should already have been checked before...
        // = Regression Test of previous bugs!

        if (destNode.compareTo(sourceNode) == 0)
            throw new IOException("Cannot copy resource to itself:" + sourceNode);

        // ===
        // Create InputStream
        // ===
        InputStream istr = null;
        if (sourceNode instanceof VStreamReadable)
        {
            istr = ((VStreamReadable) sourceNode).createInputStream(); // read stream
            // bugs,bugs,bugs
            if (istr == null)
                throw new IOException("Read Error: source returned NULL InputStream:" + sourceNode);
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
            ostr = ((VStreamWritable) destNode).createOutputStream(); // create new empty file
            // bugs,bugs,bugs
            if (ostr == null)
                throw new IOException("Write Error: destination returned NULL OutputStream to write to:" + destNode);
        }
        else
        {
            throw new IOException(
                    "Write methods not implemented/available for source:"
                            + sourceNode);
        }

        logger.infoPrintf("Performing sequential stream copy for:%s to %s\n", sourceNode, destNode);

        //
        // Setup & Initiate Stream Copy:
        //
        try
        {
            transfer.startSubTask("Performing stream copy", nrToTransfer);

            // do not allocate buffer size bigger than than file size
            if ((nrToTransfer > 0) && (nrToTransfer < bufferSize))
                bufferSize = (int) nrToTransfer;

            // Use CirculareStreamBuffer to copy from InputStream => OutputStream
            BufferStreamTransferer cbuffer = new BufferStreamTransferer(bufferSize);

            // update into this object please:
            cbuffer.setTaskMonitor(transfer);
            //
            // nrToTransfer=-1 -> then UNKNOWN !

            // ***
            // SFTP-WRITE-OUTPUTSTREAM-32000
            // Bug in SFTP. The OutputStream has problems when writing
            // chunks > 32000.
            // ***

            if (destNode.getScheme().compareTo(VRS.SFTP_SCHEME) == 0)
            {
                cbuffer.setMaxWriteChunkSize(32000);
            }
            else
            {
                cbuffer.setMaxWriteChunkSize(VFS.DEFAULT_STREAM_WRITE_CHUNK_SIZE);

                // check optimal read buffer size.
                int optimalWriteChunkSize = VFS.getOptimalWriteBufferSizeFor(destNode);

                if (optimalWriteChunkSize > 0)
                    cbuffer.setMaxReadChunkSize(optimalWriteChunkSize);

            }

            cbuffer.setMaxReadChunkSize(VFS.DEFAULT_STREAM_READ_CHUNK_SIZE);
            // check optimal read buffer size.
            int optimalReadChunkSize = VFS.getOptimalReadBufferSizeFor(sourceNode);

            if (optimalReadChunkSize > 0)
                cbuffer.setMaxReadChunkSize(optimalReadChunkSize);

            logger.debugPrintf(" + streamCopy transferSize   =%d\n", nrToTransfer);
            logger.debugPrintf(" + streamCopy readChunkSize  =%d\n", cbuffer.getReadChunkSize());
            logger.debugPrintf(" + streamCopy writeChunkSize =%d\n", cbuffer.getWriteChunkSize());
            logger.debugPrintf(" + streamCopy buffer size    =%d\n", cbuffer.getCopyBufferSize());

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
                logger.warnPrintf("Warning: Got error when flushing and closing outputstream:%s\n", e);
            }

            try
            {
                // istr.flush();
                istr.close();
            }
            catch (Exception e)
            {
                logger.warnPrintf("Warning: Got exception when closing inputstream (after read):%s\n", e);
            }

            long numTransferred = cbuffer.getTotalWritten();
            transfer.updateSubTaskDone(numTransferred);
            transfer.endSubTask("Performing stream copy");
            return numTransferred;
        }
        catch (Exception ex)
        {
            transfer.endSubTask("Performing stream copy: Error!");

            if (ex instanceof IOException)
            {
                throw (IOException) ex;
            }
            else
            {
                throw new IOException("Could not copy file:" + sourceNode
                        + "\n Message=" + ex.getMessage(), ex);
            }
        }
        finally
        {

        }
    }

    public static int syncReadBytes(VRandomReadable randomFile, long fileOffset, byte[] buffer, int bufferOffset, int nrBytes)
            throws Exception
    {
        RandomReadable reader = randomFile.createRandomReadable();
        int numRead = reader.readBytes(fileOffset, buffer, bufferOffset, nrBytes);
        reader.close();
        return numRead;
    }


}