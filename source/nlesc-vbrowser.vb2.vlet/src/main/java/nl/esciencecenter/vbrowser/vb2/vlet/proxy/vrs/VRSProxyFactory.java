/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs;

import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.ptk.vbrowser.ui.browser.BrowserPlatform;
import nl.esciencecenter.ptk.vbrowser.ui.model.ViewNode;
import nl.esciencecenter.ptk.vbrowser.ui.proxy.ProxyException;
import nl.esciencecenter.ptk.vbrowser.ui.proxy.ProxyFactory;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSClient;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.VRSFactory;

/**
 * VRS Proxy Factory for VRSProxyNodes.
 */
public class VRSProxyFactory extends ProxyFactory
{
    private static ClassLogger logger;

    static
    {
        logger = ClassLogger.getLogger(VRSProxyFactory.class);
    }

    private static VRSProxyFactory instance;

    private static VRSContext staticContext;

    public static synchronized VRSContext getProxyVRSContext()
    {
        if (staticContext == null)
        {
            staticContext = new VRSContext();
        }

        return staticContext;
    }

    // ========================================================================
    //
    // ========================================================================

    private VRSContext vrsContext;

    private VRSClient vrsClient;

    protected VRSViewNodeDnDHandler viewNodeDnDHandler = null;

    public VRSProxyFactory(BrowserPlatform platform)
    {
        super(platform);

        this.vrsContext = getProxyVRSContext();
        this.vrsClient = new VRSClient(vrsContext);
    }

    public VRSProxyNode _openLocation(VRL vrl) throws ProxyException
    {
        try
        {
            return (VRSProxyNode) openLocation(vrl);
        }
        catch (Exception e)
        {
            throw new ProxyException("Failed to open location:" + vrl + "\n" + e.getMessage(), e);
        }
    }

    // actual open location:

    public VRSProxyNode doOpenLocation(VRL locator) throws ProxyException
    {
        logger.infoPrintf(">>> doOpenLocation():%s <<<\n", locator);

        try
        {
            VNode vnode = vrsClient.openLocation(createVRL(locator));
            return new VRSProxyNode(this, vnode, locator);
        }
        catch (Exception e)
        {
            throw new ProxyException("Failed to open location:" + locator + "\n" + e.getMessage(), e);
        }
    }

    private nl.esciencecenter.vbrowser.vrs.vrl.VRL createVRL(VRL locator)
    {
        return new nl.esciencecenter.vbrowser.vrs.vrl.VRL(locator.getScheme(),
                locator.getUserinfo(),
                locator.getHostname(),
                locator.getPort(),
                locator.getPath(),
                locator.getQuery(),
                locator.getFragment());
    }

    @Override
    public boolean canOpen(VRL locator, StringHolder reason)
    {
        // internal scheme!
        if (StringUtil.equals("myvle", locator.getScheme()))
        {
            reason.value = "Internal 'MyVle' object";
            return true;
        }
        VRSFactory vrs = this.vrsContext.getRegistry().getVRSFactoryForScheme(locator.getScheme());

        if (vrs != null)
            return true;

        reason.value = "Unknown scheme:" + locator.getScheme();
        return false;
    }

    public VRSViewNodeDnDHandler getVRSProxyDnDHandler(ViewNode viewNode)
    {
        if (viewNodeDnDHandler == null)
        {
            viewNodeDnDHandler = new VRSViewNodeDnDHandler(this);
        }
        return viewNodeDnDHandler;
    }

}
