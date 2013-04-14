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

package nl.esciencecenter.vbrowser.vrs.data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import java.util.Properties;

import nl.esciencecenter.ptk.data.IndexedHashtable;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.util.logging.ClassLogger;


/**
 *  An AttributeSet is implemented as an OrdenedHashtable with extra
 *  set manipulation methods.
 *  Note that the order of the entries in the Hashtable is now kept since 
 *  this class is a subclass of OrdenedHashtable (custom data type).
 *  <p>
 *  About the set() methods:<br>
 *  The set methods only add a new value (using put) to the Hashtable if 
 *  the Attribute object entry wasn't stored yet. 
 *  If the Attribute object already exists, the Value of that Attribute will
 *  be changed, keeping the original Attribute object in the Set.
 *  The Attribute has to be Editable.<br>
 *  This way it is possible to keep references to the stored Attribute for
 *  advanced manipulation methods.   
 *  
 *  @see IndexedHashtable
 */

public class AttributeSet extends IndexedHashtable<String,Attribute> 
    implements Serializable, Cloneable, Iterable<Attribute>
{
    // ========================================================================
    // Class
    // ========================================================================
    
    private static final long serialVersionUID = 1469139439009399990L;
    
         
    private static ClassLogger logger=null; 
    
    static
    {
        logger=ClassLogger.getLogger(AttributeSet.class); 
    }
    
    // === Public === 
    
    public static final String ATTR_SETNAME = "setName";
    
    
    /** Create AttributeSet from Properties */ 
    public static AttributeSet createFrom(Properties properties)
    {
        return new AttributeSet(properties); 
    }
    
    public static Vector<Attribute> createVector(Attribute[] attrs)
    {
        return new AttributeSet(attrs).toVector(); 
     }
    
    public static Vector<Object> createObjectVector(Attribute[] attrs)
    {
        return new AttributeSet(attrs).toObjectVector();
    }
    
    // ========================================================================
    // Instance
    // ========================================================================
    
    /** Optional set Name */ 
    protected String setName="<AttributeSet>"; 
    
    // === Constructor/Initializers === 
    
    protected void init(Vector<Attribute> attrs)
    {
       if (attrs==null)
       {
           this.clear(); 
           return; // empty set
       }
        
       for (Attribute attr:attrs)
           if ((attr!=null) && (attr.getName()!=null)) 
               this.put(attr.getName(),attr); 
    }
    
    protected void init(Attribute[] attrs)
    {
        if (attrs==null)
        {
            this.clear();
            return; // empty set
        }
        
       for (Attribute attr:attrs)
       { 
           if ((attr!=null) && (attr.getName()!=null)) 
               this.put(attr); 
       }
    }
    
    public AttributeSet()
    {
        super(); //empty hastable     
    }
    
    /** Named Attribute Set */ 
    public AttributeSet(String name)
    {
      super(); //empty hastable
      this.setName=name; 
    }
    
    /**
     * Create from Vector. Duplicate entries
     * are overwritten. Last entry is kept.
     */ 
    public AttributeSet(Vector<Attribute> attrs)
    {
        init(attrs); 
    }

    public AttributeSet(String nname, Attribute[] attrs)
    {
        setName(nname); 
        init(attrs); 
    }
    
    /**
     * Constructs an AttributeSet from the Map. 
     * Note that AttributeSet is a map as well, so this constructor
     * can be used as an Copy Constructor. 
     * 
     * @param map source map. 
     */
    public AttributeSet(Map<? extends Object,? extends Object> map)
    {
        init(map);
    }
   
    /**
     *  Create attribute set from generic <Key,Value> Map.   
     *  As key value, the STRING representation of the Key object
     *  is used. 
     *  As value the Attribute factory createFrom(object) is used.
     *  @see nl.uva.vlet.data.Attribute#createFrom(String, Object) 
     */
    private void init(Map<? extends Object,? extends Object> map)
    {
         int index=0; 
         
         Set<? extends Object> keys =map.keySet();

         for (Iterator<? extends Object> iterator = keys.iterator(); iterator.hasNext();) 
         {
            Object key = iterator.next();
            // Use STRING representation of Key Object ! 
            String keystr=key.toString(); 
            Object value=map.get(key);
            
            // Use VAtribute Factory: 
            Attribute attr = Attribute.create(keystr,value);
            this.put(attr); 
            
            index++; 
         }
    }
    
    /**
     * Create from Array. Duplicate entries
     * are overwritten. Last entry is kept, NULL entries are skipped. 
     */ 
    public AttributeSet(Attribute[] attrs)
    {
        if (attrs==null) 
            return; // empty set 
        
       for (Attribute attr:attrs)
           // filter bogus attributes! 
           if ((attr!=null) && (attr.getName()!=null)) 
               this.put(attr); 
    }

    /** Creates deep copy */ 
    public AttributeSet duplicate()
    {
        AttributeSet newset = new AttributeSet(this);
        newset.setName(this.getName()); 
        newset.setKeyOrder(this.getKeyArray(), true); 
        
        return newset; 
    }
    
    
    /** Creates deep copy */ 
     public AttributeSet clone()
     {
         return duplicate(); 
     }
     
    // ========================================================================
    // Getters/Setters/Putter/Adders
    // ========================================================================
  
    /** Sets optional name. null name is allowed */ 
    public void setName(String newName)
    {
        setName=newName; 
    }

    /** Returns optional name. Can be null */
    public String getName()
    {
        return setName; 
    }
    
    /**
     * Ordened Put.
     * This method will add the attribute to the hashtable 
     * and keep the order in which it is put. 
     * If the attribute already has been added the order
     * will be kept. 
     */  
    public void put(Attribute attr)
    {
    	if (attr==null)
    	{
    		logger.debugPrintf("put(): cannot put NULL attribute\n"); 
    		return; 
    	}
        this.put(attr.getName(),attr); 
    }
    
    /** Combined put() and setEditable() */ 
    public void put(Attribute attr, boolean editable)
    {
    	attr.setEditable(editable); 
    	this.put(attr); 
    }
    
    public void set(Attribute attribute)
    {
        this.put(attribute); 
    }

 
    /** Returns array of attribute names of the key set. */
    public String[] getAttributeNames()
    {
        return this.getKeyArray(); 
    }

    /**
     * Returns String value of Attribute with name 'name'
     * Returns null if the attribute is not in the set. 
     */ 
    public String getStringValue(String name)
    {
        Attribute attr=get(name);
        
        if (attr==null) 
        {
            logger.debugPrintf("*** Warning: null attribute for:%s\n",name); 
            return null;
        }
        else
        {
            logger.debugPrintf("Returning:%s=%s\n",name,attr.getValue());
        }
        
        return attr.getStringValue();
    }

    /**
     * Returns String value of Attribute with name 'name'
     * Returns null if the attribute is not in the set. 
     */ 
    public Object getValue(String name)
    {
        Attribute attr=get(name);
        
        if (attr==null) 
        {
            logger.debugPrintf("*** Warning: null attribute for:%s\n",name); 
            return null;
        }
        else
        {
            logger.debugPrintf("Returning:%s=%s\n",name,attr.getValue());
        }
        
        return attr.getValue();
    }
    
    /** 
     * Returns String value of Attribute 
     * @param defVal default value if attribute is not in this set */ 
    public int getIntValue(String name, int defVal)
    {
        Attribute attr=get(name);
        
        if (attr==null) 
            return defVal;
        
        return attr.getIntValue();
    }
    
    public long getLongValue(String name,long defVal)
    {
        Attribute attr=get(name);
            
        if (attr==null) 
            return defVal;
            
        return attr.getLongValue();
    }
    
    public VRI getVRIValue(String name) throws VRISyntaxException
    {
        Attribute attr=get(name);
        
        if (attr==null) 
            return null;
            
        return attr.getVRI();  // autocast to VRI 
    }
    
    /**
     * Returns String value of Attribute 
     * @param defVal default value if attribute is not in this set 
     */ 
    public int getIntValue(String name)
    {
        Attribute attr=get(name);
        
        if (attr==null) 
            return -1; 
        
        return attr.getIntValue();
    }
    
    public long getLongValue(String name)
    {
        Attribute attr=get(name);
            
        if (attr==null) 
            return -1; 
            
        return attr.getLongValue();
    }
    
    /**
     * Helper method used by the set() methods. 
     * @throws AttributeNotEditableException
     */ 
    private Object set(AttributeType type, String name, Object val)
    {
        Attribute orgAttr = this.get(name); 
        
        if (orgAttr==null)
        {
            // set: put new Editable Attribute with specified type: 
            Attribute attr = new Attribute(type,name,val);
            attr.setEditable(true);
            this.put(attr); 
            return null; 
        }
        else
        {
            // this method will change the Attribute 
            // and update the 'changed' flag
            Object orgval=orgAttr.getValue(); 
            orgAttr.setValue(type,val); 
            return orgval; 
        }
    }
    /**
     * Set Attribute Value. Returns previous value if any.  
     * The difference between put and set is that this method changes the 
     * stored Attribute in the hashtable by using Attribute.setValue(). 
     * It does NOT put a new Attribute into the hashtable. <br>
     * This means the already stored Attribute has to be editable !
     * This way the 'changed' flag is updated from the Attribute. 
     * If the named attribute isn't stored, a new attribute will be created
     * and the behaviour is similar to put().  
     */
    
    public String set(String name, String val) 
    {
        String oldvalue=getStringValue(name); 
        set(AttributeType.STRING,name,val);
        return oldvalue; 
    }
    
    /**
     * Set Attribute Value. Returns previous value if any.  
     * The difference between put and set is that this method changes the 
     * stored Attribute in the hashtable by using Attribute.setValue(). 
     * It does NOT put a new Attribute into the hashtable. <br>
     * This means the already stored Attribute has to be editable !
     * This way the 'changed' flag is updated from the Attribute. 
     * If the named attribute isn't stored, a new attribute will be created
     * and the behaviour is similar to put().  
     */ 
    public Boolean set(String attrName, boolean val)
    {
        return (Boolean)set(AttributeType.BOOLEAN,attrName,""+val); 
    }
    
    public Object setAny(String attrName, Object obj) 
    {
        return set(AttributeType.ANY,attrName,obj); 
    }

    /**
     * Set Attribute Value. Returns previous value if any.  
     * The difference between put and set is that this method changes the 
     * stored Attribute in the hashtable by using Attribute.setValue(). 
     * It does NOT put a new Attribute into the hashtable. <br>
     * This means the already stored Attribute has to be editable !
     * This way the 'changed' flag is updated from the Attribute. 
     * If the named attribute isn't stored, a new attribute will be created
     * and the behaviour is similar to put().   
     */ 
    public Integer set(String attrName, int val) 
    {
        //String prev=this.getValue(attrName); 
        return (Integer)set(AttributeType.INT,attrName,""+val); 
    }
    
    public boolean getBooleanValue(String name,boolean defaultValue)
    {
       Attribute attr=get(name);
            
       if (attr==null) 
          return defaultValue;  
            
       return attr.getBooleanValue();
    }
    
//    /**
//     * as XML file. 
//     */
//    public void storeAsXML(OutputStream outp, String comments) throws VlXMLDataException
//    {
//        XMLData xmlData=new XMLData();
//        xmlData.writeAsXML(outp,this,comments); 
//    }
        
    public String toString()
    {
        Attribute[] attrs = toArray(); 
    
        String str = "AttributeSet:"+this.setName+":{";
        
        if (attrs!=null)
            for (int i=0;i<attrs.length;i++)
            {
                str+=attrs[i]+(i<(attrs.length-1)?",":"");
            }
        
        str+="}";
        
        return str; 
    }

    /**
     * Stored new String Attribute, replacing already stored
     * Attribute if it already exists. 
     * @see #set(String, String) Use set() method to keep already stored Attributes.
     */  
    public void put(String name,String value) 
    {
        put(new Attribute(name,value));
    }
    
    public void put(String name, VRI vri)
    {
        put(new Attribute(name,vri));
    }

    /**
     * Stored new Integer Attribute, replacing already stored
     * Attribute if it already exists. 
     * @see #set(String, int) Use set() method to keep already stored Attributes.
     */  
    public void put(String attrName, int val)
    {
        put (new Attribute(attrName,val)); 
    }
    
    /**
     * Stored new boolean Attribute, replacing already stored
     * Attribute if it already exists. 
     * @see #set(String, boolean) Use set() method to keep already 
     *      stored Attributes.
     */  
    public void put(String attrName, boolean val)
    {
        put (new Attribute(attrName,val)); 
    }

    /** Returns changed attributes as array */ 
    public synchronized Attribute[] getChangedAttributesArray()
    {
        int numChanged=0; 
        int index=0; 
        
        for (int i=0;i<this.size();i++)
            if (this.elementAt(i).hasChanged()==true)
                numChanged++;
        
        Attribute attrs[]=new Attribute[numChanged];
        
        for (int i=0;i<this.size();i++)
            if (this.elementAt(i).hasChanged()==true)
                attrs[index++]=this.elementAt(i);
        
        return attrs; 
    }

    /** Set Editable flag of attribute */ 
    public void setEditable(String name, boolean val)
    {
        Attribute attr = this.get(name);
        
        if (attr==null)
            return;
        
        attr.setEditable(val); 
    }

    public StringList getOrdenedKeyList()
    {
        return new StringList(this.getKeyArray());
    }

    /**
     * De-Generalized remove method (downcast) 
     * for strict type matching
     */  
    public Attribute remove(String name)
    {
        return super.remove((Object)name); 
    }

    /** Remove attribute if name isn't in the key list */ 
    public void removeIfNotIn(StringList keylist)
    {
        StringList names=this.getOrdenedKeyList(); 

        // match current attribute against newlist; 
        for (String name:names)
        {
            if (keylist.contains(name)==false)
            {
                this.remove(name); 
            }
        }
    }

   /**
    * Match this AttributeSet with template set. 
    * If attribute in templateSet is not in this set, it will be copied.   
    * If attribute exists in this set, it's type and flags will be copied, but
    * not the actual value unless it is NULL or empty. 
    * This allows updating of AttributeSet while keeping their value. 
    * Set boolean remoteOthers to true to remove attribute not in the template set. 
    * Is used by ServerInfo to update Server Attributes. 
    */ 
   public void matchTemplate(AttributeSet templateSet, boolean removeOthers)
   {
       StringList names=templateSet.getOrdenedKeyList(); 

       for (String name:names)
       {
          if (this.containsKey(name)==false) 
          {
              this.put(templateSet.get(name)); 
          }
          else
          {
              // update attribute type and attribute flags. 
              // just copy old value into new Attribute 
              Attribute tmplAttr=templateSet.get(name); 
              Attribute oldAttr=get(name);
              
              if (tmplAttr.hasSameType(oldAttr))
              {
                  // attribute exists and has same type: keep! 
              }
              else
              {
                  // copy old value but use new type and attribute
                  tmplAttr.setValue(tmplAttr.getType(),oldAttr.getValue());
                  put(tmplAttr);
              }
          }
       }

       if (removeOthers) 
           removeIfNotIn(names); 
   }

   /** Returns sub set of attributes */ 
   public AttributeSet getAttributes(String[] names)
   {
       AttributeSet subset=new AttributeSet(); 
       for (String name:names)
       {    
           Attribute attr=this.get(name);
            if (attr!=null)
                subset.put(attr.duplicate()); 
       }
       return subset; 
   }

   public void add(Attribute[] attrs)
   {
       for (Attribute attr:attrs)
            this.put(attr); 
   }


 

}

