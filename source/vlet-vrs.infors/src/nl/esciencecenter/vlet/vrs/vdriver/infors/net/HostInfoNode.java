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


import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.vdriver.infors.CompositeServiceInfoNode;
import nl.esciencecenter.vlet.vrs.vdriver.infors.InfoConstants;


public class HostInfoNode extends CompositeServiceInfoNode<VNode>
{
    private static final String ATTR_IP_ADDRESS = "ipAddress";
    private static final String ATTR_DNS_HOSTNAME = "dnsHostname";
    
    // === //
    
    private static NetUtil netUtil=NetUtil.getDefault(); 
    
    // === //
    
    private boolean rescan=true;
    
    private NetworkNode parent=null; 
    
    public HostInfoNode(NetworkNode parent, VRL logicalLocation)
    {
        super(parent.getVRSContext(), logicalLocation);
        this.parent=parent; 
        this.setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
        this.setEditable(false); 
    }

    public String getName()
    {
        String hostname=this.attributes.getStringValue(ATTR_DNS_HOSTNAME); 
        // String ipaddr=this.attributes.getValue(ATTR_IP_ADDRESS);
        if (hostname==null)
            return getBasename();
        return hostname; 
    }
    
    public void setDNSHostname(String hostname)
    {
        this.attributes.set(ATTR_DNS_HOSTNAME,hostname);
    }
    
    public String getDNSHostname() 
    {
        return this.attributes.getStringValue(ATTR_DNS_HOSTNAME); 
    }

    public void setIPAdress(byte[] ip)
    {
        this.attributes.set(ATTR_IP_ADDRESS,NetUtil.ip2string(ip)); 
    }
    
    public void setIPAdress(String ipAdress)
    {
        this.attributes.set(ATTR_IP_ADDRESS,ipAdress);  
    }
    
    public String getIPAdress() 
    {
        return this.attributes.getStringValue(ATTR_IP_ADDRESS);  
    }

    @Override
    public String[] getResourceTypes()
    {
        return null;
    }

    @Override
    public String getResourceType()
    {
        return InfoConstants.HOST_INFO_NODE; 
    }
    
    public VNode[] getNodes() throws VrsException
    {
        if (rescan==true)
        {
            scanHost(); 
        }
        
        return super._getNodes(); 
    }

    Object scanMutex=new Object(); 
    
    private void scanHost()
    {
        synchronized(scanMutex)
        {
            if (rescan==false)
                return;// already rescanned by previous thread.
            
            rescan=false;// pre-emptive assertion
            
            Scanner scanner = netUtil.getScanner();
            String host=this.getDNSHostname(); 
            scanner.scanHost(host);
            
            PortInfo[] infos = scanner.getPortInfos(host);
            
            for (PortInfo info:infos)
            {
                try
                {
                    if (info.isValidConnection())
                    {
                        VRL subVrl=this.getVRL().resolvePath(""+info.port);
                        String scheme=info.getProtocol();   
                        
                        VRL targetVrl=new VRL(scheme,null,host,info.port,"/");
                        
                        ServiceInfoNode node=ServiceInfoNode.createNode(this,subVrl,targetVrl,info); 
                        
                        this.addSubNode(node); 
                    }
                }
                catch (Exception e)
                {
                    error("Failed to add port:"+info.port+":"+e); 
                }
            }
        }
    }

    private void error(String msg)
    {
       VletConfig.getRootLogger().errorPrintf(this+":%s\n",msg);
    }
}
