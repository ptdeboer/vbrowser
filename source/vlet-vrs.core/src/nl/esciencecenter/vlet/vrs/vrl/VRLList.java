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

package nl.esciencecenter.vlet.vrs.vrl;

import java.io.Serializable;
import java.util.ArrayList;

import nl.esciencecenter.ptk.net.URIFactory;
import nl.esciencecenter.vbrowser.vrs.exceptions.VRLSyntaxException;
import nl.esciencecenter.vbrowser.vrs.vrl.VRL;

/**
 * VRLList.
 */
public class VRLList extends ArrayList<VRL> implements Cloneable, Serializable
{
    private static final long serialVersionUID = 5329294418747748112L;

    /** Construct VRL list from VRL array */
    public VRLList(VRL[] vrls)
    {
        if (vrls == null)
            return; // NIL list
        add(vrls);
    }

    public VRLList()
    {
        super();// =default;
    }

    public VRLList(VRL vrl)
    {
        super();
        add(vrl);
    }

    /**
     * Creates a ArraList with an capacity of num. Actual reported size() will
     * be 0! Only useful if the size is known in advance and the list size will
     * not change.
     * 
     * @param len
     */
    public VRLList(int capacity)
    {
        super(capacity);
    }

    /**
     * Concatenates String Array to one String using ',' (comma) as line
     * separator. <br>
     * Calls toString(",");
     */
    public String toString()
    {
        return toString(",");
    }

    /**
     * Concatenates String array using lineSeperator between lines
     */
    public String toString(String lineSeperator)
    {
        // two pass to alloc right size of target string
        int nrStrs = this.size();

        // allocate target size to speed up allocation.
        StringBuffer buf = new StringBuffer();

        // calculate target size
        for (int i = 0; i < nrStrs; i++)
        {
            buf.append(this.get(i));
            // add lineseperator
            if (i + 1 < nrStrs)
                buf.append(lineSeperator);
        }

        return buf.toString();
    }

    public VRL[] toArray()
    {
        VRL array[] = new VRL[size()];

        for (int i = 0; i < size(); i++)
            array[i] = this.get(i);

        return array;
    }

    /**
     * Remove elements from this list.
     * 
     * Polymorphism note: This method overrides remove(Object o) *only* for VRL
     * Arrays.
     */
    public void remove(VRL els[])
    {
        if (els == null)
            return;

        for (VRL el : els)
            this.remove(el);
    }

    /**
     * Add all elements. Does not check for duplicates. NULL objects are
     * omitted!
     */
    public void add(VRL[] els)
    {
        if (els == null)
            return;

        for (VRL el : els)
            if (el != null)
                this.add(el);
    }

    /** Add if not in the list already */
    public void addUnique(VRL el)
    {
        add(el, true);
    }

    /**
     * Add String, if unique==true the entry won't be added if it already
     * exists.
     */
    public boolean add(VRL vrl, boolean unique)
    {
        if (unique)
        {
            if (find(vrl) < 0)
            {
                add(vrl);
                return true;
            }
            return false;
        }
        else
        {
            add(vrl);
            return true;
        }
    }

    /**
     * Add Elements if NOT already in this list. Returns number of elements
     * really added
     */
    public int merge(VRL[] els)
    {
        if (els == null)
            return 0; // nothing to merge

        int numAdded = 0;

        for (VRL el : els)
        {
            if (el != null)
            {
                if (this.contains(el) == false)
                {
                    this.add(el);
                    numAdded++;
                }
            }
        }

        return numAdded;
    }

    /**
     * Finds specified VRL. uses VRL.equals() to find matching object. Uses
     * ArrayList<>indexOf().
     */
    public int find(VRL key)
    {
        return indexOf(key);
    }

    public VRLList duplicate()
    {
        return new VRLList(this.toArray());
    }

    public VRLList clone()
    {
        return duplicate();
    }

    /** Insert new value at position. */
    public void insert(int insertPosition, VRL value)
    {
        this.add(insertPosition, value);
    }

    /** Find VRL with specified hostname, ignores case! */
    public VRL findHostname(String host)
    {
        for (VRL vrl : this)
        {
            if (vrl.hasHostname(host))
                return vrl;
        }

        return null;
    }

    public static VRL[] parseVRLs(String uristr) throws VRLSyntaxException
    {
        if (uristr == null)
            return null;

        String strs[] = uristr.split(URIFactory.URI_LIST_SEPERATOR);
        VRL vrls[] = new VRL[strs.length];
        int index = 0;
        for (String str : strs)
        {
            VRL vrl;
            vrl = new VRL(str);

            vrls[index++] = vrl;
        }

        return vrls;
    }
}
