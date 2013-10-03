package nl.esciencecenter.vbrowser.vb2.ui.viewerpanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.net.URI;

import javax.swing.JFrame;
import javax.swing.JPanel;


public abstract class ViewerPanel extends JPanel
{
    private static final long serialVersionUID = -8148836110597201287L;

    private JPanel innerPanel;

    private URI viewedUri;

    private boolean isBusy; 
    
    protected ViewerPanel()
    {
        this.setLayout(new BorderLayout());
    }
    
    /** 
     * Add custom content to this panel.
     * @return
     */
    public JPanel getContentPanel()
    {
        return this; 
//        if (innerPanel==null)
//        {
//            return initInnerPanel(); 
//            
//        }
//        
//        return innerPanel; 
    }

    public JPanel initInnerPanel()
    {
        this.innerPanel=new JPanel();
        this.add(innerPanel,BorderLayout.CENTER); 
        this.innerPanel.setLayout(new FlowLayout());
        return innerPanel;
    }
    
    public URI getURI()
    {
        return viewedUri; 
    }
    
    public void updateURI(URI newUri)
    {
        this.viewedUri=newUri; 
    }
    
    public void notifyBusy(boolean isBusy)
    {
        this.isBusy=isBusy; 
    }
    
    public boolean isBusy()
    {
        return this.isBusy;
    }

    protected void notifyException(String message, Throwable e)
    {
        System.err.printf("Error:%s\n",message); 
        e.printStackTrace(); 
    }
    
    /** 
     * Whether Viewer has it own ScrollPane. 
     * If not the parent Component might embedd the viewer into a ScrollPanel. 
     * @return
     */
    public boolean haveOwnScrollPane()
    {
        return false; 
    }
    
    /** Set title of master frame or Viewer tab */
    public void setViewerTitle(final String name)
    {
        JFrame frame = getJFrame();
        if (frame != null)
            getJFrame().setTitle(name);

        this.setName(name);
    }
    
    /**
     * Returns parent JFrame if contained in one. Might return NULL if parent is
     * not a JFrame! use getTopLevelAncestor() to get the (AWT) toplevel
     * component.
     * 
     * @see javax.swing.JComponent#getTopLevelAncestor()
     * @return the containing JFrame or null.
     */
    final public JFrame getJFrame()
    {

        Container topcomp = this.getTopLevelAncestor();
        if (topcomp instanceof Frame)
            return ((JFrame) topcomp);

        return null;
    }

    final protected boolean hasJFrame()
    {
        return (this.getJFrame() != null);
    }
    // =========================================================================
    // Abstract Interface 
    // ========================================================================
    
    /**
     * Initialize GUI Component of viewer. Do not start loading resource. 
     */
    abstract public void initViewer();

    /** 
     * Start the viewer, load resources of necessary.
     */
    abstract public void startViewer();

    /**
     * Stop/suspend viewer. 
     * All background activity must stop. 
     * After a stopViewer() a startViewer() may occure to notify the viewer can be actived again. 
     */
    abstract public void stopViewer();

    /**
     * Stop viewer and dispose resources. 
     * After a disposeViewer() 
     */ 
    abstract public void disposeViewer();

}
