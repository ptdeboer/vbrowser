//
// Copyright 2010-2011 Piter.NL
//
package nl.esciencecenter.vbrowser.vb2.ui.data;

 
import java.io.Serializable;
import java.util.Date;

import nl.esciencecenter.ptk.data.Duplicatable;
import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.exceptions.VRISyntaxException;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;



/**
 * This class provides a high level interface to resource Attributes.
 * <p> 
 * It is implemented using a {<code>type</code>, <code>name</code>,
 * <code>value</code>} triple, so that runtime type and name checking can 
 * be performed.<br>
 * The VAttributes does not do any type checking, so casting is possible. 
 * a getStringValue() after a setValue(int) will return the string representation of the int. 
 * Currently the attributes are stored as (Java) Object so this class is not
 * efficient for large binary attributes.<br>
 * <br>
 * TODO: add XML support. 
 * 
 * @author P.T. de Boer
 */

public class Attribute implements Cloneable, Serializable, Duplicatable<Attribute>// Triple<VAttributeType,String,Object>
{
    private static final long serialVersionUID = -8999238098979470171L;

    private static ClassLogger logger; 
	
	{
		logger=ClassLogger.getLogger(Attribute.class);  
	}
	
	private static String[] booleanEnumValues={"false","true"}; 

	// ========================================================================
	// Class Methods 
	// ========================================================================


	/** Create Attribute and get object type from object value*/
    public static Attribute create(String name, Object obj)
    {
        AttributeType newtype= AttributeType.getObjectType(obj,AttributeType.STRING); 
        Attribute attr=new Attribute(newtype,name,obj);
        //check? 
        return attr;
    }

	protected static Object stringValueToObject(AttributeType toType, String strValue) throws Exception
    {
		if (toType==null)
			return null; 
		
        switch(toType)
        {
            case BOOLEAN: 
                return Boolean.parseBoolean(strValue); 
            case INT: 
                return Integer.parseInt(strValue); 
            case LONG:
                return Long.parseLong(strValue);
            case FLOAT:
                return Float.parseFloat(strValue);
            case DOUBLE:
                return Double.parseDouble(strValue);
            case STRING:
                return strValue;
            case VRI: 
                try
                {
                    return new VRI(strValue);
                }
                catch (VRISyntaxException e)
                {
                    throw new Exception("Couldn't create VRI object.",e); 
                }
            case ENUM:
                return strValue; 
            case DATETIME:
                return strValue; //keep DateTime String "as is".    
            default:
                throw new Error("Cannot convert String:'"+strValue+"' to type:"+toType); 
        }
    }
    
    
    
	private static Object duplicateValue(AttributeType type,Object object)
	{
	    if (object==null)
	        return null; 
	    
		switch(type)
		{
            case BOOLEAN:
                return new Boolean((Boolean)object);
			case INT:
				return new Integer((Integer)object);
			case LONG:
				return new Long((Long)object); 
			case FLOAT:
				return new Float((Float)object); 
			case DOUBLE:
				return new Double((Double)object); 
			case ENUM: // enums are stored as String 
			case DATETIME: // normalized time string !  
			case STRING:
				return new String((String)object);
			case VRI: 
			    return ((VRI)object).duplicate(); 
			case ANY:
			default:
			{
				if (object instanceof Duplicatable)
					return ((Duplicatable)object).duplicate(false); 
					
				throw new Error("Cannot clone/duplicate value object:"+object); 
			}
		}
	}
	
	public static Attribute createAny(String name,Object obj)
	{
		return new Attribute(AttributeType.ANY,name,obj);
	}

