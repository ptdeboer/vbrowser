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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.junit.Assert;

import nl.esciencecenter.octopus.Octopus;
import nl.esciencecenter.octopus.engine.OctopusEngine;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.files.Path;
import nl.esciencecenter.octopus.files.DirectoryStream;
import nl.esciencecenter.octopus.files.Path;
import nl.esciencecenter.octopus.files.FileSystem;
import nl.esciencecenter.octopus.files.Pathname;

public class TestOctopusSftp
{

    public static void main(String args[])
    {
        try
        {
            Octopus oct=createEngine();
            testListDir(oct,new URI("sftp://ptdeboer@localhost"),"/home/ptdeboer/");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        
    }
    
    private static void testListDir(Octopus oct, URI fsUri,String pathStr) throws Exception
    {
        
        FileSystem fs = oct.files().newFileSystem(fsUri, null, null); 
        Assert.assertNotNull("FileSystem is null",fs); 
        
        Pathname relPath=new Pathname(pathStr);
        Path path = oct.files().newPath(fs, relPath); 
        DirectoryStream<Path> dirStream = oct.files().newDirectoryStream(path); 
        
        Iterator<Path> iterator = dirStream.iterator(); 
        
        while (iterator.hasNext())
        {
            Path el = iterator.next();
            System.out.printf("> Path:%s\n",el.getPathname()); 
        }
        
    }

    public static Octopus createEngine() throws OctopusException
    {
        Map<String,String> props=new Hashtable<String,String>();  
        //Credentials octoCredentials = new Credentials(); 
        Octopus engine = OctopusEngine.newOctopus(props); 
        
        return engine; 
        
    }
    
}
