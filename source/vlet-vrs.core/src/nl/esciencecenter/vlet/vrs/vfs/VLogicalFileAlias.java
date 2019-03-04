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

package nl.esciencecenter.vlet.vrs.vfs;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/** 
 * Interface for Logical File Aliases.  
 * When a VFile interface supports Aliases, more then one "logical file name"
 * can be added to that file. All logical file names or aliases are equivalent
 * and point to the same (physical) file. 
 * In the case of LFC this physical file is identified by it's Grid Unique IDentifier
 * or "GUID". 
 * <p>
 * Warning: For LFC Aliases are implemented as Symbolic Links to one 'master'
 * entry so if the 'master file' is deleted, aliases to that file will be 'broken'.
 *  
 * @see nl.uva.vlet.vfs.VSymbolicLInk
 * @see nl.esciencecenter.vlet.vrs.vfs.VGlobalUniqueID
 * @see nl.esciencecenter.vlet.vrs.vfs.VLinkListable
 * @author P.T. de Boer (based upon the LCG Alias Interface) 
 */
public interface VLogicalFileAlias extends VGlobalUniqueID, VSymbolicLink, VLinkListable
{
    /** 
     * Returns whether this file is an Alias or Not.
     */       
    public boolean isAlias() throws VrsException; 
   
    /** 
     * Creates another alias to this File Resource.
     * Currently used by the LFC Implementations. 
     * The host+port information might be ignored as typically 
     * only the path information is used to create another
     * logical file path to this resource.
     * 
     * @throws VrsException 
     */
    public VRL addAlias(VRL newAlias) throws VrsException;
    
    /**
     * If this file is an alias, return the alias target
     * this file is an alias for.
     * Currently implemented by the LFC FileSystem. 
     * This method is similar to VSymbolicLink#getSymbolicLinkTargetVRL. 
     * 
     * @see VSymbolicLink#getSymbolicLinkTargetVRL()
     * @return return target VRL or NULL if it has none.  
     * @throws VrsException
     */
    public VRL getAliasTarget() throws VrsException; 
    
    /**
     * If this file or alias is identifiable by a Unique IDentifier
     * this method will return it. 
     * For LFC Files this will be the GUID.  
     * @return GUID 
     */
    public String getGUID() throws VrsException;
    
    /**
     * Returns all links (or aliases) to this file as VRLs. 
     * Also lists the master LFN of this file so the number
     * of links for an LFC file at least '1'.  
     * @throws VrsException */ 
    public VRL[] getLinksTo() throws VrsException; 
    
    /**
     * Extra Method to update the registered file size of an LFC File. 
     * This does NOT change the actual file size of the replicas, but 
     * updates size as stored in the meta data catalog. 
     */ 
    public void updateFileSize(long size) throws VrsException; 
}
