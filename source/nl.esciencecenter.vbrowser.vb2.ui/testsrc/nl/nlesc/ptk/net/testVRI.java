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

package nl.nlesc.ptk.net;

import java.net.URI;
import java.net.URISyntaxException;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.util.StringUtil;


import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Tests VRI. 
 */
public class testVRI extends TestCase
{
    // VAttribute attribute=null;

    public static VRI createDosVRI(String vrlstr) throws VRISyntaxException
    {
        String newStr=vrlstr.replace('\\','/'); 
        // constructor might change ! 
        return new VRI(newStr); 
    }
    
    /**
     * Sets up the tests fixture. (Called before every tests case method.)
     */
    protected void setUp()
    {
        // VAttribute=new VAttribute((String)null,(String)null,(String)null);
    }

    /**
     * Tears down the tests fixture. (Called after every tests case method.)
     */
    protected void tearDown()
    {

    }

    // some check to ensure URI<->VRI consistancy !
    public void testURICompatibility() throws Exception
    {
        // scheme/host/path/fragment
        URI uri = new URI("file", "", "/etc", (String) null);
        VRI vri = new VRI("file", "", "/etc", (String) null);

        if (vri.toURI().compareTo(uri) != 0)
            Assert.assertEquals("VRI not similar to URI", vri.toString(), uri.toString());

        // scheme/host/path/fragment
        uri = new URI("file", null, "/etc", (String) null);
        vri = new VRI("file", null, "/etc", (String) null);

        if (vri.toURI().compareTo(uri) != 0)
            Assert.assertEquals("VRI not similar to URI", vri.toString(), uri.toString());

    }

    public void testConstructors() throws Exception
    {
        // NOTE: use normalized URI strings here: 
        testConstructor("file:/path"); 
        testConstructor("http://host:9909/path"); 
        testConstructor("https://server:1234/path?urlAttribute=value"); 
        
        // normalized: use decoded strings:
        testConstructor("gfile://HOST:1234/path"); 

        // normalized: use decoded strings:
        testConstructor("gfile://HOST:1234/A path"); 

        testConstructor("Constructor VRI(\"file\",null,\"/etc\") does not match",new VRI("file", null, "/etc"),"file:/etc"); 
        testConstructor("Constructor VRI(\"file\",null,\"/etc\") does not match",new VRI("file", null, "etc"),"file:etc"); 

        
        VRI local = new VRI("file", null, "dirname/etc");

        // if (local.toString().compareTo("file://localhost/etc")!=0)
        Assert.assertEquals("Constructor VRI(\"file\",null,\"/etc\") does not match", "file:dirname/etc", local
                .toString());

        local = new VRI("file", null, null);
        Assert.assertEquals("Constructor VRI(\"file\",null,null) does not match", "file:", local.toString());

        local = new VRI("file:///");
        Assert.assertEquals("local file VRI does not match", "file:/", local.toString());

        local = new VRI("file:/");
        Assert.assertEquals("local file VRI does not match", "file:/", local.toString());

//        // test scheme with ":" appended
//        local = new VRI("file:", null, "/etc");
//        if (local.toString().compareTo("file://localhost/etc") != 0)
//            Assert.assertEquals("added colon is not ignored.", "file:/etc", local.toString());

        // negative port must be filtered out.
        local = new VRI("file", null, -1, "/etc");
        if (local.toString().compareTo("file://localhost/etc") != 0)
            Assert.assertEquals("Negative port number is not ignored.", "file:/etc", local.toString());

        // zero port must be filtered out.
        local = new VRI("file", null, 0, "/etc");
        if (local.toString().compareTo("file://localhost/etc") != 0)
            Assert.assertEquals("Zero port number is not ignored.", "file:/etc", local.toString());
    }

    
    // VRI String must be normalized! 
    private void testConstructor(String vriStr) throws VRISyntaxException
    {
        VRI newVRI = new VRI(vriStr);
        Assert.assertEquals("create VRI does not match it's original string", vriStr, newVRI.toString());
    }
    
