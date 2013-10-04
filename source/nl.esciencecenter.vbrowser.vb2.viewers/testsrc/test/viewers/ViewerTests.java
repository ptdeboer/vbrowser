package test.viewers;

import java.net.URI;


import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerFrame;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerPanel;


public class ViewerTests
{

    public static void testViewer(ViewerPanel viewer, URI uri)
    {
        ViewerFrame frame = ViewerFrame.startViewer(viewer, uri); 
        
        
    }

}