	// ========================================================================
	// Class Methods 
	// ========================================================================

//	/** Create a deep copy of Attribute Array */
//	public static Attribute[] duplicateArray(Attribute[] attrs)
//	{
//		if (attrs == null)
//			return null;
//
//		Attribute newAttrs[] = new Attribute[attrs.length];
//
//		for (int i = 0; i < attrs.length; i++)
//		{
//			if (attrs[i] != null)
//				newAttrs[i] = attrs[i].clone();
//			else
//				newAttrs[i] = null; // be robuust: accept null attributes 
//		}
//
//		return newAttrs;
//	}
//
//	public static Attribute[] convertVectorToArray(
//			Vector<Attribute> attributes)
//	{
//
//		Attribute newAttrs[] = new Attribute[attributes.size()];
//
//		for (int i = 0; i < newAttrs.length; i++)
//		{
//			if (attributes.elementAt(i) != null)
//				newAttrs[i] = attributes.elementAt(i).clone();
//			else
//				newAttrs[i] = null; // be robuust: accept null attributes 
//		}
//
//		return newAttrs;
//	}
	// ========================================================================
	// Instance
	// ========================================================================

	/** The resolved type of the VAttribute */
	private AttributeType type = null;

	/** The name of the attribute */
	private String name = null;

	/**
	 * Generic Object as value. 
	 * The default is to use String Representation 
	 */ 
	private Object value=null;

	/** Whether attribute is editable.*/
	private boolean editable = false;

	/** List of enum values, index enumValue determines which enum value is set */
	private StringList enumValues = null;

	/** index into enumValues[] so that: enumAvalues[enumIndex]==value */
	private int enumIndex = 0;

	private boolean changed=false; 

	/** 
	 * Main init method to be called by other constructors. <br>
	 * This method may only be used by contructors. 
	 * Object value must be a PRIVATE copy! (deep copy) 
	 */
	protected void init(AttributeType type, String name, Object value)
	{
		this.type = type;
		this.name = name;
		this.value = value;
		checkValueType(type,value); 
	}

	private void checkValueType(AttributeType type,Object value)
	{
	    AttributeType objType=AttributeType.getObjectType(value,AttributeType.STRING);  
	    
        // DateTime is stored as String; 
        if (type==AttributeType.DATETIME && objType==AttributeType.STRING)
            return;
        
        // Same for ENUM  
        if (type==AttributeType.ENUM && objType==AttributeType.STRING)
            return; 
	    
        if (type==AttributeType.ANY)
        	return; 
        
	    if (objType!=type) 
	        throw new Error("Object type is not the same as expected. Expected="+type+",parsed="+objType); 
	}
	
	/** Initialize as Enum Type */
	protected void init(String name, StringList enumValues, int enumIndex)
	{
		this.name = name;
		this.type = AttributeType.ENUM;
		this.enumValues = enumValues;
		this.enumIndex = enumIndex;
		
		if ((enumValues!=null) && (enumIndex>=0) && (enumIndex<enumValues.size()) )
		{
			this.value = enumValues.get(enumIndex);
		}
		else
		{
			logger.errorPrintf("Error enumIndex out of bound:%d\n",enumIndex); 
			value="";
		}
	}

	
	
	
	// master setter: 
    private void _setValue(AttributeType type,Object object) 
    {
        this.checkValueType(type,object);
        this.type=type;
        this.value=object;
    }

	   
	/** Copy Constructor */
	public Attribute(Attribute source)
	{
	    copyFrom(source); 
	}

	protected Attribute(String name)
	{
	    this.name=name; 
	}
	
	protected void copyFrom(Attribute source)
	{
		// Duplicate String objects: !
		init(source.type, source.name,duplicateValue(source.type,source.value)); 
		
		this.editable = source.editable;
		this.enumIndex = source.enumIndex;
		this.enumValues = source.enumValues;
		this.changed = false; // new Attribute: reset 'changed' flag.  
	}

	/** Constructor to create a enum list of string */
	public Attribute(String name, String enumValues[], int enumVal)
	{
		init(name,new StringList(enumValues), enumVal);
	}

	   /** Constructor to create a enum list of string */
    public Attribute(String name, StringList enumValues, int enumVal)
    {
        init(name,enumValues, enumVal);
    }
    
    public Attribute(String name, Boolean val)
    {
        init(AttributeType.BOOLEAN,name,val); 
    }
    
