package nl.esciencecenter.vbrowser.vb2.ui.presentation;

import java.awt.Color;
import java.util.Map;

public class AttributePresentation
{
    // === class === //s
    public static class PreferredSizes
    {
        int minimum=-1; 
        int preferred=-1; 
        int maximum=-1; 
    
        public PreferredSizes(int minWidth, int prefWidth, int maxWidth)
        {
            this.minimum=minWidth;
            this.preferred=prefWidth;
            this.maximum=maxWidth;
        }
        
        public int getMinimum()
        {
            return minimum; 
        }
        
        public int getMaximum()
        {
            return maximum; 
        }
        
        public int getPreferred()
        {
            return preferred; 
        }
        
        public int[] getValues()
        {
            return new int[]{minimum,preferred,maximum}; 
        }
    
        /** Set [minimum,preferred,maximum] values */ 
        public void setValues(int[] values)
        {
            this.minimum   = values[0]; 
            this.preferred = values[1]; 
            this.maximum   = values[2]; 
        }
    }

    AttributePresentation.PreferredSizes widths = null;

    Color foreground = null;
    
    Color background = null;

    Map<String, Color> colorMap = null;

    boolean attributeFieldResizable=true; 

    public AttributePresentation.PreferredSizes getWidths()
    {   
        return widths; 
    }
    
    public int[] getWidthValues()
    {   
        if (widths==null)
            return null;
        
        return widths.getValues();  
    }
    
    public void setWidthValues(int[] values)
    {
        if (this.widths==null)
            this.widths=new AttributePresentation.PreferredSizes(values[0],values[1],values[2]); 
        else
            this.widths.setValues(values); 
    }
}