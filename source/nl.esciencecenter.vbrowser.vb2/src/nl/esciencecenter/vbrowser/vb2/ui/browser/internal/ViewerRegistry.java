package nl.esciencecenter.vbrowser.vb2.ui.browser.internal;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerPanel;
import nl.esciencecenter.vbrowser.vb2.ui.viewers.ImageViewer;
import nl.esciencecenter.vbrowser.vb2.ui.viewers.TextViewer;

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
        TextViewer textViewer=new TextViewer();
        
        if (textViewer.canView(mimeType))
            return textViewer.getClass(); 

        ImageViewer imageViewer=new ImageViewer();
        
        if (imageViewer.canView(mimeType))
            return imageViewer.getClass(); 

       return null;
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
