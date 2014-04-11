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

package nl.esciencecenter.vlet.vfs.gftp;

import java.util.Vector;

import nl.esciencecenter.ptk.util.logging.ClassLogger;

import org.globus.ftp.GridFTPRestartMarker;
import org.globus.ftp.Marker;
import org.globus.ftp.MarkerListener;
import org.globus.ftp.PerfMarker;

public class TransferMarkerlistener implements MarkerListener
{

    public TransferMarkerlistener(GftpFileSystem gftpFileSystem)
    {

    }

    public void markerArrived(Marker marker)
    {
        try
        {
            if (marker instanceof PerfMarker)
            {
                PerfMarker perfMarker = (PerfMarker) marker;
                // Global.errorPrintln(this,"PerfMarker:"+perfMarker);
                // Global.errorPrintln(this,"PerfMarker.timestamp       :"+perfMarker.getTimeStamp());
                // Global.errorPrintln(this,"PerfMarker.stripIndex      :"+perfMarker.getStripeIndex());
                // Global.errorPrintln(this,"PerfMarker.totalStipeCount :"+perfMarker.getTotalStripeCount());
            }
            else if (marker instanceof GridFTPRestartMarker)
            {
                Vector vec = ((GridFTPRestartMarker) marker).toVector();
                // Global.errorPrintln(this,"GridFTPRestartMarker:"+((GridFTPRestartMarker)marker).toVector()
                // );

                if (vec != null)
                {
                    // for (Object obj:vec)
                    // {
                    // Global.errorPrintln(this,"GridFTPRestartMarker: obj"+obj);
                    // }
                }
            }
            else
            {
                // Global.errorPrintln(this,"Marker:"+marker.getClass());
            }

        }
        catch (Exception e)
        {
            ClassLogger.getLogger(TransferMarkerlistener.class).logException(ClassLogger.ERROR, e, "markerArrived() Exception\n");
        }

    }

}
