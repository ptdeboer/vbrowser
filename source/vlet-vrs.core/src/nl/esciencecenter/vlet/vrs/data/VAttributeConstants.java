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

package nl.esciencecenter.vlet.vrs.data;

import nl.esciencecenter.vbrowser.vrs.data.AttributeNames;

public class VAttributeConstants extends AttributeNames 
{
    public static final String ATTR_ALLOW_3RD_PARTY = "allow3rdParty";

    public static final String ATTR_ATTEMPTS = "attempts";

    /** Comma seperated attribute names which the resource supports */
    public static final String ATTR_ATTRIBUTE_NAMES = "attributeNames";

    public static final String ATTR_BROKER_NAME = "brokerName";

    /** Character set used by the resource if the content contains text */
    public static final String ATTR_CHARSET = "charSet";

    /** Return checksum value if supported */
    public static final String ATTR_CHECKSUM = "checksum";

    /**
     * Default Checksum type used for the Checksum value. Is first checksum type
     * as returned by getChecksumTypes if the resource implements the VChecksum
     * interface.
     * 
     */
    public static final String ATTR_CHECKSUM_TYPE = "checksumType";

    /**
     * Comma separated list of checksum types, if the resource implements the
     * VChecksum interface.
     */
    public static final String ATTR_CHECKSUM_TYPES = "checksumTypes";

    /** Destination directory used by Reliable File Transfers (RFT) */
    public static final String ATTR_DEST_DIRNAME = "destDirname";

    /** Destination file name used by Reliable File Transfers (RFT) */
    public static final String ATTR_DEST_FILENAME = "destFilename";

    /** Destination hostname name used by Reliable File Transfers (RFT) */
    public static final String ATTR_DEST_HOSTNAME = "destHostname";

    /** Destination path used by Reliable File Transfers (RFT) */
    public static final String ATTR_DEST_PATH = "destPath";

    public static final String ATTR_DEST_URL = "destinationUrl";

    /**
     * Extra error text if the resource has an error or an exceptio has been
     * thrown.
     */
    public static final String ATTR_ERROR_TEXT = "errorText";

    /** Value of exists() method if implemented. */
    public static final String ATTR_EXISTS = "exists";

    /** Spell out as Grid UID instead of confusinf GUID */
    public static final String ATTR_GRIDUID = "gridUniqueID";

    public static final String ATTR_ID = "id";

    public static final String ATTR_INDEX = "index";

    public static final String ATTR_ISCOMPOSITE = "isComposite";

    /** Whether the resource properties are 'editable'. */
    public static final String ATTR_ISEDITABLE = "isEditable";

    public static final String ATTR_ISREADABLE = "isReadable";

    public static final String ATTR_ISWRITABLE = "isWritable";

    /** Whether this node is a VLink */
    public static final String ATTR_ISVLINK = "isVLink";

    /** Optional error text explaining the (error) status of a resource */
    public static final String ATTR_JOB_ERROR_TEXT = "errorText";

    /**
     * Whether the job resource has encountered an error and has stopped
     * executing
     */
    public static final String ATTR_JOB_HAS_ERROR = "jobHasError";

    /**
     * Whether Job has terminated or not. A failed or canceled job will be
     * considered 'terminated' as well.
     */
    public static final String ATTR_JOB_HAS_TERMINATED = "jobHasTerminated";

    /** Whether Job is running or not. */
    public static final String ATTR_JOB_IS_RUNNING = "jobIsRunning";

    // /** Time of last status update */
    // public static final String ATTR_JOB_ENTERED_TIME = "jobEnteredTime";

    /** Time of last status update */
    public static final String ATTR_JOB_STATUS_UPDATE_TIME = "jobStatusUpdateTime";

    public static final String ATTR_JOB_SUBMISSION_TIME = "jobSubmissionTime";

    /** Job ID as returned by the Job Manager */
    public static final String ATTR_JOBID = "jobId";

    /** Job URI as returned by the Job Manager */
    public static final String ATTR_JOBURI = "jobUri";

    /**
     * Virtual Resource Location URI or VRL.
     * 
     * @see nl.esciencecenter.vlet.vrs.VNode#getVRL() VNode.getVRL()
     */
    public static final String ATTR_LOCATION = "location";

    public static final String ATTR_MAX_WALL_TIME = "maxWallTime";
    /**
     * Logical Resource name. For files and directories this is the basename of
     * the path.
     */
    public static final String ATTR_NAME = "name";

    public static final String ATTR_NODE_TEMP_DIR = "nodeTempDir";

    public static final String ATTR_NRACLENTRIES = "nrACLEntries";

    /**
     * VAttribute name for the nr of childs nodes (Only for VComposite and
     * subclasses)
     */
    public static final String ATTR_NRCHILDS = "nrChilds";

    /**
     * Unambiguous Parent directory attribute for both files and directories.
     * 
     * @see #ATTR_DIRNAME
     */
    public static final String ATTR_PARENT_DIRNAME = "parentDirname";

    /** Make sure Global.PASSIVE_MODE and ATTR_PASSIVE_MODE are the same ! */
    // public static final String ATTR_PASSIVE_MODE =
    // GlobalConfig.PROP_PASSIVE_MODE;



    public static final String ATTR_PERSISTANT = "persistant";

    public static final String ATTR_QUEUE_NAME = "queueName";

    public static final String ATTR_RECURSIVE = "recursive";

    public static final String ATTR_REQUEST_STATUS = "requestStatus";

    public static final String ATTR_RESOURCE_CLASS = "resourceClass";

    public static final String ATTR_RESOURCE_TYPES = "resourceTypes";

    public static final String ATTR_SHELL_PATH = "shellPath";

    public static final String ATTR_SHOW_SHORTCUT_ICON = "showShortCutIcon";

    /** Status attribute, if implemented by resource */
    public static final String ATTR_STATUS = "status";

    /**
     * Extra status information. Human readable text/explanation about current
     * job status. For example WMS "Reason".
     */
    public static final String ATTR_JOB_STATUS_INFORMATION = "jobStatusInformation";

    /** Storage element used by LFC and SRM resources */
    public static final String ATTR_STORAGE_ELEMENT = "storageElement";

    /**
     * If the resource is a symbolic link, this attribute return the target path
     * on the filesystem.
     * 
     * @see #ATTR_ISSYMBOLICLINK
     */
    public static final String ATTR_SYMBOLICLINKTARGET = "symbolicLinkTarget";

    /** Hidden attribute to quickly know whether target is composite. */
    public static final String ATTR_TARGET_IS_COMPOSITE = "targetIsComposite";

    /** Hidden attribute: mimetype of linkTarget. */
    public static final String ATTR_TARGET_MIMETYPE = "targetMimetype";

    public static final String ATTR_TRANSPORT_URI = "transportUri";

    public static final String ATTR_GFTPUNIQUE = "gftpUnique";

    /** Unix style octal file mode value. For example "0755" */
    public static final String ATTR_UNIX_FILE_MODE = "unixFileMode";

}
