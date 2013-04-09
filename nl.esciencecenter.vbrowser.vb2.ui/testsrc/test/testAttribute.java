package test;

import java.util.Date;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;

import nl.esciencecenter.ptk.data.StringList;
import nl.esciencecenter.ptk.net.VRI;
import nl.esciencecenter.ptk.presentation.Presentation;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.vbrowser.vb2.ui.data.Attribute;
import nl.esciencecenter.vbrowser.vb2.ui.data.AttributeType;


/**
 * jUnit test created to test the refactoring of Attribute. 
 * 
 * @author P.T. de Boer. 
 */
public class testAttribute 
{
    final static Random rnd=new Random(0); 

    private String createRandomString(int size)
    {
        StringBuffer strbuf=new StringBuffer(size);
        for (int i=0;i<size;i++)
        {
            int val=rnd.nextInt();
            char c=(char)('a'+val%31); 
            strbuf.append(c); 
        }
        
        String str=strbuf.toString(); 
        return str; 
    }
    
    private StringList createRandomStringList(int listSize,int stringSize)
    {
        String array[]=new String[listSize]; 
        for (int i=0;i<listSize;i++)
            array[i]=createRandomString(stringSize);
         
        return new StringList(array); 
    }


    
    // AttributeType ::= {BOOLEAN,INT,LONG,FLOAT,DOUBLE,STRING,ENUM,VRI,TIME} 
    
    class TestValues
    {
        boolean  boolval=true; 
        int      intval=1; 
        long     longval=1024*1024*1024;
        float    floatval=1.13f; 
        double   doubleval=Math.PI;  
        String    strval="String Value"; 
        String    enumstrs[]={"aap","noot","mies"}; 
        String    datestr="1970-01-13 01:23:45.678"; 
        VRI       vrl=newVRI("file:","user","localhost",-1,"/tmp/stuff/","query","fragment"); 
        Date      dateval=Presentation.createDateFromNormalizedDateTimeString(datestr); 
                
        TestValues()
        {
            
        }
        
        TestValues(boolean _boolval,
                   int _intval,
                   long _longval, 
                   float _floatval, 
                   double _doubleval,
                   String _strval,
                   String[] _enumvals,
                   VRI _vrl,
                   Date _dateval)
        {
            this.boolval=_boolval;
            this.intval=_intval; 
            this.longval=_longval; 
            this.floatval=_floatval; 
            this.doubleval=_doubleval; 
            this.strval=_strval; 
            this.enumstrs=_enumvals;
            this.vrl=_vrl; 
            this.dateval=_dateval; 
            this.datestr=Presentation.createNormalizedDateTimeString(_dateval);
        }
    }
    
    protected void setUp()
    {
    }

    // Tears down the tests fixture. (Called after every tests case method.)
    protected void tearDown()
    {
    }
    
    @Test
    public void testPresentationDateTimeString() 
    {
        // text exception: 
        //testPresentationDateTimeString("000000-00-00 00:00:00.000");
        doPresentationDateTimeString("0001-01-01 00:00:00.000");
        doPresentationDateTimeString("1970-01-13 01:23:45.678");  
        doPresentationDateTimeString("999999-12-31 23:59:59.999");  
    }
    
    public void doPresentationDateTimeString(String datestr)
    {
        Date date=Presentation.createDateFromNormalizedDateTimeString(datestr);
        String reversestr=Presentation.createNormalizedDateTimeString(date); 
        Assert.assertEquals("Normalized datetime strings should be the same",datestr,reversestr); 
    }
    
