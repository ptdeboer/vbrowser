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

package nl.esciencecenter.vlet.gui.vbrowser;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.gui.UIPlatform;
import nl.esciencecenter.vlet.gui.aboutrs.AboutRSFactory;
import nl.esciencecenter.vlet.gui.proxynode.impl.direct.ProxyVNode;
import nl.esciencecenter.vlet.gui.proxynode.impl.direct.ProxyVNodeFactory;

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
