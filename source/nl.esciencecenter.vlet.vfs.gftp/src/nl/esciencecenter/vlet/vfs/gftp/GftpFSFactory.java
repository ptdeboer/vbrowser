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


import static nl.esciencecenter.vlet.VletConfig.ATTR_PASSIVE_MODE;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_ALLOW_3RD_PARTY;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_GROUP;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_OWNER;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_PORT;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_UNIQUE;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.grid.globus.GlobusUtil;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSFactory;

/**
 * Factory class for GftpFileSystems
 *    
 */
public class GftpFSFactory extends VFSFactory
{
    // =================================================================
    // Class Fields 
    // =================================================================
    /** Default attributes names for all VFSNodes */

    public static final String[] gftpAttributeNames =
    { 
        ATTR_OWNER, 
        ATTR_GROUP,
        ATTR_UNIQUE
    };
    
    // =================================================================
    // Class Methods 
    // =================================================================
 
    /** current supported type "gftp://" */
    private static final String supportedTypes[] =
         { VFS.GFTP_SCHEME,"gftp","gridftp" };

    // =================================================================
    // Instance Methods
    // =================================================================
   
    public GftpFSFactory()
    {
        // Make sure Globus bindings are initialized 
        GlobusUtil.init(); 
    }
    
    /**
     * Implementation of VFS.getTypes. <br>
     * Returns list 
     * @see nl.esciencecenter.vlet.vrs.vfs.VFS#getSchemeNames()
     */
    public String[] getSchemeNames()
    {
        return supportedTypes;
    }

    public String getName()
    {
        return "GFTP";
    }

    @Override
    public ServerInfo updateServerInfo(VRSContext context,ServerInfo info, VRL location)
        throws VrsException 
    {
        super.updateServerInfo(context,info,location); 

        info.matchTemplate(getDefaultServerAttributes(),true); 
        
        // always use GSI auth:
        info.setUseGSIAuth();
        
        return info; 
    }
      
    private AttributeSet getDefaultServerAttributes() 
    {
    	AttributeSet attrs=new AttributeSet(); 
    	// set default server attributes (if not set already) 
        attrs.put(new Attribute(ATTR_PASSIVE_MODE,true),true);
        // old resource description didn't have this one: 
        attrs.put(new Attribute(ATTR_ALLOW_3RD_PARTY,true),true);
        attrs.put(new Attribute(ATTR_HOSTNAME,"GFTPHOST"),true);
        attrs.put(new Attribute(ATTR_PORT,2811),true);
        
        
        // auto update when in debug mode ! 
        if (ClassLogger.getRootLogger().isLevelDebug())
        {
            //debug attribute
            attrs.put(new Attribute(GftpFileSystem.ATTR_GFTP_BLIND_MODE,false),true);   
        }
        
		return attrs; 
	}

	@Override
    public void clear()
    {
      
    }

    @Override
    public GftpFileSystem createNewFileSystem(VRSContext context, ServerInfo info,VRL location)
            throws VrsException 
    {
    	return new GftpFileSystem(context,info,location);
    }
    
    // =================================================================
    // Class Misc. Methods 
    // =================================================================
    
    public String getVersion()
    {
         return "GridFTP VFS Plugin version: "+ VletConfig.getVletVersion();   
    }
        
    public String getAbout()
    {
        return "<html><body><center>"
        +"<table border=0 cellspacing=4 width=600>"
         + "<tr bgcolor=#c0c0c0><td> <h3> Globus GFTP VRS Plugin. </h3></td></tr>"
         + "<tr bgcolor=#f0f0f0><td>"
                 +"Globus 1.0 & 2.0 compatible Grid FTP Virtual File System plug-in"
                 +"</td></tr>" 
         + "</table></center></body></html>";
        
    }

}
