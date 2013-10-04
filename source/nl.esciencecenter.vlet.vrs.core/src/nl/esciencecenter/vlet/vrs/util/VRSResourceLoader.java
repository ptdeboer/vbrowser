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

package nl.esciencecenter.vlet.vrs.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import nl.esciencecenter.ptk.io.IOUtil;
import nl.esciencecenter.ptk.util.ResourceLoader;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.exception.NotImplementedException;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSClient;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.io.VRandomReadable;
import nl.esciencecenter.vlet.vrs.io.VResizable;
import nl.esciencecenter.vlet.vrs.io.VStreamWritable;
import nl.esciencecenter.vlet.vrs.vfs.VFile;

/** 
 * ResourceLoader which uses VRS methods for reading/writing resources. 
 */
public class VRSResourceLoader extends ResourceLoader
{
    private VRSContext vrsContext;

    private VRSClient vrsClient;

    public VRSResourceLoader(VRSContext context)
    {
        this.vrsContext=context; 
        this.vrsClient=new VRSClient(context);
    }
    
    public String readText(URI uri) throws IOException
    {
        try
        {
            InputStream inps;
            inps = vrsClient.openInputStream(new VRL(uri));
            String text=readText(inps,null);  
            try { inps.close(); } catch (Exception e) { ; } 
            return text; 
        }
        catch (VrsException e1)
        {
            throw new IOException(e1);
        }  
    }
    
    /** 
     * Returns resource as String. 
     */ 
    public  String getText(VRL vrl) throws IOException, VrsException 
    {
         InputStream inps=vrsClient.openInputStream(vrl);  
         String text=readText(inps,null);  
         try { inps.close(); } catch (Exception e) { ; } 
         return text; 
    }
   
    public String getText(VRL vrl, String textEncoding) throws IOException, VrsException 
    {
        InputStream inps=vrsClient.openInputStream(vrl);  
        String text=readText(inps,textEncoding);  
        try { inps.close(); } catch (Exception e) { ; } 
        return text;
    }

    public void syncReadBytes(VFile file, long fileOffset, byte[] buffer, int bufferOffset, int numBytes) throws IOException, VrsException 
    {
        if ((file instanceof VRandomReadable)==false)
        {
            throw new IOException("Can't read from resource. Must be RandomReadable:"+file); 
        }
        
        VRandomReadable readable=(VRandomReadable)file;
        IOUtil.syncReadBytes(readable, fileOffset, buffer, bufferOffset, numBytes); 
    }
    
    public void writeTextTo(VRL vrl, String txt)  throws IOException, VrsException
    {
        writeTextTo(vrl,txt,this.charEncoding); 
    }
    /**
     * Writes String contents to remote location using optional encoding
     * Tries to truncate resource or delete original resource
     */ 
    public void writeTextTo(VRL vrl, String txt,String encoding) throws IOException, VrsException 
    {
        VNode node = this.vrsContext.openLocation(vrl); 
        OutputStream outps=null; 
        
        if (encoding==null)
            encoding=this.getCharEncoding();
        
        // delete existing file: 
        if (node instanceof VFile)
        {
            VFile file=(VFile)node; 
            if ((file.exists() && file.getLength()>0))
            {
                file.delete();
            }
        }
        else if (node instanceof VResizable)
        {
            ((VResizable)node).setLengthToZero(); 
        }
        
        if (node instanceof VStreamWritable)
        {
            outps=((VStreamWritable) node).createOutputStream();
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
            throw new NestedIOException("UnsupportedEncoding!", e);
        }
        catch (IOException e)
        {
            throw new NestedIOException(e);
        }
    }
    
    public boolean setSizeToZero(VFile file) throws  IOException, VrsException
    {
        if (file.exists()==false)
            return true; 
        
        if (file.getLength()<=0)
            return true; 
        
        // optimization 
        if (file instanceof VResizable)
        {
            ((VResizable)file).setLengthToZero(); 
            return true; 
        }
        
        file.delete(); 
        return file.sync(); 
    }

    public boolean writeTextTo(VFile file,String text,boolean truncate) throws VrsException
    {
        try 
        {
            if (truncate)
                setSizeToZero(file);
            
            OutputStream outps=file.createOutputStream(); 
            outps.write(text.getBytes(this.charEncoding));
            outps.flush();
 
            
            try 
            {
                outps.close();  
            }
            catch (IOException e)
            {
                ;
            }
            
            return file.sync();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e);
        }
    }
    
    public boolean writeContentsTo(VFile file, byte[] bytes,boolean truncate) throws VrsException
    {
        try 
        {
            if (truncate)
                setSizeToZero(file);
            
            OutputStream outps=file.createOutputStream(); 
            outps.write(bytes); 
            
            try 
            {
                outps.close();  
            }
            catch (IOException e)
            {
                ;
            }
            
            return file.sync();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e);
        }
        
    }

    public String readText(VFile file) throws IOException
    {
        InputStream inps = file.createInputStream(); 
        String text=this.readText(inps, this.charEncoding); 
        try
        {
            inps.close(); 
        }
        catch (IOException e)
        {
            ; 
        }
        return text; 
    }

    public byte[] readContents(VFile file) throws IOException
    {
        InputStream inps = file.createInputStream(); 
        byte[] bytes=this.readBytes(inps);  
        try
        {
            inps.close(); 
        }
        catch (IOException e)
        {
            ; 
        }
        return bytes; 
        
    }
   
}
