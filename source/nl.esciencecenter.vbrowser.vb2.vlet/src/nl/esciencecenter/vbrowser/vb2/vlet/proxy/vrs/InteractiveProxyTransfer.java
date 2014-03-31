package nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs;

import java.util.List;

import javax.swing.JFrame;

import nl.esciencecenter.ptk.task.ActionTask;
import nl.esciencecenter.ptk.task.ITaskMonitor;
import nl.esciencecenter.ptk.task.ITaskSource;
import nl.esciencecenter.ptk.ui.panels.monitoring.TaskMonitorDialog;
import nl.esciencecenter.ptk.ui.panels.monitoring.TransferMonitorDialog;
import nl.esciencecenter.ptk.vbrowser.ui.proxy.ProxyNode;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VComposite;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.events.ResourceEvent;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSTransfer;
import nl.esciencecenter.vlet.vrs.vfs.VRSTransferManager;

public class InteractiveProxyTransfer
{

    public void doCopyMoveDrop(final VRSProxyNode target, 
            final List<VRL> sources,
            final boolean isMove)
    {
        // ====
        // Pre: Check CopyDrop type: 
        // ==== 
        boolean vfsDrop=true; 
        
        //VDir supports almost *any* type now . 
        if (target.getResourceType().equals(VFS.DIR_TYPE)) 
        {   
            vfsDrop=true; 
        }
        else
        {
            vfsDrop=false; 
        }
        
        //
        // Optimized VFS resource only drop, use VFSTransfer Dialogs!
        //
        if (vfsDrop)
        {
            doVFSCopyDrop(target,sources,isMove); 
            return; 
        }
        else
        {
            doAnyCopyDrop(target,sources,isMove); 
        }
    }
    
    private void doAnyCopyDrop(final VRSProxyNode targetPNode, 
            final List<VRL> sources,
            final boolean isMove)
    {
        
        final ITaskSource browserController=null;
        
        // =======================
        // Any Drop: 
        // =======================
        
        ActionTask task = new ActionTask(browserController, "CopyMoveDrop #"+sources.size()+" sources to:"+targetPNode.getVRL() )
        {
            public void doTask() 
            {
                // once started: disconnect from browserController : 
                //this.setTaskSource(DummyTransferWatcher.getBackgroundWatcher());
                ProxyNode resolvedTarget=targetPNode;
                
                ITaskMonitor monitor = this.getTaskMonitor(); 
                // Default Copy Move Drop!
                
                try
                {
                    // ===========
                    // Resolve Target: Drop on target not link itself 
                    // ===========
                    
                    if (targetPNode.isResourceLink())
                    {
                        resolvedTarget=targetPNode.resolveResourceLink();
                        if (resolvedTarget==null)
                        {
                            resolvedTarget=targetPNode; 
                        }
                    }

                    // =======
                    // OPEN !
                    // =======
                    
                    // Parent = Composite Node  
                    VNode destNode=getVRSContext().openLocation(resolvedTarget.getVRL()); 
                    
                    if (destNode instanceof VComposite)
                    {
                        VComposite destCNode;
                        destCNode=(VComposite)destNode;
                        for (VRL ref:sources)
                        {
                            VNode sourceNode=getVRSContext().openLocation(ref);  
                            
                            String actionStr=(isMove)?"Move":"Copy";
                            monitor.logPrintf("Performing "+actionStr+" of '"+sourceNode.getName()+"' to: "+destNode.getHostname()+"\n");
                            
                            // Synchronous copy/move (but in background) !
                            VNode resultNode=destCNode.addNode(sourceNode,null,isMove);
                            // 
                            
                            getVRSContext().fireEvent(ResourceEvent.createChildAddedEvent(destNode.getVRL(),resultNode.getVRL()));
                            
                            if (isMove)
                            {
                                getVRSContext().fireEvent(ResourceEvent.createDeletedEvent(sourceNode.getVRL())); 
                            }
                        }
                    }
                    else
                    {
                        throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Cannot perform drop on: target destination:"+destNode);
                    }
                }
                catch (Throwable t)
                {
                    handle(t);
                }
            }
            
            @Override
            public void stopTask()
            {
                
            }
        };
       
       // Show default task monitor: 
       task.startTask();
       TaskMonitorDialog.showTaskMonitorDialog(getFrame(), task,0);
    
    }

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
                        resolvedTarget = targetPNode.resolveResourceLink();
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

                    TransferMonitorDialog dialog = TransferMonitorDialog.showTransferDialog(browserController, vfsTransfer, 0);
                    copi.setDialog(dialog);

                    // Wait In Background!!!
                    vfsTransfer.waitForCompletion();
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
        t.printStackTrace(); 
    }

    protected VRSContext getVRSContext()
    {
       return VRSProxyFactory.getProxyVRSContext();
    }
 
    private JFrame getFrame()
    {
        return null;
    }
}
