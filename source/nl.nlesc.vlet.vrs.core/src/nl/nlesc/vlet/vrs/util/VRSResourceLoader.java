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

package nl.nlesc.vlet.vrs.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import nl.esciencecenter.ptk.util.ResourceLoader;
import nl.nlesc.vlet.exception.NotImplementedException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.exception.VlIOException;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRSClient;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.io.VStreamWritable;

public class VRSResourceLoader extends ResourceLoader
{
    private VRSContext vrsContext;
    private VRSClient vrsClient;

    public VRSResourceLoader(VRSContext context)
    {
        this.vrsContext=context; 
        this.vrsClient=new VRSClient(context);
    }
    
    public String getText(URI uri) throws IOException
    {
        try
        {
            InputStream inps;
            inps = vrsClient.openInputStream(new VRL(uri));
            String text=getText(inps,null);  
            try { inps.close(); } catch (Exception e) { ; } 
            return text; 
        }
        catch (VlException e1)
        {
            throw new IOException(e1);
        }  
    }
    
    /** Returns resource as String */ 
    public  String getText(VRL vrl) throws Exception
    {
         InputStream inps=vrsClient.openInputStream(vrl);  
         String text=getText(inps,null);  
         try { inps.close(); } catch (Exception e) { ; } 
         return text; 
    }
   
    public String getText(VRL vrl, String textEncoding) throws Exception
    {
        InputStream inps=vrsClient.openInputStream(vrl);  
        String text=getText(inps,textEncoding);  
        try { inps.close(); } catch (Exception e) { ; } 
        return text;
    }

    /**
     * Writes String contents to remote location using optional encoding
     * Tries to truncate resource or delete original resource
     */ 
    public void setContents(VRL vrl, String txt,String encoding) throws Exception
    {
        VNode node = this.vrsContext.openLocation(vrl); 
        OutputStream outps=null; 
        
        if (encoding==null)
            encoding=this.getCharEncoding(); 
        
        if (node instanceof VStreamWritable)
        {
            outps=((VStreamWritable) node).getOutputStream();
        }
        else
        {
            throw new NotImplementedException("Cannot get OutputStream from location:" + vrl);
        }
        
        OutputStreamWriter writer;
        
        try
        {
            writer = new OutputStreamWriter(outps, encoding);
            writer.write(txt);
            writer.close();
        }
        catch (UnsupportedEncodingException e)
        {
            throw new VlIOException("UnsupportedEncoding!", e);
        }
        catch (IOException e)
        {
            throw new VlIOException(e);
        }
    }
 
    
}
