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

package nl.esciencecenter.vlet.vrs.vfs;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

/** 
 * Interface for (file) resource which support checksums.
 * 
 * @author S. Koulouzis
 */
public interface VChecksum
{
    public static final String MD5 = "MD5";
    
    public static final String ADLER32 = "Adler32";
    
    /** Returns the checksum types supported by this (file) resource */ 
    String[] getChecksumTypes() throws VrsException;
    
    /** Returns the actual checksum value for the specified algorithm. */ 
	String getChecksum(String algorithm) throws VrsException;
}
