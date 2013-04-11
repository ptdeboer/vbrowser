/*
 * Copyright 2006-2011 The Virtual Laboratory for e-Science (VL-e) 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache Licence at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * See: http://www.vl-e.nl/ 
 * See: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 * $Id: AboutRSFactory.java,v 1.2 2013/01/23 10:39:57 piter Exp $  
 * $Date: 2013/01/23 10:39:57 $
 */ 
// source: 

package nl.nlesc.vlet.gui.aboutrs;

import nl.esciencecenter.ptk.Global;
import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.nlesc.vlet.exception.VlException;
import nl.nlesc.vlet.gui.UILogger;
import nl.nlesc.vlet.vrl.VRL;
import nl.nlesc.vlet.vrs.ServerInfo;
import nl.nlesc.vlet.vrs.VRS;
import nl.nlesc.vlet.vrs.VRSContext;
import nl.nlesc.vlet.vrs.VRSFactory;
import nl.nlesc.vlet.vrs.VResourceSystem;

/** 
 * Prototype "about:" resource system. 
 */

public class AboutRSFactory extends VRSFactory
{
    private AboutRS aboutRS;
    
	@Override
	public String getName()
	{
		return "AboutRS";	
	}

	private String[] schemes=
		{
			"about",
	        "About"
		};
	
	@Override
	public String[] getSchemeNames()
	{
		return schemes;
	}

	
	@Override
	public void clear()
	{
		
	}
  

    @Override
    public VResourceSystem createNewResourceSystem(VRSContext context, ServerInfo info,VRL location)
            throws VlException
    {
        if (aboutRS==null)
            aboutRS=new AboutRS(context);
        
        return aboutRS; 
    }


    @Override
    public String[] getResourceTypes()
    {
        String types[]=new String[1];
        types[0]="About";
        return types; 
    }


    public static void initPlatform()
    {
        try
        {
            VRS.getRegistry().registerVRSDriverClass(AboutRSFactory.class);
        }
        catch (Exception e)
        {
            UILogger.logException(AboutRSFactory.class,ClassLogger.ERROR,e,"*** Exception:%s\n",e); 
        }
        
    }

}