    public Attribute(String name, Integer val)
    {
        init(AttributeType.INT,name, val); 
    }
    
    public Attribute(String name, Long val)
    {
        init(AttributeType.LONG,name,val); 
    }
    
    public Attribute(String name, Float val)
    {
        init(AttributeType.FLOAT,name,val); 
    }
    
    public Attribute(String name, Double val)
    {
        init(AttributeType.DOUBLE,name,val); 
    }
    
    public Attribute(String name, VRI vri)
    {
        init(AttributeType.VRI,name,vri); 
    }

	/** Create new Enumerated VAttribute with enumVals as possible values and
	 * defaultVal (which must be element of enumVals) as default */
	public Attribute(String name, String[] enumVals, String defaultVal)
	{
		int index = 0; // use default of 0! 

		if ((enumVals==null) || (enumVals.length<=0))
			throw new NullPointerException("Cannot not have empty enum value list !");
		StringList enums=new StringList(enumVals); 
		
		// robuustness! add defaultVal if not in enumVals! 
		enums.add(defaultVal,true); 
		
		index = enums.indexOf(defaultVal);

		if (index < 0)
			index = 0;

		init(name, enums, index);
	}

	/** Named String {Type,Value} Tuple */
	public Attribute(String name, String value)
	{
		init(AttributeType.STRING, name, value);
	}

    /** Custom named & explicit typed Attribute */
    public Attribute(AttributeType type, String name, Object value)
    {
        init(type, name, value);
    }

	public Attribute(String name, Date date)
	{
	    init(AttributeType.DATETIME,name,Presentation.createNormalizedDateTimeString(date));
	}

	/** 
	 * Return duplicate of this object. 
	 * This method returns the same class instead of the object.clone() method 
	 * All values are copied.  
	 * @return
	 */

	public Attribute clone()
	{
		return new Attribute(this);
	}

	public Attribute duplicate()
	{
		return clone();
	}

	//  =================================================================
	//  Instance Getters/Setters 
	//  =================================================================

	/** 
	 * Get Name of Attribute. 
	 * Note that the Name may never change during the lifetime of an VAttribute ! 
	 */
	public String getName()
	{
		return this.name;
	}

	// type+name should never change in the lifetime of an Attribute  
	/*private void setType(VAttributeType type)
	 {
	 this.type = type;
	 }*/

	/** 
	 * Get Type of Attribute. 
	 * Note that the Type may never change during the lifetime of an VAttribute !
	 */
	public AttributeType getType()
	{
		return type;
	}

	/**
	 * Return Actual Object Value. 
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * Explicit return value as String.
	 * Peforms toString() if object isn't a string type
	 */
	public String getStringValue()
	{ 
		if (value == null)
			return null; 
		
		if (value instanceof String)
			return (String)value; 
		
		return value.toString(); 
	}

	/** Get String list of enumeration types */
	public String[] getEnumValues()
	{
		if (type == AttributeType.ENUM)
		{
		    if (enumValues==null)
		        return null;
		    else
		        return enumValues.toArray();
		}
		
		
        if (type == AttributeType.BOOLEAN)
			return booleanEnumValues;

		return null;
	}

	/** Return enum order of current value */
	public int getEnumIndex()
	{
		if (type == AttributeType.ENUM)
			return enumIndex;

		if (type == AttributeType.BOOLEAN)
			return (getBooleanValue() ? 1 : 0);

		return 0;
	}

	public boolean hasEnumValue(String val)
	{
		return this.enumValues.contains(val);   
	}

	public boolean hasSetValue(String val)
	{
		// Set is Enum:
		return this.hasEnumValue(val); 
	}

	// Formatters/Stringifiers 

	/** For printing to stdout only. This is NOT a serializer */
	public String toString()
	{
		String enumStr = "";

		if (isEnumType())
		{
			enumStr = ",{";

			if (this.enumValues!=null)
				for (int i = 0; i < this.enumValues.size(); i++)
				{
					enumStr = enumStr + enumValues.get(i);
					if (i + 1 < enumValues.size())
						enumStr = enumStr + ",";
				}

			enumStr = enumStr + "}";
		}

		// convert to VAttribute Triplet
		return "{" + type + "," + name + "," + value + enumStr + ",["
		       + ((isEditable()) ? "E" : "") + ((hasChanged()) ? "C" : "")
		       + "]}";
	}
	
