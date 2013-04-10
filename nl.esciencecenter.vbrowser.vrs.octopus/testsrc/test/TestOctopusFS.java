package test;

import nl.esciencecenter.vbrowser.vrs.octopus.OctopusFSFactory;
import nl.uva.vlet.VletConfig;
import nl.uva.vlet.vfs.VDir;
import nl.uva.vlet.vfs.VFSClient;
import nl.uva.vlet.vfs.VFSNode;
import nl.uva.vlet.vrs.VRS;

public class TestOctopusFS
{
    private static VFSClient vfs=null; 
    
    public static VFSClient initOctopusVFS() throws Exception
    {
        if (vfs!=null)
            return vfs; 
        
        VletConfig.init();
        VRS.getRegistry().unregisterVRSDriverClass(nl.uva.vlet.vdriver.vfs.localfs.LocalFSFactory.class); 
        VRS.getRegistry().registerVRSDriverClass(OctopusFSFactory.class);
        
        VFSClient vfs=VFSClient.getDefault(); 
        return vfs; 
    }
    
    public static void main(String args[]) throws Exception
    {
        VFSClient vfs=initOctopusVFS(); 
        
        VDir dir = vfs.getDir("file:///home/"+VletConfig.getUserName()); 
        
        VFSNode[] nodes = dir.list(); 
        
        System.out.printf(">>> Dir:"+dir); 
        for (int i=0;i<nodes.length;i++)
        {
            System.out.printf(" - node[#%d] =%s\n",i,nodes[i]);
        }
        
    }
    
}
