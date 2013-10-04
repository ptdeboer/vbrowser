package test.viewers;

import java.net.URI;

import nl.esciencecenter.vlet.gui.viewers.x509viewer.X509Viewer;

public class StartX509Viewer
{
    // === Main ===

    public static void main(String args[])
    {
        // Global.setDebug(true);
        
        try
        {
            ViewerTests.testViewer(new X509Viewer(),new URI("file:///home/ptdeboer/tests/cert.pem"));
            // viewStandAlone(null);
        }
        catch (Exception e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }

    
}
