package nl.esciencecenter.vbrowser.vb2.ui.viewerpanel;

import java.net.URI;

import javax.swing.JFrame;

public class ViewerFrame extends JFrame
{
    private static final long serialVersionUID = -1604425778812821234L;
    
    protected ViewerPanel viewer; 
    
    public ViewerFrame(ViewerPanel viewer)
    {
        this.viewer=viewer;
        initGui(); 
    }

    protected void initGui()
    {
        this.add(viewer);
    }
    
    public ViewerPanel getViewer()
    {
        return viewer; 
    }
     
    public static ViewerFrame startViewer(ViewerPanel viewer, URI optionalURI)
    {
        ViewerFrame frame=createViewerFrame(viewer,true); 
        
        frame.setVisible(true); 
        viewer.updateURI(optionalURI,true); 
        
        return frame;
    }

    public static ViewerFrame createViewerFrame(ViewerPanel viewer, boolean initViewer)
    {
        ViewerFrame frame=new ViewerFrame(viewer); 
        if (initViewer)
        {
            viewer.initViewer();  
        }
        frame.pack(); 
        frame.setSize(frame.getPreferredSize()); 
        //frame.setSize(800,600); 
        
        return frame; 
    }
}
