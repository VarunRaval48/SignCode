package signature.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

import signature.CertImport;

public class CertImportTest {

	static String pathToKeyStore = "";
	private static final String keyStorePass="";		//password of keystore

	public static void main(String args[]){
		
		KeyStore keyStore;
		try {
			keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
			FileInputStream fileInputStream = new FileInputStream(new File(pathToKeyStore));
			keyStore.load(fileInputStream, keyStorePass.toCharArray());
			
			CertImport certImport = new CertImport(keyStore);
			Certificate c = certImport.importCert();		//two types java.security and java.security.cert

			System.out.println(c.toString());
			
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
