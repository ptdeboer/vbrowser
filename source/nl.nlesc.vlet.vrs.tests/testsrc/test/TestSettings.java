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

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;


import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;

/**
 * Test configuarion settings. TODO: custom settings.
 * 
 * @author P.T. de Boer
 */
public class TestSettings
{
    public static final String VFS_LOCALFS_LOCATION         = "vfsLocalFS_location1";
    public static final String VFS_LOCAL_TEMPDIR_LOCATION   = "vfsLocalFS_tmpDirLocation"; 
    public static final String VFS_GFTP_V1_LOCATION         = "vfsGFTP_V1_location1"; 
    public static final String VFS_GFTP_LOCATION            ="vfsGFTPLocation"; 
    public static final String VFS_GFTP_LOCATION2           ="vfsGFTPLocation2"; 
    public static final String VFS_GFTP_ELAB_LOCATION       ="vfsGFTPElabLocation"; 
    public static final String VFS_SFTP_SARA_LOCATION       ="vfsSFTPSARALocation"; 
    public static final String VFS_SFTP_LOCALHOST_TESTUSER  ="vfsSFTPLocalhostLocation";
    
    public static final String VFS_SRM_DCACHE_SARA_LOCATION         ="vfsSRM_dCache_SARA_location";
    public static final String VFS_SRM_DCACHE_SARA_OTHER_LOCATION   ="vfsSRM_dCache_SARA_otherLocation";
    public static final String VFS_SRM_DTEAM_DCACHE_SARA_LOCATION   ="vfsSRM_dteam_dCache_SARA_location";
    public static final String VFS_SRM_DPM_NIKHEF_LOCATION          ="vfsSRM_DPM_NIKHEF_location";
    public static final String VFS_SRM_CASTORUK_LOCATION            ="vfsSRM_CASTOR_UK_location";    
    public static final String VFS_SRM_STORMIT_LOCATION             ="vfsSRM_STORM_IT_location";
    public static final String VFS_SRM_AMC_LOCATION                 ="vfsSRM_DPM_AMC_location";
    
    public static final String VFS_LFC_SARA_LOCATION = "vfsLFC_SARA_location1"; 
    public static final String VFS_LFC_SARA_OTHER_LOCATION = "vfsLFC_SARA_location2"; 

    public static final String VFS_SRM_STORMRUG_LOCATION   ="vfsSRM_STORM_RUG_Location";
    public static final String VFS_SRM_DCACHE_RUG_LOCATION ="vfsSRM_dCache_RUG_Location";
    public static final String VFS_WEBDAV_LOCATION_1 = "vfsWebdav_location_1";
    public static final String VFS_WEBDAV_LOCATION_2 = "vfsWebdav_location_2";
   
    public static final String BDII_URI_SARA = "ldap://bdii.grid.sara.nl:2170";
    public static final String BDII2_URI_SARA = "ldap://bdii2.grid.sara.nl:2170";
    public static final String BDII_URI_NIKHEF = "ldap://bdii03.nikhef.nl:2170";
    public static final String BDII_URI_EXP_NIKHEF = "ldap://tbn19.nikhef.nl:2170";
    
    public static final String[] BDII_LOCATIONS = 
            { 
                TestSettings.BDII_URI_SARA, 
                TestSettings.BDII2_URI_SARA,
                TestSettings.BDII_URI_NIKHEF,
                TestSettings.BDII_URI_EXP_NIKHEF 
            };

    /** Singleton! */ 
    private static TestSettings instance; 
    
    static
    {
        instance=new TestSettings(); 
    }
    
    public static TestSettings getDefault()
    {
        return instance; 
    }
    
    public static VRL getTestLocation(String name)
    {
        return getDefault().getLocation(name); 
    }

    // ========================================================================
    // 
    // ========================================================================

    private Map<String,VRL> testLocations=new Hashtable<String,VRL>();
    private String testUserName; 
    
    private TestSettings()
    {
        testUserName=GlobalProperties.getGlobalUserName();  
    	initLocations();
    }
    