    // VRI String must be normalized! 
    private void testConstructor(String message,VRI vri,String vriStr) throws VRISyntaxException
    {
        Assert.assertEquals(message, vriStr, vri.toString());
    }
    
    public void testGetParent() throws Exception
    {
        VRI local = new VRI("file", null, "/etc");
        VRI parent = local.getParent();

        Assert.assertEquals("Method getParent does not return root path:" + parent, "/", parent.getPath());

        parent = parent.getParent();

        Assert.assertEquals("Method isRootPath of root should return true.", "/", parent.getPath()); 
    }

    public void testNewLocationFromLocalTildeExpansion() throws Exception
    {
        VRI loc = new VRI("file:/~");
    }

    public void testMyScheme() throws Exception
    {
        String myScheme="myScheme"; 
        
        VRI loc = new VRI(myScheme+":");

        Assert.assertEquals("MyVLe location must have MyVLE scheme type", myScheme, loc.getScheme());

        loc = new VRI("myScheme:/");
        Assert.assertEquals("MyVLe location must have MyVLE scheme type", myScheme, loc.getScheme());

        loc = new VRI("myScheme://");
        Assert.assertEquals("MyVLe location must have MyVLE scheme type", myScheme, loc.getScheme());

        loc = new VRI("myScheme:///");
        Assert.assertEquals("MyVLe location must have MyVLE scheme type", myScheme, loc.getScheme());
    }

    public void testNewLocationFromNullString() throws Exception
    {
        VRI loc = new VRI(null, null, (String) null);

        // Assert.assertEquals("NULL location, shoud have NULL hostname",
        // "localhost",
        // loc.getHostname());

        Assert.assertEquals("NULL location, shoud have NULL userinfo", null, loc.getUserinfo());
        Assert.assertEquals("NULL location, shoud have 0 port", true, loc.getPort() <= 0);
        Assert.assertEquals("NULL location, should have null path or empty string", true, StringUtil.isEmpty(loc.getPath()));
    }

    public void testBasename() throws VRISyntaxException
    {
        VRI vri=new VRI("file://localhost/dirname/basename.ext");
        
        Assert.assertEquals("Basename (with extension) doesn't match.","basename.ext",vri.getBasename(true)); 
        Assert.assertEquals("Basename (without extension) doesn't match.","basename",vri.getBasename(false)); 
        Assert.assertEquals("Extension part doesn't match.","ext",vri.getExtension()); 
        
    }
    
    
    public void testRelative() throws Exception
    {
        VRI relvri = new VRI("../relative directory");
        Assert.assertTrue("VRI should be relative", relvri.isRelative());
        Assert.assertTrue("VRI should be relative", relvri.isAbsolute() == false);

        relvri = new VRI("/absolute path/but relative URI!");
        Assert.assertTrue("VRI should be relative", relvri.isRelative());
        Assert.assertTrue("VRI should be relative", relvri.isAbsolute() == false);

        relvri = new VRI("/absolute path/but relative URI!");
        Assert.assertTrue("VRI should be relative", relvri.isRelative());
        // url with scheme without host= absolute

        relvri = new VRI("file:///aap");
        Assert.assertTrue("VRI should be absolute", relvri.isAbsolute());
        Assert.assertTrue("VRI should be absolute", relvri.isRelative() == false);

        relvri = new VRI("subdir");
        VRI parent = new VRI("gftp://hostname/parentpath");

        VRI loc2 = parent.resolvePath(relvri);
        Assert.assertEquals("resolved VRI turned out wrong", "gftp://hostname/parentpath/subdir", loc2.toString());
        
        relvri = new VRI("subdir");
        parent = new VRI("gftp://hostname/parentpath/base.html");

        loc2 = parent.resolve(relvri);
        Assert.assertEquals("resolved VRI turned out wrong", "gftp://hostname/parentpath/subdir", loc2.toString());

        String subFile="other.html"; 
        loc2 = parent.resolve(subFile);
        Assert.assertEquals("resolved VRI turned out wrong", "gftp://hostname/parentpath/other.html", loc2.toString());
    }
    
