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

package test.data;

import nl.esciencecenter.vbrowser.vrs.data.Attribute;
import nl.esciencecenter.vbrowser.vrs.data.AttributeSet;
import nl.esciencecenter.vlet.exception.XMLDataParseException;
import nl.esciencecenter.vlet.vrs.data.xml.XMLData;

import org.w3c.dom.Document;


public class testXMLData
{
	public static XMLData xmlData=new XMLData(); 
	
	public static void main(String args[])
	{
		try
		{
			test1();
			test2(); 
			
			xmlData.setVAttributeSetElementName("ServerInfo");
			xmlData.setVAttributeElementName("server.property");
			
			test2(); 
			
			
		}
		catch (Exception e)
		{
			System.err.println("Exception:"+e) ; 
			e.printStackTrace();
		}
	}
	
	public static void test1() throws XMLDataParseException 
	{
		Attribute attr=new Attribute("AttributeName","AttributeValue");
		
		String str=createXMLString(attr); 
		
		println("Simple attr XML=\n----\n"+str+"---");
		
		String enumstrs[]={"Aap","Noot","Mies"}; 
		Attribute enumattr=new Attribute("EnumAttributeName",enumstrs,0); 
		
		str=createXMLString(enumattr); 
		println("Enum attr XML=\n----\n"+str+"---");
		
		
	}
	
	private static String createXMLString(Attribute attr) throws XMLDataParseException 
	{
		Document doc=xmlData.createDefaultDocument(); 
		doc.appendChild(xmlData.createXMLNode(doc, attr)); 
		return xmlData.createXMLString(doc); 
	}
	
	private static String createXMLString(AttributeSet attrSet) throws XMLDataParseException
	{
		Document doc=xmlData.createDefaultDocument(); 
		doc.appendChild(xmlData.createXMLNode(doc, attrSet)); 
		return xmlData.createXMLString(doc); 
	}


	public static void test2() throws XMLDataParseException
	{
		AttributeSet set=new AttributeSet("Test-Set");
		
		set.put("boolval1", true); 
		
		set.put("stringval1", "string-value1");
		
		String enumstrs[]={"Aap","Noot","Mies"}; 
		Attribute enumattr=new Attribute("EnumAttributeName",enumstrs,0); 
		set.put(enumattr);
		
		String str=createXMLString(set); 
		println("VAttributeSet 1=\n----\n"+str+"---");
		
		AttributeSet newAttrSet = xmlData.parseVAttributeSet(str);  
	
		println("VAttributeSet reread=\n----\n"+newAttrSet+"---");
	}

	private static void println(String msg)
	{
		System.out.println(msg); 
	}
}
