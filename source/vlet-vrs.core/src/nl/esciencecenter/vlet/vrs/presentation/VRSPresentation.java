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

package nl.esciencecenter.vlet.vrs.presentation;

import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_CREATION_TIME;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_FILE_SIZE;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_HOSTNAME;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_ICON;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_MIMETYPE;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_MODIFICATION_TIME;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_NAME;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_PATH;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_PERMISSIONSTRING;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_RESOURCE_STATUS;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_RESOURCE_TYPE;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_SCHEME;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_MAX_WALL_TIME;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_NODE_TEMP_DIR;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.vfs.VFS;

/** 
 * Factory class for UIPresentation of VRS Nodes. 
 */
public class VRSPresentation extends Presentation
{
    public static String defaultVFSAttributeNames[] = 
        {
            ATTR_ICON, 
            ATTR_NAME, 
            ATTR_RESOURCE_TYPE, 
            ATTR_FILE_SIZE,
            // ATTR_MODIFICATION_TIME_STRING,
            ATTR_MODIFICATION_TIME, 
            ATTR_MIMETYPE, 
            ATTR_PERMISSIONSTRING,
            // ATTR_ISHIDDEN,
            // ATTR_ISLINK
            // VFS.ATTR_ISFILE,
            // VFS.ATTR_ISDIR
        };

    /** 
     * Default Attribute Name to show for VFSNodes 
     */
    public static String defaultSRBAttributeNames[] = 
        {
            ATTR_ICON, 
            ATTR_NAME, 
            ATTR_RESOURCE_TYPE,
            ATTR_FILE_SIZE, 
            "Resource",
            // ATTR_MODIFICATION_TIME_STRING,
            ATTR_MODIFICATION_TIME, ATTR_MIMETYPE,
            // ATTR_PERMISSIONS_STRING,
            // ATTR_ISHIDDEN,
            // ATTR_ISLINK
            // VFS.ATTR_ISFILE,
            // VFS.ATTR_ISDIR
        };
    
    /** 
     * Default Attribute Name to show for VNodes 
     */
    public static String defaultNodeAttributeNames[] = 
        {
            ATTR_ICON, 
            ATTR_RESOURCE_TYPE, 
            ATTR_NAME,
            // ATTR_LENGTH,
            ATTR_MIMETYPE
        };

    /** Default Attribute Name to show for VNodes */
    public static String myvleAttributeNames[] = 
        {
            ATTR_ICON, 
            ATTR_RESOURCE_TYPE,
            ATTR_NAME,
            ATTR_SCHEME, 
            ATTR_HOSTNAME,
            ATTR_PATH,
            // ATTR_LENGTH,
            // ATTR_MIMETYPE
        };

    /** @see getPresentationFor(String, String, String, boolean) */
    public static Presentation getPresentationFor(String scheme, String host, String type)
    {
        return getPresentationFor(scheme, host, type, true);
    }

    /** @see getPresentationFor(String, String, String, boolean) */
    public static Presentation getPresentationFor(VRL vrl, String type, boolean autocreate)
    {
        return getPresentationFor(vrl.getScheme(), vrl.getHostname(), type, autocreate);
    }

    /**
     * Checks the PresentationStore if there is already Presentation information
     * stored. If no presentation can be found and autocreate==true, a default
     * Presentation object will be created.
     * 
     * @param scheme
     *            scheme of resource
     * @param host
     *            hostname of resource
     * @param type
     *            VRS type of resource
     * @param autocreate
     *            whether to initialize a default Presentation object
     * @return
     */
    public static Presentation getPresentationFor(String scheme, String host, String type, boolean autocreate)
    {
        String id = createID(scheme, host, type);
        Presentation pres = null;

        pres=Presentation.getPresentation(id,false); 
        if (pres!=null)
            return pres; 

        if (autocreate == false)
            return null;

        pres = createDefault(); 
        
        //
        // Set defaults:
        //
        
        if (scheme.compareTo(VRS.MYVLE_SCHEME) == 0)
        {
            pres.setPreferredContentAttributeNames(new StringList(myvleAttributeNames));
            // do not sort MyVle !
            pres.setSortOption(SortOption.NEVER);
        }
        else if (scheme.compareTo(VRS.SRB_SCHEME) == 0)
        {
            pres.setPreferredContentAttributeNames(new StringList(VRSPresentation.defaultSRBAttributeNames));
        }
        else if (type.compareTo(VFS.DIR_TYPE) == 0)
        {
            pres.setPreferredContentAttributeNames(new StringList(VRSPresentation.defaultVFSAttributeNames));
        }
        else
        {
            pres.setPreferredContentAttributeNames(new StringList(VRSPresentation.defaultNodeAttributeNames));
        }
        
        pres.setIconAttributeName(ATTR_ICON);
        
        return pres;
    }
    
    private static String createID(String scheme, String host, String type)
    {
        if (scheme==null)
            throw new NullPointerException("Presentation must have at least a scheme"); 
        
        if (host==null)
            host="";
        
        if (type==null)
            type="";
        
        String id= scheme + "-" + host + "-" + type;
        return id; 
    }
    
    public static Presentation createDefault()
    {
        Presentation pres=new Presentation(); 
        initDefaults(pres);
        return pres; 
    }
    
    public static void initDefaults(Presentation pres)
    {
        pres.setAttributePreferredWidths(ATTR_ICON, 32, 32,999);
        // pres.setAttributePreferredWidth(ATTR_INDEX, 32);
        pres.setAttributePreferredWidths(ATTR_NAME, 48,200,999);
        pres.setAttributePreferredWidths(ATTR_RESOURCE_TYPE, 64,140,999);
        pres.setAttributePreferredWidths(ATTR_SCHEME, 64,100,999);
        pres.setAttributePreferredWidths(ATTR_HOSTNAME, 64,140,999);
        // pres.setAttributePreferredWidth(ATTR_PORT, 32);
        pres.setAttributePreferredWidths(ATTR_FILE_SIZE, 60,70,999);
        pres.setAttributePreferredWidths(ATTR_PATH, 100,200,999);
        pres.setAttributePreferredWidths(ATTR_RESOURCE_STATUS, 48,64,999);
        pres.setAttributePreferredWidths(ATTR_MODIFICATION_TIME, 100,120,999);
        pres.setAttributePreferredWidths(ATTR_CREATION_TIME, 100,120,999);
        
        // VQueues and VJobs:
        pres.setAttributePreferredWidth(ATTR_MAX_WALL_TIME, 100);
        pres.setAttributePreferredWidth(ATTR_NODE_TEMP_DIR, 160);
        
        pres.setPreferredContentAttributeNames(new StringList(defaultNodeAttributeNames));
    }
    
}
