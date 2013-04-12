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

package nl.nlesc.vlet.dialog;

import javax.swing.JFrame;

/**
 * Simple Super Class for vrs.core dialogs. 
 * Note that vrs.core dialogs have nothing to do
 * with gui.utils and the vbrowser. 
 * 
 * @author Piter T. de Boer
 */
public class VletDialog extends javax.swing.JDialog
{
	private static final long serialVersionUID = 77675878481916532L;
	
	public VletDialog()
	{
		super();  
	}

	public VletDialog(JFrame frame)
	{
		super(frame); 
	}

}