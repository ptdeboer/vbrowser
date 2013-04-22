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

package nl.nlesc.vlet.gui.presentation;

import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_ATTEMPTS;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_CREATION_TIME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_DEST_HOSTNAME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_DEST_URL;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_FAULT;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_ICON;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_INDEX;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_LENGTH;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_MAX_WALL_TIME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_MIMETYPE;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_MODIFICATION_TIME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_NODE_TEMP_DIR;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_PATH;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_PERMISSIONS_STRING;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_RESOURCE_TYPE;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_SCHEME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_SOURCE_FILENAME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_SOURCE_HOSTNAME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_SOURCE_URL;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_STATUS;

import nl.esciencecenter.vbrowser.vb2.ui.presentation.UIPresentation;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.vfs.VFS;
import nl.nlesc.vlet.vrs.vrl.VRL;

/** 
 * Factory class for UIPresentation of VRS Nodes. 
 */
public class VRSPresentation
{
    public static String defaultVFSAttributeNames[] = { ATTR_ICON, ATTR_NAME, ATTR_RESOURCE_TYPE, ATTR_LENGTH,
    // ATTR_MODIFICATION_TIME_STRING,
            ATTR_MODIFICATION_TIME, ATTR_MIMETYPE, ATTR_PERMISSIONS_STRING,
    // ATTR_ISHIDDEN,
    // ATTR_ISLINK
    // VFS.ATTR_ISFILE,
    // VFS.ATTR_ISDIR
    };

    /** Default Attribute Name to show for VFSNodes */
    public static String defaultSRBAttributeNames[] = { ATTR_ICON, ATTR_NAME, ATTR_RESOURCE_TYPE, ATTR_LENGTH, "Resource",
    // ATTR_MODIFICATION_TIME_STRING,
            ATTR_MODIFICATION_TIME, ATTR_MIMETYPE,
    // ATTR_PERMISSIONS_STRING,
    // ATTR_ISHIDDEN,
    // ATTR_ISLINK
    // VFS.ATTR_ISFILE,
    // VFS.ATTR_ISDIR
    };
    
    /** Default Attribute Name to show for VNodes */
    public static String defaultNodeAttributeNames[] = { ATTR_ICON, ATTR_RESOURCE_TYPE, ATTR_NAME,
    // ATTR_LENGTH,
            ATTR_MIMETYPE };

    /** Default Attribute Name to show for VNodes */
    public static String myvleAttributeNames[] = { ATTR_ICON, ATTR_RESOURCE_TYPE, ATTR_NAME, ATTR_SCHEME, ATTR_HOSTNAME,
            ATTR_PATH,
    // ATTR_LENGTH,
    // ATTR_MIMETYPE
    };

    /** Default Attribute names to show for RTFSJobs */
    public static String defaultRFTJobAttributeNames[] = { ATTR_ICON, ATTR_RESOURCE_TYPE, ATTR_NAME, "requestStatus",
            ATTR_FAULT, "transfersFinished", "transfersActive", "transfersRestarted", "transfersFailed",
            "transfersCancelled", "transfersPending",

    };

    /** Default Attribute names to show for RTFSJobs */
    public static String defaultRFSTransferAttributeNames[] = { ATTR_ICON, ATTR_RESOURCE_TYPE, ATTR_NAME, ATTR_ATTEMPTS,
            ATTR_STATUS, ATTR_FAULT, ATTR_SOURCE_HOSTNAME, ATTR_SOURCE_FILENAME, ATTR_SOURCE_URL, ATTR_DEST_HOSTNAME,
            ATTR_DEST_URL

    };

    /** @see getPresentationFor(String, String, String, boolean) */
    public static UIPresentation getPresentationFor(String scheme, String host, String type)
    {
        return getPresentationFor(scheme, host, type, true);
    }

