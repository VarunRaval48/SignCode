package stored_keys;

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
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;


//This class is an implementation to show usage of GetSigExistingKeys class's sigUsingExistKeys method
public class GenSigExistKeys {

	//TODO Enter valid pass here
	private static final String keyStorePass="";		//password of keystore
	private static final String privateKeyPass="";		//password of privatekey

	public static void main(String args[]){

		String pathToKeyStore = "/home/varun/keystore";
		String dataFile="/home/varun/Documents/projects/GSoC/T1_testData/files/hello.js", 
//				signFile="/home/varun/Documents/projects/GSoC/T1_testData/signed_files/TODO_sign_exist_1.sgn",
//				signStrFile="/home/varun/Documents/projects/GSoC/TODO_sign_exist_1_str.txt";
		signFile="/home/varun/Documents/projects/GSoC/T1_testData/signed_files/hello_sign_exist.sgn",
		signStrFile="/home/varun/Documents/projects/GSoC/T1_testData/signed_files/hello_sign_exist_str.js";

		try {

			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

			FileInputStream fileInputStream = new FileInputStream(new File(pathToKeyStore));

			keyStore.load(fileInputStream, keyStorePass.toCharArray());

			GetSigExistingKeys gsek = new GetSigExistingKeys();

			byte[] realsig = gsek.sigUsingExistKeys(keyStore, "Varun Raval", privateKeyPass, dataFile, signFile);

			gsek.storeSignUsingExistKeys(realsig, signFile);

			String strSig = gsek.convertBytesToBase64(realsig);

			gsek.storeSignUsingExistKeys(strSig, signStrFile);

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
		} catch (UnrecoverableKeyException e) {
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
