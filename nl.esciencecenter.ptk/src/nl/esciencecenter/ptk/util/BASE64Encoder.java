/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.esciencecenter.ptk.util;

import java.io.IOException;

/**
 * sun.misc.BASE64Encoder API compatible class. 
 * @deprecated use javax.xml.bind.DatatypeConverter
 */
public class BASE64Encoder 
{

	public String encode(byte[] bytes)
	{
	    return javax.xml.bind.DatatypeConverter.printBase64Binary(bytes);
	}
	
	public byte[] decode(String base64) throws IOException	
	{
	    return javax.xml.bind.DatatypeConverter.parseBase64Binary(base64);
	}
 
	
}