    public void testResolvePath() throws Exception
    {
        String path="file"; 
        VRI parent = new VRI("gftp://hostname/parentpath");
        VRI loc2 = parent.resolvePath(path);
        Assert.assertEquals("resolved VRI turned out wrong", "gftp://hostname/parentpath/file", loc2.toString());

        path="../file"; 
        parent = new VRI("gftp://hostname/parentpath");
        loc2 = parent.resolvePath(path);
        Assert.assertEquals("resolved VRI turned out wrong", "gftp://hostname/file", loc2.toString());

        path="A File"; 
        parent = new VRI("gftp://hostname/parentpath");
        loc2 = parent.resolvePath(path);
        Assert.assertEquals("resolved VRI turned out wrong", "gftp://hostname/parentpath/A File", loc2.toString());

        path="../A File"; 
        parent = new VRI("gftp://hostname/parentpath");
        loc2 = parent.resolvePath(path);
        Assert.assertEquals("resolved VRI turned out wrong", "gftp://hostname/A File", loc2.toString());

    }
        
    public void testReleativeIndex() throws VRISyntaxException
    {
        VRI parent = new VRI("gftp://hostname/parentpath/base.html");

        // index
        VRI loc2 = parent.resolve("#index");
        Assert.assertEquals("resolved VRI turned out wrong", "gftp://hostname/parentpath/base.html#index", 
                loc2.toString());
    }
    
    public void testReleativeQuery() throws VRISyntaxException
    {
        VRI parent = new VRI("http://hostname/parentpath/base.html");

        // index
        VRI loc2 = parent.resolve("?fraq");
        Assert.assertEquals("resolved VRI turned out wrong", "gftp://hostname/parentpath/base.html?fraq", 
                loc2.toString());

        // index
        loc2 = parent.resolve("?fraq&par=1;");
        Assert.assertEquals("resolved VRI turned out wrong", "gftp://hostname/parentpath/base.html?fraq&par=1;", 
                loc2.toString());
    }
    
    public void testDosRelative() throws Exception
    {
        //
        // DOS relative paths
        // 

        VRI parent = new VRI("file:///C:");
        VRI relvri = new VRI("subdir");
        VRI resolved = parent.resolvePath(relvri);
        Assert.assertEquals("resolved DOS VRI turned out wrong", "file:/C:/subdir", resolved.toString());

        parent = new VRI("file://WinHost/C:");
        relvri = new VRI("subdir");
        resolved = parent.resolvePath(relvri);
        Assert.assertEquals("resolved DOS VRI turned out wrong", "file://WinHost/C:/subdir", resolved.toString());

        parent = new VRI("file:/C:");
        relvri = new VRI("subdir");
        resolved = parent.resolvePath(relvri);
        Assert.assertEquals("resolved DOS VRI turned out wrong", "file:/C:/subdir", resolved.toString());

        parent = createDosVRI("file:///C:\\");
        relvri = createDosVRI(".\\subdir");
        resolved = parent.resolvePath(relvri);
        Assert.assertEquals("resolved DOS VRI turned out wrong", "file:/C:/subdir", resolved.toString());

        parent = createDosVRI("file:///C:\\Windos XP\\Stuffdir\\");
        relvri = createDosVRI(".\\subdir\\subsubdir");
        resolved = parent.resolvePath(relvri);
        Assert.assertEquals("resolved DOS VRI turned out wrong", "file:/C:/Windos XP/Stuffdir/subdir/subsubdir",
                resolved.toString());

        parent = createDosVRI("file:/C:\\Windos XP\\Stuffdir\\");
        relvri = createDosVRI(".\\subdir\\subsubdir");
        resolved = parent.resolvePath(relvri);
        Assert.assertEquals("resolved DOS VRI turned out wrong", "file:/C:/Windos XP/Stuffdir/subdir/subsubdir",
                resolved.toString());

        parent = createDosVRI("file:///C:\\subdir\\");
        resolved = parent.resolvePath("subsubdir");
        Assert.assertEquals("resolved DOS VRI turned out wrong", "file:/C:/subdir/subsubdir", resolved.toString());

        resolved = parent.resolvePath("../twindir");
        Assert.assertEquals("resolved DOS VRI turned out wrong", "file:/C:/twindir", resolved.toString());

        resolved = parent.resolvePath("./../twindir2");
        Assert.assertEquals("resolved DOS VRI turned out wrong", "file:/C:/twindir2", resolved.toString());

        // TODO:
        // query+index
        // loc=parent.resolve("?query");
        // Assert.assertEquals("resolved VRI turned out wrong","file://hostname/parentpath/base.html?query#index",loc.toString());

    }

