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

package nl.esciencecenter.vlet.vrs.vdriver.infors.grid;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.NotImplementedException;
import nl.esciencecenter.vlet.vrs.LinkNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.vrms.LogicalResourceNode;


/**
 * Info node is a logical resource node providing some (non editable)
 * information about a remote resource.
 */
public class InfoNode extends LogicalResourceNode
{
    /**
     * Creates new InfoNode object with logical location and specified
     * linkTarget. Set resolve==true to resolve the location and add (optional)
     * extra attributes acquired from target location.
     */
    public static InfoNode createServerInfoNode(VRSContext context, VRL logicalLocation, VRL targetVRL, boolean resolve)
            throws VrsException
    {
        InfoNode lnode = new InfoNode(context, logicalLocation);
        lnode.init(logicalLocation, targetVRL, resolve);
        lnode.setShowShortCutIcon(false);
        lnode.setTargetIsComposite(true);
        lnode.setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
        return lnode;
    }

    private AttributeSet infoAttrs;

    public InfoNode(VRSContext context, VRL logicalLocation)
    {
        super(context, logicalLocation);
        // Default to Resource Location;
        this.setType(VRS.RESOURCE_INFO_TYPE);
        setEditable(false);
    }

    public String getMimeType()
    {
        return null;
    }

    public InfoNode duplicate()
    {
        InfoNode infoNode = new InfoNode(vrsContext, getVRL());
        infoNode.copyFrom(this);
        if (this.infoAttrs != null)
        {
            infoNode.infoAttrs = this.infoAttrs.duplicate();
        }
        return infoNode;
    }

    public InfoNode clone()
    {
        return duplicate();
    }

    /**
     * Info nodes are not editable, their attributes are reflected by remote
     * information services like BDII
     */
    public boolean isEditable()
    {
        return false;
    }

    public LinkNode toLinkNode() throws NotImplementedException
    {
        LinkNode lnode = new LinkNode(this);
        return lnode;
    }

    public Attribute getAttribute(String name) throws VrsException
    {
        Attribute attr = null;

        if (infoAttrs != null)
        {
            attr = infoAttrs.get(name);
            if (attr != null)
                return attr;
        }

        return super.getAttribute(name);

    }

    public String[] getAttributeNames()
    {
        StringList names = new StringList(super.getAttributeNames());
        if (infoAttrs != null)
            names.add(infoAttrs.getAttributeNames());
        return names.toArray();
    }

    // extra information
    public void setInfoAttributes(AttributeSet attrs)
    {
        this.infoAttrs = attrs;
    }

    public void setPresentation(Presentation pres)
    {
        super.setPresentation(pres);

    }
}
