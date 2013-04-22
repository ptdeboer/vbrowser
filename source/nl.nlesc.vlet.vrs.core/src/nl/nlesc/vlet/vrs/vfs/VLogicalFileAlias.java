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

package nl.nlesc.vlet.vrs.vfs;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrs.vrl.VRL;

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
 * @see nl.nlesc.vlet.vrs.vfs.VGlobalUniqueID
 * @see nl.nlesc.vlet.vrs.vfs.VLinkListable
 * @author P.T. de Boer (based upon the LCG Alias Interface) 
 */
public interface VLogicalFileAlias extends VGlobalUniqueID, VSymbolicLink, VLinkListable
{
    /** 
     * Returns whether this file is an Alias or Not.
     */       
    public boolean isAlias() throws VlException; 
   
    /** 
     * Creates another alias to this File Resource.
     * Currently used by the LFC Implementations. 
     * The host+port information might be ignored as typically 
     * only the path information is used to create another
     * logical file path to this resource.
     * 
     * @throws VlException 
     */
    public VRL addAlias(VRL newAlias) throws VlException;
    
    /**
     * If this file is an alias, return the alias target
     * this file is an alias for.
     * Currently implemented by the LFC FileSystem. 
     * This method is similar to VSymbolicLink#getSymbolicLinkTargetVRL. 
     * 
     * @see VSymbolicLink#getSymbolicLinkTargetVRL()
     * @return return target VRL or NULL if it has none.  
     * @throws VlException
     */
    public VRL getAliasTarget() throws VlException; 
    
    /**
     * If this file or alias is identifiable by a Unique IDentifier
     * this method will return it. 
     * For LFC Files this will be the GUID.  
     * @return GUID 
     */
    public String getGUID() throws VlException;
    
    /**
     * Returns all links (or aliases) to this file as VRLs. 
     * Also lists the master LFN of this file so the number
     * of links for an LFC file at least '1'.  
     * @throws VlException */ 
    public VRL[] getLinksTo() throws VlException; 
    
    /**
     * Extra Method to update the registered file size of an LFC File. 
     * This does NOT change the actual file size of the replicas, but 
     * updates size as stored in the meta data catalog. 
     */ 
    public void updateFileSize(long size) throws VlException; 
}
