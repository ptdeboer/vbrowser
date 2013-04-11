package voms;

import org.globus.gsi.GlobusCredential;

import nl.nlesc.vlet.grid.globus.GlobusUtil;
import nl.nlesc.vlet.grid.voms.VomsUtil;
import nl.nlesc.vlet.util.grid.GridProxy;

public class ParseProxy 
{
	public static void main(String args[])
	{
		GlobusUtil.init(); 
		
		GridProxy proxy=GridProxy.getDefault(); 
		
		GlobusCredential cred = GlobusUtil.getGlobusCredential(proxy); 
		
		try 
		{
			String log=VomsUtil.parse(cred.getCertificateChain());
			System.out.printf("--- VomsUtils Parse Log ---\n%s-----\n",log);
			
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
			
		  
		
	}
}
