///*
// * Copyrighted 2012-2013 Netherlands eScience Center.
// *
// * Licensed under the Apache License, Version 2.0 (the "License").  
// * You may not use this file except in compliance with the License. 
// * For details, see the LICENCE.txt file location in the root directory of this 
// * distribution or obtain the Apache License at the following location: 
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software 
// * distributed under the License is distributed on an "AS IS" BASIS, 
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// * See the License for the specific language governing permissions and 
// * limitations under the License.
// * 
// * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
// * ---
// */
//// source: 
//
//package test;
//
//import nl.esciencecenter.vbrowser.vrs.octopus.OctopusFSFactory;
//import nl.esciencecenter.vlet.VletConfig;
//import nl.esciencecenter.vlet.vrs.VRS;
//import nl.esciencecenter.vlet.vrs.vfs.VDir;
//import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
//import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
//
//public class TestOctopusSftpFS
//{
//    private static VFSClient vfs=null; 
//    
//    public static VFSClient initOctopusVFS() throws Exception
//    {
//        if (vfs!=null)
//            return vfs; 
//        
//        VletConfig.init();
//        VRS.getRegistry().unregisterVRSDriverClass(nl.esciencecenter.vlet.vrs.vdriver.localfs.LocalFSFactory.class); 
//        VRS.getRegistry().registerVRSDriverClass(OctopusFSFactory.class);
//        
//        VFSClient vfs=VFSClient.getDefault(); 
//        return vfs; 
//    }
//    
//    public static void main(String args[]) throws Exception
//    {
//        VFSClient vfs=initOctopusVFS(); 
//        
//        VDir dir = vfs.getDir("sftp://localhost/home/"+VletConfig.getUserName()); 
//
//        System.out.printf(">>> Dir:"+dir); 
//        VFSNode[] nodes = dir.list(); 
//        
//        System.out.printf(">>> Dir.list():\n"); 
//        
//        for (int i=0;i<nodes.length;i++)
//        {
//            System.out.printf(" - node[#%d] =%s\n",i,nodes[i]);
//            System.out.printf(" - node[#%d].getPath() =%s\n",i,nodes[i].getPath());
//
//        }
//        
//    }
//    
//}
