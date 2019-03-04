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

import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.tasks.VRSTaskMonitor;
import nl.esciencecenter.vlet.vrs.util.VRSResourceLoader;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFile;
import nl.esciencecenter.vlet.vrs.vfs.VFileActiveTransferable;
import nl.esciencecenter.vlet.vrs.vfs.VFileSystem;
import nl.esciencecenter.vlet.vrs.vfs.VFileActiveTransferable.ActiveTransferType;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import test.VTestCase;

public class TestVFS_3rdPartyTransfers extends VTestCase 
{
    protected VRL otherRemoteLocation;
    
    protected VRL remoteLocation;
    
    protected VDir remoteTestDir=null;
    
    protected VDir otherRemoteTestDir=null;
    
    private Object setupMutex=new Object(); 
    
    public VRL getRemoteLocation()
    {
        return remoteLocation; 
    }
    
    public VRL getOtherRemoteLocation()
    {
        return otherRemoteLocation; 
    }
    
    public VDir getRemoteTestDir()
    {
        return remoteTestDir;
    }

    public VDir getOtherRemoteTestDir()
    {
        return otherRemoteTestDir;
    }
    
    @Before // Before the new Setup()!
    public void setUpTestEnv() throws Exception
    {
        verbose(3, "setUp(): Checking remote test location:" + getRemoteLocation());

        synchronized (setupMutex)
        {
            // create/get only if VDir hasn't been fetched/created before !
            if (remoteTestDir == null)
            {

                if (getVFS().existsDir(getRemoteLocation()))
                {
                    remoteTestDir=getVFS().getDir(getRemoteLocation());
                    verbose(3, "setUp(): Using remoteDir:" + getRemoteTestDir());

                }
                else
                {
                    // create complete path !
                    try
                    {
                        verbose(1, "creating new remote test location:" + getRemoteLocation());
                        remoteTestDir=getVFS().mkdirs(getRemoteLocation());
                        verbose(1, "New created remote test directory=" + getRemoteTestDir());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                        throw e;
                    }

                    verbose(1, "created new remote test location:" + getRemoteTestDir());
                }
            }

            if (otherRemoteTestDir == null)
            {
                VRL localdir = getOtherRemoteLocation();

                if (getVFS().existsDir(localdir))
                {
                    otherRemoteTestDir = getVFS().getDir(localdir);
                    // localTempDir.delete(true);
                }
                else
                {
                    // create complete path !
                    otherRemoteTestDir = getVFS().mkdirs(localdir, true);
                    verbose(1, "created new local other test location:" + otherRemoteTestDir);
                }
            }
        }
    }

    
    
    @Test
    public void testX3rdPartySameServer() throws Exception
    {
        VRL loc1 = getRemoteLocation();

        VRL loc2 = getRemoteLocation();

        // same location but different names
        testX3rdPartyTransfer(loc1, loc2, "3rdPartyFile1", "3rdPartyFile2", true);

    }

    @Test
    public void testX3rdPartySameServerReverse() throws Exception
    {
        VRL loc1 = getRemoteLocation();

        VRL loc2 = getRemoteLocation();

        // same location but different names
        testX3rdPartyTransfer(loc1, loc2, "3rdPartyFile3", "3rdPartyFile4", false);

    }

    @Test 
    public void testX3rdPartyTwoServers() throws Exception
    {
        VRL loc1 = getRemoteLocation();

        VRL loc2 = getOtherRemoteLocation();
        if (loc2 == null)
        {
            message("testX3rdPartyTwoServers: skipping 3rd party test: No other location configured");
            return;
        }
        // same location but different names
        testX3rdPartyTransfer(loc1, loc2, "3rdPartyFile5", "3rdPartyFile6", true);
    }

    @Test
    public void testX3rdPartyTwoServersReverse() throws Exception
    {
        VRL loc1 = getRemoteLocation();

        VRL loc2 = getRemoteLocation();
        if (loc2 == null)
        {
            message("testX3rdPartyTwoServers: skipping 3rd party test: No other location configured");
            return;
        }
        // same location but different names
        testX3rdPartyTransfer(loc1, loc2, "3rdPartyFile7", "3rdPartyFile8", false);
    }

    // // need two different test locations for this:
    // public void test2Servers() throws Exception
    // {
    // VRL loc1=TestSettings.testGFTPLocation;
    // VRL loc2=TestSettings.testGFTPLocation2;
    //      
    // // different locations
    // testX3rdPartyTransfer(loc1,loc2,"file1","file2");
    // }
    //    
    // public void test2ServersReverse() throws Exception
    // {
    // VRL loc1=TestSettings.testGFTPLocation;
    // VRL loc2=TestSettings.testGFTPLocation2;
    //      
    // // different locations
    // testX3rdPartyTransfer(loc2,loc1,"file3","file4");
    // }

