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

package uitests.test;

import javax.swing.JFrame;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.panels.attribute.AttributePanel;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRSContext;

public class testAttributePanel
{

    public static void main(String args[])
    {
        ClassLogger.getRootLogger().setLevelToDebug();  
        
        // jigloo panel: 
        AttributePanel panel=new AttributePanel();
        panel.setVisible(true); 
        
        ServerInfo lfcInfo=null;
        try
        {
            lfcInfo = new ServerInfo(VRSContext.getDefault(),new VRL("lfn://lfc.grid.sara.nl:5010/"));
        }
        catch (VRLSyntaxException e)
        {
            e.printStackTrace();
        }
        
        AttributeSet attrs=lfcInfo.getAttributeSet();
        AttributePanel.showEditor(attrs); 
        
        // tests asynchtonous setAttributes ! 
        
        JFrame frame=new JFrame();
        panel=new AttributePanel();
       
        frame.add(panel);
        //frame.pack();
        frame.setVisible(true);
        
        panel.setAttributes(attrs,true);
        
        // update frame: 
        frame.setSize(frame.getPreferredSize());
        
        // panel.setSize(panel.getLayout().preferredLayoutSize(panel)); 
    }

}
