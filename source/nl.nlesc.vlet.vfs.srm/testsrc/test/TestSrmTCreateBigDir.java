package test;

import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.vfs.srm.SRMFSFactory;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vfs.VFSClient;
import nl.nlesc.vlet.vrs.vfs.VFile;

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
