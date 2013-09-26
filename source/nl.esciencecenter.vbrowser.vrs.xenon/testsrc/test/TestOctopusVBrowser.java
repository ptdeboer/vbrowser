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

package test;

import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;

public class TestOctopusVBrowser
{
	
	public static void main(String args[]) throws Exception
    {
	    
	    // The VBrowser classes must be in the classpath to be able to start this. 
        nl.esciencecenter.vlet.gui.startVBrowser.main(args);
        
        testGetDir(); 

    }

    private static void testGetDir() throws Exception
    {
        VFSClient vfs=TestOctopusFS.initOctopusVFS(); 
        
        VDir dir = vfs.getDir("file:/home/ptdeboer/test"); 
        
        VFSNode[] nodes = dir.list(); 
        
        System.out.printf(">>> Dir:"+dir); 
        for (int i=0;i<nodes.length;i++)
        {
            System.out.printf(" - node[#%d] =%s\n",i,nodes[i]);
        }

        
    }


}
