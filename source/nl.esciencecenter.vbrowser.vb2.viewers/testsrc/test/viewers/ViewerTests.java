package test.viewers;

import java.net.URI;

import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerFrame;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerPanel;
import nl.esciencecenter.vbrowser.vb2.ui.viewers.HexViewer;
import nl.esciencecenter.vbrowser.vb2.ui.viewers.TextViewer;


public class ViewerTests
{

    public static void testViewer(Class<? extends ViewerPanel> class1, URI uri)
    {
        ViewerFrame frame = ViewerFrame.startViewer(class1, uri); 
        
        
    }

}
