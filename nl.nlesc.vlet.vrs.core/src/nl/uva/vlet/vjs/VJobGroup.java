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

package nl.uva.vlet.vjs;

import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VCompositeNode;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRSContext;

public abstract class VJobGroup extends VCompositeNode
{
	String childTypes[]=
		{
			VJS.TYPE_VJOB,
			VJS.TYPE_VJOBGROUP
		};
	
	public VJobGroup(VRSContext context, VRL vrl)
	{
		super(context, vrl);
	}

	@Override
	public String getResourceType()
	{
		return VJS.TYPE_VJOBGROUP;
	}

	public VNode[] getNodes() throws VlException
	{
		return getJobs(); 
	}

	public String[] getResourceTypes()
	{
		return childTypes; 
	}
	
	// =======================================================================
	// Abstrct interface  
	// ======================================================================= 
	
	abstract public String getGroupID();
	
	abstract public VNode[] getJobs(); 
}
