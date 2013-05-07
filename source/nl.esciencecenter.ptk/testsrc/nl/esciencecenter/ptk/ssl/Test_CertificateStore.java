package nl.esciencecenter.ptk.ssl;

import java.security.cert.Certificate;
import java.security.cert.X509Certificate;

import java.util.List;

import nl.esciencecenter.ptk.GlobalProperties;

import org.junit.Test;

public class Test_CertificateStore
{

    @Test
    public void test_ListCertificates() throws Exception
    {
        // CertificateStore certs=CertificateStore.getDefault(true); 
        CertificateStore certs=CertificateStore.loadCertificateStore(GlobalProperties.getGlobalUserHome()+"/.vletrc/cacerts","changeit",false,false);
        
        List<String> aliasses = certs.getAliases();
        
        for (String alias:aliasses)
        {
            Certificate cert = certs.getCertificate(alias); 
            if (cert instanceof X509Certificate)
            {
                System.out.printf("alias %s=%s",alias,CertUtil.toString((X509Certificate)cert,"  ","\n"));
            }
            else
            {
                System.out.printf("alias %s=Unknown Certificate class:%s\n",alias,cert.getClass());
            }
        }
    }
    
}
