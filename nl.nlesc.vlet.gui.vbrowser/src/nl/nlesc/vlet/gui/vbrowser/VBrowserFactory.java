package nl.nlesc.vlet.gui.vbrowser;

import java.awt.Dimension;
import java.awt.Point;

import nl.esciencecenter.ptk.Global;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.gui.BrowserFactory;
import nl.nlesc.vlet.gui.GuiSettings;
import nl.nlesc.vlet.gui.UIGlobal;
import nl.nlesc.vlet.gui.UILogger;
import nl.nlesc.vlet.gui.dialog.ExceptionForm;
import nl.nlesc.vlet.vrl.VRL;

public class VBrowserFactory implements BrowserFactory
{
    private static VBrowserFactory instance=null; 
    
    public static VBrowserFactory getInstance()
    {
        if (instance==null)
            instance=new VBrowserFactory();
        
        return instance; 
    }
    
    // ========================================================================
    //
    // ========================================================================
    
    protected VBrowserFactory()
    {
        //singleton!
    }
    
    @Override
    public BrowserController createBrowser()
    {
        return createBrowser((VRL)null); 
    }
    
    // @Override
    public BrowserController createBrowser(String str) throws VlException
    {
        return createBrowser(new VRL(str));
    }
    
    @Override
    public BrowserController createBrowser(VRL vrl)
    {
        VBrowser vb = new VBrowser(this);

        Point p=GuiSettings.getScreenCenter();
        Dimension size=vb.getSize(); 

        vb.setLocation(p.x-size.width/2,p.y-size.height/2);

        vb.setVisible(true);

        BrowserController bc = vb.getBrowserController();

        VRL rootLoc=null; 

        try
        {
            rootLoc = UIGlobal.getProxyVRS().getVirtualRootLocation();
        }
        catch (VlException e)
        {
            handle(e); 
        }  

        if (vrl == null)
        {
            vrl = rootLoc; 
        }

        // show it */

        bc.messagePrintln("New browser for:" + vrl);

        // start the populate in a different thread to finish the major task
        // event ask quickly as possible ! 

        bc.asyncOpenLocation(vrl);

        return bc; 
    }


    private void handle(VlException e)
    {
        ExceptionForm.show(e);
        UILogger.logException(this,ClassLogger.ERROR,e,"Exception:%s\n",e); 
    }

}
