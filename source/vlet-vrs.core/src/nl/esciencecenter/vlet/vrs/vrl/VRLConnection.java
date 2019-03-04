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

package nl.esciencecenter.vlet.vrs.vrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownServiceException;

import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.io.VStreamReadable;
import nl.esciencecenter.vlet.vrs.io.VStreamWritable;
import nl.esciencecenter.vlet.vrs.vfs.VDir;

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
        catch (VrsException e)
        {
            throw new IOException(e.getMessage(),e); 
        }
    }

    public InputStream getInputStream() throws IOException
    {
        if (this.connected == false)
            connect();

        if (node instanceof VStreamReadable)
        {
            return ((VStreamReadable) node).createInputStream();
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
            return ((VStreamWritable) node).createOutputStream();
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

    public VRL getVRL() throws VRLSyntaxException
    {
        return new VRL(this.getURL());
    }
}
