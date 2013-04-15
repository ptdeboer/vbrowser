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

package nl.nlesc.vlet.vrs.vdriver.infors.net.testers;

import nl.nlesc.vlet.vrs.vdriver.infors.net.ProtocolTester;

/**
 * GFTP tester. 
 */
public class GFTPTester extends ProtocolTester
{
    protected GFTPTester(String name, boolean isSSL)
    {
        super(name,isSSL); 
    }

    public GFTPTester()
    {
        super("GFTPTester",false); // initial socket is NOT SSL! 
    }

    public String getScheme()
    {
        return "gftp"; 
    }
    
    
    protected byte[] getReponseChallenge()
    {
        return "HELO GFTP\n".getBytes();  
    }
    
    // Check HTTP/HTML response: any is ok. 
    protected boolean checkResponse(byte[] bytes)
    {
        try
        {
            String string=new String(bytes); 
        
            if ( (string.contains("GridFTP")) || (string.contains("gridftp")) )
                return true;
            
            // response to "HELO" command :-) 
            if (string.contains("Must perform GSSAPI"))
                return true; 
            if (string.contains("gridftp"))
                return true;
        
            return false;
        }
        catch (Throwable t)
        {
            setException(t); 
            return false; 
        }
    }
 
    
}