    private void initLocations()
    {
        // init locations:
        
        testLocations.put(VFS_LOCAL_TEMPDIR_LOCATION,
                new VRL("file", null, "/tmp/" + testUserName + "/localtmpdir"));
                 
        testLocations.put(VFS_LOCALFS_LOCATION,
                new VRL("file", null, "/tmp/" + testUserName + "/testLocalFS"));
        
        testLocations.put(VFS_GFTP_LOCATION,
                new VRL("gftp", "fs2.das3.science.uva.nl", "/tmp/" + testUserName + "/testGFTP1"));

        testLocations.put(VFS_GFTP_LOCATION2,
                new VRL("gftp", "fs2.das3.science.uva.nl", "/tmp/" + testUserName + "/testGFTP2"));
        
        testLocations.put(VFS_GFTP_ELAB_LOCATION,
                new VRL("gftp", "elab.lab.uvalight.net", "/tmp/" + testUserName + "/testGFTP3"));
        
        testLocations.put(VFS_SFTP_SARA_LOCATION,
                new VRL("sftp", "ui.grid.sara.nl", "/tmp/" + testUserName+ "/testSFTP1"));

        testLocations.put(VFS_SFTP_LOCALHOST_TESTUSER,
                new VRL("sftp", "testuser", "localhost", 22, "/tmp/testuser/testSFTP2"));
        
        testLocations.put(VFS_SRM_DCACHE_SARA_LOCATION,
                new VRL("srm","srm.grid.sara.nl","/pnfs/grid.sara.nl/data/nlesc.nl/" + testUserName+ "/testSRM_dCache_SARA_t1"));

        testLocations.put(VFS_SRM_DCACHE_SARA_OTHER_LOCATION,
                new VRL("srm","srm.grid.sara.nl","/pnfs/grid.sara.nl/data/nlesc.nl/other" + testUserName+ "/testSRM_dCache_SARA_t1"));
        
        testLocations.put(VFS_SRM_DTEAM_DCACHE_SARA_LOCATION,
                new VRL("srm","srm-t.grid.sara.nl","/pnfs/grid.sara.nl/data/dteam/" + testUserName+ "/testSRM_T_dCache_SARA_t1"));
        
        testLocations.put(VFS_SRM_DPM_NIKHEF_LOCATION,
                new VRL("srm",null,"tbn18.nikhef.nl",8446,"/dpm/nikhef.nl/home/pvier/" + testUserName+ "/testSRM_DPM_NIKHEF"));
        
        testLocations.put(VFS_SRM_STORMIT_LOCATION,
                new VRL("srm","prod-se-01.pd.infn.it","/dteam/" + testUserName+ "/testSRM_STORM_it"));
        
        testLocations.put(VFS_SRM_STORMRUG_LOCATION,
                new VRL("srm","srm.grid.rug.nl","/pvier/" + testUserName+ "/testSRM_STORM_rug"));
        
        testLocations.put(VFS_SRM_CASTORUK_LOCATION,
                new VRL("srm",null, "srm-dteam.gridpp.rl.ac.uk", 8443,"/castor/ads.rl.ac.uk/test/dteam/" +testUserName + "/test_SRMVFS_CASTOR_uk"));
        
        testLocations.put(VFS_SRM_AMC_LOCATION,
                new VRL("srm", null, "gb-se-amc.amc.nl", 8446, "/dpm/amc.nl/home/pvier/"));
        
        testLocations.put(VFS_SRM_DCACHE_RUG_LOCATION,
                new VRL("srm", null, "se.grid.rug.nl", 8443, "/pnfs/grid.rug.nl/data/pvier/")); 
        
        testLocations.put(VFS_WEBDAV_LOCATION_1,
                new VRL("webdav", null, "localhost", 8008, "/tmp/" + testUserName + "/testWEBDAV"));
        
        testLocations.put(VFS_WEBDAV_LOCATION_2,
                new VRL("webdav", null, "localhost", 8008, "/tmp/" + testUserName + "/testWEBDAV_2"));
    
        testLocations.put(VFS_LFC_SARA_LOCATION,
                new VRL("lfn", null, "lfc.grid.sara.nl", 5010, "/grid/nlesc.nl/ptdeboer/testlfc"));

        testLocations.put(VFS_LFC_SARA_OTHER_LOCATION,
                new VRL("lfn", null, "lfc.grid.sara.nl", 5010, "/grid/nlesc.nl/ptdeboer/testlfc_other")); 
    }
    
    public VRL getLocation(String name)
    {
        return this.testLocations.get(name);  
    }

    public String[] getLocationNames()
    {
        Set<String> set = this.testLocations.keySet(); 
        String names[]=new String[set.size()];  
        names=set.toArray(names);
        return names; 
    }
    
    public void setUsername(String user)
    {
    	this.testUserName=user; 
    	initLocations(); 
    }
    // ========================================================================
    // 
    // ========================================================================
    
    /** test location for the SRB tests */
    public static VRL test_srb_location = null;

