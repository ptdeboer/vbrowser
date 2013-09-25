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

import nl.esciencecenter.vbrowser.vrs.xenon.XenonFSFactory;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSFactory;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import test.TestSettings;


public class TestVFS_XenonLocalFS extends TestVFS
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
        VRS.getRegistry().unregisterVRSDriverClass(nl.esciencecenter.vlet.vrs.vdriver.localfs.LocalFSFactory.class); 
        VRS.getRegistry().registerVRSDriverClass(XenonFSFactory.class);
        
        VFSClient vfs=VFSClient.getDefault(); 
        return vfs; 
    }
    
    @Override
    public VRL getRemoteLocation()
    {
        return TestSettings.getTestLocation(TestSettings.VFS_LOCALFS_LOCATION); 
    }
    
    @Override
    public Class<? extends VRSFactory> getVRSFactoryClass()
    {
        return XenonFSFactory.class;
    }

}
