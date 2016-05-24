package stored_keys;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Base64;

public class GetSigExistingKeys {

	/*
	 * sign file with existing key in keystore with given alias
	 * and store sign in signedFile
	 * 
	 * @param keyStore
	 * 			keyStore of the keys
	 * @param alias
	 * 			alias with which key is to be signed
	 * @param dataFile
	 * 			file which needs to be signed
	 * 
	 * @throws UnrecoverableKeyException 
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeyException
	 * @throws SignatureException
	 * 
	 * @return byte[]
	 * 			signed signature in byte format or null if file is not readable
	 * 
	 */
	byte[] sigUsingExistKeys(KeyStore keyStore, String alias, String pass, String dataFile, String signFile) 
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, 
				NoSuchProviderException, InvalidKeyException, SignatureException{

		byte buffer[] = new byte[1024];
		int len;

		System.out.println("Alias exists :"+keyStore.isKeyEntry("Varun Raval"));

		Key key = keyStore.getKey(alias, pass.toCharArray());

		Signature signature = Signature.getInstance("SHA1withDSA", "SUN");

		signature.initSign((PrivateKey)key);

		try(BufferedInputStream bf = new BufferedInputStream(new FileInputStream(dataFile))){	
			while((len=bf.read(buffer)) >= 0){
				signature.update(buffer, 0, len);
			}
			bf.close();
		} catch(IOException e){
			e.printStackTrace();
			return null;
		}

		byte realsig[] = signature.sign();

		return realsig;
	}	

	/*
	 * @param byte[]
	 * 			byte[] to be converted to string of Base64 format
	 * 
	 * @return String
	 * 			final String in Base64 format
	 */	
	String convertBytesToBase64(byte[] sig){
		
		Base64.Encoder b = Base64.getEncoder();
		return b.encodeToString(sig);
	}

	/*
	 * @param signFile
	 * 			file where signed content is stored
	 *
	 * @return true 
	 * 			this returns result of write operation whether successful or not
	 */
	boolean storeSignUsingExistKeys(byte sig[], String signFile){

		try( BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(signFile))){
			bout.write(sig);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * @param sig
	 * 			string in base64 encoding to store in file
	 * 
	 * @param signFile
	 * 			file where signed content is stored
	 *
	 * @return true 
	 * 			this returns result of write operation whether successful or not
	 */
	boolean storeSignUsingExistKeys(String sig, String signFile){

		try(FileWriter fw = new FileWriter(signFile)){
			fw.write(sig);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
