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

package nl.nlesc.ptk.crypt;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

import junit.framework.Assert;

import nl.esciencecenter.ptk.crypt.StringHasher;
import nl.esciencecenter.ptk.util.StringUtil;
import nl.esciencecenter.ptk.util.logging.ClassLogger;

import org.junit.Test;

public class TestHasher
{
    static
    {
        ClassLogger logger=ClassLogger.getLogger(StringHasher.class);
        logger.setLevelToWarn(); 
    }
    
    @Test
    public void testMD5Hash() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        StringHasher hasher = new StringHasher("MD5");
        String salt="";
        //testHash(hasher,null,null);
        testHash(hasher,salt,"","D41D8CD98F00B204E9800998ECF8427E"); 
        testHash(hasher,salt,"0","CFCD208495D565EF66E7DFF9F98764DA"); 
        testHash(hasher,salt,"1","C4CA4238A0B923820DCC509A6F75849B");
        // don't use 12345 as MD5 hashed password ! 
        testHash(hasher,salt,"12345","827CCB0EEA8A706C4C34A16891F84E7B");
        testHash(hasher,salt,"00000000000000000000000000000000","CD9E459EA708A948D5C2F5A6CA8838CF");
    }

   
    
    @Test
    public void testMD5SaltedHash() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        StringHasher hasher = new StringHasher("MD5");
        testHash(hasher,"","","D41D8CD98F00B204E9800998ECF8427E"); 
        testHash(hasher,"hello","","5D41402ABC4B2A76B9719D911017C592");
        testHash(hasher,"hello","salt1","8EE3F8E4B41664B9B943D039BBA41A5C");
        testHash(hasher,"","salt1","55F312F84E7785AA1EFA552ACBF251DB"); 
    }
    
   
    
    @Test
    public void testSHA256Hash() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        StringHasher hasher = new StringHasher("SHA-256");
        //testHash(hasher,null,null);
        String salt=""; 
        testHash(hasher,salt,"","E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855");
        testHash(hasher,salt,"0","5FECEB66FFC86F38D952786C6D696C79C2DBC239DD4E91B46729D73A27FB57E9"); 
        testHash(hasher,salt,"1","6B86B273FF34FCE19D6B804EFF5A3F5747ADA4EAA22F1D49C01E52DDB7875B4B"); 
        testHash(hasher,salt,"12345","5994471ABB01112AFCC18159F6CC74B4F511B99806DA59B3CAF5A9C173CACFC5"); 
        testHash(hasher,salt,"00000000000000000000000000000000","84E0C0EAFAA95A34C293F278AC52E45CE537BAB5E752A00E6959A13AE103B65A"); 
    }

    @Test
    public void testSAH256SaltedHash() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        // The salt String is just appended to the source text: text+salt 
        // So hashing "hello" or the "salt1" String alone will yield the same hash when
        // either used as source text or salt. This is tested here also. 
        
        StringHasher hasher = new StringHasher("SHA-256");
        
        testHash(hasher,"","","E3B0C44298FC1C149AFBF4C8996FB92427AE41E4649B934CA495991B7852B855");
        testHash(hasher,"hello","","2CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824"); 
        testHash(hasher,"","hello","2CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824");
        testHash(hasher,"salt1","","DC90CF07DE907CCC64636CEDDB38E552A1A0D984743B1F36A447B73877012C39");
        testHash(hasher,"","salt1","DC90CF07DE907CCC64636CEDDB38E552A1A0D984743B1F36A447B73877012C39");
        testHash(hasher,"hello","salt1","98199B80A1D79568AC5C6D56A28D46E27434CE063C36AE7B27D635979ABB9F15");
        testHash(hasher,"0","0","F1534392279BDDBF9D43DDE8701CB5BE14B82F76EC6607BF8D6AD557F60F304E");  
        testHash(hasher,"1","0","4A44DC15364204A80FE80E9039455CC1608281820FE2B24F1E5233ADE6AF1DD5");
        testHash(hasher,"0","1","938DB8C9F82C8CB58D3F3EF4FD250036A48D26A712753D2FDE5ABD03A85CABF4"); 
        testHash(hasher,"1","1","4FC82B26AECB47D2868C4EFBE3581732A3E7CBCC6C2EFB32062C08170A05EEB8");
     }
    
    
    @Test
    public void testSHA256Truncated() throws NoSuchAlgorithmException, UnsupportedEncodingException
    {
        StringHasher hasher = new StringHasher("SHA-256");
        
        // echo -n hello | sha256sum
        String hash32="2CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824"; 
        testHash(hasher,"","hello",32,hash32);
        
        for (int i=32; i>4;i--)
        {
            String shortHash=createExorHash(hash32,i); 
            testHash(hasher,"","hello",i,shortHash);
        }
     }
  
//    @Test
//    public void testSHA256TruncatedBase64() throws NoSuchAlgorithmException, UnsupportedEncodingException
//    {
//        StringHasher hasher = new StringHasher("","SHA-256");
//        
//        // echo -n hello | sha256sum
//        String hash32="2CF24DBA5FB0A30E26E83B2AC5B9E29E1B161E5C1FA7425E73043362938B9824"; 
//        testHashBase64(hasher,"hello",32,hash32);
//        
//        for (int i=32; i>4;i--)
//        {
//            String shortHash=createExorHash(hash32,i); 
//            testHash(hasher,"hello",i,shortHash);
//        }
//     }
    
    private String createExorHash(String hexString, int len)
    {
        byte bytes[]=StringUtil.parseBytesFromHexString(hexString); 
        byte result[]=new byte[len];
        // needed? 
        // for (int i=0;i<len;i++)
        //    result[i]=0; 
        
        for (int i=0;i<bytes.length;i++)
            result[i%len]^=bytes[i]; // exor and wrap around len
        
        String shortHash=StringUtil.toHexString(result);
        return shortHash; 
        
    }
    
    public void testHash(StringHasher hasher, String salt, String text, String expectedHash)
    {
        String digest=hasher.createHashToHexString(salt,text,-1); 
        Assert.assertEquals("Salted hash not the same.",expectedHash,digest);
    }
    
    public void testHash(StringHasher hasher, String salt,String text,int maxLen,String expectedHash)
    {
        //hasher.setSalt(""); 
        String digest=hasher.createHashToHexString(salt,text,maxLen); 
        Assert.assertEquals("Salted hash not the same (maxLen="+maxLen+").",expectedHash,digest);
    }
 }
