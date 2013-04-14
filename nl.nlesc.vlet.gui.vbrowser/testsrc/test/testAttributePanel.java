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

package test;

import javax.swing.JFrame;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.vlet.data.VAttributeSet;
import nl.nlesc.vlet.exception.VRLSyntaxException;
import nl.nlesc.vlet.gui.panels.attribute.AttributePanel;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRSContext;

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
        
        VAttributeSet attrs=lfcInfo.getAttributeSet();
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
