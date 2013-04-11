/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: TestSkelFSBrowser.java,v 1.2 2011-05-02 13:36:11 ptdeboer Exp $  
 * $Date: 2011-05-02 13:36:11 $
 */ 
// source: 
package test;

import nl.nlesc.vlet.vfs.VDir;
import nl.nlesc.vlet.vfs.VFSClient;
import nl.nlesc.vlet.vfs.VFSNode;

public class TestOctopusVBrowser
{
	
	public static void main(String args[]) throws Exception
    {
	    testGetDir(); 
	    
	    // The VBrowser classes must be in the classpath to be able to start this. 
        nl.nlesc.vlet.gui.startVBrowser.main(args);
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
