/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.esciencecenter.vbrowser.vb2.ui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;
import nl.esciencecenter.vbrowser.vrs.net.VRL;

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
       List<VRL> vris=new ArrayList<VRL>(n);
       for (int i=0;i<n;i++)
           vris.add(nodes[i].getVRI()); 
       return new VRIListTransferable(vris);
    }
    
    // ========================================================================
    //
    // ========================================================================
    
    List<VRL> vris = null;
    
    public VRIListTransferable(List<VRL> vris)
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
        	
        	for (VRL ref:vris)
        	{
        	    VRL vri=ref;
        	    
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

            for (VRL ref:vris)
            {
                VRL vri=ref;
                
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

    public String toString()
    {
        String str="{vriList:["; 
        
        for (int i=0;i<vris.size();i++)
        {
            str+=vris.get(i);
            if (i+1<vris.size())
                str+=",";
        }
        
        return str+"]}";
                
    }

}