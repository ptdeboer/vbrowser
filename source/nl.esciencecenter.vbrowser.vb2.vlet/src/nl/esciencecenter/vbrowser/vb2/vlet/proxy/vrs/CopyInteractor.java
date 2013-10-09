package nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import nl.esciencecenter.ptk.data.BooleanHolder;
import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.ptk.ui.panels.monitoring.TransferMonitorDialog;
import nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs.CopyDialog.CopyOption;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.ui.ICopyInteractor;

public class CopyInteractor implements ICopyInteractor
{
    BooleanHolder overWriteAll=null;
    BooleanHolder skipAll=null; 
    
    private TransferMonitorDialog transferDialog=null;
    private boolean suspended;  
    
    public CopyInteractor()
    {
        overWriteAll=new BooleanHolder(false); 
        skipAll=new BooleanHolder(false); 
    }
    
    @Override
    public InteractiveAction askTargetExists(String message,
            VRL source,
            Attribute sourceAttrs[], 
            VRL target, 
            Attribute targetAttrs[],
            StringHolder optNewName) 
    {
        // already checked "Skip All"  or "Overwrite All"! 
        InteractiveAction action = checkSkipOrOverWriteAll();
        
        if (action!=null)
            return action; 
        
        suspendTransfer(); 
        
        InteractiveAction result=doAsk(message,
                source,
                sourceAttrs,
                target,
                targetAttrs,
                optNewName);
        
        resumeTransfer();
        
        return result; 
    }
    
    private synchronized void suspendTransfer()
    {
        this.suspended=true;
        
        if (transferDialog!=null)
            transferDialog.stop();
    }
    
    private synchronized void resumeTransfer()
    {
        this.suspended=false; 
        
        if (transferDialog!=null)
            transferDialog.start();
    }
    
    public synchronized void setDialog(TransferMonitorDialog dialog)
    {
        // Set Dialog might occur after the Copy Interactor already popped up a dailog.  
        // 
        if (suspended)
            if  ((transferDialog==null) && (dialog!=null))
                dialog.stop();
        
        this.transferDialog=dialog; 
    }
    
    // already asked ? 
    protected InteractiveAction checkSkipOrOverWriteAll()
    {
        // overwrite all! 
        if ((overWriteAll!=null) && (overWriteAll.value==true))
        {
            // update for this copy action 
            return InteractiveAction.CONTINUE;
        }
        
        // skip all! 
        if ((skipAll!=null) && (skipAll.value==true))
        {
            // update for this copy action 
            return InteractiveAction.SKIP; 
        }
        
        return null;
    }
    
    private InteractiveAction doAsk(String message, 
            VRL source, 
            Attribute[] sourceAttrs, 
            VRL target,
            Attribute[] targetAttrs, 
            StringHolder optNewName)
    {
        
        InteractiveAction action=checkSkipOrOverWriteAll();
        if (action!=null)
            return action; 
        
        // link to VBrowser frame!
        JFrame frame = null; // BrowserInteractiveActions.this.browserController.getFrame(); 
        
        CopyDialog dailog = CopyDialog.showCopyDialog(frame,
                source,
                new AttributeSet(sourceAttrs),
                target,
                new AttributeSet(targetAttrs),
                true); 
    
        
        CopyOption option = dailog.getCopyOption();
        
        if (option==null || option==CopyOption.Cancel)
        {
            return InteractiveAction.CANCEL; 
        }
        
        if (option==CopyOption.Skip)
        {
            if (dailog.getSkipAll())
            {
                skipAll.value=true;
            }
            return InteractiveAction.SKIP;
        }
        
        if (option==CopyOption.Overwrite)
        {
            if (dailog.getOverwriteAll())               
            {
                overWriteAll.value=true;
            }
            return InteractiveAction.CONTINUE; 
        }
        
        if (option==CopyOption.Rename)
        {
             optNewName.value="Copy Of "+target.getBasename(); 
             
             optNewName.value= JOptionPane.showInputDialog("Specify new name", optNewName.value);
             
             if (optNewName.value==null)
                 return InteractiveAction.CANCEL; 
             else
                 return InteractiveAction.RENAME; 
         }
        
        // Default: 
        return InteractiveAction.CANCEL; 
    }
}   

