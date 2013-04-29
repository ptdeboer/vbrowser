package test;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.vrs.vfs.VFSClient;
import nl.nlesc.vlet.vrs.vfs.VFile;
import nl.nlesc.vlet.vrs.vfs.VReplicatable;
import nl.nlesc.vlet.vrs.vrl.VRL;
import nl.nlesc.vlet.vrs.VRS;

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
