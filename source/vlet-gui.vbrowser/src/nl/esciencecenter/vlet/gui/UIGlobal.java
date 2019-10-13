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

package nl.esciencecenter.vlet.gui;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.ssl.CertificateStore;
import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskSource;
import nl.esciencecenter.ptk.ui.UI;
import nl.esciencecenter.ptk.ui.icons.IconProvider;
import nl.esciencecenter.ptk.util.ResourceLoader;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.mimetypes.MimeTypes;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.exception.InternalError;
import nl.esciencecenter.vlet.grid.proxy.GridProxy;
import nl.esciencecenter.vlet.gui.dialog.ExceptionForm;
import nl.esciencecenter.vlet.gui.dialog.SimpleDialog;
import nl.esciencecenter.vlet.gui.proxyvrs.ProxyVRSClient;
import nl.esciencecenter.vlet.vrs.Registry;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.events.ResourceEvent;
import nl.esciencecenter.vlet.vrs.tasks.VRSTaskWatcher;
import nl.esciencecenter.vlet.vrs.util.VRSResourceLoader;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vrms.ConfigManager;



/** 
 * Shared Global environment for the UI objects (VBrowser, Viewers, etc.). 
 * 
 * @author P.T. de Boer
 */
public class UIGlobal
{

	
	private static VFSClient vfs; 
	private static VRSContext vrsContext; 
	private static GuiSettings guiSettings;
	private static VRSResourceLoader _resourceLoader;
	
	/** Frame to store graphics and other AWT resources */ 
	private static JFrame globalFrame=new JFrame(); 
	private static IconProvider iconProvider;
	
	/**
	 * Initialized Global VRSContext and GUI Configuration. 
	 */
	public static void init()
	{
		GlobalProperties.init(); 
		
		// context and context configuration 
		VRSContext ctx = getVRSContext();
		ConfigManager confMan = ctx.getConfigManager();
		
		// Enable UI and persistant ServerInfoRegistry 
        //confMan.setPersistantUserConfiguration(true);
        confMan.loadPersistantServerConfiguration(); 
        confMan.setHasUI(true);		
	}
	
	public static String[] init(String args[])
	{
		args=VletConfig.parseArguments(args);
		init(); 
		
		return args; 
	}
	
	
	public static VRSContext getVRSContext() 
	{
		if (vrsContext==null)
			vrsContext=VRSContext.getDefault(); 
		
		return vrsContext; 
	}

    public static VRSTaskWatcher getTaskWatcher()
    {
        return getVRSContext().getTaskWatcher(); 
    }   
    
	/** Return global user certificate store: ~/.vletrc/cacerts 
	 * @throws Exception */ 
	public static CertificateStore getUserCertificateStore() throws Exception
	{
	    return getVRSContext().getConfigManager().getCertificateStore();   
	}
	
	public static VFSClient getVFSClient()
	{
		if (vfs==null)
			vfs=new VFSClient(getVRSContext());
		
		return vfs; 
	}
	
	public static GuiSettings getGuiSettings() 
	{
		if (guiSettings==null)
			guiSettings=GuiSettings.getDefault(); 
		
		return guiSettings;
	}
	
	/** Return Registry associated with current VRSContext */ 
	public static Registry getRegistry() 
	{
		return getVRSContext().getRegistry(); 
	}
	
	public static JFrame getGlobalFrame() {return globalFrame;}
		
	/** Call this to dispose and flush all global resources */ 
	public static void shutdown()
	{
	    if (vfs!=null) 
	        vfs.close();
		iconProvider.clearCache();
		globalFrame.dispose();
	    VRS.exit();
		//ProxyNode.disposeClass();
	}

	public static boolean isGuiThread()
	{
		 return (SwingUtilities.isEventDispatchThread()==true);
	}

	public static GridProxy getGridProxy()
	{
		return getVRSContext().getGridProxy();
	}

	public static void saveProperties(VRL loc, Properties props) throws Exception
	{
		getResourceLoader().saveProperties(loc.toURL(), props,"VLET UIGlobal properties");
	}

	public static Properties loadProperties(VRL guiSettingsLocation) throws Exception
	{
	    return getResourceLoader().loadProperties(guiSettingsLocation.toURL());
	}

	public static VRSResourceLoader getResourceLoader()
	{
		if (_resourceLoader==null)
			_resourceLoader=new VRSResourceLoader(getVRSContext());
		
		return _resourceLoader;
	}

	/** Return icon or 'Broken Image' icon */ 
	public static Icon getIconOrBroken(String url)
	{
		return getIconProvider().createIconOrBroken(url);
	}

	/** Cached icon factory */ 
	public static IconProvider getIconProvider()
	{
		if (iconProvider==null)
			iconProvider=new IconProvider(globalFrame, ResourceLoader.getDefault());
		
		return iconProvider; 
	}

	public static VRL getVirtualRootLocation() throws VrsException
	{
		return getVRSContext().getVirtualRootLocation(); 
	}

	public static void displayEventMessage(ResourceEvent e)
	{
		SimpleDialog.displayMessage(null,e.getMessage()); 
	}

	public static void displayErrorMessage(String message)
	{ 
		SimpleDialog.displayErrorMessage(message);
	}

	/** Static method to call SwingUtil.*/
	
	public static void swingInvokeLater(Runnable task)
	{
		SwingUtilities.invokeLater(task); 
	}

	/** Static method to call SwingUtil.*/
	public static void swingInvokeLater(ActionTask task)
	{
		SwingUtilities.invokeLater(task); 
	}

	public static void assertNotGuiThread(String msg) throws InternalError
	{
		assertGuiThread(false,msg);
	}
	
	public static void assertGuiThread(String msg) throws InternalError
	{
		assertGuiThread(true,msg); 
	}

	public static void assertGuiThread(boolean mustBeGuiThread,String msg) throws InternalError
	{
        // still happens when trying to read/acces link targets of linknodes 
        if (mustBeGuiThread!=UIGlobal.isGuiThread())
        {
            UILogger.infoPrintf(UIGlobal.class,"\n>>>\n    *** Swing GUI Event Assertion Error *** !!!\n>>>\n");
            throw new  InternalError("Internal Error. Cannot perform this "
            						+(mustBeGuiThread?"during":"outside")+" the Swing GUI Event thread.\n"+msg);
        }
	}

    public static void showMessage(String title, String message)
    {
        SimpleDialog.displayMessage(null,title,message); 
    }

    public static void showError(String title, String message)
    {
        SimpleDialog.displayMessage(null,title,message);
    }

	public static UI getMasterUI()
	{
	    // should be VBrowser
		return UIGlobal.getVRSContext().getUI();
	}

    public static void showException(final Throwable ex)
    {
     // asynchronous exception 
        // Do No Invoke Outside Event Thread: 
        if (UIGlobal.isGuiThread()==false)
        {
            Runnable run=new Runnable()
            {
                public void run()
                {
                    showException(ex);  
                }
            };

            SwingUtilities.invokeLater(run); 
 
            return; 
        }

        ExceptionForm.show(ex);
    }

    public static String getJGridStartLocation()
    {
        return VletConfig.getProperty("vlet.jgridstart.location");  
    }

    public static MimeTypes getMimeTypes()
    {
        return MimeTypes.getDefault(); 
    }

    public static void resetContext()
    {
         VRS.getRegistry().reset(); 
         getVRSContext().reset();
    }

    public static ProxyVRSClient getProxyVRS()
    {
        return ProxyVRSClient.getInstance();
    }

}
