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

package nl.esciencecenter.vbrowser.vrs.util;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.activation.MimetypesFileTypeMap;

import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

/**
 * Simple MimteType util class. 
 * 
 * @author P.T. de Boer
 */
public class MimeTypes
{
	public static final String MIME_TEXT_PLAIN    = "text/plain";
	public static final String MIME_TEXT_HTML     = "text/html";
	public static final String MIME_OCTET_STREAM  =	"application/octet-stream";
	public static final String MIME_BINARY        =	MIME_OCTET_STREAM; 

	/** Class Instance */ 
	private static MimeTypes instance;
	
	private static ClassLogger logger; 
	
	static
	{
	    logger=ClassLogger.getLogger(MimeTypes.class);
	}
	
	public static MimeTypes getDefault()
	{
	    if (instance==null)
	        instance=new MimeTypes();
	    
	    return instance; 
	}
	
	// ========================================================================
	// Instance 
	// ========================================================================

	/** the mime types file type map */ 
	private MimetypesFileTypeMap typemap=null;
	
    //private URL customMimetypesUrl;

    public MimeTypes()
    {
//        try
//        {
//           //this.customMimetypesUrl= new URL("file",null,0,Global.getGlobalUserHome()+"/.mimetypes/mime.types");
//        }
//        catch (MalformedURLException e)
//        {
//            logger.logException(ClassLogger.INFO,e,"Could not create user mimetype URL\n");
//        }

        init();
    }
    
//    public MimeTypes(URL customMimeTypes)
//    {
//        this.customMimetypesUrl=customMimeTypes; 
//        init();
//    }
    
	private void init()
	{
		try
		{  
			// use mime.types from class path:  

			String confFile="etc/mime.types";
			URL result = getClass().getClassLoader().getResource(confFile);

			if (result==null)
			{
				// Service/Applet configuration: 
				// get ANY mime.types file on the classpath 
				confFile="mime.types";
				result = getClass().getClassLoader().getResource(confFile);
			}
			
			if (result!=null)
			{
                InputStream inps = result.openStream();
                typemap=new MimetypesFileTypeMap(inps);
			}
			else
			{
			    logger.warnPrintf("Couldn't locate ANY mime.types file on classpath \n");
			    typemap=new MimetypesFileTypeMap();    
			}
		}
		catch (IOException e)
		{
			logger.logException(ClassLogger.WARN,e,"Couldn't initialize default mimetypes\n",e);
			// empty one ! 
			this.typemap=new MimetypesFileTypeMap(); 
		}
		
		// load custom mime types:
		
//		URL usermimes=getUserMimeTypeURL(); 
//		if (usermimes!=null)
//        {
//    		try
//    		{
//    		    addMimeTypes(usermimes);
//    		}
//    		catch (IOException e)
//    		{
//    		    logger.infoPrintf("Ignoring missing: user mimetypes:%s\n",usermimes);
//    		}
//        }
	}
	
//	public void addMimeTypes(URL url) throws IOException 
//	{
//	    try
//	    {
//	        String txt=ResourceLoader.getDefault().getText(url);
//	        addMimeTypes(txt); 
//        }
//	    catch (IOException e) 
//	    {
//	        throw new IOException("Failed to add mime type from url"+url+"\n"+e.getMessage(),e);   
//		}
//	}

    /** Add mime type definitions */ 
    public void addMimeTypes(String mimeTypes)
    {
        String lines[]=mimeTypes.split("\n");
        if (lines!=null)
            for (String line:lines)
            {
                logger.debugPrintf("Adding user mime.type:"+line);
                typemap.addMimeTypes(line);
            }
    }
    
//	public URL getUserMimeTypeURL()
//    {
//	    return this.customMimetypesUrl;  
//	}

    /** Returns mimetype string by checking the extension or name of the file */ 
	public String getMimeType(String path)
	{
		// garbage in, garbage out 
		if (path==null) 
			return null;
		
		return typemap.getContentType(path);
	}

	/**
	 * Returns the MimeType by checking against known the 'Magic' 
	 * attribute of a file. Note that this method provides 
	 * a better way to determinte the actual file type, 
	 * but needs to read (some) bytes from the file. 
	 * 
	 * @param firstBytes The first bytes of a file
	 * @return
	 * @throws VlException 
	 */

	public String getMagicMimeType(byte firstBytes[]) throws Exception
	{

		//Magic parser = new Magic() ;

		//      getMagicMatch accepts Files or byte[], 
		//      which is nice if you want to tests streams

		// MagicMatch match = parser.getMagicMatch(new File("gumby.gif"));

		MagicMatch match;

		try
		{
			match = Magic.getMagicMatch(firstBytes);
			return  match.getMimeType(); 
		}
		catch (MagicParseException e)
		{
			throw new Exception("MagicParseException:\n"+e.getMessage(),e);  
		}
		catch (MagicMatchNotFoundException e)
		{
			throw new Exception("MagicMatchNotFoundException\n"+e.getMessage(),e);  
		}
		catch (MagicException e)
		{
			throw new Exception("MagicException\n"+e.getMessage(),e); 
		}
	}

	public String getMagicMimeType(File file)
	{
		// getMagicMatch accepts Files or byte[], 
		// which is nice if you want to tests streams
		// MagicMatch match = parser.getMagicMatch(new File("gumby.gif"));

		MagicMatch match;
		 
		try
		{ 
			match = Magic.getMagicMatch(file,false);  
			return  match.getMimeType();
		}
		catch (Exception e)
		{
		    logger.logException(ClassLogger.WARN,e,"Couldn't parse MagicMime type for:%s\n",file); 
		}
		
		return null;
		
	}
}
