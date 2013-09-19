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

package tests;

import java.awt.Frame;

import nl.esciencecenter.vlet.gui.util.charpane.CharPane;
import nl.esciencecenter.vlet.util.vlterm.TermPanel;


public class testCharPane
{
    
    public static void main(String args[])
    {
    	test1(false);
    	test1(true);
    	
    }
    
    public static void test1(boolean sync)
    {
        Frame frame=new Frame(); 
        CharPane term=new CharPane();
        frame.add(term);
        frame.pack(); 
        frame.setVisible(true); 
        
        int h=term.getRowCount(); 
        int w=term.getColumnCount(); 
        term.setWrapAround(false); 
        
        for (int i='A';i<='z';i++)
        {
            for (int x=0;x<w;x++) 
                for(int y=0;y<h;y++) 
                {
                    term.putChar((char)i,x,y); 
                }
            
            if (sync)
            	term.syncPainted(); 
            else
            {
            	try
            	{
            		Thread.sleep(20);
            	}
            	catch (InterruptedException e)
            	{
            		System.out.println("***Error: Exception:"+e); 
            		e.printStackTrace();
            	}
            }
            
        }
    }

}
