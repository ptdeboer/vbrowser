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

package nl.esciencecenter.vlet.gui.viewers.grid.replicaviewer;

import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.viewers.grid.replicaviewer.ReplicaDataModel.ReplicaStatus;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;

public class ReplicaInfo
{
    private final VRL replicaVRL;
    
    private boolean error=false;
    
    private Exception exception=null;  
    
    private long length=-1; 
    
    // Might be null 
    private VRL transportURI=null;  
    
    private boolean exists=false; 
    
    public ReplicaInfo(VRL vrl)
    {
        this.replicaVRL=vrl; 
    }

    public VRL getVRL()
    {
        return replicaVRL; 
    }
    
    public String toString()
    {
        String str="";
        if (replicaVRL==null)
            if (exception==null)
                str="[NULL Replica VRL]";
            else
                str="[Exception:"+exception+"]"; 
        else
            str="<Replica>{"+replicaVRL.toString()+","+length+"}"; 
        return str;  
    }

    public String getHostname()
    {
       if (replicaVRL==null)
           return null; 
       
       return replicaVRL.getHostname(); 
    }
    
    public long getLength()
    {
        return this.length;
    }

    public boolean hasError()
    {
        return this.error; 
    }
    
    public boolean exists()
    {
        return this.exists; 
    }
    
    public void setExists(boolean val)
    {
        this.exists=val; 
    }
    
    public Exception getException()
    {
        return this.exception; 
    }
    
    public void setException(Exception e)
    {
        this.exception=e;  
    }

    public void setError(boolean val)
    {
        this.error=val; 
        
    }

    public void setLength(long val)
    {
        this.length=val;
        
    }

    public void setTransportURI(VRL vrl)
    {
        this.transportURI=vrl;  
    }

    public void setAttributes(AttributeSet attrs)
    {
        // updat rep infos: 
        if (attrs.containsKey(VAttributeConstants.ATTR_FILE_SIZE))
            setLength(attrs.getLongValue(VAttributeConstants.ATTR_FILE_SIZE));
        
        if (attrs.containsKey(VAttributeConstants.ATTR_EXISTS))
            setExists(attrs.getBooleanValue(VAttributeConstants.ATTR_EXISTS,false));
     
        if (attrs.containsKey(VAttributeConstants.ATTR_STATUS))
        {
            String val=attrs.getStringValue(VAttributeConstants.ATTR_STATUS);
            if (val!=null)
                if (val.equals(ReplicaStatus.ERROR.toString()))
                    setError(true);
                else
                    setError(false); 
        }
     
        try
        {
            if (attrs.containsKey(VAttributeConstants.ATTR_TRANSPORT_URI))
                setTransportURI(attrs.getVRLValue(VAttributeConstants.ATTR_TRANSPORT_URI));
        }
        catch (VRLSyntaxException e)
        {
            e.printStackTrace();
        }
    }

}