    @Test 
    public void testValueConstructors()
    {
        TestValues tValues=new TestValues(); 
        doTestValueConstructors(tValues); 
        
        tValues=new TestValues(
                true,
                (int)1, 
                (long)1024*1024*1024,
                (float)1.13f, 
                (double)Math.PI,
                "String Value",     
                new String[]{"aap","noot","mies"},
                newVRI("file:","user","localhost",-1,"/tmp/stuff/","query","fragment"),    
                Presentation.createDateFromNormalizedDateTimeString("1970-01-13 01:23:45.678")
                );
        doTestValueConstructors(tValues); 

        // empty: neutral (legal) 
        tValues=new TestValues(
                false,
                (int)0, 
                (long)0,
                (float)0, 
                (double)0,
                "",     
                new String[]{""},
                newVRI("ref:",null,null,0,"",null,null),    
                Presentation.createDateFromNormalizedDateTimeString("0001-01-01 00:00:00.000")
                );
        doTestValueConstructors(tValues); 

        // minimum/null (allowed) 
        tValues=new TestValues(
                false,
                (int)Integer.MIN_VALUE, 
                (long)Long.MIN_VALUE,
                (float)Float.MIN_VALUE, 
                (double)Double.MIN_VALUE,
                null,     
                new String[]{null},
                newVRI(null,null,null,0,null,null,null),    
                Presentation.createDate(1)
                );
        
        doTestValueConstructors(tValues); 
        
        // max (allowed) 
        tValues=new TestValues(
                true,
                (int)Integer.MAX_VALUE, 
                (long)Long.MAX_VALUE,
                (float)Float.MAX_VALUE, 
                (double)Double.MAX_VALUE,
                createRandomString(1024),     
                createRandomStringList(1024,1024).toArray(),
                newVRI("scheme:",
                        "Jan.Piet.Joris[Groupid-dev-0]",
                        "www.llanfairpwllgwyngyllgogerychwyrndrobwyll-llantysiliogogogoch.com",
                        99999,
                        "/tmp/llanfairpwllgwyngyllgogerychwyrndrobwyll-llantysiliogogogoch/With Space",
                        "aap=AapValue&noot=NootValue&mies=MiewValue",
                        "fragment"), 
                        Presentation.createDateFromNormalizedDateTimeString("0001-01-01 00:00:00.000")
                );
        
        doTestValueConstructors(tValues); 
    }

  
    private VRI newVRI(String string, String string2, String string3, int i, String string4, String string5,
            String string6)
    {
       return  new VRI("file:","user","localhost",-1,"/tmp/stuff/","query","fragment");
    }

    public void doTestValueConstructors(TestValues tValues)
    {
        double epsd=Double.MIN_NORMAL; 
        float epsf=Float.MIN_NORMAL; 
        
        // Core Types 
        Attribute boolAttr=new Attribute("boolname",tValues.boolval); 
        Assert.assertEquals("boolean value doesn't match",tValues.boolval,boolAttr.getBooleanValue());
        Assert.assertEquals("boolean type expected",AttributeType.BOOLEAN,boolAttr.getType()); 
        Attribute intAttr=new Attribute("intname",tValues.intval); 
        Assert.assertEquals("int value doesn't match",tValues.intval,intAttr.getIntValue());
        Assert.assertEquals("int type expected",AttributeType.INT,intAttr.getType()); 
        Attribute longAttr=new Attribute("longname",tValues.longval); 
        Assert.assertEquals("long value doesn't match",tValues.longval,longAttr.getLongValue());
        Assert.assertEquals("long type expected",AttributeType.LONG,longAttr.getType()); 
        Attribute floatAttr=new Attribute("floatname",tValues.floatval); 
        Assert.assertEquals("float value doesn't match",tValues.floatval,floatAttr.getFloatValue(),epsf);
        Assert.assertEquals("float type expected",AttributeType.FLOAT,floatAttr.getType()); 
        Attribute doubleAttr=new Attribute("doublename",tValues.doubleval); 
        Assert.assertEquals("double value doesn't match",tValues.doubleval,doubleAttr.getDoubleValue(),epsd);
        Assert.assertEquals("double type expected",AttributeType.DOUBLE,doubleAttr.getType()); 
        Attribute strAttr=new Attribute("strname",tValues.strval); 
        Assert.assertEquals("String value doesn't match",tValues.strval,strAttr.getStringValue());
        Assert.assertEquals("String type expected",AttributeType.STRING,strAttr.getType());
        for (int i=0;i<tValues.enumstrs.length;i++)
        {
            Attribute enumAttr=new Attribute("enumname",tValues.enumstrs,tValues.enumstrs[i]); 
            Assert.assertEquals("Enum value #"+i+" doesn't match",tValues.enumstrs[i],enumAttr.getStringValue());
            Assert.assertEquals("Enum type expected",AttributeType.ENUM,enumAttr.getType()); 
        }
        
        // uses Presentation class for Date <-> String conversion ! 
        Attribute dateAttr=new Attribute("datename",tValues.dateval); 
        Assert.assertEquals("Date value doesn't match",tValues.datestr,dateAttr.getStringValue());
        Assert.assertEquals("Date type expected",AttributeType.DATETIME,dateAttr.getType());
      
    }
    
