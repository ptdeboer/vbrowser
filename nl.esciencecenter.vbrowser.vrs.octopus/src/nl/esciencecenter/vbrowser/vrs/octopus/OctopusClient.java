package nl.esciencecenter.vbrowser.vrs.octopus;

import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;

public class OctopusClient
{

    public static OctopusClient createFor(VRSContext context, ServerInfo info, VRL location)
    {
        return new OctopusClient(); 
    }

    protected OctopusClient()
    {
        
    }
}
