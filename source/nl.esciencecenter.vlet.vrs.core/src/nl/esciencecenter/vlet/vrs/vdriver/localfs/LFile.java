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

import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_UNIX_FILE_MODE;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.file.attribute.GroupPrincipal;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.io.local.LocalFSNode;
import nl.esciencecenter.ptk.io.local.LocalFSReader;
import nl.esciencecenter.ptk.io.local.LocalFSWriter;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NestedFileNotFoundException;
import nl.esciencecenter.vlet.exception.NestedIOException;
import nl.esciencecenter.vlet.exception.NotImplementedException;
import nl.esciencecenter.vlet.exception.ResourceAlreadyExistsException;
import nl.esciencecenter.vlet.exception.ResourceCreationFailedException;
import nl.esciencecenter.vlet.exception.ResourceNotFoundException;
import nl.esciencecenter.vlet.exception.ResourceNotWritableException;
import nl.esciencecenter.vlet.exception.ResourceReadAccessDeniedException;
import nl.esciencecenter.vlet.exception.ResourceWriteAccessDeniedException;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.io.VRandomAccessable;
import nl.esciencecenter.vlet.vrs.io.VResizable;
import nl.esciencecenter.vlet.vrs.io.VStreamAccessable;
import nl.esciencecenter.vlet.vrs.io.VStreamAppendable;
import nl.esciencecenter.vlet.vrs.vfs.VChecksum;
import nl.esciencecenter.vlet.vrs.vfs.VDir;
import nl.esciencecenter.vlet.vrs.vfs.VFile;
import nl.esciencecenter.vlet.vrs.vfs.VUnixFileAttributes;


/**
 * Local LFile System implementation of the VFile class
 */
