package nl.esciencecenter.ptk.ui.widgets;
 
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.io.IOException;
import java.util.List;

import nl.esciencecenter.ptk.net.URIUtil;

/** 
 * Handle drops on the Navigation Bar. 
 */
public class URIDropHandler implements DropTargetListener
{
    public static DataFlavor flavorURIList = new DataFlavor("text/uri-list;class=java.lang.String", "uri list");
    public static DataFlavor stringFlavor = DataFlavor.stringFlavor; 
    
    public static DataFlavor myFlavors[]={stringFlavor,flavorURIList};
    
    private URIDropTargetLister uriDropTargetListener;

    public URIDropHandler(URIDropTargetLister uriDropListener)
    {
        this.uriDropTargetListener=uriDropListener;
    }

    public void dragEnter(DropTargetDragEvent dtde)
    {
        // example how to check& Reject/Accept flavor.
//        for (DataFlavor flavor:myFlavors)
//        {
//            if (dtde.isDataFlavorSupported(flavor))
//            {
//                dtde.acceptDrag(DnDConstants.ACTION_COPY);
//                return ; 
//            }
//        }
//        dtde.rejectDrag(); 
    }

    public void dragOver(DropTargetDragEvent dtde)
    {
    }

    public void dropActionChanged(DropTargetDragEvent dtde)
    {
    }

    public void dragExit(DropTargetEvent dte)
    {
    }

    public void drop(DropTargetDropEvent dtde)
    {
        // check 
        Transferable t = dtde.getTransferable();
        DropTargetContext dtc = dtde.getDropTargetContext();

        try
        {
            if (t.isDataFlavorSupported(flavorURIList))
            {
                // Check URI(s)
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String urilist = (String) t.getTransferData(flavorURIList);

                List<java.net.URI>uris = URIUtil.parseURIList(urilist,";"); 
                dtde.getDropTargetContext().dropComplete(true);

                if ((uris!=null) && (uris.size()>0))
                	uriDropTargetListener.notifyDnDDrop(uris.get(0).toString());
                dtde.dropComplete(true);
            }
            else if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                // Convert to text: 
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String txt = (String) t.getTransferData(DataFlavor.stringFlavor);
                dtde.getDropTargetContext().dropComplete(true);
                uriDropTargetListener.notifyDnDDrop(txt);
                dtde.dropComplete(true);
            }
            
        }
        catch (UnsupportedFlavorException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        dtde.dropComplete(false);

    }

}
