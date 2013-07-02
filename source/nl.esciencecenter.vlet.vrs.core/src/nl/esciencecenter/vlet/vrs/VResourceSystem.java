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

package nl.esciencecenter.vlet.vrs;

import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/**
 * VResourceSystem is a factory class for VNodes. 
 * It represents an abstract resource like a remote (web) service or a file system. 
 * @author P.T. de Boer 
 */
public interface VResourceSystem
{
	/**
	 * Return unique identifier for this Resource System.
	 * This is used in the VRSContext to manage resource system
	 * instances.    
	 * @return Unique Identifier String 
	 */
	public String getID();  
	
	/** Returns ResourceSystem VRL */ 
	public VRL getVRL(); 
	
    /** 
     * Resolve relative path or URI part to this Filesystem and return Absolute VRL.
     * Actual result depends on implementing ResourceSystem. 
     */ 
    public VRL resolve(String path) throws VRLSyntaxException; 
    
	/**
	 * Main VNode factory method. 
	 * Open the location and return the resource from this ResourceSystem specified
	 * by the VRL. 
	 */
	public VNode openLocation(VRL vrl) throws VrsException;
	
	/** 
	 * Returns VRSContext associated with this ResourceSystem  
	 */ 
	public VRSContext getVRSContext();

	/**
	 * Setup connection if not already connected. 
	 * Multiple connect() calls may occur. If already connected ignore 
	 * any successive invocations.  
	 */
	public void connect() throws VrsException ;
	
	/** 
	 * Disconnected and close/cleanup all resources associated with this 
	 * resource system.
	 * After this call a connect() may be called. 
	 */
    public void disconnect() throws VrsException ;

    /** 
     * Dispose all resources, object may not be used anymore.
     */
    public void dispose();
}
