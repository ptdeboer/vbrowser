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

package test;

import java.util.Vector;

import nl.esciencecenter.glite.lbl.srm.SRMClientV2;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vfs.srm.SRMFSFactory;
import nl.esciencecenter.vlet.vfs.srm.SRMFileSystem;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;

public class TestSrmMultiLs
{

    public static void main(String args[])
    {
        ClassLogger srmLogger = ClassLogger.getLogger(SRMClientV2.class);
        SRMClientV2.setLogger(srmLogger);
        srmLogger.setLevelToDebug();

        try
        {
            // Global.init();
            VRS.getRegistry().registerVRSDriverClass(SRMFSFactory.class);

            VRSContext context = new VRSContext();
            VFSClient vfs = new VFSClient(context);

            VRL dirVrl = new VRL("srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/pvier");

            if (vfs.existsDir(dirVrl) == false)
            {
                System.out.println("Creating dir:" + dirVrl);
                vfs.mkdirs(dirVrl);
            }

            SRMFileSystem srmFs = (SRMFileSystem) vfs.openFileSystem(dirVrl);
            Vector<String> paths = new Vector<String>();
            paths.add(dirVrl.getPath());

            int level = 3;

            do
            {
                System.out.println("===============================================\n");
                System.out.println("*** Level=" + level + " ***\n");
                System.out.println("===============================================\n");

                System.out.println(" === Paths === \n");

                String arr[] = new String[paths.size()];
                arr = paths.toArray(arr);
                for (String path : paths)
                    System.out.println(" - path=" + path);

                System.out.println(" === Nodes === \n");

                VFSNode[] nodes = srmFs.listPaths(arr, true, 1, 900);

                paths.clear(); // clear previous level

                if (nodes == null)
                {
                    System.out.println("*** NULL NODES ***\n");
                }
                else
                {
                    System.out.printf("*** num nodes= #%d ***\n", nodes.length);

                    for (VFSNode node : nodes)
                    {
                        if (node.isDir())
                        {
                            paths.add(node.getPath());
                        }

                        // System.out.println(" - node:"+node);
                    }
                }

                level--;

            } while (level > 0);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println(" === END === \n");
    }

}
