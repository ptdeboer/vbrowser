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

package nl.esciencecenter.vlet.vrs.vdriver.infors.net.testers;

import nl.esciencecenter.vlet.vrs.vdriver.infors.net.ProtocolTester;

/**
 * HTTP tester. 
 */
public class HTTPTester extends ProtocolTester
{
    protected HTTPTester(String name, boolean isSSL)
    {
        super(name,isSSL); 
    }

    public HTTPTester()
    {
        super("HTTPTester",false); 
    }

    public String getScheme()
    {
        return "http"; 
    }
    
    
    protected byte[] getReponseChallenge()
    {
        return "GET / HTTP/1.0\n\n\n".getBytes();  
    }
    
    // Check HTTP/HTML response: any is ok. 
    protected boolean checkResponse(byte[] bytes)
    {
        try
        {
            String string=new String(bytes); 
        
            if (string.contains("html") || string.contains("HTML"))
                return true;
            
            if (string.contains("text/html")) 
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
