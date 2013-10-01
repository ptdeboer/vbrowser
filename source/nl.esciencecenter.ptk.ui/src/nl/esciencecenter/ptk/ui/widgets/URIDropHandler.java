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
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import nl.esciencecenter.ptk.net.URIUtil;

/** 
 * Handle drops on the Navigation Bar. 
 */
public class URIDropHandler implements DropTargetListener
{
    public static DataFlavor flavorURIList = new DataFlavor("text/uri-list;class=java.lang.String", "uri list");
    
    /** "text/plain" mimetype. */ 
    public static DataFlavor plainText=new DataFlavor("text/plain;representationclass=java.lang.String","plain text"); 
            
    public static DataFlavor javaFileListFlavor = DataFlavor.javaFileListFlavor; 

    public static DataFlavor stringFlavor = DataFlavor.stringFlavor; 

    /** Flavors in order of preference */ 
    public static DataFlavor myFlavors[]={javaFileListFlavor,flavorURIList,stringFlavor};
    
    private URIDropTargetLister uriDropTargetListener;

    public URIDropHandler(URIDropTargetLister uriDropListener)
    {
        this.uriDropTargetListener=uriDropListener;
    }

    public void dragEnter(DropTargetDragEvent dtde)
    {
        // accept/reject DataFlavor
        for (DataFlavor flavor:myFlavors)
        {
            if (dtde.isDataFlavorSupported(flavor))
            {
                dtde.acceptDrag(DnDConstants.ACTION_COPY);
                return ; 
            }
        }
        dtde.rejectDrag(); 
    }

    public void dragOver(DropTargetDragEvent dtde)
    {
        
        // dtde.rejectDrag(); 
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

//        DataFlavor[] flavs = t.getTransferDataFlavors();
//        for (DataFlavor flav:flavs)
//        {
//            System.err.printf(" - %s\n",flav);
//        }
        
        List<java.net.URI> uris = null; 
        
        try
        {

            // Note: 'javaFileListFlavor' might be supported when URIs are dropped 
            // but still won't return correct transfer data as "http" URIs are not Files. 
            // Check URI list first. 
            
            if ( t.isDataFlavorSupported(flavorURIList))
            {
                // Check URI(s)
                debugPrintf(">>>%s\n","flavorURIList");
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String urilist = (String) t.getTransferData(flavorURIList);
                
                debugPrintf(">>>%s\n",urilist);

                uris = URIUtil.parseURIList(urilist,";"); 
                
                dtde.getDropTargetContext().dropComplete(true);

                if ((uris!=null) && (uris.size()>0))
                {
                	uriDropTargetListener.notifyDnDDrop(uris);
                	dtde.dropComplete(true);
                	return;
                }
            }
            else if (t.isDataFlavorSupported(javaFileListFlavor))
            {
            	// (Java) Files dropped native browser: 
                debugPrintf(">>>%s\n","javaFileListFlavor");
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                List<java.io.File> fileList = ( List<java.io.File>) t.getTransferData(javaFileListFlavor);
                uris = new ArrayList<java.net.URI>();

                for (int i=0;i<fileList.size();i++)
                {
                    debugPrintf(">>> adding File:%s\n",fileList.get(i));
                    uris.add(fileList.get(i).toURI());
                }
                
                if ((uris!=null) && (uris.size()>0))
                {
                    uriDropTargetListener.notifyDnDDrop(uris);
                    dtde.dropComplete(true);
                    return;
                }
                
                uris=null; // continue; 
            }
            else if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                // Convert to text: 
                dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
                String txt = (String) t.getTransferData(DataFlavor.stringFlavor);
                dtde.getDropTargetContext().dropComplete(true);
                uris = new ArrayList<java.net.URI>();

                try
                {
                    uris.add(new URI(txt));
                    uriDropTargetListener.notifyDnDDrop(uris);
                    dtde.dropComplete(true);
                    return; 
                }
                catch (URISyntaxException e)
                {
                    // uriDropTargetListener.notifyDnDDropText(txt);
                    dtde.rejectDrop();
                    return; 
                }
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

    private void debugPrintf(String format, Object... args)
    {
        
    }

}
