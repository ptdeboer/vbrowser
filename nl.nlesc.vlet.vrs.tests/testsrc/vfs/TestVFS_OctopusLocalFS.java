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

package vfs;

import test.TestSettings;
import nl.esciencecenter.vbrowser.vrs.octopus.OctopusFSFactory;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.vfs.VFSClient;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VRS;


public class TestVFS_OctopusLocalFS extends TestVFS
{
    private static final VFSClient vfs=null;
    
    static
    {
        try
        {
            initOctopus();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
    }
    
    public static VFSClient initOctopus() throws Exception
    {
        if (vfs!=null)
            return vfs; 
                
        VletConfig.init();
        VRS.getRegistry().unregisterVRSDriverClass(nl.nlesc.vlet.vdriver.vfs.localfs.LocalFSFactory.class); 
        VRS.getRegistry().registerVRSDriverClass(OctopusFSFactory.class);
        
        VFSClient vfs=VFSClient.getDefault(); 
        return vfs; 
    }
    
    @Override
    public VRL getRemoteLocation()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_LOCALFS_LOCATION); 
    }


}
