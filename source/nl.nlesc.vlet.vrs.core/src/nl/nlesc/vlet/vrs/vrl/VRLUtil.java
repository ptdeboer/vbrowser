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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.data.VAttribute;
import nl.nlesc.vlet.vrs.data.VAttributeSet;

public class VRLUtil
{
    // hostname cache:
    static Hashtable<String, String> hostnames = new Hashtable<String, String>();

    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(VRLUtil.class);
        // logger.setLevelToDebug();
    }

    /**
     * Get fully qualified and <strong>resolved</strong> hostname.
     * <p>
     * This method will return the resulting hostname by means of reverse DNS
     * lookup. If this hostname is an alias for another hostname, which could be
     * on a complete other domain, the resulting hostname will be returned !
     * <p>
     * For example "my.internetdomain.org" might result in te providers domain
     * name "userXYS.provider.com".
     */
    public static String resolveHostname(String name)
    {
        try
        {
            return reverseDNSlookup(name);
        }
        catch (UnknownHostException e)
        {
            logger.warnPrintf("Warning: Unknown host:%s\n", name);
            return name;
        }
    }

    /**
     * Returns fully qualified hostname by means of reverse DNS lookup
     */
    public static String reverseDNSlookup(String name) throws UnknownHostException
    {
        // update
        name = resolveLocalHostname(name);
        String newname = null;

        if (name == null)
        {
            logger.warnPrintf("reverseDNSlookup(): NULL Hostname!\n");
            return null;
        }

        // check cache:
        synchronized (hostnames)
        {
            if (name != null)
                if ((newname = hostnames.get(name)) != null)
                {
                    logger.debugPrintf("reverseDNSlookup(): (I) cached '%s' => '%s'\n", name, newname);
                    return newname;
                }
        }

        // java.net.InetAddress.getLocalHost().getCanonicalHostName();

        InetAddress ipaddr;

        // try
        // {
        ipaddr = java.net.InetAddress.getByName(name);
        // }
        // catch (UnknownHostException e)
        // {
        // Global.errorPrintln("VRL","Exception:"+e);
        // e.printStackTrace(Global.getDebugStream());
        // / return name; // return name as is.
        // }

        newname = ipaddr.getCanonicalHostName();

        synchronized (hostnames)
        {
            hostnames.put(name, newname);
        }

        logger.debugPrintf("reverseDNSlookup(): (II) Put new '%s' => '%s'\n", name, newname);

        return newname;
    }

    /**
     * Returns true if and only true if both hostnames point to the same
     * physical host. 
     * For example HOST equivelant with HOST.DOMAIN and/or raw
     * IP-adresses ! Allows for null and empty hostnames.
     */

    public static boolean hostnamesAreEquivelant(String host1, String host2)
    {
        if (host1 == null)
            host1 = "";

        if (host2 == null)
            host2 = "";

        // check unresolved hostsname (speedup aliasing)
        if (host1.compareTo(host2) == 0)
            return true;

        // now check unresolved hostnames
        host1 = resolveHostname(host1);
        host2 = resolveHostname(host2);

        // check resolved hostnames
        if (host1.compareTo(host2) == 0)
            return true;

        return false;
    }

    /**
     * Check for empty or localhost names aliases and return 'localhost'.
     */
    public static String resolveLocalHostname(String hostname)
    {
        if ((hostname == null) || (hostname.compareTo("") == 0))
            return VRS.LOCALHOST;

        if (hostname.compareToIgnoreCase(VRS.LOCALHOST) == 0)
            return VRS.LOCALHOST;

        // support local loop device:
        if (hostname.compareTo("127.0.0.1") == 0)
            return VRS.LOCALHOST;

        return hostname;
    }

    public static boolean hasSameServer(VRL vrl1, VRL vrl2)
    {
        if (vrl2 == null)
            return false;

        logger.debugPrintf("hasSameServer(): '%s' <==> '%s'\n", vrl1, vrl2);

        String scheme = vrl1.getScheme();
        String scheme2 = vrl2.getScheme();

        if (scheme.compareToIgnoreCase(scheme2) != 0)
        {
            // check normalized scheme:
            scheme = VRS.getDefaultScheme(scheme);
            scheme2 = VRS.getDefaultScheme(scheme2);

            // check normalized schemes
            if (scheme.compareToIgnoreCase(scheme2) != 0)
                return false;
        }

        String hostname = vrl1.getHostname();
        String host2 = vrl2.getHostname();

        // check hostname
        if (StringUtil.compareIgnoreCase(hostname, host2) != 0)
            return false;

        int port = vrl1.getPort();
        int port2 = vrl2.getPort();

        if (port <= 0)
            port = VRS.getSchemeDefaultPort(scheme);

        if (port2 <= 0)
            port2 = VRS.getSchemeDefaultPort(scheme2);

        // check port
        if (port != port2)
            return false;

        logger.debugPrintf("hasSameServer: TRUE for: '%s' <==> '%s'\n", vrl1, vrl2);

        // should be on the same server !
        return true;
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
    public static String isSubPath(String parentPath, String childPath)
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

    public static VAttributeSet getQueryAttributes(VRL vrl)
    {
       String qstr=vrl.getQuery();
    
       // no query 
       if (qstr==null) 
           return null;
    
       // split in '&' parts 
       String stats[]=vrl.getQueryParts();        
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

}
