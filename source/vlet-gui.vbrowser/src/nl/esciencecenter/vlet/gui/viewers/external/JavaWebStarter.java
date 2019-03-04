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

package nl.esciencecenter.vlet.gui.viewers.external;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.border.BevelBorder;

import nl.esciencecenter.ptk.exec.LocalProcess;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.viewers.ViewerPlugin;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;


/** Wrapper plugin for external tools */ 
public class JavaWebStarter extends ViewerPlugin implements ActionListener
{
    private static final long serialVersionUID = -8153274632131510572L;
    private JTextPane mainTP;
    private LocalProcess process;
    private JButton okB;
    private JPanel buttonPanel;

    public JavaWebStarter() 
    {
        
    }
    
    // do not embed the viewer inside the VBrowser.
    @Override
    public boolean getAlwaysStartStandalone()
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
    public void updateLocation(VRL loc) throws VrsException
    {
        try
        {
            openVRL(loc);
        }
        catch (IOException e)
        {
            throw new VrsException(e.getMessage(),e); 
        }
    }

    public void openVRL(VRL loc) throws IOException
    {
        debug("starting:"+loc);
        
        // Execute Plain VRL: 
        process=ExternalToolDB.getDefault().executeVrl(ExternalToolDB.JAVAWS,loc,false);
        
        process.waitFor(); 
        
        debug(">>> stdout="+process.getStdout()); 
        debug(">>> stderr="+process.getStderr());
    }

    private void debug(String str)
    {
        //Global.errorPrintln(this,str); 
    }
    
	/**
	* This method should return an instance of this class which does 
	* NOT initialize it's GUI elements. This method is ONLY required by
	* Jigloo if the superclass of this class is abstract or non-public. It 
	* is not needed in any other situation.
	 */
	public static Object getGUIBuilderInstance() {
		return new JavaWebStarter(Boolean.FALSE);
	}
	
	/**
	 * This constructor is used by the getGUIBuilderInstance method to
	 * provide an instance of this class which has not had it's GUI elements
	 * initialized (ie, initGUI is not called in this constructor).
	 */
	public JavaWebStarter(Boolean initGUI)
	{
		super();
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
            exitViewer(); 
        }
    }
}
