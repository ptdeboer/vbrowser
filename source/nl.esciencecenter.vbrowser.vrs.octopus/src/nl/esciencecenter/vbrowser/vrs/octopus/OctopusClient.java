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

package nl.esciencecenter.vbrowser.vrs.octopus;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import nl.esciencecenter.octopus.Octopus;
import nl.esciencecenter.octopus.credentials.Credential;
import nl.esciencecenter.octopus.credentials.Credentials;
import nl.esciencecenter.octopus.engine.OctopusEngine;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.exceptions.OctopusIOException;
import nl.esciencecenter.octopus.files.AbsolutePath;
import nl.esciencecenter.octopus.files.DirectoryStream;
import nl.esciencecenter.octopus.files.FileAttributes;
import nl.esciencecenter.octopus.files.FileSystem;
import nl.esciencecenter.octopus.files.OpenOption;
import nl.esciencecenter.octopus.files.PathAttributesPair;
import nl.esciencecenter.octopus.files.PosixFilePermission;
import nl.esciencecenter.octopus.files.RelativePath;
import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;

public class OctopusClient
{
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(OctopusClient.class); 
        logger.setLevelToDebug(); 
    }
    
    /** 
     * Create OctopusClient and initalize Octopuse Engine for the specfied VRSContext. 
     * Optionally add properties from ServerInfo to the initialization. 
     * 
     * @param vrsContext - The VRSContext  
     * @param serverInfo - specific remote resource configuration. 
     * @return new OctopusClient. 
     * @throws VrsException
     */
    public static OctopusClient createFor(VRSContext vrsContext, ServerInfo serverInfo) throws VrsException
    {
        // check shared clients here. 
        try
        {
            Properties props=new Properties();

            // lib subdirs. when started from eclipse, these might not exist!
            String subDirs[]={"octopus","auxlib/octopus","vdriver/octopus"};
            
            for (String subDir:subDirs)
            {
                String octoDir=VletConfig.getInstallationLibDir().resolvePath(subDir).getPath(); 
                if (FSUtil.getDefault().existsDir(octoDir))
                {
                    ClassLogger.getLogger(OctopusClient.class).infoPrintf("Using ocotopus dir:%s\n",octoDir); 
                    props.put("octopus.adaptor.dir",octoDir);
                    break;
                }
            }
            
            OctopusClient client = new OctopusClient(props); 
            client.updateProperties(vrsContext,serverInfo); 
            return client;
        }
        catch (Exception e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }
    public static class NillPathAttributesPair implements  PathAttributesPair
    {
        AbsolutePath path; 
        FileAttributes fileAttrs; 
        Exception exception; 
        
        NillPathAttributesPair(AbsolutePath octoPath, FileAttributes attrs)
        {
            this.path=octoPath; 
            this.fileAttrs=attrs;  
        }

        NillPathAttributesPair(AbsolutePath octoPath, FileAttributes attrs, Exception e)
        {
            this.path=octoPath; 
            this.fileAttrs=attrs;  
            this.exception=e; 
        }

        @Override
        public AbsolutePath path()
        {
            return path;
        }

        @Override
        public FileAttributes attributes()
        {
            return fileAttrs;
        }
    }
    // === instance == 
    
    private Octopus engine;
    private Properties octoProperties;
    private VRL userHomeDir;
    private String userName;

    /**
     * Protected constructor: Use factory method.
     */
    protected OctopusClient(Properties props) throws OctopusException
    {
        octoProperties=props;
        //octoCredentials=new Credentials(); 
        engine=OctopusEngine.newOctopus(octoProperties); 
    }
    
    protected void updateProperties(VRSContext context, ServerInfo info)
    {
        this.userName=info.getUsername(); 
        this.userHomeDir=context.getUserHomeLocation();  
    }
    
    public String getUsername()
    {
        return this.userName; 
    }
    
    public VRL getUserHome()
    {
        return this.userHomeDir; 
    }
    
    public AbsolutePath resolvePath(FileSystem octoFS,String pathString) throws OctopusIOException, OctopusException
    {
        boolean startsWithTilde=false;  
                
        if ( (pathString.startsWith("~")) || (pathString.startsWith("/~")) )  
        {
            if (pathString.startsWith("/~"))
            {
                pathString=pathString.substring(2);
            }
            else
            {
                pathString=pathString.substring(1);
            }
            
            startsWithTilde=true; 
        }
        
        RelativePath relativePath=new RelativePath(pathString);
        AbsolutePath path=engine.files().newPath(octoFS, relativePath); 

        return path; 
    }
    
    public AbsolutePath resolvePath(FileSystem octoFS, RelativePath relativePath) throws OctopusIOException, OctopusException
    {
        return engine.files().newPath(octoFS, relativePath); 
    }
    
    public FileSystem createFileSystem(java.net.URI uri) throws OctopusIOException, OctopusException
    {
        return engine.files().newFileSystem(uri, null, octoProperties);
    }
 
    public FileSystem createFileSystem(java.net.URI uri,Credential cred) throws OctopusIOException, OctopusException
    {
        return engine.files().newFileSystem(uri, cred, octoProperties);
    }
    
    public FileAttributes statPath(AbsolutePath path) throws OctopusIOException
    {
        return engine.files().getAttributes(path); 
    }

    /**
     *  Stat Directory including attributes
      */ 
    public List<PathAttributesPair> statDir(AbsolutePath octoPath) throws OctopusIOException
    {
        DirectoryStream<PathAttributesPair> dirIterator = engine.files().newAttributesDirectoryStream(octoPath); 

        Iterator<PathAttributesPair> iterator = dirIterator.iterator(); 
        
        List<PathAttributesPair> paths=new ArrayList<PathAttributesPair>(); 
        
        Exception lastException=null;  
        int count=0; 
        
        while(iterator.hasNext())
        {
            PathAttributesPair el=null; 
            
            try
            {
                el= iterator.next();
                paths.add(el);
            }
            catch (Exception e)
            {
                // happens when file is a borken link. but can check that here. 
                lastException=e;
                paths.add(new NillPathAttributesPair(octoPath.resolve(new RelativePath("?#"+count)),null,e));
                logger.logException(ClassLogger.ERROR, this, e, "Couldn't get next when listing directory:"+octoPath);
            }
            count++;
        }
        
        if (paths.size()==0)
            return null; 
        
        return paths;
    }

    /** 
     * list files only without attributes 
     */ 
    public List<AbsolutePath> listDir(AbsolutePath octoAbsolutePath) throws OctopusIOException
    {
        DirectoryStream<AbsolutePath> dirIterator = engine.files().newDirectoryStream(octoAbsolutePath); 

        Iterator<AbsolutePath> iterator = dirIterator.iterator(); 
        
        List<AbsolutePath> paths=new ArrayList<AbsolutePath>(); 
        
        while(iterator.hasNext())
        {
            AbsolutePath el = iterator.next();
            paths.add(el); 
            System.err.printf("***>> adding:%s\n",el.getPath());
        }
        
        if (paths.size()==0)
            return null; 
        
        return paths;
    }

    public FileAttributes getFileAttributes(AbsolutePath octoAbsolutePath) throws OctopusIOException
    {
        return engine.files().getAttributes(octoAbsolutePath); 
    }

    public boolean deleteFile(AbsolutePath octoAbsolutePath, boolean force) throws OctopusIOException
    {
        engine.files().delete(octoAbsolutePath); 
        return true; // no exceptions 
    }
    
    public boolean exists(AbsolutePath octoAbsolutePath) throws OctopusIOException
    {
        return engine.files().exists(octoAbsolutePath); 
    }

    public AbsolutePath mkdir(AbsolutePath octoAbsolutePath) throws OctopusIOException
    {
        return engine.files().createDirectory(octoAbsolutePath); //,getDefaultDirPermissions());
    }

    public Set<PosixFilePermission> createPermissions(int mode)
    {
        Set<PosixFilePermission> set=new HashSet<PosixFilePermission>();

        // [d]rwx------
        if ((mode & 0400)>0)
            set.add(PosixFilePermission.OWNER_READ); 
        if ((mode & 0200)>0)
            set.add(PosixFilePermission.OWNER_WRITE); 
        if ((mode & 0100)>0)
            set.add(PosixFilePermission.OWNER_EXECUTE); 

        // [d]---rwx---
        if ((mode & 0040)>0)
            set.add(PosixFilePermission.GROUP_READ); 
        if ((mode & 0020)>0)
            set.add(PosixFilePermission.GROUP_WRITE); 
        if ((mode & 0010)>0)
            set.add(PosixFilePermission.GROUP_EXECUTE); 

        // [d]------rwx
        if ((mode & 0004)>0)
            set.add(PosixFilePermission.OTHERS_READ); 
        if ((mode & 0002)>0)
            set.add(PosixFilePermission.OTHERS_WRITE); 
        if ((mode & 0001)>0)
            set.add(PosixFilePermission.OTHERS_EXECUTE); 
        return set;
    }        

    public int getUnixFileMode(Set<PosixFilePermission> set)
    {
        int mode=0; 

        // [d]rwx------
        if (set.contains(PosixFilePermission.OWNER_READ))
            mode |= 0400; 
        if (set.contains(PosixFilePermission.OWNER_WRITE))
            mode |= 0200; 
        if (set.contains(PosixFilePermission.OWNER_EXECUTE))
            mode |= 0100; 
        
        // [d]---rwx---
        if (set.contains(PosixFilePermission.GROUP_READ))
            mode |= 0040; 
        if (set.contains(PosixFilePermission.GROUP_WRITE))
            mode |= 0020; 
        if (set.contains(PosixFilePermission.GROUP_EXECUTE))
            mode |= 0010; 

        // [d]------rwx
        if (set.contains(PosixFilePermission.OTHERS_READ))
            mode |= 0004; 
        if (set.contains(PosixFilePermission.OTHERS_WRITE))
            mode |= 0002; 
        if (set.contains(PosixFilePermission.OTHERS_EXECUTE))
            mode |= 0001; 
        
        return mode; 
    }
    
    public Set<PosixFilePermission> getDefaultFilePermissions()
    {
        return createPermissions(0644); // octal
    }
    
    public Set<PosixFilePermission> getDefaultDirPermissions()
    {
        return createPermissions(0755); // octal 
    }
     
    public AbsolutePath createFile(AbsolutePath octoAbsolutePath) throws OctopusIOException
    {
        return engine.files().createFile(octoAbsolutePath); //, getDefaultFilePermissions());
    }

    public InputStream createInputStream(AbsolutePath octoAbsolutePath) throws OctopusIOException
    {
        return engine.files().newInputStream(octoAbsolutePath);
    }

    // Open existing file and rewrite contents. If appen==true the OutputStream
    // will start at the end of the file. 
    public OutputStream createRewritingOutputStream(AbsolutePath path, boolean append) throws IOException
    {
        OpenOption opts[]=new OpenOption[1];

        if (append)
        {   
            opts[0]=OpenOption.CREATE;
        }
        else
        {
            opts[0]=OpenOption.APPEND;
        }
        
        return engine.files().newOutputStream(path, opts); 
        
    }

    // Create new OutputStream, optionally create new file if it doesn exists,
    // old file will be deleted. 
    public OutputStream createNewOutputStream(AbsolutePath path, boolean ignoreExisting) throws IOException
    {
        OpenOption opts[]=new OpenOption[2];
        
        if (ignoreExisting)
        {
            opts[0]=OpenOption.CREATE;
        }
        else
        {
            opts[0]=OpenOption.CREATE_NEW;
        }
        
        opts[1]=OpenOption.TRUNCATE_EXISTING;
        
        return engine.files().newOutputStream(path, opts);
        
    }
    
    public void rmdir(AbsolutePath octoAbsolutePath) throws OctopusIOException
    {
        //DeleteOption options;
        engine.files().delete(octoAbsolutePath) ;; // (octoAbsolutePath,options);  
    }

    public AbsolutePath rename(AbsolutePath oldAbsolutePath, AbsolutePath newAbsolutePath) throws VrsException, OctopusIOException
    {
        // Move must here be a rename on the same filesystem!

        if (checkSameFilesystem(oldAbsolutePath,newAbsolutePath)==false)
            throw new VrsException("Cannot rename file when new file is on other file system:"+oldAbsolutePath+"=>"+newAbsolutePath); 
        
        AbsolutePath actualAbsolutePath=engine.files().move(oldAbsolutePath, newAbsolutePath);
        return actualAbsolutePath;
    }

    public boolean checkSameFilesystem(AbsolutePath path1, AbsolutePath path2)
    {
        URI uri1 = path1.getFileSystem().getUri(); 
        URI uri2 = path2.getFileSystem().getUri(); 
        
        if (StringUtil.compare(uri1.getHost(),uri2.getHost())!=0) 
            return false;
        
//        if (StringUtil.compare(uri1.getHost(),uri2.getHost())!=0) 
//            return false;
        
        return true; 
    }
    
    public Credential createSSHCredentials(ServerInfo info) throws OctopusException, VRLSyntaxException
    {
        String sshUser=info.getUsername(); 
        String ssh_id_key_file=info.getAttributeValue(ServerInfo.ATTR_SSH_IDENTITY);          
        char passWord[]=null; 
        
        if (StringUtil.isEmpty(ssh_id_key_file)==false)
        {
            // ssh_id_key_file can be absolute here: 
            VRL idFile=getUserHome().resolvePath(".ssh").resolvePath(ssh_id_key_file);  
         
            if (FSUtil.getDefault().existsFile(idFile.getPath(), true))
            {
                ssh_id_key_file=idFile.getPath();
            }
            else
            {
                ssh_id_key_file=null; // do not use!
            }
         
        }

        logger.debugPrintf("createSSHCredentials(): Using Username:"+sshUser);
        logger.debugPrintf("createSSHCredentials(): Using ID Key file:%s\n",ssh_id_key_file);
        
        Credentials creds = engine.credentials();
        Credential cred;
        
        if (ssh_id_key_file==null)
        {
            cred= creds.newCertificateCredential("ssh", 
                    null, 
                    null,
                    null, 
                    sshUser, 
                    passWord);
        }
        else
        {
            cred= creds.newCertificateCredential("ssh", 
                    null, 
                    ssh_id_key_file, 
                    ssh_id_key_file+".pub",
                    sshUser, 
                    passWord);
        }
        
        return cred; 
    }

   

}
