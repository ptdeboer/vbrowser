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

import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.nlesc.vlet.vrs.vrl.VRL;

/**
 * Replica Interface for files which support replicas (LFC).   
 */ 
public interface VReplicatable
{
    /**
     * List all replicas. 
     */ 
	public VRL[] getReplicas() throws VrsException;
	
	/**
	 * Register Replica URIs. 
	 * This method does not create any replicas and does not do any checking whether VRLs are 
	 * valid ! 
	 */
	public boolean registerReplicas(VRL vrls[]) throws VrsException;

	/** 
	 * Unregister Replicas URIs.
	 * Does not delete them and does not do any checking, it just removed
	 * matching VRLs from the LFC registry! 
	 */
	public boolean unregisterReplicas(VRL vrls[]) throws VrsException; 
	
	/**
	 * Replicate to specified Storage Element, returns new Replica VRL. 
	 * Implementation should update subTask fields of monitor ! 
	 */ 
    public VRL replicateTo(ITaskMonitor monitor, String storageElement) throws VrsException;

    /** Delete Replica and unregister. */ 
    public boolean deleteReplica(ITaskMonitor monitor, String storageElement) throws VrsException; 
}
