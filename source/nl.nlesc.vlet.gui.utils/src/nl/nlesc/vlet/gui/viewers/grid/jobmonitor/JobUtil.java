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

package nl.nlesc.vlet.gui.viewers.grid.jobmonitor;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.exception.ResourceNotFoundException;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSClient;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.data.VAttribute;
import nl.nlesc.vlet.vrs.vjs.VJob;

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
            vrl=new VRL(vrl.replaceScheme(VRS.LB_SCHEME));
        
		return vrl; 
	}
    
	public static String guessJobIdFromJobVrl(VRL vrl)
	{	
		vrl=new VRL(vrl.replaceScheme(VRS.HTTPS_SCHEME));
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
            throw new nl.nlesc.vlet.exception.ResourceTypeMismatchException("URI is not a job URI:"+jobid
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

    public VAttribute[] getAttributes(String id, String[] attrNames) throws VrsException
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
	