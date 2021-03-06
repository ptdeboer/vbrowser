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

package examples;

import gov.lbl.srm.v22.stubs.TMetaDataPathDetail;

import java.util.ArrayList;

import nl.esciencecenter.glite.lbl.srm.SRMClient;
import nl.esciencecenter.glite.lbl.srm.SRMClientV2;

public class SRMExample
{
    public static void main(String args[])
    {
        boolean connect = true;
        String host = "tbn18.nikhef.nl";
        int port = 8446;

        try
        {
            SRMClientV2 srmc = SRMClient.createSRMClientV2(host, port, connect);

            System.out.println(">>> Ping=" + srmc.srmPing().getVersionInfo());

            boolean fulldetails = true;
            ArrayList<TMetaDataPathDetail> details = srmc.listPath("/", fulldetails);

            for (TMetaDataPathDetail detail : details)
            {
                System.out.println(" - path=" + detail.getPath());
                System.out.println(" - size=" + detail.getSize());
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

}
