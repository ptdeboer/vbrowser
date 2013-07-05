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


import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.util.VRSResourceLoader;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;

/**
 * My own subclasses VTestCase. Added some conveniance methods.
 * 
 * @author P.T. de Boer
 */
public class VTestCase 
{
    public static final int VERBOSE_NONE = 10;

    public static final int VERBOSE_ERROR = 8;

    public static final int VERBOSE_WARN = 6;

    public static final int VERBOSE_INFO = 4;

    public static final int VERBOSE_DEBUG = 2;

    int verboseLevel = VERBOSE_INFO;

    public void setVerbose(int level)
    {
        verboseLevel = level;
    }

    public  void verbose(int verbose, String msg)
    {
        if (verbose >= verboseLevel)
            System.out.println("testVFS:" + msg);
    }

    public  void errorPrintf(String format,Object... args)
    {
        if (VERBOSE_INFO >= verboseLevel)
           System.out.printf(format,args); 
    }

    public  void warnPrintf(String format,Object... args)
    {
        if (VERBOSE_WARN >= verboseLevel)
           System.out.printf(format,args); 
    }

    public  void message(String msg)
    {
        verbose(VERBOSE_INFO, msg);
    }

    public  void messagePrintf(String format,Object... args)
    {
        if (VERBOSE_INFO >= verboseLevel)
           System.out.printf(format,args); 
    }

    public  void debug(String msg)
    {
        verbose(VERBOSE_DEBUG, msg);
    }

    public  void debugPrintf(String format,Object... args)
    {
        if (VERBOSE_DEBUG >= verboseLevel)
           System.out.printf(format,args); 
    }
   
    // ===
    // Instance
    // ===

    protected VFSClient vfs = new VFSClient();

    protected VRSResourceLoader resourceLoader=null;

    protected VFSClient getVFS()
    {
        return vfs;
    }

    protected VRSContext getVRSContext()
    {
        return vfs.getVRSContext(); 
    }
    
    protected VRSResourceLoader getResourceLoader()
    {
        if (this.resourceLoader==null)
        {
            this.resourceLoader=new VRSResourceLoader(getVRSContext()); 
        }
        
        return this.resourceLoader; 
    }

}
