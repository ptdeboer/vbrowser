//
// Copyright 2010-2011 Piter.NL
//
package nl.esciencecenter.vbrowser.vrs.data;

import java.net.URI;
import java.net.URL;
import java.util.Date;

/** 
 * Basic Attribute Types.  
 */
public enum AttributeType
{	
	ANY("Any",Object.class), //   
    BOOLEAN("Boolean",Boolean.class), 
    INT("Integer",Integer.class), 
    LONG("Long",Long.class), 
    FLOAT("Float",Float.class), 
    DOUBLE("Double",Double.class),
    STRING("String",String.class),
    /** Enum type stores it's values as String */ 
    ENUM("Enum",String.class),
    /** Store date time as unified string */ 
    DATETIME("Time",String.class), 
    VRI("VRI",nl.esciencecenter.ptk.net.VRI.class)
    ;
	
	// === // 
	
    private final String enumName;
    
    private final Class<? extends Object> storageClass;
	
	private AttributeType(String name,Class<?> storageClass)
	{
		this.enumName=name;
		this.storageClass=storageClass; 
	}

    public String getName()
    {
        return this.enumName; 
    }
    
    public Class<? extends Object> getStorageClass()
    {
        return this.storageClass;  
    }
	
	public static AttributeType getObjectType(Object object,AttributeType defaultType) 
    {
        if (object==null)
            return defaultType; 
        
        if (object instanceof Integer)
            return AttributeType.INT;
        
        if (object instanceof Boolean)
            return AttributeType.BOOLEAN;
        
        if (object instanceof Long)
            return AttributeType.LONG;
        
        if (object instanceof Float)
            return AttributeType.FLOAT;
        
        if (object instanceof Double)
            return AttributeType.DOUBLE;
        
        if (object instanceof String)
            return AttributeType.STRING;
        
        if (object instanceof Date)
            return AttributeType.DATETIME;
        
        if (object instanceof nl.esciencecenter.ptk.net.VRI)
            return AttributeType.VRI;
        
        if (object instanceof URL)
            return AttributeType.VRI;
        
        if (object instanceof URI)
            return AttributeType.VRI; 
        
        return defaultType; 
    }

}