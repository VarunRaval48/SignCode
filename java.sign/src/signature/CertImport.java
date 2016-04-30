package signature;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Enumeration;

//imports certificate from keystore
public class CertImport {

	KeyStore keyStore;
	public CertImport(KeyStore keyStore){
		
		this.keyStore = keyStore;
	}
	
	public Certificate importCert() throws KeyStoreException, NumberFormatException, IOException{
		
		String alias = listCert();
		return keyStore.getCertificate(alias);
	}
	
	public Certificate importCert(String alias) throws KeyStoreException{

//		keyStore.getCertificateChain(alias); for returning chain of certificates. Use CertPath
		return keyStore.getCertificate(alias);
	}
	
	private String listCert() throws KeyStoreException, NumberFormatException, IOException{
		
		ArrayList<String> list = new ArrayList();
		String temp;
		
		Enumeration<String> list_cert = keyStore.aliases();
		while(list_cert.hasMoreElements()){
			temp = list_cert.nextElement();
			System.out.println(temp);
			list.add(temp);
		}
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		return list.get(Integer.parseInt(br.readLine()));	
	}	
}
