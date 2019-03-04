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

package nl.esciencecenter.vlet.vrs;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import nl.esciencecenter.ptk.data.HashMapList;
import nl.esciencecenter.ptk.ui.UI;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.error.InitializationError;
import nl.esciencecenter.vlet.grid.proxy.GridProxy;
import nl.esciencecenter.vlet.vrs.events.ResourceEvent;
import nl.esciencecenter.vlet.vrs.events.ResourceEventListener;
import nl.esciencecenter.vlet.vrs.events.ResourceEventNotifier;
import nl.esciencecenter.vlet.vrs.tasks.VRSTaskWatcher;
import nl.esciencecenter.vlet.vrs.vfs.VFileSystem;
import nl.esciencecenter.vlet.vrs.vfs.VRSTransferManager;
import nl.esciencecenter.vlet.vrs.vrms.ConfigManager;
import nl.esciencecenter.vlet.vrs.vrms.MyVLe;

/**
 * VRS Context class.
 * <p>
 * All resources are linked to a (user) VRSContext.
 * <p>
 * Also, for use in Grid Services, one context per user must be used to ensure
 * user space separation of authenticated resources. For example, an
 * authenticated SSH Session may not be shared by other users!
 * <p>
 * Initialization Dependency: VRSContext -> {Registry,Global}
 * 
 * @author P.T. de Boer
 */
public class VRSContext implements Serializable
{
    // Serializable
    private static final long serialVersionUID = -1983193298093366604L;

    private static ClassLogger logger;

    // /** Class object for global (=default) context */
    // private static VRSContext instance=null;

    private static int instanceCounter = 0;

    /** Singleton or default class instance */
    private static VRSContext instance = null;

    /**
     * Get default VRS Context environment. It is recommended to share one
     * VRSContext between classes in the same Application. Customized
     * VRSContexts are currently used for multi user environments in web and
     * grid services.
     * 
     * @return The Global VRSContext object from VRS.getDefaultVRSContext()
     * @throws VrsException
     */
    public static synchronized VRSContext getDefault()
    {
        if (instance == null)
            instance = new VRSContext();
        return instance;
    }

    static
    {
        logger = ClassLogger.getLogger(VRSContext.class);
    }

    // =======================================================================

    // =======================================================================

    private int contextID = instanceCounter++;

    protected GridProxy _gridProxy = null;

    // static getRegistry() has been moved to VRS
    // private Registry getRegistry()=null;

    protected VNode virtualRoot = null;

    protected ConfigManager configManager = null;

    // Context Properties !
    protected Properties properties = new Properties();

    /** Alternative User Home */
    protected VRL userHomeLocation = null;

    /** Alternative User Home */
    protected VRL currentWorkingDir = null;

    /** Server and FileSystem properties */
    protected ServerInfoRegistry serverInfoRegistry = null;

    /**
     * Connected ResourceSystems ands Servers. VRS implementations can store
     * their ResourceSystems object into this VRSContext for runtime management
     * and caching of (connected) servers.
     */
    protected HashMapList<String, VResourceSystem> resourceSystemInstances = new HashMapList<String, VResourceSystem>();

    /** New Copy Manager to centralize VFS Transfers */
    protected VRSTransferManager vrsTransferManager = null;

    protected VRSTaskWatcher vrsTaskWatcher = null;

    // =======================================================================
    // Constructor
    // =======================================================================

    /**
     * Create new VRSContext. This will also initialize the Core VRS class
     * Registry and GridProxy if not initialized alreay.
     * <p>
     * 
     * @throws VrsException
     */
    public VRSContext()
    {
        init();
    }

    /**
     * This constructor can be used to create a VRSContext without any default
     * initialization. Use Setters methods to configure the context manually.
     * <p>
     * 
     * @throws VrsException
     */
    public VRSContext(boolean initialize)
    {
        if (initialize == false)
            init();
    }

    public int getID()
    {
        return this.contextID;
    }

