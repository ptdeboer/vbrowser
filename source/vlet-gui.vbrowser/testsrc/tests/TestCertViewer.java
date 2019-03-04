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

package tests;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.viewers.x509viewer.ViewerX509;

public class TestCertViewer
{

    public static void main(String[] args)
    {
        try
        {
            VRL vrl=new VRL("file:///home/ptdeboer/certs/test.crt"); 
        
            ViewerX509 tv = new ViewerX509();
            //tv.setViewStandalone(true);
            tv.startAsStandAloneApplication(vrl); 
        }
        catch (VrsException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }
    }

}
