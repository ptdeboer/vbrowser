/*
 * 
 */
package nl.esciencecenter.vbrowser.vb2.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;

/**
 * 	List of ResourceTransferables !  
 */
public class VRIListTransferable implements Transferable
{
    
    public static VRIListTransferable createFrom(ViewNode[] nodes)
    {
        if (nodes==null)
            return null;
        
       int n=nodes.length; 
       List<VRI> vris=new ArrayList<VRI>(n);
       for (int i=0;i<n;i++)
           vris.add(nodes[i].getVRI()); 
       return new VRIListTransferable(vris);
    }
    
    // ========================================================================
    //
    // ========================================================================
    
    List<VRI> vris = null;
    
    public VRIListTransferable(List<VRI> vris)
    {
        this.vris=vris;
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException
    {
    	//System.err.println("getTransferData:"+flavor); 
    	
        if (!isDataFlavorSupported(flavor))
        {
            throw new UnsupportedFlavorException(flavor);
        }
//        else if (flavor.equals(DnDData.VRIFlavor))
//        {
//            return vris.get(0);
//        }
        else if (flavor.equals(DnDData.flavorVRIList))
        {
            return vris;
        }
        //
        // KDE drag and drop: asks for URIs  
        //
        else if ((flavor.equals(DnDData.flavorURIList))
        		 || (flavor.equals(DataFlavor.stringFlavor)))
        {
        	// export as newline separated string 
        	// to mimic KDE's newline separated uriList flavor ! 
        	String sepStr="\n"; 
        	 
        	// String flavor: use ';' as separator ! ; 
        	if (flavor.equals(DataFlavor.stringFlavor))
        		sepStr=";"; 
        	
            //I can export local file 
        	String urisstr="";
        	
        	for (VRI ref:vris)
        	{
        	    VRI vri=ref;
        	    
        		if ( (vri.isLocalLocation())
                    && (vri.getScheme().compareTo(FSNode.FILE_SCHEME) == 0))
        		{
        			// create local file path (leave out hostname!) 
        			urisstr+= "file://" + vri.getPath()+sepStr;
        		}
        		else
        		{
        			urisstr+=vri.toString()+";"; // toURI().toString();
        		}
        	}
        	return urisstr; 
        }
     
        else if (flavor.equals(DataFlavor.javaFileListFlavor))
        {
            java.util.Vector<File> fileList = new Vector<File>();

            for (VRI ref:vris)
            {
                VRI vri=ref;
                
            	if ( (vri.isLocalLocation())
                        && (vri.getScheme().compareTo(FSNode.FILE_SCHEME) == 0))
                {
                    File file = new File(vri.getPath());
                    fileList.add(file);
                }
                else
                {
                    DnDUtil.errorPrintf("Cannot export remote file as local file flavor:%s\n",vri);
                    ;// cannot export remote file as local files ! 
                }
        	}
            return fileList;
        }

        return null;
    }

    public DataFlavor[] getTransferDataFlavors()
    {
        return DnDData.dataFlavorsVRI;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        DnDUtil.debugPrintf("isDataFlavorSupported:%s\n",flavor); 

        for (DataFlavor flav : DnDData.dataFlavorsVRI)
            if (flav.equals(flavor))
                return true;

        // return true; 
        return false;
    }

   

}