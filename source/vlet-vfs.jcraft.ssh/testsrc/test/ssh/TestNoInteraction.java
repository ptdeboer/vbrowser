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

package test.ssh;

import java.net.MalformedURLException;
import java.net.URL;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.crypt.Secret;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;

public class TestNoInteraction
{
    public static void main(String args[])
    {
        try
        {
            testNoUI();
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        VFS.exit();
    }

    public static void testNoUI() throws VrsException
    {

        try
        {
            VletConfig.setBaseLocation(new URL("http://dummy/url"));
        }
        catch (MalformedURLException ex)
        {
            System.err.print("Exception" + ex);

        }
        // runtime configuration
        VletConfig.setHasUI(false);
        VletConfig.setIsApplet(true);
        VletConfig.setPassiveMode(true);
        VletConfig.setIsService(true);
        VletConfig.setInitURLStreamFactory(false);
        VletConfig.setAllowUserInteraction(false);

        // user configuration
        VletConfig.setUsePersistantUserConfiguration(false);
        // GlobalConfig.setUserHomeLocation(new URL("file:///tmp/myservice"));

        GlobalProperties.init();

        VRL vrl = new VRL("sftp://user@elab.lab.uvalight.net/tmp");

        VFSClient vfs = new VFSClient();
        VRSContext context = vfs.getVRSContext();

        ServerInfo info = context.getServerInfoFor(vrl, true);

        info.setAttribute(ServerInfo.ATTR_DEFAULT_YES_NO_ANSWER, true);
        info.setPassword(new Secret("***".toCharArray()));
        info.store();

        VFSNode node = vfs.openLocation(vrl);
        System.out.println("node=" + node);

    }
}
