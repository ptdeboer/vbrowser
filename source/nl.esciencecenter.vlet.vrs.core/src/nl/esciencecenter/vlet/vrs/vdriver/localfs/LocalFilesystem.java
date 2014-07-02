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

package nl.esciencecenter.vlet.vrs.vdriver.localfs;

import java.io.File;
import java.io.IOException;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.io.exceptions.FileURISyntaxException;
import nl.esciencecenter.ptk.io.FSPath;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsIOException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.InternalError;
import nl.esciencecenter.vlet.exception.ResourceAlreadyExistsException;
import nl.esciencecenter.vlet.exception.ResourceCreationFailedException;
import nl.esciencecenter.vlet.exception.ResourceNotFoundException;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.FileSystemNode;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.vlet.vrs.vfs.VFile;

/**
 * Implementation of the LocalFilesystem.
 */
public class LocalFilesystem extends FileSystemNode
{
    public static final String DEFAULT_LOCALFS_SERVERID = "localfs";

    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(LocalFilesystem.class);
    }

    // ========================================================================
    // Instance
    // ========================================================================

    private FSUtil fsUtil;

    public LocalFilesystem(VRSContext context, ServerInfo info, VRL location)
    {
        // Use one ServerInfo for all !
        super(context, context.getServerInfoRegistry().getServerInfoFor(new VRL("file", null, "/"), true));
        fsUtil=FSUtil.getDefault(); 
    }

    @Override
    public VFSNode openLocation(VRL location) throws VrsException
    {
        // cannot handle location other then for localhost,
        // so location MUST be a local file path
        Debug("path=" + location.getPath());
        return getPath(location.getPath());
    }

    @Override
    public void connect() throws VrsException
    {
        fsUtil=FSUtil.getDefault(); 
    }

    @Override
    public void disconnect() throws VrsException
    {
        fsUtil=null; 
    }

    @Override
    public final String getHostname()
    {
        return VRS.LOCALHOST;
    }

    @Override
    public String getID()
    {
        return DEFAULT_LOCALFS_SERVERID;
    }

    @Override
    public int getPort()
    {
        return 0;
    }

    @Override
    public String getScheme()
    {
        return VFS.FILE_SCHEME;
    }

    @Override
    public VRL getServerVRL()
    {
        return new VRL("file", null, "/");
    }

    @Override
    public boolean isConnected()
    {
        return true;
    }

    @Override
    public VFSNode getPath(String path) throws VrsException
    {
        if (path == null)
            throw new InternalError("Path is NULL");

        // System.setSecurityManager(null);
        // resolve ~:
        if (path.startsWith("~"))
        {
            path = GlobalProperties.getProperty("user.home") + URIFactory.URI_SEP_CHAR_STR + path.substring(1);
        }
        else if (path.startsWith("/~"))
        {
            path = GlobalProperties.getProperty("user.home") + URIFactory.URI_SEP_CHAR_STR + path.substring(2);
        }

        FSPath node;
        
        try
        {
            node = fsUtil.newFSPath(path);
        }
        catch (IOException e)
        {
            throw new ResourceNotFoundException("Invalid path:"+path,e);
        } 
     
        
        if (node.exists() == true)
        {
//            if (node.isSymbolicLink()&&node.isBrokenLink())
//            {
//                return new LFile(this, node);
//            }
            if (node.isFile())
            {
                return new LFile(this, node);
            }
            else if (node.isDirectory())
            {
                LDir dir = new LDir(this, node);
                return dir;
            }
        }
        
        try
        {
            // broken link handling: 
            if (node.isBrokenLink())
            {
                return new LFile(this, node);
            }
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }

//        // ===================================================
//        // Windows hack. The drive path "/A:/" doesn't exists
//        // if there is no floppy in the drive!
//        // Check normalized windows drive path: "/C:/" (or "/A:/")
//        // If "A:" then return as (existing) Directory !
//        // ===================================================
//
//        if ((path.length() == 4) && (path.substring(2).compareTo(":/") == 0))
//        {
//            LDir dir = new LDir(this, file);
//            return dir;
//        }

        throw new ResourceNotFoundException("Couldn't locate path:" + path);
    }

      private static void Debug(String str)
    {
        logger.debugPrintf("%s\n", str);
    }

    public File renameTo(String filepath, String newname, boolean nameIsPath)
    {
        String fullname = null;
        // local filesystem path, use File.seperatorChar
        Debug("Renaming:" + filepath + " to: (nameIsPath=" + nameIsPath + ")" + newname);

        if (nameIsPath)
        {
            fullname = newname;
        }
        else
        {
            fullname = URIFactory.dirname(filepath) + URIFactory.URI_SEP_CHAR + newname;
        }

        Debug("newfilename=" + fullname);
        java.io.File newfile = new java.io.File(fullname);
        java.io.File _file = new java.io.File(filepath);
        Debug("newfilename.absolute =" + newfile.getAbsolutePath());
        try
        {
            Debug("newfilename.canonical=" + newfile.getCanonicalPath());
        }
        catch (IOException e)
        {
            logger.logException(ClassLogger.ERROR, this, e, "IOException:%s\n", e);
        }

        // Again nothing is throw if it failes.
        _file.renameTo(newfile);

        if (newfile.exists())
            return newfile;
        else
        {
            // ?
            return null;
        }
    }

    static void checkExitStatus(String[] result) throws VrsException
    {
        if ((result != null) && (result.length > 2))
        {
            int status = Integer.parseInt(result[2]);

            if (status != 0)
            {
                throw VrsException.create("Exit status=" + status + "\n. stdout=" + result[1]
                        + "\n. stderr=" + result[2], null,"Execution Error");
            }
        }

    }
   
    @Override
    public VFile createFile(VRL fileVrl, boolean force) throws VrsException
    {
        return createFile(fileVrl.getPath(), force);
    }

    public VFile createFile(String name, boolean force) throws VrsException
    {
        // URI: use forward slash:
        String loc = resolvePathString(name);

        FSPath node;
        try
        {
            node = fsUtil.newFSPath(loc);
        }
        catch (IOException e)
        {
            throw new VrsIOException(e.getMessage(),e);
        } 

        if (node.exists() == true)
        {
            if (node.isFile() == false)
                throw new ResourceCreationFailedException("File path already exists, but is not a file:" + loc);

            if (force == false)
                throw new ResourceAlreadyExistsException("File path already exists:" + this);

            // delete existing file:!
            try
            {
                node.delete();
            }
            catch (IOException e)
            {
                ;
            }
        }

        // create file:

        LFile lfile = new LFile(this, node);
        lfile.create();

        return lfile;
    }

    @Override
    public boolean existsFile(VRL fileVrl) throws VrsException
    {
        java.io.File f = new File(fileVrl.getPath());
        return (f.exists() && f.isFile());
    }

    @Override
    public boolean existsDir(VRL fileVrl) throws VrsException
    {
        java.io.File f = new File(fileVrl.getPath());
        return (f.exists() && f.isDirectory());
    }

    @Override
    public VDir newDir(VRL dirPath) throws VrsException
    {
        try
        {
            return new LDir(this, fsUtil.newFSPath(dirPath.toURINoException()));
        }
        catch (IOException e)
        {
            throw new VrsIOException(e.getMessage(),e);
        }
    }

    @Override
    public VFile newFile(VRL fileVrl) throws VrsException
    {
        try
        {
            return new LFile(this, fsUtil.newFSPath(fileVrl.toURINoException()));
        }
        catch (IOException e)
        {
            throw new VrsIOException(e.getMessage(),e);
        }

        
    }

    public boolean hasPosixFS()
    {
        return fsUtil.hasPosixFS(); 
    }
}
