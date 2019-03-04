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

package nl.esciencecenter.vlet.vrs.vrms;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VComposite;
import nl.esciencecenter.vlet.vrs.VNode;

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
