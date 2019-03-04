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

package nl.esciencecenter.vlet.vrs.vjs;

import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VCompositeNode;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;


public abstract class VQueue extends VCompositeNode
{
		
	
	public VQueue(VRSContext context, VRL vrl)
	{
		super(context, vrl);
	}

	public String getType()
	{
		return VJS.TYPE_VQUEUE; 
	}
	
	public VNode getNode(String name) throws VrsException
	{
		return getJob(name); 
	}
	
	public VNode[] getNodes() throws VrsException
	{
		return getJobs(); 
	}
	
	public boolean hasNode(String name) throws VrsException
	{
		return hasJob(name);
	}
	
	public boolean hasJob(String name) throws VrsException
	{
		VJob jobs[]=getJobs();
		
		if (jobs==null) 
			return false; 
		
		for (VJob job:jobs) 
		{
			if (job.getName().compareTo(name)==0) 
				return true; 
		}
		
		return false; 
	}
	
	public boolean isAccessable()
	{
		return true;
	}

	public boolean isDeletable() throws VrsException
	{
		return false;
	}	
	
	public Attribute[][] getNodeAttributes(String[] jobNames, String[] attrNames) throws VrsException
	{
		return getJobAttributes(jobNames,attrNames); 
	}

	public Attribute[][] getNodeAttributes(String[] attrNames) throws VrsException
	{
		return getJobAttributes(attrNames); 
	}
	
	/** Get job by Name */ 
	public VJob getJob(String name) throws VrsException
	{
		VJob jobs[]=getJobs();
		
		if (jobs==null) 
			return null;
		
		for (VJob job:jobs) 
		{
			if (job.getName().compareTo(name)==0) 
				return job; 
		}
		
		return null; 
	}
	
	public VJob addNode(VNode node, boolean isMove) throws VrsException
	{
		if ((node instanceof VJob)==false)  
			throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Cannot schedule non VJob types:"+node); 
			
		return schedule((VJob)node); 
	}

	public VJob addNode(VNode node, String newName, boolean isMove) throws VrsException
	{
		if ((node instanceof VJob)==false)  
			throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Cannot schedule non VJob types:"+node); 
		
		return schedule((VJob)node); 
	}

	public VJob[] addNodes(VNode[] nodes, boolean isMove) throws VrsException
	{
		if (nodes==null) 
			return null; 
		
		if ((nodes instanceof VJob[])==false) 
			throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Cannot schedule non VJob types:"+nodes); 
		
		VJob jobs[]=new VJob[nodes.length];
		
		int index=0;
		
		for (VNode node:nodes)	
			jobs[index++]=(VJob)addNode(node,false);
		
		return jobs; 
	}

	public VJob createNode(String type, String name, boolean force) throws VrsException
	{
		throw new nl.esciencecenter.vlet.exception.NotImplementedException("Cannot create empty job"); 
	}
	
	/** Delete Node = abort job */
	public boolean delNode(VNode node) throws VrsException
	{
		return false;
	}
	
	/** Delete Nodes = abort jobs */
	public boolean delNodes(VNode[] nodes) throws VrsException
	{
		return false;
	}

	public boolean delete(boolean recurse) throws VrsException
	{
		return false;
	}
	
	//=========================================================================
	// Abstract interface 
	//=========================================================================
	
	public abstract Attribute[][] getJobAttributes(String[] attrNames) throws VrsException; 
	
	public abstract Attribute[][] getJobAttributes(String[] jobNames, String[] attrNames) throws VrsException;
	
	public abstract VJob[] getJobs() throws VrsException; 

	public abstract VJob schedule(VJob job) throws VrsException; 
}
