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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Vector;

import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.vbrowser.vb2.ui.model.ViewNode;

public class DnDData
{
//    public static class ViewNodeList extends ArrayList<ViewNode> {};
//    
//    public static DataFlavor ViewNodeDataFlavor = new DataFlavor(ViewNode.class, "ViewNode class");
//
//    public static DataFlavor ViewNodeListDataFlavor = new DataFlavor(ViewNodeList.class, "List<ViewNode> class");
//    
    public static class VRIList extends ArrayList<VRI> {};
    
    /** One ore more URIstrings seperated by a ';' */
    public static DataFlavor flavorString = DataFlavor.stringFlavor;

    public static DataFlavor flavorJavaFileList = DataFlavor.javaFileListFlavor;

    /**
     * In KDE this is a newline seperators list of URIs
     */
    public static DataFlavor flavorURIList = new DataFlavor("text/uri-list;class=java.lang.String", "uri list");

    public static DataFlavor octetStreamDataFlavor = new DataFlavor(
            "application/octet-stream;class=java.io.InputStream", "octetStream");

//  public static DataFlavor VRIFlavor = new DataFlavor(VRI.class, "VRI class");

  public static DataFlavor flavorVRIList = new DataFlavor(VRIList.class, "(Array)List<VRI> class");

    
    public static DataFlavor[] dataFlavorsVRI = new DataFlavor[]
            { 
//                ViewNodeDataFlavor, 
//                ViewNodeListDataFlavor,
                //VRIFlavor,
                flavorVRIList,
                flavorJavaFileList, 
                flavorURIList, 
                flavorString, 
            };

    // ========================================================================
    //
    // ========================================================================
   

    // === Object === //

    /**
     * DataFlavors from which VRL(s) can be imported !
     */

    public static boolean canConvertToVRIs(Transferable t)
    {
        // DataFlavor flavors[]=t.getTransferDataFlavors();

        for (DataFlavor flav : dataFlavorsVRI)
            if (t.isDataFlavorSupported(flav))
                return true;

        // return true;
        return false;
    }

//    public static boolean isVRIListFlavor(DataFlavor[] flavors)
//    {
//        DataFlavor[] dataFlavors = dataFlavorsVRI;
//
//        for (int i = 0; i < flavors.length; i++)
//        {
//            for (int j = 0; j < dataFlavors.length; j++)
//
//                if (dataFlavors[j].equals(flavors[i]))
//                {
//                    return true;
//                }
//
//        }
//        return false;
//        // return true;
//    }

    public static List<VRI> getVRIsFrom(Transferable t) throws VRISyntaxException, UnsupportedFlavorException,
            IOException
    {
        // Known URI/File type flavors:

//        if (t.isDataFlavorSupported(DnDData.ViewNodeListDataFlavor))
//        {
//            // II: get data:
//            ViewNode refs[] = (ViewNode[]) t.getTransferData(DnDData.ViewNodeListDataFlavor);
//            return toVRIs(refs);
//        }
//        else if (t.isDataFlavorSupported(DnDData.ViewNodeDataFlavor))
//        {
//            // II: get data:
//            ViewNode refs[] = new ViewNode[1];
//            refs[0] = (ViewNode) t.getTransferData(DnDData.ViewNodeDataFlavor);
//            return toVRIs(refs);
//        }
        if (t.isDataFlavorSupported(DnDData.flavorVRIList))
        {
            // II: get data:
            List<VRI> vris = (List<VRI>) t.getTransferData(DnDData.flavorVRIList);
            return vris; 
        }
//        else if (t.isDataFlavorSupported(DnDData.VRIFlavor))
//        {
//            // II: get data:
//            List<VRI> vris=new ArrayList<VRI>(0); 
//            vris.add((VRI)t.getTransferData(DnDData.VRIFlavor));
//            return vris; 
//        }
        // drops from Windows create these objects (thanks to swing) !:
        else if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
        {
            List<VRI> vris = DnDData.getJavaFileListVRIs(t);
            return vris;
        }
        //
        // DnD Support for KDE: drops uri lists ! (Yay for Ke-De-Yay)
        //
        else if (t.isDataFlavorSupported(DnDData.flavorURIList))
        {
            String urilist = (String) t.getTransferData(DnDData.flavorURIList);

            Scanner scanner = new Scanner(urilist.trim());

            Vector<VRI> vris = new Vector<VRI>();

            while (scanner.hasNextLine())
            {
                String lineStr = scanner.nextLine();

                try
                {
                    vris.add(new VRI(lineStr));
                }
                catch (VRISyntaxException e)
                {
                    DnDUtil.errorPrintf("DnDData: Failed to parse:%s\nException=%s\n", lineStr, e);
                    // Be robust: continue;
                }
            }

            return vris;
        }
        //
        // Default MimeTypes :
        // Todo: DataStreams, etc:

        else if (t.isDataFlavorSupported(DataFlavor.stringFlavor))
        {
            String str = (String) t.getTransferData(DataFlavor.stringFlavor);
            Vector<VRI> vris = new Vector<VRI>();
            vris.add(new VRI(str));
            return vris;

        }

        throw new UnsupportedFlavorException(t.getTransferDataFlavors()[0]);
    }

    private static List<VRI> toVRIs(ViewNode[] refs)
    {
        List<VRI> list=new ArrayList<VRI>(refs.length); 
        for (int i=0;i<refs.length;i++)
            list.add(refs[i].getVRI());
        
        return list; 
    }

    /**
     * Handle Java File List
     * 
     * @throws IOException
     * @throws UnsupportedFlavorException
     */

    public static List<VRI> getJavaFileListVRIs(Transferable t) throws UnsupportedFlavorException, IOException
    {
        java.util.List<File> fileList = (java.util.List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
        Iterator<File> iterator = fileList.iterator();

        int len = fileList.size();

        List<VRI> vris = new ArrayList<VRI>(len);

        while (iterator.hasNext())
        {
            java.io.File file = (File) iterator.next();

            // Debug("name="+file.getName());
            // Debug("url="+file.toURI().toString());
            // Debug("path="+file.getAbsolutePath());

            VRI vrl = new VRI("file", null, file.getAbsolutePath());
            // String type=(file.isDirectory()?VRS.DIR_TYPE:VRS.FILE_TYPE);

            // String
            // mimeType=UIGlobal.getMimeTypes().getMimeType(vrl.getPath());

            vris.add(vrl);
        }

        return vris;
    }

}
