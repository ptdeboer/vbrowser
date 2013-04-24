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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.data.VAttributeSet;
import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;


/**
 *
 */
public final class VRL extends VRI // implements Comparable<VRL>
{
    private static final long serialVersionUID = -3255450059796404575L;
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(VRL.class);
        //logger.setLevelToDebug();
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
        parentPath=uripath(parentPath);
        
        childPath=uripath(childPath); 
        
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
    
    // ========================================================================
    // instance 
    // ========================================================================

    protected VRL()
    {
    }
    
    public VRL(String uristr) throws VRLSyntaxException
    {
        try
        {
            init(uristr);
        }
        catch (VRISyntaxException e)
        {
          throw new VRLSyntaxException(e);
        }
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
    
    public VRL(VRI vri)
    {
       super(vri); 
    }

    public VRL duplicate()
    {
        VRL loc = new VRL();
        loc.copyFrom(this); 
        return loc; 
    }
    
    public VRL(String scheme, String host, String path)
    {
        super(scheme,null,host,-1,path, null,null); 
    }

    public VRL(String scheme, String host,int port, String path)
    {
        super(scheme,null,host,port,path, null,null); 
    }

    public VRL(String scheme, String userinfo,String host,String path)
    {
        super(scheme,userinfo,host,-1,path,null,null); 
    }

    public VRL(String scheme,String userInfo, String host,int port, String path)
    {
        super(scheme,userInfo,host,port,path, null,null); 
    }

    public VRL(String scheme,
            String userInfo, 
            String hostname, 
            int port,
            String path,
            String query,
            String fragment)
    {
        super(scheme,userInfo,hostname,port,path,query,fragment); 
    }

    // VRS interface: 

    public VAttributeSet getQueryAttributes()
    {
       String qstr=getQuery();
       
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
      
    public VRL getParent()
    {
        return new VRL(super.getParent());
    }
    
    public VRL resolve(String path) throws VRISyntaxException
    {
        return new VRL(super.resolve(path)); 
    }
   
    public VRL resolvePath(String path) throws VRISyntaxException 
    {
        return new VRL(super.resolvePath(path)); 
    }
    
    public VRL appendPath(String path)
    {
        return new VRL(super.appendPath(path)); 
    }

    public VRL replacePath(String path)
    {
        return new VRL(super.replacePath(path)); 
    }
    
    

    public boolean isVLink()
    {
        return this.hasExtension("vlink",false); 
    }


    public VRI toVRI()
    {
        // upcast: 
        return (VRI)this; 
    }

    public boolean isRootPath()
    {
        // normalize path: 
        String upath=uripath(this.getPath());
        //Debug("isRootPath(): uri path="+upath); 
        
        if (StringUtil.isEmpty(upath))
            return true; 
        
        // "/"
        if (upath.compareTo(SEP_CHAR_STR)==0) 
            return true; 
        
        // uripath normalized windosh root "/X:/" 
        if (upath.length()==4)
            if ((upath.charAt(0)==SEP_CHAR) && (upath.substring(2,4).compareTo(":/")==0))
                return true; 
        
        return false; 
    }
    
    // alternate method to cast Exception to VRLSyntaxException
    public VRL resolveToVRL(VRL relLoc) throws VRLSyntaxException
    {
       try
       {
           VRI vri=resolve(relLoc);
           return new VRL(vri);
       }
       catch (VRISyntaxException e)
       {
           throw new VRLSyntaxException(e);
       } 
       
    }
    
    // alternate method to cast Exception to VRLSyntaxException
    public VRL resolveToVRL(String relLoc) throws VRLSyntaxException
    {
       try
       {
           VRI vri=resolve(relLoc);
           VRL vrl=new VRL(vri);
           logger.debugPrintf("resolveToVRL():%s+%s=>%s\n",this,relLoc,vrl); 
           return vrl;
       }
       catch (VRISyntaxException e)
       {
           throw new VRLSyntaxException(e);
       } 
       
    }
    
    // alternate method to cast Exception to VRLSyntaxException
    public VRL resolvePathToVRL(String path) throws VRLSyntaxException
    {
       try
       {
           VRI vri=resolvePath(path);
           VRL vrl=new VRL(vri);
           logger.debugPrintf("resolvePathToVRL() '%s'+'%s'=>'%s'\n",this,path,vrl,vri); 
           return vrl; 
       }
       catch (VRISyntaxException e)
       {
           throw new VRLSyntaxException(e);
       } 
       
    }

    public VRL appendStringToVRL(String relPath) throws VRLSyntaxException
    {
        try
        {
            VRL vrl=new VRL(this.append(relPath)); 
            return vrl; 
        }
        catch (VRISyntaxException e)
        {
            throw new VRLSyntaxException(e);
        } 
    }
    
    public VRL copyWithNewPathToVRL(String relPath)
    {
       return new VRL(this.replacePath(relPath));
    }
    
    /** Duplicate VRL but create new Query */
    public VRL copyWithNewQuery(String str)
    {
        VRL vrl=this.duplicate();
        vrl.setQuery(str);
        return vrl; 
    }

    
    public VRL copyWithNewPort(int val)
    {
        VRL loc=duplicate(); 
        loc.setPort(val); 
       
        return loc; 
    }
    
    /**
     * Append Path, addes URI Seperator char "/" between path elements.  
     * Used append() for plain string appends or this one for filesystems paths. 
     */ 
    public VRL appendPathToVRL(String path)
    {
        return new VRL(this.appendPath(path)); 
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
            
            if ((subPath.length()>pathStr.length()) && (subPath.charAt(pathStr.length())==SEP_CHAR)) 
                return true; 
        }
        
        return false; 
    }

    public static VRL createDosVRL(String vrlstr) throws VRLSyntaxException
    {
        String newStr=vrlstr.replace('\\','/'); 
        // constructor might change ! 
        return new VRL(newStr); 
    }

    public VRL resolvePathToVRL(VRL relvrl) throws VRLSyntaxException
    {
        // ambigious: 
        if (relvrl.isAbsolute())
            return relvrl;
        else 
            return resolvePathToVRL(relvrl.getPath()); 
        
    }

 
   
    
}