    protected synchronized void init()
    {
        // One initilazor For All!
        // When creating a VRSContext. Registry et all must already be
        // initialized
        // to avoid initialization problems.
        VRS.init();

        // ============================
        // FIRST create config manager!
        // ============================
        configManager = new ConfigManager(this);

        // get Default trigger default initialization
        // getRegistry() = VRS.getRegistry();
        serverInfoRegistry = new ServerInfoRegistry(this);
        vrsTransferManager = new VRSTransferManager(this);
        vrsTaskWatcher = new VRSTaskWatcher("VRSTaskWatcher fro VRSContext #" + this.getID());
    }

    /**
     * Returns GridProxy (wrapper) object. Use this object to manipulate Grid
     * Proxies The GridProxy class is a wrapper for globus grid proxies.
     */
    public GridProxy getGridProxy()
    {
        if (_gridProxy == null)
            _gridProxy = GridProxy.createProxy(this);

        return _gridProxy;
    }

    /**
     * Specify custom Grid Proxy to use for this context. Already authenticated
     * resources won't be updated so use this method during initialization time.
     */
    public void setGridProxy(GridProxy prox)
    {
        this._gridProxy = prox;

        if (prox != null)
            // update ownership !
            prox.setVRSContext(this);
    }

    /**
     * Return initialized grid proxy as string.
     */
    public String getProxyAsString() throws VrsException
    {
        return this.getGridProxy().getProxyAsString();
    }

    /**
     * Get the Registry object that this Context uses. This is the Global
     * Registry object from VRS.
     * 
     * @return Global VRS Registry Object
     */
    public Registry getRegistry()
    {
        // All VRSContext share the same global VRS Registry !
        return VRS.getRegistry();
    }

    /**
     * Returns Virtual Root VRL of top level Resource Tree. Currently returns
     * the MyVLe object
     */
    public VRL getVirtualRootLocation() throws VrsException
    {
        return getVirtualRoot().getLocation();
    }

    /**
     * Returns Virtual Root of top level Resource Tree. Currently returns the
     * MyVLe object but as a VNode.
     */
    public synchronized VNode getVirtualRoot() throws VrsException
    {
        // intialize MyVle as default virtual root:
        if (this.virtualRoot == null)
            this.virtualRoot = getMyVLe();

        return virtualRoot;
    }

    /**
     * Return Top Level Resource if it is MyVle.
     */
    public synchronized MyVLe getMyVLe()
    {
        // if current root resource is MyVle, return that one
        if (this.virtualRoot instanceof MyVLe)
            return (MyVLe) virtualRoot;

        // To avoid circular initialization, use late initialization:
        // only initialize default MyVle AFTER
        // VRSContext has been initialized correctly.
        MyVLe myvle = null;

        if (myvle == null)
        {
            try
            {
                myvle = MyVLe.createVLeRoot(this);
            }
            catch (VrsException e)
            {
                logger.logException(ClassLogger.ERROR, this, e, "Exception during initialization:%s\n", e);
            }
        }
        return myvle;
    }

    /**
     * Set new virtual root, make sure to do this directly after creating a new
     * VRSContext, before doing any other calls.
     */
    public void setVirtualRoot(VNode vnode)
    {
        virtualRoot = vnode;
    }

    /**
     * Get ServerInfo object registered to the specified VRL.
     * <p>
     * The Scheme, Hostname and Port are use to query the ServerInfo database.
     * Optionally the UserInfo, if present in the VRL, is used as well. If more
     * ServerInfo object match, the first is returned. If autoCreate==true a new object
     * will be created but not stored in the Registry !
     * 
     * @see ServerInfoRegistry
     */
    public ServerInfo getServerInfoFor(VRL loc, boolean autoCreate) throws VrsException
    {
        if (loc == null)
            return null;

        // check existing:
        ServerInfo info = this.serverInfoRegistry.getServerInfoFor(loc, false);

        if (info != null)
        {
            logger.debugPrintf("Found existing ServerInfo for:%s\n", loc);
        }

        // AutoCreate Default one
        if ((info == null) && (autoCreate))
        {
            logger.warnPrintf("No ServerInfo found or created. Creating new DEFAULT ServerInfo for:%s\n", loc);
            info = this.serverInfoRegistry.getServerInfoFor(loc, true);
        }

        info = this.updateServerInfo(info,loc);

        return info;
    }

