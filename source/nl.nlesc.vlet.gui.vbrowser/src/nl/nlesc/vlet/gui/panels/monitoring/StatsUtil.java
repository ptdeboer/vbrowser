/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.nlesc.vlet.gui.panels.monitoring;

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
