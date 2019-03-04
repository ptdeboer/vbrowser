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

package nl.esciencecenter.vlet.vrs.ui;

import nl.esciencecenter.ptk.data.StringHolder;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/**
 * Interface to ask what to do when copying/transferring files
 * or other resources. 
 * Convenience interface used for call-backs to the VBrowser or another UI. 
 * 
 * @author Piter T. de Boer
 */
public interface ICopyInteractor
{
    /**
     * Possible actions the user can choose from. Continue means overwrite and RENAME
     * requires that optNewName is filled with a name.
     */
    public enum InteractiveAction 
    {
       CANCEL,
       SKIP,
       CONTINUE,
       RENAME
    }
    
	/** 
	 * Interface Adaptor which always returns 'continue' (with the copy action). 
	 */ 
	public static class AlwaysOverwrite implements ICopyInteractor
	{
		public InteractiveAction askTargetExists(String message,
				VRL source,
				Attribute sourceAttrs[], 
				VRL target, 
				Attribute targetAttrs[],
				StringHolder optNewName) 
		{
			return InteractiveAction.CONTINUE;
		}
	}
	
    
    /**
     * Ask what to do when a Resource Target already exists. 
     * An optional new name may be specified if a copy must be created.
     * The option Continue would mean Overwrite.  
     * 
     * @param message Human readable message informing the target already exists 
     * @param source Optional Source VRL 
     * @param target Optional Target VRL 
     * @param optNewName Optional New Name which could be specified.     
     * @return on of the action possible form InteractiveAction.   
     */
    public InteractiveAction askTargetExists(String message,
    		VRL source,
			Attribute sourceAttrs[], 
			VRL target, 
			Attribute targetAttrs[],
            StringHolder optNewName); 
    
}
