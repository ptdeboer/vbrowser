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

package nl.nlesc.vlet.grid.globus;

public class VOMSInfo
{
    public String vo;
    public String group; 
    public String role; 
    public String capability;
    
    protected VOMSInfo()
    {
        
    }
    
    public VOMSInfo(String str)
    {
        parse(str); 
    }
    
    public static VOMSInfo parse(String str)
    {
        if (str==null)
            return null; 
        VOMSInfo info=new VOMSInfo(); 
        info._parse(str); 
        return info; 
    }
    
    private void _parse(String str)
    {
        if (str==null)
            throw new NullPointerException("VOMS String can't be null"); 
        
        int index=0; 
        String strs[]=str.split("/");
        
        if (str.startsWith("/"))
            index=1; 
        
        // <vo> 
        if (index<strs.length) 
            vo=strs[index];
        else 
            return; 
          
        index++; 
        
        // <vo>/<group> 
        if (index<strs.length)
        {
            if (strs[index].contains("=")==false)
            {
                group=strs[index];
                index++; 
            }
        }
        
        // [/Role=...]  and [/Capability=..]  
        for (int i=index;i<strs.length;i++)
        {
            String attr[]=strs[i].split("=");
            
            if (attr.length>=2)
            {
                if ("Role".equalsIgnoreCase(attr[0])) 
                    this.role=attr[1]; 
                else if ("Capability".equalsIgnoreCase(attr[0]))
                    this.capability=attr[1]; 
            }
        }
        
        // filter out "NULL" strings. 
        if ("NULL".equalsIgnoreCase(role))
            role=null; 
        
        if ("NULL".equalsIgnoreCase(group))
            group=null; 
    }
}