    @Test 
    public void testNullConstructors()
    {
        // NULL value with NULL type default to String
        Attribute attr=new Attribute((String)null,(String)null);
        Assert.assertEquals("NULL attribute should return NULL",null,attr.getStringValue());  
        Assert.assertEquals("NULL value defaults to StringType",AttributeType.STRING, attr.getType());
    }
 
    @Test 
    public void testStringValueConstructors() throws Exception
    {   
        // test simple String based constructors and match against object value 
        doTestStringValueConstructor(AttributeType.BOOLEAN,"boolean1","true",new Boolean(true));
        doTestStringValueConstructor(AttributeType.BOOLEAN,"boolean2","false",new Boolean(false));
        doTestStringValueConstructor(AttributeType.BOOLEAN,"boolean3","True",new Boolean(true));
        doTestStringValueConstructor(AttributeType.BOOLEAN,"boolean4","False",new Boolean(false));
        doTestStringValueConstructor(AttributeType.BOOLEAN,"boolean5","TRUE",new Boolean(true));
        doTestStringValueConstructor(AttributeType.BOOLEAN,"boolean6","FALSE",new Boolean(false));
        //
        doTestStringValueConstructor(AttributeType.INT,"integer1","0", new Integer(0));
        doTestStringValueConstructor(AttributeType.INT,"integer2","1", new Integer(1));
        doTestStringValueConstructor(AttributeType.INT,"integer3","-1", new Integer(-1));
        doTestStringValueConstructor(AttributeType.INT,"integer4",""+Integer.MAX_VALUE,new Integer(Integer.MAX_VALUE));  
        doTestStringValueConstructor(AttributeType.INT,"integer5",""+Integer.MIN_VALUE,new Integer(Integer.MIN_VALUE));  
        doTestStringValueConstructor(AttributeType.LONG,"long1","0",new Long(0));
        doTestStringValueConstructor(AttributeType.LONG,"long2","1",new Long(1));
        doTestStringValueConstructor(AttributeType.LONG,"long3","-1",new Long(-1));
        doTestStringValueConstructor(AttributeType.LONG,"long4",""+Long.MAX_VALUE,new Long(Long.MAX_VALUE));  
        doTestStringValueConstructor(AttributeType.LONG,"long5",""+Long.MIN_VALUE,new Long(Long.MIN_VALUE));  
        // watch out for rounding errors from decimal to IEEE floats/doubles !
        doTestStringValueConstructor(AttributeType.FLOAT,"float1","0.0",new Float(0.0));
        doTestStringValueConstructor(AttributeType.FLOAT,"float2","1.0",new Float(1.0));
        doTestStringValueConstructor(AttributeType.FLOAT,"float3","-1.0",new Float(-1.0));
        doTestStringValueConstructor(AttributeType.FLOAT,"float4",""+Float.MAX_VALUE,new Float(Float.MAX_VALUE));
        doTestStringValueConstructor(AttributeType.FLOAT,"float5",""+Float.MIN_VALUE,new Float(Float.MIN_VALUE));
        // todo: check rounding errors
        doTestStringValueConstructor(AttributeType.DOUBLE,"double1","0.0",new Double(0.0));
        doTestStringValueConstructor(AttributeType.DOUBLE,"double2","1.0",new Double(1.0));
        doTestStringValueConstructor(AttributeType.DOUBLE,"double3","-1.0",new Double(-1.0));
        doTestStringValueConstructor(AttributeType.DOUBLE,"double4","-1.123456",new Double(-1.123456));
        doTestStringValueConstructor(AttributeType.DOUBLE,"double5",""+Double.MAX_VALUE,new Double(Double.MAX_VALUE));
        doTestStringValueConstructor(AttributeType.DOUBLE,"double6",""+Double.MIN_VALUE,new Double(Double.MIN_VALUE));
        // STRING
        doTestStringValueConstructor(AttributeType.STRING,"string1","value","value");
        doTestStringValueConstructor(AttributeType.STRING,"string2","","");
        // allow NULL 
        doTestStringValueConstructor(AttributeType.STRING,"string3",null,null);
        
        // DATETIME
        long millies=System.currentTimeMillis(); 
        Date dateVal=Presentation.createDate(millies); 
        
        //doTestStringValueConstructor(AttributeType.DATETIME,"name",Presentation.createNormalizedDateTimeString(millies),dateVal);  
        
        doTestStringValueConstructor(AttributeType.DATETIME,"name",dateVal.toString(),dateVal);  

        // VRI. Use *normalized* URIs:
        String vrlStr="file:/local/path/to/file.ext";
        VRI vrl=new VRI(vrlStr);
        doTestStringValueConstructor(AttributeType.VRI,"name",vrlStr,vrl);

        
        // VRI 
        vrlStr="http://user@host.domain:1234/Directory/Path";
        vrl=new VRI(vrlStr);
        doTestStringValueConstructor(AttributeType.VRI,"name",vrlStr,vrl);

        // VRI 
        vrlStr="http://user@host.domain:1234/Directory/AFile?query#frag";
        vrl=new VRI(vrlStr);
        doTestStringValueConstructor(AttributeType.VRI,"name",vrlStr,vrl);

    }
    
