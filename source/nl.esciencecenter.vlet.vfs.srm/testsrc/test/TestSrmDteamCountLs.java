package test;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vfs.srm.SRMFSFactory;
import nl.esciencecenter.vlet.vfs.srm.SRMFileSystem;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;

public class TestSrmDteamCountLs
{

	public static void main(String args[])
	{
	    
		try
		{
			VletConfig.init();
			VRS.getRegistry().registerVRSDriverClass(SRMFSFactory.class);
			
		    VRSContext context=new VRSContext(); 
		    VFSClient vfs=new VFSClient(context);
		    
		    VRL dirVrl=new VRL("srm://srm.ciemat.es:8443/pnfs/ciemat.es/data/dteam/generated"); 
		    
		    SRMFileSystem srmFs = (SRMFileSystem)vfs.openFileSystem(dirVrl);
		    
		    testCountLS(srmFs,dirVrl,1,900); 
		    
		    
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}


		System.out.println(" === END === \n");
	}

    private static void testCountLS(SRMFileSystem srmFs, VRL dirVrl, int offset, int count) throws VrsException
    {
        
        VFSNode[] nodes = srmFs.list(dirVrl.getPath(),offset,count); 
        
        if (nodes==null)
        {
            System.out.println("*** NULL NODES ***\n");
        }
        else
        {
            System.out.printf("*** num nodes= #%d ***\n",nodes.length);
            
            int index=0; 
            
            for (VFSNode node:nodes)
            {   
                System.out.println(" -[#"+(index++)+"] node:"+node); 
            }
        } 
    }


}
