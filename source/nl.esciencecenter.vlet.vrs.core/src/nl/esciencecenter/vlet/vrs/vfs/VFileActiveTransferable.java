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

package nl.esciencecenter.vlet.vrs.vfs;

import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/**
 * The ActiveTransferable interface contain methods to indicate that a
 * ResourceSystem can perform the transfer itself or example perform a third
 * party file tranSfer.
 * <p>
 * Use this interface to indicate that this ResourceSystem wants to do the
 * transfer itself instead of the default VRS/VFS copy mechanism which is based
 * upon stream read and write method. This allows for optimal (3rd party) transfers between
 * different resources. <br>
 * 
 * @author Piter T. de Boer
 * @since 1.6
 */
public interface VFileActiveTransferable
{
    public static enum ActiveTransferType
    {
        NONE, ACTIVE_3RDPARTY
    };

    /**
     * Checks whether this ResourceSystem can perform an optimized (third party)
     * transfer to the remote location. The StringHolder explanation might hold
     * the reason why it can or can't perform the transfer. 
     * 
     * @param sourceFile
     *            - Actual source file from this FileSystem to transfer. 
     * @param remoteTarget
     *            - Remote destination file to copy to.
     * @param explenation
     *            - Extra information why it can or can't do the transfer.
     * @return Active Transfer Type. either NONE or ACTIVE_3RDPARTY is supported. 
     * @see #canTransferFrom(VRL, StringHolder)
     */
    ActiveTransferType canTransferTo(VFile sourceFile, VRL remoteTargetLocation, StringHolder explanation)
            throws VrsException;

    /**
     * Similar to canTransferTo but with the active and passive parties
     * reversed.
     * 
     * @param targetFile
     *            - Target File on this file system. 
     * @param remoteLocation
     *            - Remote source file to copy from.
     * @param explanation
     *            - Extra information why it can or can't do the transfer.
     * @return Active Transfer Type. Either NONE or ACTIVE_3RDPARTY if supported.
     * @see #canTransferTo(VRL, StringHolder)
     */
    ActiveTransferType canTransferFrom(VFile targetFile, VRL remoteSourceLocation, StringHolder explanation)
            throws VrsException;

    /**
     * Perform Active Transfer. Remote location is new File location.
     * Implementation might choose to create parent directory as well.
     * 
     * @param monitor
     *            -  TaskMonitor: Use startSubTask() and updateSubTaskDone() to
     *            update current transfer statistics as this file can be a
     *            subTask in a larger transfer action (directory copy).
     * @param sourceFile
     *            - Actual source file from this FileSystem to transfer. 
     * @param remoteTargetLocation
     *            - Remote destination file to copy to.
     * @return new created VFile
     */
    VFile activeTransferTo(ITaskMonitor monitor, VFile sourceFile, VRL remoteTargetLocation) throws VrsException;

    /**
     * Perform Active Transfer. Remote location is source File. Implementation
     * might choose to create parent directory as well. If this File doesn't
     * exist yet it will be created by copying the remoteLocation to this File.
     * <p>
     * Since this method might update this VFile, call this method as follows:<br>
     * <code> VFile targetFile=targetFile.activePartyTransferFrom(monitor,vrl); </code>
     * <br>
     * 
     * @param monitor
     *            TaskMonitor: Use startSubTask() and updateSubTaskDone() for
     *            transfer current transfer statistics as this file can be a
     *            subTask in a larger transfer action (directory copy).
     * @param targetFile
     *            - Target file on this FileSystem to copy to. 
     * @param remoteSourceLocation 
     *            - source file to copy from
     * @return new created VFile which should match the VFile implementing this
     *         interface ! Although the implementation might choose to create a
     *         new one.
     */
    VFile activeTransferFrom(ITaskMonitor monitor, VFile targetFile, VRL remoteSourceLocation) throws VrsException;
}
