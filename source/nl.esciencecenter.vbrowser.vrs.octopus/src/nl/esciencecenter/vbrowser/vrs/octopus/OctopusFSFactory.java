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

package nl.esciencecenter.vbrowser.vrs.octopus;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vfs.VFSFactory;
import nl.nlesc.vlet.vrs.vfs.VFileSystem;


/**
 *  FSFactory for Octopus adaptor. 
 *  Handles filesystem objects.  
 */ 
public class OctopusFSFactory extends VFSFactory
{
	// ========================================================================
	// Static 
	// ========================================================================
	
	// Example schemes. A FileSystem implementation can support mutliple schemes. 
	static private String schemes[]=
		{
			"file",
			"sftp",
			// for testing: 
			"octopus.file",
			"octopus.sftp"
		}; 

	// ========================================================================
	// Instance
	// ========================================================================

	@Override
	public VFileSystem openFileSystem(VRSContext context, VRL location)
			throws VlException 
	{
		// Delegate implementation to super method. 
		// Super method checks if there isn't already a filesystem object created
		// for this specific context and VRL location and optionally return cached FileSystem object. 
		VFileSystem fs=super.openFileSystem(context,location);
		return fs; 
	}
	
	public OctopusFS createNewFileSystem(VRSContext context,ServerInfo info, VRL location) throws VlException
	{
		// Create new FileSystem instance. 
		// Use VRSContext for user context dependend specific settings. 
		// Checks ServerInfo for Resource Info settings and properties. 
		return new OctopusFS(context,info,location);
	}
		
	@Override
	public void clear() 
	{
		// clear class, clean up cached objects and close open (file) resources. 
	}

	@Override
	public String getName() 
	{
		return "OctopusRS";
	}

	@Override
	public String[] getSchemeNames()
	{
		return schemes;
	}
		
	// See super method 
	public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL loc) throws VlException
	{
		// Update server configuration information. 
		// This method should check (Server/Resource) properties and optional update them. 
		// The ServerInfo object might be a new created object or an old (saved) configuration. 
		// Important: 
		// Use VRSContext object for context specific settings. 
				
		// defaults: 
		info=super.updateServerInfo(context, info, loc); 
		int port=info.getPort();
		
		// Example: update default port. 
		if (port<=0) 
		    info.setPort(getDefaultPortFor(loc)); 
		
		// === 
		// Check global properties from Context (AND System Properties) 
		// === 
		// Example: use property from VRSContext and update ServerInfo if that property
		// was set yet: 
		String par1=context.getStringProperty("octopus.tentacles");
		
		if (StringUtil.isEmpty(par1)==false)
		    info.setIfNotSet(new VAttribute("tentacles",par1), true);
		else
		    info.setIfNotSet(new VAttribute("tentacles","8"), true);
		
		// If property "parameter2" hasn't been specified, specify it as follows: 
        info.setIfNotSet(new VAttribute("color","blue"), true);
        
        // Important: Always perform an explicit update in registry after changing ! 
		info.store(); 
		
		return info; 
	}

    public static int getDefaultPortFor(VRL loc)
    {
        // resolve per scheme: file is
        return 0;
    }

   
}
