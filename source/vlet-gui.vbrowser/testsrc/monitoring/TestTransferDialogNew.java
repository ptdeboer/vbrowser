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

package monitoring;

import javax.swing.JFrame;

import nl.esciencecenter.ptk.ui.panels.monitoring.TransferMonitorPanel;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.vfs.VFSTransfer;

public class TestTransferDialogNew
{

    public static void main(String[] args)
    {
        JFrame frame = new JFrame();
        // dimmy:
        VFSTransfer transfer=new VFSTransfer(null,
                "Transfer", 
                new VRL("file","host","/source"),
                new VRL("file","host","/dest"),
                true);

        TransferMonitorPanel inst = new TransferMonitorPanel(transfer);

        frame.add(inst); 
        frame.pack(); 
        frame.setVisible(true); 


        int max=1000*1024; 
        int dif=50*1024;
        int step=1024; 

        transfer.startTask("TransferTask",max); 

        for (int i=0;i<=max;i+=step)
        {
            if (transfer.isCancelled())
            {
                transfer.logPrintf("\n*** CANCELLED ***\n"); 
                break; 
            }

            String subTaskName="Transfer #"+i/dif;
            
            if ((i%dif)==0)
            {
                transfer.startSubTask(subTaskName, dif); 
                transfer.logPrintf("--- New Transfer ---\n -> nr="+i/dif+"\n");  
            }

            transfer.updateTaskDone(i);
            transfer.updateSourcesDone(i/dif); 
            transfer.updateSubTaskDone(subTaskName,i%dif);

            // Following method can only called by package members!
            // Do update here:
            inst.update(false); 

            try
            {
                Thread.sleep(50);
            }
            catch (InterruptedException e)
            {
                e.printStackTrace();
            } 
        }
        
        inst.update(true); 

        transfer.endTask(null);

    }


}
