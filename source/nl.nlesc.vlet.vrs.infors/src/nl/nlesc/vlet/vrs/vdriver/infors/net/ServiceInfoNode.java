/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.nlesc.vlet.vrs.vdriver.infors.net;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.vdriver.infors.grid.InfoNode;

public class ServiceInfoNode extends InfoNode
{

    private PortInfo info;

    public ServiceInfoNode(VRSContext context, VRL logicalLocation)
    {
        super(context, logicalLocation);
    }

    public static ServiceInfoNode createNode(HostInfoNode hostInfoNode, VRL vrl,
            VRL targetVrl, PortInfo info)
    {
        ServiceInfoNode node=new ServiceInfoNode(hostInfoNode.getVRSContext(),vrl);
        node.setInfo(info); 
 
        node.setResourceVRL(targetVrl); 
        node.setShowShortCutIcon(false); 
        node.setTargetIsComposite(true); 
        node.setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
        String scheme=info.getProtocol(); 
        if (StringUtil.isEmpty(scheme))
            scheme="?";

        node.setName("port:"+info.port+" ["+scheme+"]"); 
        return node; 
    }

    protected void setInfo(PortInfo newInfo)
    {
        this.info=newInfo; 
    }


}
