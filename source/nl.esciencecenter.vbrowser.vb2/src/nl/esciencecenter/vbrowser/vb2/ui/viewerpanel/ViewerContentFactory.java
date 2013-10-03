package nl.esciencecenter.vbrowser.vb2.ui.viewerpanel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import nl.esciencecenter.ptk.util.ResourceLoader;

/** 
 * Content Factory for the various embedded Viewers.
 */
public class ViewerContentFactory
{

    public static ViewerContentFactory getDefault()
    {
        return new ViewerContentFactory();
    }

    private ResourceLoader resourceLoader;

    // === // 
    
    protected  ViewerContentFactory()
    {
        resourceLoader=new ResourceLoader(); 
    }

    public InputStream openInputStream(URI uri) throws IOException
    {
        //register/cache streams ? 
        return resourceLoader.createInputStream(uri);
        
    }
    
    ResourceLoader getResourceLoader()
    {
        return resourceLoader;
    }

}
