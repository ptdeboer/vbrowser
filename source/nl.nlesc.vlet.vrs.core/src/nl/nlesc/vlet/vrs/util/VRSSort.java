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

package nl.nlesc.vlet.vrs.util;

import nl.esciencecenter.ptk.util.Comparer;
import nl.esciencecenter.ptk.util.QSort;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.nlesc.vlet.data.VAttribute;
import nl.nlesc.vlet.vrs.VNode;

/**
 * Sort VRSNodes. 
 */
public class VRSSort<Type> extends QSort<Type>
{

    public VRSSort(Comparer<Type> comparer)
    {
        super(comparer);
    }

    public static class VAttributeComparer implements Comparer<VAttribute>
    {
        public boolean ignoreCase = false;

        public int compare(VAttribute a1, VAttribute a2)
        {
            if (a1 == null)
                if (a2 == null)
                    return 0;
                else
                    return -1;
            else if (a2 == null)
                return 1;
            else
                ; // continue

            if (ignoreCase)
            {
                return a1.getName().compareToIgnoreCase(a2.getName());
            }
            else
            {
                return a1.getName().compareTo(a2.getName());
            }
        }
    }

    public static abstract class VNodeComparer implements Comparer<VNode>
    {

    }

    public static class VNodeTypeNameComparer extends VNodeComparer
    {
        boolean ignoreCase = false;

        boolean typeFirst = false;

        int typeDirection = 1; // set to -1 for inverse Type

        int nameDirection = 1; // set to -1 for inverse name

        public VNodeTypeNameComparer(boolean typeFirst, boolean ignoreCase)
        {
            this.ignoreCase = ignoreCase;
            this.typeFirst = typeFirst;
        }

        public int compare(VNode n1, VNode n2)
        {
            if (n1 == null)
                if (n2 == null)
                    return 0;
                else
                    return -1;
            else if (n2 == null)
                return 1;
            else
                ; // continue


            if (typeFirst == true)
            {
                int res = StringUtil.compare(n1.getResourceType(), n2.getResourceType());

                if (res != 0)
                    return res * typeDirection;

                // else compare same type nodes !
            }

            if (ignoreCase)
            {
                return nameDirection * n1.getName().compareToIgnoreCase(n2.getName());
            }
            else
            {
                return nameDirection * n1.getName().compareTo(n2.getName());
            }
        }
    }

    public static class VNodeAttributeComparer extends VNodeComparer
    {
        public boolean ignoreCase = false;

        public String[] sortFields;

        public int directions[];// sort direction of above sort fields;

        public VNodeAttributeComparer(String[] sortFields, boolean ignoreCase)
        {
            this.ignoreCase = ignoreCase;
            this.sortFields = sortFields;
        }

        public int compare(VNode o1, VNode o2)
        {
            if (o1 == null)
                if (o2 == null)
                    return 0;
                else
                    return -1;
            else if (o2 == null)
                return 1;
            else
                ; // continue

            VNode n1 = o1;
            VNode n2 = o2;

            int result = 0;
            int i = 0;
            int n = sortFields.length;
            // compare attributes while values are equal
            while ((result == 0) && (i < n))
            {
                VAttribute a1;
                VAttribute a2;

                try
                {
                    a1 = n1.getAttribute(sortFields[i]);
                    a2 = n2.getAttribute(sortFields[i]);
                }
                catch (Exception e)
                {
                    //Global.warnPrintf(this, "Error during sort. Got exception while fetching attribute:%s\n", e);
                    // throw new
                    // Error("Fatal: Exception while fetching attribute:"+e,e);
                    return 0;
                }

                if (a1 == null)
                    if (a2 == null)
                        result = 0;
                    else
                        result = -1;
                else if (a2 == null)
                    result = 1;
                else if (ignoreCase)
                    result = a1.compareToIgnoreCase(a2);
                else
                    result = a1.compareTo(a2);

                i++;
            }

            return result;
        }

    }

    private static VAttributeComparer vattributeComparer = new VAttributeComparer();

    public static void sortVAttributes(VAttribute[] attrs)
    {
        if (attrs == null)
            return;

        VRSSort<VAttribute> qsort = new VRSSort<VAttribute>(vattributeComparer);
        qsort.sort(attrs);
    }

    /** Sort by name. Optionally sort by type first, then by name */
    public static void sortVNodesByTypeName(VNode[] nodes, boolean typeFirst, boolean ignoreCase)
    {
        if (nodes == null)
            return;

        VNodeComparer comparer = new VNodeTypeNameComparer(typeFirst, ignoreCase);
        // comparer.ignoreCase=ignoreCase;
        // comparer.sortFields=sortFields;
        VRSSort<VNode> qsort = new VRSSort<VNode>(comparer);
        qsort.sort(nodes);
    }

    /** Sort by name. Optionally sort by type first, then by name */
    public static void sortVNodes(VNode[] nodes, String[] sortFields, boolean ignoreCase)
    {
        if (nodes == null)
            return;

        VNodeComparer comparer = new VNodeAttributeComparer(sortFields, ignoreCase);
        // comparer.ignoreCase=ignoreCase;
        // comparer.sortFields=sortFields;
        VRSSort<VNode> qsort = new VRSSort<VNode>(comparer);
        qsort.sort(nodes);
    }
    
}
