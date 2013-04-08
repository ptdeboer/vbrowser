package nl.vbrowser.ui.dnd;
///*
// * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
// * 
// * Licensed under the Apache License, Version 2.0 (the "License").  
// * You may not use this file except in compliance with the License. 
// * For details, see the LICENCE.txt file location in the root directory of this 
// * distribution or obtain the Apache Licence at the following location: 
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software 
// * distributed under the License is distributed on an "AS IS" BASIS, 
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// * See the License for the specific language governing permissions and 
// * limitations under the License.
// * 
// * See: http://www.vl-e.nl/ 
// * See: LICENCE.txt (located in the root folder of this distribution). 
// * ---
// * $Id: ViewNodeTransferable.java,v 1.1 2012/11/18 13:20:35 piter Exp $  
// * $Date: 2012/11/18 13:20:35 $
// */ 
//// source: 
//
//package nl.piter.ptk.ui.vb2.dnd;
//
//import java.awt.datatransfer.DataFlavor;
//import java.awt.datatransfer.Transferable;
//import java.awt.datatransfer.UnsupportedFlavorException;
//import java.io.File;
//import java.util.Vector;
//
//import nl.nlesc.ptk.io.FSNode;
//import nl.nlesc.ptk.net.VRI;
//import nl.piter.ptk.ui.vb2.model.ViewNode;
//
//
///**
// * Generic DnD (Drag and Drop) class. 
// * Custom VRL Transferable class
// */
//public class ViewNodeTransferable implements Transferable
//{
//    ViewNode nodes = null;
//    
//    public ViewNodeTransferable(ViewNode location)
//    {
//        this.nodes = location;
//    }
//
//    public Object getTransferData(DataFlavor flavor)
//            throws UnsupportedFlavorException
//    {
//        if (!isDataFlavorSupported(flavor))
//        {
//            throw new UnsupportedFlavorException(flavor);
//        }
//        else if (flavor.equals(DnDData.ViewNodeDataFlavor))
//        {
//            return nodes;
//        }
//        else if (flavor.equals(DnDData.ViewNodeListDataFlavor))
//        {
//        	ViewNode refs[]=new ViewNode[1];
//        	refs[0]=nodes;
//        	return refs;
//        }
//        //
//        // KDE drag and drop: asks for URIs  
//        //
//        else if (flavor.equals(DnDData.uriListFlavor))
//        {
//            //I can export local file 
//            if ((isLocalLocation(nodes.getVRI()) == true)
//                    && (nodes.getVRI().getScheme().compareTo(FSNode.FILE_SCHEME) == 0))
//            {
//                // create local file path: 
//                return "file://" + nodes.getVRI().getPath();
//            }
//
//            else
//            {
//            	return nodes.getVRI().toString(); // toURI().toString();
//            }
//        }
//        else if (flavor.equals(DataFlavor.stringFlavor))
//        {
//            // always support string flavors
//            return  nodes.getVRI().toString();
//        }
//
//        else if (flavor.equals(DataFlavor.javaFileListFlavor))
//        {
//            java.util.Vector<File> fileList = new Vector<File>();
//
//            //I can export local file 
//            if ((isLocalLocation(nodes.getVRI()) == true)
//                    && (nodes.getVRI().getScheme().compareTo(FSNode.FILE_SCHEME) == 0))
//            {
//                File file = new File(nodes.getVRI().getPath());
//                fileList.add(file);
//                return fileList;
//            }
//            else
//            {
//                DnDUtil.errorPrintf("Cannot export remote file as local file flavor:%s\n",nodes);
//                ;// cannot export remote file as local files ! 
//            }
//
//            return null;
//        }
//        else if (flavor.equals(DnDData.octetStreamDataFlavor))
//        {
//            DnDUtil.infoPrintf("getTransferData(): get: octetStreamDataFlavor!!\n");
//        }
//
//        throw new UnsupportedFlavorException(flavor);
//    }
//
//    private boolean isLocalLocation(VRI vri)
//    {
//        // localhost, file:/, etc: 
//        return vri.isLocalLocation(); 
//    }
//
//    public DataFlavor[] getTransferDataFlavors()
//    {
//        return DnDData.dataFlavorsVRI;
//    }
//
//    public boolean isDataFlavorSupported(DataFlavor flavor)
//    {
//        DnDUtil.debugPrintf("isDataFlavorSupported:%s\n",flavor); 
//
//        for (DataFlavor flav : DnDData.dataFlavorsVRI)
//            if (flav.equals(flavor))
//                return true;
//
//        // return true; 
//        return false;
//    }
//
//}