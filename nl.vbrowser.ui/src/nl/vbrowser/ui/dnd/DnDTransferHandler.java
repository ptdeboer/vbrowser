package nl.vbrowser.ui.dnd;

import java.awt.Component;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;

import javax.swing.JComponent;
import javax.swing.TransferHandler;

import nl.vbrowser.ui.model.ViewNode;
import nl.vbrowser.ui.model.ViewNodeComponent;
import nl.vbrowser.ui.model.ViewNodeContainer;

/**
 * Default TransgerHandler for ViewNodes.
 */
public class DnDTransferHandler extends TransferHandler
{
    private static final long serialVersionUID = -115960212645219778L;
    private static DnDTransferHandler defaultTransferHandler = new DnDTransferHandler();

    public static DnDTransferHandler getDefault()
    {
        return defaultTransferHandler;
    }
    
    // === Instance stuff === //

    /** Construct default TransferHandler wich handles VRL Objects */
    public DnDTransferHandler()
    {
        // Default ViewNode Transferer.
    }

    
    @Override
    public void exportDone(JComponent comp, Transferable data, int action)
    {
        // this method is called when the export of the Transferable is done.
        // The actual DnD is NOT finished.

        DnDUtil.debugPrintln("exportDone():" + data);
        DnDUtil.debugPrintln("exportDone action=" + action);
    }

    @Override
    public boolean canImport(JComponent c, DataFlavor[] flavors)
    {
        for (DataFlavor flav : flavors)
            DnDUtil.debugPrintln("canImport():" + flav);
        return false;
        // return VTransferData.hasMyDataFlavor(flavors);
    }

    @Override
    protected Transferable createTransferable(JComponent c)
    {
        DnDUtil.debugPrintf("createTransferable():%s\n",c);

        if ((c instanceof ViewNodeComponent)==false)
        {
            DnDUtil.errorPrintf("createTransferable(): Error: Not a ViewNodeComponent:%s\n",c);
            return null;
        }
        
        return createTransferable((ViewNodeComponent) c);
    }

    protected Transferable createTransferable(ViewNodeComponent c)
    {
        //Debug("Create Transferable:"+c);
        ViewNode[] nodes=null;
        
        ViewNodeContainer parent = c.getViewContainer();
        
        if (parent!=null)
        {
            // redirect to parent for multi selection!
            nodes = parent.getNodeSelection();
        }
        else if (c instanceof ViewNodeContainer)
        {
            // drag initiated from Container! (ResourceTree) 
            nodes=((ViewNodeContainer)c).getNodeSelection();
        }
        else
        {
            // stand-alone 'node'
            nodes=new ViewNode[1];
            nodes[0]=c.getViewNode(); // get actual view node i.s.o contains selection.
        }
        
        if ((nodes!=null) && (nodes.length>0))
        {
            DnDUtil.debugPrintf("createTransferable(): getNodeSelection()=%d\n",nodes.length);
            
            if (nodes.length<=0)
                return null;
            
            return VRIListTransferable.createFrom(nodes); 
        }
        else
        {
            DnDUtil.debugPrintf("Tranfer source not recognised:%s\n",c); 
        }
        
        return null; 
    }
  
    
    @Override
    public void exportAsDrag(JComponent comp, InputEvent e, int action)
    {
        DnDUtil.debugPrintln("exportAsDrag():" + e);
        super.exportAsDrag(comp, e, action);
    }

    public void exportToClipboard(JComponent comp, Clipboard clipboard, int action)
    {
        DnDUtil.debugPrintln("exportToClipboard():" + comp);
        super.exportToClipboard(comp, clipboard, action);
    }

    @Override
    public int getSourceActions(JComponent c)
    {
        return COPY_OR_MOVE;// nodes can be copied and moved. 
    }    
    

    // NOT called if importData(TransferSupport is implemented) 
    @Override
    public boolean importData(JComponent comp, Transferable data)
    {
        // This method is directory called when performing CTRL-V

        DnDUtil.debugPrintf("importData():%s\n",comp);
        if ((comp instanceof ViewNodeComponent)==false)
        {
            DnDUtil.errorPrintf("importData(): Error: Not a ViewNodeComponent:%s\n",comp);
            return false;
        }
        return doPasteData((ViewNodeComponent)comp,data);
    }

    /*
     * New (java 1.6) version. If this is overriden, the old ImportData(JComponent, Transferable) is NOT called 
     * @see javax.swing.TransferHandler#importData(javax.swing.TransferHandler.TransferSupport)
     */
    @Override
    public boolean importData(TransferSupport support) 
    {
        // This method is directory called when performing CTRL-V

        Component comp = support.getComponent(); 
        Transferable data = support.getTransferable(); 
        // This method is directory called when performing CTRL-V
        DnDUtil.debugPrintf("importData(TransferSupport):%s\n",comp);
        if ((comp instanceof ViewNodeComponent)==false)
        {
            DnDUtil.errorPrintf("importData(): Error: Not a ViewNodeComponent:%s\n",comp);
            return false;
        }
        
        return doPasteData((ViewNodeComponent)comp,data); 
    }
    
    // ========================================================================
    // Drop/Paste actions
    // ========================================================================
    
    /**
     * Paste Data call when for example CTRL-V IS called ! Supplied component
     * is the Swing Component which has the focus when CTRL-V was called !
     */
    public boolean doPasteData(ViewNodeComponent comp, Transferable data)
    {
        DnDUtil.debugPrintf("pasteData():" + comp);
        return false;
    }

}
