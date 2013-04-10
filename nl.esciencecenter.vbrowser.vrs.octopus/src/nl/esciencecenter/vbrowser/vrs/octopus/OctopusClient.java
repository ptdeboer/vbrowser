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
import nl.esciencecenter.octopus.engine.OctopusEngine;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.files.DeleteOption;
import nl.esciencecenter.octopus.files.DirectoryStream;
import nl.esciencecenter.octopus.files.FileAttributes;
import nl.esciencecenter.octopus.files.Files;
import nl.esciencecenter.octopus.files.Path;
import nl.esciencecenter.octopus.files.PathAttributes;
import nl.esciencecenter.octopus.files.PosixFilePermission;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;

public class OctopusClient
{

    public static OctopusClient createFor(VRSContext context, ServerInfo info, VRL location) throws VlException
    {
        try
        {
            OctopusClient client = new OctopusClient(); 
            client.updateProperties(context,info); 
            return client;
        }
        catch (Exception e)
        {
            throw new VlException(e.getMessage(),e); 
        }
    }

    // === instance == 
    
    private Octopus engine;
    private Properties octoProperties;
    //private Credentials octoCredentials;

    /**
     * Protected constructor: Use factory method.
     */
    protected OctopusClient() throws OctopusException
    {
        octoProperties=new Properties(); 
        //octoCredentials=new Credentials(); 
        engine=OctopusEngine.newEngine(octoProperties); 
    }
    
    protected void updateProperties(VRSContext context, ServerInfo info)
    {
        
    }   
    

    public Path newPath(URI uri) throws OctopusException
    {
        Path path = engine.files().newPath(octoProperties, uri); 
        return path; 
    }

    public FileAttributes statPath(Path path) throws OctopusException
    {
        return engine.files().getAttributes(path); 
    }

    /** Full Stat */ 
    public List<PathAttributes> statDir(Path octoPath) throws OctopusException
    {
        DirectoryStream<PathAttributes> dirIterator = engine.files().newAttributesDirectoryStream(octoPath); 

        Iterator<PathAttributes> iterator = dirIterator.iterator(); 
        
        List<PathAttributes> paths=new ArrayList<PathAttributes>(); 
        
        while(iterator.hasNext())
        {
            PathAttributes el = iterator.next();
            paths.add(el); 
        }
        
        if (paths.size()==0)
            return null; 
        
        return paths;
    }

    /** list files only without attributes */ 
    public List<Path> listDir(Path octoPath) throws OctopusException
    {
        DirectoryStream<Path> dirIterator = engine.files().newDirectoryStream(octoPath); 

        Iterator<Path> iterator = dirIterator.iterator(); 
        
        List<Path> paths=new ArrayList<Path>(); 
        
        while(iterator.hasNext())
        {
            Path el = iterator.next();
            paths.add(el); 
        }
        
        if (paths.size()==0)
            return null; 
        
        return paths;
    }

    public FileAttributes getFileAttributes(Path octoPath) throws OctopusException
    {
        return engine.files().getAttributes(octoPath); 
    }

    public boolean deleteFile(Path octoPath, boolean force) throws OctopusException
    {
        engine.files().delete(octoPath); 
        return true; // no exceptions 
    }
    
    public boolean exists(Path octoPath) throws OctopusException
    {
        return engine.files().exists(octoPath); 
    }

    public Path mkdir(Path octoPath, boolean force) throws OctopusException
    {
        return engine.files().createDirectories(octoPath,getDefaultDirPermissions());
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

    public Set<PosixFilePermission> getDefaultFilePermissions()
    {
        return createPermissions(0644); // octal
    }
    
    public Set<PosixFilePermission> getDefaultDirPermissions()
    {
        return createPermissions(0755); // octal 
    }
     
    public Path createFile(Path octoPath) throws OctopusException
    {
        return engine.files().createFile(octoPath, getDefaultFilePermissions());
    }

    public InputStream createInputStream(Path octoPath) throws IOException
    {
        try
        {
            return engine.files().newInputStream(octoPath);
        }
        catch (OctopusException e)
        {
            throw new IOException(e.getMessage(),e); 
        } 
    }

    public OutputStream createOutputStream(Path path) throws IOException
    {
        try
        {
            return engine.files().newOutputStream(path);
        }
        catch (OctopusException e)
        {
            throw new IOException(e.getMessage(),e); 
        }
    }

    public void rmdir(Path octoPath) throws OctopusException
    {
        //DeleteOption options;
        engine.files().delete(octoPath) ;; // (octoPath,options);  
        
    }
    
}
