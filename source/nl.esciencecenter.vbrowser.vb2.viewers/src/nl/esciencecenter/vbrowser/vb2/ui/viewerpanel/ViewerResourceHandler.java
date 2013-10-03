package nl.esciencecenter.vbrowser.vb2.ui.viewerpanel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

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

    // === // 
    
    protected  ViewerResourceHandler()
    {
        resourceLoader=new UIResourceLoader(); 
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

}
