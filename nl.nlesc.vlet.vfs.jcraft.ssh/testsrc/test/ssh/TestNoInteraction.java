package test.ssh;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;

import nl.esciencecenter.ptk.Global;
import nl.esciencecenter.ptk.crypt.Secret;
import nl.uva.vlet.VletConfig;
import nl.uva.vlet.exception.VRLSyntaxException;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vfs.VFS;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.ServerInfo;
import nl.uva.vlet.vrs.VRSContext;

public class TestNoInteraction 
{
	public static void main(String args[])
	{
		try {
			testNoUI();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
        VFS.exit(); 
	}
	
	public static void testNoUI() throws VlException
	{
	
		   try {
	            VletConfig.setBaseLocation(new URL("http://dummy/url"));
	        } catch (MalformedURLException ex)
	        {
	        	System.err.print("Exception"+ex); 
	        	
	        }
	        // runtime configuration
	        VletConfig.setHasUI(false);
	        VletConfig.setIsApplet(true);
	        VletConfig.setPassiveMode(true);
	        VletConfig.setIsService(true);
	        VletConfig.setInitURLStreamFactory(false);        
	        VletConfig.setAllowUserInteraction(false);
	        
	        // user configuration 
	        VletConfig.setUsePersistantUserConfiguration(false);
	        //  GlobalConfig.setUserHomeLocation(new URL("file:///tmp/myservice"));

	        Global.init();
	        
	        VRL vrl=new VRL("sftp://user@elab.lab.uvalight.net/tmp"); 
	        
	        VFSClient vfs=new VFSClient(); 
	        VRSContext context=vfs.getVRSContext(); 
	        
	        ServerInfo info = context.getServerInfoFor(vrl, true);
	        
	        info.setAttribute(ServerInfo.ATTR_DEFAULT_YES_NO_ANSWER,true);
	        info.setPassword(new Secret("***".toCharArray())); 
	        info.store(); 
	        
	        VFSNode node=vfs.openLocation(vrl);
	        System.out.println("node="+node);
	        
	}
}
