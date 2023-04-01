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

package nl.esciencecenter.vlet.gui.viewers;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

import nl.esciencecenter.ptk.GlobalProperties;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.actions.ActionContext;
import nl.esciencecenter.vlet.actions.ActionMenuConstants;
import nl.esciencecenter.vlet.actions.ActionMenuMapping;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.util.vlterm.VLTerm;
import nl.esciencecenter.vlet.vrs.io.VShellChannelCreator;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.vlet.vrs.vfs.VFileSystem;
import nl.piter.vterm.api.ShellChannel;


public class VLTermStarter extends ViewerPlugin implements ActionListener
{
    public static void viewStandAlone(VRL loc)
    {
        VLTermStarter tv = new VLTermStarter();

        try
        {
            tv.startAsStandAloneApplication(loc); 
        }
        catch (VrsException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }
    }
    
    public static void main(String args[])
    {
        
        try
        {
            VRL loc=new VRL("sftp://localhost/home/"+GlobalProperties.getGlobalUserName());
            
            //VFSClient.getDefault().openLocation(loc); 

            viewStandAlone(loc); 
        }
        catch (VrsException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }

    }
    
	private static final long serialVersionUID = 3709591457664399612L;
    private JPanel panel;
    private JButton okButton;
    private JTextField textF;
    private JPanel butPanel;

	@Override
	public String getName() 
	{
	    return "VLTerm";
	}
	
	@Override
	public String[] getMimeTypes() 
	{
		return new String[]{"application/ssh-location"}; 
	}
	
	@Override
	public void initViewer()
	{
		initGUI(); 
	}

	public boolean getAlwaysStartStandalone()
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

	public void startViewer(VRL location, String optMethodName, ActionContext actionContext) throws VrsException
    {
        setVRL(location);

        // update location
        if (location != null)
            updateLocation(location);
        
        // perform action method: 
        if (StringUtil.isWhiteSpace(optMethodName)==false)
            this.doMethod(optMethodName,actionContext); 
    }
	
	@Override
	public void updateLocation(VRL loc) throws VrsException 
	{
		startVLTerm(loc); 
	}

	@Override
	public void stopViewer()
	{
	}

	@Override
	public void disposeViewer()
	{
	}
	
	public void startVLTerm(VRL loc) 
	{

        try
        {
            VFSNode node;
            node = UIGlobal.getVFSClient().openLocation(loc);
            
            if (node.isFile())
                node=node.getParent();
            
            VFileSystem vfs = node.getFileSystem();
            
            if (vfs instanceof VShellChannelCreator)
            {
                ShellChannel shellChan = ((VShellChannelCreator)vfs).createShellChannel(loc);
                VLTerm.newVLTerm(shellChan);
            }
            else
            {
                VLTerm.newVLTerm(loc);
            }
        }
        catch (Exception e)
        {
            handle(e);
        }
        
        try
        {
            Thread.sleep(1);
        }
        catch (InterruptedException e)
        {
         //   e.printStackTrace();
        } 
        
        exitViewer(); 
        // this.disposeJFrame(); 
		 
	}
	
	public Vector<ActionMenuMapping> getActionMappings()
    {
        Vector<ActionMenuMapping> mappings=new Vector<ActionMenuMapping>(); 
        
        // LFC File mappings:
        ActionMenuMapping mapping;         
        
        // single selection action: 
        mapping=new ActionMenuMapping("openVLTerm","Open VLTerm");
        // Sftp Dirs: 
        mapping.addTypeSchemeMapping(VFS.DIR_TYPE,VFS.SFTP_SCHEME); 
        
        mapping.setMenuOptions(ActionMenuMapping.DEFAULT_MENU_OPTIONS 
        		| ActionMenuConstants.MENU_CANVAS_ACTION); 
        //mapping.addResourceMapping(fileTypes,schemes,null,null,ActionMenuConstants.SELECTION_ONE);        
        mappings.add(mapping);
        
        return mappings; 
    }
	
	/** Perform Dynamic Action Method */
    public void doMethod(String methodName, ActionContext actionContext)
            throws VrsException
    {
    	// already handled by first openLocation. This Method will be called
    	// after the VLTermStarter already started for this location. 
    	//VRL loc=actionContext.getSource();
    	//System.err.println("location="+loc); 
    	//startVLTerm(loc); 
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        this.disposeJFrame(); 
    }
}
