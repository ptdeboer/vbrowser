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

package nl.esciencecenter.vlet.gui.viewers.grid.jobmonitor;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.ResourceNotFoundException;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSClient;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vjs.VJob;
import nl.esciencecenter.vlet.vrs.vrl.VRLUtil;

/** 
 * Job Status utility. 
 * Returns Implemenation  
 */
public class JobUtil
{
    public static VRL createJobVrl(String jobid) throws VRLSyntaxException
    {
    	VRL vrl=new VRL(jobid);
        
        // replace https -> LB scheme. WMS implementation should do the rest. 
    	// note that JobVRL mights be updated when the VNode (VJob) object is returned!
        if (vrl.hasScheme("https"))
            vrl=VRLUtil.replaceScheme(vrl,VRS.LB_SCHEME);
        
		return vrl; 
	}
    
	public static String guessJobIdFromJobVrl(VRL vrl)
	{	
		vrl=VRLUtil.replaceScheme(vrl,VRS.HTTPS_SCHEME);
		return vrl.toString(); 
	}

    private static Map<String,JobUtil> jobUtilInstances=new Hashtable<String,JobUtil>(); 
    
    public static JobUtil getJobUtil(VRSContext context)
    {
    	int id = context.getID(); 
    	String keystr="jobutil-"+id; 
    	
    	synchronized(jobUtilInstances)
    	{
    		JobUtil jobUtil=jobUtilInstances.get(keystr);
    		if (jobUtil==null)
    		{
    			jobUtil=new JobUtil(context); 
    		}
    		
    		jobUtilInstances.put(keystr,jobUtil);
    		
    		return jobUtil; 
    	}
    	
    }
    
    // ========================================================================
    //
    // ========================================================================
    
    private VRSClient vrsClient;
    // mini cache: 
    private Map<String,VJob> _cache=new HashMap<String,VJob>(); 
    private boolean useCache=true; 
    
    protected JobUtil(VRSContext context)
    {
        this.vrsClient=new VRSClient(context); 
    }

    public String getStatus(String jobid, boolean fullUpdate) throws VrsException
    {
        VJob job=getJob(jobid); 
        if (job==null)
        	throw new ResourceNotFoundException("Couldn' get job:"+jobid); 
        
        // use new sync method but only for unfinished jobs: 
        if (fullUpdate)
        	if (job.hasTerminated()==false)
        		job.sync(); 
        String stat=job.getResourceStatus();
       
        return stat; 
    }
 
    public String[] getJobAttributeNames(String jobId)  throws VrsException
    {
        VJob job=getJob(jobId); 
        return job.getAttributeNames(); 
    }
     
    protected VJob getJob(String jobid) throws VrsException
    {  
        if (useCache)
        {
            synchronized(this._cache)
            {
                if (_cache.containsKey(jobid))
                    return _cache.get(jobid); 
            }
        }
        
        // guess first: don't forget to update the actual VRL as returned by teh VNode!!!
        VRL vrl=createJobVrl(jobid); 
        VNode jobNode = vrsClient.openLocation(vrl); 
    
        if ((jobNode instanceof VJob)==false)
        {
            throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("URI is not a job URI:"+jobid
                +"\n. Resource Type="+jobNode.getResourceType() );   
        }
    
        VJob job=(VJob)jobNode;
        VRL jobVrl=job.getVRL(); 
        
        if (useCache)
        {
            synchronized(this._cache)
            {
                _cache.put(jobid,job); 
                _cache.put(jobVrl.toString(),job); // double cache using resolved (!)  VRL 
            }
        }
        
        return job; 
    }

	public String[] getJobAttrNames(String id) throws VrsException
    {
        return getJob(id).getJobAttributeNames(); 
    }

    public Attribute[] getAttributes(String id, String[] attrNames) throws VrsException
    {
        return getJob(id).getAttributes(attrNames);
    }
    
    public void clearCache()
    {
        this._cache.clear(); 
    }

    // return actual Job VRL. 
	public VRL getJobVRL(String id) throws VrsException 
	{
		VJob job = getJob(id);
		if (job==null)
			return null;
		return job.getVRL(); 
	}

	public boolean isJobVRL(VRL vrl) 
	{
		return vrl.hasScheme(VRS.LB_SCHEME);
	}

	
}
	