package test;


import nl.uva.vlet.GlobalConfig;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.gui.lobo.LoboBrowser;
import nl.uva.vlet.vrl.VRL;

public class startStandalone
{

    public static void main(String args[])
    {
        GlobalConfig.init(); 
        
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
