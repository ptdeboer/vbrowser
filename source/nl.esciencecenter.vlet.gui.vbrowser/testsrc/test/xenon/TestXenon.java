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

import java.net.URI;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.engine.XenonEngine;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.DirectoryStream;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.RelativePath;

public class TestXenon
{

    public static void main(String args[])
    {
        try
        {
            Xenon oct=createEngine();
            testListDir(oct,new URI("file:/"),"/home/ptdeboer/");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        
    }
    
    private static void testListDir(Xenon oct, URI fsUri,String pathStr) throws Exception
    {
        
        FileSystem fs = oct.files().newFileSystem(fsUri.getScheme(),fsUri.getPath(), null, null); 
        //Assert.assertNotNull("FileSystem is null",fs); 
        
        RelativePath relPath=new RelativePath(pathStr);
        Path path = oct.files().newPath(fs, relPath); 
        DirectoryStream<Path> dirStream = oct.files().newDirectoryStream(path); 
        
        Iterator<Path> iterator = dirStream.iterator(); 
        
        while (iterator.hasNext())
        {
            Path el = iterator.next();
            System.out.printf("> Path:%s\n",el.getRelativePath().getAbsolutePath()); 
        }
        
    }

    public static Xenon createEngine() throws XenonException
    {
        Map<String,String> props=new Hashtable<String,String>();  
        //Credentials octoCredentials = new Credentials(); 
        Xenon engine = XenonEngine.newXenon(props); 
        
        return engine; 
        
    }
    
}
