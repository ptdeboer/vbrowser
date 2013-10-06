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

import nl.esciencecenter.ptk.exec.ShellChannel;
import nl.esciencecenter.ptk.util.vterm.CreateVTerm;
import nl.esciencecenter.ptk.util.vterm.VTerm;
import nl.esciencecenter.ptk.util.vterm.VTermChannelProvider;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.UIGlobal;
import nl.esciencecenter.vlet.vfs.ssh.jcraft.SSHChannel;
import nl.esciencecenter.vlet.vfs.ssh.jcraft.SSHShellChannelFactory;

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

    public static VTerm newVLTerm(ShellChannel shellChan)
    {
        return newVLTerm(null, shellChan);
    }

    public static VTerm newVLTerm(VRL loc)
    {
        return newVLTerm(loc, null);
    }

    public static VTerm newVLTerm(final VRL optionalLocation, final ShellChannel shellChan)
    {
        VTermChannelProvider provider = new VTermChannelProvider();

        provider.registerChannelFactory("SSH",new SSHShellChannelFactory(UIGlobal.getVRSContext())); 
        
        java.net.URI uri=null;
        
        if (optionalLocation != null)
        {
            uri = optionalLocation.toURINoException();
        }
        
        VTerm term = CreateVTerm.startVTerm(provider, uri, shellChan);

        return term;
    }


}