    /**
     * Update ServerInfo (or Resource Description). Calls VRS Implementation
     * updateServerInfo(). This is the only public method to call as ServerInfo
     * is context dependent.
     * 
     * @throws VrsException
     */
    public ServerInfo updateServerInfo(ServerInfo info, VRL location) throws VrsException
    {
        if (info == null)
            return null;

        String scheme = info.getScheme();
        VRSContext ctx = this;
        VRSFactory vrs = ctx.getRegistry().getVRSFactoryForScheme(scheme);

        if (vrs == null)
        {
            logger.warnPrintf("Warning: couldn't get VRS implementation for:%s\n", info);
            return info;
        }

        info = vrs.updateServerInfo(ctx, info, location);
        info.store();

        return info;
    }

    /**
     * Forwards ServerInfo query to ServerInfo getRegistry() associated with
     * this Context.
     * 
     * @see ServerInfoRegistry#getServerInfos(String, String, int, String)
     */
    public ServerInfo[] getServerInfosFor(String scheme, String host, int port, String userinfo)
    {
        return this.serverInfoRegistry.getServerInfos(scheme, host, port, userinfo);
    }

    /**
     * Perform openLocation using this VRSContext. Replaces static
     * Registry.openLocation();
     * 
     * @throws VrsException
     */
    public VNode openLocation(VRL vrl) throws VrsException
    {
        return getRegistry().openLocation(this, vrl);
    }

    /**
     * Perform openLocation using this VRSContext. Replaces static
     * Registry.openLocation();
     * 
     * @throws VrsException
     */
    public VNode openLocation(String vrlStr) throws VrsException
    {
        return getRegistry().openLocation(this, new VRL(vrlStr));
    }

    /** Resolves scheme to unified scheme (gftp->gsiftp) */
    public String resolveScheme(String scheme)
    {
        return getRegistry().getDefaultScheme(scheme);
    }

    /**
     * Check VRSContext property, if the property hasn't been defined as
     * VRSContext property, GlobalConfig.getProperty() will be called. To check
     * whether the property has been set at the VRSContext only, use
     * getProperty(name,false);
     * 
     * @param name
     *            - the property name
     */
    public Object getProperty(String name)
    {
        return getProperty(name, true);
    }

    /**
     * Check VRSContext property. If checkGlobal==true the Global environment
     * will be checked. If this isn't desired, set checkGlobal to false.
     * 
     * @param name
     *            - the property name
     * @param checkGlobal
     *            - whether global properties can be checked.
     */
    public Object getProperty(String name, boolean checkGlobal)
    {
        Object val = this.properties.get(name);

        if ((val == null) && (checkGlobal))
            return VletConfig.getProperty(name);

        return val;
    }

    /**
     * Set specified property for this context. Returns previous value.
     */
    public Object setProperty(String name, Object value)
    {
        return this.properties.put(name, value);
    }

    /**
     * Set Persistant User Property. Saves property in
     * $HOME/.vletrc/vletrc.prop.
     */
    public void setUserProperty(String name, String value)
    {
        this.getConfigManager().setUserProperty(name, value);
    }

    /**
     * Return property as String or returns String representation of property.
     * If Property is not defined in this context, Global.getPropery() is
     * called.
     */
    public String getStringProperty(String name)
    {
        Object obj = getProperty(name);
        if (obj == null)
            return null;
        String strval;

        if (obj instanceof String)
            strval = (String) obj;
        else
            strval = obj.toString();

        if (StringUtil.isEmpty(strval))
            return null;

        return strval;
    }

    /**
     * Return property as Integer or returns Integer representation of property.
     * Returns defaultValue is propery is not set If Property is not defined in
     * this context, Global.getPropery() is called.
     */
    public int getIntProperty(String name, int defaultVal)
    {
        Object obj = getProperty(name);
        if (obj == null)
            return defaultVal;
        if (obj instanceof Integer)
            return (Integer) obj;
        else
            // try to parse String:
            return new Integer(obj.toString());

    }

    /**
     * Return property as boolean or parses String representation to boolean
     * 
     * Checks this property store and if not defined in this context, will check
     * Global property store.
     * 
     * @see #getProperty(String)
     */
    public boolean getBoolProperty(String name, boolean defaultVal)
    {
        Object obj = getProperty(name);

        if (obj == null)
            return defaultVal;
        if (obj instanceof Boolean)
            return (Boolean) obj;
        else
            // try to parse String:
            return new Boolean(obj.toString());
    }