	/**
	 * Convert String value to desired type 
	 * @throws ValueException 
	 */ 
    public void setValueFromString(AttributeType type,String stringValue) throws Exception
    {
        this.value=stringValueToObject(type,stringValue);
        this.type=type; 
    }
    
    
    public void setValue(int intVal)  
    {
        _setValue(AttributeType.INT,new Integer(intVal));  
    }
    
    public void setValue(long longVal)  
    {
        _setValue(AttributeType.LONG,new Long(longVal));  
    }
    
    public void setValue(float floatVal)  
    {
        _setValue(AttributeType.FLOAT,new Float(floatVal));  
    }
    
    public void setValue(double doubleVal)  
    {
        _setValue(AttributeType.DOUBLE,new Double(doubleVal));  
    }
    
    /** Reset changed flag. */
	public void setNotChanged()
	{
		this.changed = false;
	}

	/** Whether the value has changed since the last setNotChanged() */
	public boolean hasChanged()
	{
		return changed;
	}

	/** 
	 * Return true if this VAttribute is editable. 
	 * This mean that the setValue() methods can be 
	 * used to change the value. 
	 * Note that VAttribute by default or NOT editable. 
	 * use setEditable() to change this. 
	 */
	public boolean isEditable()
	{
		return this.editable;
	}

	public void setEditable(boolean b)
	{
		this.editable = b;
	}

	public int getIntValue()
	{
		if (value==null)
			return 0; // by definition;  
		
		switch (this.type)
		{
			case INT:
				return ((Integer)value).intValue(); 
			case LONG:
				return ((Long)value).intValue(); 
			case FLOAT:
				return ((Float)value).intValue(); 
			case DOUBLE: 				
				return ((Double)value).intValue(); 
			default:
				return Integer.parseInt(getStringValue());
		}
	}

	public long getLongValue()
	{
		if (value==null)
			return 0; // by definition;  
		
		switch (this.type)
		{
			case INT:
				return ((Integer)value).longValue(); 
			case LONG:
				return ((Long)value).longValue(); 
			case FLOAT:
				return ((Float)value).longValue(); 
			case DOUBLE: 				
				return ((Double)value).longValue(); 
			default:
				return Long.parseLong(getStringValue());
		}
	}


	public float getFloatValue()
	{
		if (value==null)
			return Float.NaN; 

		switch (this.type)
		{
			case INT:
				return ((Integer)value).floatValue(); 
			case LONG:
				return ((Long)value).floatValue(); 
			case FLOAT:
				return ((Float)value).floatValue(); 
			case DOUBLE: 				
				return ((Double)value).floatValue(); 
			default:
				return Float.parseFloat(getStringValue()); // auto cast !
		}
	}

	public double getDoubleValue()
	{
		if (value==null)
			return Double.NaN; 
		
		switch (this.type)
		{
			case INT:
				return ((Integer)value).doubleValue(); 
			case LONG:
				return ((Long)value).doubleValue(); 
			case FLOAT:
				return ((Float)value).doubleValue(); 
			case DOUBLE: 				
				return ((Double)value).doubleValue(); 
			default:
				return Long.parseLong(getStringValue());
		}
	}

	public boolean getBooleanValue()
	{
		if (value==null)
			return false; // by definition 
		
		switch (this.type)
		{
			case INT:
				return (((Integer)value)!=0);  
			case LONG:
				return (((Long)value)!=0); 
			case FLOAT:
				return (((Float)value)!=0);  
			case DOUBLE: 				
				return (((Double)value).doubleValue()!=0);  
			default:
				return Boolean.parseBoolean(getStringValue());
		}
	}
	
