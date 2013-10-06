package nl.esciencecenter.vbrowser.vb2.vlet.viewers;
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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.vterm.CreateVTerm;
import nl.esciencecenter.ptk.util.vterm.ShellChannel;
import nl.esciencecenter.ptk.util.vterm.VTerm;
import nl.esciencecenter.ptk.util.vterm.VTermChannelProvider;
import nl.esciencecenter.vbrowser.vb2.ui.viewerplugin.MimeViewer;
import nl.esciencecenter.vbrowser.vb2.ui.viewerplugin.ViewerPanel;
import nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs.VRSProxyFactory;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vfs.ssh.jcraft.SSHShellChannelFactory;


public class VLTermStarter extends ViewerPanel implements MimeViewer, ActionListener
{
    private static final long serialVersionUID = 6104695556400295643L;
    
    private JPanel panel;
    private JButton okButton;
    private JTextField textF;
    private JPanel butPanel;

	@Override
	public String getName() 
	{
	    return "VTerm";
	}
	
	@Override
	public String[] getMimeTypes() 
	{
		return new String[]{"application/ssh-location"}; 
	}
	
	@Override
	public void doInitViewer()
	{
		initGUI(); 
	}
	
	@Override
	public boolean isStandaloneViewer()
	{
		 return true; 
	}
	 
	private void initGUI() 
	{
	    
	    
	    {
	        panel=new JPanel();
	        panel.setPreferredSize(new Dimension(250,60)); 
	        
	        this.add(panel); 
            panel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
	        panel.setLayout(new BorderLayout()); 
	        {
	            textF = new JTextField(); 
	            textF.setText("\nA VLTerm will be started.\n "); 
	            
	            panel.add(textF,BorderLayout.CENTER);
	        }
	        {
	            butPanel= new JPanel();
	            panel.add(butPanel,BorderLayout.SOUTH);
	            butPanel.setLayout(new FlowLayout()); 
	           // butPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));

    	        {
    	            okButton=new JButton(); 
    	            okButton.setText("OK"); 
    	            butPanel.add(okButton); 
    	            okButton.addActionListener(this); 
    	        }
	        }
	    }
	        
	}

	public void doStartViewer(String optMethodName) 
    {
	    startVTerm(getURI(),null);
    }
	
	@Override
	public void doUpdateURI(java.net.URI uri)  
	{
		startVTerm(uri,null); 
	}

	@Override
	public void doStopViewer()
	{
	}

	@Override
	public void doDisposeViewer()
	{
	}
	
	public void startVTerm(java.net.URI uri,final ShellChannel shellChan) 
	{
	    VTermChannelProvider provider = new VTermChannelProvider();

	    // Share Context ! 
	    provider.registerChannelFactory("SSH",new SSHShellChannelFactory(VRSProxyFactory.getProxyVRSContext())); 
	        
	    VTerm term = CreateVTerm.startVTerm(provider, uri, shellChan);
	}

    @Override
    public void actionPerformed(ActionEvent e)
    {
        this.closeViewer(); 
    }

    @Override
    public Map<String, List<String>> getMimeMenuMethods()
    {
        return null;
    }
 }
