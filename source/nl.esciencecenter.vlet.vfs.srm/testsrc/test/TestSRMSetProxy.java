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
