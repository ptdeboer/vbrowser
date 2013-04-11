package nl.nlesc.vlet.vrms;
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

//package nl.uva.vlet.vrms;
//
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.vrl.VRL;
//import nl.uva.vlet.vrms.grid.GridNeighbourhood;
//import nl.uva.vlet.vrs.VCompositeNode;
//import nl.uva.vlet.vrs.VNode;
//import nl.uva.vlet.vrs.VRSContext;
//
//public class ServerInstanceGroup extends VCompositeNode 
//{
//	public static final String SERVER_INSTANCES_NAME="ServerInstances"; 
//	 
//	public ServerInstanceGroup(VRSContext context)
//	{
//		super(context, new VRL("myvle",null,"/"+GridNeighbourhood.GRID_NEIGHBOURHOOD_NAME+"/"+SERVER_INSTANCES_NAME));
//	}
//
//	@Override
//	public String getType()
//	{
//		return "ServerInstances"; 
//	}
//
//	public VNode[] getNodes() throws VlException
//	{
//		return vrsContext.getResourceSystemNodes(); 
//	}
//
//	public String[] getResourceTypes()
//	{
//		return null;
//	}
//
//	public VRL getDescriptionLocation() throws VlException
//	{
//		return null;
//	}
//
//	public VRL getTargetLocation() throws VlException
//	{
//		return null;
//	}
//
//}
