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

import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vfs.srm.SRMFSFactory;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFile;

public class TestSrmTCreateBigDir
{

    public static void main(String args[])
    {

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

            int numFiles = 500;
            String filePref = "test_file";

            boolean createFiles = false;

            int start = 99;

            if (createFiles)
                for (int i = start; i < numFiles; i++)
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

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("=== END ===");

    }

}
