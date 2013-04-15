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

package nl.nlesc.vlet.vrs.vfs;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.VRSFactory;
import nl.nlesc.vlet.vrs.VResourceSystem;

/** 
 * The VFSFactory for VFileSystems classes. 
 * <p>
 * This is the VRSFactory class for {@link VFileSystem} Resources. 
 * It is used by the {@link nl.nlesc.vlet.vrs.Registry} to open a remote filesystem. 
 * 
 * @author P.T. de Boer
 *
 */
public abstract class VFSFactory extends VRSFactory
{
	// ========================================================================
    // Instance Methods
    // ========================================================================
    /** Enforce public constructor for subclasses ! */ 
    public VFSFactory()
    {
        super();  
    }

    public void init()
    {
    }

    /** 
     * Return types of resources it supports.<br>
     * Overide this method to create custom types.<br>
     * Default the VFS (Virtual File System) should support FILE and DIR types !
     */
    public String[] getResourceTypes()
    {
        return VFS.defaultChildTypes; 
    }
   
	public VResourceSystem openResourceSystem(VRSContext context,VRL location) throws VlException
	{
		return this.openFileSystem(context, location); 
	}
	 
    public VFSNode openLocation(VRSContext context,String location) throws VlException
    {
        return openLocation(context,new VRL(location)); 
    }
    
	public VFSNode openLocation(VRSContext context,VRL location) throws VlException 
	{
		 return openFileSystem(context,location).openLocation(location); 
	}
	
	/**
	 * Open location and return {@link VFileSystem} instance which can handle resources 
	 * associated with the specified VRL. 
	 * 
	 * @param context the VRSContext to use 
	 * @param location actual location
	 * @return new or cached VFileSystem instance 
	 * @throws VlException
	 */
	public VFileSystem openFileSystem(VRSContext context, VRL location) throws VlException
	{
		return (VFileSystem)super.openResourceSystem(context,location); 
	}

	// ---------------------------------------------------------------
	// Super interface implementation to create a ResourceSystem. 
	// Is implemented by calling createNewFileSystem to 
	// so the the instance can be downcasted to VFileSystem. 
	// ---------------------------------------------------------------
	public VFileSystem createNewResourceSystem(VRSContext context,ServerInfo info, VRL location) throws VlException
	{
		return createNewFileSystem(context,info,location); 
	}

	// =======================================================================
	// Abstract Interface 
	// =======================================================================

	/**
	 * Factory method creating a new {@link VFileSystem} instance. 
	 * Will only be called when a new file system is needed. 
	 * Instance will be used for similar locations.
	 */
	public abstract VFileSystem createNewFileSystem(VRSContext context,ServerInfo info, VRL location) throws VlException; 
		 
	
}
