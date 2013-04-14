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

package nl.esciencecenter.ptk.task;

import nl.esciencecenter.ptk.net.VRI;

/** 
 * Transfer Specific Monitor. 
 * 
 * @author Piter T. de BOer. 
 */
public class TransferMonitor extends MonitorAdaptor
{
    private static int transferCounter=0; 
    
    private int transferId=0; 
    
    private VRI source;

    private VRI dest;

    private String actionStr;

    private int sourcesDone;

    private int totalSources;

    public TransferMonitor(String action, VRI sourceVri, VRI destVri)
    {
        this.transferId=transferCounter++; 
        this.actionStr=action; 
        this.source=sourceVri;
        this.dest=destVri; 
    }

    public String getID()
    {
        return ""+transferId; 
    }
    
    public VRI getDestination()
    {
        return dest;
    }

    public VRI getSource()
    {
        return source;
    }

    public int getTotalSources()
    {
        return totalSources;
    }
    
    public void setTotalSources(int sources)
    {
        this.totalSources=sources;
    }

    public int getSourcesDone()
    {
        return sourcesDone; 
    }

    public void updateSourcesDone(int done)
    {
       this.sourcesDone=done; 
    }

    public String getActionType()
    {
        return actionStr;
    }
    
    
}
