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

//package voms ;
//
//import net.sourceforge.acacia.security.GlobusCredentialUtils;
//import nl.uva.vlet.Global;
//import nl.uva.vlet.data.StringList;
//import nl.uva.vlet.exception.VlException;
//import nl.uva.vlet.util.cog.GridProxy;
//import nl.uva.vlet.util.voms.AcaciaUtil;
//
//import org.apache.log4j.Logger;
//
////--------------------------------------------------------------------------------
//
///**
//  *Tester for the voms package; 
//  */
//public class TestValidateVOMS
//{
//
//  static Logger logger = Logger.getLogger( "net.sourceforge.acacia.voms" ) ;
//
//  //------------------------------------------------------------------------------
//
//  public TestValidateVOMS()
//  {
//  }
//
//  //------------------------------------------------------------------------------
//
//  public static void main( String[] args )
//  {
//	//
//	System.setProperty("ACACIA_SECURITY_ROOT","/home/ptdeboer/workspace/auxtool.acacia/etc/grid-security");
//	//	System.setProperty("ACACIA_SECURITY_ROOT","/etc/grid-security");
// 
//    // this is a VERY IMPORTANT line - otherwise the DN cannot match against the C client that uses openssl
//    org.bouncycastle.asn1.x509.X509Name.DefaultSymbols.put( org.bouncycastle.asn1.x509.X509Name.EmailAddress , "Email" ) ;
//    org.bouncycastle.asn1.x509.X509Name.RFC2253Symbols.put( org.bouncycastle.asn1.x509.X509Name.EmailAddress , "Email" ) ;
//    org.bouncycastle.asn1.x509.X509Name.DefaultLookUp.put( "Email" , org.bouncycastle.asn1.x509.X509Name.E ) ;
//
//    Global.setDebug(true); 
//    
//    logger.info( "Starting Voms Proxy Test" ) ;
//
//    GridProxy myProx = GridProxy.getDefault(); 
//    
//    GlobusCredentialUtils globCredUtil = new GlobusCredentialUtils(myProx.getGlobusCredential()); 
//    
//    try
//	{
//    	StringList report=new StringList();
//    	
//		AcaciaUtil.validate(globCredUtil,report);
//		
//		System.out.println("---------------------- Validate Report -------------------"); 
//		System.out.println(report.toString());	
//		System.out.println("---------------------- Validate Report -------------------"); 	
//	
//		String strs[]=AcaciaUtil.getFQANSforIssuerDN(myProx.getGlobusCredential(),null);
//		
//		for (String str:strs)
//		{
//			System.out.println("- FQANs="+str);
//		}
//	}
//	catch (VlException e)
//	{
//		System.err.println("Exception:"+e);
//		e.printStackTrace();
//	} 
//
//  }
//
//  //------------------------------------------------------------------------------
//
//}
