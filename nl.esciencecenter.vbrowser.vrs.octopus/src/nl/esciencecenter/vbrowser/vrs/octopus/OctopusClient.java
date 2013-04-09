package nl.esciencecenter.vbrowser.vrs.octopus;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import nl.esciencecenter.octopus.Octopus;
import nl.esciencecenter.octopus.engine.OctopusEngine;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.files.DirectoryStream;
import nl.esciencecenter.octopus.files.FileAttributes;
import nl.esciencecenter.octopus.files.Files;
import nl.esciencecenter.octopus.files.Path;
import nl.esciencecenter.octopus.files.PathAttributes;
import nl.esciencecenter.octopus.security.Credentials;
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
    private Credentials octoCredentials;

    protected OctopusClient() throws OctopusException
    {
        octoProperties=new Properties(); 
        octoCredentials=new Credentials(); 
        engine=OctopusEngine.newEngine(octoProperties, octoCredentials); 
    }
    
    protected void updateProperties(VRSContext context, ServerInfo info)
    {
    }   
    
    public void exitPath()
    {
        Files files = engine.files(); 
    }

    public Path newPath(URI uri) throws OctopusException
    {
        Path path = engine.files().newPath(octoProperties,octoCredentials, uri); 
        return path; 
    }

    public FileAttributes statPath(Path path) throws OctopusException
    {
        return engine.files().readAttributes(path); 
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

    /** Full Stat */ 
    public List<Path> list(Path octoPath) throws OctopusException
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
}
