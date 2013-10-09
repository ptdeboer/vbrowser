package nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs;

import java.util.List;

import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.task.ITaskSource;
import nl.esciencecenter.ptk.ui.panels.monitoring.TransferMonitorDialog;
import nl.esciencecenter.vbrowser.vb2.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vfs.VFSTransfer;
import nl.esciencecenter.vlet.vrs.vfs.VRSTransferManager;

public class InteractiveProxyTransfer
{

   
    protected void doVFSCopyDrop(final VRSProxyNode targetPNode, final List<VRL> sources, final boolean isMove)
    {
        final ITaskSource browserController=null;
        
        ActionTask task = new ActionTask(browserController, "CopyMoveDrop #" + sources.size() + " sources to:"
                + targetPNode.getVRL())
        {
            private VFSTransfer vfsTransfer = null;

            public void doTask()
            {
                try
                {
                    ProxyNode resolvedTarget = targetPNode;

                    // drop on target not link itself
                    if (targetPNode.isResourceLink())
                    {
                        resolvedTarget = targetPNode.getTargetPNode();
                    }

                    // ====
                    // Once started: disconnect from browserController, this
                    // allow this
                    // thread to wait in background and monitor the transfer.
                    // ====
                    //this.setTaskSource(DummyTransferWatcher.getBackgroundWatcher());
                    ITaskMonitor monitor = this.getTaskMonitor();

                    // PARENT of dropped sources
                    VRL targetDirVrl = resolvedTarget.getVRL();
                    VRL vrls[] = sources.toArray(new VRL[0]); 

                    VRSTransferManager transferMngr = getVRSContext().getTransferManager();
                    // Delegate to TransferManager:
                    CopyInteractor copi = new CopyInteractor();
                    if (vrls.length > 1)
                    {
                        vfsTransfer = transferMngr.asyncMultiCopyMove(monitor, vrls, targetDirVrl, isMove, copi);
                    }
                    else
                    {
                        // Single Drop!
                        vfsTransfer = transferMngr.asyncCopyMove(monitor, vrls[0], targetDirVrl, null, isMove, copi);
                    }
                    // nl.uva.vlet.gui.panels.monitordialog.TaskMonitorDialog.showTaskMonitorDialog(browserController,transfer);

                    TransferMonitorDialog dialog = TransferMonitorDialog.showTransferDialog(browserController,
                            vfsTransfer, 0);
                    copi.setDialog(dialog);

                    //
                    // Wait In Background!!!
                    //
                    vfsTransfer.waitForCompletion();
                    //
                    //
                    //

                }
                catch (Throwable t)
                {
                    handle(t);
                }
            }

            @Override
            public void stopTask()
            {
                if (vfsTransfer != null)
                {
                    vfsTransfer.setIsCancelled();
                }
            }
        };

        task.startTask();

    }

    protected void handle(Throwable t)
    {
    }

    protected VRSContext getVRSContext()
    {
       return VRSProxyFactory.getProxyVRSContext();
    }
    
}
