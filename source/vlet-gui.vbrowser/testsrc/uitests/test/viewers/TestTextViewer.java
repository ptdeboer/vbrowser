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

package uitests.test.viewers;

import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.viewers.HexViewer;
import nl.esciencecenter.vlet.gui.viewers.TextViewer;
import nl.esciencecenter.vlet.gui.viewers.ViewContext;
import nl.esciencecenter.vlet.gui.viewers.ViewerManager;

public class TestTextViewer
{

    public static void main(String args[])
    {   
        TextViewer textViewer=new TextViewer();     
        ViewContext ctx = new ViewContext(null); 
        ViewerManager manager=new ViewerManager(null, ctx, textViewer);

        
        try
        {
            textViewer.startAsStandAloneApplication(new VRL("file:///etc/passwd"));
        }
        catch (VRLSyntaxException e)
        {
            e.printStackTrace();
        }
        catch (VrsException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }   
}
