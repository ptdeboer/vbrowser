/*
 * (C) 2012 Netherlands eScience Center/Biomarker Boosting consortium. 
 * 
 * This code is under development. 
 *  
 */ 
// source: 

package nl.nlesc.ptk.io;

import java.io.File;

import junit.framework.Assert;

import nl.esciencecenter.ptk.Global;

import org.junit.Test;



public class TestGlobal 
{
	
	@Test
	public void testArchitecture()
	{
		printf("--- TestGlobal ---\n");
		
		printf(" - user name =%s\n",assertNotEmpty(Global.getGlobalUserName()));
		printf(" - user home =%s\n",assertNotEmpty(Global.getGlobalUserHome()));
		printf(" - tmp dir   =%s\n",assertNotEmpty(Global.getGlobalTempDir()));
		printf(" - hostname  =%s\n",assertNotEmpty(Global.getHostname()));
		//
		printf(" - OS info   = %s/%s/%s\n",Global.getOsArch(),Global.getOsName(),Global.getOsVersion()); 
		printf(" - isWindows = %s\n",Global.isWindows());  
		printf(" - isLinux   = %s\n",Global.isLinux());	
		printf(" - isMacOS   = %s\n",Global.isMacOS());
		printf(" - isMacOSX  = %s\n",Global.isMacOSX());
		printf(" - isMac     = %s\n",Global.isMac());
	        
		//assertNotEmpty(""); 
	}

    @Test 
    public void testTempDir()
    {
    	String tmpDir = Global.getGlobalTempDir(); 
    	
    	Assert.assertNotNull("getGlobalTempDir() must return a value",tmpDir); 
    	
    	File file=new File(tmpDir); 

    	Assert.assertTrue("System depended temp directory must exists:"+tmpDir,file.exists()); 
    	Assert.assertTrue("System depended temp location must be a directory:"+tmpDir,file.isDirectory()); 
    
    }
    
	private String assertNotEmpty(String string)
	{
		Assert.assertNotNull("String value may not be null",string); 
		Assert.assertFalse("String value may not be empty",string.equals("")); 
		return string;
	}

	
	private void printf(String format, Object... args) 
	{
		System.out.printf(format,args); 
	}
}
