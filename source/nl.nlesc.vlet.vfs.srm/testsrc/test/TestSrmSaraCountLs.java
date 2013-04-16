package test;

import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.vfs.srm.SRMFSFactory;
import nl.nlesc.vlet.vfs.srm.SRMFileSystem;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vfs.VFSClient;
import nl.nlesc.vlet.vrs.vfs.VFSNode;

public class TestSrmSaraCountLs
{

	public static void main(String args[])
	{
	    
		try
		{
			//GlobalConfig.init();
			VRS.getRegistry().registerVRSDriverClass(SRMFSFactory.class);
			
		    VRSContext context=new VRSContext(); 
		    VFSClient vfs=new VFSClient(context);
		    
		    // Created with TestSrmLs: 
		    VRL dirVrl=new VRL("srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/pvier/ptdeboer/bigdir/"); 
		    //VRL dirVrl=new VRL("srm://srm.ciemat.es:8443/pnfs/ciemat.es/data/dteam/generated");   
		    
		    SRMFileSystem srmFs = (SRMFileSystem)vfs.openFileSystem(dirVrl);
		    
		    testCountLS(srmFs,dirVrl,100,200); 
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		System.out.println(" === END === \n");
	}

    private static void testCountLS(SRMFileSystem srmFs, VRL dirVrl, int offset, int count) throws VlException
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