    /**
     * Returns path to LOCAL user home. In service contexts, this MIGHT be a
     * temporary local location to store user settings
     */

    public String getLocalUserHome()
    {
        return VletConfig.getUserHome();
    }

    /**
     * Get User's Home Location as VRL. The default location is "file:///"+$HOME
     * (${user.home})
     * 
     * @return user home location as VRL.
     */
    public VRL getUserHomeLocation()
    {
        if (userHomeLocation != null)
            return userHomeLocation;

        try
        {
            return new VRL("file:///" + getLocalUserHome());
        }
        catch (Exception e)
        {
            // fatal:
            throw new Error("URI Error:" + e);
        }
    }

    /**
     * Set Alternative (User) Home Location. May be called only once during the
     * lifetime of a VRSContext. This home might be a grid enabled location to
     * store remote settings. It is currently used in a Service Context
     * Environment.
     */
    public void setUserHomeLocation(VRL location)
    {
        if (this.userHomeLocation != null)
            throw new InitializationError("Alternative User Home Location already set !");

        this.userHomeLocation = location;
    }

    /**
     * Get Current Working Directory The default location is "file:///"+$CWD
     * (startup directory)
     * 
     * @return 'current' working dir or startup directory as VRL.
     */
    public VRL getWorkingDir()
    {
        // check custom:

        if (this.currentWorkingDir != null)
            return currentWorkingDir;
        // return global
        return VletConfig.getStartupWorkingDir();
    }

    /**
     * Specify alternative 'current working directory' for relative VRLs
     */
    public void setWorkingDir(VRL vrl)
    {
        this.currentWorkingDir = vrl;
    }

    /**
     * Store ServerInfo into the ServerInfoRegistry
     * 
     * @see ServerInfoRegistry
     */
    public ServerInfo storeServerInfo(ServerInfo info)
    {
        return this.serverInfoRegistry.store(info);
    }

    public ServerInfo removeServerInfo(ServerInfo info)
    {
        return this.serverInfoRegistry.remove(info);
    }

    /**
     * Registry for the Servers and Server Info descriptions. Used by MyVLe to
     * create/query Server Objects.
     */
    public ServerInfoRegistry getServerInfoRegistry()
    {
        return this.serverInfoRegistry;
    }

    /**
     * If a ResourceSystem has been instantiated and stored in the internal
     * instance repository, this method can be used to get that (cached)
     * instance. The instance must have the specified serverClass.
     */
    public VResourceSystem getServerInstance(String serverid, Class<? extends VResourceSystem> serverClass)
    {
        VResourceSystem server = resourceSystemInstances.get(serverid);

        if (server == null)
            return null;

        if (serverClass.isInstance(server))
            return server;
        else
            logger.warnPrintf("Server Mismatch for server '%s'. class<%s>!=<%s> \n", serverid, serverClass,
                    server.getClass());

        return null;
    }

    /**
     * Return all instances of the specified ResourceSystem.
     */
    public List<VResourceSystem> getServerInstances(Class<? extends VResourceSystem> serverClass)
    {
        List<VResourceSystem> list = new ArrayList<VResourceSystem>();

        Set<String> keys = resourceSystemInstances.keySet();

        for (String key : keys)
        {
            VResourceSystem server = resourceSystemInstances.get(key);
            if (serverClass.isInstance(server))
                list.add(server);
        }
        return list;
    }

    /** Store server in Context owned instance repository. */
    public VResourceSystem putServerInstance(String id, VResourceSystem server)
    {
        if (this.resourceSystemInstances.get(id) == null)
            logger.infoPrintf("Storing New Server Instance <%s>:%s \n", server.getClass(), id);
        else
            logger.warnPrintf("Warning: Replacing Server Instance <%s>:%s \n", server.getClass(), id);

        return this.resourceSystemInstances.put(id, server);
    }

    public void putServerInstance(VResourceSystem server)
    {
        this.putServerInstance(server.getID(), server);
    }

    public void removeServerInstance(VResourceSystem server)
    {
        resourceSystemInstances.remove(server);
    }

