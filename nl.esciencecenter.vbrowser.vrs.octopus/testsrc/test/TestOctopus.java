package test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Properties;

import nl.esciencecenter.octopus.Octopus;
import nl.esciencecenter.octopus.engine.OctopusEngine;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.files.DirectoryStream;
import nl.esciencecenter.octopus.files.Path;
import nl.esciencecenter.octopus.security.Credentials;

public class TestOctopus
{

    public static void main(String args[])
    {
        try
        {
            Octopus oct=createEngine();
            testListDir(oct,"file:/home/ptdeboer/test/");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        
    }
    
    private static void testListDir(Octopus oct, String string) throws OctopusException, URISyntaxException
    {
        Path path = oct.files().newPath(new URI("file:/home/ptdeboer/test/"));
        DirectoryStream<Path> dirStream = oct.files().newDirectoryStream(path); 
        
        Iterator<Path> iterator = dirStream.iterator(); 
        
        while (iterator.hasNext())
        {
            Path el = iterator.next();
            System.out.printf(">Path:%s\n",el); 
            System.out.printf(">Path.getPath():%s\n",el.getPath()); 

        }
        
    }

    public static Octopus createEngine() throws OctopusException
    {
        Properties octoProperties = new Properties(); 
        //Credentials octoCredentials = new Credentials(); 
        Octopus engine = OctopusEngine.newEngine(octoProperties); 
        
        return engine; 
        
    }
    
}
