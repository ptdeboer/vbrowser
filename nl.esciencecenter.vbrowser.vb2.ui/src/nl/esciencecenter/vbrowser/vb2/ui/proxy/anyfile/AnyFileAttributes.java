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

package nl.esciencecenter.vbrowser.vb2.ui.proxy.anyfile;

import nl.esciencecenter.ptk.io.FSNode;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.vbrowser.vb2.ui.presentation.UIPresentable;
import nl.esciencecenter.vbrowser.vb2.ui.presentation.UIPresentation;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.IAttributes;

/**
 * combined Attribute+Presentation interface for AnyFile;   
 */
public class AnyFileAttributes implements IAttributes,UIPresentable
{
    public static enum FileAttribute
    {
        ICON,NAME,RESOURCE_TYPE,PATH,BASENAME,DIRNAME,EXTENSION,LENGTH,MIMETYPE,
        MODIFICATION_TIME,MODIFICATION_TIME_STRING,ACCESS_TIME,CREATION_TIME,PERMISSIONS,PERMISSIONS_STRING; 
        // ---
        
        private String name; 
        
        private FileAttribute()
        {
            String enumStr=this.toString(); 
            // use enum as Name
            this.name=enumStr.substring(0,1).toUpperCase()+enumStr.substring(1,enumStr.length()).toLowerCase(); 
        }
        
        public String getName()
        {
            return name;  
        }
        
        // === static=== 
        private static FileAttribute defaultFileAttributes[]=new FileAttribute[]
                    {   ICON,NAME,LENGTH,MIMETYPE,MODIFICATION_TIME_STRING,PERMISSIONS_STRING }; 

        public static String[] getStringValues()
        {
            FileAttribute[] values = defaultFileAttributes; 
            String strValues[]=new String[values.length];
            for (int i=0;i<values.length;i++)
                strValues[i]=values[i].toString();
            return strValues; 
        }
    }
    private static UIPresentation defaultPresentation;

    static
    {
        initStatic(); 
    }
    
    private static void initStatic()
    {
        defaultPresentation=UIPresentation.createDefault(); 
        
        defaultPresentation.setChildAttributeNames(FileAttribute.getStringValues()); 
//        Presentation.storeSchemeType(FSNode.FILE_SCHEME,ResourceType.FILE.toString(),defaultPresentation);
//        Presentation.storeSchemeType(FSNode.FILE_SCHEME,ResourceType.DIRECTORY.toString(),defaultPresentation);
    }
    
    // ========================================================================
    
    // ========================================================================
    
    private FSNode anyFile;

    public AnyFileAttributes(FSNode anyFile)
    {
        this.anyFile=anyFile; 
    }
    
    @Override
    public String[] getAttributeNames()
    {
        return FileAttribute.getStringValues(); 
    }

    @Override
    public Attribute getAttribute(String name)
    {
        if (name==null)
            return null; 
        if (name.equals(""))
            return null; 

        if (name.equals(""+FileAttribute.RESOURCE_TYPE))
            return new Attribute(name,anyFile.isFile()?"File":"Dir"); 

        if (name.equals(""+FileAttribute.NAME))
            return new Attribute(name,anyFile.getBasename());

        if (name.equals(""+FileAttribute.BASENAME))
            return new Attribute(name,anyFile.getBasename());

        if (name.equals(""+FileAttribute.DIRNAME))
            return new Attribute(name,anyFile.getDirname());

        if (name.equals(""+FileAttribute.MODIFICATION_TIME))
            return new Attribute(name,anyFile.getModificationTime());
        
        if (name.equals(""+FileAttribute.MODIFICATION_TIME_STRING))
            return new Attribute(name,Presentation.createNormalizedDateTimeString(anyFile.getModificationTime()));

        if (name.equals(""+FileAttribute.LENGTH))
            return new Attribute(name,anyFile.length());
        
        if (name.equals(""+FileAttribute.PATH))
            return new Attribute(name,anyFile.getPath());  
        
        return null; 
    }

    @Override
    public Attribute[] getAttributes(String[] names)
    {
        Attribute attrs[]=new Attribute[names.length]; 
        for (int i=0;i<names.length;i++)
            attrs[i]=getAttribute(names[i]); 
        return attrs; 
    }

    @Override
    public UIPresentation getPresentation()
    {
      return defaultPresentation; 
    }

}
