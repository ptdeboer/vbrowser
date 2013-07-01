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

import gov.lbl.srm.v22.stubs.ArrayOfTExtraInfo;
import gov.lbl.srm.v22.stubs.TGetRequestFileStatus;
import gov.lbl.srm.v22.stubs.TReturnStatus;

import org.apache.axis.types.URI;
import org.apache.axis.types.UnsignedLong;

/**
 * Wraps the <code>gov.lbl.srm.v22.stubs.TGetRequestFileStatus</code>, returned
 * from
 * <coed>ov.lbl.srm.StorageResourceManager.SrmStatusOfGetRequestResponse</code>,
 * in a common interface for all srm file statuses.
 * 
 * @author S. koulouzis
 * 
 */
public class GetRequestFileStatus implements IFileStatus
{
    private TGetRequestFileStatus tGetRequestFileStatus;

    /**
     * Creates an instance of
     * <code>nl.uva.vlet.lbl.srm.status.GetRequestFileStatus</code>
     * 
     * @param tGetRequestFileStatus
     *            the <code>gov.lbl.srm.v22.stubs.TGetRequestFileStatus</code> ,
     *            obtained from
     *            <coed>gov.lbl.srm.v22.stubs.SrmStatusOfGetRequestResponse
     *            </code>
     */
    public GetRequestFileStatus(TGetRequestFileStatus tGetRequestFileStatus)
    {
        this.tGetRequestFileStatus = tGetRequestFileStatus;
    }

    @Override
    public Integer getEstimatedWaitTime()
    {

        return tGetRequestFileStatus.getEstimatedWaitTime();
    }

    @Override
    public UnsignedLong getFileSize()
    {
        return tGetRequestFileStatus.getFileSize();
    }

    @Override
    public Integer getRemainingFileLifetime()
    {
        return null;
    }

    @Override
    public Integer getRemainingPinLifetime()
    {
        return null;
    }

    @Override
    public Integer getRemainingPinTime()
    {
        return tGetRequestFileStatus.getRemainingPinTime();
    }

    @Override
    public URI getSURL()
    {
        return tGetRequestFileStatus.getSourceSURL();
    }

    @Override
    public TReturnStatus getStatus()
    {
        return tGetRequestFileStatus.getStatus();
    }

    @Override
    public ArrayOfTExtraInfo getTransferProtocolInfo()
    {
        return tGetRequestFileStatus.getTransferProtocolInfo();
    }

    @Override
    public URI getTransferURL()
    {
        return tGetRequestFileStatus.getTransferURL();
    }

}
