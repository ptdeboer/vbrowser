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

package nl.esciencecenter.vlet.vrs.vdriver.infors.net;

import static nl.esciencecenter.vlet.vrs.vdriver.infors.InfoConstants.ATTR_NETWORK_ADRESS;
import static nl.esciencecenter.vlet.vrs.vdriver.infors.InfoConstants.NETWORK_INFO;

import java.net.UnknownHostException;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.exception.ConnectionException;
import nl.esciencecenter.vlet.exception.NotImplementedException;
import nl.esciencecenter.vlet.vrs.LinkNode;
import nl.esciencecenter.vlet.vrs.VEditable;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vdriver.infors.CompositeServiceInfoNode;

public class NetworkNode extends CompositeServiceInfoNode<VNode> implements VEditable
{
    private static NetUtil netUtil = NetUtil.getDefault();

    private boolean rescan = true;

    public NetworkNode(VRSContext context, VRL logicalLocation)
    {
        super(context, logicalLocation);
        init();
    }

    private void init()
    {
        // Default to Resource Location;
        // this.setType(InfoConstants.NETWORK_INFO);
        // this.setName(getVRL().getBasename());
        try
        {
            this.setAddress("127.0.0.0/0");
        }
        catch (VrsException e)
        {
            VletConfig.getRootLogger().logException(ClassLogger.ERROR,e,"Exception:%s\n",e);
        }
        setEditable(true);

        // setShowShortCutIcon(false);
        // setTargetIsComposite(true);
        setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
    }

    public String getMimeType()
    {
        return null;
    }

    // public NetworkNode duplicate()
    // {
    // NetworkNode infoNode=new NetworkNode(vrsContext,getVRL());
    // infoNode.copyFrom(this);
    // return infoNode;
    // }
    //
    // public InfoNode clone()
    // {
    // return duplicate();
    // }
    //
    public boolean isEditable()
    {
        return true;
    }

    public LinkNode toLinkNode() throws NotImplementedException
    {
        throw new nl.esciencecenter.vlet.exception.NotImplementedException("Cannot cast to link node");
    }

    public String[] getAttributeNames()
    {
        StringList names = new StringList(super.getAttributeNames());
        names.add(ATTR_NETWORK_ADRESS);
        return names.toArray();
    }

    public Attribute getAttribute(String name) throws VrsException
    {
        Attribute attr = super.getAttribute(name);
        return attr;
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public String getResourceType()
    {
        return NETWORK_INFO;
    }

    public String getNetworkAddress()
    {
        String str = this.attributes.getStringValue(ATTR_NETWORK_ADRESS);
        if (str == null)
            return null;

        String strs[] = str.split("/");

        return strs[0];
    }

    /** Returns nr. of UNsignificant bits in network address range. */
    public int getNetworkBits()
    {
        String str = this.attributes.getStringValue(ATTR_NETWORK_ADRESS);
        if (str == null)
            return 0x00ff;

        String strs[] = str.split("/");
        if (strs.length > 1)
            return Integer.parseInt(strs[1]);

        // single address network!
        return 0;
    }

    public boolean setAttribute(Attribute attr) throws VrsException
    {
        if (attr.getName().equals(ATTR_NETWORK_ADRESS))
        {
            return setAddress(attr.getStringValue());
        }
        else
        {
            return super.setAttribute(attr);
        }
    }

    public boolean setAddress(String addrStr) throws VrsException
    {
        this.childNodes.clear();
        this.rescan = true;
        // default
        Attribute attr = new Attribute(ATTR_NETWORK_ADRESS, addrStr);

        String networkBitStr = "0";

        String strs[] = addrStr.split("/");
        if ((strs != null) || (strs.length > 0))
        {
            addrStr = strs[0];
            if (strs.length > 1)
                networkBitStr = strs[1];
        }

        // auto lookup/resolve logical hostname
        if (netUtil.isIPAddress(addrStr) == false)
        {
            try
            {
                StringList ips;
                ips = netUtil.lookupAddresses(addrStr);
                if ((ips != null) && (ips.size() > 0))
                {
                    addrStr = ips.get(0);
                    if (addrStr != null)
                        attr = new Attribute(ATTR_NETWORK_ADRESS, addrStr + "/" + networkBitStr);
                }
            }
            catch (UnknownHostException e)
            {
                throw new ConnectionException("Not a valid IP Address of resolvable host:" + addrStr,e);
            }
        }

        return super.setAttribute(attr, true); // store in super
    }

    public VNode[] getNodes() throws VrsException
    {
        if (rescan == true)
        {
            scanNodes();
        }
        return _getNodes();
    }

    private void scanNodes() throws VrsException
    {
        // use childNodes as mutex !
        synchronized (this.childNodes)
        {
            // already done by previous thread !
            if (rescan == false)
                return;

            this.childNodes.clear();

            String ipAddr = this.getNetworkAddress();
            int maskbits = this.getNetworkBits();

            // hard coded limit!
            if (maskbits > 16)
                maskbits = 16;

            try
            {
                byte ip[] = NetUtil.parseIpaddress(ipAddr);
                ip = NetUtil.mask(ip, maskbits);

                for (int i = 0; i < (1 << maskbits); i++)
                {
                    try
                    {
                        resolveAndAddNode(ip, true);
                    }
                    catch (Exception e)
                    {
                        // info("Skipping:"+NetUtil.ip2string(ip)+"\nError="+e);
                    }

                    NetUtil.add(ip, 1);
                }
            }
            catch (UnknownHostException e)
            {
                throw new ConnectionException("Network Address Error", e);
            }

            rescan = false;
        }
    }

    private void resolveAndAddNode(byte[] ip, boolean scan) throws VrsException
    {
        String ipstr = NetUtil.ip2string(ip);
        // info("Checking host:"+ipstr);
        String hostname = null;

        if (netUtil.existsAddress(ip) == false)
        {
            // info("No hostname for:"+ipstr);
            return;
        }

        try
        {
            hostname = netUtil.resolve(ip);
        }
        catch (UnknownHostException e)
        {
            // info("Couldn't resolve address:"+ipstr);
            return;
        }

        // use IP string a unique part in VRL:
        VRL vrl = this.getVRL().resolvePath(ipstr);

        HostInfoNode node = new HostInfoNode(this, vrl);
        // info("New node:"+node);

        node.setDNSHostname(hostname);
        node.setIPAdress(ip);
        this.addSubNode(node);

        // Asynchronous updates!
        this.fireChildAdded(vrl);
    }

}
