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

package nl.nlesc.vlet.vrs;

import nl.nlesc.vlet.exception.VlException;

/**
 * VDuplicatable is the Cloneable interface for VRS Resource Objects (VNodes). 
 * This method returns a copy of the VRS Object and does not 
 * perform a copy of the actual resource it points to.  
 * This is a different interface the Duplicatable which is about data types. 
 * This interface is for VNodes (and sub classes). 
 */
public interface VDuplicatable<T extends VNode>
{ 
	/**
	 * Create a deep copy of this resource and all it's children. 
	 * For logicalresource nodes: Do not duplicate the logical resources. 
	 * For LinkNode, do not update/share the same storage location 
	 */ 
    public T duplicate() throws VlException;
	
	///** Whether a shallow copy is supported */ 
	//public boolean isShallowDuplicateSupported();
	
    //public VNode duplicate(boolean shallow) throws VlException;
	
}