    public void doTestStringValueConstructor(AttributeType type,String name,String strValue,Object objectValue) 
    {
        // basic constructor tests 
        Attribute attr=Attribute.create(name, objectValue); // Create From Object. Must match actual type!
        
        // check type,name and String value 
        Assert.assertEquals("Type must be:"+type,type,attr.getType());
        
        boolean ignoreCase=false;
        
        if (objectValue instanceof Boolean)
            ignoreCase=true; 
        
        if (ignoreCase==false)
            Assert.assertEquals("String values should match (if parsed correctly) for type:"+type,strValue,attr.getStringValue());
        else
            assertEquals("String values should match (if parsed correctly) for type:"+type,strValue,attr.getStringValue(),ignoreCase); 
        
        Assert.assertEquals("Attribute name must match",name,attr.getName()); 
        Assert.assertTrue("isType() must return true for attr:"+attr,attr.isType(type)); 
        
        checkObjectValueType(attr,objectValue); 
    }

    private void assertEquals(String message, String value1, String value2, boolean ignoreCase)
    {
        Assert.assertTrue(message,StringUtil.compare(value1,value2,ignoreCase)==0);
    }

    // test whether object has matching type and native value 
    void checkObjectValueType(Attribute attr,Object objValue)
    {
        AttributeType type=getObjectAttributeType(objValue);
        AttributeType type2=AttributeType.getObjectType(objValue,null); 
        Assert.assertEquals("AttributeType.getObjectType() and unit test getObjectType() must agree.",type,type2);
        if (objValue==null)
        {
            Assert.assertNull("NULL object msut have NULL type.",objValue);
            return; // NULL value.  
        }
        
        Assert.assertTrue("Object type must be:"+type,attr.isType(type)); 

        // check native value type! 
        switch(type)
        {
            case BOOLEAN:
                Assert.assertTrue("getBoolValue() must match native type!",(attr.getBooleanValue()==((Boolean)objValue))); 
                break;
            case INT:
                Assert.assertTrue("getIntValue() must match native type!",(attr.getIntValue()==((Integer)objValue))); 
                break;
            case LONG:
                Assert.assertTrue("getLongValue() must match native type!",(attr.getLongValue()==((Long)objValue))); 
                break; 
            case FLOAT:
                Assert.assertTrue("getFloatValue() must match native type!",(attr.getFloatValue()==((Float)objValue))); 
                break; 
            case DOUBLE:
                Assert.assertTrue("getDoubleValue() must match native type!",(attr.getDoubleValue()==((Double)objValue))); 
                break;
            case STRING:
                Assert.assertTrue("getStringValue() must match native type!",(attr.getStringValue()==((String)objValue))); 
                break; 
            case VRI:
                try
                {
                    Assert.assertTrue("getDoubleValue() must match native type!",((VRI)objValue).equals(attr.getVRI()) );
                }
                catch (Exception e)
                {
                    Assert.fail("Exception:"+e);
                } 
                break; 
            case DATETIME:
                Assert.assertTrue("getDateValue() must match native type!", compareDateValues(attr.getDateValue(),(Date)objValue)); 
                break; 
            default:
                Assert.fail("Can not check type:"+type); 

        }
        
        //compareDateValues(attr.getDateValue(),(Date)objValue))); 
    }

    private boolean compareDateValues(Date val1,Date val2)
    {
        boolean result=false; 

        if (val1==val2)
            result=true; 
        
        if (result==false)
        {
            if ( val1.toString().equals(val2.toString()) ) 
                result=true; 
        }
       
        String unistr1=Presentation.createNormalizedDateTimeString(val1); 
        String unistr2=Presentation.createNormalizedDateTimeString(val2); 

        // also Compare Presentation string implementations. 
        if (result)
        {
            Assert.assertEquals("Normalize date/time strings should be equal",unistr1,unistr2); 
        }
        else
        {
            Assert.assertEquals("Normalize date/time strings should NOT be equal",unistr1,unistr2); 
        }
        
        return result; 
    }

