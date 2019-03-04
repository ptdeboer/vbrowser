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

package nl.esciencecenter.vlet;

// IMPORTANT: Keep Import footprint SMALL to avoid chicken and egg
// problems at class initialization. 
// import only 'core' classes which do not refer back to VletConfig!
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import nl.esciencecenter.ptk.GlobalProperties;

import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.exception.ResourceReadAccessDeniedException;
import nl.esciencecenter.vlet.vrs.VRS;

/**
 * The Global VletConfig class holds (static) global configuration settings. The
 * configuration settings are checked in following order:<br>
 * <ul>
 * <li>1) The installation settings from VLET_INSTALL/etc/vletrc.prop
 * <li>2) The environment variables using System.getenv()
 * <li>3) Optional user settings from ~/.vletrc/vletrc.prop
 * <li>4) System property using System.getProperty()
 * </ul>
 * Where the last one matched will be used (each next configuration 'overrides'
 * the previous).
 * <p>
 * Important VletConfig methods which must be specified as
 * <strong>first</strong> statements in an application before using the VRS are:
 * <ul>
 * <li> {@link #setBaseLocation(String)} Alternative VLET_INSTALL or 'code base'
 * location.
 * <li> {@link #setIsService(boolean)} whether runtime environment is a service
 * environment.
 * <li> {@link #setIsApplet(boolean)} whether runtime environment is an applet
 * environment (broken).
 * <li> {@link #setInitURLStreamFactory(boolean)} whether to initialize the
 * URLStreamFactory.
 * <li> {@link #setUsePersistantUserConfiguration(boolean)} whether to read from
 * and save automatically to ~/.vletrc/vletrc.prop when specifying user
 * properties.
 * </ul>
 * <b>Important</b>:<br>
 * This class is outside Global and does NOT refer to it, so that
 * pre-initialization configuration can be done (in the case of applet or
 * service startup).
 * <p>
 * 
 * @author P.T. de Boer
 */
public class VletConfig
{
    /** Java property name for architecture, for example: "i386" or "i586". */
    public static final String JAVA_OS_ARCH = "os.arch";

    /** Java property name for Operation System name "Linux" or "Windows". */
    public static final String JAVA_OS_NAME = "os.name";

    /** Java property name for OS Version. */
    public static final String JAVA_OS_VERSION = "os.version";

    /** Java os.name value for Linux. */
    public static final String JAVA_OS_LINUX = "Linux";

    /** Java os.name value for Windows. */
    public static final String JAVA_OS_WINDOWS = "Windows";

    /** Java temporary directory. */
    public static final String JAVA_TMPDIR = "java.io.tmpdir";

    /** Java property which specified the user home or $HOME. */
    public static final String JAVA_USER_HOME = "user.home";

    /** Java property which specifies the user name or $USER. */
    public static final String JAVA_USER_NAME = "user.name";

    // ======================================================================
    // Environment variables
    // ======================================================================

    /**
     * Environment Variable "VLET_INSTALL" which points to the VLET
     * Distribution.
     */
    public static final String ENV_VLET_INSTALL = "VLET_INSTALL";

    /**
     * Environment Variable "X509_USER_PROXY" which points to the user proxy
     * file on some grid systems.
     */
    public static final String ENV_X509_USER_PROXY = "X509_USER_PROXY";

    /** Globus TCP portrange environment variabele for incoming ports. */
    public static final String ENV_GLOBUS_TCP_PORT_RANGE = "GLOBUS_TCP_PORT_RANGE";

    // ======================================================================
    // VLET Global properties
    // ======================================================================

    /**
     * Global property "firewall.incoming.portrange" wich specifies the VLET
     * allowed firewall portrange for incoming ports.
     */
    public static final String PROP_INCOMING_FIREWALL_PORT_RANGE = "firewall.incoming.portrange";

    /** Global property "vlet.version" which holds the VLET Version. */
    public static final String PROP_VLET_VERSION = "vlet.version";

    /**
     * Global Property which determines whether to skip the floppy seek at
     * startup
     */
    public static final String PROP_SKIP_FLOPPY_SCAN = "vlet.skipFloppyScan";

    /**
     * Global property which determines whether ALL communication should be done
     * in 'passive' mode. The default is "true" .
     */
    public static final String PROP_PASSIVE_MODE = "vlet.passivemode";

    public static final String ATTR_PASSIVE_MODE = "passiveMode";

    /** Global property which specifies the grid proxy location. */
    public static final String PROP_GRID_PROXY_LOCATION = "grid.proxy.location";

    /**
     * Global property which specifies the directory of the grid certificate(s).
     */
    public static final String PROP_GRID_CERTIFICATE_LOCATION = "grid.certificate.location";

    /** Comma seperated list of custom locations for (root) ca certificates. */
    public static final String PROP_GRID_CA_CERTIFICATE_LOCATIONS = "grid.ca.certificate.locations";

    /**
     * Comma separated list of VRSFactory (VDriver) classes to be initialized.
     * Classes specified must be subclass of nl.uva.vlet.vrs.VRSFactory.
     * 
     * @see nl.esciencecenter.vlet.vrs.VRSFactory
     */
    public static final String PROP_VDRIVERS = "vlet.vrs.vdrivers";

