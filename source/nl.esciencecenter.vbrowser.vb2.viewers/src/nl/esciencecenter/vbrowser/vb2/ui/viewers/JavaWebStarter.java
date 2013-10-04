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

package nl.esciencecenter.vbrowser.vb2.ui.viewers;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.exec.LocalExec;
import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.MimeViewer;
import nl.esciencecenter.vbrowser.vb2.ui.viewerpanel.ViewerPanel;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/** 
 * Start Webstart application. 
 */ 
public class JavaWebStarter extends ViewerPanel implements ActionListener, MimeViewer
{
    private static final long serialVersionUID = -8153274632131510572L;
    private JTextPane mainTP;
    private JButton okB;
    private JPanel buttonPanel;

    public JavaWebStarter() 
    {
        
    }
    
    // do not embed the viewer inside the VBrowser.
    @Override
    public boolean isStandaloneViewer()
    {
        return true;
    }
    
    @Override
    public String[] getMimeTypes()
    {
        return new String[]{"application/x-java-jnlp-file"}; 
    }
    
    @Override
    public String getName()
    {
        return "JavaWebStart"; 
    }

    public void initGUI()
    {
        FormLayout thisLayout = new FormLayout(
                "5dlu, max(p;145dlu):grow, 5dlu", 
                "max(p;5dlu), 24dlu, max(p;5dlu), 6dlu, max(p;15dlu), max(p;5dlu)");
        this.setLayout(thisLayout);
        this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        {
            mainTP = new JTextPane();
            this.add(mainTP, new CellConstraints("2, 2, 1, 2, default, default"));
            mainTP.setText("Starting Java Web Start. Please wait for the dailog to appear and follow instructions. \n");
            mainTP.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        }
        {
            buttonPanel = new JPanel();
            this.add(buttonPanel, new CellConstraints("2, 5, 1, 1, default, default"));
            buttonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
            {
                okB = new JButton();
                buttonPanel.add(okB);
                okB.setText("OK");
                okB.addActionListener(this); 
            }
        }
    }
    
    @Override
    public void initViewer()
    {
        initGUI(); 
    }

    @Override
    public void stopViewer()
    {
    }

    @Override
    public void updateURI(URI loc, boolean startViewer) 
    {
        startURI(loc);
    }

    @Override
    public void startViewer()
    {
        startURI(getURI()); 
    }
    
    public void startURI(URI loc) 
    {
        try
        {
            debug("starting:"+loc);
            
            // Execute Plain VRL: 
            executeJavaws(loc);
        }
        catch (Throwable e)
        {
            notifyException("Failed to start webstarter for:"+loc,e); 
        }
    }

    private void executeJavaws(URI loc)
    {
        try
        {
            String javaHome=GlobalProperties.getJavaHome(); 
            String cmdPath=javaHome+"/bin/javaws";
            
            if (GlobalProperties.isWindows())
                cmdPath+=".exe"; 
    
            URIFactory uriFactory=new URIFactory("file:///"+cmdPath); 
    
            if (GlobalProperties.isWindows())
            {
                cmdPath=uriFactory.getDosPath(); 
            }
            else
            {
                cmdPath=uriFactory.getPath(); 
            }
            
            String cmds[]=new String[2];
            cmds[0]=cmdPath;
            cmds[1]=loc.toString(); 
            
            String result[]=LocalExec.execute(cmds); 
        }
        catch (Throwable e)
        {
            notifyException("Failed to start WebStarter for:"+loc,e); 
        }
    }

    private void debug(String str)
    {
        
    }


    @Override
    public void disposeViewer()
    {
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        Object source=e.getSource();
        if (source.equals(this.okB))
        {
            closeViewer(); 
        }
    }

 
}
