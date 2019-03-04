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

package tests;

import java.util.Properties;

import nl.esciencecenter.vlet.gui.dialog.AuthenticationDialog;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRS;

public class testAuthenticationDialog
{

    public static void main(String[] args)
    {
        ServerInfo info = AuthenticationDialog.askAuthentication(
                "Testing authentication Dailog\n.Authentication is Needed\n " +
                "This is a long sentence to tests the autowrap function. 1.2.3.4.5.6.7.8.9.10. ",
                
                null);

        if (info == null)
        {
            System.out.println("*** Action Cancelled ***");
        }
        else
        {
            System.out.println("User      ="
                    + info.getUsername()); 
            System.out.println("passwd    ="
                    + info.getPassword()); 
            System.out.println("passphrase="
                    + info.getPassphrase()); 
        }
    }

}
