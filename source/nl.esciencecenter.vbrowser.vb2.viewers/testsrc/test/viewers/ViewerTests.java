package test.viewers;

import java.net.URI;

import javax.swing.JFrame;

import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerPanel;


public class ViewerTests
{

    public static void testViewer(ViewerPanel textViewer, URI uri)
    {
        JFrame frame=new JFrame();
        
        frame.add(textViewer);
        frame.pack(); 
        frame.setSize(800,600);
        
        textViewer.initViewer();
        textViewer.startViewer();
        textViewer.updateURI(uri); 
        
        frame.setVisible(true); 
        
    }

}
