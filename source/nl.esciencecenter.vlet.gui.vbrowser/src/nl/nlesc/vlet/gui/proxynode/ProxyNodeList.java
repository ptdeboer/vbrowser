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

package nl.nlesc.vlet.gui.proxynode;

import java.util.Vector;

import nl.nlesc.vlet.gui.proxyvrs.ProxyNode;

public class ProxyNodeList extends Vector<ProxyNode>
{
	private static final long serialVersionUID = 3156726387218280584L;

	public ProxyNodeList(ProxyNode nodes[])
	{
		if (nodes!=null)
			for (ProxyNode node:nodes)
				this.add(node); 
	}	
	
	public ProxyNode[] toArray()
	{
		if (this.size()<=0) 
			return null; 
		
		ProxyNode arr[]=new ProxyNode[this.size()]; 
		arr=this.toArray(arr);
		
		return arr; 
	}
	
}
