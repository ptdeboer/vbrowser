/*
 * Copyrighted 2012-2013 Netherlands eScience Center.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").  
 * You may not use this file except in compliance with the License. 
 * For details, see the LICENCE.txt file location in the root directory of this 
 * distribution or obtain the Apache License at the following location: 
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 * 
 * For the full license, see: LICENCE.txt (located in the root folder of this distribution). 
 * ---
 */
// source: 

package nl.esciencecenter.vlet.vrs.vrms;

import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_ICONURL;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_NAME;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_RESOURCE_TYPE;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.w3c.dom.DOMException;

import nl.esciencecenter.ptk.object.Duplicatable;
import nl.esciencecenter.ptk.presentation.IPresentable;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.exception.ResourceDeletionFailedException;
import nl.esciencecenter.vlet.exception.ResourceTypeMismatchException;
import nl.esciencecenter.vlet.exception.ResourceTypeNotSupportedException;
import nl.esciencecenter.vlet.vrs.LinkNode;
import nl.esciencecenter.vlet.vrs.VComposite;
import nl.esciencecenter.vlet.vrs.VCompositeDeletable;
import nl.esciencecenter.vlet.vrs.VDeletable;
import nl.esciencecenter.vlet.vrs.VEditable;
import nl.esciencecenter.vlet.vrs.VNode;
import nl.esciencecenter.vlet.vrs.VRS;
import nl.esciencecenter.vlet.vrs.VRSClient;
import nl.esciencecenter.vlet.vrs.VRSContext;
import nl.esciencecenter.vlet.vrs.VRSFactory;
import nl.esciencecenter.vlet.vrs.VRenamable;
import nl.esciencecenter.vlet.vrs.data.VAttributeConstants;
import nl.esciencecenter.vlet.vrs.data.xml.VCompositePersistance;
import nl.esciencecenter.vlet.vrs.data.xml.XMLData;
import nl.esciencecenter.vlet.vrs.io.VStreamReadable;
import nl.esciencecenter.vlet.vrs.util.VRSResourceLoader;
import nl.esciencecenter.vlet.vrs.vfs.VFSClient;
import nl.esciencecenter.vlet.vrs.vfs.VFile;


/**
 * New composite Resource Folder.
 * <p>
 * Implementation note: this class implements VLogicalResource but it NOT a
 * LogicalResourceNode which is used as parent for VLinks ! (Main difference is
 * that ResourceFolder is composite and LogicalNode is singular)
 */
