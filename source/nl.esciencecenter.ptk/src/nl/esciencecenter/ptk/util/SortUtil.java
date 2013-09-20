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

package nl.esciencecenter.ptk.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Collection of sort and other list manipulation methods 
 */
public class SortUtil
{

    public static class StringComparer implements Comparer<String>
    {
        public boolean ignoreCase = false;

        public int compare(String o1, String o2)
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
            String str1 = o1.toString();
            String str2 = o2.toString();

            return str1.compareToIgnoreCase(str2);
        }
    }

    public static class IntegerComparer implements Comparer<Integer>
    {
        @Override
        public int compare(Integer i1, Integer i2)
        {
            if (i1==null)
                if (i2==null)
                    return 0;
                else
                    return -1; // null > not null 
            
            return ((Integer)i1).compareTo((Integer)i2); 
        }
    }
    
    /** 
     * Sort by name. 
     */
    public static int[] sort(List<String> list, boolean ignoreCase)
    {
        if (list == null)
            return null;

        StringComparer comparer = new StringComparer();
        comparer.ignoreCase = ignoreCase;
        QSort<String> qsort = new QSort<String>(comparer);
        return qsort.sort(list);
    }
    
    /** 
     * In place sorting 
     */ 
    public static int[] sort(List<Integer> list) 
    {
        if (list == null)
            return null;

        IntegerComparer comparer = new IntegerComparer();
        QSort<Integer> qsort = new QSort<Integer>(comparer);
        
        return qsort.sort(list);
    }
    
    /** 
     * Returns set of unique integers from an Integer List. 
     * Set alreadySorted==true for already sorted lists. 
     * Implementation type of Set<> is LinkedHashSet. 
     */
    public static Set<Integer> unique(List<Integer> list,boolean alreadySorted)
    {
        if (list==null)
            return null;
        
        if (list.size()==0)
            return new LinkedHashSet<Integer>(0); 
            
        if (alreadySorted==false)
        {
            sort(list); 
        }
        
        Set<Integer> uniqueSet=new LinkedHashSet<Integer>();
        // first
        int k=list.get(0);
        uniqueSet.add(k); 
        
        for (int i=1;i<list.size();i++)
        {
            if (list.get(i)>k)
            {
                k=list.get(i);
                uniqueSet.add(k); 
            }
        }
        
        return uniqueSet;
    }
        
}
