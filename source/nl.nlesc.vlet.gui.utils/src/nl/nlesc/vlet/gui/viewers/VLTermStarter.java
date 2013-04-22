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

package nl.nlesc.vlet.gui.viewers;

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

import nl.esciencecenter.ptk.Global;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.nlesc.vlet.actions.ActionContext;
import nl.nlesc.vlet.actions.ActionMenuConstants;
import nl.nlesc.vlet.actions.ActionMenuMapping;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.gui.UIGlobal;
import nl.nlesc.vlet.vrs.io.VShellChannel;
import nl.nlesc.vlet.vrs.io.VShellChannelCreator;
import nl.nlesc.vlet.vrs.vfs.VFS;
import nl.nlesc.vlet.vrs.vfs.VFSClient;
import nl.nlesc.vlet.vrs.vfs.VFSNode;
import nl.nlesc.vlet.vrs.vfs.VFileSystem;
import nl.nlesc.vlet.vrs.vrl.VRL;
import nl.uva.vlet.util.vlterm.VLTerm;

public class VLTermStarter extends ViewerPlugin implements ActionListener
{
    public static void viewStandAlone(VRL loc)
    {
        VLTermStarter tv = new VLTermStarter();

        try
        {
            tv.startAsStandAloneApplication(loc); 
        }
        catch (VlException e)
        {
            System.out.println("***Error: Exception:" + e);
            e.printStackTrace();
        }
    }
    
    public static void main(String args[])
    {
        
        try
        {
            VRL loc=new VRL("sftp://localhost/home/"+Global.getGlobalUserName());
            
            //VFSClient.getDefault().openLocation(loc); 

            viewStandAlone(loc); 
        }
        catch (VlException e)
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

	public void startViewer(VRL location, String optMethodName, ActionContext actionContext) throws VlException
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
	public void updateLocation(VRL loc) throws VlException 
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
	    VLTerm term=null; 
	    
        try
        {
            VFSNode node;
            node = UIGlobal.getVFSClient().openLocation(loc);
            
            if (node.isFile())
                node=node.getParent();
            
            VFileSystem vfs = node.getFileSystem();
            
            if (vfs instanceof VShellChannelCreator)
            {
                VShellChannel shellChan = ((VShellChannelCreator)vfs).createShellChannel(loc);
                term=VLTerm.newVLTerm(shellChan); 
            }
            else
            {
                term=VLTerm.newVLTerm(loc);
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
            throws VlException
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