public class ResourceFolder extends LogicalFolderNode<VNode> implements VCompositePersistance, VEditable, VRenamable,
        Duplicatable<ResourceFolder>, VDeletable, VCompositeDeletable, VStreamReadable, IPresentable
{
    AttributeSet attributes = new AttributeSet();

    private VRL descriptionLocation;

    // unique ID reference counter for child nodes:
    private int childRefCounter = 0;

    static private String[] attributeNames =
    { ATTR_RESOURCE_TYPE, ATTR_NAME,
            // ATTR_PATH,
            // ATTR_LOCATION,
            ATTR_ICONURL };

    public ResourceFolder(ResourceFolder parent, VRSContext context, VRL vrl)
    {
        super(context, vrl);
        this.logicalParent = parent;
        init();
    }

    public ResourceFolder(VRSContext context, VRL vrl)
    {
        super(context, vrl);
        init();
    }

    public ResourceFolder(VRSContext context, AttributeSet attrSet, VRL vrl)
    {
        super(context, vrl);
        init(attrSet);
    }

    public ResourceFolder duplicate()
    {
        return duplicate(false);
    }
    
    public boolean shallowSupported()
    {
        return false;
    }
    
    public ResourceFolder duplicate(boolean shallowCopy)
    {
        try
        {
            // duplicate attributes:
            ResourceFolder group = new ResourceFolder(this.getVRSContext(), this.attributes, (VRL) null);
    
            VNode nodes[] = this.getNodes();
    
            if (nodes != null)
            {
                for (VNode node : this.getNodes())
                {
                    group.addSubNode(node.duplicate());
                }
            }
    
            return group;
        }
        catch(VrsException e)
        {
            throw new RuntimeException(e.getMessage(),e); 
        }
    }

    private void init(AttributeSet attrSet)
    {
        // duplicate
        attributes = attrSet.duplicate();
        initDefaultAttributes();
    }

    private void init()
    {
        initDefaultAttributes();
    }

    private void initDefaultAttributes()
    {
        if (attributes == null)
            attributes = new AttributeSet();

        setIfNotSet(ATTR_ICONURL, "vle-world-folder.png");
        setIfNotSet(ATTR_NAME, "<NO NAME>");
    }

    private void setIfNotSet(String name, String newValue)
    {
        Attribute attr = attributes.get(name);
        String value = null;

        if (attr != null)
            value = attr.getStringValue();

        if (StringUtil.isEmpty(value))
            attributes.put(new Attribute(name, newValue));
    }

    public Attribute[] getAttributes() throws VrsException
    {
        return getAttributes(getAttributeNames());
    }

    @Override
    public String getResourceType()
    {
        return VRS.RESOURCEFOLDER_TYPE;
    }

    public String[] getAttributeNames()
    {
        return attributeNames;
    }

    public Attribute getAttribute(String name) throws VrsException
    {
        if (name == null)
            return null;

        // check attribute store first:
        Attribute attr = this.attributes.get(name);

        // check super (standard or derived attributes, like: scheme,path,etc!)
        if (attr == null)
            attr = super.getAttribute(name);

        if (attr != null)
        {
            if (name.compareTo(ATTR_NAME) == 0)
            {
                attr.setEditable(true);
            }
            else if (name.compareTo(ATTR_ICONURL) == 0)
            {
                attr.setEditable(true);
            }
        }

        return attr;
    }

    public void setIconURL(String str)
    {
        this.attributes.put(new Attribute(VAttributeConstants.ATTR_ICONURL, str));
    }

    public String getIconURL()
    {
        // concurrent modifications ?
        if (attributes == null)
            return null;
        return this.attributes.getStringValue(VAttributeConstants.ATTR_ICONURL);
    }

    public void setLogicalParent(VNode node) throws ResourceTypeMismatchException
    {
        if ((node instanceof ResourceFolder) == false)
            throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException(
                    "ResourceGroups can only have ResourceFolder node as parent. Got:" + node);

        this.logicalParent = (ResourceFolder) node;
    }

    public String[] getResourceTypes()
    {
        // ResourceFolder group MyVle Resources.
        return this.vrsContext.getMyVLe().createResourceTypes();
    }

    public AttributeSet getPersistantAttributes()
    {
        return this.attributes;
    }

    public String getPersistantType()
    {
        return VRS.RESOURCEFOLDER_TYPE;
    }

    public void setName(String name)
    {
        synchronized (attributes)
        {
            this.attributes.set(new Attribute(ATTR_NAME, name));
        }

    }

    public String getName()
    {
        return attributes.getStringValue(ATTR_NAME);
    }

    public String getMimeType() throws VrsException
    {
        return super.getMimeType(); // should be vlet-resourcefolder-xml
    }

    public boolean setAttribute(Attribute attr) throws VrsException
    {
        return setAttribute(attr, true);
    }

    public boolean setAttribute(Attribute attr, boolean save) throws VrsException
    {
        synchronized (attributes)
        {
            this.attributes.set(attr);
        }

        if (save)
            save();

        return true;
    }

    public boolean setAttributes(Attribute[] attrs) throws VrsException
    {
        synchronized (attributes)
        {
            for (Attribute attr : attrs)
                setAttribute(attr, false);
        }

        save();

        return true;
    }

    public boolean isRenamable() throws VrsException
    {
        return isEditable();
    }

    public boolean renameTo(String newName, boolean nameIsPath) throws VrsException
    {
        return (rename(newName, nameIsPath) != null);
    }

    public VRL rename(String newName, boolean nameIsPath) throws VrsException
    {
        if (isEditable() == false)
            throw new nl.esciencecenter.vlet.exception.ResourceNotEditableException("Cannot rename this folder: Read Only folder");

        setName(newName);
        return this.getVRL();
    }

    public VNode addNode(VNode node) throws VrsException
    {
        if (isEditable() == false)
            throw new nl.esciencecenter.vlet.exception.ResourceNotEditableException(
                    "Cannot add resource to this folder: Read Only folder");

        return addNode(node, null, false);
    }

    public VNode addNode(VNode node, String newName, boolean isMove) throws VrsException
    {
        if (isEditable() == false)
            throw new nl.esciencecenter.vlet.exception.ResourceNotEditableException(
                    "Cannot add resource to this folder: Read Only folder");

        // only Logical Resources allowed:
        if ((node instanceof VLogicalResource) == false)
            throw new ResourceTypeNotSupportedException("Resource type not supported:" + node);

        VNode oldParent = node.getParent();

        VNode newNode = node;

        // Currently two concrete types allowed: ResourceFolder and
        // LogicalResourceNode
        if (node instanceof ResourceFolder)
        {
            if (isMove == false)
            {
                newNode = ((ResourceFolder) node).duplicate();
                addSubNode(newNode);
            }
            else
            {
                // delete old storage location (if it has one):
                // this is only the case when moving from MyVle to
                // existing ResourceFolder
                ((ResourceFolder) newNode).deleteDiscriptionLocation();
                addSubNode(newNode);
            }
        }
        else if (node instanceof LogicalResourceNode)
        {
            if (isMove == false)
            {
                newNode = ((LogicalResourceNode) node).duplicate();
                addSubNode(newNode);
            }
            else
            {
                // delete old storage location (if it has one):
                // this is only the case when moving from MyVle to
                // existing ResourceFolder
                ((LogicalResourceNode) newNode).deleteStorageLocation();
                addSubNode(newNode);
            }
        }

        // notify parent of move:
        if (isMove)
        {
            if (oldParent instanceof ResourceFolder)
                ((ResourceFolder) oldParent).unlinkNode(node); // unlink
            else if (oldParent instanceof MyVLe)
                ((MyVLe) oldParent).unlinkNode(node); // unlink
            // default:
            else if (oldParent instanceof VComposite)
                ((VComposite) oldParent).delNode(node); // unlink ?
        }

        save();

        return newNode; // could be same node in case of a move !
    }

    /**
     * Sets logical location of this ResourceFolder, but also updates ALL
     * logical location of all of it's children.
     */
    public void setLogicalLocation(VRL vrl) throws VrsException
    {
        this.setLocation(vrl);

        for (VNode child : childNodes)
        {
            this.setSubNodeLogicalLocation((VLogicalResource) child);
        }
    }

    public VNode createNode(String type, String name, boolean force) throws VrsException
    {
        //debug("Create new node:(" + type + "):" + name);
        VNode vnode = createSubNode(type, name, force);
        addSubNode(vnode);
        return vnode;
    }

    /**
     * When not stored on a file system, this node must have a VNode parent
     */
    protected void addSubNode(VNode node) throws VrsException
    {
        //debug("addSubNode():" + node);

        if ((node instanceof VLogicalResource) == false)
            throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Node type not supported:" + node);

        synchronized (childNodes)
        {
            childNodes.add(node);
            setSubNodeLogicalLocation((VLogicalResource) node);
            // update parent !
            ((VLogicalResource) node).setLogicalParent(this);

            if (node instanceof LogicalResourceNode)
            {
                ((LogicalResourceNode) node).deleteStorageLocation();
            }
        }

        save();
    }

    public boolean delete() throws VrsException
    {
        return delete(false);
    }

    public boolean delete(boolean recursive) throws VrsException
    {
        if (isEditable() == false)
            throw new nl.esciencecenter.vlet.exception.ResourceNotEditableException("Cannot delete this folder: Read Only folder");

        if ((recursive == false) && (this.childNodes != null) && (this.childNodes.size() > 0))
        {
            throw new ResourceDeletionFailedException("Resource is not empty:" + this);
        }

        VNode parent = this.getParent();

        // unlink myself, childs will be discared automatically !
        if ((parent != null) && (parent instanceof VComposite))
        {
            ((VComposite) parent).delNode(this);
        }

        // delete description location if I have one. (.vrsx file)
        deleteDiscriptionLocation();

        dispose();

        return true;
    }

    private boolean deleteDiscriptionLocation() throws VrsException
    {
        if (this.descriptionLocation != null)
        {
            VNode node = this.vrsContext.openLocation(descriptionLocation);

            if (node instanceof VDeletable)
            {
                //Global.infoPrintf(this, "Feleting description location:%s\n",node);
                return ((VDeletable) node).delete();
            }
        }

        return false;
    }

    public void dispose()
    {
        this.attributes.clear();
        this.attributes = null;
        this.childNodes.clear();
        this.childNodes = null;
        this.descriptionLocation = null;
        // this.vrsContext=null; is final
        // this.location=null; keep locatio

    }

    private void setSubNodeLogicalLocation(VLogicalResource node) throws VrsException
    {
        if (this.getLocation() == null)
        {
            // can happen at initialization:
            //Global.warnPrintf(this, "setSubNodeLogicalLocation(): No Logical location (yet?) for:%s\n",this);
            return;
        }

        //
        // To avoid cashing bugs in the VBrowser, continue
        // counting !
        // this assures each new node isn't shadowed by (old) cache values !
        // This stil is a bug in the VBrowser's cache !
        //

        int ref = this.childRefCounter++; // .childNodes.indexOf(node);

        VRL newRef = this.getLocation().appendPath("v" + StringUtil.toZeroPaddedNumber(ref, 3));

        //debug("addSubNode():newRef=" + newRef);
        node.setLogicalLocation(newRef);
    }

    /** New implementation of create Node */
    protected VNode createSubNode(String type, String name, boolean force) throws VrsException
    {
        // New Link
        if (type.compareTo(VRS.LINK_TYPE) == 0)
        {
            LinkNode lnode = LinkNode.createLinkNode(vrsContext, (VRL) null, new VRL("file:///"), false);
            // add default attributes to show up in Attributes
            lnode.setShowShortCutIcon(true);
            lnode.setIconURL("");

            if (name != null)
                lnode.setName(name);
            else
                lnode.setName("New Link");

            return lnode;
        }
        // ResourceFolder
        else if (type.compareTo(VRS.RESOURCEFOLDER_TYPE) == 0)
        {
            ResourceFolder gnode = new ResourceFolder(this, this.vrsContext, null);
            if (name != null)
                gnode.setName(name);
            else
                gnode.setName("New Group");

            return gnode;
        }
        //
        // Check New <ResourceType>
        //
        String vrsName = null;
        int i = type.indexOf(" " + MyVLe.resourcePostfix);

        if (i >= 0)
            vrsName = type.substring(0, i);
        else
            vrsName = type;

        VRSFactory vrs = this.vrsContext.getRegistry().getVRSFactoryWithName(vrsName);

        // Create New LogicalResource for VRS specification
        if (vrs != null)
        {
            VRL targetLoc = null;
            String scheme = vrs.getSchemeNames()[0];
            int port = VRS.getSchemeDefaultPort(scheme);
            targetLoc = new VRL(scheme, "", port, "/~");
            LinkNode lnode = LinkNode.createResourceNode(vrsContext, (VRL) null, targetLoc, false);

            if (name != null)
                lnode.setName(name);
            else
                lnode.setName("New " + vrsName);

            // do not show the shortcut image
            lnode.setShowShortCutIcon(false);
            lnode.setLogicalParent(this);

            // copy/create default attributes: will consult VRS for defaults!
            lnode.initializeServerAttributes();

            // custom icons:

            if (StringUtil.endsWith(lnode.getTargetHostname(), "sara.nl"))
            {
                lnode.setIconURL("custom/sara_server.png");
            }
            else if (StringUtil.compare(lnode.getTargetScheme(), VRS.SRB_SCHEME) == 0)
            {
                // rootLocations.add(targetLoc);
                lnode.setIconURL("custom/srb_server.png");
            }
            else
            {
                // rootLocations.add(targetLoc);
                lnode.setIconURL("gnome/48x48/filesystems/gnome-fs-network.png");
            }

            return lnode;
        }

        throw new ResourceTypeNotSupportedException("Resource type not supported:" + type);

    }

    /**
     * Returns location of XML description of this ResourceFolder and all it's
     * children resources.<br>
     * Only the root of a ResourceFolder has a description location.
     */
    public VRL getStorageLocation() throws VrsException
    {
        return descriptionLocation;
    }

    public boolean save()
    {
        if (logicalParent != null)
        {
            //debug("save(): calling save of :" + logicalParent);
            return logicalParent.save();
        }
        else if (this.descriptionLocation != null)
        {
            try
            {
                saveToXml();
            }
            catch (Exception e)
            {
                //Global.logException(ClassLogger.ERROR,this,e,"Couldn't save xml description:%s",this);
            }

            return false;
        }
        else
        {

            if (this.logicalParent != null)
            {
                // Global.warnPrintf(this, "*** Warning: save() called on node which does not have a parent container:%s\n",
                //       this);
            }
            else if (this.descriptionLocation == null)
            {
                //Global.warnPrintf(this,
                //  "*** Warning: save() called on node which does not have a description location:%s\n",this);
            }
            
            return false;
        }
    }

    private Object saveMutex = new Object();

    private Presentation presentation;

    private void saveToXml() throws Exception
    {
        //debug("Save to xml:" + this);
        synchronized (saveMutex)
        {

            VRL loc = this.getStorageLocation();
            //debug("Saving location=" + loc);

            if (loc == null)
            {
                throw new nl.esciencecenter.vlet.exception.ResourceDeletionFailedException("Description Location is NULL of:"
                        + this);
            }
            
            VRSResourceLoader writer=new VRSResourceLoader(vrsContext); 
            writer.writeTextTo(loc, this.toXMLString()); 
        }
    }
  
    public String toXMLString() throws DOMException, VrsException
    {
        String comments = "VL-e ResourceFolder description :" + this.getName();
        XMLData xmlData = getXMLData();
        return xmlData.createXMLString(this, comments);
    }

    private static XMLData getXMLData()
    {
        XMLData xmlData = new XMLData();
        xmlData.setPersistanteNodeElementName("vlet:resourceNode");
        xmlData.setVAttributeSetElementName("vlet:resourceDescription");
        xmlData.setVAttributeElementName("vlet:resourceProperty");
        return xmlData;
    }

    public void saveAsXmlTo(VRL loc)
    {
        synchronized (saveMutex)
        {
            this.descriptionLocation = loc;
            save();
        }
    }

    public static ResourceFolder readFromXMLStream(VRSContext context, VStreamReadable source) throws Exception
    {
        InputStream stream = source.createInputStream();
        ResourceFolder node = readFromXMLStream(context, stream);

        if (node == null)
            return null;

        if (source instanceof VNode)
            node.descriptionLocation = ((VNode) source).getVRL();

        return node;
    }

    private static ResourceFolder readFromXMLStream(VRSContext context, InputStream stream) throws Exception
    {
        XMLData data = getXMLData();
        ResourceNodeFactory nodeFactory = new ResourceNodeFactory(data, context);
        return (ResourceFolder) data.parsePersistantNodeTree(nodeFactory, stream);
    }

    public InputStream createInputStream() throws IOException
    {
        try
        {
            String xmlString = this.toXMLString();
            // ByteArray to Input Stream
            ByteArrayInputStream inps = new ByteArrayInputStream(xmlString.getBytes("UTF-8"));
            return inps; 
        }
        catch (DOMException e)
        {
            throw new IOException(e.getMessage(),e); 
        }
        catch (VrsException e)
        {
            throw new IOException(e.getMessage(),e);
        } 
    }

    public int getOptimalReadBufferSize()
    {
        return -1; // use default
    }

    public static boolean isResourceFolderDescription(VNode sourceNode)
    {
        if (sourceNode == null)
            return false;

        if (sourceNode instanceof ResourceFolder)
            return true;

        if ((sourceNode.getVRL() != null) && (sourceNode.getVRL().hasExtension(VRS.VRESX_EXTENSION,false)))
            return true;

        return false;
    }

    public static ResourceFolder createFrom(VNode sourceNode) throws Exception
    {
        if (sourceNode instanceof ResourceFolder)
            return ((ResourceFolder) sourceNode).duplicate();

        if (sourceNode instanceof VStreamReadable)
        {
            return readFromXMLStream(sourceNode.getVRSContext(), (VStreamReadable) sourceNode);
        }

        throw new nl.esciencecenter.vlet.exception.ResourceTypeMismatchException("Not a ResourceFolder description:" + sourceNode);
    }

    public Presentation getPresentation()
    {
        return this.presentation;
    }

    public void setPresentation(Presentation newPresentation)
    {
        this.presentation = newPresentation;
    }

}
