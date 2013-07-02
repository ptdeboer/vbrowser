package test;

import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.grid.proxy.GridProxy;
import nl.esciencecenter.vlet.vfs.srm.SRMFSFactory;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFile;

public class TestSRMSetProxy
{

    public static void main(String args[])
    {
        {

            try
            {
                VletConfig.setUsePersistantUserConfiguration(false);

                VRS.getRegistry().initVDriver(SRMFSFactory.class);

                VRSContext context = new VRSContext();
                VFSClient vfs = new VFSClient(context);
                GridProxy proxy = new GridProxy(context);

                VFile tmpProxy = vfs.newFile("file:///tmp/testproxy.tmp");

                // copy original to temporary proxy!
                vfs.copy(vfs.getFile("file:///tmp/testproxy.org"), tmpProxy);

                // load temporary proxy
                proxy.load(tmpProxy.getPath());

                // delete temporary proxy !
                tmpProxy.delete();

                context.setGridProxy(proxy);

                VNode node = vfs.openLocation("srm://srm.grid.sara.nl:8443/");

                System.out.printf("---\nGot node:%s\n---\n", node);

            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

    }

}
