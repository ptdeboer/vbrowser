package nl.esciencecenter.vbrowser.vb2.ui.dnd;

public class DnDUtil
{

    public static DnDTransferHandler getDefaultTransferHandler()
    {
        return DnDTransferHandler.getDefault(); 
        
    }

    // === logging ===
    
    public static void debugPrintf(String format,Object... args)
    {
        System.err.printf("DnD:"+format,args); 
    }
    
    public static void errorPrintf(String format,Object... args)
    {
        System.err.printf("DnD:"+format,args); 
    }

    public static void debugPrintln(String message)
    {
       System.err.printf("DnD:%s\n",message);
    }

    public static void infoPrintln(String format,Object... args)
    {
        System.err.printf("DnD:%s\n",args);
    }

    public static void infoPrintf(String format,Object... args)
    {
        System.err.printf("DnD:"+format,args);
    }

    public static void logException(Exception e, String format,Object... args)
    {
        System.err.printf("DnD:"+format,args);
        System.err.printf("Exception=%s\n",e); 
    }
    
}
