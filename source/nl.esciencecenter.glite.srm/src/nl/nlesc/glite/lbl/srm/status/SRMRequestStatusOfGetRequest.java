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

import gov.lbl.srm.v22.stubs.TReturnStatus;

import org.apache.axis.types.URI;

/**
 * 
 * Implementation of Request status of get request. It's used as a container
 * object for constructing
 * <code>gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestRequest</code> (mainly for
 * holding the return status and request token)
 * 
 * @author S. Koulouzis
 * 
 */
public class SRMRequestStatusOfGetRequest implements ISRMRequestStatusOfRequest
{

    private SRMGetResponse response;

    /**
     * Creates an instance of
     * <code>nl.uva.vlet.lbl.srm.status.SRMRequestStatusOfGetRequest</code>.
     * 
     * @param responce
     *            the <code>nl.uva.vlet.lbl.srm.status.SRMGetResponse</code>
     *            constructed when getting back
     *            <code>gov.lbl.srm.v22.stubs.SrmGetResponse</code> from the SRM
     *            service.
     */
    public SRMRequestStatusOfGetRequest(SRMGetResponse response)
    {
        this.response = response;
    }

    @Override
    public String getRequestToken()
    {
        return response.getRequestToken();
    }

    @Override
    public URI[] getSURIs()
    {
        return response.getSURIs();
    }

    public TReturnStatus getReturnStatus()
    {
        return response.getReturnStatus();
    }

}
