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

package nl.esciencecenter.vbrowser.vrs.data;

import java.util.Vector;

public class History<T> 
{
	protected Vector<T> elements; 	
	
	protected boolean noDoubles=true; 
	
	/** Point to latest added entry */ 
	protected int currentIndex=-1;  
	
	public History()
	{
		elements=new Vector<T>(); 
	}
	
	public History(int size)
	{
		elements=new Vector<T>(size);
	}
	
	public void add(T el)
	{
		// insert at current index. 
		// currentIndex points to next 'empty' space. 
		synchronized(elements)
		{
		    if ((noDoubles) && (isLast()))
		    {
		        // if last element quale current, don't add
		        if (currentIndex>0)
		            if (this.elements.get(currentIndex).equals(el))
		                return; 
		    }
		    
		    currentIndex++;
		    
		    elements.setSize(currentIndex);// truncate first 
		    
			elements.add(currentIndex,el); // current point to latest added
		}
	}
	
	public boolean isLast()
    {
	    return (currentIndex==size()-1); 
    }

    public T back()
	{
		// go back but keep current stack :
		synchronized(elements)
		{
			//int len=elements.size();
	
			if (currentIndex<=0)
				return null; 
			
            currentIndex--; // go back 
			
			T el=elements.get(currentIndex); 
	 
			return el; 
		}
	}
	
	public T forward()
	{
		// go forward in current stack: 
		synchronized(elements)
		{
			int len=elements.size();
	
			if (currentIndex+1>=len)
				return null; 
			
			currentIndex++; // go forward; 
			T el=elements.get(currentIndex);
			
			return el; 
		}
	}
	
	public int size()
	{
		return elements.size(); 
	}
	
}
