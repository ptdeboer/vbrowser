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

package nl.esciencecenter.vbrowser.vrs.xenon;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.esciencecenter.xenon.Xenon;
import nl.esciencecenter.xenon.XenonException;
import nl.esciencecenter.xenon.credentials.Credential;
import nl.esciencecenter.xenon.credentials.Credentials;
import nl.esciencecenter.xenon.engine.XenonEngine;
import nl.esciencecenter.xenon.files.Path;
import nl.esciencecenter.xenon.files.DirectoryStream;
import nl.esciencecenter.xenon.files.FileAttributes;
import nl.esciencecenter.xenon.files.FileSystem;
import nl.esciencecenter.xenon.files.OpenOption;
import nl.esciencecenter.xenon.files.PathAttributesPair;
import nl.esciencecenter.xenon.files.PosixFilePermission;
import nl.esciencecenter.xenon.files.RelativePath;
import nl.esciencecenter.ptk.crypt.Secret;
import nl.esciencecenter.ptk.data.SecretHolder;
import nl.esciencecenter.ptk.io.FSUtil;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRSContext;

public class XenonClient
{
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(XenonClient.class); 
        logger.setLevelToDebug(); 
    }
    
    /** 
     * Create XenonClient and initialize Xenon Engine for the specified VRSContext. 
     * Optionally add properties from ServerInfo to the initialization. 
     * 
     * @param vrsContext - The VRSContext  
     * @param serverInfo - specific remote resource configuration. 
     * @return new XenonClient. 
     * @throws VrsException
     */
    public static XenonClient createFor(VRSContext vrsContext, ServerInfo serverInfo) throws VrsException
    {
        // check shared clients here. 
        try
        {
            Map<String,String> props=new Hashtable<String,String>(); 

            // lib subdirs. when started from eclipse, these might not exist!
            String subDirs[]={"xenon","auxlib/xenon","vdriver/xenon"};
            
            for (String subDir:subDirs)
            {
                String octoDir=VletConfig.getInstallationLibDir().resolvePath(subDir).getPath(); 
                if (FSUtil.getDefault().existsDir(octoDir))
                {
                    ClassLogger.getLogger(XenonClient.class).infoPrintf("Using ocotopus dir:%s\n",octoDir); 
                    props.put("xenon.adaptor.dir",octoDir);
                    break;
                }
            }
            
            XenonClient client = new XenonClient(props); 
            client.updateProperties(vrsContext,serverInfo); 
            return client;
        }
        catch (Exception e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }
    
    /** 
     * Dummy or Nill PathAttributesPair object to use when fetching the FileAttributes fails.  
     */
    public static class NillPathAttributesPair implements  PathAttributesPair
    {
        Path path; 
        FileAttributes fileAttrs; 
        Exception exception; 
        
        NillPathAttributesPair(Path octoPath, FileAttributes attrs)
        {
            this.path=octoPath; 
            this.fileAttrs=attrs;  
        }

        NillPathAttributesPair(Path octoPath, FileAttributes attrs, Exception e)
        {
            this.path=octoPath; 
            this.fileAttrs=attrs;  
            this.exception=e; 
        }

        @Override
        public Path path()
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
    
    private Xenon engine;
    private Map<String, String> octoProperties;
    private VRL userHomeDir;
    private String userName;
    private VRSContext vrsContext;

    /**
     * Protected constructor: Use factory method.
     */
    protected XenonClient(Map<String,String> props) throws XenonException
    {
        octoProperties=props;
        //octoCredentials=new Credentials(); 
        engine=XenonEngine.newXenon(octoProperties); 
    }
    
    protected void updateProperties(VRSContext context, ServerInfo info)
    {
        this.userName=info.getUsername(); 
        this.userHomeDir=context.getUserHomeLocation();  
        this.vrsContext=context; 
    }
    
    public String getUsername()
    {
        return this.userName; 
    }
    
    public VRL getUserHome()
    {
        return this.userHomeDir; 
    }
    
    public Path resolvePath(FileSystem octoFS,String pathString) throws XenonException
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
        Path path=engine.files().newPath(octoFS, relativePath); 

        return path; 
    }
    
    public Path resolvePath(FileSystem octoFS, RelativePath relativePath) throws XenonException, XenonException
    {
        return engine.files().newPath(octoFS, relativePath); 
    }
    
    public FileSystem createFileSystem(java.net.URI uri) throws XenonException, XenonException
    {
        return engine.files().newFileSystem(uri.getScheme(),"/", null, octoProperties);
    }
 
    public FileSystem createFileSystem(java.net.URI uri,Credential cred) throws XenonException, XenonException
    {
        return engine.files().newFileSystem(uri.getScheme(),"/", cred, octoProperties);
    }
    
    public FileAttributes statPath(Path path) throws XenonException
    {
        return engine.files().getAttributes(path); 
    }

    /**
     *  Stat Directory including attributes
     * @throws XenonException 
      */ 
    public List<PathAttributesPair> statDir(Path octoPath) throws XenonException, XenonException
    {
        DirectoryStream<PathAttributesPair> dirIterator = engine.files().newAttributesDirectoryStream(octoPath); 

        Iterator<PathAttributesPair> iterator = dirIterator.iterator(); 
        
        List<PathAttributesPair> paths=new ArrayList<PathAttributesPair>(); 
        
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
                // happens when file is a borken link. but can not check that here. 
                // lastException=e;
                //RelativePath dummyPath=octoPath.getRelativePath().resolve("?#"+count);
                Path dummyPath = resolvePath(octoPath.getFileSystem(),"?#"+count); 
                paths.add(new NillPathAttributesPair(dummyPath,null,e));
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
    public List<Path> listDir(Path octoPath) throws XenonException
    {
        DirectoryStream<Path> dirIterator = engine.files().newDirectoryStream(octoPath); 

        Iterator<Path> iterator = dirIterator.iterator(); 
        
        List<Path> paths=new ArrayList<Path>(); 
        
        while(iterator.hasNext())
        {
            Path el = iterator.next();
            paths.add(el); 
            System.err.printf("***>> adding:%s\n",el.getRelativePath());
        }
        
        if (paths.size()==0)
            return null; 
        
        return paths;
    }

    public FileAttributes getFileAttributes(Path octoPath) throws XenonException
    {
        return engine.files().getAttributes(octoPath); 
    }

    public boolean deleteFile(Path octoPath, boolean force) throws XenonException
    {
        engine.files().delete(octoPath); 
        return true; // no exceptions 
    }
    
    public boolean exists(Path octoPath) throws XenonException
    {
        return engine.files().exists(octoPath); 
    }

    public void mkdir(Path octoPath) throws XenonException
    {
        engine.files().createDirectory(octoPath);
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
     
    public void createFile(Path octoPath) throws XenonException
    {
        engine.files().createFile(octoPath); //, getDefaultFilePermissions());
    }

    public InputStream createInputStream(Path octoPath) throws XenonException
    {
        return engine.files().newInputStream(octoPath);
    }

    // Open existing file and rewrite contents. If appen==true the OutputStream
    // will start at the end of the file. 
    public OutputStream createAppendingOutputStream(Path path, boolean append) throws XenonException
    {
        OpenOption opts[]=new OpenOption[1];

        if (append)
        {   
            opts[0]=OpenOption.APPEND;
        }
        else
        {
            opts[0]=OpenOption.OPEN_OR_CREATE;
        }
        
        return engine.files().newOutputStream(path, opts); 
    }

    // Create new OutputStream, optionally create new file if it doesn exists,
    // old file will be deleted. 
    public OutputStream createNewOutputStream(Path path, boolean ignoreExisting) throws XenonException
    {
        OpenOption opts[];
        
        if (ignoreExisting)
        {
            // replace existing file:  
            opts=new OpenOption[2];
            opts[0]=OpenOption.OPEN_OR_CREATE;
            opts[1]=OpenOption.TRUNCATE;
        }
        else
        {
            opts=new OpenOption[1];
            // create new, do not ignore already existing: 
            opts[0]=OpenOption.CREATE;
        }
        
        return engine.files().newOutputStream(path, opts);
        
    }
    
    public void rmdir(Path octoPath) throws XenonException
    {
        //DeleteOption options;
        engine.files().delete(octoPath) ;; // (octoPath,options);  
    }

    public void rename(Path oldPath, Path newPath) throws VrsException, XenonException
    {
        // Move must here be a rename on the same filesystem!

        if (checkSameFilesystem(oldPath,newPath)==false)
            throw new VrsException("Cannot rename file when new file is on other file system:"+oldPath+"=>"+newPath); 
        
        engine.files().move(oldPath, newPath);
    }

    public boolean checkSameFilesystem(Path path1, Path path2)
    {
        FileSystem fs1 = path1.getFileSystem(); 
        FileSystem fs2 = path2.getFileSystem(); 
        
        if (fs1!=fs2)
        {
            return false; 
        }
        
        logger.errorPrintf("FIXME:Cannot compare fileSystems yet!"); 
        
//        URI uri1 = path1.getFileSystem().getUri(); 
//        URI uri2 = path2.getFileSystem().getUri(); 
//        
//        if (StringUtil.compare(uri1.getHost(),uri2.getHost())!=0) 
//            return false;
        
//        if (StringUtil.compare(uri1.getHost(),uri2.getHost())!=0) 
//            return false;
        
        return true; 
    }
    
    public Credential createSSHCredentials(ServerInfo info) throws XenonException, VRLSyntaxException
    {
        String sshUser=info.getUsername(); 
        String ssh_id_key_file=info.getAttributeValue(ServerInfo.ATTR_SSH_IDENTITY);          
        boolean useIdFile=false; 
        char passwordChars[]=null; 
        
        if (StringUtil.isEmpty(ssh_id_key_file)==false)
        {
            // ssh_id_key_file can be absolute here: 
            VRL idFile=getUserHome().resolvePath(".ssh").resolvePath(ssh_id_key_file);  
         
            useIdFile=FSUtil.getDefault().existsFile(idFile.getPath(), true); 
            
            if (useIdFile)
            {
                ssh_id_key_file=idFile.getPath();
            }
            else
            {
                ssh_id_key_file=null; // do not use!
            }
        }
        Secret pwd=null;
        if (useIdFile==false)
        {
            // fall back to password 
            pwd=info.getPassword();
            if ((pwd==null) || (pwd.isEmpty()))
            {
                String serverStr=info.getUserinfo()+"@"+info.getServerVRL().getHostname(); 
                SecretHolder secretH=new SecretHolder(); 
                this.vrsContext.getUI().askAuthentication("Provide password for:"+serverStr,secretH); 
                pwd=secretH.value;
            }
        }
        
        if ((pwd!=null) && (!pwd.isEmpty()))
        {
            passwordChars=pwd.getChars(); 
        }
        
//        logger.debugPrintf("createSSHCredentials(): Using Username:"+sshUser);
//        logger.debugPrintf("createSSHCredentials(): Using ID Key file:%s\n",ssh_id_key_file);
//        logger.debugPrintf("createSSHCredentials(): Using password = %s\n",(passwordChars!=null)?"Yes":"No"); 
        
        
        Credentials creds = engine.credentials();
        Credential cred;
        
        
        if (useIdFile)
        {
            cred= creds.newCertificateCredential("ssh", 
                    ssh_id_key_file, 
                    sshUser, 
                    passwordChars,null);
        }
        else
        {
            cred= creds.newPasswordCredential("ssh", 
                    sshUser, 
                    passwordChars,
                    null);
        }
        
        return cred; 
    }

   

}