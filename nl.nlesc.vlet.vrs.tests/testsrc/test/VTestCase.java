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


import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vrs.VRSContext;

/**
 * My own subclasses VTestCase. Added some conveniance methods.
 * 
 * @author P.T. de Boer
 */
public class VTestCase 
{
    public static final int VERBOSE_NONE = 0;

    public static final int VERBOSE_ = 1;
    
    public static final int VERBOSE_WARN = 2;

    public static final int VERBOSE_INFO = 3;

    public static final int VERBOSE_DEBUG = 4;

    static int verboseLevel = VERBOSE_WARN;

    public static void setVerbose(int level)
    {
        verboseLevel = level;
    }

    public static void verbose(int verbose, String msg)
    {
        if (verbose <= verboseLevel)
            System.out.println("testVFS:" + msg);
    }

    public static void message(String msg)
    {
        verbose(VERBOSE_INFO, msg);
    }
    
    public static void warning(String msg)
    {
        verbose(VERBOSE_WARN, msg);
    }

    public static void debug(String msg)
    {
        verbose(VERBOSE_DEBUG, msg);
    }

    public  synchronized boolean staticCheckProxy()
    {
//        if ((VRSContext.getDefault().getGridProxy().isValid() == false))
//            GridProxyDialog.askInitProxy("Grid Proxy needed for Junit tests");
        return false;
    }

    // ===
    // Instance
    // ===

    private VFSClient vfs = new VFSClient();

    public VFSClient getVFS()
    {
        return vfs;
    }

    public VRSContext getVRSContext()
    {
        return vfs.getVRSContext(); 
    }
    
}
