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

package nl.nlesc.vlet.gui.panels.acldialog;

import nl.esciencecenter.ptk.data.StringList;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.data.VAttributeSet;
import nl.nlesc.vlet.gui.panels.resourcetable.ResourceTableModel;

public class ACLDataModel extends ResourceTableModel
{
    private static final long serialVersionUID = 3364344241776757918L;

    public ACLDataModel()
    {
        init(); 
    }
    
    public void init()
    {
        StringList headers=new StringList(); 
        
        headers.add("aclEntity"); 
        headers.add("aclValue"); 
        
        this.setHeaders(headers);
        this.getHeaderModel().setEditable(false); 
        
        // clear dummy data
        this.clearData(); 
    }
    
    public void setACL(VAttribute[][] attrs)
    {
        this.clearData(); // keep headers ? 
        
        if ((attrs==null) || (attrs[0]==null)) 
        {
            return; 
        }
        
        int nrrows=attrs.length;
        int nrcols=attrs[0].length; 
        
        StringList headers=new StringList(nrcols); 
        
        for (int i=0;i<nrcols;i++)
        {
            headers.add(attrs[0][i].getName()); 
        }
        
        // set headers: 
        int index=0; 
        this.setHeaders(headers);
        this.setAllHeaders(headers);
        // set data: 
        for (VAttribute row[]:attrs)
        {
            String key=""+index++; 
            VAttributeSet attrSet=new VAttributeSet(row); 
            this.addRow(key, attrSet);
        }
    }

    public void addACLRecord(VAttribute[] record)
    {
        String key=""+this.getRowCount();  
        VAttributeSet attrSet=new VAttributeSet(record); 
        this.addRow(key, attrSet);
    }
    
    public VAttribute[][] getACL()
    {// should have same format: 
        return this.getAttributeData(); 
    }

   
}
