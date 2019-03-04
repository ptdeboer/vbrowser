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

import java.awt.event.MouseEvent;

import nl.esciencecenter.vbrowser.vrs.data.AttributeType;

/**
 * Enum class of the PERSISTENT property gui names&types 
 * The triple exists of: 
 *  - type : VAttribute type (needed to restore the attribute)<br> 
 *  - name : persistant property name ! (may not change) <br>
 *  - message : Human readable information about the property<br>
 *  - default value : 
 */
public enum GuiPropertyName
{
    MOUSE_SELECTION_BUTTON(AttributeType.INT,"mouseSelectionButton","Mouse selection button",MouseEvent.BUTTON1),  
    MOUSE_POPUP_BUTTON(AttributeType.INT,"mousePopupButton","Mouse menu pop-up button",MouseEvent.BUTTON3),     
    MOUSE_ALT_BUTTON(AttributeType.INT,"mouseAltButton","Mouse Alt (right) button",MouseEvent.BUTTON3),      
    SINGLE_CLICK_ACTION(AttributeType.BOOLEAN,"singleClickAction","Single click action",true), 
    GLOBAL_SHOW_LOG_WINDOW(AttributeType.BOOLEAN,"showLogWindow","Default show log window",false), 
    GLOBAL_FILTER_HIDDEN_FILES(AttributeType.BOOLEAN,"filterHiddenFiles","Default filter hidden files and directories",true),
    GLOBAL_SHOW_RESOURCE_TREE(AttributeType.BOOLEAN,"showResourceTree","Default show resource tree",true),
    GLOBAL_USE_WINDOWS_ICONS(AttributeType.BOOLEAN,"useWindowsIcons","Use windows icons",false),
    GLOBAL_LOOK_AND_FEEL(AttributeType.STRING,"defaultLookAndFeel","Platform Look and Feel",null)
    ;
    
    /** Attribute type is used when getting/setting VAttributes */
    private AttributeType type; 
    private String message; 
    private String name; // Property name used for storage/Attribute type 
    private String defaultValue; 
    
    private GuiPropertyName(AttributeType type,String name,String messagestr, int defaultVal)
    {
        this.name=name; 
        this.message=messagestr;
        this.type=type;
        this.defaultValue=""+defaultVal; 
    }
    
    private GuiPropertyName(AttributeType type,String name,String messagestr, boolean defaultVal)
    {
        this.name=name; 
        this.message=messagestr;
        this.type=type;
        this.defaultValue=""+defaultVal; 
    }
    
    private GuiPropertyName(AttributeType type,String name,String messagestr, String valstr)
    {
        this.name=name; 
        this.message=messagestr;
        this.type=type;
        this.defaultValue=valstr;  
    }
    
    public String getName()
    {
        return name; 
    }
    
    public String getMessage()
    {
        return message; 
    }
    
    public String getDefaultValue()
    {
        return defaultValue; 
    }
    
    public AttributeType getType()
    {
        return type; 
    }

    public String getDefault()
    {
        return defaultValue;
    }
}