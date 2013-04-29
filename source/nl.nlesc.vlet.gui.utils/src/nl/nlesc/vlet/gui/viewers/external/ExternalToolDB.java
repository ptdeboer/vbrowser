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

package nl.nlesc.vlet.gui.viewers.external;

import java.util.Vector;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.vrs.vdriver.localfs.LocalExec;
import nl.nlesc.vlet.vrs.vdriver.localfs.LocalProcess;

public class ExternalToolDB
{
    /** Java Web Start binary */ 
    public static final String JAVAWS="javaws";
    
    /** Java Binary */ 
    public static final String JAVA="java"; 
    
    private static ExternalToolDB instance=null;
    
    public static ExternalToolDB getDefault()
    {
        synchronized(ExternalToolDB.class)
        {
            if (instance==null)
                instance=new ExternalToolDB();
            return instance;
        }
    }
    
    // ========================================================================
    // 
    // ========================================================================
    
    // plain vector: 
    private Vector<ExternalToolInfo> toolInfos=new Vector<ExternalToolInfo>();
    
    private ExternalToolDB()
    {
        initToolDB(); 
    }
    
    private void initToolDB()
    {
        // add linux echo command: 
        toolInfos.add(ExternalToolInfo.createLocalCommand("/bin/echo",true,false));
    }
    
    /** 
     * Resolve command: checks for 'standard' command line commands. 
     * When an absolute path is specified, for example "/bin/bash" this 
     * complete path will be returned. 
     */ 
    public String getCommandPath(String cmd)
    {
        String cmdpath=null;
        
        // return absolute command path ! 
        if (cmd.startsWith("/"))
            return cmd; 
            
        if (cmd.equals(JAVAWS))
        {
            String javahome=VletConfig.getProperty("java.home");
            cmdpath=javahome+"/bin/javaws";
            if (GlobalProperties.isWindows())
                cmdpath+=".exe"; 
        }
        else if (cmd.equals(JAVA))
        {
            String javahome=VletConfig.getProperty("java.home");
            cmdpath=javahome+"/bin/java";
            if (GlobalProperties.isWindows())
                cmdpath+=".exe"; 
        }
        
        if (cmdpath!=null)
            return cmdpath; 
        
        return cmd; 
    }

    /**
     *  Simple starter: Start tool/command with plain VRL as argument. 
     *  Does not download the VRL and uses the plain string representation as first argument
     *  to start the tool.  
     *  Tries to resolve the command 'cmd' to a local binary. 
     */ 
    public LocalProcess executeVrl(String cmd, VRL vrl, boolean waitFor) throws VrsException
    {
        String cmdPath=this.getCommandPath(cmd); 
        
        String cmds[]=new String[2]; 
        cmds[0]=cmdPath;  
        cmds[1]=vrl.toString(); 

        return LocalExec.execute(cmds,waitFor); 
    }
    
    
}
