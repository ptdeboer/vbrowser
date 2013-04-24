///*
// * Copyrighted 2012-2013 Netherlands eScience Center.
// *
// * Licensed under the Apache License, Version 2.0 (the "License").  
// * You may not use this file except in compliance with the License. 
// * For details, see the LICENCE.txt file location in the root directory of this 
// * distribution or obtain the Apache License at the following location: 
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software 
// * distributed under the License is distributed on an "AS IS" BASIS, 
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// * See the License for the specific language governing permissions and 
// * limitations under the License.
// * 
// * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
// * ---
// */
//// source: 
//
//package nl.esciencecenter.ptk.net;
//
//import java.io.Serializable;
//import java.net.MalformedURLException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.URL;
//
//import nl.esciencecenter.ptk.GlobalProperties;
//import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
//import nl.esciencecenter.ptk.object.Duplicatable;
//import nl.esciencecenter.ptk.util.StringUtil;
//
///**
// * Virtual Resource Indicator. 
// * URI compatible resource locator.
// *  
// * @author Piter T. de Boer. 
// */
//public class VRI implements Cloneable,Comparable<VRI>, Duplicatable<VRI>, Serializable
//{ 
//    private static final long serialVersionUID = 1453387786879921571L;
//
//    // =======================================================================
//    // Class Methods
//    // =======================================================================
//  
//    private String scheme;
//    
//    private String hostname;
//
//    private int port;
//    
//    private String userInfo;
//    
//    /** Contains path or scheme specific part without authorization,etc */ 
//    private String pathOrReference;
//    
//    private String query;
//    
//    private String fragment;
//
//    // === Field guards === // 
//    private boolean hasAuthority=false;
//
//    private boolean isReference;
//  
//    public VRI(URI uri)
//    {
//        init(uri);  
//    }
//    
//    protected VRI()
//    {
//    }
//    
//    public VRI(final String vristr) throws VRISyntaxException
//    {
//        init(vristr);
//    }
//    
//    protected void init(final String vristr) throws VRISyntaxException
//    {
//        if (vristr==null) 
//            throw new VRISyntaxException("VRI String can not be null");
//        
//        URISyntaxException ex1;//,ex2; 
//        
//        // ===
//        // TODO: other URI parsing class !
//        // URI parse bug. Java.net.URI doesn't allow empty schemespecific
//        // nor missing authority parts after "://"
//        // ===
//        
//        int index=vristr.indexOf(':');
//        
//        String sspStr=null;
//        
//        if (index>=0) 
//            sspStr=vristr.substring(index+1,vristr.length()); 
//        
//        if (sspStr!=null)
//            if (StringUtil.isEmpty(sspStr) 
//                || StringUtil.equals(sspStr,"/")
//                || StringUtil.equals(sspStr,"//"))
//        {
//            // Parse: "scheme:", "scheme:/" "scheme://". 
//            // create scheme only URI 
//            this.scheme=vristr.substring(0,index); 
//            this.hasAuthority=false; 
//            return; 
//        }
//        
//        
//        try
//        {
//            URI uri=new URI(vristr);
//         // use URI initializer: contains patches for URI parsing ! (better use initializors!)
//            init(uri); 
//            return; 
//        }
//        catch (URISyntaxException e1)
//        {
//            ex1=e1; 
//            // Be Flexible: could be not encoded string: try to encode it first 
//            // There is no way to detect whether the string is encoded if it contains a valid '%'. 
//            try
//            {
//                // VRI strings should be URI compatible so let the URI 
//                // class do the parsing ! 
//                // to be save encode the uriStr!
//              
//                URI uri=new URI(URIFactory.encode(vristr));
//                init(uri); // use URI initializer
//                return; 
//            }
//            catch (URISyntaxException e2)
//            {
//               ; //ex2=e2; 
//            }
//        } 
//        
//        throw new VRISyntaxException("Couldn't parse:"+vristr,ex1);  
//    }   
//    
//    public VRI(String scheme, String host, int port, String path)
//    {
//        init(scheme,null,host,port,path,null,null); 
//    }
//
//    public VRI(String scheme,String host, String path)
//    {
//        init(scheme,null,host,-1,path,null,null);
//    }
//    
//    public VRI(String scheme,String host, String path,String fragment)
//    {
//        init(scheme,null,host,-1,path,null,fragment);
//    }
//
//    public VRI(String scheme, String user, String host, int port, String path, String query, String fragment)
//    {
//        init(scheme,user,host,port,path,query,fragment);
//    }
//    
//    public VRI(final URL url) throws VRISyntaxException
//    {
//        try
//        {
//            init(url.toURI());
//        }
//        catch (URISyntaxException e)
//        {
//            throw new VRISyntaxException(e); 
//        } 
//    }
//    
//    public VRI(final VRI vri)
//    {
//        this.copyFrom(vri); 
//    }
//
//    /**
//     * Initialize VRL URI using provided URI object.
//     * Note that is the MAIN initializer. URI constructor is used
//     * then the URI is normalized, then the part are stored seperately
//     * 
//     */
//    protected void init(final URI _uri)
//    {
//        URI uri=_uri.normalize(); // a VRI is a stricter implementation then an URI ! 
//
//        // scheme: 
//        String newScheme=uri.getScheme();
//
//        boolean hasAuth=StringUtil.notEmpty(uri.getAuthority()); 
//        
//        String newUserInf=uri.getUserInfo();
//        String newHost=uri.getHost(); // Not: resolveLocalHostname(uri.getHost()); // resolveHostname(uri.getHost());
//        int newPort=uri.getPort();
//        
//        // ===========
//        // Hack to get path,Query and Fragment part from SchemeSpecific parth if the URI is a reference
//        // URI. 
//        // The java.net.URI doesn't parse "file:relativePath?query" correctly
//        // ===========
//        String pathOrRef=null;
//        String newQuery=null; 
//        String newFraq=null; 
//        
//        if ((hasAuth==false) && (uri.getRawSchemeSpecificPart().startsWith("/")==false)) 
//        {
//            // Parse Reference URI or Relative URI which is not URL compatible: 
//            // hack to parse query and fragment from non URL compatible 
//            // URI 
//            try
//            {
//                // check: "ref:<blah>?query#frag"
//                URI tempUri=null ;
//                
//                tempUri = new URI(newScheme+":/"+uri.getRawSchemeSpecificPart());
//                String path2=tempUri.getPath(); 
//                // get path but skip added '/': 
//                // set Path but keep as Reference ! (path is reused for that) 
//                
//                pathOrRef=path2.substring(1,path2.length()); 
//                newQuery=tempUri.getQuery(); 
//                newFraq=tempUri.getFragment(); 
//            }
//            catch (URISyntaxException e)
//            {
//                //Global.errorPrintStacktrace(e);
//                // Reference URI:   ref:<blahblahblah>  without starting '/' ! 
//                // store as is without parsing: 
//                pathOrRef=uri.getRawSchemeSpecificPart();  
//            }
//        }
//        else
//        {
//            // plain PATH URI: get Path,Query and Fragment.  
//            pathOrRef=uri.getPath(); 
//            newQuery=uri.getQuery(); 
//            newFraq=uri.getFragment(); 
//        }
//        
//        // ================================
//        // use normalized initializer
//        // ================================
//        init(newScheme,newUserInf,newHost,newPort,pathOrRef,newQuery,newFraq); 
//    }
//    
//    /**
//     * Master Initializer. 
//     * All fields are given and no exception is thrown. 
//     * Fields should be decoded.
//     * 
//     * Path can be relative or absolute but must be normalized with forward slashes.
//     *  
//     */ 
//    private void init(String newscheme,String userinf,String newhost,int newport,String newpath,String newquery,String newfrag)
//    {
//        // ============================
//        // Cleanup URI initialization. 
//        // ============================
//        
//        // must be null or uri will add empty values 
//        if (StringUtil.isEmpty(newhost))
//            newhost=null; // null => no hostname
//
//        if (StringUtil.isEmpty(userinf))
//            userinf=null; // null => no userinfo
//        
//        if (StringUtil.isEmpty(newquery))
//            newquery=null; // null => no userinfo
//        
//        if (StringUtil.isEmpty(newfrag))
//            newfrag=null; // null => no userinfo
//        
//        // port value of -1 leaves out port in URI !
//        // port==0 is use protocol default, SSH => 22, etc. 
//        if (newport<=0) 
//            newport=-1;
//        
//        // ===
//        // AUTHORITY  ::=  [ <userinfo> '@' ] <hostname> [ ':' <port> ] 
//        // ===
//        this.hasAuthority=StringUtil.notEmpty(userinf)
//                            || StringUtil.notEmpty(newhost) 
//                            || (port>0); 
//        
//        // ===
//        // Feature: Strip ':' after scheme
//        // ===
//        if ((newscheme!=null) && (newscheme.endsWith(":")))
//            newscheme=newscheme.substring(0,newscheme.length()-1);
//      
//        // ===
//        // Relative URI: does NOT have a scheme nor Authority !
//        // examples: "dirname/tmp", "#label", "?query#fragment","local.html",
//        // ===
//       // boolean isRelativeURI=StringUtil.isEmpty(newscheme) && (hasAuthority==false);
//        
//        // === 
//        // PATH 
//        // Sanitize path, but keep relative paths or reference paths intact
//        // if there is no authority ! 
//        // ====
//        newpath=URIFactory.uripath(newpath,hasAuthority); 
//        
//
//        // store duplicates (or null) ! 
//        this.scheme=StringUtil.duplicate(newscheme); 
//        this.userInfo=StringUtil.duplicate(userinf);  
//        this.hostname=StringUtil.duplicate(newhost); // Keep hostname as-is: // resolveHostname(hostname);
//        this.port=newport;
//        this.pathOrReference=StringUtil.duplicate(newpath);   
//        this.query=StringUtil.duplicate(newquery); 
//        this.fragment=StringUtil.duplicate(newfrag);
//    }
//    
//    private void setPath(String newPath)
//    {
//        setPath(newPath,hasAuthority()); 
//    }
//    
//    protected void setPort(int newPort)
//    {
//        this.port=newPort;
//    }
//    
//    protected void setQuery(String newQuery)
//    {
//        this.query=newQuery;
//    }
//    
//    /** Set new Path and format it to default URI path */ 
//    private void setPath(String path,boolean makeAbsolute)
//    {
//        // decode and normalize path 
//        this.pathOrReference=URIFactory.uripath(path,makeAbsolute);
//    }
//    
//    // ========================================================================
//    // Duplicatable interface 
//    // ========================================================================
//
//    public VRI duplicate()
//    {
//        VRI loc = new VRI();
//        loc.copyFrom(this); 
//        return loc; 
//    }
//
//    @Override
//    public boolean shallowSupported()
//    {
//        return false;
//    }
//
//    @Override
//    public VRI duplicate(boolean shallow)
//    {
//        return duplicate();//ignore shallow  
//    }
//
//    
//    protected void copyFrom(VRI loc)
//    {
//        // field guards: 
//        this.hasAuthority=loc.hasAuthority;
//        this.isReference=loc.isReference; 
//        
//        // duplicate Strings !
//        this.scheme=StringUtil.duplicate(loc.scheme);
//        this.userInfo=StringUtil.duplicate(loc.userInfo);
//        this.hostname=StringUtil.duplicate(loc.hostname);
//        this.port=loc.port;
//        this.pathOrReference=StringUtil.duplicate(loc.pathOrReference); 
//        this.query=StringUtil.duplicate(loc.query); 
//        this.fragment=StringUtil.duplicate(loc.fragment);
//    }
//    
//    public String getBasename()
//    {
//        return URIFactory.basename(pathOrReference);   
//    }
//
//    /** 
//     * returns basename part (last part) of this location.
//     * @param withExtension whether to keep the extension. 
//     */
//    public String getBasename(boolean withExtension)
//    {
//        String name=URIFactory.basename(getPath());
//        
//        if (withExtension==true)
//            return name;           
//        else
//            return URIFactory.stripExtension(name);
//    }
//    
//    public String getPath()
//    {
//        return pathOrReference; 
//    }
//    
//    /**
//     * Returns path with explicit "/" at the end. 
//     * This is mandatory for some Globus implementations */ 
//    public String getDirPath()
//    {
//        return this.getPath()+"/";
//    }
//    
//    public String getHostname() 
//    {
//        return hostname; 
//    }
//    
//    public int getPort() 
//    {
//        return port; 
//    }
// 
//
//    public String getFragment()
//    {
//        return this.fragment; 
//    }
//    
//    /**
//     * @return decoded reference component of URI
//     */
//    public String getReference()
//    {
//        return pathOrReference; 
//    }
//  
//    // Must be overriden to comply with hashCode() requirements ! 
//    public boolean equals(Object obj)
//    {
//        if (obj instanceof VRI)
//            return equals((VRI)obj);
//        else
//            return super.equals(obj); // should be false;
//    }
//    
//    /** By definition two VRIs are equivalent when their normalized String representations are similar */ 
//    public boolean equals(VRI locator)
//    {
//        return (this.compare(locator)==0);  
//    }
//    
//    /** By definition two VRIs are equivalent when their normalized String representations are similar */ 
//    public int compare(VRI locator)
//    {
//        // delegate to string compare which uses toString() to compare: 
//        return StringUtil.compare(this,locator); 
//    }
//    
//    // @see #toNormalizedString()
//    public String toString()
//    {
//        return toNormalizedString();
//    }
//
//    /**
//     * This method returns the DECODED URI string. 
//     * For an URI compatible string (with %XX encoding) 
//     * use toURI().toString() or toURIString() ! 
//     *  
//     * @return normalized and decoded VRI String. 
//     */
//    public String toNormalizedString()
//    {
//        // suport relative VRLs!
//        String str="";
//
//        if (this.isAbsolute()==true)
//            str+=scheme+":";
//
//        if (this.hasAuthority)
//        {
//            str+=URIFactory.SEP_CHAR_STR+URIFactory.SEP_CHAR_STR;
//        
//            if ((userInfo!=null) && (userInfo.compareTo("")!=0)) 
//                str+=userInfo+"@"; 
//        
//            if (hostname!=null)
//            {
//                str+=hostname; 
//                if (port>0) 
//                    str+=":"+port; 
//            }
//        }
//        
// 
//        // path could start without "/" ! 
//        if (pathOrReference!=null)
//            str+=pathOrReference; 
//        else
//            str+=URIFactory.SEP_CHAR; // still end with '/' for consistancy ! 
//        
//        if (query!=null)
//            str+="?"+query;
//        
//        if (fragment!=null)
//            str+="#"+fragment;  
//        
//        return str; 
//    }
//    
//    
//    public boolean isAbsolute()
//    {
//        return (scheme!=null);
//    }
//    
//    public boolean isRelative()
//    {
//        return (scheme==null);
//    }
//    
//    public boolean hasScheme(String otherScheme) 
//    {
//        return StringUtil.equals(this.scheme,otherScheme); 
//    }
//
//    public String getScheme() 
//    {
//        return this.scheme;  
//    }
//
//    public VRI getParent()
//    {
//        VRI loc = duplicate();
//        loc.pathOrReference=URIFactory.dirname(this.pathOrReference);
//        return loc; 
//    }
//    
//    public boolean hasAuthority()
//    {
//        return this.hasAuthority;
//    }
//    
//    public boolean hasUserInfo()
//    {
//        return (userInfo!=null); 
//    }
//   
//    /** Get last part of filename starting from a '.' but ignore fragment and/or query part ! */ 
//    public String getExtension()
//    {
//        return URIFactory.extension(getPath());
//    }
//
//    public boolean hasExtension(String ext, boolean matchCase)
//    {
//        String vriext=this.getExtension(); 
//        if (vriext==null)
//            return false; 
//        
//        if (matchCase==false)
//            return vriext.equalsIgnoreCase(ext);
//
//        return vriext.equals(ext); 
//    }
//    
//    public String getQuery()
//    {
//        return this.query; 
//    }
//    
//    public String[] getQueryParts()
//    {
//        if (getQuery()==null) 
//            return null; 
//        
//        return this.getQuery().split(URIFactory.ATTRIBUTE_SEPERATOR);
//    }
//   
//
//    public String getUserinfo()
//    {
//        return this.userInfo;
//    }
//
//    /** Returns username part from userinfo if it as one */ 
//    public String getUsername()
//    {
//        String info=this.userInfo;
//        
//        if (info==null) 
//            return null; 
//        
//        // strip password: 
//        String parts[]=info.split(":");
//   
//        if ((parts==null) || (parts.length==0)) 
//            return info;
//   
//        if (parts[0].length()==0)
//            return null;
//        
//        return parts[0]; 
//    }
//    
//    /**
//     *  Returns password part (if specified !) from userInfo string 
//     *  @deprecated It is NOT safe to use clear text password in any URI! 
//     */ 
//    public String getPassword()
//    {
//        String info=userInfo;
//        
//        if (info==null) 
//            return null; 
//        
//        String parts[]=info.split(":");
//        
//        if ((parts==null) || (parts.length<2)) 
//            return null;
//        
//        return parts[1]; 
//    }  
//   
//    
//    /** Calls toURI().toString() */  
//    public String toURIString() throws URISyntaxException
//    {
//        return this.toURI().toString(); 
//    }
//
//    /**
//     * Returns the hashcode of the String representation of this VRI.  
//     * @see String.hashCode(); 
//     */
//    public int hashCode()
//    {
//        return toString().hashCode(); 
//    }
//    
//    /** 
//     * Appends plain string to this VRI's String representation. 
//     * Does *NOT* check for fragments or query strings. 
//     * When appending filepaths use explicit appendPath()! 
//     */ 
//    public VRI append(String substr) throws VRISyntaxException
//    {
//        return new VRI(this.toString()+substr); 
//    }
//    
//    /** 
//     * Creates new location by appending path to this one added seperator char between path elements. 
//     * To added query and/or fragments string use append().s
//     */ 
//    public VRI appendPath(String dirname)
//    {
//        VRI newLoc=this.duplicate();
//        
//        String oldpath=newLoc.getPath(); 
//        String newpath=null; 
//       
//        if (oldpath==null) 
//            oldpath=""; 
//        
//        if (StringUtil.isEmpty(dirname)) 
//        {
//            return duplicate(); // nothing to append
//        }
//        else if (dirname.charAt(0)==URIFactory.SEP_CHAR)
//        {
//            newpath=oldpath+dirname; 
//        }
//        else if (dirname.charAt(0)==URIFactory.DOS_SEP_CHAR)
//        {
//            newpath=oldpath+URIFactory.SEP_CHAR+dirname.substring(1);
//        }
//        else
//        {
//            newpath=oldpath+URIFactory.SEP_CHAR+dirname;
//        }
//        
//        // sanitize path: 
//        newLoc.setPath(newpath,true);  
//        
//        return newLoc; 
//    }
//    
//    /**
//     * Resolves optional relative URI using this URI 
//     * as base location. 
//     * Note: the last part (basename) of the URI is stripped, 
//     * assuming the URI starts from a file, for example 
//     * "http://myhost/index.html", base location = "http://myhost/".   
//     * Any relative url starts from "http://myhost/" NOT "http://myhost/index.html"  
//     * Also the supplied string must match URI paths (be %-encoded).
//     * 
//     * @throws VRISyntaxException
//     */
//    public VRI resolve(String reluri) throws VRISyntaxException 
//    { 
//        if ((reluri==null)||(reluri==""))
//            return this; 
//            
//        // resolve must have ENCODED URI paths...
//        URI uri;
//        
//        try
//        {
//            // normalize relative uri: 
//            String encodedPath; 
//            char c0=reluri.charAt(0); 
//            // keep query and fragment uri as-is. 
//            if ((c0=='#') || (c0=='?'))
//                encodedPath=reluri; 
//            else
//                encodedPath=URIFactory.encode(URIFactory.uripath(reluri,false));
//            uri = toURI().resolve(encodedPath); // *encoded* URI 
//        }
//        catch (URISyntaxException e)
//        {
//            throw new VRISyntaxException("Couldn't resolve:"+reluri,e); 
//        }
//        
//        // Debug("resolve:"+this+","+reluri+"="+uri); 
//        VRI newVRI=new VRI(uri);
//
//        // authority update  bug. keep either file:/// or file:/
//        newVRI.hasAuthority=this.hasAuthority;
//        return newVRI; 
//    }
//    
//    /** 
//     * Resolves relative VRI again this location
//     *  
//     * @see java.net.URI#resolve(String) 
//     * @throws VRISyntaxException 
//     */ 
//    public VRI resolve(VRI loc) throws VRISyntaxException
//    {
//        // Absolute VRI cannot be resolved. It is already absolute. 
//        if (loc.isAbsolute()==true)
//            return loc;
//        // delegate to URI resolve methods ! 
//        VRI newVrl;
//        
//        try
//        {
//            newVrl = new VRI(this.toURI().resolve(loc.toURI()));
//        }
//        catch (URISyntaxException e)
//        {
//          throw new VRISyntaxException("Couldn't resolve:"+loc,e); 
//        }
//        
//        newVrl.hasAuthority=this.hasAuthority; 
//        return newVrl; 
//    }
//    
//    /**
//     * Resolve DECODED (no %-chars) relative path against this URI, assuming
//     * this URI is a (directory) PATH and not a file or url location. 
//     * The default URI.resolve() method strips the last part 
//     * (basename) of an URI and uses that as base URI (for example the 
//     * "index.html" part). Also the relative URIs must be %-coded 
//     * when they contain spaces ! 
//     * 
//     * This method doesn't strip the last part of the URI. 
//     * 
//     * @param relpath decoded relative path without fragments or queries ('#' or '?')
//     * @return
//     * @throws VRISyntaxException 
//     */
//    public VRI resolvePath(String relpath) throws VRISyntaxException 
//    { 
//        // add dummy html, and use URI 'resolve' (which expects .html file)
//        VRI kludge=this.appendPath("dummy.html"); 
//        return kludge.resolve(URIFactory.uripath(relpath,false)); // resolves encoded path!
//    }
// 
//    /** 
//     * Resolves relative URL again this location
//     *  
//     * @throws VlURISyntaxException 
//     */ 
//    public VRI resolvePath(VRI loc) throws VRISyntaxException
//    {
//        return resolvePath(loc.getPath()); 
//    }
//
//    public URI toURI() throws URISyntaxException
//    {
//        try
//        { 
//            if (this.hasAuthority())
//            {
//                return new URI(this.scheme,
//                        this.userInfo,
//                        this.hostname,
//                        this.port,
//                        this.getPath(),
//                        this.query,
//                        this.fragment); 
//                        
//            }
//            else 
//            {
//                // create Reference URI 
//                return new URI(this.scheme,
//                        null,
//                        null,
//                        -1,
//                        this.getReference(),
//                        this.query,
//                        this.fragment); 
//                
//            }
//            //else
//            //{
//            //    return new URI(encode(this.toString()));
//            //}
//        }
//        catch (URISyntaxException e)
//        {
//            throw e; // new URISyntaxException(e);
//        }  
//        
//    }
//
//    /** 
//     * Explicitly encode URI. 
//     * Does not check if fields are already encoded.  
//     */
//    public URI toEncodedURI() throws URISyntaxException
//    {
//        return new URI(scheme,
//                userInfo,
//                hostname, 
//                port,
//                URIFactory.encode(pathOrReference),
//                URIFactory.encode(query),
//                URIFactory.encode(fragment)); 
//    }
//    
//    public URL toURL() throws MalformedURLException
//    {
//        return new URL(toString());   
//    }
//
//    /** Create duplicate, but with path set to newPath */ 
//    public VRI replacePath(String newPath)
//    {
//        VRI loc=duplicate();
//        // format path ! make absolute if has authority part:
//        loc.setPath(newPath);
//        return loc; 
//    }
//    
//    /** Create copy but with new scheme */  
//    public VRI replaceScheme(String newscheme)
//    {
//        VRI vrl=this.duplicate(); 
//        vrl.scheme=newscheme; 
//        return vrl; 
//    }
//    
//    /** Duplicate VRL but create new Query */
//    public VRI replaceQuery(String str)
//    {
//        VRI vrl=this.duplicate();
//        vrl.query=str;
//        return vrl; 
//    }
//    
//    public VRI replaceUserinfo(String user)
//    {
//        VRI loc=this.duplicate();
//        loc.userInfo=user; 
//        //loc.normalize(); 
//        return loc; 
//    }
//
//    public boolean hasHostname(String host)
//    {
//        return StringUtil.equals(hostname,host); 
//    }
//
//    /** Returns dirname part of path */ 
//    public String getDirname()
//    {
//        return URIFactory.dirname(this.pathOrReference);  
//    }
//    
//    /** 
//     * Returns granparent dirname. Calls dirname on dirname result 
//     */ 
//    public String getDirdirname()
//    {
//        return URIFactory.dirname(URIFactory.dirname(getPath())); 
//    }
//    
//    public int compareToObject(Object val)
//    {
//        if ((val instanceof VRI)==false)
//            return -1;
//            
//        return compareTo((VRI)val);
//    }
//    
//    /** 
//     * Compares this location to loc. 
//     * Note that Hostname aliases are NOT checked here.
//     * Make sure that fully qualified hostnames are present in the VRL. 
//     *   
//     * @see toString
//     * */
//    public int compareTo(VRI loc)
//    {
//         //  return this.toString().compareTo(loc.toString());
//        
//        if (loc==null) 
//            return 1; // this > null 
//        
//        // MUST normalize scheme names ! (use static registry) 
//        // String scheme1= VRS.getDefaultScheme(this.scheme); 
//        // String scheme2= VRS.getDefaultScheme(loc.scheme); 
//        String scheme1= this.scheme; 
//        String scheme2= loc.getScheme();  
// 
//        // Scheme compare: cany be null in the case of relative VRLs !  
//        int val=StringUtil.compareIgnoreCase(scheme1,scheme2); 
//        if (val!=0)
//            return val;
//       
//        String host1=this.hostname; // resolveHostname(this.hostname);  
//        String host2=loc.hostname;  //resolveHostname(loc.hostname);
//        
//        // normalize hostnames: null,empty and 'localhost' are equivalent! 
//        //if ((host1==null) ||  (host1.compareTo("")==0))
//        //    host1=VRS.LOCALHOST;
//        //if ((host2==null) ||  (host2.compareTo("")==0))
//        //    host2=VRS.LOCALHOST; 
//        
//        //logical compare ? 
//        //host1=resolveHostname(host1);  
//        //host2=resolveHostname(host2); 
//        
//        val=StringUtil.compareIgnoreCase(host1,host2); 
//        
//        if (val!=0)
//            return val;
//        
//        int p1=this.port;
//        int p2=loc.port; 
//        if (p1<0)
//            p1=0; 
//        if (p2<0)
//            p2=0;
//        
//        if (p1<p2)
//            return -1;
//    
//        if (p1>p2) 
//            return 1; 
//        
//        // case SENSITIVE path: 
//        val=StringUtil.compare(pathOrReference,loc.pathOrReference); 
//        if (val!=0)
//            return val;
//        
//        // case sensitive query:
//        val=StringUtil.compare(query,loc.query);
//        if (val!=0)
//           return val;
//
//        val=StringUtil.compare(fragment,loc.fragment);
//        if (val!=0)
//           return val;
//
//        return 0; 
//    }
//
//    /**
//     * Returns array of path elements. 
//     * For example "/dev/zero" = {"dev","zero"}
//     */ 
//    public String[] getPathElements()
//    {
//        String path=getPath();
//        
//        if (path==null)
//            return null; 
//        
//        // strip starting '/' to avoid empty path as first path element
//        if (path.startsWith(URIFactory.SEP_CHAR_STR))
//            path=path.substring(1); 
//        
//        return path.split(URIFactory.SEP_CHAR_STR); 
//    }
//
//    /**
//     * Compares host to empty hostname, localhost, and actual fully qualified hostname. 
//     * Don not rely on this method. Use actual network interface. This is just an indication. 
//     */
//    public boolean isLocalLocation()
//    {
//        String host=this.getHostname(); 
//        if (StringUtil.isEmpty(host))
//            return true; 
//    
//        if (StringUtil.compare(host,"localhost",true)==0) 
//            return true; 
//
//        // ipv4:
//        if (StringUtil.compare(host,"127.0.0.1",true)==0) 
//            return true; 
//        
//        // ipv6:
//        if (StringUtil.compare(host,"::1",true)==0) 
//            return true; 
//        
//        if (StringUtil.compare(host,GlobalProperties.getHostname(),true)==0) 
//            return true; 
//
//        return false;
//    }
//   
//    /** Returns true if this VRL has a non empty fragment part ('?...') in it. */ 
//    public boolean hasQuery()
//    {
//        return (StringUtil.isEmpty(getQuery())==false); 
//    }
//
//    /** Returns true if this VRL has a non empty fragment part ('#...') in it. */ 
//    public boolean hasFragment()
//    {
//        return (StringUtil.isEmpty(getFragment())==false); 
//    }
//   
//
//}
//
