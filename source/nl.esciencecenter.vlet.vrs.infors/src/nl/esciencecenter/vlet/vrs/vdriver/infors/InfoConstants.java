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

package nl.esciencecenter.vlet.vrs.vdriver.infors;

import nl.esciencecenter.vlet.vrs.VRS;

public class InfoConstants
{
    // schemes 
    
    public static final String INFO_SCHEME = "info";
    
    // Types 
    
    public static final String GRID_NEIGHBOURHOOD_NAME="Grid Neighbourhood";
    
    public static final String GRID_NEIGHBOURHOOD_TYPE=VRS.INFO_GRID_NEIGHBOURHOOD;  // "GridNeighbourhood";

    // Generic non editable info type: 
    public static final String INFONODE_TYPE="InfoNode";

    public static final String VO_TYPE = "VO";
    
    // names,also used as info path!  
    
    public static final String STORAGE_INFO_NAME="Storage";
    
    public static final String COMPUTATIONAL_INFO_NAME="Computional";
    
    public static final String FILECATALOGS_INFO_NAME="FileCatalogs";

    public static final String VOGROUPS_FOLDER_NAME = "VOs";
    
    public static final String VOGROUPS_FOLDER_TYPE = "VOGroupsFolder";
    
    // ids, other consts  
    
    public static final String INFO_INSTANCE_ID="info-resourcesystem-id";
    
    public static final String NETWORK_INFO = "NetworkInfo"; 

    public static final String HOST_INFO_NODE = "HostInfo";

    // === Attributes === 
    public static final String ATTR_NETWORK_ADRESS="networkAddress";

    public static final String ATTR_TCP_CONNECTION_TIMEOUT = "tcpConnectionTimeout";

    public static final String LOCALSYSTEM_TYPE = VRS.INFO_LOCALSYSTEM; // "LocalSystem";
    
    public static final String LOCALSYSTEM_NAME = "Local System";

    public static final String ATTR_CONFIGURED_VOS = "configuredVOs";

    public static final String ATTR_SYSTEMHOSTNAME = "systemHostname"; ;
    
    public static final String ATTR_SYSTEMOS = "systemOS";

    public static final String ATTR_JAVAVERSION = "javaVersion";

    public static final String ATTR_JAVAHOME = "javaHome";   
    

}
