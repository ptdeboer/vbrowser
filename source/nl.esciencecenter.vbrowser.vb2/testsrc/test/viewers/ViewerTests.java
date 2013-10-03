package test.viewers;

import java.net.URI;

import javax.swing.JFrame;

import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ImageViewer;

public class ViewerTests
{

    public static void testViewer(ImageViewer imageViewer, URI uri)
    {
        JFrame frame=new JFrame();
        
        frame.add(imageViewer);
        frame.pack(); 
        frame.setSize(800,600);
        
        imageViewer.initViewer();
        imageViewer.startViewer();
        imageViewer.updateURI(uri); 
        
        frame.setVisible(true); 
        
    }

}
