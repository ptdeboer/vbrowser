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

package nl.esciencecenter.vlet.vrs;

import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;

/**
 * This interface provides some methods for nodes or resources which attributes
 * are 'editable'.   
 * 
 * @author P.T. de Boer
 */
public interface VEditable
{
    /** 
     * returns true if the caller has the permissions to edit this resource. 
     * This means the setAttribute(s) method(s) are allowed. 
     * The default implementation for a VFSNode is to check whether
     * it is writable
     */
    public boolean isEditable() throws VrsException;
    
    /**
     * Sets a list of attributes. Returns true if all attributes could be set.
     */ 
    public boolean setAttributes(Attribute[] attrs) throws VrsException;

    /** Set single attribute. Return true if attribute was set. */  
    public boolean setAttribute(Attribute attr) throws VrsException;
    
}
