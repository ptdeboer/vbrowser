package nl.esciencecenter.ptk.jfx.util;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.net.URISyntaxException;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class FXWebKitJFrame extends JFrame
{
    private static final long serialVersionUID = -8338688804088729908L;
    
    private FXWebKitJPanel webKitPanel;
    
    public FXWebKitJFrame()
    {
        initComponents(); 
    }
    
    protected void initComponents()
    {
        {
            webKitPanel = new FXWebKitJPanel(new BorderLayout(),true);
            getContentPane().add(webKitPanel);
        }
    }
    
    public void loadURL(final String url)
    {
        webKitPanel.loadURL(url);
    }
    
    public java.net.URI getURI() throws URISyntaxException
    {
        return webKitPanel.getURI(); 
    }
    
    public static FXWebKitJFrame launch(final String url)
    {
        final FXWebKitJFrame frame=new FXWebKitJFrame(); 
        
        Runnable starter=new Runnable()
        {
            public void run()
            {
                frame.setPreferredSize(new Dimension(1024, 600));
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
                frame.loadURL(url);
                
                frame.pack();
                frame.setVisible(true);
            }
        };

        SwingUtilities.invokeLater(starter);
        
        return frame; 
    }
}
