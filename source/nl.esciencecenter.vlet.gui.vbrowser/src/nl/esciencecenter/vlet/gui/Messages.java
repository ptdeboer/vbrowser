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

package nl.esciencecenter.vlet.gui;

import nl.esciencecenter.vlet.VletConfig;

/**
 *  Class to hold the String Messages used in the VBrowser.
 *  Not all messages have been moved to this location yet.
 */ 
public class Messages
{
    // Questions 
    
	public static  String Q_do_you_want_to_recursive_delete_resource 
    	= "Delete resource and contents of";


    // Messages 
    public static  String M_could_not_add_resource 
        = "Could not add resource.";

    public static  String M_resource_cannot_be_added_to_itself = 
        "One or more resources can not be added to itself";

    public static  String M_resource_cannot_be_added_to_itself_or_its_childs = 
        "Resource can not be added to itself or to one of it's children."
        +"(Source is parent of destination).";

    // (Real) Error Messages: 
    public static  String E_renameble_interface_not_implemented = 
        "This resource cannot be renamed or doesn't implement the rename method";

    // Misc Texts 
    public static String aboutText=
         "<html><body><a name=top>"
        +"<center><h1> About the VBrowser </h1>"
        +"("+VletConfig.getVletVersion()+")<br>"
        +"</center>"
        +"<p>"
        +"<center>VBrowser Toolkit information.<br>" 
        +"<br>"
        +"This toolkit is licensed under the Apache License, Version 2.0<br>"
        +"<p>"
        +" <table>"
        +"  <tr><td> For more info mailto:</td>"  + "<td> vlet-develop@lists.sourceforge.net</td></tr>"  
        +"  <tr><td> See also:</td>"              + "<td> <a href=https://github.com/NLeSC/vbrowser/wiki>github.com/NLeSC/vbrowser/wiki</a> </td></tr>"
        +"  <tr><td></td> <td></td> </tr>" 
        +"  <tr><td> This about text:</td>"       + "<td> <a href=\"about:/\">about:/</a> </td></tr>"
        +"  <tr><td> Plugin information:</td>"    + "<td> <a href=\"about:/plugins\">about:/plugins</a> </td></tr>" 
        +" </table>"
        +"</center>"
        +"<p>"
        +"<center>"
        +"  <table>"
        +"    <tr><td align=center width=300 colspan=2><h3>Developers</h3></td></tr>"
        +"    <tr height=16><td width=50></td><td></td></tr>"
        +"    <tr><td width=300 colspan=2>Lead Programmer:</td></tr>"
        +"    <tr><td></td><td></td></tr>"
        +"    <tr><td width=50></td> <td>Piter T. de Boer</td></tr>"
        +"    <tr><td></td><td></td></tr>"
        +"    <tr><td colspan=2>Contributions by:</td></tr>"
        +"    <tr><td></td><td></td></tr>"
        +"    <tr><td></td><td>Spiros Koulouzis (SRM/LFC/Cloud)</td></tr>"
        +"    <tr><td></td><td>Martin Stam (vlemed)</td></tr>"
        +"    <tr><td></td><td>Kamel Boulebiar (LFC/vlemed)</td></tr>"
        +"    <tr><td></td><td>Ketan C. Maheshwari (RFTS/Grid Services)</td></tr>"
        +"    <tr><td></td><td>Kasper van den Berg (AID) </td></tr>"
        +"    <tr><td></td><td>Abdullah Z. &Ouml;zsoy (VTK)</td></tr>"
        +"    <tr><td></td><td></td></tr>"
        +"  </table>"
        +" </center>" 
        +" <center>"
        +"  <table> "
        +"    <tr><td align=center width=300 colspan=2><h3>(L)GPL Third party plugin software.</h3></td></tr>"
        +"    <tr height=16><td width=50></td><td></td></tr>"
        +"    <tr><td colspan=2>Lobo/Cobra HTML rendering toolkit.</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
        +"    <tr><td></td><td>See: <a href=\"http://www.lobobrowser.org/\">www.lobobrowser.org</a></td></tr>"
        +"    <tr><td></td><td>(GPL Licenced)</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
        +"    <tr><td colspan=2>jPedal PDF rendering toolkit</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
        +"    <tr><td></td><td>See: <a href=\"http://www.jpedal.org\">www.jpedal.org</a></td></tr>"
        +"    <tr><td></td><td>(GPL Licenced)</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
        +"    <tr><td colspan=2>Windows Bootstrapper and XTerm emulator: Piter.nl</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
        +"    <tr><td></td><td>See: <a href=\"http://www.piter.nl/java\">www.piter.nl/java</a></td></tr>"
        +"    <tr><td></td><td>(LGPL Licenced)</td></tr>"
        +"    <tr height=16><td></td><td> </td></tr>"
 
        +"  </table>"
        +"</center>"
        +"</body></html>";
 

    //public static  String _I_ACL_INFO_TEXT = "Edit ACL entries. \n";

    // ========================================================================
    // Menu ToolTips 
    // ========================================================================
    
    public static String TT_SET_AS_ROOT_TEXT="Make this location the toplevel location in the Resource Tree Window";
    
    public static String TT_COPYLOCATION_TEXT = "Copy this location to the copy buffer";

    public static String TT_EDIT_ACL = "Edit the Access Control List properties";

    public static String TT_CLICK_ON_VRL = "Click on the URI to open the location";

    public static String TT_VIEW_AS_ICONS = "View contents as icons (icon view)";
    
    public static String TT_VIEW_AS_TABLE = "View contents as detailed list (table view)";
        
    // =============
    // Resource Editor Messages  
    // ==============
	
	public static String M_can_not_create_new_server_config_invalid_port =  "Can not create new Server Configurarion. Invalid port number:%d"; 

	public static String D_view_or_edit_properties = "View properties of this resource";
	         
	public static String D_edit_link_properties = "Edit Link properties.";

	public static String D_view_resource_folder_properties = "Viewer Resource Folder properties.";

	public static String D_edit_server_configuration = "Edit Server Configuration. Specify location properties first,"
			+"then edit server configarution.";

}
