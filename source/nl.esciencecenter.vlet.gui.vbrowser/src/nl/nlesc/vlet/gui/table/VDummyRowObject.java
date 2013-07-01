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

package nl.nlesc.vlet.gui.table;

import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.gui.MasterBrowser;
import nl.nlesc.vlet.gui.data.ResourceRef;
import nl.nlesc.vlet.gui.view.VComponent;
import nl.nlesc.vlet.gui.view.VContainer;

/** 
 * Current VComponent compatible Row Object.
 * Needed for GUI actions directly on a single Row   
 */ 

public class VDummyRowObject implements VComponent
{
	MasterBrowser mb;
	private TablePanel tablePanel;
	ResourceRef resourceRef=null; 
	
	public VDummyRowObject(TablePanel panel,MasterBrowser master,ResourceRef ref)
	{
		this.mb=master; 
		this.tablePanel=panel; 
		this.resourceRef=ref; 
	}
	
	public MasterBrowser getMasterBrowser()
	{
		return mb;
	}

	public VRL getVRL()
	{
		return this.resourceRef.getVRL(); 
	}

	public VContainer getVContainer()
	{
		return this.tablePanel; 
	}

	public String getResourceType()
	{
		return this.resourceRef.getResourceType(); 
	} 
	
	public ResourceRef getResourceRef()
	{
		return this.resourceRef;  
	} 
	
}
