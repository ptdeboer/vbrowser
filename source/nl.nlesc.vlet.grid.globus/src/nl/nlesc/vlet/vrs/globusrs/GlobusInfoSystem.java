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

package nl.nlesc.vlet.vrs.globusrs;

import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.VResourceSystem;
import nl.nlesc.vlet.vrs.vdriver.infors.CompositeServiceInfoNode;
import nl.nlesc.vlet.vrs.vrl.VRL;

public class GlobusInfoSystem extends CompositeServiceInfoNode<VNode> implements VResourceSystem
{

	public GlobusInfoSystem(VRSContext context, VRL vrl) 
	{
		super(context, vrl);
	}

	@Override
	public String getID() 
	{
		return "globusinfo";
	}

	@Override
	public VRL resolve(String path) throws VRLSyntaxException
	{
	    return this.getVRL().resolveSibling(path);
    }
	 
	@Override
	public VNode openLocation(VRL vrl) throws VlException 
	{
		GlobusInfoNode node=GlobusInfoNode.createNode(this.getVRSContext(),vrl); 
		return node; 
	}


	@Override
	public void connect() throws VlException 
	{//dummy
	}

	@Override
	public void disconnect() throws VlException 
	{//dummy
	}

	@Override
	public void dispose() 
	{//dummy
	}

	@Override
	public String getResourceType() 
	{
		return "GlobusInfo"; 
	}

	@Override
	public String[] getResourceTypes() 
	{
		return null;
	}

	
}
