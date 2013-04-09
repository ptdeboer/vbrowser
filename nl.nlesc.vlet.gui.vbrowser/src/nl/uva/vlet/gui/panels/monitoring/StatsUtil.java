package nl.uva.vlet.gui.panels.monitoring;

import nl.esciencecenter.ptk.presentation.Presentation;

public class StatsUtil
{

    public static String timeString(long timeInMillis, boolean showMillis)
    {
        String timestr = "";
    
        if (timeInMillis > 1000L * 24L * 60L * 60L)
        {
            long days = timeInMillis / (1000L * 24L * 60L * 60L);
            timestr += days + " (days) ";
        }
    
        if (timeInMillis > 1000 * 60 * 60)
        {
            long hours = (timeInMillis / (1000L * 60 * 60)) % 60;
            timestr += timestr + Presentation.to2decimals(hours) + ":";
        }
        // show it anyway to always show 00:00s format
        // if (time>1000*60)
        {
            long mins = (timeInMillis / (1000 * 60)) % 60;
            timestr += timestr + Presentation.to2decimals(mins) + ":";
        }
    
        long secs = (timeInMillis / 1000L) % 60L;
        timestr += Presentation.to2decimals(secs) + "s";
    
        if (showMillis)
            timestr += "." + (timeInMillis % 1000);
    
        return timestr;
    }

}
