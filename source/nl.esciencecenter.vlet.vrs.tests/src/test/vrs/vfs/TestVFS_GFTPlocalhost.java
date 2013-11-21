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

package test.vrs.vfs;

import org.junit.Before;

import test.TestSettings;
import test.TestSettings.TestLocation;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.ServerInfo;
//import nl.uva.vlet.gui.dialog.AuthenticationDialog;

/**
 * TestSuite extends TestVFS class to test GFTP. 
 */
public class TestVFS_GFTPlocalhost extends TestVFS
{
    static private ServerInfo info;

    public TestVFS_GFTPlocalhost()
    {
        // this.doRename=false;
        // this.doWrites=false;
    }

    @Override
    public VRL getRemoteLocation()
    {
        return TestSettings.getTestLocation(TestLocation.VFS_GFTP_LOCALHOST); 
    }

//    public static void authenticate() throws VlException
//    {
//        if (info == null)
//            info = TestSettings.getServerInfoFor(TestSettings.getTestLocation(TestSettings.VFS_SFTP_ELAB_LOCATION), true);
//
//        info.store();
//
//        if (info.hasValidAuthentication() == false)
//        {
//            ServerInfo ans = AuthenticationDialog.askAuthentication("Password for:" + info.getUsername() + "@"
//                    + info.getHostname(), info);
//
//            if (ans == null)
//            {
//                // fail("Authentication Failed!!!");
//            }
//
//            ans.store(); // store in ServerInfo database !
//        }
//    }

    @Before
    public void testSetup()
    {
        
    }

}
