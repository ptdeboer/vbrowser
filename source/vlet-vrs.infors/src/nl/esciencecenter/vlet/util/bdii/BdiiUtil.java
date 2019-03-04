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

package nl.esciencecenter.vlet.util.bdii;

import java.util.HashMap;
import java.util.Map;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vlet.vrs.VRSContext;

public class BdiiUtil
{
    private static Map<String,BdiiService> services=new HashMap<String, BdiiService>(); 
    
    /**
     * Creates or return already created BDII Service for this context. 
     * @retun Always create a BdiiService Object. 
     * @throws VrsException
     */
    public static BdiiService getBdiiService(VRSContext vrsContext) throws VrsException
    {
        // auto update !
        java.net.URI uri = vrsContext.getConfigManager().getBdiiServiceURI();
        
        synchronized (services)
        {
            BdiiService bdiiService = services.get(uri.toString()); 
            
            if (bdiiService == null)
            {
                bdiiService = BdiiService.createService(vrsContext);
                services.put(uri.toString(),bdiiService); 
            }

            return bdiiService;
        }
    }
}
