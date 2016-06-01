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
import java.security.cert.Certificate;
import java.security.cert.CertificateException;

//This class is an implementation to show usage of GetSigExistingKeys class's verUsingExistKeys method
public class VerSigExistKeys {
	
	//TODO Enter valid pass here
	private static final String keyStorePass="";		//password of keystore

	public static void main(String args[]) throws FileNotFoundException{

		String pathToCertificate = "/home/varun/Documents/Example.cer";
		String pathToKeyStore = "/home/varun/keystore",
				signFile="/home/varun/Documents/projects/GSoC/T1_testData/signed_files/hello_sign_exist.sgn",
				signStrFile="/home/varun/Documents/projects/GSoC/T1_testData/signed_files/hello_sign_exist_str.js",
				dataFile="/home/varun/Documents/projects/GSoC/T1_testData/files/hello.js";

//		try {

//			KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());			
//
//			FileInputStream fileInputStream = new FileInputStream(new File(pathToKeyStore));
//
//			keyStore.load(fileInputStream, keyStorePass.toCharArray());

			GetVerExistingKeys gver = new GetVerExistingKeys();

//			gver.verUsingExistKeys(keyStore, pathToCertificate, signedFile, dataFile);

			Certificate certificate = gver.importCertFromFile(pathToCertificate);
			String sigStr = gver.getSignatureFromStringFile(signStrFile);
			byte[] sig = gver.convertBase64ToByte(sigStr);
			
			boolean verify = gver.verSignature(sig, new File(dataFile), certificate);
			System.out.println("File Verified: "+verify);
//		}
//		catch (KeyStoreException e) {
//			e.printStackTrace();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
//		} catch (CertificateException e) {
//			e.printStackTrace();
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}		
	}
}
