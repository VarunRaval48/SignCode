package basic_code;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

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
	 * @param signFile
	 * 			file where signed content is stored
	 * 
	 * @throws UnrecoverableKeyException 
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeyException
	 * @throws IOException
	 * @throws SignatureException
	 */
	void sigUsingExistKeys(KeyStore keyStore, String alias, String pass, String dataFile, String signFile) 
			throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException, 
				NoSuchProviderException, InvalidKeyException, IOException, SignatureException{
		
		System.out.println("Alias exists :"+keyStore.isKeyEntry("Varun Raval"));
		
		Key key = keyStore.getKey(alias, pass.toCharArray());
		
		Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
		
		signature.initSign((PrivateKey)key);

		FileInputStream file_input = new FileInputStream(dataFile);
		BufferedInputStream bf = new BufferedInputStream(file_input);
		
		byte buffer[] = new byte[1024];
		int len;
		
		while((len=bf.read(buffer)) >= 0){
			signature.update(buffer, 0, len);
		}
		
		bf.close();
		
		byte realsig[] = signature.sign();
		
		FileOutputStream fout = new FileOutputStream(signFile);
		BufferedOutputStream bout = new BufferedOutputStream(fout);
		
		bout.write(realsig);
		
		bout.close();

	}
	
	/*
	 * sign file with existing key in keystore with given alias
	 * and store sign in signedFile
	 * 
	 * @param keyStore
	 * 			keyStore of the keys
	 * @param pathToCert
	 * 			path of the public key certificate
	 * @param signFile
	 * 			file where signed content is stored
	 * @param dataFile
	 * 			file which needs to be signed
	 * 
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 * @throws InvalidKeyException
	 * @throws IOException
	 * @throws SignatureException
	 * @throws CertificateException
	 */
	void verUsingExistKeys(KeyStore keyStore, String pathToCert, String signedFile, String dataFile) 
			throws IOException, KeyStoreException, CertificateException, NoSuchAlgorithmException, NoSuchProviderException, 
				InvalidKeyException, SignatureException{
		
		BufferedInputStream buf_in = new BufferedInputStream(new FileInputStream(pathToCert));
		BufferedOutputStream buf_out = new BufferedOutputStream(new FileOutputStream("/home/varun/Documents/Example.txt"));
		
		CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
		
		int len;
		byte buffer[] = new byte[1024];
		
		while(buf_in.available() > 0){
			Certificate cert = certificateFactory.generateCertificate(buf_in);
			keyStore.setCertificateEntry("Example", cert);
		}
		
		buf_in.close();
		buf_out.close();
	
		System.out.println("Imported in keystore");
		
		System.out.println("Certificate Exists: "+keyStore.isCertificateEntry("Example"));
		
		Certificate certificate = keyStore.getCertificate("Example");
		System.out.println(certificate.toString());
		System.out.println("Certificate Type: "+certificate.getType());
				
		PublicKey publicKey = keyStore.getCertificate("Example").getPublicKey();
		
		Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
		signature.initVerify(publicKey);
		
		FileInputStream fin_sgn = new FileInputStream(signedFile);
		byte realsgn[] = new byte[fin_sgn.available()];
		
		fin_sgn.read(realsgn);
		
		FileInputStream fin_data = new FileInputStream(dataFile);
		BufferedInputStream buf_data_in = new BufferedInputStream(fin_data);
		
		buffer = new byte[1024];
		while(buf_data_in.available()>0){
			
			len = buf_data_in.read(buffer);
			signature.update(buffer, 0, len);
		}
		
		boolean verified = signature.verify(realsgn);
		
		System.out.println("File verified: "+verified);
		
		fin_sgn.close();
		buf_data_in.close();
	}
}
