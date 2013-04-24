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

package nl.esciencecenter.ptk.io;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

/**
 * Global File System utils and resource loaders. 
 * Used for the local file system.
 */
public class FSUtil
{
    public static final String ENCODING_UTF8 = "UTF8";
    public static final String ENCODING_ASCII = "ASCII";

    private static ClassLogger logger;
    private static FSUtil instance = null;

    static
    {
        logger = ClassLogger.getLogger(FSUtil.class);
    }

    public static FSUtil getDefault()
    {
        if (instance == null)
            instance = new FSUtil();
        return instance;
    }
    
    // ========================================================================
    // Instance
    // ========================================================================
    
    private VRI userHome;
    private VRI workingDir; 
    private VRI tmpDir; 
    
    public FSUtil()
    {
        init();
    }

    private void init()
    {
        this.userHome = new VRI("file", null, 0, GlobalProperties.getGlobalUserHome());
        this.workingDir=new VRI("file", null, 0, GlobalProperties.getGlobalUserHome());
        this.tmpDir=new VRI("file", null, 0, GlobalProperties.getGlobalTempDir());
    }
   
    /**
     * Check syntax and decode optional (relative) URL or path to an absolute
     * normalized path. When an exception occured (syntax error) the path is
     * returned "as is" !
     * Use resolve(path) for VRI.
     */
    public String resolvePath(String path)
    {
        try
        {
            VRI vri=resolve(path); this.workingDir.resolve(path);
            vri.getPath(); 
        }
        catch (VRISyntaxException e)
        {
            logger.logException(Level.WARNING,e,"Couldn't resolve path:%s\n",path); 
        } 
        
        // Be Robuust: return AS IS !
        return path;
    }
    
    public VRI resolve(String path) throws VRISyntaxException
    {
        return this.workingDir.resolve(path);
    }
    
    public boolean existsPath(String path)
    {
        return newLocalFSNode(path).exists();
    }

    /** Pref VFS initialization file copy. */
    public void copyFile(String source, String destination) throws Exception
    {
        InputStream finput = newLocalFSNode(source).createInputStream();
        OutputStream foutput = newLocalFSNode(destination).createOutputStream();

        IOUtil.copyStreams(finput, foutput);

        try
        {
            finput.close();
        }
        catch (Exception e)
        {
            ;
        }
        try
        {
            foutput.close();
        }
        catch (Exception e)
        {
            ;
        }

        return;
    }

    /** Whether paths exists and is a file */
    public boolean existsFile(String filePath, boolean mustBeFileType)
    {
        if (filePath == null)
            return false;

        LocalFSNode file = newLocalFSNode(filePath);
        if (file.exists() == false)
            return false;

        if (mustBeFileType)
            if (file.isFile())
                return true;
            else
                return false;
        else
            return true;
    }

    /** Whether dirPath paths exists and is a directory */
    public boolean existsDir(String dirPath)
    {
        if (dirPath == null)
            return false;

        LocalFSNode file = newLocalFSNode(dirPath);
        if (file.exists() == false)
            return false;

        if (file.isDirectory())
            return true;

        return false;
    }

    /**
     * Return new local file node specified by the path.
     * 
     * @param path -  the relative,logical or absolute path to resolve on the
     *                local file system.
     */
    public LocalFSNode newLocalFSNode(String path)
    {
        return new LocalFSNode(new java.io.File(resolvePath(path)));
    }

    /** list directory: returns (URI) normalized paths */
    public String[] list(String dirPath)
    {
        LocalFSNode file = newLocalFSNode(dirPath);
        if (file.exists() == false)
            return null;

        if (file.isDirectory() == false)
            return null;

        String strs[] = file.list();
        if ((strs == null) || (strs.length <= 0))
            return null;

        // sanitize:
        for (int i = 0; i < strs.length; i++)
            strs[i] = resolvePath(dirPath + "/" + strs[i]);

        return strs;
    }

    public boolean deleteFile(String filename) throws IOException
    {
        LocalFSNode file = newLocalFSNode(filename);
        if (file.exists() == false)
            return false;
        return file.delete();
    }

