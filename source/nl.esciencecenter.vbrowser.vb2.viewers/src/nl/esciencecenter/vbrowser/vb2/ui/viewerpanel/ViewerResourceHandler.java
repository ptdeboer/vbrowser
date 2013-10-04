package nl.esciencecenter.vbrowser.vb2.ui.viewerpanel;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Properties;

import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.io.IOUtil;
import nl.esciencecenter.ptk.io.RandomReader;
import nl.esciencecenter.ptk.io.RandomWriter;
import nl.esciencecenter.ptk.io.local.LocalFSNode;
import nl.esciencecenter.ptk.ssl.CertificateStore;
import nl.esciencecenter.ptk.ssl.CertificateStoreException;
import nl.esciencecenter.ptk.ui.util.UIResourceLoader;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.mimetypes.MimeTypes;

/** 
 * Content Factory and Resource Manager for the various embedded Viewers.
 */
public class ViewerResourceHandler
{
    private static ClassLogger logger=ClassLogger.getLogger(ViewerResourceHandler.class); 
    
    private UIResourceLoader resourceLoader;
    
    private URI viewersConfigDir;

    private CertificateStore certificateStore; 
    
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

    private FSUtil getFSUtil()
    {
        return FSUtil.getDefault();
    }
    
    public void saveProperties(URI uri, Properties properties) throws IOException
    {
        logger.infoPrintf("Saving Properties to:"+uri); 
        if (uri==null)
            return; 
        
        LocalFSNode propsNode = getFSUtil().newLocalFSNode(uri); 
        
        LocalFSNode dir = propsNode.getParent(); 
            
        if (dir.exists()==false)
            dir.mkdirs(); 
        
        resourceLoader.saveProperties(propsNode.getURI(),properties);
    }

    public void syncReadBytes(RandomReader reader, long fileOffset, byte[] buffer, int bufferOffset, int numBytes) throws IOException
    {
        // delegate to IOUtil 
        IOUtil.syncReadBytes(reader, fileOffset, buffer, bufferOffset, numBytes); 
        //reader.close(); 
    }

    public void syncWriteBytes(FSNode file, long fileOffset, byte[] buffer, int bufferOffset, int numBytes) throws IOException
    {
        RandomWriter writer = getFSUtil().createRandomWriter(file); 
        writer.writeBytes(fileOffset, buffer, bufferOffset, numBytes); 
        writer.close(); 
    }
    

    public CertificateStore getCertificateStore() throws CertificateStoreException
    {
        if (this.certificateStore==null)
        {
            certificateStore=CertificateStore.getDefault(true); 
        }
        return certificateStore;

    }
    
    public void setCertificateStore(CertificateStore store)
    {
        this.certificateStore=store; 
    }

    public String getMimeType(URI uri)
    {
        return MimeTypes.getDefault().getMimeType(uri.getPath());  
    }

    public RandomReader createRandomReader(URI loc) throws IOException
    {
        FSUtil fsUtil=this.getFSUtil();
        return fsUtil.createRandomReader(fsUtil.newFSNode(loc)); 
    }
  
    public RandomWriter createRandomWriter(URI loc) throws IOException
    {
        FSUtil fsUtil=this.getFSUtil();
        return fsUtil.createRandomWriter(fsUtil.newFSNode(loc)); 
    }

}
