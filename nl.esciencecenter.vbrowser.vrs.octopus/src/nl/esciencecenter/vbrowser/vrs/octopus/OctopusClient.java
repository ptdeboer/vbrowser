package nl.esciencecenter.vbrowser.vrs.octopus;

import java.util.Properties;

import nl.esciencecenter.octopus.Octopus;
import nl.esciencecenter.octopus.engine.OctopusEngine;
import nl.esciencecenter.octopus.exceptions.OctopusException;
import nl.esciencecenter.octopus.files.Files;
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

    protected OctopusClient() throws OctopusException
    {
        Properties properties=new Properties(); 
        Credentials credentials=new Credentials(); 
        engine=OctopusEngine.newEngine(properties, credentials); 
    }
    
    protected void updateProperties(VRSContext context, ServerInfo info)
    {
    }   
    
    public void exitPath()
    {
        Files files = engine.files(); 
    }

    
}
