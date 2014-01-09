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

package test.xenon;

import nl.esciencecenter.vbrowser.vrs.xenon.XenonFSFactory;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;

public class TestXenonFS
{
    private static VFSClient vfs=null; 
    
    public static VFSClient initXenonVFS() throws Exception
    {
        if (vfs!=null)
            return vfs; 
        
        VletConfig.init();
        VRS.getRegistry().unregisterVRSDriverClass(nl.esciencecenter.vlet.vrs.vdriver.localfs.LocalFSFactory.class); 
        VRS.getRegistry().registerVRSDriverClass(XenonFSFactory.class);
        
        VFSClient vfs=VFSClient.getDefault(); 
        return vfs; 
    }
    
    public static void main(String args[]) throws Exception
    {
        VFSClient vfs=initXenonVFS(); 
        
        VDir dir = vfs.getDir("file:///home/"+VletConfig.getUserName()); 
        
        VFSNode[] nodes = dir.list(); 
        
        System.out.printf(">>> Dir:"+dir); 
        for (int i=0;i<nodes.length;i++)
        {
            System.out.printf(" - node[#%d] =%s\n",i,nodes[i]);
        }
        
    }
    
}
