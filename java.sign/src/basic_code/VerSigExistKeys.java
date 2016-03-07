package basic_code;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;


//This class is an implementation to show usage of GetSigExistingKeys class's verUsingExistKeys method
public class VerSigExistKeys {
	
	//TODO Enter valid pass here
	private static final String keyStorePass="";		//password of keystore

	public static void main(String args[]){
		
		String pathToCertificate = "/home/varun/Documents/Example.cer";
		String pathToKeyStore = "/home/varun/keystore";
		String signedFile = "/home/varun/Documents/TODO_sign_exist_1.sgn";
		String dataFile = "/home/varun/Documents/TODO";
		try {
		
			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());			
			
			FileInputStream fileInputStream = new FileInputStream(new File(pathToKeyStore));
			
			keyStore.load(fileInputStream, keyStorePass.toCharArray());
		
			GetSigExistingKeys gsek = new GetSigExistingKeys();
						
			gsek.verUsingExistKeys(keyStore, pathToCertificate, signedFile, dataFile);
			
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
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (SignatureException e) {
			e.printStackTrace();
		}		
	}
}
