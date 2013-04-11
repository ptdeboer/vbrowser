package test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.Properties;

import org.junit.Assert;

import nl.esciencecenter.octopus.Octopus;
import nl.esciencecenter.octopus.engine.OctopusEngine;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.files.AbsolutePath;
import nl.esciencecenter.octopus.files.DirectoryStream;
import nl.esciencecenter.octopus.files.AbsolutePath;
import nl.esciencecenter.octopus.files.FileSystem;
import nl.esciencecenter.octopus.files.RelativePath;

public class TestOctopus
{

    public static void main(String args[])
    {
        try
        {
            Octopus oct=createEngine();
            testListDir(oct,new URI("file:/"),"/home/ptdeboer/");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        } 
        
    }
    
    private static void testListDir(Octopus oct, URI fsUri,String pathStr) throws Exception
    {
        
        FileSystem fs = oct.files().newFileSystem(fsUri, null, null); 
        Assert.assertNotNull("FileSystem is null",fs); 
        
        RelativePath relPath=new RelativePath(pathStr);
        AbsolutePath path = oct.files().newPath(fs, relPath); 
        DirectoryStream<AbsolutePath> dirStream = oct.files().newDirectoryStream(path); 
        
        Iterator<AbsolutePath> iterator = dirStream.iterator(); 
        
        while (iterator.hasNext())
        {
            AbsolutePath el = iterator.next();
            System.out.printf("> Path:%s\n",el.getPath()); 
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
