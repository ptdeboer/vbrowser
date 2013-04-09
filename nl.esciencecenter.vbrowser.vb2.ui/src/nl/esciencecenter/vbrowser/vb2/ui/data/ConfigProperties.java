package nl.esciencecenter.vbrowser.vb2.ui.data;

import java.util.Properties;

import nl.esciencecenter.ptk.util.StringUtil;

public class ConfigProperties 
{
	// Optional Parent!
    final ConfigProperties parent; 

	Properties properties=new Properties(); 

    public ConfigProperties(ConfigProperties parent)
    {
        this.parent=parent; 
    }
    
    public ConfigProperties()
    {
        this.parent=null; 
    }
    
	public String getProperty(String name)
	{
		String val=this.properties.getProperty(name); 
		if ((val==null) && (parent!=null)) 
			val=parent.getProperty(name); 
		return val;
	}

	public String getProperty(String propName,String defaultValue) 
	{
		String val=getProperty(propName); 
		if (val==null)
			return defaultValue; 
		return val;
	}
	
	public int getIntProperty(String propName,int defaultValue) 
	{
		String val=getProperty(propName); 
		if (StringUtil.isWhiteSpace(val))
			return defaultValue; 
		return Integer.parseInt(val); 
	}

	public boolean getBoolProperty(String propName,boolean defaultValue) 
	{
		String val=getProperty(propName); 
		if (StringUtil.isWhiteSpace(val)==false)
			return defaultValue; 
		return Boolean.parseBoolean(val); 
	}

}
