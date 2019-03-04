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

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.grid.globus.GlobusUtil;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.VRSFactory;
import nl.esciencecenter.vlet.vrs.VResourceSystem;

/** 
 * Globus Information System. 
 * 
 * By creating a Globus Resource Factory, the Grid-Globus bindings will be initialized
 * when this factory is created!
 * The current resource system provides information about the Globus implementation. 
 */
public class GlobusRSFactory extends VRSFactory
{
	// "grid" is covered by the info system, as "grid" is generic, and "globus" is 
	// implementation specific. 
	// " voms" shouldbe covered by GridInfosystem, this (minimal) resource system
	// is only for globus stuff. 
	
	private static String schemes[]={"globus"};  
	
    static 
    {
        // Static Initializer! -> registers Globus Bindings
        GlobusUtil.init(); 
    }
    
    @Override
    public void clear()
    {
        
    }

    @Override
    public String getName()
    {
        return "GlobusRS"; 
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public String[] getSchemeNames()
    {
        return schemes; 
    }

	@Override
	public VResourceSystem createNewResourceSystem(VRSContext context,
			ServerInfo info, VRL location) throws VrsException 
	{
		return new GlobusInfoSystem(context,location); 
	}

}
