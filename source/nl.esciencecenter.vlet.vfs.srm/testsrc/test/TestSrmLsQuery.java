package test;

import nl.esciencecenter.glite.lbl.srm.SRMClientV2;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vfs.srm.SRMDir;
import nl.esciencecenter.vlet.vfs.srm.SRMFSFactory;
import nl.esciencecenter.vlet.vfs.srm.SRMFileSystem;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;

public class TestSrmLsQuery
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

            VRL dirVrl = new VRL(
                    "srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/pvier/user/bigdir/?srmCount=50&srmOffset=50");

            SRMFileSystem srmFs = (SRMFileSystem) vfs.openFileSystem(dirVrl);

            SRMDir dir = (SRMDir) srmFs.openLocation(dirVrl);

            VFSNode[] nodes = dir.list();
            if (nodes == null)
            {
                System.out.println(" NULL Nodes!");
            }
            else
                for (VFSNode node : nodes)
                {
                    System.out.println(" - node:" + node);
                }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("=== END ===");

    }

}
