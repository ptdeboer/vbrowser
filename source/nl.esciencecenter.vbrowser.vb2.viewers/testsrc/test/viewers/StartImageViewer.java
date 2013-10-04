package test.viewers;

import java.net.URI;

import nl.esciencecenter.vbrowser.vb2.ui.viewers.ImageViewer;

public class StartImageViewer
{
    // === Main ===

    public static void main(String args[])
    {
        // Global.setDebug(true);
        
        try
        {
            ViewerTests.testViewer(ImageViewer.class,new URI("file:///home/ptdeboer/tests/image1.jpg"));

            // viewStandAlone(null);
        }
        catch (Exception e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }

    
}
