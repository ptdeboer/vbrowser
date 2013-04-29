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

package nl.nlesc.vlet.vrs.data;

import java.util.Date;

import nl.nlesc.vlet.vrs.vrl.VRL;

/** 
 * Basic Attribute Types.  
 */
public enum VAttributeType
{
    // ------------------------
	// Support All Native types 
    // ------------------------
    BOOLEAN,
    INT,    // INT32
    LONG,   // INT64
    FLOAT,  // float32
    DOUBLE, // float64
    
    // ------------------
    // Object types 
    // ------------------
    
    // NUMBER *any* number 
    STRING,
    // === Enum === //
    ENUM,
    /** A VRL is stored as plain string. */
    // === Custom/Complex types // 
    VRL, 
    /** TIME has DATE information and is stored as normalized date-time string. */  
    TIME;
    
    //=========================================================================
    //
    //=========================================================================
    
    /**
     * Check Object class and return matched Attribute Type or defaultType
     * if object class is not supported.
     */
    public static VAttributeType getObjectType(Object object,VAttributeType defaultType) 
    {
        if (object==null)
            return defaultType; 
        
        if (object instanceof Boolean)
            return VAttributeType.BOOLEAN;

        if (object instanceof Integer)
            return VAttributeType.INT;

        if (object instanceof Long)
            return VAttributeType.LONG;
        
        if (object instanceof Float)
            return VAttributeType.FLOAT;
        
        if (object instanceof Double)
            return VAttributeType.DOUBLE;
        
        if (object instanceof String)
            return VAttributeType.STRING;
        
        if (object instanceof Date)
            return VAttributeType.TIME;
        
        if (object instanceof VRL)
            return VAttributeType.VRL; 
        
        if (object instanceof Enum)
            return VAttributeType.ENUM;
        
        return defaultType; 
    }
    
}