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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.gui.Messages;
import nl.esciencecenter.vlet.gui.viewers.ViewerInfo;
import nl.esciencecenter.vlet.gui.viewers.ViewerRegistry;
import nl.esciencecenter.vlet.gui.viewers.ViewerRegistry.ViewerList;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSFactory;
import nl.esciencecenter.vlet.vrs.io.VStreamReadable;
import nl.esciencecenter.vlet.vrs.vfs.VFSFactory;

public class AboutNode extends VNode implements VStreamReadable
{
    public static final String VIEWER_PLUGIN_PREFIX="ViewerPlugin:"; 
    
    public static final String VRS_PLUGIN_PREFIX="VRSPlugin:"; 
    
    public static final String VFS_PLUGIN_PREFIX="VFSPlugin:"; 
       
	public AboutNode(AboutRS resResourceSystem, VRL vrl)
	{
		super(resResourceSystem.getVRSContext(), vrl);
	}

	public InputStream createInputStream() throws IOException 
	{
	    byte[] bytes;
	    
        try
        {
            bytes = getAboutText(this.getName()).getBytes("UTF-8");
            return new ByteArrayInputStream(bytes);
        }
        catch (UnsupportedEncodingException e)
        {
            throw new IOException("UnsupportedEncodingException:"+e.getMessage(),e); 
        } 
	     
    }
	
	public String getAboutText(String ref)
	{
	    if ( (StringUtil.isEmpty(ref)) || (ref.compareTo("/")==0))
	        return Messages.aboutText;
	    
	    if (ref.startsWith(VIEWER_PLUGIN_PREFIX)) 
	    {
	        String clazz=ref.substring(VIEWER_PLUGIN_PREFIX.length());
	        return getViewerPluginAboutText(clazz); 
	    }
	    
        if (ref.startsWith(VRS_PLUGIN_PREFIX))
        {
            String clazz=ref.substring(VRS_PLUGIN_PREFIX.length());
            return getVRSPluginAboutText(clazz);  
        }
        
        if ( ref.startsWith(VFS_PLUGIN_PREFIX))
        {
            String clazz=ref.substring(VFS_PLUGIN_PREFIX.length());
            return getVRSPluginAboutText(clazz);  
        }
	    
	    if (StringUtil.equalsIgnoreCase(ref,"plugins"))
                return createPluginInfo(true,true);
	    
        if (StringUtil.equalsIgnoreCase(ref,"vrsplugins"))
            return createPluginInfo(true,false);
        
        if (StringUtil.equalsIgnoreCase(ref,"vfsplugins"))
            return createPluginInfo(true,false);
        
        if (StringUtil.equalsIgnoreCase(ref,"viewerplugins"))
                return createPluginInfo(false,true); 
        
        return "<html><body><h1>No Information about:"+ref+"</h1></body></html>"; 
	}

	public static String createPluginInfo(boolean vrsPlugins, boolean viewerPlugins)
    {
	    String str="<html><body> <center><h1> Overview of Installed Plugins </h1></center><br>";
	    
	    if (vrsPlugins)
        {
	        str+="<table><tr><td colspan=2><center><h2> VRS Plugins </h2></center></td><tr>";
            VRSFactory[] vrss = VRS.getRegistry().getServices();
            for (VRSFactory vrs:vrss)
            {
                str+="<tr><td width=16></td><td>"+createVRSInfo(vrs)+"</td></tr>"; 
                str+="<tr height=8><td width=16></td><td> </td></tr>"; 
            }
            str+="</table>";  
        }
	    
	    if (viewerPlugins)
        {
            str+="<table><tr><td colspan=2><center><h2> Viewer Plugins </h2></center> </td><tr>";
            
            ViewerList viewerInfos = ViewerRegistry.getRegistry().getAllViewers();
            
            for (ViewerInfo info:viewerInfos)
            {
                str+="<tr><td width=16></td><td>"+createViewerInfo(info)+"</td></tr>"; 
                str+="<tr height=8><td width=16></td><td> </td></tr>"; 
            }
            str+="</table>";  
        }
	    
	    str+="</body></html>";
	        
	    return str; 
        
    }

    public String getViewerPluginAboutText(String viewerClass)
    {
        ViewerInfo info = ViewerRegistry.getRegistry().getViewerInfoForClass(viewerClass);
	    
	    if (info==null)
	        return "<html><h1>Couldn't find ViewerPlugin class:"+viewerClass+"</h1></html>"; 

	    return info.getAboutText(); 
    }
    
    public String getVRSPluginAboutText(String vrsClass)
    {
        VRSFactory vrs=VRS.getRegistry().getVRSFactoryClass(vrsClass); 
        
        if (vrs==null)
            return "<html><h1>Couldn't find VRS Plugin class:"+vrsClass+"</h1></html>"; 

        return vrs.getAbout(); 
    }

    public String getMimeType()
	{
	    return "text/html"; 
	}

    @Override
    public String getResourceType()
    {
        return "About"; 
    }

    // provide some harcoded html info for now 
    public static String createViewerInfo(ViewerInfo info)
    {
        String classname=info.getClassName(); 
        
        String str=
           "<table border=0 cellspacing=4 width=700>"
           +"  <tr bgcolor=#c0c0c0><td colspan=2><h3>Viewer:"+info.getName() + "</h3></td></tr>"
           +"  <tr bgcolor=#f0f0f0><td width=250>ViewerPlugin class:</td><td>"     + info.getClassName()      + "</td><tr>"
           +"  <tr bgcolor=#f0f0f0><td>ViewerPlugin Version:</td><td>"   + info.getVersion()        + "</td><tr>"
           +"  <tr bgcolor=#f0f0f0><td>More Information:</td><td>"
                +"<a href=\"about:ViewerPlugin:"+classname+"\">"+classname+"</a></td></tr>"
           +"  <tr bgcolor=#f0f0f0><td>ViewerPlugin mimeTypes:</td><td>" + new StringList(info.getMimeTypes()).toString("<br>")+ "</td><tr>"            
           +"</table>";
        
        return str; 
    }   

    public static String createVRSInfo(VRSFactory  vrs)
    {
        String classname=vrs.getClass().getName();  
        StringList schemeL=new StringList(vrs.getSchemeNames()); 
        String schemeStr=schemeL.toString("'",":'",", "); 
            
        String str=
           "<table border=0 cellspacing=4 width=700>"
           +"  <tr bgcolor=#c0c0c0 ><td colspan=2><h3>VRSFactory:"+vrs.getName() + "</h3></td></tr>"
           +"  <tr bgcolor=#f0f0f0><td td width=250>VRSFactory class:</td><td>"     + classname  + "</td></tr>"
           +"  <tr bgcolor=#f0f0f0><td>VRSFactory Version:</td><td>"   + vrs.getVersion() + "</td></tr>"
           +"  <tr bgcolor=#f0f0f0><td>VRSFactory information:</td>"
                  +"<td><a href=\"about:VRSPlugin:"+classname+"\">"+classname+"</a></td></tr>"
           +"  <tr bgcolor=#f0f0f0><td>Schemes:</td><td>"+schemeStr+"</td><tr>"

           +"</table>"; 
        
        if (vrs instanceof VFSFactory)
            str=str.replace("VRS","VFS"); 
        
        return str; 
    }   


    

}