public class LFile extends VFile implements VStreamAccessable, 
        VRandomAccessable, VUnixFileAttributes, VResizable, VChecksum
{
    private static ClassLogger logger;
    
    static
    {
        logger=ClassLogger.getLogger(LFile.class); 
    }
    
    private LocalFilesystem localfs;
    private LocalFSNode fsNode; 
    
    // =================================================================
    // Constructors
    // =================================================================

    /**
     * Public constructor to create new LFile.
     * 
     * @param path
     * @throws VrsException
     */
    public LFile(LocalFilesystem localFS, LocalFSNode node) throws VrsException
    {
        super(localFS, new VRL(node.getURI()));  
        this.localfs = localFS;
//
//        // windows hack: 'c:' is a relative path
//        if (path.charAt(path.length() - 1) == ':')
//        {
//            path = path + URIFactory.URI_SEP_CHAR; // make absolute !
//        }
//        // Note: on windows the file path gets converted to use BACKSLASHES
//        // for URI' use VRL.sepChar, for java Files use File.seperatorChar
//        java.io.File file = new File(path);
        init(node);
    }

    private void init(LocalFSNode node) throws VrsException
    {
        logger.debugPrintf("init():new file:%s\n",node);
        this.setLocation(new VRL(node.getURI()));
        this.fsNode=node; 
    }

    /** Returns all default attributes names */
    public String[] getAttributeNames()
    {
        String superNames[] = super.getAttributeNames();

        if (localfs.hasPosixFS())
        {
            StringList list = new StringList(superNames);
            list.add(LocalFSFactory.unixFSAttributeNames);

            return list.toArray();
        }

        return superNames;
    }

    /**
     * Returns single atttribute triplet
     * 
     * @throws VrsException
     */
    public Attribute getAttribute(String name) throws VrsException
    {
        // slowdown: logger.debugPrintf("getAttribute '%s' for:%s\n",name,this); 
        
        if (name == null)
            return null;

        // Check if super class has this attribute
        Attribute supervalue = super.getAttribute(name);

        // Super class has this attribute, and since I do not overide
        // any attribute, return this one:
        if (supervalue != null)
            return supervalue;

        // unix attributes

        if (name.compareTo(ATTR_UNIX_FILE_MODE) == 0)
            return new Attribute(name, Integer.toOctalString(getMode()));

        return null;
    }

    public VDir getParentDir() throws VrsException
    {
        return new LDir(localfs,fsNode.getParent());  
    }

    public long getSize() throws VrsException
    {
        try
        {
            return fsNode.getBasicAttributes().size();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }  
    }

    public String toString()
    {
        return getLocation().toString();
    }

    public boolean exists()
    {
        return fsNode.exists() && fsNode.isFile(); 
    }

    public boolean isReadable()
    {
        return fsNode.toJavaFile().canRead();
    }

    public boolean isWritable()
    {
        return fsNode.toJavaFile().canWrite();
    }

    public boolean create() throws VrsException
    {
        boolean result = create(true);
        return result;
    }

    public boolean create(boolean force) throws VrsException
    {
        try
        {
            if (fsNode.exists())
            {
                if (fsNode.isDirectory())
                {
                    throw new ResourceAlreadyExistsException(
                            "path already exists but is a directory:" + this);
                }

                if (force == false)
                {
                    throw new ResourceAlreadyExistsException(
                            "LFile already exists:" + this);
                }
                else
                {
                    logger.debugPrintf("Warning: Not creating existing file, but truncating:%s\n",this);
                    this.delete();
                }
            }

            // check parent:
            if (fsNode.getParent().exists() == false)
            {
                throw new ResourceCreationFailedException(
                        "Parent directory doesn't exist for file:" +fsNode);
            }

            return fsNode.create();
        }
        catch (IOException e)
        {
            throw new ResourceCreationFailedException("Couldn't create file:"
                    + fsNode, e);
        }
    }

    public boolean delete() throws VrsException
    {
        // _File.delete doesn't provide much information
        // so precheck delete conditions:

        if (this.fsNode.exists() == false)
        {
            throw new ResourceNotFoundException("File doesn't exist:" + this);
        }
        else if (this.isWritable() == false)
        {
            throw new ResourceWriteAccessDeniedException(
                    "No permissions to delete this file:" + this);
        }
        
        try
        {
            fsNode.delete();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
        
        return true; 
    }

    public VRL rename(String newname, boolean nameIsPath)
            throws VrsException
    {
        File newFile = localfs.renameTo(this.getPath(), newname, nameIsPath);

        if (newFile!=null) 
        {
            return new VRL(newFile.toURI());   
        }
        
        return null;  
    }

    public long getLength() throws IOException 
    {
        try
        {
            return fsNode.length();
        }
        catch (IOException e)
        {
            if (fsNode.isBrokenLink())
            {
                return 0; 
            }
            else throw e;
        }
    }

    public long getModificationTime() throws VrsException
    {
        try
        {
            return fsNode.getModificationTime();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    public boolean isHidden()
    {
        return fsNode.isHidden();
    }

    /** Local File is local */ 
    public boolean isLocal()
    {
        return true;
    }

    public InputStream createInputStream() throws IOException
    {
        return fsNode.createInputStream(); 
    }

    public OutputStream createOutputStream() throws IOException 
    {
        return fsNode.createOutputStream(); 
    }

    // Method from VRandomAccessable:
    public void setLength(long newLength) throws IOException
    {
        RandomAccessFile afile = null;
        
        try
        {
            afile = new RandomAccessFile(fsNode.toJavaFile(), "rw");
            afile.setLength(newLength);
        }
        finally
        {
            if (afile!=null)
            {
                try
                {
                    // Must close between Reads! (not fast but ensures consistency between reads). 
                    afile.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        
        return;
    }

    // Method from VRandomAccessable:
    public int readBytes(long fileOffset, byte[] buffer, int bufferOffset,
            int nrBytes) throws IOException
    {
        return new LocalFSReader(fsNode).readBytes(fileOffset,buffer,bufferOffset,nrBytes); 
    }

    // Method from VRandomAccessable:
    public void writeBytes(long fileOffset, byte[] buffer, int bufferOffset,
            int nrBytes) throws IOException
    {
        new LocalFSWriter(fsNode).writeBytes(fileOffset,buffer,bufferOffset,nrBytes); 
    }

    public void setLengthToZero() throws IOException
    {
        setLength(0);
        sync();
    }

    @Override
    public String getSymbolicLinkTargetPath() throws VrsException
    {
        if (isSymbolicLink() == false)
        {
            logger.debugPrintf("*** WARNING: getLinkTarget:not a link:%s\n",this);
            return null;
        }

        try
        {
            LocalFSNode targetNode = fsNode.getSymbolicLinkTarget();
            if (targetNode==null)
                return null; 
            
            return targetNode.getPathname(); 
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        } 
        
    }

    public boolean isSymbolicLink() throws VrsException
    {
        return fsNode.isSymbolicLink(); 
    }


    public void setMode(int mode) throws VrsException
    {
        try
        {
            fsNode.setUnixFileMode(mode);
            sync();
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    public boolean sync()
    {
        return fsNode.sync(); 
    }
    
    public String getChecksum(String algorithm) throws VrsException
    {
        String[] types = getChecksumTypes();
        try
        {
            for (int i = 0; i < types.length; i++)
            {
                if (algorithm.equalsIgnoreCase(types[i]))
                {
                    InputStream in = this.createInputStream();
                    algorithm = algorithm.toUpperCase();
                    return ChecksumUtil.calculateChecksum(in, algorithm);
                }
            }
            throw new NotImplementedException(algorithm
                    + " Checksum algorithm is not implemented ");

        }
        catch (IOException e)
        {
            throw new NestedIOException(e);
        }

    }

    public String[] getChecksumTypes()
    {
        return new String[] { VChecksum.MD5, VChecksum.ADLER32 };
    }

    public String getGid() throws VrsException
    {
        try
        {
            return fsNode.getPosixAttributes().group().getName();
        }
        catch (IOException e)
        {
           throw new VrsException(e.getMessage(),e); 
        }   
    }

    public String getUid() throws VrsException
    {
        try
        {
            return fsNode.getPosixAttributes().owner().getName();
        }
        catch (IOException e)
        {
           throw new VrsException(e.getMessage(),e); 
        } 
    }

    public int getMode() throws VrsException
    {
        try
        {
            return fsNode.getUnixFileMode();
        }
        catch (IOException e)
        {
           throw new VrsException(e.getMessage(),e); 
        } 
    }

  
}
