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

package nl.nlesc.glite.lbl.srm.status;

import gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse;
import gov.lbl.srm.v22.stubs.TReturnStatus;

import org.apache.axis.types.URI;

/**
 * Wraps the <code>gov.lbl.srm.v22.stubs.SrmPrepareToGetResponse</code>,
 * returned from <coed>srmPrepareToGet</code> from the SRM service, in a common
 * interface for all srm responses.
 * 
 * @author S. koulouzis
 * 
 */
public class SRMGetResponse implements ISRMResponse
{
    private SrmPrepareToGetResponse responce;

    private URI[] suris;

    /**
     * Creates an instance of an
     * <code>nl.uva.vlet.lbl.srm.status.SRMGetResponse</code>.
     * 
     * @param responce
     *            the get response obtained from from
     *            <coed>srmPrepareToGet</code> in <code>ISRM</code>
     * @param suris
     */
    public SRMGetResponse(SrmPrepareToGetResponse responce, org.apache.axis.types.URI[] suris)
    {
        this.responce = responce;
        this.suris = suris;
    }

    @Override
    public String getRequestToken()
    {
        return responce.getRequestToken();
    }

    @Override
    public TReturnStatus getReturnStatus()
    {
        return responce.getReturnStatus();
    }

    @Override
    public URI[] getSURIs()
    {
        return this.suris;
    }

    // @Override
    // public void setSURLs(URI[] suris)
    // {
    // this.suris = suris;
    //        
    // }

}
