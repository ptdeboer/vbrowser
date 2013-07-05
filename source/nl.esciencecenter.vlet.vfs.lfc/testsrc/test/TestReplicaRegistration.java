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

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFile;
import nl.esciencecenter.vlet.vrs.vfs.VReplicatable;

public class TestReplicaRegistration
{
    public static void main(String args[])
    {
        try
        {
            VletConfig.init();
            // VRS.getRegistry().addVRSDriverClass(LFCFSFactory.class);
            // VRS.getRegistry().addVRSDriverClass(SRMFSFactory.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        VRS.exit();

        // testVReplicatable();

        // VRS.exit();

    }

    public static void testVReplicatable()
    {
        VFSClient vfs = new VFSClient();

        try
        {
            VRL rep1 = new VRL("srm://hello.world/bogus/1");
            VRL rep2 = new VRL("srm://hello.world/bogus/2");

            VFile file = vfs.newFile(new VRL("lfn://lfc.grid.sara.nl/grid/pvier/piter/testRegistration"));

            if (file.exists() == false)
                file.create();

            if (file instanceof VReplicatable)
            {
                VReplicatable repFile = ((VReplicatable) file);
                VRL reps[] = repFile.getReplicas();
                println("current replicas (should empty)");
                println(reps);

                if (reps != null)
                {
                    try
                    {
                        repFile.unregisterReplicas(repFile.getReplicas());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                VRL newReps[] = new VRL[1];
                newReps[0] = rep1;
                repFile.registerReplicas(newReps);
                reps = repFile.getReplicas();
                println("-- Added on replica ---");
                println(reps);

                newReps[0] = rep2;
                repFile.registerReplicas(newReps);
                reps = repFile.getReplicas();
                println("-- Added another replica ---");
                println(reps);

                VRL delReps[] = new VRL[2];
                delReps[0] = rep1;
                delReps[1] = rep2;

                repFile.unregisterReplicas(delReps);
                reps = repFile.getReplicas();
                println("-- Deleted replicas ---");
                println(reps);

            }
        }
        catch (VrsException e)
        {
            e.printStackTrace();
        }

    }

    private static void println(VRL[] reps)
    {
        for (VRL rep : reps)
        {
            System.out.println("Replica : " + rep);
        }
    }

    public static void println(String str)
    {
        System.out.println(str);
    }
}