	/** Ignore case only makes sense for String like Attributes */ 
	public int compareToIgnoreCase(Attribute attr2) 
	{
		return compareTo(attr2,true); 
	}
	
	public int compareTo(Attribute attr2) 
	{
		return compareTo(attr2,false); 
	}
	
	/**
	 * Compares this value to value of other VAttribute 'attr'.  
	 * The type of this attribute is used and the other
	 * attribute is converted (casted) to this type. 
	 * 
	 * @param attr
	 * @return
	 */
	public int compareTo(Attribute attr,boolean ignoreCase)
	{
	    // NULL comparison MUST match String compare !
		if (this.value == null)
		{
		    /// (this.value=null) < (other.value!=null)
			if ((attr != null) && (attr.getValue() != null))
				return -1;
			else
				return 0; // null equals null
		}

		switch (this.type)
		{
			case INT:
			case LONG:
			case DATETIME:
				// use long both for int,long and time (millis!) 
				if (this.getLongValue() < attr.getLongValue())
					return -1;
				else if (this.getLongValue() > attr.getLongValue())
					return 1;
				else
					return 0;
				//break;
			case FLOAT:
				if (this.getFloatValue() < attr.getFloatValue())
					return -1;
				else if (this.getFloatValue() > attr.getFloatValue())
					return 1;
				else
					return 0;
				//break;
			case DOUBLE:
				if (this.getDoubleValue() < attr.getDoubleValue())
					return -1;
				else if (this.getDoubleValue() > attr.getDoubleValue())
					return 1;
				else
					return 0;
			case STRING: 
			    return StringUtil.compare((String)value,attr.getStringValue()); 
			case VRI: 
			    return ((VRI)value).compareToObject(attr.getValue()); // use object compare
				//break;
			    
			default:
			{
				String s1=this.toString(); 
				String s2=attr.toString(); 
				
				// Default use string reprentation 
				if (attr.getValue() != null)
				{
					if (ignoreCase)
					{
						return s1.compareToIgnoreCase(s2);
					}
					else
					{
						return s1.compareTo(s2);
					}
				}
				else
					return 1; // this >> null
				//break;
			}
		}
	}

	public boolean hasName(String nname)
	{
		return (this.name.compareTo(nname) == 0);
	}

	public void setValue(boolean b) 
	{
	    _setValue(AttributeType.BOOLEAN,new Boolean(b)); 
	}
	
    public void setValue(AttributeType type, Object value)
    {
        _setValue(type,value); 
    }
    

	public Date getDateValue()
	{
	    if (this.value instanceof java.util.Date)
	        return (Date)this.value; 
	    
		return Presentation.createDateFromNormalizedDateTimeString(getStringValue());
	}

	public boolean isEnumType()
	{
		if (this.type==AttributeType.ENUM) 
			return true; 

		return false;
	}

    public static boolean isSectionName(String name)
    {
       if (name==null)
          return false;
       
       return ( name.startsWith("[") ||  name.startsWith("-["));
       
    }

	@Override
	public boolean shallowSupported() 
	{
		return false;
	}

	@Override
	public Attribute duplicate(boolean shallow) 
	{
		if (shallow)
			logger.warnPrintf("Asked for a shallow copy when this isn't supported\n"); 
		return new Attribute(this); 
	}


	/**
	 * Return as VRI. 
	 * Autocasts value to VRI object if possible. 
	 * Return null otherwise.  
	 * @throws VRISyntaxException 
	 */
    public VRI getVRI() throws VRISyntaxException
    {
        if (value==null)
            return null; 
        
        if (value instanceof VRI)
            return (VRI)value; 

        if (value instanceof String)
            return new VRI((String)value);
        
        return new VRI(value.toString()); 
        
    }

    public boolean hasSameType(Attribute other)
    {
        return (this.type==other.type); 
    }

    public void setObjectValue(Object newValue)
    {
        this.value=newValue; 
        this.type=AttributeType.getObjectType(newValue,AttributeType.STRING); 
    }

    public boolean isType(AttributeType type2)
    {
      return (this.type==type2); 
    }


}