    /**
     * Returns normalized or default scheme name for the specifed scheme.
     * 
     * @param scheme
     *            alias to resolve
     * @return default scheme
     */
    public String getDefaultScheme(String scheme)
    {
        return this.getRegistry().getDefaultScheme(scheme);
    }

    /**
     * Returns all VResourceSystems which have implemented the VNode or
     * VServerNode interface and are stored in the Instance registry. Currently
     * used for debugging.
     */
    public VNode[] getResourceSystemNodes()
    {
        VResourceSystem[] vsyss = this.resourceSystemInstances.toArray(new VResourceSystem[0]);

        Vector<VNode> nodes = new Vector<VNode>();

        if (vsyss != null)
            for (VResourceSystem vsys : vsyss)
                if (vsys instanceof VNode)
                    nodes.add((VNode) vsys);

        VNode nodeArr[] = new VNode[nodes.size()];
        nodeArr = nodes.toArray(nodeArr);
        return nodeArr;
    }

    // ===========================================================
    // Resource Factory Methods
    // ===========================================================

    public VRSFactory getResourceFactoryFor(VRL vrl)
    {
        return this.getRegistry().getVRSFactory(vrl.getScheme(), vrl.getHostname());
    }

    public VResourceSystem openResourceSystem(VRL loc) throws VrsException
    {
        return this.getRegistry().openResourceSystem(this, loc);
    }

    public VFileSystem openFileSystem(VRL location) throws VrsException
    {
        return this.getRegistry().openFileSystem(this, location);
    }

    // ===========================================================
    // Context Event Handling
    // ===========================================================

    /**
     * Fire a resource event from this context. Use ResourceEvent class to
     * create one.
     */
    public void fireEvent(ResourceEvent event)
    {
        this.getRegistry().getResourceEventNotifier().fire(event);
    }

    public void addResourceEventListener(ResourceEventListener listener)
    {
        this.getRegistry().getResourceEventNotifier().addListener(listener);
    }

    public void removeResourceEventListener(ResourceEventListener listener)
    {
        this.getRegistry().getResourceEventNotifier().removeListener(listener);
    }

    /** Returns Global Resource Event notofier. */
    public ResourceEventNotifier getResourceEventNotifier()
    {
        return this.getRegistry().getResourceEventNotifier();
    }

    /**
     * Returns Default VO for this Context.
     * 
     * @return Default VO from the Grid Proxy Object.
     */
    public String getVO()
    {
        return this.getGridProxy().getVOName();
    }


    public String getSystemEnv(String envVar)
    {
        // no context overrided Environment Variables:
        return VletConfig.getSystemEnv(envVar);
    }

    public synchronized ConfigManager getConfigManager()
    {
        if (this.configManager == null)
        {
            configManager = new ConfigManager(this);
        }
        return configManager;
    }

    public VRSTaskWatcher getTaskWatcher()
    {
        return this.vrsTaskWatcher;
    }

    /**
     * Returns UI for this Context. If no UI is configured, a 'Dummy' UI will be
     * returned.
     * 
     * @return VBrowser UI or a 'dummy' UI (in the case of a headless
     *         environment).
     */
    public UI getUI()
    {
        // get runtime UI !
        return VRS.getRegistry().getUI();
    }

    public VRSTransferManager getTransferManager()
    {
        return this.vrsTransferManager;
    }

    public void dispose()
    {
        reset();
    }

    public void reset()
    {
        disposeResourceSystems();
    }

    /**
     * Dispose and remove registered ResourceSystems
     */
    protected void disposeResourceSystems()
    {
        Set<String> keySet = resourceSystemInstances.keySet();
        String keys[] = new String[keySet.size()];

        keys = keySet.toArray(keys);

        for (String key : keys)
        {
            VResourceSystem server = resourceSystemInstances.get(key);
            logger.debugPrintf("Disconnecting ResourceSystem:%s::%s\n", key, server);

            if (server != null)
            {
                try
                {
                    server.disconnect();
                    server.dispose();
                    resourceSystemInstances.remove(key);
                }
                catch (Exception e)
                {
                    logger.logException(ClassLogger.DEBUG, this, e,
                            "Exception when disconnecting resource server %s\n", server);
                }
            }
        }

    }

}
