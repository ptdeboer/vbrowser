package nl.esciencecenter.vlet.vfs.ssh.jcraft;

import java.io.IOException;
import java.net.URI;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.vfs.ssh.jcraft.SSHChannel.SSHChannelOptions;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.piter.vterm.api.ChannelOptions;
import nl.piter.vterm.api.ShellChannel;
import nl.piter.vterm.api.ShellChannelFactory;

public class SSHShellChannelFactory implements ShellChannelFactory
{
    protected VRSContext vrsContext; 
    
    public SSHShellChannelFactory(VRSContext context)
    {
        this.vrsContext=context; 
    }

    @Override
    public ShellChannel createChannel(URI uri, String user, char[] password, ChannelOptions channelOptions) throws IOException {
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
