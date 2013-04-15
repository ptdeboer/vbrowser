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

package nl.nlesc.vlet.vrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;

import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.io.VStreamReadable;
import nl.nlesc.vlet.vrs.io.VStreamWritable;
import nl.nlesc.vlet.vrs.vfs.VDir;

/**
 * VRL Connection which support VRLs. It extends URLConnection with the
 * supported protocols from the VRS Registry so that VRL can be used as URLs.
 * <p>
 * By suppling an VRLConnection class, VRLs can be converted to URLs and be used
 * in the default Java Stream Reader which use URL.openConnection();
 * 
 * @author P.T. de Boer
 */
public class VRLConnection extends URLConnection
{
    VNode node = null;

    protected VRLConnection(URL url)
    {
        super(url);
    }

    @Override
    public void connect() throws IOException
    {
        try
        {
            node = VRS.getDefaultVRSContext().openLocation(this.getVRL());
            connected = true;
        }
        catch (Exception e)
        {
            throw convertToIO(e);
        }
    }

    public InputStream getInputStream() throws IOException
    {
        if (this.connected == false)
            connect();

        if (node instanceof VStreamReadable)
        {
            try
            {
                return ((VStreamReadable) node).getInputStream();
            }
            catch (Exception e)
            {
                throw convertToIO(e);
            }
        }
        else if (node instanceof VDir)
        {
            // Directories do not have stream read methods.
            // possibly the 'index.html' file is meant, but
            // here we don't know what the caller wants.
            throw new UnknownServiceException("VRS: Location is a directory:" + node);
        }
        else
        {
            throw new UnknownServiceException("VRS: Location is not streamreadable:" + node);
        }
    }

    public OutputStream getOutputStream() throws IOException
    {
        if (this.connected == false)
            connect();

        if (node instanceof VStreamReadable)
        {
            try
            {
                return ((VStreamWritable) node).getOutputStream();
            }
            catch (Exception e)
            {
                throw convertToIO(e);
            }
        }
        else if (node instanceof VDir)
        {
            // Directories do not have stream read methods.
            // possibly the 'index.html' file is meant, but
            // here we don't know what the caller wants.
            throw new UnknownServiceException("VRS: Location is a directory:" + node);
        }
        else
        {
            throw new UnknownServiceException("VRS: location is not streamwritable:" + node);
        }
    }

    private IOException convertToIO(Exception e)
    {
        if (e instanceof IOException)
            return (IOException) e;

        return new IOException(e.getClass().getName() + "\n" + e.getMessage());
    }

    public VRL getVRL() throws VRLSyntaxException
    {
        return new VRL(this.getURL());
    }
}
