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

package nl.esciencecenter.glite.lbl.srm.status;

import gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse;
import gov.lbl.srm.v22.stubs.TCopyRequestFileStatus;
import gov.lbl.srm.v22.stubs.TReturnStatus;

/**
 * Wraps the <code>gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse</code> ,
 * returned from <coed>srmStatusOfCopyRequest</code> from the SRM service, in a
 * common interface for all srm request responses.
 * 
 * @author S. koulouzis
 * 
 */
public class SRMStatusOfRequestCpResponse implements ISRMStatusOfRequestResponse
{

    private SrmStatusOfCopyRequestResponse cpResponce;

    private String token;

    /**
     * Creates an instance of a
     * <code>nl.uva.vlet.lbl.srm.status.SRMStatusOfRequestCpResponse</code>
     * 
     * @param cpResponce
     *            the
     *            <code>gov.lbl.srm.v22.stubs.SrmStatusOfCopyRequestResponse</code>
     */
    public SRMStatusOfRequestCpResponse(SrmStatusOfCopyRequestResponse cpResponce)
    {
        this.cpResponce = cpResponce;
    }

    @Override
    public Integer getRemainingTotalRequestTime()
    {
        return cpResponce.getRemainingTotalRequestTime();
    }

    @Override
    public TReturnStatus getReturnStatus()
    {
        return cpResponce.getReturnStatus();
    }

    @Override
    public IFileStatus[] getStatusArray()
    {
        TCopyRequestFileStatus[] copyRequestFileStatus = cpResponce.getArrayOfFileStatuses().getStatusArray();
        CopyRequestFileStatus[] satatusArray = new CopyRequestFileStatus[copyRequestFileStatus.length];
        for (int i = 0; i < copyRequestFileStatus.length; i++)
        {
            satatusArray[i] = new CopyRequestFileStatus(copyRequestFileStatus[i]);
        }
        return satatusArray;
    }

    @Override
    public String getToken()
    {
        return token;
    }

    public void setToken(String token)
    {
        this.token = token;
    }

}
