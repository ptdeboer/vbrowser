/*
 * Copyright 2006-2010 Virtual Laboratory for e-Science (www.vl-e.nl)
 * Copyright 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at the following location:
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * For the full license, see: LICENSE.txt (located in the root folder of this distribution).
 * ---
 */
// source:

package test;

import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.vfs.skelfs.SkelFSFactory;


public class TestSkelFSBrowser
{
	public static void main(String args[])
	{
		try
		{
		    VletConfig.init();
			VRS.getRegistry().registerVRSDriverClass(SkelFSFactory.class);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		// The VBrowser classes must be in the classpath to be able to start this. 
		nl.esciencecenter.vlet.gui.StartVBrowser.main(args);
	}


}
