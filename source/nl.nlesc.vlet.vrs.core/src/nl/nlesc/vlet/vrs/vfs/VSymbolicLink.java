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

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/**
 * Interface for VFile objects which support Unix style symbolic links. 
 * If a VFile implements this interface it means the filesystem supports
 * symbolic links, but the actual file doesn't need to be one.  
 * Use isSymbolicLink() to detect whether the file really is a symbolic link. 
 * Note that symbolic link implementations might differ between filesystem 
 * implementation.  
 * <p>
 * Currently the methods are already integrated in the VFSNode class, but 
 * by default will not handle symbolic links. 
 *  
 * @author P.T. de Boer
 */
public interface VSymbolicLink
{
    /**
     * Returns whether this (file) resource is a symbolic link 
     * or not.<br>
     */   
    boolean isSymbolicLink() throws VrsException; 
    
    /**
     * Returns SymbolicLink target if it has one.
     */
    VRL getSymbolicLinkTargetVRL() throws VrsException; 
}
