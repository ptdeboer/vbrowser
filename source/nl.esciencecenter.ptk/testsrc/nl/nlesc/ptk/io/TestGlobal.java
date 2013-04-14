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
