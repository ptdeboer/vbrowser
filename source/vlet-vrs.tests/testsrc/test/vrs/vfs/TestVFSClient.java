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

import junit.framework.Assert;
import junit.framework.TestCase;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFile;

/**
 * Test VFSClient specific methods not tested in VFSTest! 
 * 
 * @author P.T. de Boer
 */
public class TestVFSClient extends TestCase
{
    // VAttribute attribute=null;

    private VFSClient vfsClient = null;

    private VDir testDir = null;

    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     * 
     * @throws VrsException
     */
    protected synchronized void setUp() throws VrsException
    {
        if (vfsClient == null)
            vfsClient = new VFSClient();

        if (testDir == null)
        {
            testDir = vfsClient.createUniqueTempDir("testVFSClient", "junit");
            Message("new testDir:" + testDir);
        }

    }

    private void Message(String msg)
    {
        System.out.println(msg);
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     * 
     * @throws VrsException
     */
    protected void tearDown() throws VrsException
    {

    }

    protected void finalize()
    {
        Message("deleting:" + testDir);

        try
        {
            if (testDir != null)
                testDir.delete();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        testDir = null;
    }

    // check /tmp dir and relative access into /tmp dir
    public void testTempDir() throws VrsException
    {
        VDir tmpdir = vfsClient.getTempDir();

        Assert.assertTrue("Temp dir MUST exist!:" + tmpdir, tmpdir.exists());

        // must have write access:
        VDir newdir = tmpdir.createDir("subdir");
        Assert.assertTrue("Temp dir MUST exist!:" + tmpdir, newdir.exists());

        // must be deletable
        newdir.delete();
        Assert.assertFalse("Temp dir wasn't deleted:" + tmpdir, newdir.exists());

        // create file
        VFile file1 = tmpdir.createFile("file1");

        // check relative access using current working directory
        vfsClient.setWorkingDir(tmpdir);
        VFile file2 = vfsClient.getFile("file1");
        Assert.assertEquals("relative file names do not match", file1.getVRL().toString(), file2.getVRL().toString());

        file1.delete();
        Assert.assertFalse("Temp file was not deleted:" + file1, newdir.exists());

    }

    public void testMkdir() throws VrsException
    {
        VDir subDir = vfsClient.mkdir(testDir.getVRL().appendPath("/aap/noot/mies"), false);
        Assert.assertTrue("New subdirectory doesn't exists:" + subDir, subDir.exists());

        Message("Created:" + subDir);
        subDir.delete();
    }

    // Check user home VRL 
    public void testHomeDir() throws VrsException
    {
        VRL homeVrl = vfsClient.getUserHomeLocation();
        
        String path=GlobalProperties.getGlobalUserHome();
        
        Assert.assertEquals("Home path and UserHomeLocation must be the same",path,homeVrl.getPath());
    }
    

        
}
