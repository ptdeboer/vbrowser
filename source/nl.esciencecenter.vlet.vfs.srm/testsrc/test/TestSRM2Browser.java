package test;

import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vfs.srm.SRMFSFactory;
import nl.esciencecenter.vlet.vrs.VRS;

public class TestSRM2Browser
{

	public static void main(String args[])
	{
	    
		try
		{
		    VletConfig.init();
			VRS.getRegistry().registerVRSDriverClass(SRMFSFactory.class);
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		// nl.nlesc.vlet.gui.startVBrowser.main(args);
	}


}
