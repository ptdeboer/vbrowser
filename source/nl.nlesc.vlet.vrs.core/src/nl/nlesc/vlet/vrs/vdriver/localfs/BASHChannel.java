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

package nl.nlesc.vlet.vrs.vdriver.localfs;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import nl.esciencecenter.ptk.Global;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.vrs.io.VShellChannel;

/**
 * 
 * Open BASH Shell Channel to local filesystem 
 */
public class BASHChannel implements VShellChannel
{
    // ========================================================================
    
    // ========================================================================
    
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(BASHChannel.class);
        //logger.setLevelToDebug(); 
    }
    
    public static BASHChannel create()
    {
        return new BASHChannel(); 
        
    }
    
    // ========================================================================
    
    // ========================================================================
    private Process shellProcess=null;
    private InputStream inps=null;
    private OutputStream outps=null;
    private InputStream errs=null;

    @Override
    public OutputStream getStdin()
    {
        return outps;
    }

    @Override
    public InputStream getStdout()
    {
        return inps;
    }

    @Override
    public InputStream getStderr()
    {
        return errs;
    }

    @Override
    public void connect() throws IOException
    {
     
        this.shellProcess = null;

        try
        {
            boolean plainbash = false;
            String cmds[] = null;

            // pseudo tty which invokes bash.

            if (Global.isLinux())
            {
                cmds = new String[1];
                // linux executable .lxe :-)
                cmds[0] = VletConfig.getInstallBaseDir().getPath()+"/bin/ptty.lxe"; 
            }
            else if (Global.isWindows())
            {
                cmds = new String[1];
                cmds[0] = VletConfig.getInstallBaseDir().getPath()+"/bin/ptty.exe"; 
            }
            else
            {
                //Global.errorPrintf(this,"exec bash: Can't determine OS:%s\n",Global.getOsName());
                return; 
            }

            shellProcess = Runtime.getRuntime().exec(cmds);
            inps = shellProcess.getInputStream();
            outps = shellProcess.getOutputStream();
            errs = shellProcess.getErrorStream();

            // final PseudoTtty ptty;

            if (plainbash)
            {
                errs = shellProcess.getErrorStream();
                // ptty=new PseudoTtty(inps,outps,errs);
                // inps=ptty.getInputStream();
                // outps=ptty.getOutputStream();
            }
            else
            {
                // ptty=null;
            }
        }
        catch (Exception e)
        {
            logger.logException(ClassLogger.ERROR,e,"Couldn't initialize bash session:%s\n",e);
        }
    }

    @Override
    public void disconnect() 
    {
        if (this.shellProcess!=null)
            this.shellProcess.destroy(); 
        this.shellProcess=null; 
    }

    @Override
    public String getTermType()
    {
        return null;
    }

    @Override
    public boolean setTermType(String type)
    {
        logger.warnPrintf("Can't set TERM type to:%s\n",type);
        return false;
    }

    @Override
    public boolean setTermSize(int col, int row, int wp, int hp)
    {
        logger.warnPrintf("Can't set TERM type to:%dx%dx%dx%d\n",col,row,wp,hp); 
        return false;
    }

    @Override
    public int[] getTermSize()
    {
        return null;
    }

    public void waitFor() throws InterruptedException
    {
        this.shellProcess.waitFor(); 
    }

    public int exitValue()
    {
        return this.shellProcess.exitValue(); 
    }

}
