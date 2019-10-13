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

package starters;


import nl.esciencecenter.vlet.gui.StartVBrowser;
import nl.esciencecenter.vlet.gui.lobo.LoboBrowser;
import nl.esciencecenter.vlet.gui.viewers.ViewerRegistry;
import nl.esciencecenter.vlet.vfs.gftp.GftpFSFactory;
import nl.esciencecenter.vlet.vfs.lfc.LFCFSFactory;
import nl.esciencecenter.vlet.vfs.srm.SRMFSFactory;
import nl.esciencecenter.vlet.vrs.VRSFactory;
import nl.esciencecenter.vlet.vrs.globusrs.GlobusRSFactory;
import org.lobobrowser.main.PlatformInit;

/**
 * Start Dev VBRowser with plugins and full vrs.
 */
public class StartVBrowserDev
{
	
	public static void main(String args[])
	{
		try {
			initViewers();
			initVRS();
			// main
			StartVBrowser.main(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initViewers() throws Exception {
		// LOBO
		PlatformInit instance = PlatformInit.getInstance();
		// disable logging BEFORE initializing Lobo
		instance.initLogging(false);
		instance.initExtensions();
		ViewerRegistry.getRegistry().registerViewer(LoboBrowser.class);
	}

	private static void initVRS() {
		// check bindings compile time:
		Class<? extends VRSFactory> vrsRS = GlobusRSFactory.class;
		Class<? extends VRSFactory> gftpFS = GftpFSFactory.class;
		Class<? extends VRSFactory> srmFS = SRMFSFactory.class;
		Class<? extends VRSFactory> lfcFS = LFCFSFactory.class;
	}
}

