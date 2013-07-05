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

package nl.esciencecenter.vlet.gui.aboutrs;

import nl.esciencecenter.ptk.util.logging.ClassLogger;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.UILogger;
import nl.esciencecenter.vlet.vrs.ServerInfo;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.VRSFactory;
import nl.esciencecenter.vlet.vrs.VResourceSystem;

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
            throws VrsException
    {
        if (aboutRS==null)
            aboutRS=new AboutRS(context,location);
        
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
