package nl.uva.vlet.gui;

import java.util.logging.Level;

import nl.esciencecenter.ptk.util.logging.ClassLogger;

public class UILogger
{
    private static ClassLogger logger; 
    
    static
    {
        logger=ClassLogger.getLogger(UIGlobal.class); 
    }

    public static void logException(Object source,Level level,Throwable e,String format,Object... args)
    {
        logger.logException(level, e, format, args);
    }

    public static void errorPrintf(Object source,String format,Object... args)
    {
        logger.errorPrintf(format, args); 
    }

    public static void infoPrintf(Object source,String format,Object... args)
    {
        logger.infoPrintf(format, args); 
    }

    public static void warnPrintf(Object source,String format,Object... args)
    {
        logger.warnPrintf(format, args); 
    }

    public static void debugPrintf(Object source,String format,Object... args)
    {
        logger.debugPrintf(format, args); 
    }
    
}
