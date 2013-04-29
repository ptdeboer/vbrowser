package test;

import java.util.ArrayList;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.util.bdii.BdiiUtil;
import nl.nlesc.vlet.util.bdii.StorageArea;
import nl.nlesc.vlet.vfs.srm.SRMDir;
import nl.nlesc.vlet.vfs.srm.SRMFSFactory;
import nl.nlesc.vlet.vfs.srm.SRMFile;
import nl.nlesc.vlet.vfs.srm.SRMFileSystem;
import nl.nlesc.vlet.vrs.VNode;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vfs.FileWriter;
import nl.nlesc.vlet.vrs.vfs.VDir;
import nl.nlesc.vlet.vrs.vfs.VUnixFileMode;

public class BenchmarkSRM
{

    private static SRMFSFactory srmFacktory;

    private static VRSContext context;

//    private static Map<String, String> resultMap = new HashMap<String, String>();

    private static final String SE_NAME_TITLE = "SE";

    private static final String BACKEND_TYPE_TITLE = "Backend Type";

    private static final String BACKEND_VERSION_TITLE = "Backend Version";

    private static final String CHECKSUM_TYPE_TITLE = "Checksum Type";

    private static final String ACL_FOR_FILES_TITLE = "Suport For File ACL";

    private static final String ACL_FOR_DIR_TITLE = "Suport For Dir ACL";

    private static final String PUT_OVERHEAD_TITLE = "Put Overhead";

    private static final String THIRD_PARTY_COPY_TITLE = "3rdParty";

    private static final String[] KEYS = { SE_NAME_TITLE, BACKEND_TYPE_TITLE, BACKEND_VERSION_TITLE,
            CHECKSUM_TYPE_TITLE, ACL_FOR_FILES_TITLE, ACL_FOR_DIR_TITLE, PUT_OVERHEAD_TITLE, THIRD_PARTY_COPY_TITLE };
    
    
    

