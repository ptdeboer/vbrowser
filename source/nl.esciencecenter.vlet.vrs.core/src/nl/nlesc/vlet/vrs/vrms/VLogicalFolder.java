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

package nl.nlesc.vlet.vrs.vrms;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.vrs.VComposite;
import nl.nlesc.vlet.vrs.VNode;

public interface VLogicalFolder extends VComposite, VLogicalResource
{
	/** Unlink from internal set, Does not call 'delete' on the VNode bute
	 * removes it from the internal vector only */ 
	boolean unlinkNode(VNode node) throws VrsException;
	
	/** Unlink nodes from internal set
	 * Does not call 'delete' on the VNodes but
     * removes it from the internal vector only 
	 * @throws VrsException */ 
	boolean unlinkNodes(VNode node[]) throws VrsException;

	/** Find a child in this resource matching the VRL */ 
	public VNode findNode(VRL childVRL,boolean recurse) throws VrsException; 
}
