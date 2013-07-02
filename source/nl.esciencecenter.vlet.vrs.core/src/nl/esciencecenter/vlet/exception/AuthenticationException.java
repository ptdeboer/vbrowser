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

package nl.esciencecenter.vlet.exception;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

/**
 * @author P.T. de Boer
 * 
 *         Grid Credential Exception(s)
 */
public class AuthenticationException extends VrsException
{
    private static final long serialVersionUID = -7234344441074734646L;

    /**
     * Credential Exceptions
     */
    public class VlPasswordException extends AuthenticationException
    {
        private static final long serialVersionUID = -9218600987549414855L;
    
        public VlPasswordException(String message)
        {
            super(message,null,"Invalid Password.");
        }
    
        /**
         * Create VlException: CrendentialException which keeps original System
         * Exception
         */
        public VlPasswordException(String message, Exception e)
        {
            super(message,e,"Invalid Password.");
        }
    }

    public AuthenticationException(String message)
    {
        super(message,null,ExceptionStrings.INVALID_AUTHENTICATION_EXCEPTION);
    }

    public AuthenticationException(String message, Throwable e)
    {
        super(message,e,ExceptionStrings.INVALID_AUTHENTICATION_EXCEPTION);
    }

    public AuthenticationException(String message, Throwable e,String name)
    {
        super(message,e,name);
    }

  
}
