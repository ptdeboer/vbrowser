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

package nl.esciencecenter.vlet.grid.voms;

import java.util.Hashtable;
import java.util.Map;

/**
 * ---
 * Gidon Moont
 * Imperial College London
 * Copyright (C) 2006
 * ---
 * This class will be able to translate the following OIDs
 *
 * - Certificate Distinguised Name parts...
 * 
 * Country. 
 *  Attribute name	C
 *  OID	2.5.4.6
 * 
 *  Location. 
 *  Attribute name	L
 * OID	2.5.4.7
 * 
 *  Common name. 
 *  Attribute name	CN
 * OID	2.5.4.3
 * 
 * Organization. 
 * Attribute name	O
 * OID	2.5.4.10
 * 
 * Organizational Unit. 
 * Attribute name	OU
 * OID	2.5.6.5
 * 
 * Email address
 * Attribute name	E
 * OID 1.2.840.113549.1.9.1
 */

public class Translate_OID
{
    private static Map<String,String> oid2string=null; 
    private static Map<String,String> string2oid=null; 
    
    static
    {
        oid2string=new Hashtable<String,String>();
        string2oid=new Hashtable<String,String>();
        
        // Oid -> String 
        
        oid2string.put("2.5.4.3", "CN");
        oid2string.put("2.5.4.6", "C");
        oid2string.put("2.5.4.7", "L");
        oid2string.put("2.5.4.10", "O");
        oid2string.put("2.5.4.11", "OU");
        oid2string.put("1.2.840.113549.1.9.1", "E");
        oid2string.put("1.2.840.113549.1.1.4", "MD5 with RSA encryption");
        
        // String -> OID 
        
        string2oid.put("CN", "2.5.4.3");
        string2oid.put("C", "2.5.4.6");
        string2oid.put("L", "2.5.4.7");
        string2oid.put("O", "2.5.4.10");
        string2oid.put("OU", "2.5.4.11");
        string2oid.put("E", "1.2.840.113549.1.9.1");

    }
    
	public static String getStringFromOID(String oid)
	{
		if (oid2string.containsKey(oid))
		{
			return oid2string.get(oid);
		}
		else
		{
			return new String("" + oid);
		}
	}

	public static String getOIDFromString(String string)
	{
		if (string2oid.containsKey(string))
		{
			return string2oid.get(string);
		}
		else
		{
			return new String("" + string);
		}
	}

}
