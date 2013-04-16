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

package nl.esciencecenter.ptk.util;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Properties;

import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

/**
 * Generic ResourceLoader class which supports URIs and URLs.
 * <p>
 * 
 * @author Piter.T. de Boer
 */
public class ResourceLoader
{
    /** Default UTF-8 */
    public static final String CHARSET_UTF8 = "UTF-8";

    /** Legacy UTF-16 Big Endian */
    public static final String CHARSET_UTF16BE = "UTF-16BE";

    /** Legacy UTF-16 Little Endian */
    public static final String CHARSET_UTF16LE = "UTF-16LE";

    /** 7-bits (US) ASCII, mother of all ASCII's */
    public static final String CHARSET_US_ASCII = "US-ASCII";

    /** 8-bits US/Euro 'standard' encoding */
    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";

    /** Latin is an alias for ISO-8859-1 (All Roman/Latin languages) */
    public static final String CHARSET_LATIN = "ISO-8869-1";

    /** Old EXTEND (US) ASCII Code Page 437 */
    public static final String CHARSET_CP437 = "CP437";

    /** Default is UTF-8 */
    public static final String DEFAULT_CHARSET = CHARSET_UTF8;

    public static final String charEncodings[] =
    { CHARSET_UTF8, CHARSET_UTF16BE, CHARSET_UTF16LE, CHARSET_US_ASCII, CHARSET_ISO_8859_1, CHARSET_LATIN,
            CHARSET_CP437 };

    private static ResourceLoader instance;

    private static ClassLogger logger;

    // =================================================================
    // Static methods
    // =================================================================

    static
    {
        logger = ClassLogger.getLogger(ResourceLoader.class);
        // logger.setLevelToDebug();
    }

    public static String[] getCharEncodings()
    {
        return charEncodings;
    }

    public static ResourceLoader getDefault()
    {
        if (instance == null)
            instance = new ResourceLoader(null);

        return instance;
    }

    // =================================================================
    // Instance
    // =================================================================

    protected String charEncoding = DEFAULT_CHARSET;

    protected URLClassLoader classLoader = null;

    protected FSUtil fsUtil;

    public ResourceLoader()
    {
        init(null);
    }

    /**
     * Initialize ResourceLoader with extra URL search path. When resolving a
     * relative URL these path URLs will be searched as well.
     * 
     * @param urls
     *            Optional URL search path
     */
    public ResourceLoader(URL urls[])
    {
        init(urls);
    }

    protected void init(URL urls[])
    {
        if (urls != null)
        {
            // context class loader including extra search path:
            ClassLoader parent = Thread.currentThread().getContextClassLoader();
            classLoader = new URLClassLoader(urls, parent);
        }
        fsUtil = FSUtil.getDefault();
    }

    /**
     * Tries to load resource from relative or absolute url: <br>
     * - I) get current classLoader to resource 'urlstr'<br>
     * - II) get thread classload to resolve 'urlstr'<br>
     * - III) tries if urlstr is an absolute url and performs
     * openConnection().getInputStream()<br>
     * 
     * @param urlstr
     *            can be boths relative (classpath) url or URI
     * @return InputStream
     * @throws VlIOException
     * @throws VlIOException
     */
    public InputStream getInputStream(String urlstr) throws IOException
    {
        URL url=resolveUrl(null, urlstr); 
        if (url==null)
            throw new FileNotFoundException("Couldn't resolve:"+urlstr); 
        
        return getInputStream(url);
    }

    /**
     * Returns an inputstream from the specified URI.
     * 
     * @param uri
     * @return
     * @throws VlException
     */
    public InputStream getInputStream(URL url) throws IOException
    {
        if (url==null)
            throw new NullPointerException("URL is NULL!"); 
        
        try
        {
            return url.openConnection().getInputStream();
        }
        catch (IOException e)
        {
            // wrap:
            throw new IOException("Cannot get inputstream from" + url + "\n" + e.getMessage(), e);
        }
    }

