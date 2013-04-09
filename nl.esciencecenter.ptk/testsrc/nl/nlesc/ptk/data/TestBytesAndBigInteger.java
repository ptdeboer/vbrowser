/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.nlesc.ptk.data;

import nl.esciencecenter.ptk.util.StringUtil;

import org.junit.Assert;
import org.junit.Test;

public class TestBytesAndBigInteger 
{
    
    @Test
    public void test16bytesBigInteger()
    {
        // create 16 bytes unsinged integer. 
        byte bytes[]=new byte[16]; 
        int n=16;
        
        for (int i=0;i<n;i++)
        {
            bytes[i]=(byte)0x00ff;  
        }
        
        testBigIntegerString("340282366920938463463374607431768211455",bytes,false,false); 
        // todo: signed (negative) 
    }

    @Test
    public void test24bytesBigInteger()
    {
        // create 16 bytes unsinged integer. 
        byte bytes[]=new byte[24]; 
        int n=16;
        
        for (int i=0;i<n;i++)
        {
            bytes[i]=(byte)0x00ff;  
        }
        
        testBigIntegerString("6277101735386680763835789423207666416083908700390324961280",bytes,false,false); 
        // todo: signed (negative) 
    }
    
    private void testBigIntegerString(String expected, byte[] bytes,boolean signed, boolean isLE)
    {
        // big endian: 
        String beStr=StringUtil.toBigIntegerString(bytes,signed, isLE); 
        Assert.assertEquals("Big Endian BigInteger string doesn't match expected",expected, beStr); 
        
        byte reverseEndian[];
        // little endian:
        if (bytes==null)
        {
            reverseEndian=null;
        }
        else
        {
            int len=bytes.length; 
            reverseEndian=new byte[len];
            for (int i=0;i<len;i++)
                reverseEndian[len-i-1]=bytes[i];
        }
        String leStr=StringUtil.toBigIntegerString(reverseEndian, false,true); 
        Assert.assertEquals("Little Endian BigInteger string doesn't match expected",expected, leStr); 
    } 
    
    
}