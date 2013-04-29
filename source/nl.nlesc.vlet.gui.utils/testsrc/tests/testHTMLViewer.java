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

package tests;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.gui.viewers.VHTMLViewer;
import nl.nlesc.vlet.vrs.vrl.VRL;

public class testHTMLViewer
{

    public static void main(String[] args)
    {
        VletConfig.getRootLogger().setLevelToDebug(); 
        
        try
        {
            VHTMLViewer.viewStandAlone(new VRL("http://www.piter.nl/index.html")); 
            VHTMLViewer.viewStandAlone(new VRL("http://www.vl-e.nl")); 
        }
        catch (VrsException e)
        {
                System.out.println("***Error: Exception:" + e);
                e.printStackTrace();
        }
        
    }

}