    public InputStream getInputStream(URI uri) throws IOException
    {
        try
        {
            // use URL compatible method
            return uri.toURL().openConnection().getInputStream();
        }
        catch (IOException e)
        {
            // Wrap:
            throw new IOException("Cannot get inputstream from:" + uri + "\n" + e.getMessage(), e);
        }
    }

    /** Returns resource as String */
    public String getText(URL location, String charset) throws IOException
    {
        InputStream inps = getInputStream(location);
        return getText(inps, charset);
    }

    /** Returns resource as String */
    public String getText(URL loc) throws IOException
    {
        InputStream inps = getInputStream(loc);
        return getText(inps, null);
    }

    /** Returns resource as String */
    public String getText(URI uri) throws IOException
    {
        InputStream inps = getInputStream(uri.toURL());
        return getText(inps, null);
    }

    public byte[] getBytes(URL loc) throws IOException
    {
        InputStream inps = getInputStream(loc);
        byte bytes[] = getBytes(inps);
        try
        {
            inps.close();
        }
        catch (Exception e)
        {
            ;
        }
        return bytes;
    }

    public byte[] getBytes(String pathOrUrl) throws IOException
    {
        InputStream inps = getInputStream(pathOrUrl);
        byte bytes[] = getBytes(inps);
        try
        {
            inps.close();
        }
        catch (Exception e)
        {
            ;
        }
        return bytes;
    }

    /**
     * Read text from input stream in encoding 'charset'. (Default is utf-8)
     * Does this line by line. Line seperators might be changed!
     */
    public String getText(InputStream inps, String charset) throws IOException
    {
        if (charset == null)
            charset = charEncoding;

        // just read all:
        try
        {
            byte bytes[] = getBytes(inps);
            return new String(bytes, charset);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IOException("UnsupportedEncoding:" + charset, e);
        }
    }

