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

import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_ATTRIBUTE_NAMES;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_HOSTNAME;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_ICONURL;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_ISCOMPOSITE;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_ISEDITABLE;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_LOCATION;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_MIMETYPE;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_NAME;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_PATH;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_PORT;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_RESOURCE_CLASS;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_RESOURCE_TYPE;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_RESOURCE_TYPES;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_SCHEME;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_URI_FRAGMENT;
import static nl.esciencecenter.vlet.vrs.data.VAttributeConstants.ATTR_URI_QUERY;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.util.ResourceLoader;
import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vbrowser.vrs.exceptions.VrsException;
import nl.esciencecenter.vbrowser.vrs.mimetypes.MimeTypes;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;
import nl.esciencecenter.vlet.vrs.events.ResourceEvent;
import nl.esciencecenter.vlet.vrs.vrms.LogicalResourceNode;

/**
 * The VNode class, the super class of all resource nodes in the VRS package. It
 * can be seen as a handler object, for example a reference to a (remote) file
 * or directory or other generic resource.
 * 
 * Every VNode is associated with a VRL.
 * 
 * @author P.T. de Boer
 */
public abstract class VNode 
{
    static protected String[] vnodeAttributeNames =
        {
            ATTR_RESOURCE_TYPE, 
            ATTR_NAME, 
            ATTR_SCHEME, 
            ATTR_HOSTNAME, 
            ATTR_PORT, 
            ATTR_ICONURL, 
            ATTR_PATH, 
            ATTR_MIMETYPE,
            ATTR_LOCATION
        };

    // ========================================================================

    // ========================================================================

    /** URI compatable VRL which specifies the resource location. */
    private VRL _vrl = null;

    /**
     * Final VRSContext associated with this node.
     */
    protected final VRSContext vrsContext;

    // ========================================================================
    // Constuctors/Initializers
    // ========================================================================

    /** Block empty constructor */
    @SuppressWarnings("unused")
    private VNode()
    {
        vrsContext = null;
    }

    /**
     * 
     */
    public VNode(VRSContext context, VRL vrl)
    {
        this.vrsContext = context;
        setLocation(vrl);
    }

    // ========================================================================
    // Field Methods
    // ========================================================================

    /**
     * See getVRL()
     * 
     * @see getVRL()
     */
    final public VRL getLocation()
    {
        return _vrl;
    }

    /** Returns extension part of VRL */
    final public String getExtension()
    {
        return this.getLocation().getExtension();
    }

    /** Returns VRSContext which whas used to create this node */
    final public VRSContext getVRSContext()
    {
        return this.vrsContext;
    }

    /**
     * Returns Virtual Resource Locator (VRL) of this object. This is an URI
     * compatible class but with more (URL like) features.
     * 
     * @see VRL
     * @see java.net.URI
     */
    final public VRL getVRL()
    {
        return _vrl;
    }

    public String getScheme()
    {
        return this.getLocation().getScheme();
    }
    
    /**
     * Returns the short name of the resource.<br>
     * The default is the basename of the resource or the last part of the path
     * part in the URI. To use another name, subclassses must overide this
     * method.
     */
    public String getName()
    {
        if (_vrl == null)
            return null;

        return _vrl.getBasename(); // default= last part of path
    }

    /** Returns logical path of this resource */
    public String getPath()
    {
        if (_vrl == null)
            return null;

        return _vrl.getPath();
    }

    /** Returns Hostname */
    public String getHostname()
    {
        if (_vrl == null)
            return null;

        return _vrl.getHostname();
    }

    /** Returns Port. If the value <=0 then the default port must be used. */
    public int getPort()
    {
        return _vrl.getPort();
    }

    /** Returns basename part of the path of a node. */
    public String getBasename()
    {
        // Default implementation: getBasename of path
        return _vrl.getBasename();
    }

    /**
     * Returns Mime Type based upon file filename/extension. For a more robust
     * method, use MimeTypes.getMagicMimeType().
     * 
     * @throws VrsException
     * 
     * @see MimeTypes.getMagicMimeType(byte[])
     * @see MimeTypes.getMimeType(String)
     */
    public String getMimeType() throws VrsException
    {
        return MimeTypes.getDefault().getMimeType(this.getPath());
    }

    /**
     * Check whether this VNode implements the VComposite interface.
     */
    public boolean isComposite()
    {
        return (this instanceof VComposite);
    }

    /**
     * Get the names of the all attributes this resource has. To get the subset
     * of resource specific
     */
    public String[] getAttributeNames()
    {
        return vnodeAttributeNames;
    }

    /**
     * Get the names of the resource specific attributes leaving out default
     * attributes and optional super class attributes this resource has. This
     * typically is the subset of getAttributeNames() minus
     * super.getAttributeNames();
     */
    public String[] getResourceAttributeNames()
    {
        return null;
    }