    public static void main(String args[])
    {
        try
        {
            context = VRSContext.getDefault();
            srmFacktory = new SRMFSFactory();
            
            
            new BenchmarkSRM().testAllSE(); 
            
            

            VRS.exit();
            Thread.sleep(3500);

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.exit(0);
    }

    public void testAllSE()
    {
        ArrayList<StorageArea> Sas = null;
        try
        {
            Sas = BdiiUtil.getBdiiService(context).getSRMv22SAsforVO(context.getVO());
        }
        catch (VrsException e)
        {
            e.printStackTrace();
        }

        VRL seVRL = null;

        SRMDir testDir = null;
        int numOfBulkFiles = 4;
        StringBuffer results = new StringBuffer();
        int[] fileModes = { 33206, 33279, 33152 };
        
        SRMFile[] bulkFiles = new SRMFile[numOfBulkFiles];

        for (int i = 0; i < Sas.size(); i++)
        {

            seVRL = Sas.get(i).getVOStorageLocation();

            // message("VRL: " + seVRL);
            results.append(seVRL.getHostname()+"\t");
            
            SRMFileSystem srmFS;
            try
            {
                srmFS = (SRMFileSystem) srmFacktory.openFileSystem(context, seVRL);

                testDir = createTestDir(srmFS, seVRL);

                results.append(getBackendType(srmFS)+"\t");
                // message("------------------Type: " + backendType);

                results.append(getBackendVersion(srmFS)+"\t");
                // message("------------------Version: " + backendVersion);

                SRMFile[] testFiles = createTestFiles(numOfBulkFiles, testDir);

                results.append(getChecksumTypes(testFiles[0])[0]+"\t");

                results.append(supportsACL(testFiles[0], fileModes)+"\t");

                results.append(supportsACL(testDir, fileModes)+"\t");

                results.append(testOverhead(numOfBulkFiles, testDir, bulkFiles)+"\t");

                StringBuffer thirdPartyCopy = new StringBuffer();
                for (int j = 0; j < Sas.size(); j++)
                {
                    VRL seVRLNext = Sas.get(j).getVOStorageLocation();

                     message("---- Copying from: "+seVRL.getHostname()+" to: "+seVRLNext.getHostname());

                    SRMFileSystem srmFSNext = (SRMFileSystem) srmFacktory.openFileSystem(context, seVRLNext);
                    SRMDir nextTestDir = createTestDir(srmFSNext, seVRLNext);
                    long thirdrdPartyCopyTime = do3rdPartyCopy(srmFS, bulkFiles, nextTestDir.getVRL());
                    thirdPartyCopy.append(seVRL.getHostname() + "->" + seVRLNext.getHostname() + " = " + thirdrdPartyCopyTime);
                    if(j<Sas.size()-1){
                        thirdPartyCopy.append(",");
                    }

                }
                results.append(thirdPartyCopy.toString()+"\t");
                results.append("\n");
                
                message(((i * 100.0) / Sas.size()) + " Done");

            }
            catch (VrsException e)
            {
                e.printStackTrace();
                results.append(e.getMessage()+"\n");
                // continue;
            }
        }

        printResults(results);

    }

    private static void printResults(StringBuffer results)
    {
        StringBuffer title = new StringBuffer();
        for (int i = 0; i < KEYS.length; i++)
        {
                title.append(KEYS[i] + "\t");
        }
        title
                .append("\n-----------------------------------------------------------------------------------------------------------------------");
        
        
        message(title.toString()+"\n"+results.toString());
    }

    private static SRMFile[] createTestFiles(int numOfBulkFiles, VDir testDir) throws VrsException
    {
        SRMFile[] bulkFiles = new SRMFile[numOfBulkFiles];
        for (int j = 0; j < numOfBulkFiles; j++)
        {
            bulkFiles[j] = (SRMFile) testDir.newFile("chsFile" + j);
            bulkFiles[j].create();
            new FileWriter(bulkFiles[j]).setContents("This test contents");
        }
        return bulkFiles;
    }

    private static SRMDir createTestDir(SRMFileSystem srmFS, VRL testLoc) throws VrsException
    {
        SRMDir testDir = (SRMDir) srmFS.newDir(testLoc.appendPath("spiros_test_delete_me").getPath());
        if (!testDir.exists())
        {
            testDir.create();
        }

        return testDir;

    }

    private static long do3rdPartyCopy(SRMFileSystem srmFS, SRMFile[] bulkFiles, VRL destDir)
    {
        VRL[] sourceVrls = new VRL[bulkFiles.length];
        VRL[] destVrls = new VRL[bulkFiles.length];

        long time = -99;
        for (int i = 0; i < bulkFiles.length; i++)
        {
            sourceVrls[i] = bulkFiles[i].getVRL();
            destVrls[i] = destDir.appendPath("CopyOf" + bulkFiles[i].getName());
        }
        long start = System.currentTimeMillis();
        try
        {
            srmFS.thirdPartyCopy(sourceVrls, destVrls);
            time = System.currentTimeMillis() - start;
        }
        catch (VrsException e)
        {
            message("Failed to copy files from: " + bulkFiles[0].getHostname() + " to " + destDir.getHostname());
            // e.printStackTrace();

        }
        return time;
    }

    private static double testOverhead(int numOfBulkFiles, VDir testDir, SRMFile[] bulkFiles) throws VrsException
    {
        long start = 0;
        long overhead = 0;
        long overheadSum = 0;
        for (int j = 0; j < numOfBulkFiles; j++)
        {
            start = System.currentTimeMillis();
            bulkFiles[j] = (SRMFile) testDir.newFile("chsOverheadFiles" + j);
            new FileWriter(bulkFiles[j]).setContents(new byte[] { '1' });
            overhead = System.currentTimeMillis() - start;
            overheadSum += overhead;
        }
        return (overheadSum / (double) numOfBulkFiles);
    }

    private static boolean supportsACL(VNode node, int[] testModes) throws VrsException
    {
        if (node instanceof VUnixFileMode)
        {
            VUnixFileMode unixFile = (VUnixFileMode) node;
            unixFile.setMode(testModes[0]);

            int actualMode = unixFile.getMode();

            if (testModes[0] != actualMode)
            {
                return false;
            }

            // test next node
            unixFile.setMode(testModes[1]);
            actualMode = unixFile.getMode();

            if (testModes[1] != actualMode)
            {
                return false;
            }

            // set r---
            unixFile.setMode(testModes[2]);

            actualMode = unixFile.getMode();

            // now somehow test if another user can read the file/dir
            if (node instanceof VDir && testModes[2] == 16820)
            {
                VDir dir = (VDir) node;

                try
                {
                    // should get permition denied
                    dir.createFile("someFile.txt");
                }
                catch (Exception ex)
                {
                    if (!(ex instanceof nl.nlesc.vlet.exception.ResourceAccessDeniedException))
                    {
                        return false;
                    }
                }
                finally
                {
                    // set rwx rw- r--
                    int mode = 16884;

                    actualMode = unixFile.getMode();
                    if (mode != actualMode)
                    {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private static String[] getChecksumTypes(SRMFile chFile) throws VrsException
    {
        String[] types = null;
        types = chFile.getChecksumTypes();
        if (types == null || types.length < 1)
        {
            types = new String[] { "No Checksum returned" };
        }
        return types;
    }

    private static String getBackendVersion(SRMFileSystem srmFS) throws VrsException
    {
        String version = null;
        version = srmFS.getBackendVersion();
        return version;
    }

    private static void message(String msg)
    {
        System.err.println("" + msg);
    }

    private static String getBackendType(SRMFileSystem srmFS) throws VrsException
    {
        String type = null;
        type = srmFS.getBackendType();

        return type;
    }

}