    // Unit test implementation: Different then AttributeType to assert similar implementations
    AttributeType getObjectAttributeType(Object obj)
    { 
        if (obj instanceof Boolean)
            return AttributeType.BOOLEAN;
        else if (obj instanceof Integer)
            return AttributeType.INT;
        else if (obj instanceof Long)
            return AttributeType.LONG;
        else if (obj instanceof Float)
            return AttributeType.FLOAT;
        else if (obj instanceof Double)
            return AttributeType.DOUBLE;
        else if (obj instanceof String)
            return AttributeType.STRING;
        else if (obj instanceof Date)
            return AttributeType.DATETIME;
        else if (obj instanceof VRI)
            return AttributeType.VRI;
        else if (obj instanceof Enum)
            return AttributeType.ENUM;
        
        // check enum ? 
        return null; 
    }
    
    @Test
    public void testVRIAttribute() throws Exception
    {
        String vrlstr="file://user@host.domain:1234/Directory/A File/";
        // note: VRI normalizes the VRI string!. use VRI.toString() to get actual string representation! 
        VRI vrl=new VRI(vrlstr); 
        vrlstr=vrl.toNormalizedString(); 
        
        // create STRING type: 
        Attribute vrlStrAttr=new Attribute(AttributeType.STRING,"testvrl",vrlstr);
        // object value must match with String object. 
        checkObjectValueType(vrlStrAttr,vrlstr); // check String Value 
        
        // create VRI type: 
        Attribute vrlAttr=new Attribute("testvrl",vrl); 
        // object value must match with VRI object. 
        checkObjectValueType(vrlAttr,vrl); // check VRI Value 
    }
    
    @Test
    public void testAttributeStringCompare()
    {
        doAttributeStringCompare("aap","noot"); 
        doAttributeStringCompare("noot","aap");
        doAttributeStringCompare("aap","aap"); 
        doAttributeStringCompare("",""); 
        doAttributeStringCompare("","aap"); 
        doAttributeStringCompare("aap","");
        doAttributeStringCompare(null,""); 
        doAttributeStringCompare("",null); 
        doAttributeStringCompare(null,null); 
        doAttributeStringCompare(null,"aap"); 
        doAttributeStringCompare("aap",null); 
    }
    
    void doAttributeStringCompare(String val1,String val2)
    {
        int strComp=StringUtil.compare(val1,val2); 
        Attribute a1=new Attribute("name",val1); 
        Attribute a2=new Attribute("name",val2); 
        int attrComp=a1.compareTo(a2); 
        
        Assert.assertEquals("Attribute compareTo() must result in same value a String compareTo()!",strComp,attrComp);
    }   
    
    @Test
    public void testAttributeIntCompare()
    {
        doAttributeIntLongCompare(0,0); 
        doAttributeIntLongCompare(-1,0); 
        doAttributeIntLongCompare(0,-1); 
        doAttributeIntLongCompare(-1,-1);
        
        doAttributeIntLongCompare(0,0); 
        doAttributeIntLongCompare(1,0); 
        doAttributeIntLongCompare(0,1); 
        doAttributeIntLongCompare(1,1);
        
        doAttributeIntLongCompare(-1,1);
        doAttributeIntLongCompare(1,-1);
    }
    
    void doAttributeIntLongCompare(int val1,int val2)
    {
        // int inComp=Integer.compare(val1, val2);  // java 1.7 
        int intComp=new Integer(val1).compareTo(val2);
        Attribute a1=new Attribute("name1",val1); 
        Attribute a2=new Attribute("name2",val2); 
        int attrComp=a1.compareTo(a2); 
        Assert.assertEquals("Attribute (int)compareTo() must result in same value a Integer compareTo()!",intComp,attrComp);
       
        long lval1=val1,lval2=val2; 
        // int longComp=Long.compare(lval1, lval2); // java 1.7  
        int longComp=new Long(lval1).compareTo(lval2); 
        
        a1=new Attribute("name1",lval1); 
        a2=new Attribute("name2",lval2); 
        attrComp=a1.compareTo(a2); 
        Assert.assertEquals("Attribute (long)compareTo() must result in same value a Long compareTo()!",longComp,attrComp);
        
        // paranoia: 
        Assert.assertEquals("Integet.compare() and Long.compare() do not match!",intComp,longComp); 
    }   
}