    /**
     * Get all attributes defined by attributeNames
     * 
     * @throws VrsException
     */
    public Attribute[] getAttributes() throws VrsException
    {
        return getAttributes(getAttributeNames());
    }

    /**
     * Get all attributes defined by <code>names</code>.<br>
     * Elements in the <code>names</code> array may be null! It means do not
     * fetch the attribute. This is to speed up fetching of indexed attributes. <br>
     * <b>Developers note</b>:<br>
     * Subclasses are encouraged to override this method to speed up fetching
     * multiple attributes as this method does a getAttribute call per
     * attribute.
     * 
     * @throws VrsException
     */
    public Attribute[] getAttributes(String names[]) throws VrsException
    {
        Attribute[] attrs = new Attribute[names.length];

        for (int i = 0; i < names.length; i++)
        {
            if (names[i] != null)
                attrs[i] = getAttribute(names[i]);
            else
                attrs[i] = null;
        }

        return attrs;
    }

    /**
     * Same as getAttributes(), but return the attributes in an (Ordened)
     * Attribute set.
     */
    public AttributeSet getAttributeSet(String names[]) throws VrsException
    {
        return new AttributeSet(getAttributes(names));
    }

    /**
     * Get non changeable (static) attributes. This is an attribute which can be
     * derived from the location or object type, and doesn't change during the
     * lifetime of the Object because it is implicit bound to the object class.
     * Even if the object doesn't exist the attribute can be determined, for
     * example the Resource Type of a VFile which doesn't change during the
     * lifetime of the (VFile) Object as this always must be "File" !
     */
    public Attribute getStaticAttribute(String name) throws VrsException
    {
        // by prefix values with "", a NULL value will be convert to "NULL".
        if (name.compareTo(ATTR_RESOURCE_TYPE) == 0)
            return new Attribute(name, getResourceType());
        else if (name.compareTo(ATTR_LOCATION) == 0)
            return new Attribute(name, getVRL());
        else if (name.compareTo(ATTR_NAME) == 0)
            return new Attribute(name, getName());
        else if (name.compareTo(ATTR_HOSTNAME) == 0)
            return new Attribute(name, getHostname());
        // only return port attribute if it has a meaningful value
        else if (name.compareTo(ATTR_PORT) == 0)
            return new Attribute(name, getPort());
        else if (name.compareTo(ATTR_ICONURL) == 0)
            return new Attribute(name, getIconURL());
        else if (name.compareTo(ATTR_SCHEME) == 0)
            return new Attribute(name, getScheme());
        else if (name.compareTo(ATTR_PATH) == 0)
            return new Attribute(name, getPath());
        else if ((name.compareTo(ATTR_URI_QUERY) == 0) && getLocation().hasQuery())
            return new Attribute(name, getQuery());
        else if ((name.compareTo(ATTR_URI_FRAGMENT) == 0) && getLocation().hasFragment())
            return new Attribute(name, getLocation().getFragment());
        else if (name.compareTo(ATTR_NAME) == 0)
            return new Attribute(name, getName());
        else if (name.compareTo(ATTR_LOCATION) == 0)
            return new Attribute(name, getLocation());
        else if (name.compareTo(ATTR_MIMETYPE) == 0)
            return new Attribute(name, getMimeType());

        return null;
    }

    /**
     * This is the single method a Node has to implement so that attributes can
     * be fetched. subclasses can override this method and do a
     * super.getAttribute first to check whether the superclass provides an
     * attribute name.
     */
    public Attribute getAttribute(String name) throws VrsException
    {
        if (name == null)
            return null;

        // Check Non-mutable attributes first!
        Attribute attr = this.getStaticAttribute(name);

        if (attr != null)
            return attr;

        // ===
        // VAttribute Interface for remote invokation
        // ===
        else if (name.compareTo(ATTR_ISCOMPOSITE) == 0)
            return new Attribute(name, (this instanceof VComposite));
        else if (name.compareTo(ATTR_RESOURCE_CLASS) == 0)
            return new Attribute(name, this.getClass().getCanonicalName());

        else if (name.compareTo(ATTR_RESOURCE_TYPES) == 0)
        {
            if (this instanceof VComposite)
            {
                String types[] = ((VComposite) this).getResourceTypes();
                StringList list = new StringList(types);
                return new Attribute(name, list.toString(","));
            }
            else
                return null;
        }
        else if (name.compareTo(ATTR_ISEDITABLE) == 0)
        {
            if (this instanceof VEditable)
                return new Attribute(name, ((VEditable) this).isEditable());
            else
                return new Attribute(name, false);
        }
        else if (name.compareTo(ATTR_ATTRIBUTE_NAMES) == 0)
        {
            StringList attrL = new StringList(this.getAttributeNames());
            return new Attribute(name, attrL.toString(","));
        }

        return null;
    }

