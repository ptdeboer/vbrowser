package nl.esciencecenter.vbrowser.vb2.ui.viewerpanel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.io.LocalFSNode;
import nl.esciencecenter.ptk.ui.util.UIResourceLoader;

/** 
 * Content Factory for the various embedded Viewers.
 */
public class ViewerResourceHandler
{
    
    private static ViewerResourceHandler instance=null; 
    

    public static ViewerResourceHandler getDefault()
    {
        if (instance==null)
        {
            instance=new ViewerResourceHandler();
        }   
        
        return instance;
    }

    private UIResourceLoader resourceLoader;
    
    private URI viewersConfigDir; 
    
    // === // 
    
    protected  ViewerResourceHandler()
    {
        resourceLoader=new UIResourceLoader(); 
        
        viewersConfigDir=null;
    }

    public void setViewerConfigDir(URI configDir)
    {
        System.err.printf("ViewerConfigDir=%s\n",configDir);
        this.viewersConfigDir=configDir;
    }
    
    public URI getViewerConfigDir()
    {
        return viewersConfigDir;
    }
    
    public InputStream openInputStream(URI uri) throws IOException
    {
        //register/cache streams ? 
        return resourceLoader.createInputStream(uri);
    }
    
    UIResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }

    public String getMimeType(URI uri)
    {
        return null;
    }

    public void writeText(URI uri, String txt, String encoding) throws IOException
    {
        resourceLoader.writeTextTo(uri, txt, encoding);
    }

    public String getText(URI uri, String textEncoding) throws IOException
    {
        return resourceLoader.readText(uri, textEncoding);
    }

    public boolean hasReplicas(URI uri)
    {
        return false;
    }

    public URI[] getReplicas(URI uri)
    {
        return null;
    }

    public Properties loadProperties(URI uri) throws IOException
    {
        if (uri==null)
            return null;

       return resourceLoader.loadProperties(uri);
    }

    public FSUtil getFSUtil()
    {
        return FSUtil.getDefault();
    }
    
    public void saveProperties(URI uri, Properties properties) throws IOException
    {
        System.err.printf("SaveProperties:"+uri); 
        if (uri==null)
            return; 
        
        LocalFSNode propsNode = getFSUtil().newLocalFSNode(uri); 
        
        LocalFSNode dir = propsNode.getParent(); 
            
        if (dir.exists()==false)
            dir.mkdirs(); 
        
        resourceLoader.saveProperties(propsNode.getURI(),properties);
    }

  

}
