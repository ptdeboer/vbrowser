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

package test;


import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.gui.panels.acldialog.ACLPanel;
import nl.esciencecenter.vlet.gui.proxynode.impl.direct.ProxyVNodeFactory;

public class TestACLPanel
{

    public static void main(String args[])
    {
        VletConfig.getRootLogger().setLevelToDebug(); 
        testFor("file:/home/"+GlobalProperties.getGlobalUserName());
        //testFor("srb://piter.de.boer.vlenl@srb.grid.sara.nl/VLENL/home/piter.de.boer.vlenl");
        // testFor("sftp://ptdeboer@elab.science.uva.nl/home/ptdeboer"); 
     }
    
    public static void testFor(String loc)
    {
        try
        {
            ProxyVNodeFactory.initPlatform(); 
            ACLPanel.showEditor(null,new VRL(loc));
        }
        catch (VrsException e)
        {
            System.out.println("***Error: Exception:"+e); 
            e.printStackTrace();
        } 
        
    }
}