    /**
     * Whether to initialize the default (core) VRS/VFS Drivers, default = true.
     */
    public static final String PROP_INIT_DEFAULT_VDRIVERS = "vlet.vrs.initDefaultVDrivers";

    /** Default grid proxy lifetime. */
    public static final String PROP_GRID_PROXY_LIFETIME = "grid.proxy.lifetime";

    /** Whether to enable VOMS proxies. */
    public static final String PROP_GRID_PROXY_ENABLE_VOMS = "grid.proxy.enableVoms";

    /** Default VO name to use when creating VOMS proxies. */
    public static final String PROP_GRID_PROXY_VO_NAME = "grid.proxy.voName";

    /** Default VO role. */
    public static final String PROP_GRID_PROXY_VO_ROLE = "grid.proxy.voRole";

    /** Global property "vlet.install". Is configured by the boostrapper. */
    public static final String PROP_VLET_INSTALL = "vlet.install";

    /**
     * Global property "vlet.install.libdir" which points to the
     * VLET_INSTALL/lib directory. Is configured by the boostrapper. Can point
     * to alternative "lib" directory
     */
    public static final String PROP_VLET_LIBDIR = "vlet.install.libdir";

    /**
     * Global property "vlet.install.bindir" which points to the
     * VLET_INSTALL/bin directory. Is configured by the boostrapper. Can point
     * to alternative "bin" directory
     */
    public static final String PROP_VLET_BINDIR = "vlet.install.bindir";

    /**
     * Global property "vlet.install.docdir" which points to the
     * VLET_INSTALL/doc directory. Is configured by the boostrapper.Can point to
     * alternative "doc" directory
     */
    public static final String PROP_VLET_DOCDIR = "vlet.install.docdir";

    /**
     * Global property "vlet.install.sysconfdir" which points to the
     * VLET_INSTALL/etc directory. Is configured by the boostrapper.Can point to
     * alternative "etc" directory
     */
    public static final String PROP_VLET_SYSCONFDIR = "vlet.install.sysconfdir";

    /**
     * Global property which determines whether (global) user configurations
     * should be read from and saved to: ~/.vletrc/vletrc.prop.
     */
    public static final String PROP_PERSISTANT_USER_CONFIGURATION = "vlet.persistantUserConfiguration";

    /**
     * Global Property which specifies whether the URLStreamFactory should be
     * initiliazed.
     */
    public static final String PROP_VLET_SET_VRS_URL_FACTORY = "vlet.vrs.setURLfactory";

    /**
     * The default BDII hostname to use. <br>
     * <b>Note</b> As of vlet 1.4 this value can contain a list of hostnames
     * include the port number as follows:
     * "bdii.grid.sara.nl:2710,bdii2.grid.sara.nl:2170"
     */
    public static final String PROP_BDII_HOSTNAME = "bdii.hostname";

    /** The default BDII port to use. Default value is 2170 */
    public static final String PROP_BDII_PORT = "bdii.port";

    /** Comma separated list of User Configured VOs. */
    public static final String PROP_USER_CONFIGURED_VOS = "user.configuredVOs";

    /** Global property which specifies whether user interaction is allowed. */
    public static final String PROP_ALLOW_USER_INTERACTION = "vlet.user.allowInteraction";

    /** Default userspace configuration subdirectory ".vletrc" under HOME. */
    public static final String USER_VLETRC_DIRNAME = ".vletrc";

    /** Default "certificates" subdirectory. */
    public static final String CERTIFICATES_SUBDIRNAME = "certificates";

    /** VLETRC configuration properties file "vletrc.prop". */
    public static final String VLETRC_PROP_FILENAME = "vletrc.prop";

    /**
     * Plug-in sub directory "plugins". Appended to library directory AND and
     * user configuration directory: $HOME/.vletrc/ Default plugin directories
     * are VLET_INSTALL/lib/plugins and ~/.vletrc/plugins.
     */
    public static final String PLUGIN_SUBDIR = "plugins";

    public static final String VIEWERS_SUBDIR = "viewers";

    /**
     * Glite BDII or InfoSys environment variable. Contains BDII contact
     * information.
     */
    public static final String ENV_LCG_GFAL_INFOSYS = "LCG_GFAL_INFOSYS";

    /**
     * Default TCP connections setup time for some protocols. Not all protocols
     * honor this setting. This is work in progress.
     */
    public static final String TCP_CONNECTION_TIMEOUT = "tcp.connection.timeout";

    /**
     * Default server call timeout. This is different than the tcp connection
     * timeout since this property specifies the wait time <emph>after</emph>
     * the TCP setup already has been done and the client is waiting for a
     * response from for example a web service.
     */
    public static final String SERVER_REQUEST_TIMEOUT = "vlet.server.request.timeout";

    /** Experimental HTTP proxy settings. Under construction */
    public static final String HTTP_PROXY_ENABLED = "http.proxy.enable";

    /** HTTP proxy port property. Under construction. */
    public static final String HTTP_PROXY_PORT = "http.proxy.port";

    /** HTTP proxy host property. Under construction. */
    public static final String HTTP_PROXY_HOST = "http.proxy.host";

    /** HTTPS proxy port property. Under construction. */
    public static final String HTTPS_PROXY_PORT = "https.proxy.port";

    /** HTTPS proxy port property. Under contruction. */
    public static final String HTTPS_PROXY_HOST = "https.proxy.host";

