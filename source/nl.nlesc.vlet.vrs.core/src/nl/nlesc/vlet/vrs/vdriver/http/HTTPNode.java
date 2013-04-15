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

package nl.nlesc.vlet.vrs.vdriver.http;

import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_CHARSET;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_ICON;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_ISVLINK;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_LOCATION;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_MIMETYPE;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_NAME;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_PATH;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_PORT;
import static nl.nlesc.vlet.data.VAttributeConstants.ATTR_RESOURCE_TYPE;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

import nl.esciencecenter.ptk.net.VRI;
import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.io.VStreamAccessable;
//import sun.security.validator.ValidatorException;
/** Class represents a HTTP reference  */ 

public class HTTPNode extends VNode implements VStreamAccessable
{
    // ===
    // Class
    // ===

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

    public static boolean exists(URL url)
    {
        //debugPrintf(HTTPNode.class,"Exists():%s\n",url);

        try
        {
            if (url.openConnection()!=null)
            {
                //debugPrintf(HTTPNode.class,"Exists()==true for:%s\n",url);
                return true;
            }
        }
        catch (Exception e)
        {

        }
        //debugPrintf(HTTPNode.class,"Exists()==false for:%s\n",url);

        return false; 
    }

    // ===
    // Instance
    // ===

    URL url=null;

    //private HttpURLConnection connection=null;
    private HTTPConnection connection=null; 

    private String mimeType=null;

    private HTTPRS httprs;

    private void init(VRL loc) throws VlException 
    {
        //Debug("new URL="+loc);

        try
        {
            this.url=loc.toURL();
        }
        catch (MalformedURLException e)
        {
            throw new VRLSyntaxException("invalide URL:"+loc,e);
        }

        boolean isHTTPS=false; 
        
        if (VRSContext.getDefault().resolveScheme(loc.getScheme()).compareToIgnoreCase(VRS.HTTPS_SCHEME)==0)
            isHTTPS=true; 

        this.connection=new HTTPConnection(this,isHTTPS);
    }
    
    public HTTPNode(HTTPRS httprs,VRL loc) throws VlException
    {
        super(httprs.getVRSContext(),loc);
        this.httprs=httprs; 
        init(loc); 
    }

    /**
     * Get mimetype as reported by remote Server. 
     * returns url.openConnection().getContentEncoding();
     * @throws VlIOException 
     * 
     */
    @Override
    public String getCharSet() throws VlException
    {
        String str;
        try
        {
            str=connection.getContentType();
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }

        if (str==null)
            return null; 

        String strs[]=str.split(";");

        if (strs.length>1)
        {
            for(int i=1;i<strs.length;i++)
            {
                //Debug("getCharsSet, checking:"+strs[i]);

                String pars[]=strs[i].split("=");

                if (pars[0].compareToIgnoreCase("charset")==0) 
                    return pars[1]; 
                else if (pars[0].compareToIgnoreCase(" charset")==0) 
                    return pars[1]; 
            }

        }

        //Debug("Warning: charset parameter not found, returning default for:"+this);

        return super.getCharSet();

    }

    public boolean isConnected()
    {
        return (this.connection!=null); 
    }

    @Override
    public String getResourceType()
    {
        return VRS.HTTP_SCHEME; 
    }

    public InputStream getInputStream() throws IOException
    {
        return this.connection.getInputStream();
    }
    
    /**
     * Get mimetype as reported by remote Server. 
     * returns url.openConnection().getContentEncoding();
     * 
     */
    @Override
    public String getMimeType() throws VlException
    {
        if (mimeType!=null) 
            return mimeType;
        String str; 
        try
        {
            str=connection.getContentType();
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }

        if (str==null) 
            return "text/html"; 
        
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


    /** Get the names of the attributes this resource has */ 
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
        try
        {
            VRL vrl;
            vrl = this.getVRL().resolve("favicon.ico");
            URL url=vrl.toURL();

            if (exists(url))
                return vrl.toString(); 
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        try
        {

            VRI vri=this.getVRL().replacePath("favicon.ico");
            URL url=vri.toURL();

            if (exists(url))
                return vri.toString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return null;   // no icon
    }

    public OutputStream getOutputStream() throws IOException
    {
        return this.connection.getOutputStream();
    }

    public HTTPRS getHTTPRS()
    {
        return this.httprs; 
    }

}
