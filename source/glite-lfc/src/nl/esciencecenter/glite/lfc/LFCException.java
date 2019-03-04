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

package nl.esciencecenter.glite.lfc;



/**
 * LFCException.
 * 
 * @author Piter T. de Boer, Spiros Koulouzis  
 */

public class LFCException extends Exception
{
	/** generated id */ 
	private static final long serialVersionUID = 7875496601935511425L;
	
	private int errorCode=0; 
	
	public LFCException(String message)
	{
		super(message);  
	}

	public LFCException(String message, Exception ex) 
	{
		super(message,ex); 
	}
	
	public LFCException(String message, int errornr, Exception ex) 
	{
		super(message+"\n"+LFCError.getMessage(errornr),ex);
		this.errorCode=errornr; 
	}
	
	public LFCException(String message, int errornr) 
	{
		super(message+"\n"+LFCError.getMessage(errornr));
		this.errorCode=errornr; 
	}

	/** Returns LFC Error Code */ 
	public int getErrorCode()
	{
		return this.errorCode;
	}

	public void setErrorCode(int errorCode)
	{
		this.errorCode = errorCode;
	}
	
	/** Returns LFC Error Message */ 
	public String getErrorString()
	{
		return LFCError.getMessage(errorCode); 
	}
	
}