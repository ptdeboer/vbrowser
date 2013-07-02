package test.vrs.vrl;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.vrl.VRLUtil;

import org.junit.Assert;
import org.junit.Test;

public class Test_VRLUtil
{

    @Test
    public void test_VRLUtil_hasSameServer() throws VRLSyntaxException
    {

        VRL vrl1 = new VRL("gftp://myserver/path1");
        VRL vrl2 = new VRL("gftp://myserver:2811/path2");

        Assert.assertTrue("VRL with missing GFTP port must match against default port (I)",
                VRLUtil.hasSameServer(vrl1, vrl2));
        Assert.assertTrue("VRL with missing GFTP port must match against default port (II)",
                VRLUtil.hasSameServer(vrl2, vrl1));

        vrl1 = new VRL("gftp://myserver/path1");
        vrl2 = new VRL("gsiftp://myserver:2811/path2");

        Assert.assertTrue("VRL with missing GFTP port must match against default port (I)",
                VRLUtil.hasSameServer(vrl1, vrl2));
        Assert.assertTrue("VRL with missing GFTP port must match against default port (II)",
                VRLUtil.hasSameServer(vrl2, vrl1));

    }
    
    @Test
    public void test_VRLUtil_isLocalLocation()
    {
        VRL localVrl = new VRL("file", null, null);
        Assert.assertTrue("isLocalHostname should be true", VRLUtil.isLocalLocation(localVrl));

        localVrl = new VRL("file", "localhost", "/etc");
        Assert.assertTrue("isLocalHostname should be true", VRLUtil.isLocalLocation(localVrl));

        localVrl = new VRL("file", "", "/etc");
        Assert.assertTrue("isLocalHostname should be true", VRLUtil.isLocalLocation(localVrl));

        // current hardcoded alias for localhosts
        localVrl = new VRL("file", "127.0.0.1", "/etc");
        Assert.assertTrue("isLocalHostname should be true", VRLUtil.isLocalLocation(localVrl));

        localVrl = new VRL("file", null, "/etc");
        Assert.assertTrue("isLocalHostname should be true", VRLUtil.isLocalLocation(localVrl));

        localVrl = new VRL("file", GlobalProperties.getHostname(), "/etc");
        Assert.assertTrue("isLocalHostname should be true", VRLUtil.isLocalLocation(localVrl));
    }

    

}
