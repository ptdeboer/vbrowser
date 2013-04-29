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

package nl.nlesc.vlet.gui.vbrowser;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.nlesc.vlet.gui.UIGlobal;
import nl.nlesc.vlet.gui.UIPlatform;
import nl.nlesc.vlet.gui.aboutrs.AboutRSFactory;
import nl.nlesc.vlet.gui.proxynode.impl.direct.ProxyVNode;
import nl.nlesc.vlet.gui.proxynode.impl.direct.ProxyVNodeFactory;

public class VBrowserInit
{

    public static UIPlatform initPlatform() throws VrsException
    {
        // todo: better platform initialization. 
        // current order to initialize: 
        UIGlobal.init();

        // ========
        // Poxy VRS/ProxyNode (before UI Platform) 
        // ========
        
        ProxyVNodeFactory.initPlatform(); 
 
        AboutRSFactory.initPlatform(); 
        // prefetch MyVLe, during startup:
        ProxyVNode.getVirtualRoot();     
        
        // ==========
        // UIPlatform
        // ==========
        
        // Register VBrowser platform 
        UIPlatform plat=UIPlatform.getPlatform();
        plat.registerBrowserFactory(VBrowserFactory.getInstance());
        
        // Option --native ? :
        plat.startCustomLAF(); 
        
        return plat;
    }

}
