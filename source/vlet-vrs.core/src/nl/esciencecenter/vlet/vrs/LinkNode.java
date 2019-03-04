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

package nl.esciencecenter.vlet.vrs;

import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.vfs.VFS;
import nl.esciencecenter.vlet.vrs.vfs.VFSNode;
import nl.esciencecenter.vlet.vrs.vrms.LogicalResourceNode;

/**
 * The LinkNode class which represents a link or a remote resource.
 * 
 * This class is NOT intended as a unix softlink (very bug prone), but more as a
 * windows shortcut object !<br>
 * At the VRS/VFS level a 'saved' LinkNode appears as .vlink file and will be
 * regarded as a file. This location is called the "storage" location. <br>
 * The GUI can both handle a '.vlink' file AND the LinkNode object. In the
 * latter case the .vlink file has been 'loaded' and is of type 'Link'. When
 * resolved the logical location might be different then the actual storage
 * location, for example when loaded into "myvle".
 */
public class LinkNode extends LogicalResourceNode
{
    // =========================================================================
    // Instance stuff
    // =========================================================================

    public void setLinkTarget(VRL linkTarget)
    {
        this.setResourceVRL(linkTarget);
    }

    /** Default (VNode) Constructor */
    public LinkNode(VRSContext context, VRL logicalLocation)
    {
        super(context, logicalLocation);
        setType(VRS.LINK_TYPE);
    }

    /** Public Copy Constructor */
    public LinkNode(LogicalResourceNode lnode)
    {
        this(lnode.vrsContext, lnode.getLocation());
        this.copyFrom(lnode);
    }

    public LinkNode duplicate()
    {
        LinkNode lnode = new LinkNode(this.vrsContext, this.getLocation());
        lnode.copyFrom(this);
        return lnode;
    }

    public LinkNode clone()
    {
        return duplicate();
    }

    public VRL getLinkTarget() throws VrsException
    {
        return this.getTargetVRL();
    }

    // ========================================================================
    // Class Stuff: LinkNode Factory Methods:
    // ========================================================================

    /**
     * Creates new LinkNode object at given parentLocation with new name. to
     * specified specified targetVRL. Parent location is used to resolve new
     * (logical) child location. s
     */
    public static LinkNode createLinkTo(VRSContext context, VRL parentLocation, String name, VRL targetVRL)
            throws VrsException
    {
        VNode parentNode = context.openLocation(parentLocation);

        // Bug: update save vlink with file extention when creating new link
        // into directory.
        if (parentNode instanceof VFSNode)
        {
            if (name.endsWith(VFS.VLINK_EXTENSION) == false)
            {
                name = name + "." + VFS.VLINK_EXTENSION;
            }
        }
        // Let Parent resolve child VRL to get actual logical path !
        VRL linkLoc = parentNode.getVRL().resolvePath(name);
        LinkNode lnode = LinkNode.createLinkNode(context, linkLoc, targetVRL);
        lnode.saveAtLocation(linkLoc);

        return lnode;
    }

    /**
     * Creates new LinkNode object with logical location and specified
     * linkTarget. Does not save nor update storage location.
     */
    public static LinkNode createLinkNode(VRSContext context, VRL logicalLocation, VRL targetVRL) throws VrsException
    {
        return createLinkNode(context, logicalLocation, targetVRL, true);
    }

    /**
     * Creates new LinkNode object with logical location and specified
     * linkTarget. Set resolve==true to resolve the location and add (optional)
     * extra attributes acquired from target location. Does not save not update
     * storage location.
     */
    public static LinkNode createLinkNode(VRSContext context, VRL logicalLocation, VRL targetVRL, boolean resolve)
            throws VrsException
    {
        LinkNode lnode = new LinkNode(context, logicalLocation);
        lnode.init(logicalLocation, targetVRL, resolve);

        return lnode;
    }

    /** Load ResourceNode but return as LinkNode ! */
    public static LinkNode loadFrom(VRSContext context, VRL loc) throws VrsException
    {
        VNode vnode = context.openLocation(loc);
        return loadFrom(context, vnode);
    }

    /**
     * Factory method to load a stored LogicalResource. Currenltly only .vlink
     * files are supported.
     */
    public static LinkNode loadFrom(VRSContext context, VNode vnode) throws VrsException
    {
        VRL vrl = vnode.getVRL();
        LinkNode node = null;

        // Duplicate from LinkNode Object:use resolved LinkNode !
        if (vnode instanceof LinkNode)
        {
            node = new LinkNode(context, vrl);
            node.copyFrom((LinkNode) vnode);
        }
        else if (vnode instanceof LogicalResourceNode)
        {
            // downcast from LogicalResourceNode to LinkNode !
            node = new LinkNode(context, vrl);
            node.copyFrom((LogicalResourceNode) vnode);
        }
        else
        {
            node = new LinkNode(context, vrl);

            // load from vnode:
            node.loadFrom(vnode);
        }

        return node;
    }

    public static LinkNode createServerNode(VRSContext context, VRL logicalVRL, VRL targetVRL) throws VrsException
    {
        return LinkNode.createServerNode(context, logicalVRL, targetVRL, false);
    }

    public static LinkNode createServerNode(VRSContext context, VRL logicalVRL, VRL targetVRL, boolean resolve)
            throws VrsException
    {
        LinkNode lnode = LinkNode.createLinkNode(context, logicalVRL, targetVRL, resolve);
        lnode.setType(VRS.SERVER_TYPE);
        return lnode;
    }

    public static LinkNode createResourceNode(VRSContext context, VRL logicalVRL, VRL targetVRL, boolean resolve)
            throws VrsException
    {
        LinkNode lnode = LinkNode.createLinkNode(context, logicalVRL, targetVRL, resolve);
        lnode.setType(VRS.RESOURCE_LOCATION_TYPE);
        return lnode;
    }

    public VNode getTargetNode() throws VrsException
    {
        return this.getVRSContext().openLocation(this.getTargetLocation());
    }

}
