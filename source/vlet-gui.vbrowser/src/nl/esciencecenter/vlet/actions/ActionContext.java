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

package nl.esciencecenter.vlet.actions;

import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/**
 * ActionContext holds the context of the Menu Action when the user right- (or
 * alt mouse) clicked on a resource.
 * 
 * @author Piter T. de Boer
 */
public class ActionContext
{
    protected String type = null;
    protected VRL source = null;
    protected String mimeType = null;
    protected String selectionTypes[] = null;
    protected VRL selections[] = null;

    public String selectionMimeTypes[] = null;

    /** Resources in selections array are from clipboard (copy/past/drag/drop) */
    boolean isClipboardSelection = false;

    boolean isMiniDnDMenu = false;

    boolean isContainer = false; // isItem = (isContainer==false)

    public ActionContext()
    {
        // Empty Action/Selection
    }

    /** Action Context which has action source and optional Selections */
    public ActionContext(String typeVal, VRL sourceVRL, String sourceMimeType, String selTypes[], VRL[] selectionVRLs,
            String[] selMimeTypes, boolean selectionIsClipboardVal)
    {
        initSource(typeVal, sourceVRL, sourceMimeType);
        setSelections(selTypes, selectionVRLs, selMimeTypes, selectionIsClipboardVal);
    }

    /** Selection Action Context only */
    public ActionContext(String[] selTypes, VRL[] selVRLs, String[] selMimeTypes, boolean selectionIsClipboard)
    {
        this.selectionTypes = selTypes;
        this.selections = selVRLs;
        this.selectionMimeTypes = selMimeTypes;
        this.isClipboardSelection = selectionIsClipboard;
    }

    public ActionContext(String sourceType, VRL sourceVRL, String sourceMimeType)
    {
        initSource(sourceType, sourceVRL, sourceMimeType);
    }

    private void initSource(String sourceType, VRL sourceVRL, String sourceMimeType)
    {
        this.type = sourceType;
        this.source = sourceVRL;
        this.mimeType = sourceMimeType;
    }

    public void setSelections(String[] selTypes, VRL[] selVRLs, String[] selMimeTypes, boolean selectionIsClipboard)
    {
        this.selectionTypes = selTypes;
        this.selections = selVRLs;
        this.selectionMimeTypes = selMimeTypes;
        this.isClipboardSelection = selectionIsClipboard;
    }

    /**
     * Get source VRL of the action, that is the component or container on which
     * the user has right-clicked.
     */
    public VRL getSource()
    {
        return this.source;
    }

    /**
     * Get Resource Type (VRS) of the source of the action, that is the
     * component or container on which the user has right-clicked.
     */
    public VRL getSourceType()
    {
        return this.source;
    }

    public String getSourceMimeType()
    {
        return this.mimeType;
    }

    /**
     * Get selected sources, these can be null, if nothing was selected.
     */
    public VRL[] getSelections()
    {
        return this.selections;
    }

    /**
     * Get Resource Types of the selected sources. These can be NULL if nothing
     * was selected or when external Resources have been selected (URIs) which
     * have no VRS type. Entries in the returned types array match the entries
     * in the returned selected VRL array.
     */
    public String[] getSelectionTypes()
    {
        return this.selectionTypes;
    }

    public String[] getSelectionMimeTypes()
    {
        return this.selectionMimeTypes;
    }

    public void setIsMiniDnDMenu(boolean val)
    {
        this.isMiniDnDMenu = val;
    }

    public void setIsContainer(boolean val)
    {
        this.isContainer = val;
    }

    /**
     * Whether the action was triggered from click action on a Container. Also,
     * when multiple resources are selected the 'source' of the action is always
     * the container which has the sources selected.
     * 
     * @return
     */
    public boolean isContainer()
    {
        return isContainer;
    }

    public boolean isItem()
    {
        return (isContainer == false);
    }

    public String toString()
    {
        String str = "{<ActionContext>:\n" 
                   + "  {  type                 =" + type + "\n"
                   + "     source               =" + source + "\n" 
                   + "     isContainer          =" + isContainer + "\n"
                   + "     isClipboardSelection =" + isClipboardSelection + "\n" 
                   + "     isMiniDnDMenu        =" + isMiniDnDMenu + "\n";

        if (selections != null)
        {
            str +=     "\nnr Selections=" + selections.length + "\n";
            for (VRL vrl : selections)
            {
                str += "    - selection=" + vrl + "\n";
            }
        }

        str += "  }\n";
        str += "}\n";

        return str;
    }

}
