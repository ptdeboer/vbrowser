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

package nl.esciencecenter.vlet.vrs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.vrs.io.VInputStreamProducer;
import nl.esciencecenter.vlet.vrs.io.VOutputStreamProducer;
import nl.esciencecenter.vlet.vrs.io.VStreamReadable;
import nl.esciencecenter.vlet.vrs.io.VStreamWritable;
import nl.esciencecenter.vlet.vrs.vfs.VRSTransferManager;

/**
 * VRSClient is the main client class to the VRS services. It provides methods
 * to the different VRS interfaces and implementations.
 * <p>
 * Use method <code>openLocation()</code> to get any resource anywhere on the
 * grid. First create your local VRS handler object which interacts with the VRS
 * services.
 * 
 * @see nl.esciencecenter.vlet.vrs.vfs.VFSClient VFSClient for VFS methods
 * 
 * @author P.T. de Boer
 */
public class VRSClient
{
    // ===
    // Class
    // ===

    private static VRSClient defaultVRSClient = new VRSClient();

    /** Returns default class object */
    public static VRSClient getDefault()
    {
        return defaultVRSClient;
    }

    /**
     * Resolve relativeVRL to baseVRL
     * 
     * @throws VRISyntaxException
     */
    public static VRL resolve(VRL baseVRL, String relativeVRL) throws VRLSyntaxException
    {
        return new VRL(baseVRL.resolvePath(relativeVRL));
    }

    // ===
    // Instance
    // ===

    private VRSContext vrsContext;

    // private ResourceLoader resourceLoader;

    public VRSClient()
    {
        init();
        // will trigger initialization of VRS
        this.vrsContext = VRS.getDefaultVRSContext();
    }

    public VRSClient(VRSContext context)
    {
        this.vrsContext = context;
        init();
    }

    private void init()
    {
        // already done in super:
        // Global.init();
        // Global.debugPrintf(this,"--- INIT ---\n");
    }

    /**
     * Returns Resource Context associated with this client. If non was
     * specified during creating, this method returns the Global VRSContext. <br>
     * Use setVRSContext(new VRSContext) to customize your VRSContext.
     * 
     * @return Global (default) VRSContext.
     */

    final public VRSContext getVRSContext()
    {
        return this.vrsContext;
    }

    final public VRSTransferManager getTransferManager()
    {
        return this.vrsContext.getTransferManager();
    }

    /**
     * Sets new Resource Context associated with this client. Note that this
     * context only aplies for NEW created VNodes. Other created resources still
     * will use the old context. Preferably set this context before doing any
     * other VRS calls !
     */
    final public void setVRSContext(VRSContext context)
    {
        this.vrsContext = context;
    }

    /** Open remote location and return VNode */
    public VNode openLocation(VRL location) throws VrsException
    {
        return this.vrsContext.openLocation(location);
    }

    public VNode openLocation(String locationString) throws VrsException
    {
        return this.vrsContext.openLocation(locationString);
    }

    /**
     * Returns VNode associated with remote location. This method is mostly used
     * by the other get() methods so they can check the implementation type
     */
    public VNode getNode(VRL location) throws VrsException
    {
        return vrsContext.openLocation(location);
    }

    /**
     * Set specified property for this context. returns previous value.
     */
    public Object setProperty(String name, String value)
    {
        return this.vrsContext.setProperty(name, value);
    }

    /**
     * Set specified property for this context. returns previous value.
     */
    public Object setProperty(String name, int value)
    {
        return this.vrsContext.setProperty(name, value);
    }

    public Object getProperty(String name)
    {
        return this.vrsContext.getProperty(name);
    }

    public String getStringProperty(String name)
    {
        return this.vrsContext.getStringProperty(name);
    }

    /**
     * Set specified property for this context. returns previous value.
     */
    public Object setProperty(String name, Object value)
    {
        return this.vrsContext.setProperty(name, value);
    }

    /**
     * Generic InputStream Factory method.
     * 
     * @throws VrsException
     *             if stream could not be opened or resource doesn't support
     *             InputStreams.
     */
    public InputStream openInputStream(VRL loc) throws VrsException
    {
        try
        {
            // check new StreamProducer interface !
            VResourceSystem rs = openResourceSystem(loc);
            if (rs instanceof VInputStreamProducer)
            {
                return ((VInputStreamProducer) rs).createInputStream(loc);
            }

            // use default method:
            VNode node = openLocation(loc);

            if (node instanceof VStreamReadable)
                return ((VStreamReadable) node).createInputStream();
        }
        catch (IOException e)
        {
            throw new NestedIOException(e);
        }

        throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException(
                "Resource doesn't support InputStream method(s) (VNode is not StreamReadable):" + loc);
    }

    /**
     * Generic OutputStream Factory method
     * 
     * @throws VrsException
     *             if stream could not be opened or resource doesn't support
     *             OutputStreams.
     */
    public OutputStream openOutputStream(VRL loc) throws VrsException
    {
        try
        {
            // check new StreamProducer interface !
            VResourceSystem rs = openResourceSystem(loc);
            if (rs instanceof VOutputStreamProducer)
            {
                return ((VOutputStreamProducer) rs).createOutputStream(loc);
            }

            // use default method:
            VNode node = openLocation(loc);

            if (node instanceof VStreamWritable)
                return ((VStreamWritable) node).createOutputStream();

        }
        catch (IOException e)
        {
            throw new NestedIOException(e);
        }

        throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException(
                "Resource doesn't support OutputStream method(s) (VNode is not StreamWritable):" + loc);
    }

    /**
     * Returns ResourceSystem for the remote location
     * 
     * @throws VrsException
     */
    public VResourceSystem openResourceSystem(VRL loc) throws VrsException
    {
        return vrsContext.openResourceSystem(loc);
    }

    /**
     * Generic method to list the contents of a resource. If resource is NOT
     * composite, NULL will be returned.
     */
    public VNode[] list(VRL theVrl) throws VrsException
    {
        VNode node = this.getNode(theVrl);

        if (node instanceof VComposite)
            return ((VComposite) node).getNodes();

        return null;
    }

    /**
     * Close and dispose all resources associated with this VRSClient.
     */
    public void dispose()
    {
        this.vrsContext.dispose();
    }

}
