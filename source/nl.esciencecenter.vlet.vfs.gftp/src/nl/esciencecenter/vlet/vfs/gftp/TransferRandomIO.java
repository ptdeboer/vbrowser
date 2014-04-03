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

package nl.esciencecenter.vlet.vfs.gftp;

import java.io.IOException;
import java.io.RandomAccessFile;

import org.globus.ftp.Buffer;
import org.globus.ftp.FileRandomIO;

/**
 * Creates a FileRandomIO file which masks the method used, so the progress can be measured.
 * <p>
 * Also, I couln't find a way to abort an ongoing file transfer, so by calling setMustStop(), this object will throw an
 * exception to stop the file transfer!
 * 
 * @author P.T. de Boer
 */

public class TransferRandomIO extends FileRandomIO
{
    private long readCount = 0;

    private long writeCount = 0;

    private boolean mustStop = false;

    public TransferRandomIO(RandomAccessFile arg0)
    {
        super(arg0);
    }

    public void setMustStop()
    {
        this.mustStop = true;
    }

    public synchronized Buffer read() throws IOException
    {
        if (mustStop == true)
            throw new IOException("Trasfer interrupted!");

        Buffer buf = super.read();

        // huh ?
        if (buf == null)
            return null;

        // update transfer size:
        readCount += buf.getLength();
        return buf;

    }

    public synchronized void write(Buffer buf) throws IOException
    {
        if (mustStop == true)
            throw new IOException("Trasfer interrupted!");

        super.write(buf);
        writeCount += buf.getLength();
    }

    public long getNrRead()
    {
        return readCount;
    }

    public long getNrWritten()
    {
        return writeCount;
    }

}
