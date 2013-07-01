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

package nl.esciencecenter.glite.lfc;

import org.globus.gsi.GlobusCredential;

public class LFCConfig
{
    public LFCConfig()
    {
    }
    
	/** 
	 * Globus Credential to use, if NULL the default
	 * Globus Credential will be used. 
	 */
	public GlobusCredential globusCredential=null;
	
	/** Default connection timeout in milliseconds */ 
    public int timeout=30*1000; 
 
}
