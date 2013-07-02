/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package test.vrs.vfs;

import org.junit.Before;

import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRS;
//import nl.uva.vlet.gui.dialog.AuthenticationDialog;

/**
 * Test LFC. 
 */
public class TestVFS_LFCsara extends TestVFS
{

    static
    {
        initLocalFS();
    }
    
    public static void initLocalFS()
    {
        try
        {
            VRS.getRegistry().registerVRSDriverClass(nl.esciencecenter.vlet.vfs.lfc.LFCFSFactory.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
    }
    static private ServerInfo info;

    VRL testLoc=null; 
    
    public TestVFS_LFCsara()
    {
        testLoc=new VRL("lfn","lfc.grid.sara.nl",5010,"/grid/nlesc.nl/ptdeboer/testlfc");
            
        // this.doRename=false;
        // this.doWrites=false;
    }

    @Override
    public VRL getRemoteLocation()
    {
        return testLoc; 
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
