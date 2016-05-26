package stored_keys;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

public class LoadKeystore {

	private static KeyStore keyStore;

	public static KeyStore load(String pathToKeyStore, String keyStorePass){	
		
		if(keyStore == null)
			try (FileInputStream fileInputStream = new FileInputStream(new File(pathToKeyStore))){
				
				keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
	
				keyStore.load(fileInputStream, keyStorePass.toCharArray());
			
				return keyStore;
			} catch (KeyStoreException e) {
				e.printStackTrace();
				return null;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return null;
			} catch (CertificateException | NoSuchAlgorithmException e) {
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		else
			return keyStore;
	}
}
