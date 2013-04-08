/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: TableRowComparer.java,v 1.2 2013/01/23 00:33:10 piter Exp $  
 * $Date: 2013/01/23 00:33:10 $
 */ 
// source: 

package nl.uva.vlet.gui.table;

import nl.nlesc.ptk.util.Comparer;
import nl.uva.vlet.data.VAttribute;
import nl.uva.vlet.gui.table.VRSTableModel.RowObject;

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
        
        if ((o1 instanceof VAttribute) && (o2 instanceof VAttribute))
        {
        	if (ignoreCase)
        		result=order*((VAttribute)o1).compareToIgnoreCase((VAttribute)o2);
        	else
        		result=order*((VAttribute)o1).compareTo((VAttribute)o2);
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