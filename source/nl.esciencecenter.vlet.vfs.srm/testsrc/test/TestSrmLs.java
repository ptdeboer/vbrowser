package test;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.glite.lbl.srm.SRMClientV2;
import nl.nlesc.vlet.vfs.srm.SRMFSFactory;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vfs.VFSClient;
import nl.nlesc.vlet.vrs.vfs.VFSNode;
import nl.nlesc.vlet.vrs.vfs.VFile;

public class TestSrmLs
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

            VRL dirVrl = new VRL("srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/pvier/ptdeboer/bigdir/");

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
