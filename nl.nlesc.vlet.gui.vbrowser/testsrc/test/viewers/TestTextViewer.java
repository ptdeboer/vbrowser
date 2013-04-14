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

package test.viewers;

import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.gui.viewers.HexViewer;
import nl.nlesc.vlet.gui.viewers.TextViewer;
import nl.nlesc.vlet.gui.viewers.ViewContext;
import nl.nlesc.vlet.gui.viewers.ViewerManager;
import nl.nlesc.vlet.vrl.VRL;

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
        catch (VlException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
    }   
}
