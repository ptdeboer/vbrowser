/*
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
package nl.esciencecenter.ptk.web;

import java.io.IOException;

public class WebException extends IOException 
{
    private static final long serialVersionUID = 6695170599026563455L;
    
    public static enum Reason
    {
        // Exception Reasons; 
        UNAUTHORIZED("Unauthorized."),
        RESOURCE_NOT_FOUND("Resource not found."),
        RESOURCE_ALREADY_EXISTS("Resource already exists."),
        FORBIDDEN("Forbidden."),
        UNKNOWN_HOST("Hostname or server not found."),
        NO_ROUTE_TO_HOST_EXCEPTION("No route to host."),
        CONNECTION_TIME_OUT("Connection timeout."),
        HTTP_ERROR("HTTP Error."), 
        URI_EXCEPTION("URI Syntax Exception."), 
        CONNECTION_EXCEPTION("Connection Exception."),
        HTTP_CLIENTEXCEPTION("HTTP Client Exception."), 
        HTTPS_SSLEXCEPTION("HTTPS SSL Exception."),
        IOEXCEPTION("IOException."), 
        INVALID_REQUEST("Invalid Request"), 
        INVALID_RESPONSE("Invalid Response")
        ;
        
        // === Instance === 
        
        private String text=null;
        
        private Reason(String text)
        {
            this.text=text; 
        }
        
        public String getText()
        {
            return this.text; 
        }
    }
    
    // === Instance ===
    
    private Reason reason=null; 
    
    private int httpCode=0; 

    public WebException()
    {
        super(); 
    }
    
    public WebException(String message)
    {
        super(message); 
    }
    
    public WebException(Throwable cause)
    {
        super(cause); 
    }
    
    public WebException(String message,Throwable cause)
    {
        super(message,cause); 
    }
    
    public WebException(Reason reason,String message)
    {
        super(message); 
    }
    
    public WebException(Reason reason,Throwable cause)
    {
        super(cause);
        this.reason=reason; 
    }
    
    public WebException(Reason reason,String message,Throwable cause)
    {
        super(message,cause); 
        this.reason=reason; 
    }
    
    public WebException(Reason reason, int httpCode, String message)
    {
        super(message); 
        this.reason=reason;
        this.httpCode=httpCode; 
    }
    
    public WebException(Reason reason, int httpCode, String message,Throwable cause)
    {
        super(message,cause); 
        this.reason=reason;
        this.httpCode=httpCode; 
    }

    public Reason getReason()
    {
        return this.reason; 
    }
    
    /**
     * Returns HTTP Error/Result Code. 
     * Returns 0 if unknown or no HTTP Status Code.  
     */ 
    public int getHttpStatus()
    {
        return this.httpCode;
    }
    
    /**
     * Returns status text of HTTP Status Code. 
     */ 
    public String getHttpCodeString()
    {
        return null; // return HttpStatus.getStatusText(httpCode); 
    }
    
}
