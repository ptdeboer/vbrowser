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

package nl.esciencecenter.vlet.gui.viewers;

import java.util.Properties;
import java.util.Vector;

import nl.esciencecenter.ptk.data.HashMapList;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.actions.ActionMenuMapping;
import nl.esciencecenter.vlet.exception.NestedVrsException;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.gui.UILogger;
import nl.esciencecenter.vlet.gui.viewers.ViewerInfo;
import nl.esciencecenter.vlet.gui.viewers.ViewerPlugin;
import nl.esciencecenter.vlet.util.PluginLoader;
import nl.esciencecenter.vlet.util.PluginLoader.PluginInfo;
import nl.esciencecenter.vlet.vrs.VRSFactory;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;

/**
 * The Viewer Registry.<br>
 * Load and register the IViewer plugins + their mimetypes. Note that is it
 * allowed to let viewers register mimetypes which have been registered already.
 * In that case the one registered the last will be the default to be used when
 * viewing an object of that mimetype. This last registered viewer is the first
 * one to show up (together with the other Viewers) in the Viewer With menu.
 */
public class ViewerRegistry
{
    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(ViewerRegistry.class);
    }

    /** Explicit class: Vector of Viewers */
    public static class ViewerList extends Vector<ViewerInfo>
    {
        private static final long serialVersionUID = 6145291205032458085L;
    };

    public static final String VIEWERSETTINGS_FILE = "viewerconf.prop";

    /**
     * Default viewers to register. Last viewer registered is the first used.
     */
    public static String defaultViewers[] = {
            nl.esciencecenter.vlet.gui.viewers.HexViewer.class.getCanonicalName(),
            nl.esciencecenter.vlet.gui.viewers.TextViewer.class.getCanonicalName(),
            nl.esciencecenter.vlet.gui.viewers.ImageViewer.class.getCanonicalName(),
            nl.esciencecenter.vlet.gui.viewers.VHTMLViewer.class.getCanonicalName(),
            nl.esciencecenter.vlet.gui.viewers.grid.replicaviewer.ReplicaEditor.class.getCanonicalName(),
            nl.esciencecenter.vlet.gui.viewers.external.JavaWebStarter.class.getCanonicalName(),
            nl.esciencecenter.vlet.gui.viewers.x509viewer.ViewerX509.class.getCanonicalName(),
            nl.esciencecenter.vlet.gui.viewers.VLTermStarter.class.getCanonicalName(),
            nl.esciencecenter.vlet.gui.viewers.grid.jobmonitor.JobMonitor.class.getCanonicalName()
        };

    /** Single Class Object: single registry per class */
    private static ViewerRegistry _viewerRegistry = new ViewerRegistry();

    public static ViewerRegistry getRegistry()
    {
        return _viewerRegistry;
    }

    // ==================
    // instance
    // ==================

    public ViewerList getAllViewers()
    {
        return viewers;
    }

    /**
     * Get list of viewer which can be standalone tools
     */
    public ViewerList getAllTools()
    {
        ViewerList tools = new ViewerList();

        for (ViewerInfo viewer : viewers)
        {
            if (viewer.isTool())
                tools.add(viewer);
        }

        return tools;
    }

    public ViewerList getViewerListForMimetype(String mimetype)
    {
        // NiNo
        if (mimetype == null)
            return null;

        String prefclassname = getPreferredViewerFor(mimetype);
        ViewerInfo prefviewer = getViewerInfoForClass(prefclassname);
        ViewerList list = registeredViewers.get(mimetype);

        if (prefviewer != null)
        {
            if (list == null)
            {
                list = new ViewerList();
            }

            // remove pref viewer
            if (list.contains(prefviewer))
                list.remove(prefviewer);

            // instert as first viewer:
            list.insertElementAt(prefviewer, 0);
        }

        return list;
    }

    // ==================================================================
    // Methods to produce a Viewer
    // ==================================================================

    public ViewerPlugin getViewerInstance(String viewerClass)
    {
        Class<? extends ViewerPlugin> viewerC = getViewerClassFor(null, viewerClass);
        if (viewerC == null)
            return null;
        return this.instanciateViewer(viewerC);
    }

    public Class<? extends ViewerPlugin> getViewerClass(String viewerClass)
    {
        return getViewerClassFor(null, viewerClass);
    }

    /**
     * Find viewer class for mimetype and optional use the proved viewer Class
     * Name
     */
    public Class<? extends ViewerPlugin> getViewerClassFor(String mimetype, String optionalClassName)
    {
        if ((optionalClassName == null) && (mimetype == null))
            return null;

        Class<ViewerPlugin> viewerClass = null;
        ViewerInfo viewerClassInfo = null;

        if (optionalClassName == null)
        {
            // get Class for mimetype
            viewerClassInfo = getViewerInfoForMimetype(mimetype);
        }
        else
        {
            // fetch classname
            viewerClassInfo = getViewerInfoForClass(optionalClassName);
        }

        if (viewerClassInfo == null)
        {
            logger.infoPrintf("Couldn't find viewerClass for mimetype:%s (prefClass='%s')\n",
                    mimetype, optionalClassName);
            return null;
        }

        logger.infoPrintf("Found viewerclass:%s\n", viewerClassInfo.getClassName());
        logger.infoPrintf(" - viewerclass.urlloader =%s\n",viewerClassInfo.getClassLoader());
        logger.infoPrintf(" - viewerclass.name      =%s\n",viewerClassInfo.getName());
        logger.infoPrintf(" - viewerclass.isTool    =%s\n",viewerClassInfo.isTool());

        try
        {
            Class<?> vclass;

            // update current thread class loader:
            if (viewerClassInfo.getClassLoader() != null)
            {
                // *** DYNAMIC CLASS LOADING ***
                // Get custom URLClassloader associated with this viewer
                // so that custom class can be loaded.
                // since this might be another thread, we cannot
                // use the default classloader from this thread.
                Thread.currentThread().setContextClassLoader(viewerClassInfo.getClassLoader());
            }

            // use thread context class loader:
            vclass = Thread.currentThread().getContextClassLoader().loadClass(viewerClassInfo.getClassName());

            // vclass=viewerRegistry.getClass().getClassLoader().loadClass(viewerClassInfo.classname);

            viewerClass = (Class<ViewerPlugin>) vclass;
        }
        catch (ClassNotFoundException e)
        {
            logger.logException(ClassLogger.ERROR,e,"ClassNotFoundExeption:%s\n",viewerClassInfo.getClassName());
        }

        if (viewerClass == null)
        {
            logger.warnPrintf("Couldn't create viewerclass:%s\n",viewerClassInfo.getClassName());
            return null;
        }

        return viewerClass;
    }

    public ViewerPlugin instanciateViewer(Class<? extends ViewerPlugin> viewerClass)
    {
        ViewerPlugin viewer = null;

        try
        {
            viewer = viewerClass.newInstance();
        }
        catch (Exception e)
        {
            UILogger.logException(this,ClassLogger.ERROR,e,"Couldnt instanciate:%s\n",viewerClass);
        }

        return viewer;
    }

    // =======================================================================
    // === Instance Stuff ===
    // =======================================================================
    /** Hastable registers per mimetype a list of viewers */
    private HashMapList<String, ViewerList> registeredViewers = new HashMapList<String, ViewerList>();

    private PluginLoader pluginLoader;

    private ViewerList viewers = new ViewerList();

    /** Private Constructor for Class Object */
    private ViewerRegistry()
    {
        init();
    }

    private void init()
    {
        pluginLoader = new PluginLoader();

        loadPreferredViewers();

        // register default viewers using global classLoader:
        for (String className : defaultViewers)
            registerViewer(Thread.currentThread().getContextClassLoader(), className);

        VRL viewerPlugins[] = VletConfig.getViewerPluginDirs();
        loadViewerPlugins(viewerPlugins);

        VRL userviewers[] =new VRL[1]; 
        userviewers[0] = VletConfig.getUserPluginDir();
        loadViewerPlugins(userviewers);
    }

    private void loadPreferredViewers()
    {
        VRL vrl = VletConfig.getUserConfigDir().appendPath(VIEWERSETTINGS_FILE);

        try
        {
            this.preferredViewers = UIGlobal.loadProperties(vrl);
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.DEBUG,e,"Couldn't load viewer settings:%s\n",vrl);
        }
    }

    /* Scan directory for viewer implementations */
    private void loadViewerPlugins(VRL viewersdirs[])
    {
        VFSClient vfs = new VFSClient();
        
        for  (VRL dir:viewersdirs)
        {
            try
            {
                if (vfs.existsDir(dir) == false)
                {
                    logger.debugPrintf("viewers dir doesn't exist:%s\n",dir);
                    continue;
                }

                VDir vdir = vfs.getDir(dir);
                VFSNode nodes[] = vdir.list();
    
                // Check if file is a jar or directory contains
                // implementation.
                // Either the filename is the full package name
                // or the directory is
                if ((nodes == null) || (nodes.length <= 0))
                    continue; 
    
                for (VFSNode node : nodes)
                {
                    try
                    {
                        ViewerInfo vinfo = null;
                        // use plugin loader:
                        PluginInfo pluginInfo = pluginLoader.loadLocalPlugin(node.getPath());
    
                        // use isAssignableFrom to check subclass/interface type of
                        // Class:
                        if (pluginInfo.actualClass != null)
                        {
                            // ===
                            // Currently must be ViewerPlugin
                            // IMimeType interface not yet complete !
                            // ====
    
                            if (ViewerPlugin.class.isAssignableFrom((pluginInfo.actualClass)))
                            {
                                logger.debugPrintf("+++ adding ViewerPlugin plugin:%s\n",pluginInfo.className);
                                vinfo = addViewerPlugin(pluginInfo);
                            }
                            else if (VRSFactory.class.isAssignableFrom(pluginInfo.actualClass))
                            {
                                logger.debugPrintf("Ignoring VRS plugin :%s\n",pluginInfo.className);
                            }
                            else
                            {
                                logger.errorPrintf("*** Error: Unknown plugin (not VRS Class nor ViewerPlugin Class):%s\n",
                                                pluginInfo.className);
                            }
                        }
                        else
                        {
                            logger.errorPrintf("*** Error: Unknown plugin loading failed:%s\n",pluginInfo.className);
                        }
                    }
                    catch (Exception e)
                    {
                        logger.logException(ClassLogger.ERROR,e,"Error loading/initializing viewer:%s\n",node);
                    }
                    catch (Throwable e)
                    {
                        logger.logException(ClassLogger.ERROR,e,"Internal Error loading/initializing viewer:%s\n",node);
                    }
                } // for node:nodes 
 
            }
            catch (VrsException e)
            {
                logger.logException(ClassLogger.ERROR,e,"Error reading viewersdir:%s\n",dir);
            }
        }
    }

    /**
     * Add Plugin if plugin is of IViwer type. Will return null if it could add
     * the plugin.
     * 
     * @param plugin
     * @return
     */
    private ViewerInfo addViewerPlugin(PluginInfo plugin)
    {
        if (plugin == null)
            return null;

        String className = plugin.className;

        return registerViewer(plugin.classLoader, className);
    }

    public void registerViewer(Class<? extends ViewerPlugin> clss)
    {
        registerViewer(clss.getClassLoader(), clss.getCanonicalName().toString());
    }

    public void registerViewer(String clazzname)
    {
        registerViewer(Thread.currentThread().getContextClassLoader(), clazzname);
    }

    public ViewerInfo registerViewer(ClassLoader classLoader, String className)
    {
        Class viewclass = null;//      mimetype
        logger.infoPrintf("Registering Viewer  : %s\n",className);

        try
        {
            // use thread context class loader:
            viewclass = classLoader.loadClass(className);

            Object o = null;

            try
            {
                o = viewclass.newInstance(); // create new Object
            }
            catch (InstantiationException e)
            {
                logger.logException(ClassLogger.ERROR,e,"***Error adding Viewer Class (No public constructor maybe?):%s\n",className); 
            }
            catch (IllegalAccessException e)
            {
                logger.logException(ClassLogger.ERROR,e,"***Error adding Viewer Class (Private Member/Constructor???) :%s",className);
            }

            if (o instanceof ViewerPlugin)
            {
                ViewerPlugin viewer = (ViewerPlugin) o;
                String types[] = viewer.getMimeTypes();
                String viewerName = viewer.getName();
                // add viewer

                ViewerInfo vInfo = new ViewerInfo(classLoader, viewerName, className);
                // later registered viewers are first.
                // This allows custom viewers to show up first
                // and moves the default viewers down.

                viewers.insertElementAt(vInfo, 0);
                vInfo.isTool = viewer.isTool();
                vInfo.setVersion(viewer.getVersion());
                vInfo.setAboutText(viewer.getAbout());
                vInfo.setMimeTypes(viewer.getMimeTypes());

                for (int i = 0; (types != null) && (i < types.length); i++)
                {
                    String mimetype = types[i];
                    logger.infoPrintf("Registering mimetype: '%s'-> Viewer:%s ('%s')\n",mimetype,className,viewerName);

                    ViewerList vlist = registeredViewers.get(mimetype);

                    if (vlist == null)
                    {
                        // create new viewerlist for this mimetype
                        vlist = new ViewerList();
                        // register new mimetype list
                        registeredViewers.put(mimetype, vlist);
                    }

                    // Add this viewer to mimetype list as first viewer
                    // so that laster registere show up first.
                    vlist.insertElementAt(vInfo, 0);
                    // vlist.add(vInfo);
                }

                // Add ActionMappings
                vInfo.setActionMappings(viewer.getActionMappings());

                // verbose 2= configuration/settings:
                if (vInfo != null)
                {
                    logger.debugPrintf("Registered viewer:%s:%s\n", viewerName,className);
                }

                // let viewer keep reference
                viewer.setViewerInfo(vInfo);
                return vInfo;
            }
            else if (o == null)
            {
                logger.errorPrintf("*** Failed to add Viewer Class:%s\n",className);
            }
            else
            {
                logger.errorPrintf("*** Error adding Viewer Class:%s. Class is NOT of (interface) ViewerPlugin type!",className);
            }
        }
        catch (ClassNotFoundException e)
        {
            logger.errorPrintf("*** ClassNotFoundException: Could not load viewer class:%s\n",className);
            showError(className, e);
        }
        catch (java.lang.UnsatisfiedLinkError e)
        {
            logger.errorPrintf("*** UnsatisfiedLinkError: Could not load dependencies for class:%s\n",className);
            showError(className, e);
        }
        // classloader error ?
        catch (Throwable e)
        {
            logger.errorPrintf("*** Internal error for class:%s\n",className);
            showError(className, e);
        }

        return null;
    }

    private void showError(String className, Throwable e)
    {
        VrsException vle = NestedVrsException.create(
                "Couldn't load viewer:" + className + "\n"
                + "Exception=" + e.getClass().getCanonicalName() + "\n" 
                + "Reason=" + e.getMessage(), e,
                e.getClass().getCanonicalName());

        nl.esciencecenter.vlet.gui.dialog.ExceptionForm.show(vle);
    }

    public ViewerInfo getViewerInfoForClass(String viewerClassname)
    {
        if (viewerClassname == null)
            return null;

        for (ViewerInfo info : viewers)
        {
            if (info.getClassName().compareTo(viewerClassname) == 0)
                return info;
        }

        return null;
    }

    /** mimetype=classname preferred viewers property (has) map */
    Properties preferredViewers = new Properties(); // empty map;

    /** Returns default or preferred viewer type for mimetype */
    private ViewerInfo getViewerInfoForMimetype(String mimetype)
    {
        String viewerclass = getPreferredViewerFor(mimetype);

        if (viewerclass != null)
        {
            // fetch class
            ViewerInfo info = getViewerInfoForClass(viewerclass);

            if (info != null)
            {
                return info;
            }
            else
            {
                logger.warnPrintf("Could not find preferred viewer:%s\n",viewerclass);
                // use default
            }
        }

        ViewerList list = registeredViewers.get(mimetype);

        if ((list == null) || (list.size() <= 0))
            return null;

        // return first:
        return list.get(0);
    }

    private String getPreferredViewerFor(String mimetype)
    {
        String confstr = (String) preferredViewers.get(mimetype);

        if (confstr != null)
        {
            // command line style classname + arguments configuration line:
            String argv[] = confstr.split(" ");
            return argv[0];
        }

        return null;
    }

    public Vector<ActionMenuMapping> collectActionMappings()
    {
        Vector<ActionMenuMapping> mappings = new Vector<ActionMenuMapping>();

        for (ViewerInfo info : viewers)
        {
            Vector<ActionMenuMapping> maps = info.getActionMappings();

            if (maps != null)
                mappings.addAll(maps);
        }
        return mappings;
    }
    
//
//    /** Collect actionmappings from registered services */
//    public Vector<ActionMenuMapping> getActionMappings()
//    {
//        Vector<ActionMenuMapping> mappings = new Vector<ActionMenuMapping>();
//
//        synchronized (registeredServices)
//        {
//            for (VRSFactory vrs : getRegisteredServices())
//            {
//                Vector<ActionMenuMapping> maps = vrs.getActionMenuMappings();
//
//                if (maps != null)
//                {
//                    for (ActionMenuMapping map : maps)
//                    {
//                        map.setVRS(vrs); // update parent VRS !
//                        mappings.add(map);
//                    }
//                }
//            }
//        }
//
//        return mappings;
//    }
    
}
