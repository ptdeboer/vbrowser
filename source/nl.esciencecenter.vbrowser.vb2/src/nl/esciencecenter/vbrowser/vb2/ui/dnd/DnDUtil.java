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

package nl.esciencecenter.vbrowser.vb2.ui.dnd;

import nl.esciencecenter.ptk.util.logging.ClassLogger;

public class DnDUtil
{
    private static ClassLogger logger=ClassLogger.getLogger("DND"); 

    public static DnDTransferHandler getDefaultTransferHandler()
    {
        return DnDTransferHandler.getDefault(); 
    }

    // === logging ===
    
    public static void debugPrintf(String format,Object... args)
    {
        logger.debugPrintf("DnD:"+format,args); 
    }

    public static void debugPrintln(String message)
    {
        logger.debugPrintf("DnD:%s\n",message);
    }

    public static void infoPrintf(String format,Object... args)
    {
        logger.infoPrintf("DnD:"+format,args);
    }

    public static void errorPrintf(String format,Object... args)
    {
        logger.errorPrintf("DnD:"+format,args); 
    }

    public static void logException(Exception e, String format,Object... args)
    {
        logger.logException(ClassLogger.ERROR,e,format,args); 
    }
    
}
