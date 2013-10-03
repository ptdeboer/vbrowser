package nl.esciencecenter.vbrowser.vb2.ui.viewerpanel;

import java.awt.Cursor;
import java.net.URI;
import java.util.Properties;

import javax.swing.Icon;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.ui.icons.IconProvider;
import nl.esciencecenter.ptk.ui.util.UIResourceLoader;

public abstract class EmbeddedViewer extends ViewerPanel implements MimeViewer
{
    private static final long serialVersionUID = -873655384459474749L;
        
    // ===  
    
    protected IconProvider iconProvider=null;
    
    protected String textEncoding = "UTF-8";

    protected Cursor busyCursor = new Cursor(Cursor.WAIT_CURSOR);

    public Cursor getBusyCursor()
    {
        return busyCursor;
    }

    public void setBusyCursor(Cursor busyCursor)
    {
        this.busyCursor = busyCursor;
    }

    public Cursor getDefaultCursor()
    {
        return defaultCursor;
    }

    public void setDefaultCursor(Cursor defaultCursor)
    {
        this.defaultCursor = defaultCursor;
    }

    protected Cursor defaultCursor = new Cursor(Cursor.DEFAULT_CURSOR);
    
    public ViewerResourceHandler getResourceHandler()
    {
        return ViewerResourceHandler.getDefault(); 
    }

    public void updateURI(URI uri)
    {
        super.updateURI(uri);
        startViewer();
    }

    public String getURIBasename()
    {
        return new URIFactory(getURI()).getBasename(); 
    }
    
    protected UIResourceLoader getResourceLoader()
    {
        return this.getResourceHandler().getResourceLoader();
    }
    
    protected IconProvider getIconProvider()
    {
        if (this.iconProvider==null)
        {
            iconProvider=new IconProvider(this, getResourceLoader()); 
        }
        
        return iconProvider;
    }
    
    protected Icon getIconOrBroken(String iconUrl)
    {
        return getIconProvider().getIconOrBroken(iconUrl); 
    }
    
    public String getTextEncoding()
    {
        return this.textEncoding;
    }
    
    public void setTextEncoding(String charSet)
    {
        this.textEncoding=charSet;
    }
    
    /** 
     * Returns most significant Class Name
     */
    public String getViewerClass()
    {
        return this.getClass().getCanonicalName(); 
    }
    
    public boolean canView(String mimeType)
    {
       return  new StringList(getMimeTypes()).contains(mimeType); 
    }
    
    protected Properties loadConfigProperties()
    {
        return new Properties(); 
    }
    
    protected void saveConfigProperties(Properties configProps)
    {
        
    }

    public void errorPrintf(String format,Object... args)
    {
        System.err.printf(format,args); 
    }

    protected void warnPrintf(String format,Object... args)
    {
        System.err.printf(format,args); 
    }

    protected void infoPrintf(String format,Object... args)
    {
        System.out.printf(format,args); 
    }

    protected void debugPrintf(String format,Object... args)
    {
        System.out.printf("DEBUG:"+format,args); 
    }

    public void showMessage(String format, String... args)
    {
        System.out.printf("MESSAGE:"+format,args); 
        
    }

}
