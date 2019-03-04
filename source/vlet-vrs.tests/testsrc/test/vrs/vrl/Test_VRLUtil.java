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
