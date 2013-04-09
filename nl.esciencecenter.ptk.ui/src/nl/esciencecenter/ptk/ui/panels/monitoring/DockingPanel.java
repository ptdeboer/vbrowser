/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.ui.panels.monitoring;
import java.awt.Component;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

/**
 * Docks panels in vertical Boxed JPanel container
 */
public class DockingPanel extends JPanel
{
    private static final long serialVersionUID = -1629058136015889725L;
    
    public DockingPanel()
    {
        super(); 
        initGUI();
    }
    
    public void add(JPanel panel)
    {
        super.add(panel); 
        this.revalidate(); 
    }

    public JPanel[] getPanels()
    {
        Component[] comps = this.getComponents();
        if (comps==null)
            return null; 
        
        Vector<JPanel> panels=new Vector<JPanel>(); 
        
        for (Component comp:comps)
            if (comp instanceof JPanel)
                panels.add((JPanel)comp); 
        
        JPanel arr[]=new JPanel[panels.size()]; 
        arr=panels.toArray(arr); 
        return arr; 
    }
    
    private void initGUI() 
    {
        try 
        {
            this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
            this.setBorder(BorderFactory.createEtchedBorder(BevelBorder.LOWERED));
        }
        catch(Exception e) 
        {
            e.printStackTrace();
        }
    }

}
