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


import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.gui.proxyvrs.ProxyResourceEventListener;
import nl.esciencecenter.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.esciencecenter.vlet.vrs.events.EventType;
import nl.esciencecenter.vlet.vrs.events.ResourceEvent;


public class JobStatusUpdater implements ProxyResourceEventListener
{
    private static ClassLogger logger; 
    {
        logger=ClassLogger.getLogger(JobStatusUpdater.class);
        //logger.setLevelToDebug(); 
    }
    // ========================================================================
    
    private JobStatusDataModel jobStatusModel =null; 
    private ActionTask updateTask=null;
    private ActionTask updateAttrsTask=null; 

    private boolean stopUpdateTasks=false;
    private JobUtil jobUtil=null;
	private JobMonitorController controller=null; 
    
    public JobStatusUpdater(JobMonitorController jobMonitorController, JobStatusDataModel model)
    {
    	this.controller=jobMonitorController;
        this.jobStatusModel=model; 
        // register as event listener: 
        ProxyVRSClient.getInstance().addResourceEventListener(this); 
    }

    /** Start update in background */ 
    public void doUpdate(final boolean fullUpdate)
    {
        logger.infoPrintf(">>> JobStatusUpdater.doUpdate(): starting! <<<\n"); 
        
        this.stopUpdateTasks=false; 
        
        this.updateTask=new ActionTask(null,"JobStatusUpdater.updateTask()")
        {
            @Override
            protected void doTask() throws VrsException
            {
                bgUpdate(fullUpdate); 
            }

            @Override
            public void stopTask()
            {
                stopUpdateTasks=true; 
            }
        };
        
        this.updateTask.startTask(); 
        
    }
  
    /** Start update in background */ 
    public void doUpdateAttributes(final String[] attributeNames)
    {
        logger.infoPrintf(">>> doUpdateAttributes: starting! <<<\n"); 
        this.stopUpdateTasks=false; 
        
        this.updateAttrsTask=new ActionTask(null,"JobStatusUpdater.doUpdateAttributes()")
        {
            @Override
            protected void doTask() throws VrsException
            {
                bgUpdate(false,attributeNames); 
            }

            @Override
            public void stopTask()
            {
                stopUpdateTasks=true; 
            }
        };
        
        this.updateAttrsTask.startTask(); 
    }
    
    private void bgUpdate(boolean fullUpdate)
    {
        String attrNames[]=null; 
        if (fullUpdate)
            attrNames=this.jobStatusModel.getHeaders();
        bgUpdate(true,attrNames);
    }
    
    private void bgUpdate(boolean fullUpdate,String attrNames[])
    {   
        StringList ids=jobStatusModel.getJobIds();
        
        for (String id:ids)
        {
            if (stopUpdateTasks)
            {
                logger.warnPrintf(" *** Interrrupted: Must Stop *** \n");
                break;
            }
            
            logger.debugPrintf("Updating status of: %s\n",id); 
            
            // Pre Fetch: 
            this.jobStatusModel.setQueryBusy(id,true);
            if (jobStatusModel.isStatusUnknown(id))
                    this.jobStatusModel.setStatus(id,JobStatusDataModel.STATUS_UPDATING);
            this.jobStatusModel.setQueryBusy(id,false); 
            
            try
            {
                String newStatus=getJobUtil().getStatus(id,fullUpdate);
                VRL vrl=getJobUtil().getJobVRL(id); 
                
                logger.infoPrintf(" - new status of '%s'=%s\n",id,newStatus);
                this.jobStatusModel.setStatus(id,newStatus);
                
                this.jobStatusModel.setValue(id,JobStatusDataModel.ATTR_JOBVRL,vrl.toString());
                
                // Auto Update Attribute Names (Update All Headers)  
                String newAttrNames[]=getJobUtil().getJobAttrNames(id); 
                // update headers
                jobStatusModel.addExtraHeaders(newAttrNames); 
                
                // Update all VAttribute currently shown in table.
                if (attrNames!=null)
                {
                    Attribute attrs[]=getJobUtil().getAttributes(id,attrNames);
                    jobStatusModel.updateJobAttributes(id,attrs);
                }
               
            }
            catch (Exception e)
            {
                logger.logException(ClassLogger.ERROR,e,"Couldn't update status of job:%s\n",id); 
                this.jobStatusModel.setStatus(id,JobStatusDataModel.STATUS_ERROR);
                this.jobStatusModel.setErrorText(id,e.getMessage());
            }
            
            this.jobStatusModel.setQueryBusy(id,false);
            
        }
    }

    public JobUtil getJobUtil()
    {
        // implementation independed Job Status Util
        if (jobUtil==null)
        {
        	// get context JoUtil
            this.jobUtil=JobUtil.getJobUtil(UIGlobal.getVRSContext());  
        }
        
        return jobUtil; 
        
    }

	public VRL getJobVrl(String jobid) throws VrsException 
	{
		return getJobUtil().getJobVRL(jobid);
	}

	@Override
	public void notifyProxyEvent(ResourceEvent event) 
	{
		logger.debugPrintf(">>> Got Event:%s\n",event); 
		EventType type = event.getType(); 
		VRL source = event.getSource();
		
		//filter out jobs
		if (getJobUtil().isJobVRL(source)) //source.hasScheme(VRS.LB_SCHEME))
		{
			// search fro job using the VRL
			String jobid=jobStatusModel.getJobIdByVrl(source); 
			
			if (jobid!=null)
			{
				if (type==EventType.SET_ATTRIBUTES)
				{
					Attribute[] attrs = event.getAttributes(); 
					AttributeSet attrSet=new AttributeSet(attrs);
					// update table
				}
			}
		}
	}
	

	public void stopAll() 
	{
		stopUpdateTasks=true; 
	}
    
}
