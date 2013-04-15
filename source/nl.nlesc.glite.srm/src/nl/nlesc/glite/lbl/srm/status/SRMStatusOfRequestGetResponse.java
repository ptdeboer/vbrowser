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

import gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse;
import gov.lbl.srm.v22.stubs.TGetRequestFileStatus;
import gov.lbl.srm.v22.stubs.TReturnStatus;

/**
 * Wraps the <code>gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse</code> ,
 * returned from <coed>srmStatusOfGetRequest</code> from the SRM service, in a
 * common interface for all srm request responses.
 * 
 * @author S. koulouzis
 * 
 */
public class SRMStatusOfRequestGetResponse implements ISRMStatusOfRequestResponse
{

    private SrmStatusOfGetRequestResponse getResponce;

    private String requestToken;

    /**
     * Creates an instance of a
     * <code>nl.uva.vlet.lbl.srm.status.SRMStatusOfRequestGetResponse</code>
     * 
     * @param getResponce
     *            the
     *            <code>gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse</code>
     */
    public SRMStatusOfRequestGetResponse(SrmStatusOfGetRequestResponse getResponce)
    {
        this.getResponce = getResponce;
    }

    @Override
    public Integer getRemainingTotalRequestTime()
    {
        return getResponce.getRemainingTotalRequestTime();
    }

    @Override
    public TReturnStatus getReturnStatus()
    {
        return getResponce.getReturnStatus();
    }

    @Override
    public IFileStatus[] getStatusArray()
    {
        TGetRequestFileStatus[] getRequestFileStatus = getResponce.getArrayOfFileStatuses().getStatusArray();
        GetRequestFileStatus[] satatusArray = new GetRequestFileStatus[getRequestFileStatus.length];
        for (int i = 0; i < getRequestFileStatus.length; i++)
        {
            satatusArray[i] = new GetRequestFileStatus(getRequestFileStatus[i]);
        }
        return satatusArray;
    }

    @Override
    public String getToken()
    {
        return this.requestToken;
    }

    @Override
    public void setToken(String requestToken)
    {
        this.requestToken = requestToken;

    }

}
