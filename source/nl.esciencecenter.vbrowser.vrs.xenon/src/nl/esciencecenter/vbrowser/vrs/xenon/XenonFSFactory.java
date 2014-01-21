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

package nl.esciencecenter.vbrowser.vrs.xenon;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.ServerInfo.AuthScheme;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFSFactory;
import nl.esciencecenter.vlet.vrs.vfs.VFileSystem;


/**
 *  Octopus Meta FSFactory. 
 */ 
public class XenonFSFactory extends VFSFactory
{
	// ========================================================================
	// Static 
	// ========================================================================
	    
	static private String schemes[]=
		{
			//VRS.FILE_SCHEME,
			VRS.SFTP_SCHEME,
			//VRS.GFTP_SCHEME,
			// for testing only:
			"xenon."+VRS.FILE_SCHEME,
			"xenon."+VRS.SFTP_SCHEME,
	        "xenon."+VRS.GFTP_SCHEME
		}; 

	// ========================================================================
	// Instance
	// ========================================================================

	@Override
	public VFileSystem openFileSystem(VRSContext context, VRL location)
			throws VrsException 
	{
		// Delegate implementation to super method. 
		// Super method checks if there isn't already a filesystem object created
		// for this specific context and VRL location and optionally return cached FileSystem object. 
		VFileSystem fs=super.openFileSystem(context,location);
		return fs; 
	}
	
	public XenonVFS createNewFileSystem(VRSContext context,ServerInfo info, VRL location) throws VrsException
	{
		// Create new FileSystem instance. 
		// Use VRSContext for user context dependend specific settings. 
		// Checks ServerInfo for Resource Info settings and properties. 
		return new XenonVFS(context,info,location);
	}
		
	@Override
	public void clear() 
	{
		// clear class, clean up cached objects and close open (file) resources. 
	}

	@Override
	public String getName() 
	{
		return "XenonRS";
	}

	@Override
	public String[] getSchemeNames()
	{
		return schemes;
	}
		
	// See super method 
	public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL loc) throws VrsException
	{
	    if (loc.hasScheme(VRS.SFTP_SCHEME))
	    {
	        return updateSSHServerInfo(context,info,loc); 
	    }
	    
	    // Default file system properties:  
	    
		// defaults: 
		info=super.updateServerInfo(context, info, loc); 
		
		return info; 
	}

	public ServerInfo updateSSHServerInfo(VRSContext context, ServerInfo info, VRL loc)
    {
	    String user=info.getUsername(); 
        
        String defaultUser=context.getConfigManager().getUserName();
        // sftp MUST have username: set current to default ! 
        if ((user==null) || (user.compareTo("")==0)) 
        {
            info.setUsername(defaultUser);
        }
        
        info.setNeedUserinfo(true);
        info.setIfNotSet(VAttributeConstants.ATTR_HOSTNAME,"HOSTNAME"); 
        info.setIfNotSet(ServerInfo.ATTR_SSH_IDENTITY,"id_rsa");
        info.setAuthScheme(ServerInfo.AuthScheme.PASSWORD_OR_IDKEY_AUTH,false);  
        info.setNeedUserinfo(true); 
        
        return info; 
    }

   
}
