package nl.esciencecenter.vbrowser.vrs.node;

import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;

import nl.esciencecenter.ptk.presentation.Presentation;

/** 
 * Dummy File Attributes. 
 */
public abstract class FileAttributes implements BasicFileAttributes
{
    
    public boolean isSymbolicLink()
    {
        return false; 
    }
//    
//    public boolean isReadable()
//    {
//        return false;
//    }
//
//    public boolean isWritable()
//    {
//        return false;
//    }

    public boolean isHidden()
    {
        return false; 
    }

    public java.util.Date getModificationTimeDate()
    {
        FileTime time = this.lastModifiedTime();
        if (time==null)
        {
            return null;
        }
        return Presentation.createDate(time.toMillis());
    }

    public java.util.Date getCreationTimeDate()
    {
        FileTime time = this.creationTime();
        if (time==null)
        {
            return null;
        }
        return Presentation.createDate(time.toMillis());
    }

    public java.util.Date getLastAccessTimeDate()
    {
        FileTime time = this.lastAccessTime();
        if (time==null)
        {
            return null;
        }
        return Presentation.createDate(time.toMillis());
    }

//    public String getPermissionsString()
//    {
//        String str;
//        
//        if (isSymbolicLink())
//        {
//            str="l"; 
//        }
//        else
//        {
//            str = (isDirectory()?"d":"-");
//        }
//        
//        str+=isReadable()?"r":"-"; 
//        str+=isWritable()?"w":"-"; 
//        str+="[";
//        str+=isHidden()?"H":""; 
//        str+="]";
//        return str;  
//    }

    @Override
    public boolean isOther()
    {
        return (!isRegularFile() || !isDirectory() || !isSymbolicLink());
    }


}
