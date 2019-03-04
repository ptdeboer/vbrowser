/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package nl.esciencecenter.vlet.gui.dnd;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.util.Vector;


import nl.esciencecenter.vlet.gui.UILogger;
import nl.esciencecenter.vlet.gui.data.ResourceRef;
import nl.esciencecenter.vlet.vrs.Registry;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vrl.VRLUtil;

/**
 * Generic DnD (Drag and Drop) class. 
 * Custom VRL Transferable class
 */
public class ResourceTransferable implements Transferable
{
    ResourceRef ref = null;
    
    public ResourceTransferable(ResourceRef location)
    {
        this.ref = location;
    }

    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException
    {
        if (!isDataFlavorSupported(flavor))
        {
            throw new UnsupportedFlavorException(flavor);
        }
        else if (flavor.equals(VTransferData.ResourceRefDataFlavor))
        {
            return ref;
        }
        else if (flavor.equals(VTransferData.ResourceRefListDataFlavor))
        {
        	ResourceRef refs[]=new ResourceRef[1];
        	refs[0]=ref;
        	return refs;
        }
        //
        // KDE drag and drop: asks for URIs  
        //
        else if (flavor.equals(VTransferData.uriListFlavor))
        {
            //I can export local file 
            if ((VRLUtil.isLocalLocation(ref.getVRL()) == true)
                    && (ref.getVRL().getScheme().compareTo(VFS.FILE_SCHEME) == 0))
            {
                // create local file path: 
                return "file://" + ref.getVRL().getPath();
            }

            else
            {
            	return ref.getVRL().toString(); // toURI().toString();
            }
        }
        else if (flavor.equals(DataFlavor.stringFlavor))
        {
            // always support string flavors
            return  ref.getVRL().toString();
        }

        else if (flavor.equals(DataFlavor.javaFileListFlavor))
        {
            java.util.Vector<File> fileList = new Vector<File>();

            //I can export local file 
            if ((VRLUtil.isLocalLocation(ref.getVRL()) == true)
                    && (ref.getVRL().getScheme().compareTo(VFS.FILE_SCHEME) == 0))
            {
                File file = new File(ref.getVRL().getPath());
                fileList.add(file);
                return fileList;
            }
            else
            {
                UILogger.errorPrintf(this,"Cannot export remote file as local file flavor:%s\n",ref);
                ;// cannot export remote file as local files ! 
            }

            return null;
        }
        else if (flavor.equals(VTransferData.octetStreamDataFlavor))
        {
            UILogger.infoPrintf(this,"LocationTransferable get: octetStreamDataFlavor!\n");
        }

        throw new UnsupportedFlavorException(flavor);
    }

    public DataFlavor[] getTransferDataFlavors()
    {
        return VTransferData.dataFlavorsVRL;
    }

    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        UILogger.debugPrintf(this,"LocationTransferable is flavor supported:%s\n",flavor); 

        for (DataFlavor flav : VTransferData.dataFlavorsVRL)
            if (flav.equals(flavor))
                return true;

        // return true; 
        return false;
    }

}