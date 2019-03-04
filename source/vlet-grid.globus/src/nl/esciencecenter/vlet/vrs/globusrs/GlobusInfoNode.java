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

package nl.esciencecenter.vlet.vrs.globusrs;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;

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

    public Attribute getAttribute(String name) throws VrsException
    {
        if (name.equals("globus.version"))
        {
            return new Attribute(name,"4.1");
        }
        else
        {
            return super.getAttribute(name);
        }
        
    }
	
}

