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

package nl.nlesc.vlet.vfs.lfc;

public class SEInfo
{
    public String hostname=null;
    
    public int optionalPort=-1;
    
    public SEInfo(String infoStr)
    {
        // fail not or else fail later
        if ((infoStr==null) || infoStr.equals("")) 
            throw new NullPointerException("Storage Element info string can not be null or empty"); 
        
        String strs[]=infoStr.split(":");
        if (strs.length>0) 
            hostname=strs[0];
        if (strs.length>1) 
            optionalPort=Integer.parseInt(strs[1]); 
        
    }
    public boolean hasExplicitPort()
    {
        return (optionalPort>0); 
    }
    
    public int getPort()
    {
        return optionalPort; 
    }

    public String getHostname()
    {
        return hostname; 
    }
    
    
}
