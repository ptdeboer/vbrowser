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

package nl.nlesc.vlet.exception;

import java.io.IOException;

/**
 * Super class of all VL Exceptions.
 * <p>
 * The Class VlException provides more high-level information about the
 * Exception which occurred and hides the original System Exception.
 * <p>
 * This reduces the stack trace for example and provides the end-user (or
 * application programmer) a better descriptive error message then the low level
 * System Exception (which might be unknown to the application programmer).
 * <p>
 * <br>
 * Example of Usage:<br>
 * <li><code> thrown new VlException("Unknown Exception.");</code>
 * <li><code> thrown new VlException(ReadAccesDeniedException);</code>
 * <li>
 * <code> thrown new VlException(ReadAccesDeniedException,"File has wrong group permissions.");</code>
 * <li>
 * <code> thrown new VlException(ReadAccesDeniedException,"Message txt",e);</code>
 * <p>
 * 
 */
public class VlException extends Exception
{
    private static final long serialVersionUID = 1338724960830976888L;

    // ===============
    // Factory
    // ===============

    public static VlException newChainedException(Throwable exception)
    {
        return new VlException(exception.getClass().getSimpleName(), exception.getMessage(), exception);
    }

    public static VlException create(String exceptionName, String message, Throwable cause)
    {
        return new VlException(exceptionName, message, cause);
    }

    /**
     * Factory method to create a chained IOException. In java 1.5 there is no
     * constructor to do that !
     */
    public static IOException createIOException(String message, Throwable e)
    {
        IOException ex = new IOException(message);
        ex.initCause(e);
        return ex;
    }

    // ===============
    // Instance
    // ===============

    /** Human Readable Name of the Exception */
    protected String name = "General VlException"; // Who is this general ?

    // *** protected Instance Methods ***

    /** Default contructor. For subclasses only. */
    protected VlException()
    {
        super();
    };

    /** Default contructor which holds system exception. For subclasses only. */
    public VlException(Throwable cause)
    {
        super(cause);
    };

    protected void setName(String newName)
    {
        this.name = newName;
    }

    // ===
    // Public Constructors ***
    // ===

    /** Most basic implementation of the VlException. */
    public VlException(String message)
    {
        super(message);
        this.name = "VlException";
    };

    /** Public constructor which holds original system exception. */
    public VlException(String message, Throwable cause)
    {
        super(message, cause);
        this.name = "VlException";
    };

    // ===
    // Protected Constructors
    // ===

    /** Named Exception */
    protected VlException(String name, String message)
    {
        super(message);
        this.name = name;
    };

    /** Named Exception */
    protected VlException(String name, String message, Throwable cause)
    {
        super(message, cause);
        this.name = name;
    };

    public String toString()
    {
        String message_txt = "";

        Throwable parent = null;
        Throwable current = this;
        int index = 0;

        do
        {
            if (index == 0)
            {
                message_txt = name + ":" + getMessage();
            }
            else
            {
                message_txt += "\n--- Nested Exception Caused By [" + index + "] ---\n";
                message_txt += current.getClass().getName() + ":" + current.getMessage();
            }

            // get next in exception chain:
            parent = current;
            current = current.getCause();
            index++;

        } while ((current != null) && (current != parent) && (index < 100));

        return (message_txt);
    }

    public String toStringPlusStacktrace()
    {
        return this.toString() + "\n ---Stack Trace --- \n" + getChainedStackTraceText(this);
    }
    
    /**
     * Returns Name of VlException. If not set, this the name of the Exception
     * subclass.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the stacktrace, including nested Exceptions as single String
     */
    public static String getChainedStackTraceText(Throwable e)
    {
        String text = "";

        Throwable parent = null;
        Throwable current = e;
        int index = 0;

        // === get whole exception chain:

        do
        {
            if (index > 0)
                text += "--- Nested Exception Caused By: ---\n";

            text += "Exception[" + index + "]:" + current.getClass().getName() + "\n";
            text += "message=" + current.getMessage() + "\n";

            StackTraceElement[] els = current.getStackTrace();

            if (els != null)
                for (int i = 0; i < els.length; i++)
                    text += "[" + i + "]" + els[i] + "\n";

            // get next in exception chain:
            parent = current;
            current = current.getCause();
            index++;
        } while ((current != null) && (current != parent) && (index < 100));

        return text;
    }

}
