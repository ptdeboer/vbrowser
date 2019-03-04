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

package test;

import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRSContext;

/**
 * Test configuarion settings.
 * 
 * @author P.T. de Boer
 */
public class TestSettings
{
    public static enum TestLocation
    {
        VFS_LOCALFS_LOCATION,
        VFS_LOCAL_TEMPDIR_LOCATION,
        VFS_GFTP_V1_LOCATION,
        VFS_GFTP_LOCALHOST,
        VFS_GFTP_LOCATION1,
        VFS_GFTP_LOCATION2,
        VFS_GFTP_CLOUD_LOCATION,
        VFS_SFTP_SARA_LOCATION,
        VFS_SFTP_LOCALHOST_TESTUSER,
        VFS_SRM_DCACHE_SARA_LOCATION,
        VFS_SRM_DCACHE_SARA_OTHER_LOCATION,
        VFS_SRM_DTEAM_DCACHE_SARA_LOCATION,
        VFS_SRM_DPM_NIKHEF_LOCATION,
        VFS_SRM_CASTORUK_LOCATION,
        VFS_SRM_STORMIT_LOCATION,
        VFS_SRM_AMC_LOCATION,
        VFS_LFC_SARA_LOCATION,
        VFS_LFC_SARA_OTHER_LOCATION,

