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

import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;

abstract public class TestVFS_SRM extends TestVFS
{

    static
    {
        try
        {
            initSRM();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
    }
    
    public static VFSClient initSRM() throws Exception
    {
        //if (vfs!=null)
        //    return vfs; 
                
        VletConfig.init();
        VRS.getRegistry().registerVRSDriverClass(nl.esciencecenter.vlet.vfs.gftp.GftpFSFactory.class);
        VRS.getRegistry().registerVRSDriverClass(nl.esciencecenter.vlet.vfs.srm.SRMFSFactory.class);
        
        VFSClient vfs=VFSClient.getDefault(); 
        return vfs; 
    }
    
    /** SRM does not support URI encoding ! */
    boolean getTestEncodedPaths()
    {
        return false; 
    }
    
    /** Not strange chars please */ 
    boolean getTestStrangeCharsInPaths()
    {
        return false;
    }
    
}
