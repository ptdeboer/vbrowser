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
 * Interface for VFile implementations which support the Unix file "mode" attributes. 
 * For example: "rwxrwxrwx".   
 * <p>
 * The local filesystem implementation supports this interface. 
 * When a unix style filesystem is detected, this VUnixFileMode methods return 
 * usuable results. 
 * The SSH FileSystem support unix file attributes as well. 
 * Some implementation might support the high 'sticky bit' and set UID/set GUID 
 * mode as well. 
 * <p>
 * 
 * @author Piter T. de Boer
 */
public interface VUnixFileMode
{
	/** 
	 * Get Unix File Mode. 
	 * <p>
	 * Note:  Argument 'mode' is the integer representation 
	 * of the octal file mode. Thus octal number '0777' becomes integer '511' 
	 * and vice versa. 
	 * Use Integer.toOctal() to get the octal (string) representation. 
	 */
	int getMode() throws VrsException; 
	
	/**
	 * Set Unix File Mode.
	 * <p>
	 * Note:  Argument 'mode' is the integer representation 
	 * of the octal file mode. Thus octal number '0777' becomes integer '511' 
	 * and vice versa. 
	 * Use Integer.toOctal() to get the octal (string) representation. 
	 */
	void setMode(int mode)throws VrsException; 
	
}