        VFS_SRM_STORMRUG_LOCATION,
        VFS_SRM_DCACHE_RUG_LOCATION,
        VFS_WEBDAV_LOCATION_1,
        VFS_WEBDAV_LOCATION_2
    }

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
        instance = new TestSettings();
    }

    public static TestSettings getDefault()
    {
        return instance;
    }

    // init locations:

    public static VRL getTestLocation(String location)
    {
        return getDefault().testLocations.get(TestLocation.valueOf(location));
    }

    public static VRL getTestLocation(TestLocation location)
    {
        return getDefault().testLocations.get(location);
    }

    
    // ========================================================================
    //
    // ========================================================================

    private Map<TestLocation, VRL> testLocations = new Hashtable<TestLocation, VRL>();

    private String testUserName;

    private TestSettings()
    {
        testUserName = GlobalProperties.getGlobalUserName();
        initLocations();
    }


    private void initLocations()
    {

        testLocations.put(TestLocation.VFS_LOCAL_TEMPDIR_LOCATION,
                new VRL("file", null, "/tmp/" + testUserName + "/localtmpdir"));

        testLocations.put(TestLocation.VFS_LOCALFS_LOCATION,
                new VRL("file", null, "/tmp/" + testUserName + "/testLocalFS"));

        testLocations.put(TestLocation.VFS_GFTP_LOCALHOST,
                new VRL("gsiftp", "eslt007.local", "/tmp/gftp/testGFTP1"));
        
        testLocations.put(TestLocation.VFS_GFTP_LOCATION1,
                new VRL("gsiftp", "fs2.das4.science.uva.nl", "/tmp/" + testUserName + "/testGFTP2"));

        testLocations.put(TestLocation.VFS_GFTP_LOCATION2,
                new VRL("gsiftp", "fs2.das4.science.uva.nl", "/tmp/" + testUserName + "/testGFTP3"));

        testLocations.put(TestLocation.VFS_GFTP_CLOUD_LOCATION,
                new VRL("gsiftp", "xnatws.esciencetest.nl", 2811, "/tmp/" + testUserName + "/testGFTP4"));

        testLocations.put(TestLocation.VFS_SFTP_SARA_LOCATION,
                new VRL("sftp", "ptdeboer", "ui.grid.sara.nl", 22, "/tmp/" + testUserName + "/testSFTP1"));

        testLocations.put(TestLocation.VFS_SFTP_LOCALHOST_TESTUSER,
                new VRL("sftp", "testuser", "localhost", 22, "/tmp/testuser/testSFTP2"));

        testLocations.put(TestLocation.VFS_SRM_DCACHE_SARA_LOCATION,
                new VRL("srm", "srm.grid.sara.nl", "/pnfs/grid.sara.nl/data/nlesc.nl/" + testUserName + "/testSRM_dCache_SARA_t1"));

        testLocations.put(TestLocation.VFS_SRM_DCACHE_SARA_OTHER_LOCATION,
                new VRL("srm", "srm.grid.sara.nl", "/pnfs/grid.sara.nl/data/nlesc.nl/other" + testUserName + "/testSRM_dCache_SARA_t1"));

        testLocations.put(TestLocation.VFS_SRM_DTEAM_DCACHE_SARA_LOCATION,
                new VRL("srm", "srm-t.grid.sara.nl", "/pnfs/grid.sara.nl/data/dteam/" + testUserName + "/testSRM_T_dCache_SARA_t1"));

        testLocations.put(TestLocation.VFS_SRM_DPM_NIKHEF_LOCATION,
                new VRL("srm", null, "tbn18.nikhef.nl", 8446, "/dpm/nikhef.nl/home/pvier/" + testUserName + "/testSRM_DPM_NIKHEF"));

        testLocations.put(TestLocation.VFS_SRM_STORMIT_LOCATION,
                new VRL("srm", "prod-se-01.pd.infn.it", "/dteam/" + testUserName + "/testSRM_STORM_it"));

        testLocations.put(TestLocation.VFS_SRM_STORMRUG_LOCATION,
                new VRL("srm", "srm.grid.rug.nl", "/pvier/" + testUserName + "/testSRM_STORM_rug"));

        testLocations.put(TestLocation.VFS_SRM_CASTORUK_LOCATION,
                new VRL("srm", null, "srm-dteam.gridpp.rl.ac.uk", 8443, "/castor/ads.rl.ac.uk/test/dteam/" + testUserName
                        + "/test_SRMVFS_CASTOR_uk"));

        testLocations.put(TestLocation.VFS_SRM_AMC_LOCATION,
                new VRL("srm", null, "gb-se-amc.amc.nl", 8446, "/dpm/amc.nl/home/pvier/"));

        testLocations.put(TestLocation.VFS_SRM_DCACHE_RUG_LOCATION,
                new VRL("srm", null, "se.grid.rug.nl", 8443, "/pnfs/grid.rug.nl/data/pvier/"));

        testLocations.put(TestLocation.VFS_WEBDAV_LOCATION_1,
                new VRL("webdav", null, "localhost", 8008, "/tmp/" + testUserName + "/testWEBDAV"));

        testLocations.put(TestLocation.VFS_WEBDAV_LOCATION_2,
                new VRL("webdav", null, "localhost", 8008, "/tmp/" + testUserName + "/testWEBDAV_2"));

        testLocations.put(TestLocation.VFS_LFC_SARA_LOCATION,
                new VRL("lfn", null, "lfc.grid.sara.nl", 5010, "/grid/nlesc.nl/ptdeboer/testlfc"));

        testLocations.put(TestLocation.VFS_LFC_SARA_OTHER_LOCATION,
                new VRL("lfn", null, "lfc.grid.sara.nl", 5010, "/grid/nlesc.nl/ptdeboer/testlfc_other"));
    }

    public VRL getLocation(TestLocation location)
    {
        return this.testLocations.get(location);
    }
    
    public String[] getLocationNames()
    {
        Set<TestLocation> set = this.testLocations.keySet();
        String names[] = new String[set.size()];
        names = set.toArray(names);
        return names;
    }

    public void setUsername(String user)
    {
        this.testUserName = user;
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
     * // * local tempdir to create test files: //
     */
    // public static VRL localTempDirLocation = new VRL("file", null, "/tmp/" +
    // Global.getUsername() + "/vfstesttempdir");

    public static VRL testWSVFSLocation = new VRL("ws.vfs", null, "pc-vlab17.science.uva.nl", 8443, "/",
            "vrl=gsiftp://pc-vlab19.science.uva.nl/tmp/" + GlobalProperties.getGlobalUserName() + "/wsvfstestdir", (String) null);

    // public static VRL testSRMNikhefLocation = new VRL("srm", null,
    // "tbn18.nikhef.nl", 8446,
    // "/dpm/nikhef.nl/home/pvier/" + Global.getUsername() +
    // "/test_SRMVFS_DPM");

    public static VRL testSRMRuGLocation = new VRL("srm", null, "srm.grid.rug.nl", 8444, "/pvier/"
            + GlobalProperties.getGlobalUserName() + "/test_SRMVFS_STORM");

    // public static VRL testSRMAMCLocation = new VRL("srm", null,
    // "gb-se-amc.amc.nl", 8446, "/dpm/amc.nl/home/pvier/"
    // + Global.getUsername() + "/test_SRMVFS_DPM");

    // public static VRL testSRMSaraLocation = new VRL("srm", null,
    // "srm.grid.sara.nl", 8443,
    // "/pnfs/grid.sara.nl/data/pvier/" + Global.getUsername() +
    // "/test_SRMVFS_DCACHE");

    // public static VRL testSRMCastorUK = new VRL("srm", null,
    // "srm-dteam.gridpp.rl.ac.uk", 8443,
    // "castor/ads.rl.ac.uk/test/dteam/" + Global.getUsername() +
    // "/test_SRMVFS_CASTOR");

    public static VRL testLFCJSaraLocation = new VRL("lfn", null, "lfc.grid.sara.nl", 5010, "/grid/pvier/test-"
            + GlobalProperties.getGlobalUserName() + "-vfslfc2/");

    public static VRL testLFCJNikhefLocation = new VRL("lfn", null, "lfc03.nikhef.nl", 5010, "/grid/pvier/test-"
            + GlobalProperties.getGlobalUserName() + "-vfslfc2/");

    public static VRL testLFCLocation = testLFCJSaraLocation;

    public static VRL testLFCLocation2 = testLFCJNikhefLocation;

    // public static VRL testIrodsSaraLocation=new VRL("irods", "piter_de_boer",
    // "irods.grid.sara.nl",50000,
    // "/SARA_BIGGRID/home/piter_de_boer/testIrodsVFS");

    public static VRL testIrodsSaraLocation = new VRL("irods", "piter_de_boer", "irods.grid.sara.nl", 50000,
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
