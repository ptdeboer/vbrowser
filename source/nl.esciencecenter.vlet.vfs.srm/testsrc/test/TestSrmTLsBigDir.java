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

import nl.esciencecenter.glite.lbl.srm.SRMClientV2;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vfs.srm.SRMFSFactory;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.vlet.vrs.vfs.VFile;

public class TestSrmTLsBigDir
{

    public static void main(String args[])
    {

        ClassLogger srmLogger = ClassLogger.getLogger(SRMClientV2.class);
        SRMClientV2.setLogger(srmLogger);
        srmLogger.setLevelToDebug();

        try
        {
            VletConfig.init();
            VRS.getRegistry().registerVRSDriverClass(SRMFSFactory.class);

            VRSContext context = new VRSContext();
            VFSClient vfs = new VFSClient(context);

            VRL dirVrl = new VRL("srm://srm-t.grid.sara.nl:8443/pnfs/grid.sara.nl/data/dteam/ptdeboer/bigdir/");

            if (vfs.existsDir(dirVrl) == false)
            {
                System.out.println("Creating dir:" + dirVrl);
                vfs.mkdirs(dirVrl);
            }

            int numFiles = 1000;
            String filePref = "test_file";

            boolean createFiles = false;
            // boolean createFiles=true;

            if (createFiles)
                for (int i = 300; i < numFiles; i++)
                {
                    VRL fileVrl = dirVrl.appendPath(filePref + i);

                    if (vfs.existsFile(fileVrl))
                    {
                        System.out.println("keeping file:" + fileVrl);
                    }
                    else
                    {
                        System.out.println("creating file:" + fileVrl);
                        VFile result = vfs.createFile(fileVrl, true);
                    }
                }

            // single thread:
            testLS(vfs, dirVrl);

            // 10 threads:
            // for (int i=0;i<10;i++)
            // bgTestLS(vfs,dirVrl);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("=== END ===");

    }

    static void bgTestLS(final VFSClient vfs, final VRL dirVrl)
    {
        Runnable task = new Runnable()
        {
            public void run()
            {
                try
                {
                    testLS(vfs, dirVrl);
                }
                catch (VrsException e)
                {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();

    }

    private static void testLS(final VFSClient vfs, final VRL dirVrl) throws VrsException
    {
        VFSNode[] nodes = vfs.list(dirVrl);
        System.out.printf(" - num nodes=%d\n", nodes.length);

        // for (VFSNode node:nodes)
        {
            // System.out.println(" - node:"+node);
        }
    }

}
