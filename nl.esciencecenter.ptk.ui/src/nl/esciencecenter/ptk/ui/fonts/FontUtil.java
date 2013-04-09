/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.ui.fonts;

import java.awt.Font;

public class FontUtil
{
    /** Create Font Info from Font, copying font attributes like style and name.  */ 
    public static FontInfo createFontInfo(Font font)
    {
       FontInfo info=new FontInfo(); 
       info.init(font); 
       return info; 
    }

    /** Get FontInfo from Font Info data base */ 
    public static FontInfo getFontInfo(String name)
    {
        return FontInfo.getFontInfo(name);
    }
    
    /**
     * Check's FontInfo alias database, if not Font.getFont(name) 
     * is returned; 
     * 
     * @param name
     * @return either java's default font or font from font database. 
     */
    public static Font createFont(String name)
    {
        FontInfo info=FontInfo.getFontInfo(name);
        
        if (info!=null) 
            return info.createFont();
            
        return Font.getFont(name);   
    }

}
