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

package nl.esciencecenter.ptk;

import java.net.InetAddress;
import java.util.Properties;

import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

/** 
 * Global properties and other runtime configurations. 
 */
public class Global
{
	/** Java property name for architecture: For example: "i386" or "i586" */ 
    public static final String PROP_JAVA_OS_ARCH = "os.arch";

    /** Java property name for Operation System name "Linux" or "Windows" */ 
    public static final String PROP_JAVA_OS_NAME = "os.name";

    /** Java property name for OS Version" */ 
    public static final String PROP_JAVA_OS_VERSION = "os.version";

    /** Java temporary directory */ 
	public static final String PROP_JAVA_TMPDIR = "java.io.tmpdir";
	
	/** Java property which specified the user home or $HOME.*/ 
	public static final String PROP_JAVA_USER_HOME = "user.home";
	
	/** Java property which specifies the user name or $USER.*/ 
	public static final String PROP_JAVA_USER_NAME = "user.name"; 

    /** Java os.name value for Linux */ 
    public static final String LINUX = "Linux";

    /** Java os.name value for Windows */ 
    public static final String WINDOWS= "Windows";

    /** Java os.name value for Mac OS */ 
    public static final String MAC_OS= "Mac OS";
    
    /** Java os.name value for Mac OS X */ 
    public static final String MAC_OSX= "Mac OS X";
    
    private static ClassLogger logger;
    
    // extra non system properties:
    private static Properties globalProperties=new Properties(); 

    static
    {
    	// ---
    	// static init! Careful here not to make static references to other
        // potentiele not initialized classes
    	// ---

        // Default logger. 
    	logger=ClassLogger.getLogger(Global.class); 
    	logger.infoPrintf(">>> Global Init <<<\n"); 
    }

    public static void init()
    {
        // Dummy init. Initializes Class. See static{...}
    }

	public static Object getProperty(String name)
	{
		Object prop=globalProperties.getProperty(name); 
		if (prop!=null)
			return prop; 
		
		prop=System.getProperty(name);
		return prop; 
	}

	/** Auto cast Property to String value */ 
	public static String getStringProperty(String name)
	{
	    Object prop=getProperty(name); 
	    if (prop==null)
	        return null; 
	    return prop.toString();  
    }

	public static Object getEnv(String name)
	{
		Object envval=System.getenv(name);
		return envval; 
	}
	
	public static String getGlobalUserName() 
	{
	    return getStringProperty(PROP_JAVA_USER_NAME); 
	}
	
	public static String getGlobalUserHome() 
	{
	    String val=getStringProperty(PROP_JAVA_USER_HOME); 

		if (val==null)
			return null; 
		
		return VRI.uripath(val,true); 
	}
	
    public static VRI getGlobalUserHomeVRI()
    {
        return new VRI("file",null,0,getGlobalUserHome()); 
    }
    
	public static String getGlobalTempDir() 
	{
	    String val=getStringProperty(PROP_JAVA_TMPDIR); 

		if (val==null)
			return null;  
		
		return VRI.uripath(val.toString(),true); 
	}
	
	/**
     * Returns fully qualified hostname or 'localhost' if hostname can't be determined. 
     */ 
    public static String getHostname()
    {
        try
        {
            String hostname = InetAddress.getLocalHost().getCanonicalHostName();
            return hostname;
        }
        catch (Exception ex)
        {
            logger.logException(ClassLogger.WARN,ex,"Couldn't get HOSTNAME of local machine\n");  
        }

        Object val=getEnv("HOSTNAME");
        
        if ((val!=null) && (val.equals("")==false))
            return val.toString();   

        return "localhost"; 
    }
    
    public static ClassLogger getRootLogger()
    {
    	return ClassLogger.getRootLogger(); 
    }

    public static String getOsArch() 
    {
        return getStringProperty(PROP_JAVA_OS_ARCH); 
    }
    
    public static String getOsName() 
    {
        return getStringProperty(PROP_JAVA_OS_NAME); 
    }
    
    public static String getOsVersion() 
    {
        return getStringProperty(PROP_JAVA_OS_VERSION); 
    }

    public static boolean isWindows()
    {
      return StringUtil.equalsIgnoreCase(Global.getOsName(),WINDOWS);
    }

    public static boolean isLinux()
    {
      return StringUtil.equalsIgnoreCase(Global.getOsName(),LINUX);
    }
    
    /** Explicit Mac Os <strong>X</strong> */ 
    public static boolean isMacOSX()
    {
      return StringUtil.equalsIgnoreCase(Global.getOsName(),MAC_OSX);
    }
    
    /** Explicit Mac Os, but <strong>not</strong> X */    
    public static boolean isMacOS()
    {
      return StringUtil.equalsIgnoreCase(Global.getOsName(),MAC_OS);
    }
    
    /** is MacOS or MacOS X */ 
    public static boolean isMac()
    {
    	return (isMacOS() || isMacOSX()); 
    }
    
    public static String getJavaVersion()
    {
        return getStringProperty("java.version");
    }

    public static String getJavaHome()
    {
        return getStringProperty("java.home");
    }

}
