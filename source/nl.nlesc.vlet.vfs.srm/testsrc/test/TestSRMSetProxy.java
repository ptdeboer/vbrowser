package test;

import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.grid.proxy.GridProxy;
import nl.nlesc.vlet.vfs.srm.SRMFSFactory;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vfs.VFSClient;
import nl.nlesc.vlet.vrs.vfs.VFile;

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
