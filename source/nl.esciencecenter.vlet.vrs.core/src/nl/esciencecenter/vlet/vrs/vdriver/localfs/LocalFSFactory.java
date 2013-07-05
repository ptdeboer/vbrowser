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

package nl.esciencecenter.vlet.vrs.vdriver.localfs;

import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_GID;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_PATH;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_SCHEME;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_SYMBOLICLINKTARGET;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_UID;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_UNIX_FILE_MODE;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSFactory;
import nl.esciencecenter.vlet.vrs.vfs.VFileSystem;

/**
 * Local VFSClient implementation of the VFS interface.
 * 
 * Note that the LocalFS implementation can have only ONE INSTANCE !!!
 * This to avoid concurrency problems (these are not handled in this class)  
 * 
 * @author P.T. de Boer
 * @see nl.esciencecenter.vlet.vrs.vfs.VFS
 *  
 */

public class LocalFSFactory extends VFSFactory
{
	// ======================================================================
	// Class
    // =======================================================================
	
	
    //	*** Class attributes ***
    
    /**
     * Service name is 'localfs', not 'file' although 'file' is allowed
     * since this is a default www scheme ("file://")
     * 
     * @see #supportedTypes 
     */ 
    
    //final static String TYPE_LOCALFS=VRS.FILE_SCHEME;
    
    /**
     * Supported service protocols, like 'file://','Dir://' and 'File://'.
     * File and Dir (note capital first letter) could be used to explicitly
     * specify a File or a Directory (Not used yet) 
     */
    public static String schemes[]={VRS.FILE_SCHEME,VFS.DIR_TYPE,VFS.FILE_TYPE,"localfs"};
   
	public static String unixFSAttributeNames[]=
	{
		ATTR_UNIX_FILE_MODE,
		ATTR_SYMBOLICLINKTARGET,
		ATTR_GID,
		ATTR_UID
	};
	
	public static String serverAttributes[]=
		{
			ATTR_SCHEME,
			ATTR_PATH
		}; 
	
	// =======================================================================
	// Instance
    // =======================================================================
    
    /**
     * Contructor is called in the VRS Registry. 
     */ 
    public LocalFSFactory()
    {
    	// initFS(); 
    }
   
    public String[] getSchemeNames()
    {
        return schemes; 
    }
    
    /** Return name of service */ 
    public final String getName()
    {
        return "LocalFS";
    }

    public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL loc) throws VrsException
	{
    	// defaults: 
    	info=super.updateServerInfo(context, info, loc); 
	    
	    info.removeAttributesIfNotIn(new StringList(serverAttributes));
	    
	    info.setNeedHostname(false); 
	    info.setNeedPort(false); 
	    info.store(); 
	    
	    return info; 
	}
    
	@Override
	public void clear() 
	{
		
	}
	

	@Override
	public VFileSystem createNewFileSystem(VRSContext context,
			ServerInfo info, VRL location) 
	{
		return new LocalFilesystem(context,info,location); 
	}
	
}
