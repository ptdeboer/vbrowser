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
 * $Id: TableRowComparer.java,v 1.1 2012/11/25 15:30:25 piter Exp $  
 * $Date: 2012/11/25 15:30:25 $
 */ 
// source: 

package nl.esciencecenter.vbrowser.vb2.ui.resourcetable;

import nl.esciencecenter.ptk.util.Comparer;
import nl.esciencecenter.vbrowser.vb2.ui.resourcetable.ResourceTableModel.RowData;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;

public class TableRowComparer implements Comparer<RowData>
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
    
    
    public int compare(RowData v1, RowData v2)
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
        
        RowData r1=(RowData)v1; 
        RowData r2=(RowData)v2;
        
        Attribute o1 = r1.getAttribute(columnNr);
        Attribute o2 = r2.getAttribute(columnNr);
        
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
	    //System.err.println("TableRowComparar:"+msg); 
	}
    
}