package nl.vbrowser.ui.data;

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
