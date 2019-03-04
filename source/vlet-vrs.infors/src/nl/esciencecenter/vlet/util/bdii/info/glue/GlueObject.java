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

package nl.esciencecenter.vlet.util.bdii.info.glue;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;

/**
 * Generic Glue Object
 * 
 * @author Spiros Koulouzis, Piter T. de Boer
 * 
 */
public class GlueObject
{

    private String type;

    // private NamingEnumeration attributes;
//    private Map<String, Object> glueAttributes;

    private Map<String, Vector<Object>> extGlueAttributes;

    private String Uid;

    public GlueObject(String type, NamingEnumeration<? extends Attribute> attributes) throws NamingException
    {
        this.type = type;
        // this.attributes = attributes;
//        glueAttributes = new HashMap<String, Object>();

        extGlueAttributes = new HashMap<String, Vector<Object>>();

        initGlueObject(attributes);

    }

    private void initGlueObject(NamingEnumeration<? extends Attribute> attributes) throws NamingException
    {
        Vector<Object> glueValues;
        while (attributes.hasMore())
        {
            Attribute attrr = attributes.next();
            String id = attrr.getID();
            Object value = attrr.get();

            NamingEnumeration<?> enumer = attrr.getAll();
            glueValues = new Vector<Object>();
            // debug("All attributes for " + id);
            while (enumer.hasMoreElements())
            {
                //
                Object attr = enumer.next();
                // debug("\t\t\t\t" + attr);
                if (attr != null)
                {
                    glueValues.add(attr);
                }
            }
            if (!glueValues.contains(value))
            {
                glueValues.add(value);
            }

            extGlueAttributes.put(id, glueValues);

            // glueAttributes.put(id, value);
            if (id.endsWith("UniqueID"))
            {
                Uid = (String) value;
            }
            // sa don't really have UIDs. In this case to fix it we add the
            // chunkkey
            else if (id.endsWith("GlueChunkKey"))
            {
                String[] tmp = ((String) value).split("=");
                Uid = tmp[tmp.length - 1];
            }
            // debug(id + ": " + value);
        }
        // debug("----------------------");

    }

//    public Object getAttribute(String glueType)
//    {
//        return glueAttributes.get(glueType);
//    }

    public Vector<Object> getExtAttribute(String glueType)
    {
        return extGlueAttributes.get(glueType);
    }

    public Set<String> getExtGlueTypes()
    {
        return extGlueAttributes.keySet();
    }

    // public Set<String> getGlueTypes()
    // {
    // return glueAttributes.keySet();
    // }

    public String getType()
    {
        return this.type;
    }

    public String getUid()
    {
        return this.Uid;
    }

}
