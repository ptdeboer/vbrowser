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

import test.TestSettings;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.vrs.VRS;

/**
 * Test Local case
 * 
 * TestSuite uses testVFS class to tests Local implementation.
 * 
 * @author P.T. de Boer
 */
public class TestVFS_LocalFS extends TestVFS
{
    static
    {
        initLocalFS();
    }
    
    public static void initLocalFS()
    {
        try
        {
            VRS.getRegistry().registerVRSDriverClass(nl.nlesc.vlet.vrs.vdriver.localfs.LocalFSFactory.class);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
    }
    
    @Override
    public VRL getRemoteLocation()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_LOCALFS_LOCATION); 
    }

// Use Junit 4 annotation.
//    public static Test suite()
//    {
//        return new TestSuite(testLocalFS.class);
//    }
//
//    public static void main(String args[])
//    {
//        junit.textui.TestRunner.run(suite());
//    }

}
