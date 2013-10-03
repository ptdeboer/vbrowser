package nl.esciencecenter.vbrowser.vb2.ui.viewerpanel;

import java.net.URI;

public abstract class EmbeddedViewer extends ViewerPanel implements MimeViewer
{
    private static final long serialVersionUID = -873655384459474749L;
        
    public ViewerContentFactory getContentFactory()
    {
        return ViewerContentFactory.getDefault(); 
    }

    public void updateURI(URI uri)
    {
        super.updateURI(uri);
        startViewer();
    }

    protected void errorPrintf(String format,Object... args)
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

}