    /** @see getPresentationFor(String, String, String, boolean) */
    public static UIPresentation getPresentationFor(VRL vrl, String type, boolean autocreate)
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
    public static UIPresentation getPresentationFor(String scheme, String host, String type, boolean autocreate)
    {
        String id = createID(scheme, host, type);
        UIPresentation pres = null;

        pres=UIPresentation.getPresentation(id,false); 
        if (pres!=null)
            return pres; 

        if (autocreate == false)
            return null;

        pres = createDefault(); 
        UIPresentation.putUIPresentation(id,pres); 
        //
        // Set defaults:
        //

        if (scheme.compareTo(VRS.MYVLE_SCHEME) == 0)
        {
            pres.setChildAttributeNames(myvleAttributeNames);
            // dont sort MyVle !
            pres.setAutoSort(false);
        }

        // presentation=this.getVRS().getDefaultPresentation();
        // if (this.vnode.getType().compareTo(VRS.RFTS_JOB_TYPE)==0)
        else if (scheme.compareTo(VRS.RFTS_SCHEME) == 0)
        {

            if (type.compareTo(VRS.RFTS_JOB_TYPE) == 0)
            {
                // RFT Job, presentation is about childs: which is RFT
                // Transfers:
                pres.setChildAttributeNames(VRSPresentation.defaultRFSTransferAttributeNames);
            }
            else if (type.compareTo(VRS.RFTS_SERVER_TYPE) == 0)
            {
                pres.setChildAttributeNames(VRSPresentation.defaultRFTJobAttributeNames);// null-
                                                                                      // >
                                                                                      // show
                                                                                      // ALL
            }
            else
            {
                // not possible
                pres.setChildAttributeNames(null);
            }

            // names are numbers in RFT:
            pres.setAttributePreferredWidth(ATTR_NAME, 40);

        }
        else if (scheme.compareTo(VRS.SRB_SCHEME) == 0)
        {
            pres.setChildAttributeNames(VRSPresentation.defaultSRBAttributeNames);
        }
        else if (type.compareTo(VFS.DIR_TYPE) == 0)
        {
            pres.setChildAttributeNames(VRSPresentation.defaultVFSAttributeNames);
        }
        // Handled by WMS and LB implementations: 
//        else if (type.compareTo(VJS.TYPE_VJOBMANAGER) == 0)
//        {
//            pres.setChildAttributeNames(Presentation.defaultJobManagerAttributeNames);
//            // shorter attribute widths:
//            pres.setAttributePreferredWidth(ATTR_NAME, 120);
//            pres.setAttributePreferredWidth(ATTR_PATH, 120);
//        }
//        else if ((type.compareTo(VJS.TYPE_VJOBGROUP) == 0) || (type.compareToIgnoreCase("MyJobs") == 0))
//        {
//            pres.setChildAttributeNames(Presentation.defaultJobGroupAttributeNames);
//            // shorter attribute widths:
//            pres.setAttributePreferredWidth(ATTR_NAME, 160);
//            pres.setAttributePreferredWidth(ATTR_PATH, 180);
//            pres.setAttributePreferredWidth(ATTR_JOB_ERROR_TEXT, 240);
//            pres.setAttributePreferredWidth(ATTR_JOB_STATUS_UPDATE_TIME, 180);
//            pres.setAttributePreferredWidth(ATTR_JOB_SUBMISSION_TIME, 180);
//
//            pres.setAutoSort(false);
//            pres.getAutoSort();
//        }
        else
        {
            pres.setChildAttributeNames(VRSPresentation.defaultNodeAttributeNames);
        }

        return pres;
    }

    public static void putUIPresentation(VRL vrl, String type, UIPresentation pres)
    {
        if (pres == null)
            return;

        String id = createID(vrl.getScheme(), vrl.getHostname(), type);
        UIPresentation.putUIPresentation(id, pres);
        
    }

    private static String createID(String scheme, String host, String type)
    {
        return scheme + "-" + host + "-" + type;
    }
    
    public static UIPresentation createDefault()
    {
        UIPresentation pres=new UIPresentation(); 
        initDefaults(pres);
        return pres; 
    }
    
    public static void initDefaults(UIPresentation pres)
    {
        pres.setAttributePreferredWidth(ATTR_ICON, 32);
        pres.setAttributePreferredWidth(ATTR_INDEX, 32);
        pres.setAttributePreferredWidth(ATTR_NAME, 200);
        pres.setAttributePreferredWidth(ATTR_RESOURCE_TYPE, 140);
        pres.setAttributePreferredWidth(ATTR_SCHEME, 60);
        pres.setAttributePreferredWidth(ATTR_HOSTNAME, 140);
        pres.setAttributePreferredWidth(ATTR_LENGTH, 70);
        pres.setAttributePreferredWidth(ATTR_PATH, 200);
        pres.setAttributePreferredWidth(ATTR_STATUS, 48);
        pres.setAttributePreferredWidth(ATTR_MODIFICATION_TIME, 120);
        pres.setAttributePreferredWidth(ATTR_CREATION_TIME, 120);
        // RFT attributes
        pres.setAttributePreferredWidth(ATTR_SOURCE_URL, 200);
        pres.setAttributePreferredWidth(ATTR_DEST_URL, 200);
        pres.setAttributePreferredWidth(ATTR_DEST_HOSTNAME, 128);
        pres.setAttributePreferredWidth(ATTR_SOURCE_HOSTNAME, 128);
        pres.setAttributePreferredWidth(ATTR_FAULT, 120);
        // VQueues and VJobs:
        pres.setAttributePreferredWidth(ATTR_MAX_WALL_TIME, 100);
        pres.setAttributePreferredWidth(ATTR_NODE_TEMP_DIR, 160);
    }
    
}
