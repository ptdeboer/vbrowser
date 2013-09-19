package nl.esciencecenter.vbrowser.vb2.ui.proxy;

import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

public class VirtualRootProxyFactory extends ProxyFactory
{
    public static final String VROOT_VRL="virtual:0"; 
    
    private static VirtualRootNode rootNode;

    
    public VirtualRootProxyFactory()
    {

    }

    // ========
    // instance
    // ========

    @Override
    public VirtualRootNode doOpenLocation(VRL locator) throws ProxyException
    {
        try
        {
            return this.getRoot();
        }
        catch (VRLSyntaxException e)
        {
            throw new ProxyException("VRL Syntax Exception:" + e, e);
        }
    }

    @Override
    public boolean canOpen(VRL locator, StringHolder reason)
    {
        if (locator.toString().equals(VROOT_VRL))
            return true;
        
        if (reason != null)
            reason.value = "Can only open one root node per factory";

        return false;
    }

    public VirtualRootNode getRoot() throws VRLSyntaxException
    {
        if (rootNode == null)
        {
            rootNode = new VirtualRootNode(this, new VRL(VROOT_VRL));
        }

        return rootNode;
    }

}