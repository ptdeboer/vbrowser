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

package nl.esciencecenter.ptk.net;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.object.Duplicatable;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

/**
 * Virtual Resource Locator Class. 
 * URI compatible Class. 
 * See URIFactory.  
 */
public final class VRI implements Cloneable,Comparable<VRI>, Duplicatable<VRI>, Serializable
{
    private static final long serialVersionUID = -3255450059796404575L;

    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(VRI.class);
    }
    
    public static VRI createVRI(URIFactory factory, boolean duplicateFactory)
    {
        if (duplicateFactory)
            factory=factory.duplicate();
        return new VRI(factory.duplicate());
    }
    
    public static VRI createDosVRI(String vrlstr) throws VRISyntaxException
    {
        String newStr=vrlstr.replace('\\','/'); 
        // constructor might change ! 
        return new VRI(newStr); 
    }
    
    // ========
    // Instance 
    // ========
    
    protected URIFactory uriFactory;
    
    protected VRI()
    {
    }
    
    protected VRI(URIFactory factory)
    {
        uriFactory=factory;
    }
    
    public VRI(String uristr) throws VRISyntaxException
    {
        init(uristr); 
    }

    public VRI(URL url) throws VRISyntaxException
    {
        try
        {
            init(url.toURI());
        }
        catch (URISyntaxException e)
        {
            throw new VRISyntaxException(e);
        }
    }
 
    public VRI(URI uri)
    {
       init(uri);
    }

    public VRI(VRI other)
    {
        this.uriFactory=other.uriFactory.duplicate(); 
    }
    
    public VRI duplicate()
    {
        return createVRI(uriFactory,true); 
    }
    
    public VRI(String scheme, String host, String path)
    {
        init(scheme,null,host,-1,path, null,null); 
    }

    public VRI(String scheme, String host,int port, String path)
    {
        init(scheme,null,host,port,path, null,null); 
    }

    public VRI(String scheme, String userinfo,String host,String path)
    {
        init(scheme,userinfo,host,-1,path,null,null); 
    }

    public VRI(String scheme,String userInfo, String host,int port, String path)
    {
        init(scheme,userInfo,host,port,path, null,null); 
    }

    public VRI(String scheme,
            String userInfo, 
            String hostname, 
            int port,
            String path,
            String query,
            String fragment)
    {
        init(scheme,userInfo,hostname,port,path,query,fragment); 
    }

    private void init(String uriStr) throws VRISyntaxException
    {
        try
        {
            this.uriFactory=new URIFactory(uriStr);
        }
        catch (URISyntaxException e)
        {
            throw new VRISyntaxException("Cannot create URIFactory from:uristr",e);
        } 
    }
    
    private void init(URI uri)
    {
        this.uriFactory=new URIFactory(uri);
    }

    private void init(String scheme, 
                String userInfo, 
                String host, 
                int port, 
                String path, 
                String query, 
                String fragment)
    {
        this.uriFactory=new URIFactory(scheme,userInfo,host,port,path,query,fragment);
    }
    
    @Override
    public boolean shallowSupported()
    {
        return false;
    }

    @Override
    public VRI duplicate(boolean shallow)
    {
        return duplicate();
    }
    
    public int compareToObject(Object other)
    {   
        if ((other instanceof VRI)==false)
            return -1; 
            
        return compareTo((VRI)other);
    }
    
    /**
     * Compares this VRI with other VRI based on normalized String representations. 
     */
    @Override
    public int compareTo(VRI other)
    {   
        return StringUtil.compare(this.toNormalizedString(), other.toNormalizedString(),false);
    }
    
    /**
     * Returns hash code of normalized URI String. 
     */
    @Override
    public int hashCode()
    {
        return toNormalizedString().hashCode(); 
    }
    
    @Override
    public boolean equals(Object other)
    {
        if (other==null)
            return false; 
        
        if ((other instanceof VRI)==false)
            return false;
        
        return (compareTo((VRI)other)==0);
    }
    
    // ========================================================================
    // String/URI Formatters
    // ========================================================================
 
    public String toString()
    {
        return uriFactory.toString(); 
    }
    
    public java.net.URI toURI() throws URISyntaxException
    {
        return uriFactory.toURI(); 
    }
    
    public String toNormalizedString()
    {
        return uriFactory.toNormalizedString(); 
    }
    
    /** Calls toURI().toURL() */ 
    public java.net.URL toURL() throws MalformedURLException
    {
        try
        {
            return uriFactory.toURI().toURL();
        }
        catch (URISyntaxException e)
        {
          throw new MalformedURLException("Bad URI:"+uriFactory.toNormalizedString());
        } 
    }
    
    // ========================================================================
    // Getters
    // ========================================================================
    
    public String getScheme()
    {
        return uriFactory.getScheme();
    }
    
    /** 
     * Returns username part from userinfo if it as one 
     */
    public String getUsername()
    {
        String info = uriFactory.getUserInfo();

        if (info == null)
            return null;

        // strip password:
        String parts[] = info.split(":");

        if ((parts == null) || (parts.length == 0))
            return info;

        if (parts[0].length() == 0)
            return null;

        return parts[0];
    }

    /**
     * Returns password part (if specified !) from userInfo string
     * 
     * @deprecated It is NOT safe to use clear text password in any URI!
     */
    public String getPassword()
    {
        String info = uriFactory.getUserInfo();

        if (info == null)
            return null;

        String parts[] = info.split(":");

        if ((parts == null) || (parts.length < 2))
            return null;

        return parts[1];
    }
    
    public String getBasename()
    {
        return uriFactory.getBasename();
    }
    
    public String getHostname()
    {
        return uriFactory.getHostname();
    }    
    
    public int getPort()
    {
        return uriFactory.getPort();
    }

    public String getPath()
    {
        return uriFactory.getPath();
    }

    public String getQuery()
    {
        return uriFactory.getQuery();
    }
    
    public String getFragment()
    {
        return uriFactory.getFragment();
    }
    
    public boolean hasHostname(String otherHostname)
    {
        return StringUtil.equals(this.uriFactory.getHostname(), otherHostname); 
    }
    
    public VRI getParent()
    {
        return createVRI(uriFactory.getParent(),true);
    }
    
    public boolean isVLink()
    {
        return hasExtension("vlink",false); 
    }

    public boolean isRootPath()
    {
        // normalize path: 
        String upath=URIFactory.uripath(uriFactory.getPath());
        //Debug("isRootPath(): uri path="+upath); 
        
        if (StringUtil.isEmpty(upath))
            return true; 
        
        // "/"
        if (upath.compareTo(URIFactory.SEP_CHAR_STR)==0) 
            return true; 
        
        // uripath normalized windosh root "/X:/" 
        if (upath.length()==4)
            if ((upath.charAt(0)==URIFactory.SEP_CHAR) && (upath.substring(2,4).compareTo(":/")==0))
                return true; 
        
        return false; 
    }
    
    /**
     * Compares host to empty hostname, localhost, and actual fully qualified
     * hostname. Don not rely on this method. Use actual network interface. This
     * is just an indication.
     */
    public boolean isLocalLocation()
    {
        String host = this.getHostname();
        if (StringUtil.isEmpty(host))
            return true;
        
        if (StringUtil.compare(host, "localhost", true) == 0)
            return true;
        
        // ipv4:
        if (StringUtil.compare(host, "127.0.0.1", true) == 0)
            return true;
        
        // ipv6:
        if (StringUtil.compare(host, "::1", true) == 0)
            return true;
        
        if (StringUtil.compare(host, GlobalProperties.getHostname(), true) == 0)
            return true;
        
        return false;
    }

    public String getUserinfo()
    {
       return this.uriFactory.getUserInfo(); 
    }
    
    // ========================================================================
    // Resolvers 
    // ========================================================================

    public VRI resolveSibling(VRI relLoc) throws VRISyntaxException
    {
        try
        {
            return createVRI(uriFactory.duplicate().resolveSibling(relLoc.toString()),false);
        }
        catch (URISyntaxException e)
        {
            throw new VRISyntaxException("Failed to resolve relative VRI:"+relLoc,e);
        }
    }
    
    public VRI resolveSibling(String path) throws VRISyntaxException
    {
        try
        {
            return createVRI(uriFactory.duplicate().resolveSibling(path),false);
        }
        catch (URISyntaxException e)
        {
            throw new VRISyntaxException("Failed to resolve lreative String:"+path,e);
        } 
    }
   
    public VRI resolvePath(String path) throws VRISyntaxException
    {
        try
        {
            return createVRI(uriFactory.duplicate().resolvePath(path),false);
        }
        catch (URISyntaxException e)
        {
            throw new VRISyntaxException("Failed to resolve path:"+path,e);
        } 
    }
    
    public VRI appendPath(String path)
    {
        return createVRI(uriFactory.duplicate().appendPath(path),false);
    }

    public VRI replacePath(String path)
    {
        return createVRI(uriFactory.duplicate().changePath(path),false);
    }
    
    /** Duplicate VRI but create new Query */
    public VRI copyWithNewQuery(String str)
    {
        return createVRI(uriFactory.duplicate().changeQuery(str),true); 
    }
    
    public VRI copyWithNewPort(int val)
    {
        return createVRI(uriFactory.duplicate().changePort(val),false);
    }

    public VRI replaceScheme(String newScheme)
    {
        return createVRI(uriFactory.duplicate().changeScheme(newScheme),false);
    }
    
    /** 
     * check whether URI (and path) is a parent location of <code>subLocation</code>.  
     * 
     * @param subLocation
     * @return
     */
    public boolean isParentOf(VRI subLocation)
    {
        String pathStr=toString(); 
        String subPath=subLocation.toString(); 
        
        // Current implementation is based on simple string comparison.
        // For this to work, both VRI strings must be normalized ! 
        // Debug("isSubPath:"+pathStr+","+subPath);
        
        if (subPath.startsWith(pathStr)==true)
        {
            // To prevent that paths like '<..>/dir123' appear to be subdirs of '<..>/dir' 
            // last part of subpath after '<..>/dir' must be '/' 
            // Debug("subPath.charAt="+subPath.charAt(pathStr.length()));
            
            if ((subPath.length()>pathStr.length()) && (subPath.charAt(pathStr.length())==URIFactory.SEP_CHAR)) 
                return true; 
        }
        
        return false; 
    }

    public VRI resolvePath(VRI relvrl) throws VRISyntaxException
    {
        // ambigious: 
        if (relvrl.uriFactory.isAbsolute())
            return relvrl;
         
        return resolvePath(relvrl.getPath()); 
    }
  
    // =============================
    // Extra VRI interface methods. 
    // =============================

    public String getExtension()
    {
        return uriFactory.getExtension(); 
    }

    public String getDirPath()
    {
        return this.getPath() + "/";
    }

    public boolean isRelative()
    {
        return uriFactory.isRelative();
    }
    
    public boolean isAbsolute()
    {
        return this.uriFactory.isAbsolute(); 
    }

    public String getBasename(boolean withExtension)
    {
       return this.uriFactory.getBasename(withExtension);
    }

    public String getDirname()
    {
        return uriFactory.getDirname(); 
    }
    
    /**
     * Returns granparent dirname. Calls dirname on dirname result
     */
    public String getDirdirname()
    {
        return URIFactory.dirname(URIFactory.dirname(getPath()));
    }
    
    public String[] getPathElements()
    {
       return uriFactory.getPathElements(); 
    }

    public boolean hasExtension(String ext, boolean matchCase)
    {
        String uriext = this.getExtension();
        if (uriext == null)
            return false;

        if (matchCase == false)
            return uriext.equalsIgnoreCase(ext);

        return uriext.equals(ext);
    }

    public boolean hasScheme(String otherScheme)
    {
        if (otherScheme==null)
            return false;
        
        return otherScheme.equals(getScheme()); 
    }

    /** 
     * Create URI, ignore exceptions. 
     * Use this method if it is shure the URI is valid. 
     * Exceptions are nested into Errors. 
     * 
     * @return URI representation of this VRI.
     */
    public URI toURINoException()
    {
        try
        {
            return this.toURI(); 
        }
        catch (Exception e)
        {
            throw new Error(e.getMessage(),e); 
        }
    }

    public String[] getQueryParts()
    {
        if (getQuery() == null)
            return null;

        return getQuery().split(URIFactory.ATTRIBUTE_SEPERATOR);
    }
    
    /** 
     * Returns true if this VRI has a non empty fragment part ('?...') in it. 
     */
    public boolean hasQuery()
    {
        return (StringUtil.isEmpty(getQuery()) == false);
    }

    /** 
     * Returns true if this VRI has a non empty fragment part ('#...') in it. 
     */
    public boolean hasFragment()
    {
        return (StringUtil.isEmpty(getFragment()) == false);
    }
}
