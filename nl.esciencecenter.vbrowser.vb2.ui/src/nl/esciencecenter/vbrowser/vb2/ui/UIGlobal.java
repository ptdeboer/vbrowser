package nl.esciencecenter.vbrowser.vb2.ui;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import nl.esciencecenter.ptk.ui.icons.IconProvider;
import nl.esciencecenter.ptk.ui.util.UIResourceLoader;
import nl.esciencecenter.ptk.util.logging.ClassLogger;


public class UIGlobal
{
//    private static ProxyFactory proxyItemFactory;
    private static GuiSettings guiSettings; 
    private static ClassLogger uiLogger;
    private static UIResourceLoader resourceLoader; 
    private static JFrame rootFrame;
    private static IconProvider iconProvider;
//    private static MimeTypes mimeTypes;
    
    static
    {
    	uiLogger=ClassLogger.getLogger(UIGlobal.class); 
    	
        uiLogger.debugPrintf(">>> UIGlobal.init() <<<\n"); 
        
        try
        {
//            proxyItemFactory=ProxyRegistry.getInstance().getDefaultFactory(); 
            guiSettings=new GuiSettings();
            uiLogger=ClassLogger.getLogger("UIGlobal"); 
            resourceLoader=UIResourceLoader.getDefault();
            rootFrame=new JFrame(); 
            iconProvider=new IconProvider(rootFrame,resourceLoader); 
//            mimeTypes=MimeTypes.getDefault();
        }
        catch (Exception e)
        {
            uiLogger.logException(ClassLogger.FATAL,e,"Exception during initialization!"); 
        }
    }
    
    public static void init()
    {
    }
    
//    public static ProxyFactory getProxyItemFactory()
//    {
//        return proxyItemFactory; 
//    }

//    public static void errorPrintf(Object source, String format, Object... args)
//    {   
//        uiLogger.errorPrintf(ClassLogger.object2classname(source),format,args);  
//    }
//   
//
//    public static void debugPrintf(Object source, String format, Object... args)
//    {   
//        uiLogger.debugPrintf(ClassLogger.object2classname(source)+":"+format,args);
//    }
//    
//    public static void warnPrintf(Object source, String format, Object... args)
//    {   
//        uiLogger.warnPrintf(ClassLogger.object2classname(source)+":"+format,args);
//    }
//    
//    public static void logException(Object source, Throwable e, String format, Object... args) 
//    {
//        uiLogger.logException(ClassLogger.ERROR,e,ClassLogger.object2classname(source)+":"+format,args);  
//    }
//
//
//    public static void infoPrintf(Object source,String format, Object... args)
//    {
//        uiLogger.infoPrintf(ClassLogger.object2classname(source)+":"+format,args);  
//    }

//    public static ResourceLoader getResourceLoader()
//    {
//        return resourceLoader; 
//    }

    public static GuiSettings getGuiSettings()
    {
        return guiSettings; 
    }

    public static IconProvider getIconProvider()
    {
        return iconProvider; 
    }
    
//    public MimeTypes getMimeTypes()
//    {
//        return mimeTypes; 
//    }

    public static void assertNotGuiThread(String msg) throws Error
	{
		assertGuiThread(false,msg);
	}
	
	public static void assertGuiThread(String msg) throws Error
	{
		assertGuiThread(true,msg); 
	}

	public static void assertGuiThread(boolean mustBeGuiThread,String msg) throws Error
	{
        // still happens when trying to read/acces link targets of linknodes 
        if (mustBeGuiThread!=UIGlobal.isGuiThread())
        {
            uiLogger.infoPrintf("\n>>>\n    *** Swing GUI Event Assertion Error *** !!!\n>>>\n");
            throw new Error("Internal Error. Cannot perform this "
            						+(mustBeGuiThread?"during":"outside")+"during the Swing GUI Event thread.\n"+msg);
        }
	}
    
	public static void swingInvokeLater(Runnable task)
	{
		SwingUtilities.invokeLater(task); 
	}
	
	public static boolean isGuiThread()
	{
		 return (SwingUtilities.isEventDispatchThread()==true);
	}

	public static boolean isApplet() 
	{
		return false;
	}
}

