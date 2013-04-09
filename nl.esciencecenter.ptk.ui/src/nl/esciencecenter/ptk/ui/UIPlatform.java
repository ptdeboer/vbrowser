/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.ui;

import javax.swing.UIManager;

import nl.esciencecenter.ptk.ui.UIConst.LookAndFeelType;

/**
 * Global UI Platform. Contains Platform context for UI Applications.
 */
public class UIPlatform
{
    private static UIPlatform instance;

    public static synchronized UIPlatform getPlatform()
    {
        if (instance == null)
            instance = new UIPlatform();

        return instance;
    }

    // ========================================================================
    // Instance
    // ========================================================================

    private boolean appletMode = false;

    // ========================================================================
    // Pre INIT
    // ========================================================================

    protected UIPlatform()
    {
        init();
    }

    protected void init()
    {
    }

    /** Set applet mode. Must be one of the first method to be called. */
    public void setAppletMode(boolean val)
    {
        this.appletMode = val;
    }

    public boolean getAppletMode()
    {
        return appletMode;
    }

    // ========================================================================
    // UI Stuff
    // ========================================================================

    public boolean switchLookAndFeel(String lafstr)
    {
        return switchLookAndFeel(UIConst.LookAndFeelType.valueOf(lafstr));
    }

    /** Switch LAF */
    public boolean switchLookAndFeel(LookAndFeelType newtype)
    {
        return switchLookAndFeelType(newtype); // switch
    }
  
    public boolean switchLookAndFeelType(String lafstr)
    {
        return switchLookAndFeelType(LookAndFeelType.valueOf(lafstr));
    }

    public boolean switchLookAndFeelType(LookAndFeelType lafType)
    {
        // Set Look & Feel
        try
        {

            switch (lafType)
            {
                case NATIVE:
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    break;
                case DEFAULT:
                case METAL:
                    javax.swing.UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
                    break;
                case WINDOWS:
                    javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                    break;
                case GTK:
                    javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
                    break;
                case KDEQT:
                    // org.freeasinspeech.kdelaf.KdeLAF
                    break;
                case PLASTIC_3D:
                    javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.Plastic3DLookAndFeel");
                    break;
                case PLASTIC_XP:
                    javax.swing.UIManager.setLookAndFeel("com.jgoodies.looks.plastic.PlasticXPLookAndFeel");
                    break;
                default:
                    return false;
                    //break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(); 
            return false; 
        }
        
        return true; 
    }
    
}