    public void testX3rdPartyTransfer(VRL sourceDir, VRL targetDir, String sourceFilename, String targetFilename,
            boolean sourceIsActiveParty) throws Exception
    {
        VDir dir1 = getVFS().mkdir(sourceDir, true);
        VDir dir2 = getVFS().mkdir(targetDir, true);

        String contents = "Test 3rd party transfer\n";
        // checks also wether a file can be created: 
        VFile sourceFile = dir1.createFile(sourceFilename);
        VRSResourceLoader writer=new VRSResourceLoader(sourceFile.getVRSContext());
        writer.writeTextTo(sourceFile,contents, true); 
        
        VRL sourceVRL = sourceFile.getVRL();
        VRL targetVRL = targetDir.appendPath(targetFilename);
        VFileSystem sourceVFS=sourceFile.getFileSystem(); 
        VFileSystem targetVFS=dir2.getFileSystem(); 

        // Target File: Does not exist. 
        VFile newTargetFile = targetVFS.newFile(targetVRL);

        StringHolder explanation = new StringHolder();

        boolean skipTest = false;

        if (sourceIsActiveParty)
        {
            if ((sourceVFS instanceof VFileActiveTransferable) == false)
            {
                message("Skipping test: Third party transfers not supported by source FileSystem:" + sourceVFS);
                skipTest = true;
            }
            // fail("GridFTP file should be VThirdPartyTransferable resources");
            else
            {
                ActiveTransferType check = ((VFileActiveTransferable) sourceVFS).canTransferTo(sourceFile,targetVRL, explanation);
                if (check != ActiveTransferType.ACTIVE_3RDPARTY)
                {
                    message("Skipping test: Third party transfer not possible from source to target:" + targetVRL);
                    skipTest = true;
                    // Assert.assertTrue("A GFTP should always be able to do 3rd party transfers",check);
                }
            }
        }
        else
        {
            if ((newTargetFile instanceof VFileActiveTransferable) == false)
            {
                message("Skipping test: Third party transfer not possible by target file:" + newTargetFile);
                skipTest = true;
            }
            // fail("GridFTP file should be VThirdPartyTransferable resources");
            else
            {
                ActiveTransferType check = ((VFileActiveTransferable) targetVFS).canTransferFrom(newTargetFile,sourceVRL, explanation);
                if (check != ActiveTransferType.ACTIVE_3RDPARTY)
                {
                    message("Skipping test: Third party transfer (target <= source), not possible from source:"
                            + sourceVRL);
                    skipTest = true;
                    // Assert.assertTrue("A GFTP should always be able to do 3rd party transfers",check);
                }
            }
        }

        if (skipTest == true)
        {
            sourceFile.delete();
            return;
        }

        // perform transfer
        VFile resultFile = null;

        VRSTaskMonitor monitor = new VRSTaskMonitor();

        if (sourceIsActiveParty)
        {
            resultFile = ((VFileActiveTransferable) sourceVFS).activeTransferTo(monitor, sourceFile,targetVRL);
        }
        else
        {
            resultFile = ((VFileActiveTransferable) targetVFS).activeTransferFrom(monitor, newTargetFile,sourceVRL);
        }

        Assert.assertNotNull("Result of 3rd party transfer can not be null.", newTargetFile);
        Assert.assertTrue("Resulting new  file should report that it does exists now", newTargetFile.exists());

        // cleanup:

        sourceFile.delete();
        newTargetFile.delete();

    }

    // public void test3rdPartyNotSupported() throws Exception
    // {
    // VFile file1=this.remoteTestDir.createFile("dummyFile123");
    //      
    // if ((file1 instanceof VThirdPartyTransferable)==false)
    // {
    // fail("GridFTP file should be VThirdPartyTransferable resource");
    // }
    //      
    // VRL remoteLocation=new VRL("http://www.vl-e.nl/dummypath");
    // StringHolder explanation=new StringHolder();
    //          
    // boolean
    // check=((VThirdPartyTransferable)file1).canTransferTo(remoteLocation,
    // explanation);
    // message("3rd party canTransferTo() method returned:"+explanation.value);
    // Assert.assertFalse("GridFTP should be not be able to party transfers to HTTP locations",check);
    //          
    //    
    // }

}
