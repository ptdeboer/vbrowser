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

package nl.esciencecenter.vlet.vrs.vdriver.webrs;

import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_HOSTNAME;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_ICON;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_MIMETYPE;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_PATH;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_PORT;
import static nl.esciencecenter.vbrowser.vrs.data.AttributeNames.ATTR_RESOURCE_TYPE;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_CHARSET;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_ISVLINK;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_LOCATION;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_NAME;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.ptk.web.ResponseInputStream;
import nl.esciencecenter.ptk.web.ResponseOutputStream;
import nl.esciencecenter.ptk.web.WebClient;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.io.VStreamAccessable;

/**
 *  Class represents a HTTP reference  
 */ 
public class WebNode extends VNode implements VStreamAccessable
{
    // =====
    // Class
    // =====

    static private String[] attributeNames=
    {
        ATTR_RESOURCE_TYPE,
        ATTR_NAME,
        ATTR_HOSTNAME,
        ATTR_PORT,
        ATTR_ICON,
        ATTR_PATH,
        ATTR_MIMETYPE,
        ATTR_CHARSET,
        ATTR_LOCATION,
        ATTR_ISVLINK
    };

    // ========
    // Instance
    // ========
    
    private String mimeType=null;

    private WebResourceSystem httprs;

    private boolean isHTTPS=false; 
    
    protected WebClient getWebClient()
    {
        return this.httprs.getWebClient(); 
    }
    
    private void init(VRL loc) throws VrsException 
    {
        this.isHTTPS=false; 
        
        if (VRSContext.getDefault().resolveScheme(loc.getScheme()).compareToIgnoreCase(VRS.HTTPS_SCHEME)==0)
        {
            isHTTPS=true; 
        }
    }
    
    public WebNode(WebResourceSystem httprs,VRL loc) throws VrsException
    {
        super(httprs.getVRSContext(),loc);
        this.httprs=httprs; 
        init(loc); 
    }

    @Override
    public String getResourceType()
    {
        return VRS.HTTP_SCHEME; 
    }

    public ResponseInputStream createInputStream() throws IOException
    {
        
        try
        {
            return getWebClient().doGetInputStream(getVRL().toURI());
        }
        catch (URISyntaxException e)
        {
           throw new IOException(e.getMessage(),e);
        }

    }
    
    /**
     * Get mimetype as reported by remote Server. 
     * Open Connection and tries get the ContentType header fromt he request. 
     */
    @Override
    public String getMimeType() throws VrsException
    {
        if (mimeType!=null) 
            return mimeType;
        
        String str; 
        try
        {
            ResponseInputStream inps=createInputStream();
            
            str=inps.getMimeType(); 
            try
            {
                inps.close();
            }
            catch (IOException e)
            {
                
            }
        }
        catch (IOException e)
        {
            throw new NestedIOException(e);
        }

        if (str==null) 
        {
            return "text/html"; 
        }
        
        String strs[]=str.split(";");

        if (strs.length<1)
        {
            mimeType=str;
        }
        else
        {
            mimeType=strs[0]; 
        }

        if (mimeType==null) 
            return "text/html"; 

        return mimeType; 
    }


    /** 
     * Get the names of the attributes this resource has 
     */ 
    public String[] getAttributeNames()
    {
        return attributeNames;
    }

    // === Misc === 

    // method needs by streamread/write interface 
    public int getOptimalWriteBufferSize()
    {
        return VRS.DEFAULT_STREAM_WRITE_CHUNK_SIZE;
    }

    public int getOptimalReadBufferSize()
    {
        return VRS.DEFAULT_STREAM_READ_CHUNK_SIZE;
    }

    public String getIconURL() 
    {
        // doesn't have to be connected. 
        VRL vrl;
        
        try
        {
            vrl = this.getVRL().resolvePath("favicon.ico");
            if (exists(vrl.getPath()))
            {
                return vrl.toString();
            }
        }
        catch (VRLSyntaxException e)
        {
            e.printStackTrace();
        }


        vrl=getVRL().replacePath("favicon.ico");

        if (exists(vrl.getPath()))
        {
            return vrl.toString();
        }

        return null;   // no icon
    }

    public ResponseOutputStream createOutputStream() throws IOException
    {
        VRL vrl=getVRL();
        String queryStr=vrl.getPath();

        if (vrl.getFragment()!=null)
        {
            queryStr+=queryStr+"#"+vrl.getFragment(); 
        }

        if (vrl.getQuery()!=null)
        {
            queryStr+=queryStr+"?"+vrl.getQuery(); 
        }
        
        StringHolder statusH=new StringHolder();  
        return getWebClient().doPutOutputStream(queryStr,statusH);
    }

    public WebResourceSystem getHTTPRS()
    {
        return this.httprs; 
    }
    
    public boolean exists(String query)
    {
        try
        {
            ResponseInputStream inps=getWebClient().doGetInputStream(query); 
            inps.autoClose(); 
            return true;
        }
        catch (IOException e)
        {
            return false; 
        }
    }

}
