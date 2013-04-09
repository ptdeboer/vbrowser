/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.ui;

import nl.esciencecenter.ptk.data.SecretHolder;

/**
 * UI Interface for UI Callbacks. 
 */
public interface UI
{
    /** Whether user interaction is possible */ 
    public boolean isEnabled();
    
    /** Display message dialog or print to console */ 
    void showMessage(String message);
    
    /**
     * Simple Yes/No prompter 
     * @param defaultValue value to return if there is no UI present 
     *        or it is currently disabled. 
     */ 
    boolean askYesNo(String title,String message, boolean defaultValue);

    /**
     * Simple Yes/No/Cancel prompter. 
     * Returns JOptionPane.CANCEL_OPTION if no UI present
     */ 
    int askYesNoCancel(String title,String message);

    /** Ask for password, passphrase or other 'secret' String */ 
    boolean askAuthentication(String message, SecretHolder secretHolder);
    
    /**
     * Simple formatted Input Dialog. Method is wrapper for JOptionPane ! 
     * See  JOptionPane.showConfirmDialog() for options.
     * @return JOptionPane.OK_OPTION if successful. 
     *         Parameter inputFields can contain modified (Swing) objects.  
     */ 
    int askInput(String title, Object[] inputFields, int jOpentionPaneOption);
    
}
