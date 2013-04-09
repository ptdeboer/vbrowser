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

package nl.uva.vlet.vrms;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.uva.vlet.data.VAttributeSet;
import nl.uva.vlet.data.xml.XMLData;
import nl.uva.vlet.data.xml.XMLtoNodeFactory;
import nl.uva.vlet.exception.VlException;
import nl.uva.vlet.vrl.VRL;
import nl.uva.vlet.vrs.VNode;
import nl.uva.vlet.vrs.VRS;
import nl.uva.vlet.vrs.VRSContext;


/**
 * ResourceNode factory wich creates nodes from XML. 
 * Use by XMLData 
 * 
 * @author P.T. de Boer
 */

public class ResourceNodeFactory extends XMLtoNodeFactory
{
	protected XMLData xmlData;

	public ResourceNodeFactory(XMLData data,VRSContext context)
	{
		super(context);
		this.xmlData=data;
	}

	@Override
	public VNode createNode(VNode parent,String type, VAttributeSet attrSet) throws VlException
	{
		if (type==null)
			throw new NullPointerException("Type can not be null"); 
		
		if (type.compareTo(VRS.RESOURCEFOLDER_TYPE)==0) 
		{
			ResourceFolder groupNode=null; 
			
			if (attrSet==null)
				groupNode= new ResourceFolder(getContext(),(VRL)null);
			else
				groupNode= new ResourceFolder(getContext(),attrSet,(VRL)null);
			
			return groupNode; 
		}
		
		else if (type.compareTo(LogicalResourceNode.PERSISTANT_TYPE)==0) 
		{
			LogicalResourceNode node = new LogicalResourceNode(getContext(),attrSet,null);
			return node; 
		}
		else
		{
			ClassLogger.getLogger(this.getClass()).errorPrintf("***Error: unknown type:%s\n",type); 
		}
		
		return null;
	}

}
