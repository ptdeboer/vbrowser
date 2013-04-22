package test;

import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.vfs.srm.SRMFSFactory;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vfs.VFSClient;
import nl.nlesc.vlet.vrs.vfs.VFile;
import nl.nlesc.vlet.vrs.vrl.VRL;

public class TestSrmCreateBigDir
{

	public static void main(String args[])
	{
	    
		try
		{
			VletConfig.init();
			VRS.getRegistry().registerVRSDriverClass(SRMFSFactory.class);
			
		    VRSContext context=new VRSContext(); 
		    VFSClient vfs=new VFSClient(context);
		    
		    VRL dirVrl=new VRL("srm://srm.grid.sara.nl/pnfs/grid.sara.nl/data/pvier/ptdeboer/bigdir/"); 
		    
		    if (vfs.existsDir(dirVrl)==false)
		    {
                System.out.println("Creating dir:"+dirVrl); 
		        vfs.mkdirs(dirVrl); 
		    }
		    
		    int numFiles=1000; 
		    String filePref="test_file";
		    
		      
            boolean createFiles=true; 
		    
            int start=300; 
            
		    if (createFiles) 
		        for (int i=start;i<numFiles;i++)
		        {  
    		        VRL fileVrl=dirVrl.appendPath(filePref+i); 
    		        
    		        if (vfs.existsFile(fileVrl))
    		        {
                        System.out.println("keeping file:"+fileVrl);
    		        }
    		        else
    		        {
                        System.out.println("creating file:"+fileVrl);
                        VFile result = vfs.createFile(fileVrl,true);
    		        }
	            }
		    
		    
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		System.out.println("=== END ===");
		
	}

	
}
