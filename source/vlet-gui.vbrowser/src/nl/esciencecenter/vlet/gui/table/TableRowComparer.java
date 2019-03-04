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

package nl.esciencecenter.vlet.gui.table;

import nl.esciencecenter.ptk.util.Comparer;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vlet.gui.table.VRSTableModel.RowObject;

public class TableRowComparer implements Comparer<RowObject>
{
    public boolean ignoreCase=true;
    private int columnNr;
    private int order=1;
    
    private void init(int columnNr,boolean reverse)
    {
        this.columnNr=columnNr; 
        this.order=(reverse==false)?(1):(-1); 
    }

    public TableRowComparer(int columnNr,boolean reverse)
    {
        init(columnNr,reverse); 
    }
    
    public TableRowComparer(int columnNr)
    {
        init(columnNr,false); 
    }
    
    
    public int compare(RowObject v1, RowObject v2)
    {
    	if (v1==null) 
    		 debug("Received NULL object v1"); 

    	if (v2==null) 
    		debug("Received NULL object v2"); 

        if (v1==null) 
            if (v2==null)
                return 0;
            else
                return order*-1; 
        else
            if (v2==null)
                return order*1;
            else
                ; // continue 
        
        
        Object o1=v1.get(columnNr);
        Object o2=v2.get(columnNr);
        
        debug("comparing:"+o1+" <==> "+o2); 
        
        if (o1==null) 
            if (o2==null)
                return 0;
            else
                return order*-1; 
        else
            if (o2==null)
                return order*1;
            else
                ; // continue 
        
        int result;  
        
        if ((o1 instanceof Attribute) && (o2 instanceof Attribute))
        {
        	if (ignoreCase)
        		result=order*((Attribute)o1).compareToIgnoreCase((Attribute)o2);
        	else
        		result=order*((Attribute)o1).compareTo((Attribute)o2);
        }
        else
        {
        	 if (ignoreCase)
        		 result= order*o1.toString().compareToIgnoreCase(o2.toString());
        	 else
        		 result= order*o1.toString().compareTo(o2.toString());
        }
        
        return result; 
    }

	private void debug(String msg) 
	{
		//Global.errorPrintln(this,msg); 
	}
    
}