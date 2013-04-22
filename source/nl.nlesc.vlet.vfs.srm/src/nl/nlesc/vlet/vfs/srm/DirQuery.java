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

package nl.nlesc.vlet.vfs.srm;

import nl.esciencecenter.ptk.util.StringUtil;
import nl.nlesc.vlet.vrs.vrl.VRL;

public class DirQuery
{
    public static final String PAR_SRMCOUNT="srmCount";
    public static final String PAR_SRMOFFSET="srmOffset"; 
    
    private int srmCount=-1; 
    private int srmOffset=-1;

    public DirQuery(String[] qstrs)
    {   
        parse(qstrs); 
    }

    protected void parse(String[] qstrs)
    {
        // check for srmCount and srmOffset 
        for (String str:qstrs)
        {
            String strs[]=str.split("=");
            if ((strs!=null) && (strs.length>1))
            {
                String name=strs[0]; 
                String value=strs[1]; 
                
                // use error save StringUtil. 
                int intVal=StringUtil.parseInt(value,-1); 
                
                if (name.compareTo(PAR_SRMCOUNT)==0)
                {
                    this.srmCount=intVal; 
                }
                else if (name.compareTo(PAR_SRMOFFSET)==0)
                {
                    this.srmOffset=intVal;
                }
            }
        }
    }
    
    public int getCount()
    {
        return this.srmCount; 
    }
    
    public int getOffset()
    {
        return this.srmOffset; 
    }

    /** Check whether VRL has dir query parameters and return DirQuery object or null */
    public static DirQuery parseDirQuery(VRL vrl)
    {
        String qstrs[]=vrl.getQueryParts(); 
        
        if ((qstrs==null) || ( qstrs.length<0))
            return null; 
        
        boolean hasDirPar=false; 
           
        for (String qstr:qstrs)
        {
            if (qstr==null)
                continue;
            
            String vals[]=qstr.split("=");
                
            String par=vals[0]; 
            if (par==null)
               continue; 
               
            if (par.equals(PAR_SRMCOUNT))
                hasDirPar=true; 
                    
            if (par.equals(PAR_SRMOFFSET))
                hasDirPar=true;
        }
            
        if (hasDirPar==false)
            return null; 
            
        return new DirQuery(qstrs); 
    }
    
    public String toString()
    {
        String qstr="";
        
        if (srmOffset>=0) 
            qstr="srmOffset="+srmOffset+"&"; 
           
        if (srmCount>=0) 
            qstr="srmCount="+srmCount; // append ending '&' ? 
        
        return qstr; 
    }
}
