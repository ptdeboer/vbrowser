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
// * $Id: ViewNodeListTransferable.java,v 1.1 2012/11/18 13:20:35 piter Exp $  
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
// * 	List of ResourceTransferables !  
// */
//public class ViewNodeListTransferable implements Transferable
//{
//    ViewNode refs[] = null;
//    
//    public ViewNodeListTransferable(ViewNode locs[])
//    {
//        this.refs=locs;
//    }
//
//    public Object getTransferData(DataFlavor flavor)
//            throws UnsupportedFlavorException
//    {
//    	//System.err.println("getTransferData:"+flavor); 
//    	
//        if (!isDataFlavorSupported(flavor))
//        {
//            throw new UnsupportedFlavorException(flavor);
//        }
//        else if (flavor.equals(DnDData.ViewNodeDataFlavor))
//        {
//            return refs[0];
//        }
//        else if (flavor.equals(DnDData.ViewNodeListDataFlavor))
//        {
//            return refs;
//        }
//        //
//        // KDE drag and drop: asks for URIs  
//        //
//        else if ((flavor.equals(DnDData.uriListFlavor))
//        		 || (flavor.equals(DataFlavor.stringFlavor)))
//        {
//        	// export as newline separated string 
//        	// to mimic KDE's newline separated uriList flavor ! 
//        	String sepStr="\n"; 
//        	 
//        	// String flavor: use ';' as separator ! ; 
//        	if (flavor.equals(DataFlavor.stringFlavor))
//        		sepStr=";"; 
//        	
//            //I can export local file 
//        	String urisstr="";
//        	
//        	for (ViewNode ref:refs)
//        	{
//        		VRI vrl=ref.getVRI(); 
//        		
//        		if ( (vrl.isLocalLocation())
//                    && (vrl.getScheme().compareTo(FSNode.FILE_SCHEME) == 0))
//        		{
//        			// create local file path (leave out hostname!) 
//        			urisstr+= "file://" + vrl.getPath()+sepStr;
//        		}
//        		else
//        		{
//        			urisstr+=vrl.toString()+";"; // toURI().toString();
//        		}
//        	}
//        	return urisstr; 
//        }
//     
//        else if (flavor.equals(DataFlavor.javaFileListFlavor))
//        {
//            java.util.Vector<File> fileList = new Vector<File>();
//
//            for (ViewNode ref:refs)
//        	{
//            	VRI vrl=ref.getVRI(); 
//            	
//            	if ( (vrl.isLocalLocation())
//                        && (vrl.getScheme().compareTo(FSNode.FILE_SCHEME) == 0))
//                {
//                    File file = new File(vrl.getPath());
//                    fileList.add(file);
//                }
//                else
//                {
//                    DnDUtil.errorPrintf("Cannot export remote file as local file flavor:%s\n",vrl);
//                    ;// cannot export remote file as local files ! 
//                }
//        	}
//            return fileList;
//        }
//
//        return null;
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