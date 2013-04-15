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

package nl.nlesc.vlet.vrs.vrms;

import java.util.Hashtable;
import java.util.Map;

import nl.esciencecenter.ptk.crypt.Secret;

/**
 * 
 */ 
public class SecretStore 
{
	public class SecretCombi
	{
		private Secret passWord=null;  
		private Secret passPhrase=null;
		private boolean validated=true;  
		
		public SecretCombi(Secret word, Secret phrase)
		{
			this.passWord=word;
			this.passPhrase=phrase;
		}

		public void setPassword(Secret passstr)
		{
			passWord=passstr; 
		}

		public void setPassphrase(Secret passstr)
		{
			passPhrase=passstr; 
		}
		
		public Secret getPassword()
		{
			return passWord; 
		}
		
		public Secret getPassphrase()
		{
			return passPhrase; 
		}
		
		public void setValidated(boolean val)
		{
			this.validated=val; 
		}

		public boolean getValidated()
		{
			return this.validated; 
		}

	}
	
	public static String createID(String scheme,String user,String host,int port)
	{
		return "key-"+scheme+"-"+user+"-"+host+"-"+port; 
	}
	
	private Map<String,SecretCombi> secrets=new Hashtable<String,SecretCombi>(); 
	
	
	public Secret getPassword(String scheme,String user,String host,int port)
	{
	    SecretCombi sec=secrets.get(createID(scheme,user,host,port));
		if (sec!=null)
			return sec.getPassword();
		
		return null;
	}
	
	public Secret getPassphrase(String scheme,String user,String host,int port)
	{
	    SecretCombi sec=secrets.get(createID(scheme,user,host,port));
		if (sec!=null)
			return sec.getPassphrase();
		
		return null; 
	}
	
	public void storePassword(String scheme,String user,String host,int port,Secret passtr)
	{
	    SecretCombi sec=new SecretCombi(passtr,null);
		String id=createID(scheme,user,host,port); 
		
		secrets.put(id,sec); 
	}
	
	public void storePassphrase(String scheme,String user,String host,int port,Secret field)
	{
	    SecretCombi sec=new SecretCombi(null,field);
		String id=createID(scheme,user,host,port); 
		
		secrets.put(id,sec); 
	}

	public void setIsValid(String scheme,String user,String host,int port,boolean val)
	{
	    SecretCombi sec=getSecret(scheme,user,host,port); 
		if (sec!=null)
		{
			sec.setValidated(val); 
		}
	}

	public SecretCombi getSecret(String scheme,String user, String host,int port)
	{
		String id=createID(scheme,user,host,port); 
		//System.err.println("SecretStore:has secret for"+id+"="+(secrets.get(id)!=null));
		return secrets.get(id); 
	}
}