    /** test location for the GFTP tests */
    // public static VRL testGFTPLocation
    // =new
    // VRL("gftp","ds2a.das2.nikhef.nl","/tmp/"+Global.getUserName()+"/testGFTP");
    // GAT Locations:
    public static VRL testGAT_GFTPLocation = new VRL("gat.gsiftp", "fs2.das3.science.uva.nl", "/tmp/"
            + GlobalProperties.getGlobalUserName() + "/testGAT_GFTP");

    /**
//     * local tempdir to create test files:
//     */
//    public static VRL localTempDirLocation = new VRL("file", null, "/tmp/" + Global.getUsername() + "/vfstesttempdir");

    public static VRL testWSVFSLocation = new VRL("ws.vfs", null, "pc-vlab17.science.uva.nl", 8443, "/",
            "vrl=gsiftp://pc-vlab19.science.uva.nl/tmp/" + GlobalProperties.getGlobalUserName() + "/wsvfstestdir", (String) null);

//    public static VRL testSRMNikhefLocation = new VRL("srm", null, "tbn18.nikhef.nl", 8446,
//            "/dpm/nikhef.nl/home/pvier/" + Global.getUsername() + "/test_SRMVFS_DPM");

    public static VRL testSRMRuGLocation = new VRL("srm", null, "srm.grid.rug.nl", 8444, "/pvier/"
            + GlobalProperties.getGlobalUserName() + "/test_SRMVFS_STORM");

//    public static VRL testSRMAMCLocation = new VRL("srm", null, "gb-se-amc.amc.nl", 8446, "/dpm/amc.nl/home/pvier/"
//            + Global.getUsername() + "/test_SRMVFS_DPM");

//    public static VRL testSRMSaraLocation = new VRL("srm", null, "srm.grid.sara.nl", 8443,
//            "/pnfs/grid.sara.nl/data/pvier/" + Global.getUsername() + "/test_SRMVFS_DCACHE");

//    public static VRL testSRMCastorUK  = new VRL("srm", null, "srm-dteam.gridpp.rl.ac.uk", 8443,
//            "castor/ads.rl.ac.uk/test/dteam/" + Global.getUsername() + "/test_SRMVFS_CASTOR");

    public static VRL testLFCJSaraLocation = new VRL("lfn", null, "lfc.grid.sara.nl", 5010, "/grid/pvier/test-"
            + GlobalProperties.getGlobalUserName() + "-vfslfc2/");

    public static VRL testLFCJNikhefLocation = new VRL("lfn", null, "lfc03.nikhef.nl", 5010, "/grid/pvier/test-"
            + GlobalProperties.getGlobalUserName() + "-vfslfc2/");

    public static VRL testLFCLocation = testLFCJSaraLocation;

    public static VRL testLFCLocation2 = testLFCJNikhefLocation;

//    public static VRL testIrodsSaraLocation=new VRL("irods", "piter_de_boer", "irods.grid.sara.nl",50000,
//                                                    "/SARA_BIGGRID/home/piter_de_boer/testIrodsVFS");
    
    public static VRL testIrodsSaraLocation=new VRL("irods", "piter_de_boer", "irods.grid.sara.nl",50000,
                                                     "/SARA_BIGGRID/home/public/testdir/testIrodsVFS");   
           
    public static VRL test_vCommentable_SaraLocation = new VRL("lfn", null, "lfc.grid.sara.nl", 5010,
            "/grid/pvier/test-" + GlobalProperties.getGlobalUserName() + "-vCommentableTest/");

    public static VRL test_vCommentable_NikhefLocation = new VRL("lfn", null, "lfc03.nikhef.nl", 5010,
            "/grid/pvier/test-" + GlobalProperties.getGlobalUserName() + "-vCommentableTest/");

    public static String[] BLACK_LISTED_SE =
            { "se.grid.rug.nl", "srm.grid.rug.nl" };
    
    
    // 
    // Static initializer
    // 

    static
    {
        // Global.setDebug(true);
        try
        {
            test_srb_location = new VRL(
                    "srb://piter.de.boer.vlenl@srb.grid.sara.nl:50000/VLENL/home/piter.de.boer.vlenl/testSRB?srb.defaultResource=vleGridStore");
        }
        catch (VrsException e)
        {
            e.printStackTrace();
        }

    }

    public static ServerInfo getServerInfoFor(VRL location, boolean create) throws VrsException
    {
        return VRSContext.getDefault().getServerInfoFor(location, create);
    }

}
