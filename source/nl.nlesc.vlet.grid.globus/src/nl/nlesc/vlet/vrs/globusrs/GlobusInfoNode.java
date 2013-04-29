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

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.data.VAttribute;
import nl.nlesc.vlet.vrs.vrl.VRL;

public class GlobusInfoNode extends VNode
{
	public static GlobusInfoNode createNode(VRSContext context,VRL vrl)
	{
		GlobusInfoNode node=new GlobusInfoNode(context,vrl);
		return node; 
	}	
	 
    public GlobusInfoNode(VRSContext context, VRL logicalLocation)
    {
        super(context, logicalLocation);
    }

	@Override
	public String getResourceType() 
	{
		return "GlobusInfo"; 
	}
	
	public String getName()
	{
		return "GlobusInfo";
	}
	
	public String[] getAttributeNames()
	{
	    StringList list=new StringList(super.getAttributeNames());
	    list.add("globus.version");
	    return list.toArray(); 
	}

    public VAttribute getAttribute(String name) throws VrsException
    {
        if (name.equals("globus.version"))
        {
            return new VAttribute(name,"4.1");
        }
        else
        {
            return super.getAttribute(name);
        }
        
    }
	
}

