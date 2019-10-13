package nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs;

import java.util.List;

import nl.esciencecenter.ptk.data.ExtendedList;
import nl.esciencecenter.ptk.vbrowser.ui.model.ViewNode;
import nl.esciencecenter.ptk.vbrowser.ui.model.ProxyNodeDnDHandler;
import nl.esciencecenter.ptk.vbrowser.ui.proxy.ProxyException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

public class VRSViewNodeDnDHandler extends ProxyNodeDnDHandler
{
    VRSProxyFactory proxyFactory;

    public VRSViewNodeDnDHandler(VRSProxyFactory factory)
    {
        proxyFactory = factory;
    }

    public boolean doDrop(ViewNode targetDropNode, DropAction dropAction, List<VRL> sources)
    {
        System.err.printf(">>> VRS DROP:%s:%s:", dropAction, new ExtendedList<VRL>(sources));

        try
        {
            VRSProxyNode targetPNode = proxyFactory.doOpenLocation(targetDropNode.getVRL());
            new InteractiveProxyTransfer().doCopyMoveDrop(targetPNode, sources, false);
        }
        catch (ProxyException e)
        {
            handle(e);
        }

        return true;
    }

    private void handle(ProxyException e)
    {
        e.printStackTrace();
    }

}
