package jfx;

import nl.esciencecenter.ptk.jfx.util.FXFileChooser;
import nl.esciencecenter.ptk.jfx.util.FXFileChooser.ChooserType;

public class TestFXFileChooser
{

    public static void main(String[] args)
    {
        java.net.URI path=FXFileChooser.staticStartFileChooser(ChooserType.OPEN_FILE,"/home/ptdeboer");
        System.out.println("Open File Path="+path);
        
        path=FXFileChooser.staticStartFileChooser(ChooserType.SAVE_FILE,"/home/ptdeboer");
        System.out.println("Save File Path="+path); 

        path=FXFileChooser.staticStartFileChooser(ChooserType.OPEN_DIR,"/home/ptdeboer");
        System.out.println("Open Dir Path="+path); 

    }
}
