package test.viewers;

import java.net.URI;

import nl.esciencecenter.vbrowser.vb2.ui.viewers.JavaWebStarter;


public class StartWebStarterJGridStart
{
    // === Main ===

    public static void main(String args[])
    {
        // Global.setDebug(true);
        
        try
        {
            ViewerTests.testViewer(new JavaWebStarter(),new URI("http://ca.dutchgrid.nl/start/jgridstart.jnlp"));
        }
        catch (Exception e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }

    
}
