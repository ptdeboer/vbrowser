package test;


import nl.nlesc.vlet.VletConfig;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.gui.lobo.LoboBrowser;
import nl.nlesc.vlet.vrl.VRL;

public class startStandalone
{

    public static void main(String args[])
    {
        VletConfig.init(); 
        
        try
        {
            //VRL vrl = new VRL("http://www.google.com");
            VRL vrl=new VRL("http://www.vl-e.nl/");
            LoboBrowser.viewStandAlone(vrl);
        } 
        catch (VlException e)
        {
            e.printStackTrace();
        }
    }
    
}
