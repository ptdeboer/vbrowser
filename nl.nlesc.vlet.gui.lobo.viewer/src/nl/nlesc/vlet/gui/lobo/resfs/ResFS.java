/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: ResFS.java,v 1.4 2011-04-18 12:27:36 ptdeboer Exp $  
 * $Date: 2011-04-18 12:27:36 $
 */ 
// source: 

package nl.nlesc.vlet.gui.lobo.resfs;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vfs.VFS;
import nl.nlesc.vlet.vfs.VFSFactory;
import nl.nlesc.vlet.vfs.VFSNode;
import nl.nlesc.vlet.vfs.VFileSystem;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.VRSFactory;
import nl.nlesc.vlet.vrs.VResourceSystem;

/** * 
 * Specifies a resource that will be obtained from a module
 * @author kboulebiar
 *
 */
public class ResFS extends VRSFactory
{

	@Override
	public String getName()
	{
		return "res";	
	}

	private String[] schemes=
		{
			"res"
		};
	
	@Override
	public String[] getSchemeNames()
	{
		return schemes;
	}

	
	@Override
	public void clear()
	{
		
	}

    @Override
    public String[] getResourceTypes()
    {
        String types[]=new String[1];
        types[0]="Res";
        return types; 
    }


	@Override
	public VResourceSystem createNewResourceSystem(VRSContext context,
			ServerInfo info, VRL location) throws VlException 
	{
		// should be singleton! 
		return new ResResourceSystem(context,location);
	}

}