    /**
     * In cog-jglobus 1.4 this string isn't defined. Define it here to stay 1.4
     * compatible. This property is defined in CoG jGlobus 1.7 and higher.
     */
    public static final String COG_ENFORCE_SIGNING_POLICY = "java.security.gsi.signing.policy";

    /** PKCS11 Model property (not used). */
    public static final String PKCS11_MODEL = "org.globus.tools.proxy.PKCS11GridProxyModel";

    /** Default system wide grid certificates directory. */
    public static final String DEFAULT_SYSTEM_CERTIFICATES_DIR = "/etc/grid-security/certificates";

    /** Private classlogger. */
    private static ClassLogger logger = null;

    static
    {
        // this should be the ONLY dependency from VletConfig !
        logger = ClassLogger.getLogger(VletConfig.class);
    }

    public static void init()
    {
        ; // see above.
    }

    // ======================================================== //
    // Global config settings: package protected
    // so that other Global.* classes can access them directly.
    // ======================================================== //

    /**
     * vletrc.prop file from VLET_INSTALL/etc/vletrc.prop File will be loaded
     * from classpath wich should have VLET_INSTALL/etc on the classpath.
     */
    protected static Properties vletrcProperties;

    /** Base installation VRL. */
    protected static VRL baseInstallationVRL = null;

    /** Whether current application is an Applet. */
    protected static boolean isApplet = false;

    /** Shadow copy of 'System' properties for server/applet environments. */
    protected static Properties configProperties = new Properties();

    /** User Home Location. */
    protected static VRL userHomeLocation = null;

    /**
     * Whether inbound tcp traffic is allowed. passiveMode==true => no active
     * incoming traffic allowed !
     */
    protected static boolean passiveModeOnly = false;

    /** Whether current application is an (web/grid) service */
    protected static boolean isService = false;

    /** For debugging purposes. */
    protected static boolean globalIsInitialized = false;

    // protected static boolean isStrictService=false;

    /** Whether an UI has been registered/is present. */
    protected static boolean hasUI = false;

    // ======================================================================
    // Pre Configuration Setters !!!
    // ======================================================================

    public static String[] parseArguments(String[] args)
    {
        if (args == null)
            return null;

        List<String> remainingArgs = new ArrayList<String>();

        for (String arg : args)
        {
            // infoPrintf(Global.class, "Checking arg:%s\n",arg);

            boolean parsed = false;

            if (arg.startsWith("-D"))
            {
                // debugPrintln("Global","arg="+arg);

                // property argument
                String propDef = arg.substring(2);
                String strs[] = propDef.split("[ ]*=[ ]*");

                if ((strs != null) && (strs.length > 1) && (strs[0] != null))
                {
                    System.setProperty(strs[0], strs[1]);
                    parsed = true;
                }
                else
                {
                    // Global.errorPrintf(Global.class,
                    // "Couldn't parse property:%s\n",arg);
                }
            }
            else if (arg.equalsIgnoreCase("-debug"))
            {
                logger.setLevelToDebug();
                ClassLogger.getRootLogger().setLevelToDebug();
                parsed = true;
            }
            else if (arg.equalsIgnoreCase("-info"))
            {
                logger.setLevelToInfo();
                ClassLogger.getRootLogger().setLevelToInfo();
                parsed = true;
            }
            else if (arg.equalsIgnoreCase("-warn"))
            {
                logger.setLevelToWarn();
                ClassLogger.getRootLogger().setLevelToWarn();
                parsed = true;
            }
            else if (arg.equalsIgnoreCase("-error"))
            {
                logger.setLevelToWarn();
                ClassLogger.getRootLogger().setLevelToError();
                parsed = true;
            }
            else if (arg.equalsIgnoreCase("-fatal"))
            {
                logger.setLevelToWarn();
                ClassLogger.getRootLogger().setLevelToFatal();
                parsed = true;
            }
            if (parsed == false)
                remainingArgs.add(arg);
        }

        String newArgs[] = new String[remainingArgs.size()];
        newArgs = remainingArgs.toArray(newArgs);
        return newArgs;
    }

    /** <b>Pre Global Init</b>:Set Applet environment */
    public static void setIsApplet(boolean value)
    {
        isApplet = value;
    }

    /** <b>Pre Global Init</b>:Set Service environment */
    public static void setIsService(boolean value)
    {
        isService = value;
    }

    /**
     * VletConfig's alternative to System.setProperty(). 
     * Not all security manager allow System.get/setProperty() calls.
     * Use VletConfig.get/setProperty which are applet and service safe. If the
     * environment is an applet VletConfig will use a private property instance
     * to put/get global properties.
     */
    public static void setSystemProperty(String key, String value)
    {
        if ((key == null) || (value == null))
            return;

        // not allowed inside applet:
        if (isApplet == false)
            System.setProperty(key, value);

        // keep property in private instance as well.
        configProperties.setProperty(key, value);
    }

    /**
     * Initialize Global properties using a custom property set. Method iterates
     * over all properties and stores them in the system properties.
     */
    public static void initProperties(Properties globalProperties)
    {
        Set<Object> keys = globalProperties.keySet();
        Object keyarr[] = new Object[keys.size()];
        keyarr = keys.toArray(keyarr);

        for (Object key : keyarr)
        {
            Object val = globalProperties.get(key);
            if (val != null)
                setSystemProperty(key.toString(), val.toString());
        }
    }