    /** Return Query part of VRL */
    public String getQuery()
    {
        VRL loc = getLocation();

        if (loc == null)
            return null;

        return loc.getQuery();
    }

    /**
     * Return this node's location as String representation.<br>
     * Note that special characters are not encoded.
     */
    public String toString()
    {
        return "(" + getResourceType() + ")" + getLocation();
    }

    /**
     * Method for subclasses, a VRL should never change during the lifetime of
     * an object. May only be used during initialization.
     */
    protected void setLocation(VRL loc)
    {
        this._vrl = loc;
    }

    /** Compares whether the nodes represent the same location */
    public int compareTo(VNode other)
    {
        if (other == null)
            return 1; // this > null

        return _vrl.compareTo(other.getLocation());
    }

    /**
     * Returns optional icon URL given the preferred size. Default
     * implementation is to call getIconURL(). This method allows resources to
     * return different icons for different sizes. The actual displayed size in
     * the vbrowser may differ from the given size and the preferredSize should
     * be regarded as an indication. It is recommended to provide optimized
     * icons for sizes less than or equal to 16.
     */
    public String getIconURL(int preferredSize)
    {
        return getIconURL();
    }

    /** Returns optional icon url */
    public String getIconURL()
    {
        return null;
    }

    /**
     * Get Parent Node (if any).<br>
     * Default implementation is to open the location provided by
     * getParentLocation(). Override that method to provide the parent location
     * of this node. Overide this method to provide a more eficient way to
     * return a VNode that is the (logical) parent of this.
     */
    public VNode getParent() throws VrsException
    {
        VRL pvrl = getParentLocation();

        if (pvrl == null)
            return null;

        return vrsContext.openLocation(getParentLocation());
    }

    /**
     * Returns logical parent location of this node. By default this method
     * returns getVRL().getParent(); If an implementation has another 'logical'
     * parent then just the dirname of the current location, override this
     * method.
     */
    public VRL getParentLocation()
    {
        if (this.getVRL() == null)
            return null;

        return new VRL(this._vrl.getParent());
    }

    /**
     * Get Parents if the Node is part of a Graph. <br>
     * Returns one parent if Node is part of a Tree or null if Node has no
     * parents.
     * 
     * @throws VrsException
     */
    public VNode[] getParents() throws VrsException // for Graph
    {
        VNode parent = getParent();

        if (parent == null)
            return null;

        VNode nodes[] = new VNode[1];
        nodes[0] = parent;
        return nodes;
    }

    /**
     * Like cloneable, to use this method, implement the VDuplicatable interface
     * and override this method to return a copy. By default the object should
     * create a deep copy of it's contents and it's children. This default
     * behaviour is different then clone().
     */
    public VNode duplicate() // throws VrsException
    {
        throw new RuntimeException(new nl.esciencecenter.vlet.exception.NotImplementedException("Duplicate method not implemented"));
    }

    public VNode duplicate(boolean shallowCopy) // throws VrsException
    {
        throw new RuntimeException(new nl.esciencecenter.vlet.exception.NotImplementedException("Duplicate method not implemented"));
    }

    /**
     * Status String for nodes which implemented Status. Returns NULL if not
     * supported. This method is exposed in the toplevel VNode interface even if
     * not supported.
     * 
     * @return
     * @throws VrsException
     */
    public String getResourceStatus() throws VrsException
    {
        return null;
    }

    /**
     * Synchronize cached attributes and/or refresh cached attributes from
     * remote resource. This is an import method in the case that a resource
     * caches resource attributes, like file attributes.
     * 
     * @return - false : not applicable/not implemented for this resource.<br>
     *         - true : synchronize/refresh is supported and was successful.
     * @throws VrsException
     *             Is thrown when resource synchronization couldn't be performed. 
     */
    public boolean sync() throws VrsException
    {
        return false;
    }

    /** Fire attribute(s) changed event with this resource as even source. */
    protected void fireAttributesChanged(Attribute attrs[])
    {
        ResourceEvent event = ResourceEvent.createAttributesChangedEvent(getVRL(), attrs);
        this.vrsContext.getResourceEventNotifier().fire(event);
    }

    /** Fire attribute changed event with this resource as even source. */
    protected void fireAttributeChanged(Attribute attr)
    {
        ResourceEvent event = ResourceEvent.createAttributesChangedEvent(getVRL(), new Attribute[]
        { attr });
        this.vrsContext.getResourceEventNotifier().fire(event);
    }

    // ========================================================================
    // Abstract Interface
    // ========================================================================

    /** Returns resource type, if it has one */
    public abstract String getResourceType();

}
