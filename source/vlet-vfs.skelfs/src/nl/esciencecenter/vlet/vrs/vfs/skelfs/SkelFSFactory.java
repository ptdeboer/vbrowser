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

package nl.esciencecenter.vlet.vrs.vfs.skelfs;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFSFactory;
import nl.esciencecenter.vlet.vrs.vfs.VFileSystem;

/**
 * Example Skeleton VFS Factory. This is the Factory for VirtualFileSytems or
 * VFileSystem objects.
 */
public class SkelFSFactory extends VFSFactory
{
    // ========================================================================
    // Static
    // ========================================================================

    public static final int DEFAULT_PORT = 12345;

    // Example schemes. A FileSystem implementation can support mutliple
    // schemes.
    static private String schemes[] = { "skfs", "skelfs" };

    // ========================================================================
    // Instance
    // ========================================================================

    @Override
    public VFileSystem openFileSystem(VRSContext context, VRL location) throws VrsException
    {
        // Delegate implementation to super method.
        // Super method checks if there isn't already a filesystem object
        // created
        // for this specific context and VRL location and optionally return
        // cached FileSystem object.
        VFileSystem fs = super.openFileSystem(context, location);
        return fs;
    }

    public SkelFS createNewFileSystem(VRSContext context, ServerInfo info, VRL location)
    {
        // Create new FileSystem instance.
        // Use VRSContext for user context dependend specific settings.
        // Checks ServerInfo for Resource Info settings and properties.
        return new SkelFS(context, info, location);
    }

    @Override
    public void clear()
    {
        // clear class, clean up cached objects and close open (file) resources.
    }

    @Override
    public String getName()
    {
        return "SkelFS";
    }

    @Override
    public String[] getSchemeNames()
    {
        return schemes;
    }

    // See super method
    public ServerInfo updateServerInfo(VRSContext context, ServerInfo info, VRL loc) throws VrsException
    {
        // Update server configuration information.
        // This method should check (Server/Resource) properties and optional
        // update them.
        // The ServerInfo object might be a new created object or an old (saved)
        // configuration.
        // Important:
        // Use VRSContext object for context specific settings.

        // defaults:
        info = super.updateServerInfo(context, info, loc);
        int port = info.getPort();

        // Example: update default port.
        if (port <= 0)
            info.setPort(DEFAULT_PORT);

        // ===
        // Check global properties from Context (AND System Properties)
        // ===
        // Example: use property from VRSContext and update ServerInfo if that
        // property
        // was set yet:
        String par1 = context.getStringProperty("skelfs.defaultParameter1");

        if (StringUtil.isEmpty(par1) == false)
            info.setIfNotSet(new Attribute("parameter1", par1), true);
        else
            info.setIfNotSet(new Attribute("parameter1", "value1"), true);

        // If property "parameter2" hasn't been specified, specify it as
        // follows:
        info.setIfNotSet(new Attribute("parameter2", "value2"), true);

        // Important: Always perform an explicit update in registry after
        // changing !
        info.store();

        return info;
    }
}