    /**
     * Open local file and return InputStream to read from.
     * 
     * @param filename
     *            - relative or absolute file path (resolves to absolute path on
     *            local filesystem)
     */
    public InputStream getInputStream(String filename) throws FileNotFoundException
    {
        return newLocalFSNode(filename).createInputStream();
    }

    /**
     * Open local file and return outputstream to write to.
     * 
     * @param filename
     *            - relative or absolute file path (resolves to absolute path on
     *            local fileystem)
     */
    public OutputStream getOutputStream(String filename) throws FileNotFoundException
    {
        return newLocalFSNode(filename).createOutputStream();
    }

    /**
     * Read file and return as UTF8 String.
     * @param filename
     *            - path to resolve and read.
     */
    public String readText(String filename) throws Exception
    {
        return readText(filename, ENCODING_UTF8);
    }

    
    public String readText(String filename, String encoding) throws Exception
    {
        return readText(filename,encoding,1024*1024); 
    }    
    /**
     * Read file and return as String. Provide optional encoding (can be null for default). 
     * Limit size to maxSize
     * @param filename  
     *          - absolute of relative filepath
     * @param enconding 
     *          - optional encoding. Use null for default.
     * @param maxSize
     *          - limit size of number of bytes read (not the String size).   
     */
    public String readText(String filename, String encoding, int maxSize) throws Exception
    {
        if (encoding == null)
            encoding = ENCODING_UTF8;

        LocalFSNode file = newLocalFSNode(filename);
        int len = (int) file.length();
        if (len > maxSize)
            len = maxSize;

        InputStream finps = file.createInputStream();
        
        byte buffer[] = new byte[len + 1];

        int numRead = IOUtil.syncReadBytes(finps, 0, buffer, 0, len);
        // truncate buffer in the case of a read error:
        buffer[numRead] = 0;

        // close
        try { finps.close(); } catch (IOException e) { ; } 

        return new String(buffer, encoding);
    }

    public void writeText(String path,String txt) throws IOException
    {
        writeText(path,txt,ENCODING_UTF8);
    }

    public void writeText(String filename,String txt, String encoding) throws IOException
    {
        if (encoding == null)
            encoding = ENCODING_UTF8;

        LocalFSNode file = newLocalFSNode(filename);

        OutputStream foutps = file.createOutputStream();
        byte bytes[]=txt.getBytes(encoding); 
        int len=bytes.length; 
        foutps.write(bytes); 
                // close
        try { foutps.close(); } catch (IOException e) { ; } 
        
        long fileLen=file.length();
        if (len!=fileLen) 
            logger.warnPrintf("After writing %d byte to '%s', file length is:%d!\n",filename,len,fileLen);
            
        return; 
    }

    public LocalFSNode mkdir(String path) throws IOException
    {
        LocalFSNode dir = this.newLocalFSNode(path);
        dir.mkdir();
        return dir;
    }

    public LocalFSNode mkdirs(String path) throws IOException
    {
        LocalFSNode dir = this.newLocalFSNode(path);
        dir.mkdirs();
        return dir;
    }
   
    public LocalFSNode getLocalTempDir()
    {
        return this.newLocalFSNode(this.tmpDir.getPath());
    }
    
    public LocalFSNode getWorkingDir()
    {
        return this.newLocalFSNode(this.workingDir.getPath());
    }
    
    public VRI getUserHome()
    {
        return userHome;
    }
    
    public LocalFSNode getUserHomeDir()
    {
        return this.newLocalFSNode(this.userHome.getPath());
    }
    
    public VRI getUserHomeVRI()
    {
        return userHome;
    }
    
    public void setWorkingDir(VRI newWorkingDir)
    {
        // check for local ? 
        this.workingDir=newWorkingDir;
    }
    
    public VRI getWorkingDirVRI()
    {
        return workingDir;
    }
    
    /**
     * Returns new directory FSNode Object. Path might not exist. 
     * Use create==true to create the directory.   
     * @param  dirVri - location of new Directory 
     * @param  create - set to true to create it 
     * @return - new Local Directory object. 
     * @throws IOException 
     */
    public LocalFSNode newLocalDir(VRI vri,boolean create) throws IOException
    {
        LocalFSNode dir=this.newLocalFSNode(vri.getPath());
        if ((dir.exists()==false) && (create)) 
            dir.mkdir(); 
        return dir; 
    }

   

   
    
}
