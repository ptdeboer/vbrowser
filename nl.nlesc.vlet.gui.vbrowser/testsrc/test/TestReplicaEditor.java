/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: TestReplicaEditor.java,v 1.1 2013/01/22 15:48:05 piter Exp $  
 * $Date: 2013/01/22 15:48:05 $
 */ 
// source: 

package test;

import nl.esciencecenter.ptk.Global;
import nl.nlesc.vlet.gui.panels.resourcetable.ResourceTable;
import nl.nlesc.vlet.gui.viewers.grid.replicaviewer.ReplicaController;
import nl.nlesc.vlet.gui.viewers.grid.replicaviewer.ReplicaDataModel;
import nl.nlesc.vlet.gui.viewers.grid.replicaviewer.ReplicaEditor;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VRS;

public class TestReplicaEditor
{

    public static void main(String args[])
    {
//        Global.getLogger().addDebugClass(ReplicaController.class);
//        Global.getLogger().addDebugClass(ReplicaDataModel.class);
//        Global.getLogger().addDebugClass(ReplicaEditor.class); 
//        Global.getLogger().addDebugClass(ResourceTable.class); 
         
         
        try
        {
            ////VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.lfcfs.LFCFSFactory.class);
           // VRS.getRegistry().addVRSDriverClass(nl.uva.vlet.vfs.srmfs.SRMFSFactory.class);
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        } 

        
        ReplicaEditor rv=new ReplicaEditor(false);
        
        try
        {
            rv.startAsStandAloneApplication(new VRL("lfn://lfc.grid.sara.nl:5010/grid/pvier/piter/testFile.txt"));
         }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
}