    /** Read all bytes from inputstream */
    public byte[] getBytes(InputStream inps) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] buf = new byte[32 * 1024]; // typical TCP/IP packet size:
        int len = 0;

        try
        {
            while ((len = inps.read(buf)) > 0)
            {
                bos.write(buf, 0, len);
            }
        }
        catch (IOException e)
        {
            throw new IOException("Couldn't read from input stream", e);
        }

        byte[] data = bos.toByteArray();
        return data;
    }

    /**
     * Load properties file from specified location.<br>
     * <b>IMPORTANT</b>: When this method is used and the URL stream factory
     * handler HAS NOT BEEN SET, only default url schemes can be used !
     * (file:/,http://).
     */
    public Properties loadProperties(URI uri) throws IOException
    {
        // must use URL so it works during bootstrap !
        // (After bootstap new schemes will be possible)
        return loadProperties(uri.toURL());
    }

    public Properties loadProperties(URL url) throws IOException
    {
        Properties props = new Properties();

        try
        {
            InputStream inps = this.getInputStream(url);
            props.load(inps);
            logger.debugPrintf("Read properties from:%s\n", url);
            try
            {
                inps.close();
            }
            catch (Exception e)
            {
                ;
            }
        }
        catch (IOException e)
        {
            throw new IOException("Couldn't load propertied from:" + url + "\n" + e.getMessage(), e);
        }
        // in the case of applet startup: Not all files are
        // accessable, wrap exception for gracfull exception handling.
        catch (java.security.AccessControlException ex)
        {
            // Applet/Servlet environment !
            throw new IOException("Security Exception: Permission denied for:" + url, ex);
        }

        for (Enumeration<Object> keys = props.keys(); keys.hasMoreElements();)
        {
            String key = (String) keys.nextElement();
            String value = props.getProperty(key);
            logger.debugPrintf("Read property='%s'='%s'\n", key, value);
        }

        return props;
    }

    /** Returns char encoding which is used when reading text. */
    public String getCharEncoding()
    {
        return charEncoding;
    }

    /** Specify char encoding which is used when reading text. */
    public void setCharEncoding(String encoding)
    {
        charEncoding = encoding;
    }

    public String getText(String url) throws IOException
    {
        return this.getText(this.getInputStream(url), this.getCharEncoding());
    }

    /**
     * Resolve URL string to absolute URL
     * 
     * @see ResourceLoader#resolveUrl(ClassLoader, String)
     */
    public URL resolveUrl(String url)
    {
        return resolveUrl(null, null);
    }

    /**
     * Resolve relative resource String and return absolute URL. The URL String
     * can be matched against the optional ClassLoader in the case the URL
     * points to a resource loaded by a custom ClasLoader that is not accessible
     * by the classloader which loaded this (ResourceLoader) class.
     * 
     * If the ResourceLoader has been initialized with extra (ClassPath) URLs,
     * these will be searched also.
     * 
     * @param optClassLoader
     *            - Optional ClassLoader from plugin class Loader
     * @param url
     *            - relative URL String, might be absolute but then there is
     *            nothing to 'resolve'.
     * @return resolved Absolute URL
     */
    public URL resolveUrl(ClassLoader optClassLoader, String url)
    {
        URL resolvedUrl = null;

        logger.debugPrintf("resolveUrl():%s\n", url);

        if (url == null)
            throw new NullPointerException("URL String can not be null");

        // (I) First optional Class Loader !
        if (optClassLoader != null)
        {
            resolvedUrl = optClassLoader.getResource(url);

            if (resolvedUrl != null)
            {
                logger.debugPrintf("resolveUrl() I: Resolved URL by using extra class loader:%s\n", resolvedUrl);
            }
        }

        // (II) Use Reource Classloader
        if ((resolvedUrl == null) && (this.classLoader != null))
        {
            resolvedUrl = this.classLoader.getResource(url);

            if (resolvedUrl != null)
            {
                logger.debugPrintf("resolveURL() II:Resolved URL by using resource classloader:%s\n", resolvedUrl);
            }
        }

        // (III) Check default (global) classloader for icons which are on the
        // classpath
        if (resolvedUrl == null)
        {
            resolvedUrl = this.getClass().getClassLoader().getResource(url);

            if (resolvedUrl != null)
            {
                logger.debugPrintf("resolveURL() III:Resolved URL by using global classloader:%s\n", resolvedUrl);
            }
        }

        // keep as is:
        if (resolvedUrl == null)
        {
            try
            {
                URL url2 = new URL(url);
                resolvedUrl = url2;
            }
            catch (MalformedURLException e)
            {
                logger.debugPrintf("resolveURL() IV: Not an absolute url:%s\n", url);
            }
        }

        logger.debugPrintf("resolveURL(): '%s' -> '%s' \n", url, resolvedUrl);

        return resolvedUrl;
    }

    /** Returns current search path */
    public URL[] getSearchPath()
    {
        URL urls[] = null;

        if (this.classLoader != null)
            urls = this.classLoader.getURLs();

        return urls;
    }

    /**
     * Save properties file to specified location.
     */
    public void saveProperties(URI loc, Properties props) throws IOException
    {
        saveProperties(loc, props, "Properties file");
    }

    /**
     * Save properties file to specified location.
     */
    public void saveProperties(URI loc, Properties props, String comments) throws IOException
    {
        OutputStream outps = createOutputStream(loc);
        props.store(outps, comments);
        try
        {
            outps.close();
        }
        catch (Exception e)
        {
            ;
        }
    }

    public OutputStream createOutputStream(URI loc) throws IOException
    {
//        // local file:
//        if (isLocalLocation(loc))
//        {
//            return fsUtil.getFileOutputStream(loc.getPath());
//        }
        try
        {
            URL url = loc.toURL();
            return createOutputStream(url);
        }
        catch (IOException e)
        {
            throw e;
        }
    }

    public OutputStream createOutputStream(URL url) throws IOException
    {
        try
        {
            return url.openConnection().getOutputStream();
        }
        catch (IOException e)
        {
            // wrap:
            throw new IOException("Cannot get OutputStream from" + url + "\n" + e.getMessage(), e);
        }
    }

}