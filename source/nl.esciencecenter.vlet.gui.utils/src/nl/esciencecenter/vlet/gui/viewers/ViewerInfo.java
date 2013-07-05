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

package nl.esciencecenter.vlet.gui.viewers;

import java.util.Vector;

import nl.esciencecenter.vlet.actions.ActionMenuMapping;

/** Viewer Info Struct */
public class ViewerInfo
{
	public boolean isTool=false;
    
    /** Name for this viewer */
	private String name = null;
    
   	private String className = null; 
   	
   	private ClassLoader classLoader = null;

	private Vector<ActionMenuMapping> actionMappings;

    private String viewerVersion;

    private String viewerAboutText;

    private String[] mimeTypes; 

    /** Constructs ViewerInfo object */
    ViewerInfo(ClassLoader loader,String viewerName, String className) 
    {
    	this.name = viewerName;
    	this.className=className; 
    	this.classLoader=loader;
    }

    public boolean isTool()
    {
        return isTool;
    }
    
    /** Short name or alias for name */ 
    public String getName()
    {
    	return this.name;  
    }
    
    public String getClassName()
    {
    	return this.className; 
    }
    
    public ClassLoader getClassLoader()
    {
    	return classLoader; 
    }

	public Vector<ActionMenuMapping> getActionMappings()
	{
		return actionMappings;
	}

	public void setActionMappings(Vector<ActionMenuMapping> maps)
	{
		actionMappings=maps;
		
		if (maps!=null)
			for (ActionMenuMapping map:maps)
			{
				map.setIViewer(this.getClassName()); 
			}
	}

	public void setVersion(String version)
    {
        this.viewerVersion=version;
    }
	
	public void setAboutText(String text)
    {
        this.viewerAboutText=text; 
    }
	
	public String getVersion()
    {
        return this.viewerVersion; 
    }
	
	public String getAboutText() 
    {
        return this.viewerAboutText; 
    }

    public String[] getMimeTypes()
    {
        return mimeTypes; 
    }
    
    public void setMimeTypes(String types[])
    {
        this.mimeTypes=types; 
    }
    
    
};