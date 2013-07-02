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

package nl.esciencecenter.vlet.gui;

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
