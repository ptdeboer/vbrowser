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

package test;


import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.VletConfig;
import nl.esciencecenter.vlet.gui.panels.attribute.AttributeEditorForm;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRSContext;


public class testAttributeEditor
{
    public static void main(String args[])
    {
        VletConfig.getRootLogger().setLevelToDebug(); 
        
        int len=20; 
        
        Attribute attrs[]=new Attribute[len];
        
        ServerInfo lfcInfo=null;
        
        try
        {
            lfcInfo=VRSContext.getDefault().getServerInfoFor(new VRL("lfn://lfc.grid.sara.nl:5010/"),true); 
        }
        catch (VrsException e)
        {
            e.printStackTrace();
        }
        
        Attribute lfcAttrs[]=lfcInfo.getAttributes(); 
        
        for (int i=0;i<len;i++)
        {
            if ((i<lfcAttrs.length) && (lfcAttrs[i]!=null))
            {
                attrs[i]=lfcAttrs[i];
            }
            else
            {
                attrs[i]=new Attribute("Field:"+i,"Value"+i);
                attrs[i].setEditable((i%2)==0);
            }
        }
         
        attrs=AttributeEditorForm.editAttributes("Test AttributeForm",attrs,true); 
        
        System.out.println("--- Dialog Ended ---"); 
        
        int i=0;
        
        if ((attrs==null) || (attrs.length<=0)) 
            System.out.println("NO NEW ATTRIBUTES!");
        else
            for(Attribute a:attrs)
            {
                System.out.println(">>> Changed Attrs["+i++ +"]="+a);
            }
        
        
    }
}
