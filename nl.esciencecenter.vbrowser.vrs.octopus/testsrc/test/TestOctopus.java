package test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import nl.esciencecenter.octopus.Octopus;
import nl.esciencecenter.octopus.engine.OctopusEngine;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.files.Path;
import nl.esciencecenter.octopus.security.Credentials;

public class TestOctopus
{

    public static void main(String args[])
    {
        try
        {
            Octopus oct=createEngine();
            Path path = oct.files().newPath(new URI("file:/tmp/"));
            
            
        }
        catch (Exception e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        
    }
    
    public static Octopus createEngine() throws OctopusException
    {
        Properties octoProperties = new Properties(); 
        Credentials octoCredentials = new Credentials(); 
        Octopus engine = OctopusEngine.newEngine(octoProperties, octoCredentials); 
        
        return engine; 
        
    }
    
}
