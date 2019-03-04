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

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VCompositeNode;
import nl.esciencecenter.vlet.vrs.VEditable;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;


/** 
 * Abstract VJob interface.
 * <p>
 * <b>Job Status:</b><br>
 * 
 * A Status of a VJob is simplified and is limited to only one of tree:<br>
 * <ul> 
 * <li>Submitted: Pre execution, job is being submitted to the queue or waiting in the queue.  
 * <li>Running: Job is executing. The job is actually running on the Job (sub) System. 
 * <li>Terminated: Post execution. Job has succeeded, has an error or is cancelled.  
 * </ul>
 * This simplification is done because not all JobManager systems support all kind 
 * of statuses.  
 * A Job is terminated if it already executed or has failed. It will never will reach the status of Running. 
 * This could also mean that a job was Canceled or Aborted or the execution has failed. 
 * If it is not Running or not Terminated it is assumed the job has been Submitted
 * and is waiting for execution or is in the process of submission. 
 *  
 * @author Piter T. de Boer 
 *
 */
public abstract class VJob extends VCompositeNode implements VEditable
{
    // job only attribute names: 
    static protected StringList _jobAttrNames; 
    
    static
    {
        _jobAttrNames=new StringList(new String[]{
                VAttributeConstants.ATTR_STATUS,
                VAttributeConstants.ATTR_JOB_IS_RUNNING,
                VAttributeConstants.ATTR_JOB_HAS_TERMINATED,
                VAttributeConstants.ATTR_JOB_HAS_ERROR,
                VAttributeConstants.ATTR_ERROR_TEXT,
                VAttributeConstants.ATTR_JOB_STATUS_INFORMATION
        }); 
        
    }
    
    //=========================================================================
    // VNode -> VJob
    //=========================================================================
	
	protected String id; 
	
	
	public VJob(VRSContext context, VRL vrl)
	{
		super(context, vrl);
	}

//	VTerminatable ==> public void terminate() throws VlException
//	{
//		throw new nl.uva.vlet.exception.NotImplementedException("Not implemented: terminate:"+this); 
//	}
	
//	VSuspendable ==> public void resume() throws VlException
//	{
//		throw new nl.uva.vlet.exception.NotImplementedException("Not implemented: resume:"+this); 
//	}
	
	public boolean sync() throws VrsException
	{
		return super.sync();
	}
	
	// === VEdtiable interface ===
	
	public boolean setAttributes(Attribute[] attrs) throws VrsException
	{
		boolean result=true; 
		for (Attribute attr:attrs)
		{
			boolean val=setAttribute(attr); 
			result=result&val; 
		}
		
		return result;
	}
	
	/** Job ID */ 
	public String getJobId()
	{
		return id; 
	}
	
	/** protected setJobId */ 
	protected String setJobId(String idstr)
	{
		return id=idstr; 
	}

	/** Optional Job Group ID. Returns null is this Job doesn't have an ID */
	public String getGroupId()
	{
		return null;
	}
	
    @Override
    public String[] getResourceTypes()
    {
        // possible resource types:  JDL child, Job Output folder.
        return null;
    }
	
    public Attribute getAttribute(String name) throws VrsException
    {
        // Generic VJob Attributes: 
        if (StringUtil.equals(name, VAttributeConstants.ATTR_STATUS))
        {
            return new Attribute(name, this.getResourceStatus());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_JOB_IS_RUNNING))
        {
            return new Attribute(name, this.isRunning());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_JOB_HAS_TERMINATED))
        {
            return new Attribute(name, this.hasTerminated());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_JOB_HAS_ERROR))
        {
            return new Attribute(name,this.hasError());
        }
        else if (StringUtil.equals(name, VAttributeConstants.ATTR_ERROR_TEXT))
        {
            return new Attribute(name,this.getErrorText());
        }
        
        
        return super.getAttribute(name); 
    }
    
    public String[] getAttributeNames()
    {
        // merge attributes
        String[] attrs = super.getAttributeNames(); 
        StringList list=new StringList(attrs); 
        list.add(getJobAttributeNames()); 
        return list.toArray(); 
    }
    
    public String[] getResourceAttributeNames()
    {
        return getJobAttributeNames();
    }
    
    /**
     * Return Job Specific Attribute names. 
     * Override for implementation specific job attribute names.
     *  
     * @return String array of attribute names
     */
    public String[] getJobAttributeNames()
    {
        return _jobAttrNames.toArray(); 
    }
    
    // === VJob Outputs === 
    
    public boolean hasOutputs() throws VrsException
    {
    	return false; 
    }
    
    /** 
     * Return job outputs after job has successfully finished. 
     * Only if supported by the implementation.
     */
    public VRL[] getOuputVRLs() throws VrsException
    {
    	return null; 
    }
    
	// === VJob interface === 
	
//	/** Get list of Status string this Job supports */ 
//	public abstract String getPossibleStatuses() throws VlException;
	
	/**
	 * Returns status String. This status string is implementation depended. 
	 * Use the isRunnig(), isTerminated() and hasError() methods for explicit status checking. 
	 * Current status supported at the VJOb level is {Running,Terminated,Error} 
	 */
	public abstract String getResourceStatus() throws VrsException; 
	
	/** Whether job is currently executing. */  
	public abstract boolean isRunning() throws VrsException; 
	
	/** 
	 * Terminated means either successful execution or terminated with error
	 * or it was cancelled. 
	 * If hasTerminated==true it has finished or won't be running.  
	 */ 
	public abstract boolean hasTerminated() throws VrsException;  

	/**
	 * Returns whether the job has terminated with an error. 
	 * If the job is hasn't ran or is still running, this method might 
	 * return null. 
	 */  
	public abstract boolean hasError() throws VrsException;  
	
	/**
	 * Returns error text, return null if no error has been encountered 
	 * or if job is still running.
	 */
	public abstract String getErrorText() throws VrsException;  

	
}
