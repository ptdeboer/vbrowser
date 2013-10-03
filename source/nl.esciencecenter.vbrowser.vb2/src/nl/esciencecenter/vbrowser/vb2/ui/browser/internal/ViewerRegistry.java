package nl.esciencecenter.vbrowser.vb2.ui.browser.internal;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ImageViewer;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerPanel;

public class ViewerRegistry
{
    
    private static ViewerRegistry instance; 
    
    public static ViewerRegistry getDefault()
    {
        if (instance==null)
            instance=new ViewerRegistry();
        
        return instance; 
    }
    
    // ===
    //
    // ===
    
    public ViewerRegistry()
    {
        
    }

    public Class getMimeTypeViewerClass(String mimeType)
    {
       return ImageViewer.class; 
    }
    
    public ViewerPanel createViewer(Class<? extends ViewerPanel> viewerClass)
    {
        ViewerPanel viewer = null;

        try
        {
            viewer = viewerClass.newInstance();
        }
        catch (Exception e)
        {
            //logException(this,ClassLogger.ERROR,e,"Couldnt instanciate:%s\n",viewerClass);
        }

        return viewer;
    }
}