    public void testLocalHosts()
    {
        VRI localVrl = new VRI("file", null, null);
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalLocation());

        localVrl = new VRI("file", "localhost", "/etc");
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalLocation());

        localVrl = new VRI("file", "", "/etc");
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalLocation());

//        // Not valid for IPv6. 
//        localVrl = new VRI("file", "127.0.0.1", "/etc");
//        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalLocation());

        localVrl = new VRI("file", null, "/etc");
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalLocation());

        localVrl = new VRI("file", GlobalProperties.getHostname(), "/etc");
        Assert.assertTrue("isLocalHostname should be true", localVrl.isLocalLocation());
    }

    /*
     * public void testForException() { try { //Object o = emptyList.get(0);
     * fail("Should raise an IndexOutOfBoundsException"); } catch
     * (IndexOutOfBoundsException success) { } }
     */

    /**
     * Regressions unitTests for addPath:
     * 
     * @throws Exception
     */
    public void testLocationAddPath() throws Exception
    {
        VRI loc = new VRI("myvle:");
        VRI newLoc = loc.appendPath("testpath");
        Assert.assertEquals("added path not expected path", "/testpath", newLoc.getPath());

        // extra slashes:
        loc = new VRI("myvle:///");
        newLoc = loc.appendPath("testpath");
        Assert.assertEquals("added path not expected path", "/testpath", newLoc.getPath());

        // tests with null path
        loc = new VRI(null, null, (String) null);
        newLoc = loc.appendPath("testpath");
        Assert.assertEquals("added path to <null> path not expected path", "/testpath", newLoc.getPath());

        // unitTests with extra slashes
        loc = new VRI("myvle:////parent");
        newLoc = loc.appendPath("testpath");
        Assert.assertEquals("extra slashes I: path is not expected path", "/parent/testpath", newLoc.getPath());

        // unitTests with extra slashes
        loc = new VRI("myvle:///parent");
        newLoc = loc.appendPath("/testpath");
        Assert.assertEquals("extra slashes II: path is not expected path", "/parent/testpath", newLoc.getPath());

        // unitTests with extra slashes
        loc = new VRI("myvle:///parent");
        newLoc = loc.appendPath("//testpath");
        Assert.assertEquals("extra slashes II: path is not expected path", "/parent/testpath", newLoc.getPath());

        // unitTests with extra slashes
        loc = new VRI("myvle:///parent");
        newLoc = loc.appendPath("/testpath/");
        Assert.assertEquals("extra slashes III: path is not expected path", "/parent/testpath", newLoc.getPath());

    }

    /**
     * Regressions unitTests for windows locations:
     * 
     * @throws Exception
     */
    public void testWinDosLocations() throws Exception
    {
        // make relative paths absolute
        VRI loc = new VRI("file:///C:");
        Assert.assertEquals("added path didn't result in expected path", "/C:/", loc.getPath());

        // make relative paths absolute
        loc = new VRI("file:///c:hoi");
        Assert.assertEquals("added path didn't result in expected path", "/c:/hoi", loc.getPath());

        // backaslashes to slashes:
        loc = createDosVRI("file:///c:\\hoi\\");
        Assert.assertEquals("added path to <null> didn't result in expected path", "/c:/hoi", loc.getPath());

        // make relative paths absolute
        loc = createDosVRI("file:///a:");
        VRI newLoc = loc.appendPath("testpath");
        Assert.assertEquals("added path didn't result in expected path", "/a:/testpath", newLoc.getPath());

        // make relative paths absolute
        loc = createDosVRI("file:///a:");
        newLoc = loc.appendPath("\\testpath");
        Assert.assertEquals("added path didn't result in expected path", "/a:/testpath", newLoc.getPath());

    }

    public void testEncoding() throws Exception
    {
        // Test encoded String to decoded path:
        VRI vri = new VRI("http://www.test.piter.nl/Hoi%20Piepeloi");
        String path = vri.getPath(); // decoded path
        Assert.assertEquals("Encoded path does not decode correctly.", "/Hoi Piepeloi", path);

        // Test decoded String to decoded path:
        vri = new VRI("http://www.test.piter.nl/Hoi Piepeloi");
        path = vri.getPath(); // decoded path
        Assert.assertEquals("Encoded path does not decode correctly.", "/Hoi Piepeloi", path);

        // test non encoded constructor to Decoded String representation
        // Also a relative path will be cast to an absolute path.
        vri = new VRI("sftp", "dummyhost", 6666, "Spaced Relative Path");

        // VRI to string returns DECODED URI
        String vristr = vri.toString();
        Assert.assertEquals("VRI constructor does not have a decoded path.",
                "sftp://dummyhost:6666/Spaced Relative Path", vristr);

        // URI string returns ENCODED URI
        // Currently this is my (VRI) definitation.
        String uristr = vri.toURI().toString();
        Assert.assertEquals("URI string encoded path.", "sftp://dummyhost:6666/Spaced%20Relative%20Path", uristr);
    }

    public void testPathEncoding() throws Exception, URISyntaxException
    {
        // Special character to test.
        // All should be encoded except '?#&'. They should be kept 'as-is'.
        // 
        String chars = "`'\"~!$%^*()_+-={}[]|;:'<>,@?#&";
        // String chars="`'\"~!%^*()_+-={}[]|;:'<>,@?#&";

        for (int i = 0; i < chars.length(); i++)
        {
            char c = chars.charAt(i);
            // System.out.println("Char="+c+",str="+estr);

            // check URI encoding with VRI encoding

            try
            {
                // Make sure VRI and URI use same encoding:
                URI uri = new URI("aap", "noot", "/" + c, null);
                VRI vri = new VRI("aap", "noot", "/" + c, null);

                // if c is in "#?&" then it will be recognised as query of
                // fragment seperator
                VRI vri2;
                if ((c == '#') || (c == '?') || (c == '&'))
                    vri2 = new VRI("aap://noot/" + URIFactory.encode("" + c));
                else
                    vri2 = new VRI("aap://noot/" + c);

                VRI vri3 = new VRI("aap://noot/" + URIFactory.encode("" + c));

                Assert.assertEquals("encoded URI does not match VRI", uri.toString(), vri.toURIString());
                Assert.assertEquals("Decoded VRI path does not match. ", "/" + c, vri.getPath());
                Assert.assertEquals("Decoded URI path does not match. ", "/" + c, uri.getPath());
                Assert.assertEquals("VRI constructors do not match.", vri.toString(), vri2.toString());
                Assert.assertEquals("VRI constructors do not match.", vri.toString(), vri3.toString());

            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
                throw e;
            }

        }

    }

    public void testRelativePaths() throws VRISyntaxException
    {
        // base url;
        VRI vri = new VRI("file:///home/");

        // paths resolving !

        // resolve absolute path:
        VRI newvri = vri.resolvePath("/etc");
        // allow both change of localhost names:
        String host = newvri.getHostname();

        // must be "file:///etc" or file:/etc
        if (StringUtil.compare(newvri.toString(), "file:/etc") != 0)
            Assert.assertEquals("new absolute VRI does not match", "file:///etc", newvri.toString());

        // resolve absolute path:
        newvri = vri.resolvePath("etc");
        // must be "file:///etc"
        Assert.assertEquals("new absolute VRI does not match", newvri.toString(), "file:/home/etc");

    }

    public void testURIReferences() throws VRISyntaxException
    {
        String guidStr = "guid:aapnootmies";
        VRI guiVRI = new VRI(guidStr);
        Assert.assertEquals("Simple GUID VRI does not match original string", guidStr, guiVRI.toString());

        guidStr = "guid:10293847565647382910";
        guiVRI = new VRI(guidStr);
        Assert.assertEquals("Simple GUID VRI does not match original string", guidStr, guiVRI.toString());

        guidStr = "guid:/AAPNOOTMIES";
        guiVRI = new VRI(guidStr);
        Assert.assertEquals("Simple GUID VRI does not match original string", guidStr, guiVRI.toString());

        guidStr = "guid:1234567890-ABCDEFGHIJKLMNOPQRSTUVWXYZ-_-abcdefghijklmnopqrstuvwxyz";
        guiVRI = new VRI(guidStr);
        Assert.assertEquals("GUID VRI does not match original string", guidStr, guiVRI.toString());

    }

    /**
     * Regression for query encoding bugs
     * 
     * @throws VRISyntaxException
     */
    public void testQueryEncoding() throws VRISyntaxException
    {
        String qstr = "property=hoipiepeloi";

        VRI vri = new VRI("scheme:?" + qstr);
        Assert.assertEquals("Returned query string doesn't match", qstr, vri.getQuery());
        vri = new VRI("scheme:/?" + qstr);
        Assert.assertEquals("Returned query string doesn't match", qstr, vri.getQuery());
        vri = new VRI("scheme://host/?" + qstr);
        Assert.assertEquals("Returned query string doesn't match", qstr, vri.getQuery());
        // vri=new VRI("scheme:reference?"+qstr);
        // Assert.assertEquals("Returned query string doens't match",qstr,vri.getQuery());

    }

    /** Test Duplicate Path VRIs */
    public void testDuplicatePath() throws VRISyntaxException
    {
        //
        // Regression bug !
        // 
        String prefix = "scheme://host:1234";
        VRI path = new VRI(prefix + "/absolutepath");

        // starting a path without an "/" creates wrong VRIs

        String relPath = "relative";
        VRI newvri = path.replacePath(relPath);

        // between host:port and relpath there must be an extra slash !
        Assert.assertEquals("Extra slash isn't inserted.", newvri.toString(), prefix + "/" + relPath);

        //
        // test duplicate equals hand hashcode !
        //

        VRI dupl = path.duplicate();
        Assert.assertEquals("Duplicate doesn't return same VRI.", path.toString(), dupl.toString());
        // test hashcode of duplicate
        Assert.assertEquals("Hashcode Failure !", path.hashCode(), dupl.hashCode());
        // test equals of duplicate
        Assert.assertTrue("equals() method Failure !", path.equals(dupl));

    }

    /** Test Duplicate Reference VRIs */
    public void testDuplicateReference() throws VRISyntaxException
    {
        //
        // Regression bug !
        // 
        String prefix = "scheme:reference?hello";
        VRI ref1 = new VRI(prefix);

        Assert.assertEquals("Wrong reference VRI.", ref1.toString(), "scheme:reference?hello");

        // starting a path without an "/" creates wrong VRIs
        String relPath = "relative";
        VRI newvri = ref1.replacePath(relPath);

        // duplicate path should insert extra slash (or else it isn't a path);
        Assert.assertEquals("Relative path is kept relative.", "scheme:relative?hello", newvri.toString());

        //
        // test duplicate equals hand hashcode !
        // 

        VRI dupl = ref1.duplicate();
        Assert.assertEquals("Duplicate doesn't return same VRI.", ref1.toString(), dupl.toString());
        // test hashcode of duplicate
        Assert.assertEquals("Hascode Failure !", ref1.hashCode(), dupl.hashCode());
        // test equals of duplicate
        Assert.assertTrue("equals() method Failure !", ref1.equals(dupl));

    }

    public void testDefaultPorts() throws VRISyntaxException
    {
//        VRI vri1 = new VRI("sftp://elab/path1");
//        VRI vri2 = new VRI("sftp://elab:22/path2");
//
//        Assert.assertTrue("VRI with missing SFTP port must match against default port (I)", vri1.hasSameServer(vri2));
//        Assert.assertTrue("VRI with missing SFTP port must match against default port (II)", vri2.hasSameServer(vri1));
//
//        vri1 = new VRI("http://www.vl-e.nl/path1");
//        vri2 = new VRI("http://www.vl-e.nl:80/path2");
//
//        Assert.assertTrue("VRI with missing HTTP port must match against default port (I)", vri1.hasSameServer(vri2));
//        Assert.assertTrue("VRI with missing HTTP port must match against default port (II)", vri2.hasSameServer(vri1));
//
//        vri1 = new VRI("gftp://elab/path1");
//        vri2 = new VRI("gftp://elab:2811/path2");
//
//        Assert.assertTrue("VRI with missing GFTP port must match against default port (I)", vri1.hasSameServer(vri2));
//        Assert.assertTrue("VRI with missing GFTP port must match against default port (II)", vri2.hasSameServer(vri1));
//
//        vri1 = new VRI("gftp://elab/path1");
//        vri2 = new VRI("gsiftp://elab:2811/path2");
//
//        Assert.assertTrue("VRI with missing GFTP port must match against default port (I)", vri1.hasSameServer(vri2));
//        Assert.assertTrue("VRI with missing GFTP port must match against default port (II)", vri2.hasSameServer(vri1));

    }
    
    public void testEquals() throws VRISyntaxException
    {
       	// Test whether port<0 equals port ==0!  
    	VRI vri1 = new VRI("file:///localpath");
        VRI vri2 = new VRI("file:/localpath"); 
        Assert.assertEquals("Both triple and single slashed VRIs must match" ,vri1,vri2); 
        
       	// Test whether port<0 equals port ==0!  
        vri1 = new VRI("gftp","elab" ,-1,"/path");
        vri2 = new VRI("gftp","elab",0,"/path");
        Assert.assertEquals("Port numbers less then 0 should match against port numbers equal to 0!",vri1,vri2);
        
        vri1 = new VRI("gftp","elab",-1,"/path");
        vri2 = new VRI("gftp","elab",-2,"/path");
        Assert.assertEquals("Port numbers less then 0 should match against any other ports<0 ",vri1,vri2);

        vri1 = new VRI("gftp","elab",-1,"/path");
        vri2 = new VRI("gftp","elab",-1,"/path");
        Assert.assertEquals("Port numbers less then 0 should match against any itself",vri1,vri2); 
    }
    
    public void testLocalhostEqualsNULLHost() throws VRISyntaxException
    {
    	// check triple vs single slash. 
    	VRI vri1 = new VRI("file:///localpath");
        VRI vri2 = new VRI("file:/localpath"); 
        Assert.assertEquals("Both triple and single slashed VRIs must match" ,vri1,vri2); 
        
      
//        vri1 = new VRI("gftp","localhost" ,0,"/path");
//        vri2 = new VRI("gftp",null,0,"/path");
//        Assert.assertEquals("localhost must match NULL hostname:",vri1,vri2);
    }		
    
    //
    // When resolving file:/ URI, java URLs and URIs remove the host:port information: 
    //
    public void testRegressionLocalFileURI() throws VRISyntaxException
    {
        // if compatible to URI/URL, the actual authorization is REMOVED ! 
        
        VRI vri=new VRI("file://user@host.domain:1234/Directory/A File");
        VRI localVri=new VRI("file:/Directory/A File");
        Assert.assertEquals("Local File URI must match.",vri,localVri);
        
        vri=new VRI("file://user@host.domain:1234/Directory/AFile");
        localVri=new VRI("file:/Directory/AFile");
        Assert.assertEquals("Local File URI must match.",vri,localVri);
    }

}
