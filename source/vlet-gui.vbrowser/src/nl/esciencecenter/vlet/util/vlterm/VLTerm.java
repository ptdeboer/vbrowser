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

package nl.esciencecenter.vlet.util.vlterm;

import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.vfs.ssh.jcraft.SSHShellChannelFactory;
import nl.piter.vterm.api.ShellChannel;
import nl.piter.vterm.emulator.VTermChannelProvider;

public class VLTerm
{
    // ========================================================================

    // ========================================================================

    public static void main(String[] arg)
    {
        newVLTerm();
    }

    public static void newVLTerm()
    {
        newVLTerm(null, null);
    }

    public static void newVLTerm(ShellChannel shellChan)
    {
        newVLTerm(null, shellChan);
    }

    public static void newVLTerm(VRL loc)
    {
        newVLTerm(loc, null);
    }

    public static void newVLTerm(final VRL optionalLocation, final ShellChannel shellChan)
    {
        VTermChannelProvider provider = new VTermChannelProvider();

        provider.registerChannelFactory("SSH",new SSHShellChannelFactory(UIGlobal.getVRSContext())); 
        
        java.net.URI uri=null;
        
        if (optionalLocation != null)
        {
            uri = optionalLocation.toURINoException();
        }

        new nl.piter.vterm.VTermStarter().withChannelProvider(provider).start(shellChan, uri);
    }


}
