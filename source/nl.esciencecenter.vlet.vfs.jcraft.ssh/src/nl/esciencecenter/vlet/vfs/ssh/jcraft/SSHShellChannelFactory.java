package nl.esciencecenter.vlet.vfs.ssh.jcraft;

import java.io.IOException;

import nl.esciencecenter.ptk.crypt.Secret;
import nl.esciencecenter.ptk.exec.ChannelOptions;
import nl.esciencecenter.ptk.exec.ShellChannelFactory;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.vfs.ssh.jcraft.SSHChannel.SSHChannelOptions;
import nl.esciencecenter.vlet.vrs.VRSContext;

public class SSHShellChannelFactory implements ShellChannelFactory
{
    protected VRSContext vrsContext; 
    
    public SSHShellChannelFactory(VRSContext context)
    {
        this.vrsContext=context; 
    }

    public SSHChannel createChannel(java.net.URI uri,String user,Secret password,ChannelOptions options) throws IOException
    {
        SSHChannelOptions sshOptions=null;
        String host=uri.getHost(); 
        int port=uri.getPort(); 
        
        try
        {
            SSHChannel sshChannel=new SSHChannel(vrsContext,user,host,port,uri.getPath(),sshOptions);
            return sshChannel; 
        }
        catch (VrsException e)
        {
            throw new IOException(e.getMessage(),e); 
        } 
        
    }
}
