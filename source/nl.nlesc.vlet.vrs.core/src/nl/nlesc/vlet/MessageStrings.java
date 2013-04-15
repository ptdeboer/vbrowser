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

package nl.nlesc.vlet;

/**
 * TODO: Move all VRS message strings to this place. 
 */
public class MessageStrings 
{
    // Don't make final to allow for alternative messages!
	public static String TXT_COULDNT_GET_LOCALHOSTNAME = "Couldn't get localhostname.";

    public static String TXT_GLOBAL_PASSIVE_MODE = "Set all servers to passiveMode.";
	
	public static String TXT_PASSIVE_MODE_OVERRIDDEN = "Can not set this property: Global passiveMode has been set to 'true'.";
	
	public static String TXT_SKIP_FLOPPY_SCAN_WINDOWS = "Skip checking for floppy if windows keeps complaining.";
	
	public static String TXT_ALLOWED_INCOMING_PORTRANGE = "Allowed portrange for incoming (tcp/ip) connections."; 
}
