package nl.esciencecenter.ptk.util.vlterm;

import java.io.IOException;
import java.net.URI;

import nl.esciencecenter.ptk.crypt.Secret;
import nl.esciencecenter.ptk.util.vlterm.VTermChannelProvider.ChannelOptions;

public interface ShellChannelFactory
{
    
    public ShellChannel createChannel(URI uri,String username,Secret password,ChannelOptions options) throws IOException;
    
}
