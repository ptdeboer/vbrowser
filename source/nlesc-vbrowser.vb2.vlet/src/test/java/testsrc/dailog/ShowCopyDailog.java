package dailog;

import nl.esciencecenter.vbrowser.vb2.vlet.proxy.vrs.CopyDialog;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.VRS;

public class ShowCopyDailog
{
    /**
     * Auto-generated main method to display this JDialog
     */
     public static void main(String[] args) 
     {
         try 
         {
             VRL sourceVrl = new VRL("file:///usr/lib/verylongurl_1234567890_ABCDEFGHIJKLMNOPQRSTUVW_abcdefghijklmnopqrstuvxwz/test.txt");
             VRL destVrl=new VRL("file:///etc/passwd");
             
             AttributeSet set=new AttributeSet();
             set.set("Hostname","localhost");
             set.set("Length",12346); 
             set.set("Modification","1984-Jan-01 12:34:56");  
             
             AttributeSet set2=set.duplicate(); 
             set2.set("Modification","1984-Jan-01 12:34:59");  
             set2.set("Creation","1984-Jan-01 12:34:56");  
             set2.set("Usage","1984-Jan-01 12:34:56");  
             
             CopyDialog dialog=CopyDialog.showCopyDialog(null,
                     sourceVrl,
                     set,
                     destVrl,
                     set2,
                     true);
             
             System.err.println(">>> DONE <<<"); 
             System.err.println("option       ="+dialog.getCopyOption()); 
             System.err.println("skip all     ="+dialog.getSkipAll()); 
             System.err.println("overwrite all="+dialog.getOverwriteAll()); 

         }
         catch (VRLSyntaxException e1) 
         {
             e1.printStackTrace();
         } 
         
         VRS.exit(); 
     }
}
