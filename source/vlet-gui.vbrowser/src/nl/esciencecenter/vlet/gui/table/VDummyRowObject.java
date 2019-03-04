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

package nl.esciencecenter.vlet.gui.table;

import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.MasterBrowser;
import nl.esciencecenter.vlet.gui.data.ResourceRef;
import nl.esciencecenter.vlet.gui.view.VComponent;
import nl.esciencecenter.vlet.gui.view.VContainer;

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
