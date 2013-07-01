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

package nl.nlesc.vlet.vrs.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Generic interface for shell channels which have a pty (terminal) associated with it.  
 * Whether features are supported depends on the implementing shell channel. 
 */
public interface VShellChannel
{
    /** Get stdin OutputStream (to write to remote shell) after channel has connected. */ 
    public OutputStream getStdin();

    /** Get stdout InputStream (to read from remote shell) after channel has connected. */ 
    public InputStream getStdout();
    
    /** Get Optional stderr InputStream if supported. Stderr might be mixed with stdout.*/ 
    public InputStream getStderr(); 

    public void connect() throws IOException; 
    
    public void disconnect() throws IOException; 

    // === tty/shell Options === 
    public String getTermType() throws IOException; 
    
    public boolean setTermType(String type) throws IOException; 
    
    public boolean setTermSize(int col, int row, int wp, int hp) throws IOException; 
    
    /**
     * Returns array of int[2] {col,row} or int[4] {col,row,wp,hp} of remote terminal (pty) size.
     * Return NULL if size couldn't be determined (terminal sizes not supported)  
     */ 
    public int[] getTermSize() throws IOException; 

    // === Life Cycle management === 
    
    public void waitFor() throws InterruptedException;

    public int exitValue();
    
 }