    /** Alternative for System.getenv() when running as an applet. */
    public static String getSystemEnv(String name)
    {
        if (isApplet == false)
            return System.getenv(name);

        return configProperties.getProperty(name);
    }

    /**
     * <b>pre global</b>:Specify code base for example when starting in applet
     * mode.<br>
     * This is used as alternative for "VLET_INSTALL".
     */
    public static void setBaseLocation(URL codeBase)
    {
        try
        {
            baseInstallationVRL = new VRL(codeBase);
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR, e, "Exception when settingCodeBase to %s\n", codeBase);
        }
    }

    /**
     * <b>Pre Global Init</b>: Specify HOME location
     * 
     * @throws VRISyntaxException
     */
    public static void setUserHomeLocation(URL url) throws VRLSyntaxException
    {
        // assertNotInitialized();
        // if (globalIsInitialized==true)
        // throw new
        // VletConfigurationException("Cannot set User Home after Global has been initialized");

        userHomeLocation = new VRL(url);
    }

    /** <b>Pre Global Init</b>: Set whether there is a UI */
    public static void setHasUI(boolean val)
    {
        VletConfig.hasUI = val;
    }

    /**
     * <b>Pre Global init</b>: Set whether user interaction is allowed.
     * 
     * @see VletConfig#PROP_ALLOW_USER_INTERACTION
     */
    public static void setAllowUserInteraction(boolean val)
    {
        VletConfig.setSystemProperty(VletConfig.PROP_ALLOW_USER_INTERACTION, "" + val);
    }

    /**
     * <b>Pre Global init</b>: Set whether one global user configuration can be
     * used. This controls whether user settings will be read from:
     * ~/.vletrc/vletrc.prop. Also wether user settings will be saved when
     * specifyed. Set to false in a server environment !
     * 
     * @see VletConfig#PROP_PERSISTANT_USER_CONFIGURATION
     */
    public static void setUsePersistantUserConfiguration(boolean val)
    {
        VletConfig.setSystemProperty(VletConfig.PROP_PERSISTANT_USER_CONFIGURATION, "" + val);
    }

    /**
     * Specify whether Global Passive mode is enabled. If true no incoming
     * connections are allowed (or possible).
     */
    public static void setPassiveMode(boolean val)
    {
        // System property= current 'runtime' properties.
        setSystemProperty(PROP_PASSIVE_MODE, "" + val);
    }

    /**
     * <b>Pre Global init</b>: Set whether to initialize the URLStreamFactory or
     * not. This overides the System Property "vlet.vrs.setURLfactory" ! Set to
     * false when running inside for example a web server.
     */
    public static void setInitURLStreamFactory(boolean val)
    {
        // System property= current 'runtime' properties.
        setSystemProperty(VletConfig.PROP_VLET_SET_VRS_URL_FACTORY, "" + val);
    }

    /**
     * Whether to initialize the URLStreamFactory.
     */
    public static boolean getInitURLStreamFactory()
    {
        // Never not allowed inside applet !

        if (isApplet == true)
            return false;

        // Preferably disable inside service, but could be allowed.
        boolean defaultValue = (isService() == false);

        return getBoolProperty(VletConfig.PROP_VLET_SET_VRS_URL_FACTORY, defaultValue);
    }

    // ======================================================================
    // Configuration Getters (Can be used post initialization).
    // ======================================================================

    /** Returns true if the environment is an applet environment. */
    public static boolean isApplet()
    {
        return isApplet;
    }

    /** Returns true if the environment is a service environment. */
    public static boolean isService()
    {
        return isService;
    }

    /**
     * Whether User interaction is allowed. For example windows dialogs and
     * other pop-ups.
     */
    public static boolean getGlobalAllowUserInteraction()
    {
        String boolstr = VletConfig.getProperty(VletConfig.PROP_ALLOW_USER_INTERACTION);

        if (boolstr == null)
            return true;
        try
        {
            return Boolean.parseBoolean(boolstr);
        }
        catch (Exception e)
        {
            logger.warnPrintf("***Exception:%s\n" + e);
        }
        return true;
    }

    /**
     * Alternative for System.getProperty() when running as an applet. The
     * methods setSystemProperty() and getSystemProperty() can be used as applet
     * safe versions of System.getProperty() and System.setProperty(). It is
     * recommend all application use VletConfig versions.
     */
    public static String getSystemProperty(String key)
    {
        String val = null;

        // not allowed inside applet:
        if (isApplet == false)
            val = System.getProperty(key);

        if ((val == null) && (configProperties != null))
            val = configProperties.getProperty(key);

        return val;
    }

    /**
     * Same as getSystemProperty but optionally returns default value if not
     * set.
     */
    public static String getSystemProperty(String key, String defaultValue)
    {
        String val = getSystemProperty(key);

        if ((val == null) || StringUtil.isEmpty(val))
            return defaultValue;

        return val;
    }

    /**
     * Returns (code) base location of 'this' class or Applet URL. Might not be
     * the actual installation location but the 'logical' or web based location.
     */
    public static VRL getBaseLocation()
    {
        if (baseInstallationVRL != null)
        {
            return baseInstallationVRL;
        }

        if (isApplet == false)
        {
            URL url = VletConfig.class.getProtectionDomain().getCodeSource().getLocation();
            try
            {
                baseInstallationVRL = new VRL(url);
            }
            catch (Exception e)
            {
                logger.logException(ClassLogger.ERROR, e, "Error creating Base URL:%s\n", url);
            }
        }

        return baseInstallationVRL;
    }

    /**
     * Return location of VLET_INSTALL/etc directory. This value is configured
     * by the bootstrapper!
     */
    public static VRL getInstallationConfigDir()
    {
        if (isApplet == true)
        {
            return baseInstallationVRL;
        }

        // To avoid chicken'n Egg problems: MUST be specified
        // by bootstrapper as System property !
        String sysconfdir = VletConfig.getSystemProperty(PROP_VLET_SYSCONFDIR);

        if (sysconfdir != null)
            return new VRL("file", null, sysconfdir); // scheme,host,path

        // return VLET_INSTALL/etc
        return new VRL(getInstallBaseDir().appendPath("etc"));
    }

    /**
     * Get installation directory. Returns value of VLET_INSTALL if configured.
     * If not is will figure out from the classpath of this class.
     */
    public static VRL getInstallBaseDir()
    {
        // VLET_INSTALL should point to this installation

        VRL vrl = null;
        String path = null;

        try
        {
            // 1st: -Dvlet.install=
            path = VletConfig.getSystemProperty(VletConfig.ENV_VLET_INSTALL);

            if (path == null)
                path = VletConfig.getSystemEnv("VLET_INSTALL");

            if (path != null)
                vrl = new VRL("file:///" + path);

        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR, e, "Couldn't get InstallBase\n");
        }

        // try to figure out installation location:
        // Has now been moved to Bootstrapper, vlet.install should already
        // be specfied !

        if (vrl != null)
            return vrl;

        // this return the path to the .jar file where this class is stored.
        vrl = getBaseLocation();

        if (vrl == null)
        {
            logger.warnPrintf("getInstallBaseDir(): Can not figure out installation directory!");
            return null;
        }

        // parent dir of code base location
        vrl = vrl.getParent(); // cd ..

        path = vrl.getPath();

        // auto strip 'lib' from path when started directly from ./lib/...
        if (path.endsWith("/lib"))
        {
            vrl = vrl.getParent(); // cd ..
        }

        // auto strip 'lib' from path when started directly from ./bin/...
        if (path.endsWith("/bin"))
        {
            vrl = vrl.getParent(); // cd ..
        }

        logger.debugPrintf("getInstallBaseDir(): Returning install dir=%s\n", vrl);

        // return new VRL("file",null,path);
        return vrl;
    }

    /** Returns path of ~/.vletrc/vletrc.prop */
    public static VRL getUserPropertiesLocation()
    {
        return new VRL(getUserConfigDir().appendPath(VletConfig.VLETRC_PROP_FILENAME));
    }

    /**
     * Returns path of $HOME or alternative user home location if in applet
     * mode.
     */
    public static VRL getUserHomeLocation()
    {
        if (userHomeLocation != null)
            return userHomeLocation;

        if (isApplet == false)
        {
            userHomeLocation = new VRL("file", null, getUserHome());
            return userHomeLocation;
        }
        else
        {
            return baseInstallationVRL;
        }
    }

    /** Returns path of ~/.vletrc */
    public static VRL getUserConfigDir()
    {
        VRL homeLoc = getUserHomeLocation();
        return homeLoc.appendPath(VletConfig.USER_VLETRC_DIRNAME);
    }

    /** Returns path of ~/.vletrc/certificates */
    public static VRL getUserCertificatesDir()
    {
        return getUserConfigDir().appendPath(CERTIFICATES_SUBDIRNAME);
    }

    /** Returns path of VLET_INSTALL/etc/certificates */
    public static VRL getInstallationCertificatesDir()
    {
        return getInstallationConfigDir().appendPath(CERTIFICATES_SUBDIRNAME);
    }

    /** Returns path of "/etc/grid-security/certificates" */
    public static VRL getSystemCertificatesDir()
    {
        return new VRL(VRS.FILE_SCHEME, null, -1, DEFAULT_SYSTEM_CERTIFICATES_DIR);
    }

    /** Returns value of java property 'user.home' */
    public static String getUserHome()
    {
        String althome = getInstallationProperty("user.home");

        if (althome == null)
            // Return URI compatible path, but sanitise the location !
            return URIFactory.uripath(VletConfig.getSystemProperty("user.home"), true, java.io.File.separatorChar);

        // absolute path
        if (althome.startsWith("/"))
            return althome;

        // *** CUSTOM HOME ***

        // user home relative to installation root
        VRL install = getInstallBaseDir();
        VRL homeVrl;

        try
        {
            // tricky: when using the URI.resolve,
            // the URI uses the dirname(path) of the URI
            // as base URI (stripping for example the /index.html)
            // But the installation directory is already the base location.
            // use custom VRL.resolvePath instead of URI.resolve!
            homeVrl = install.resolvePath(althome);
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR, e, "Couldn't get UserHome!!!\n");
            return URIFactory.uripath(VletConfig.getSystemProperty(VletConfig.JAVA_USER_HOME));
        }

        // MUST OVERRIDE $HOME (cross fingers)
        System.setProperty(VletConfig.JAVA_USER_HOME, homeVrl.getPath());

        return homeVrl.getPath();
    }

    /**
     * Returns property from VLET_INSTALL/etc/vletrc.prop or NULL if file
     * doesn't exists
     */
    public static String getInstallationProperty(String name)
    {
        initVletrcProperties();

        String val = vletrcProperties.getProperty(name);

        if (val != null)
            return val;
        else
            return null;
    }

    /** Loads vletrc.prop from classpath or VLET_INSTALL/etc/vletrc.prop */
    private static void initVletrcProperties()
    {
        // auto initialize ONLY when properties are wanted !
        if (vletrcProperties == null)
        {
            try
            {
                vletrcProperties = VletConfig.loadPropertiesFromClasspath("vletrc.prop");
            }
            catch (VrsException e)
            {
                logger.warnPrintf("Couldn't load vletrc.prop from classpath.\n");
                logger.debugPrintf("Exception when load vletrc.prop from classpath:%s\n", e);
            }
        }

        if (vletrcProperties == null)
        {
            // try installation directory etc/vletrc.prop
            VRL vletrcLoc = getInstallationConfigDir().appendPath(VletConfig.VLETRC_PROP_FILENAME);

            try
            {
                vletrcProperties = VletConfig.staticLoadProperties(vletrcLoc);
            }
            catch (VrsException e)
            {
                logger.warnPrintf("Warning. Couldn't load vletrc.prop file from installation directory:%s\n", vletrcLoc);
                logger.debugPrintf("Exception when load vletrc.prop from installation location:%s\n", e);
            }
        }

        // empty environment ! (Service or applet environment )
        if (vletrcProperties == null)
            vletrcProperties = new Properties();

    }

    /** Return System depended TMP dir (TEMP under windows). */
    public static VRL getDefaultTempDir()
    {
        try
        {
            return new VRL("file:///"
                    + URIFactory.uripath(getSystemProperty(VletConfig.JAVA_TMPDIR), true, java.io.File.separatorChar));
        }
        catch (Exception e)
        {
            throw new Error(e.getMessage(), e);
        }
    }

    /**
     * Get property from ~/.vletrc/vletrc.prop or NULL if file or property
     * hasn't been configured. Always reloads ~/vletrc/.vletrc.prop! Returns
     * NULL if setUsePersistantUserConfiguration has been set to false !
     */
    public static String getUserProperty(String name)
    {
        // block single user configurations!
        if (VletConfig.getUsePersistantUserConfiguration() == false)
            return null;

        // reload!
        try
        {
            VRL loc = VletConfig.getUserPropertiesLocation();
            Properties props = VletConfig.staticLoadProperties(loc);
            return (String) props.get(name);
        }
        catch (Exception e)
        {
            logger.debugPrintf("No user property or load error:%s\n", e);
            return null;
        }
    }

    /** Returns location of VLET_INSTALL/lib */
    public static VRL getInstallationLibDir()
    {
        String libdir = getInstallationProperty(VletConfig.PROP_VLET_LIBDIR);

        if (libdir != null)
            return new VRL("file", null, libdir); // scheme,host,path

        // return VLET_INSTALL/lib
        return getInstallBaseDir().appendPath("lib");
    }

    /**
     * Return location of VLET_INSTALL/doc. This value is configured by the
     * bootstrapper.
     */
    public static VRL getInstallationDocDir()
    {
        String docdir = getInstallationProperty(VletConfig.PROP_VLET_DOCDIR);

        if (StringUtil.isWhiteSpace(docdir) == false)
            return new VRL("file", null, docdir); // scheme,host,path

        // Default: return VLET_INSTALL/lib
        return getInstallBaseDir().appendPath("doc");
    }

    /** Return directory from which the application was started */
    public static VRL getStartupWorkingDir()
    {
        try
        {
            return new VRL("file", null, getSystemProperty("user.dir"));
        }
        catch (Exception e)
        {
            throw new Error(e.getMessage(), e);
        }
    }

    /**
     * Returns user property from ~/.vletrc/vletrc.prop if it exists. Optionally
     * specify default value 'defval' for the case the property isn't set.
     */
    public static String getUserProperty(String propname, String defval)
    {
        String val = getUserProperty(propname);

        if (StringUtil.isEmpty(val))
            return defval;

        return val;
    }

    /**
     * Whether user configured properties in ~/.vlerc/vletrc.prop will be used
     * or not. If false, no settings will be read from this file, nor will user
     * configuration be written to that file.
     * 
     * @see #PROP_PERSISTANT_USER_CONFIGURATION
     */
    public static boolean getUsePersistantUserConfiguration()
    {
        // overide !
        if (isService() == true)
            return false;

        // If Service Environment == true => default value must be false !
        // Although it can be overriden...
        boolean defVal = (isService() == false);

        // persistant user configuration can only be a System Property !
        String val = VletConfig.getSystemProperty(VletConfig.PROP_PERSISTANT_USER_CONFIGURATION);
        if (StringUtil.isEmpty(val))
            return defVal;

        return StringUtil.parseBoolean(val, defVal);
    }

    public static boolean getBoolProperty(String val, boolean defaultValue)
    {
        String valstr = getProperty(val);
        return StringUtil.parseBoolean(valstr, defaultValue);
    }

    /**
     * Returns installation property from VLET_INSTALL/etc/vletrc.prop if it
     * exists. Optionally specify default value for the case the property isn't
     * set.
     */
    public static String getInstallationProperty(String propName, String defaultvalue)
    {
        String val = getInstallationProperty(propName);

        if (StringUtil.isEmpty(val))
            return defaultvalue;

        return val;
    }

    /**
     * Unified Global getProperty/getEnv method.
     * <p>
     * Checks system property settings in the following order (first found is
     * returned)
     * <ul>
     * <li>I) Check system properties VletConfig.getSystemProperty (command
     * line) for user specified (and optional overridden) properties.
     * <li>II) Check user stored properties (~/.vletrc/vletrc.prop)
     * <li>III) Check Installation stored properties
     * ($VLET_INSTALL/etc/vletrc.prop)
     * <li>IV) Check Environment Variable VletConfig.getSystemEnv();
     * </ul>
     * </pre>
     */
    public static String getProperty(String name)
    {
        String val = null;

        // I) first check System defined property (at startup with -D)
        val = VletConfig.getSystemProperty(name);
        if ((val != null) && (val.compareTo("") != 0))
            return val;

        // II: check user stored property ~/.vletrc/vletrc.prop
        val = VletConfig.getUserProperty(name);
        if ((val != null) && (val.compareTo("") != 0))
            return val;

        // III: check installation property $VLET_INSTALL/etc/vletrc.prop
        // (or vletrc.prop specified on classpath)
        val = VletConfig.getInstallationProperty(name);
        if ((val != null) && (val.compareTo("") != 0))
            return val;

        // IV: environment variable $NAME
        // Note: typically environment variable names (UPPER CASE) do not clash
        // with property setting names (Mixed Case and 'dotted').

        val = VletConfig.getSystemEnv(name);
        if ((val != null) && (val.compareTo("") != 0))
            return val;

        // Global.debugPrintln(Global.class,"Property not defined:"+name);
        return val;
    }

    /**
     * Set incoming firewall port range. Make sure this settings doesn't
     * conflict with environment variable: GLOBUS_TCP_PORT_RANGE
     * 
     * @see #ENV_GLOBUS_TCP_PORT_RANGE
     */
    public static void setIncomingFirewallPortRange(int start, int end)
    {
        String rangestr = start + "," + end;
        VletConfig.setSystemProperty(VletConfig.PROP_INCOMING_FIREWALL_PORT_RANGE, rangestr);
    }

    /**
     * Returns ~/.vletrc/cacerts location
     */
    public static String getDefaultUserCACertsLocation()
    {
        return VletConfig.getUserConfigDir().appendPath("cacerts").getPath();
    }

    public static VRL[] getCACertificateLocations()
    {
        return getCACertificateLocations(true);
    }

    /**
     * Returns: <lu> <li>file:/etc/grid-security/certificates <li>
     * file:~/.vletrc/certificates <li>file:/${VLET_INSTALL}/etc/certificates
     * </lu>
     */
    public static VRL[] getCACertificateLocations(boolean includingDefaults)
    {
        // Custom CA certificate directories:
        String pathStr = VletConfig.getProperty(VletConfig.PROP_GRID_CA_CERTIFICATE_LOCATIONS);
        String caPaths[] = null;

        if (StringUtil.isWhiteSpace(pathStr) == false)
        {
            // allow both command and color (unix style path)
            caPaths = pathStr.split("[,:]");
        }

        int len = 0;
        if (caPaths != null)
            len = caPaths.length;

        ArrayList<VRL> vrls = new ArrayList<VRL>();

        if (includingDefaults)
        {
            vrls.add(VletConfig.getSystemCertificatesDir());
            vrls.add(VletConfig.getInstallationCertificatesDir());
            vrls.add(VletConfig.getUserCertificatesDir());
        }

        // append as last:
        for (int i = 0; i < len; i++)
        {
            try
            {
                vrls.add(getBaseLocation().resolvePath(caPaths[i]));
            }
            catch (Exception e)
            {
                logger.errorPrintf("Invalid CA path:%s\n", caPaths[i]);
            }
        }

        VRL vrlsArr[] = new VRL[vrls.size()];
        vrlsArr = vrls.toArray(vrlsArr);
        return vrlsArr;
    }

    public static void setCACertificateLocations(String caCertificateLocations)
    {
        // Custom CA certificate directories:
        VletConfig.setSystemProperty(VletConfig.PROP_GRID_CA_CERTIFICATE_LOCATIONS, caCertificateLocations);
    }

    public static String getFirewallPortRangeString()
    {
        // first check my settings:
        String valstr = getProperty(VletConfig.PROP_INCOMING_FIREWALL_PORT_RANGE);

        // if FIREWALL_PORT_RANGE not set: use my GLOBUS_PORT_RANGE
        if (StringUtil.isEmpty(valstr))
            valstr = getProperty(VletConfig.ENV_GLOBUS_TCP_PORT_RANGE);

        if (StringUtil.isEmpty(valstr))
            return null;

        logger.debugPrintf("Global", "portRangeString ='%s'\n", valstr);

        //
        // LCG2/Globus 2.4 hack:
        //

        int range[] = portRange(valstr, null);

        if ((range == null) || (range.length != 2))
        {
            logger.errorPrintf("Parse error for %s='%s'\n", VletConfig.ENV_GLOBUS_TCP_PORT_RANGE, valstr);
            return null;
        }

        String newstr = range[0] + "," + range[1];
        return newstr;
    }

    public static int[] getIncomingFirewallPortRange()
    {
        return portRange(getFirewallPortRangeString(), null);
    }

    public static int[] portRange(String rangeStr, int defaultRange[])
    {
        if (rangeStr == null)
            return null;

        String vals[] = rangeStr.split("[ ,]");

        if (vals.length != 2)
            return defaultRange;

        int range[] = new int[2];

        range[0] = Integer.valueOf(vals[0]);
        range[1] = Integer.valueOf(vals[1]);

        logger.debugPrintf("portRange= %s-%s\n", range[0], range[1]);

        return range;
    }

    public static VRL getInstallationPluginDir()
    {
        return VletConfig.getInstallationLibDir().appendPath(VletConfig.PLUGIN_SUBDIR);
    }

    public static VRL[] getViewerPluginDirs()
    {
        VRL vrls[] = new VRL[2];
        vrls[0] = VletConfig.getInstallationLibDir().appendPath(VletConfig.PLUGIN_SUBDIR);
        vrls[1] = VletConfig.getInstallationLibDir().appendPath(VletConfig.VIEWERS_SUBDIR);
        return vrls;
    }

    public static String getVletVersion()
    {
        return getProperty(PROP_VLET_VERSION);
    }

    public static boolean getPassiveMode()
    {
        return getBoolProperty(PROP_PASSIVE_MODE, true);
    }

    public static VRL getHelpUrl(String keyword)
    {
        return null;
    }

    public static ClassLogger getRootLogger()
    {
        return ClassLogger.getLogger("rootlogger");
    }

    public static File[] getWindowsDrives()
    {
        ArrayList<File> rootsV = new ArrayList<File>();

        // update system property
        boolean skipFloppy = VletConfig.getBoolProperty(VletConfig.PROP_SKIP_FLOPPY_SCAN, true);

        // Create the A: drive whether it is mounted or not
        if (skipFloppy == false)
        {
            String drivestr = "A:\\";
            rootsV.add(new File(drivestr));
        }

        // Run through all possible mount points and check
        // for their existance.
        for (char c = 'C'; c <= 'Z'; c++)
        {
            char device[] =  { c, ':', '\\' };
            String deviceName = new String(device);
            File deviceFile = new File(deviceName);

            if ((deviceFile != null) && (deviceFile.exists()))
            {
                rootsV.add(deviceFile);
            }
        }

        File[] roots = new File[rootsV.size()];
        roots = rootsV.toArray(roots);

        return roots;
    }

    public static String getUserName()
    {
        return GlobalProperties.getGlobalUserName();
    }

    public static VRL getUserPluginDir()
    {
        return VletConfig.getUserConfigDir().appendPath(VletConfig.PLUGIN_SUBDIR);
    }

    public static boolean getUseHttpProxy()
    {
        // must move HTTP(S) proxies to delegated Worker class.
        return false;
    }

    /** Load properties from this URL */
    public static Properties loadPropertiesFromURL(URL url) throws VrsException
    {
        Properties props = new Properties();

        try
        {
            logger.debugPrintf("Loading classpath properties from:%s\n", url);

            InputStream inputs = null;
            if (url != null)
                inputs = url.openConnection().getInputStream();

            if (inputs != null)
                props.load(inputs);
        }
        catch (IOException e)
        {
            throw new NestedIOException(e.getMessage(), e);
        }
        // in the case of applet startup: Not all files are
        // Accessible, wrap exception for graceful exception handling.
        catch (java.security.AccessControlException ex)
        {
            throw new ResourceReadAccessDeniedException("Security Exception: Permission denied for:" + url, ex);
        }

        return props;
    }

    /**
     * Load a property file specified on the classpath or URL.
     */
    public static Properties loadPropertiesFromClasspath(String urlstr) throws VrsException
    {
        // check default classpath:
        URL url = VletConfig.class.getClassLoader().getResource(urlstr);

        if (url == null)
            // check context classpath
            url = Thread.currentThread().getContextClassLoader().getResource(urlstr);

        if (url == null)
            return new Properties();

        return loadPropertiesFromURL(url);
    }

    /** Load properties using URL stream readers */
    public static Properties staticLoadProperties(VRL vrl) throws VrsException
    {
        try
        {
            URL url = vrl.toURL();
            InputStream inps = url.openConnection().getInputStream();
            Properties props = new Properties();
            props.load(inps);
            return props;
        }
        catch (IOException e)
        {
            throw new NestedIOException("Couldn't load properties from:" + vrl, e);
        }
    }

    /**
     * Save properties using URL stream readers. Only works for file:/// URL!
     */
    public static void staticSaveProperties(VRL vrl, String optComments, Properties props) throws VrsException
    {
        if (optComments == null)
            optComments = "";

        try
        {
            OutputStream outps = FSUtil.getDefault().createOutputStream(vrl.getPath());
            props.store(outps, optComments);
            try
            {
                outps.close();
            }
            catch (Exception e)
            {
                ;
            }
        }
        catch (IOException e)
        {
            throw new NestedIOException("Couldn't save properties to:" + vrl, e);
        }
    }

}// FIN !
