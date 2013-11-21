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

import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import test.TestSettings;
import test.TestSettings.TestLocation;

public class TestVFS_XenonSftpFS_localhost extends TestVFS
{
    static
    {
        try
        {
            TestVFS_XenonLocalFS.initOctopus();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        
    }
    
    protected void checkAuthentication() throws Exception
    {
        ServerInfo info=this.getVRSContext().getServerInfoFor(this.getRemoteLocation(), true); 
        info.setAttribute(ServerInfo.ATTR_SSH_IDENTITY,"test_rsa"); 
        info.store();
    }
    
    @Override
    public VRL getRemoteLocation()
    {
        return TestSettings.getTestLocation(TestLocation.VFS_SFTP_LOCALHOST_TESTUSER); 
    }

}
