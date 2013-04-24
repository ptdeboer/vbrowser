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

package nl.nlesc.vlet.vrs.vrl;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.object.Duplicatable;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.data.VAttributeSet;
import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;

/**
 * Virtual Resource Locator Class. 
 * URI compatible Class. 
 * See URIFactory.  
 */
public final class VRL implements Cloneable,Comparable<VRL>, Duplicatable<VRL>, Serializable
{
    private static final long serialVersionUID = -3255450059796404575L;

    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(VRL.class);
    }
    
    /**
     * Static method to check for empty or localhost names 
     * and aliases (127.0.0.1) 
     */ 
    public static boolean isLocalHostname(String host)
    {
        if (host==null)
            return true;
            
        if (host.compareTo("")==0)
            return true;
        
        if (host.compareTo(VRS.LOCALHOST)==0)
            return true;
        
        if (host.compareTo("127.0.0.1")==0)
            return true; 
        
        if (host.compareTo(GlobalProperties.getHostname())==0)
            return true; 
        
        return false;
    }
    
    /**
     * Compares filepaths and return subpath of childPath relative to its parenPath. 
     * If the childPath is not subdirectory of the parentPath, null is returned. 
     */ 
    public static String relativePath(String parentPath, String childPath)
    {
        if ((childPath==null) || (parentPath==null))
            return null; 
        
        if (childPath.startsWith(parentPath)==false) 
            return null;
        
        // use NORMALIZED paths ! 
        parentPath=URIFactory.uripath(parentPath);
        
        childPath=URIFactory.uripath(childPath); 
        
        String relpath=childPath.substring(parentPath.length(),childPath.length());
        // strip path seperator 
        if (relpath.startsWith("/")) 
            relpath=relpath.substring(1);
        return relpath; 
    }

    public static String resolveScheme(String scheme)
    {
        return VRSContext.getDefault().resolveScheme(scheme);
    }
    
    public static VRL createVRL(URIFactory factory, boolean duplicateFactory)
    {
        if (duplicateFactory)
            factory=factory.duplicate();
        return new VRL(factory.duplicate());
    }
    
    public static VRL createDosVRL(String vrlstr) throws VRLSyntaxException
    {
        String newStr=vrlstr.replace('\\','/'); 
        // constructor might change ! 
        return new VRL(newStr); 
    }
    
    // ========
    // Instance 
    // ========
    
    protected URIFactory uriFactory;
    
    protected VRL()
    {
        
    }
    
    protected VRL(URIFactory factory)
    {
        uriFactory=factory;
    }
    
    public VRL(String uristr) throws VRLSyntaxException
    {
        init(uristr); 
    }

    public VRL(URL url) throws VRLSyntaxException
    {
        try
        {
            init(url.toURI());
        }
        catch (URISyntaxException e)
        {
            throw new VRLSyntaxException(e);
        }
    }
 
    public VRL(URI uri)
    {
       init(uri);
    }

    public VRL(VRL other)
    {
        this.uriFactory=other.uriFactory.duplicate(); 
    }
    
    public VRL duplicate()
    {
        return createVRL(uriFactory,true); 
    }
    
    public VRL(String scheme, String host, String path)
    {
        init(scheme,null,host,-1,path, null,null); 
    }

    public VRL(String scheme, String host,int port, String path)
    {
        init(scheme,null,host,port,path, null,null); 
    }

    public VRL(String scheme, String userinfo,String host,String path)
    {
        init(scheme,userinfo,host,-1,path,null,null); 
    }

    public VRL(String scheme,String userInfo, String host,int port, String path)
    {
        init(scheme,userInfo,host,port,path, null,null); 
    }

    public VRL(String scheme,
            String userInfo, 
            String hostname, 
            int port,
            String path,
            String query,
            String fragment)
    {
        init(scheme,userInfo,hostname,port,path,query,fragment); 
    }

    private void init(String uriStr) throws VRLSyntaxException
    {
        try
        {
            this.uriFactory=new URIFactory(uriStr);
        }
        catch (URISyntaxException e)
        {
            throw new VRLSyntaxException("Cannot create URIFactory from:uristr",e);
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
    public VRL duplicate(boolean shallow)
    {
        return duplicate();
    }

    @Override
    public int compareTo(VRL other)
    {   
        return StringUtil.compare(this.toNormalizedString(), other.toNormalizedString(),false);
    }
    
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
        
        if ((other instanceof VRL)==false)
            return false;
        
        return (compareTo((VRL)other)==0);
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
    
    // VRS interface: 

    public VAttributeSet getQueryAttributes()
    {
       String qstr=uriFactory.getQuery();
       
       // no query 
       if (qstr==null) 
           return null;
       
       // split in '&' parts 
       String stats[]=getQueryParts();        
       // empty list 
       if ((stats==null) || (stats.length<=0))
           return null; 
       
       VAttributeSet aset=new VAttributeSet(); 
       
       for (String stat:stats)
       {
           VAttribute attr=VAttribute.createFromAssignment(stat);
           //Debug("+ adding attribute="+attr); 
           if (attr!=null)
               aset.put(attr); 
       }
       return aset; 
    }
    
    public boolean hasHostname(String otherHostname)
    {
        return StringUtil.equals(this.uriFactory.getHostname(), otherHostname); 
    }
    
    public VRL getParent()
    {
        return createVRL(uriFactory.getParent(),true);
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
    
    // alternate method to cast Exception to VRLSyntaxException
    public VRL resolveToVRL(VRL relLoc) throws VRLSyntaxException
    {
        return this.resolve(relLoc); 
    }
    
    public VRL resolve(VRL relLoc) throws VRLSyntaxException
    {
        try
        {
            return createVRL(uriFactory.duplicate().resolveSibling(relLoc.toString()),false);
        }
        catch (URISyntaxException e)
        {
            throw new VRLSyntaxException("Failed to resolve relative VRL:"+relLoc,e);
        }
    }
    
    public VRL resolve(String path) throws VRLSyntaxException
    {
        try
        {
            return createVRL(uriFactory.duplicate().resolveSibling(path),false);
        }
        catch (URISyntaxException e)
        {
            throw new VRLSyntaxException("Failed to resolve lreative String:"+path,e);
        } 
    }
   
    public VRL resolvePath(String path) throws VRLSyntaxException
    {
        try
        {
            return createVRL(uriFactory.duplicate().resolvePath(path),false);
        }
        catch (URISyntaxException e)
        {
            throw new VRLSyntaxException("Failed to resolve path:"+path,e);
        } 
    }
    
    public VRL appendPath(String path)
    {
        return createVRL(uriFactory.duplicate().appendPath(path),false);
    }

    public VRL replacePath(String path)
    {
        return createVRL(uriFactory.duplicate().changePath(path),false);
    }
    
    /** Duplicate VRL but create new Query */
    public VRL copyWithNewQuery(String str)
    {
        return createVRL(uriFactory.duplicate().changeQuery(str),true); 
    }
    
    public VRL copyWithNewPort(int val)
    {
        return createVRL(uriFactory.duplicate().changePort(val),false);
    }

    public VRL replaceScheme(String newScheme)
    {
        return createVRL(uriFactory.duplicate().changeScheme(newScheme),false);
    }
    
    /** 
     * check whether URI (and path) is a parent location of <code>subLocation</code>.  
     * 
     * @param subLocation
     * @return
     */
    public boolean isParentOf(VRL subLocation)
    {
        String pathStr=toString(); 
        String subPath=subLocation.toString(); 
        
        // Current implementation is based on simple string comparison.
        // For this to work, both VRL strings must be normalized ! 
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

    public VRL resolvePathToVRL(VRL relvrl) throws VRLSyntaxException
    {
        // ambigious: 
        if (relvrl.uriFactory.isAbsolute())
            return relvrl;
         
        return resolvePath(relvrl.getPath()); 
    }
  
    // =============================
    // Extra VRL interface methods. 
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
        
    /**
     * Append Path, addes URI Seperator char "/" between path elements.  
     * Used append() for plain string appends or this one for filesystems paths. 
     */ 
    public VRL appendPathToVRL(String path)
    {
        return this.appendPath(path);
    }
    
    // alternate method to cast Exception to VRLSyntaxException
    public VRL resolveToVRL(String relLoc) throws VRLSyntaxException
    {
        return resolve(relLoc);        
    }
    
    // alternate method to cast Exception to VRLSyntaxException
    public VRL resolvePathToVRL(String path) throws VRLSyntaxException
    {
        return resolvePath(path);        
    }
    
    public VRL copyWithNewPathToVRL(String path)
    {
        return this.replacePath(path); 
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

    public URI toURINoException()
    {
        try
        {
            return this.toURI(); 
        }
        catch (Exception e)
        {
            return null; 
        }
    }

    public String[] getQueryParts()
    {
        if (getQuery() == null)
            return null;

        return getQuery().split(URIFactory.ATTRIBUTE_SEPERATOR);
    }
    
    /** 
     * Returns true if this VRL has a non empty fragment part ('?...') in it. 
     */
    public boolean hasQuery()
    {
        return (StringUtil.isEmpty(getQuery()) == false);
    }

    /** 
     * Returns true if this VRL has a non empty fragment part ('#...') in it. 
     */
    public boolean hasFragment()
    {
        return (StringUtil.isEmpty(getFragment()) == false);
    }


    
}
