/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.ui.fonts;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * 
 * Implementation of FontComboBoxRenderer.
 * Renderers the text in the ComboBox with the font name specified. 
 * Is special component in the FontToolBar.
 *  
 * @author P.T. de Boer
 */
public class FontComboBoxRenderer  extends JLabel implements ListCellRenderer
{
    boolean antiAliasing=true;
    private FontToolBar fontToolBar; 
    /**
     * 
     */
    
    private static final long serialVersionUID = -2462866413990104352L;

    public FontComboBoxRenderer(FontToolBar bar) 
    {
        this.fontToolBar=bar; 
        
            setOpaque(true);
            setHorizontalAlignment(CENTER);
            setVerticalAlignment(CENTER);
    }
        
   public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus)
        {
            if (isSelected) 
            {
                setBackground(list.getSelectionBackground());
                setForeground(list.getSelectionForeground());
            }
            else 
            {
                setBackground(list.getBackground());
                setForeground(list.getForeground());
            }
            
            FontInfo info=fontToolBar.getFontInfo();
            
            setFont(new Font((String)value,info.getFontStyle(),14)); 
            
            setText((String)value);
            
            //GuiSettings.setAntiAliasing(this,info.getAntiAliasing()); 
           
            return this;
        }
    
}
