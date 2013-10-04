package nl.esciencecenter.vbrowser.vb2.ui.viewerpanel;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vb2.ui.viewers.*;
import nl.esciencecenter.vlet.gui.viewers.x509viewer.X509Viewer;

public class ViewerRegistry
{
    private static ClassLogger logger=ClassLogger.getLogger(ViewerRegistry.class); 
    
    protected class ViewerEntry
    {
        protected Class<? extends ViewerPanel> viewerClass; 
        
        ViewerEntry(Class<? extends ViewerPanel> viewerClass)
        {
            this.viewerClass=viewerClass;
        }
        
        Class<? extends ViewerPanel> getViewerClass()
        {
            return viewerClass; 
        }
        
    }
    
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
    
    private ArrayList<ViewerEntry> viewers=new ArrayList<ViewerEntry>();
    private Map<String, List<ViewerEntry>> mimeTypeViewers = new HashMap<String,List<ViewerEntry>>();
    private ViewerResourceHandler resourceHandler=null; 

    
    public ViewerRegistry()
    {
        initViewers(); 
        
        resourceHandler=new ViewerResourceHandler();
    }

    protected void initViewers()
    {
        registerViewer(TextViewer.class); 
        registerViewer(ImageViewer.class); 
        registerViewer(HexViewer.class); 
        registerViewer(X509Viewer.class); 
        registerViewer(JavaWebStarter.class); 
    }
    
    public void registerViewer(Class<? extends ViewerPanel> viewerClass)
    {
        ViewerEntry entry= new ViewerEntry(viewerClass); 
        viewers.add(entry); 
        
        try
        {
            ViewerPanel viewer = viewerClass.newInstance();
      
            if (viewer instanceof MimeViewer)
            {
                registerMimeTypes(((MimeViewer)viewer).getMimeTypes(),entry);
            }
      
        }
        catch (InstantiationException | IllegalAccessException e)
        {
            logger.logException(ClassLogger.ERROR,e,"Failed to register viewer class:%s\n",viewerClass);
        } 
    }
    
    
    private void registerMimeTypes(String[] mimeTypes, ViewerEntry entry)
    {
        for (String type:mimeTypes)
        {
            List<ViewerEntry> list = this.mimeTypeViewers.get(type); 
         
            if (list==null)
            {
                list=new ArrayList<ViewerEntry>(); 
                mimeTypeViewers.put(type,list); 
            }
            
            list.add(entry); 
        }
    }

    public Class<? extends ViewerPanel> getMimeTypeViewerClass(String mimeType)
    {
        List<ViewerEntry> list = this.mimeTypeViewers.get(mimeType); 

        if ((list==null) || (list.size()<0)) 
        {
            return null; 
        }
                 
        return list.get(0).getViewerClass(); 
        
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
            logger.logException(ClassLogger.ERROR,e,"Couldnt instanciate:%s\n",viewerClass);
        }

        return viewer;
    }
    

    public ViewerResourceHandler getResourceHandler()
    {
        return resourceHandler; 
    }
    
    public static JFrame startStandalone(ViewerPanel textViewer, URI uri)
    {
        JFrame frame=new JFrame();
        
        frame.add(textViewer);
        frame.pack(); 
        frame.setSize(800,600);
        
        textViewer.initViewer();
        textViewer.updateURI(uri,true); 
        
        frame.setVisible(true); 
        return frame; 
        
    }
}
