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

import test.TestSettings;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
//import nl.uva.vlet.gui.dialog.AuthenticationDialog;
import nl.nlesc.vlet.vrs.ServerInfo;

/**
 * SRM. 
 * 
 */
public class TestVFS_SRMsara extends TestVFS_SRM
{
    static private ServerInfo info;

    static
    {
        TestVFS_LocalFS.initLocalFS(); 
    }
    
    public TestVFS_SRMsara()
    {
        // this.doRename=false;
        // this.doWrites=false;
    }

    @Override
    public VRL getRemoteLocation()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_SRM_DCACHE_SARA_LOCATION); 
    }

    @Before
    public void testSetup()
    {
        
    }

}
