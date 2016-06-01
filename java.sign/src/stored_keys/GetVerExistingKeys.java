package stored_keys;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringBufferInputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Base64;

public class GetVerExistingKeys {

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
	void verUsingExistKeys(KeyStore keyStore, String pathToCert, byte realsgn[], String dataFile) 
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
//			X509Certificate c = (X509Certificate)certificateFactory.generateCertificate(buf_in);
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

		FileInputStream fin_data = new FileInputStream(dataFile);
		BufferedInputStream buf_data_in = new BufferedInputStream(fin_data);

		buffer = new byte[1024];
		while(buf_data_in.available()>0){

			len = buf_data_in.read(buffer);
			signature.update(buffer, 0, len);
		}
		
		boolean verified = signature.verify(realsgn);
		
		System.out.println("File verified: "+verified);
		
		buf_data_in.close();
	}
		

	public boolean verSignature(byte[] sign, File dataFile, Certificate cert) throws FileNotFoundException{

		return verSignature(sign, new BufferedInputStream(new FileInputStream(dataFile)), cert);
	}
	
	public boolean verSignature(byte[] sign, String content, Certificate cert){

		return verSignature(sign, new BufferedInputStream(new ByteArrayInputStream(content.getBytes())), cert);
	}
	
	public boolean verSignature(byte[] sign, BufferedInputStream buf_data_in, Certificate cert){
		
		byte[] buffer;
		int len;
		boolean verified = false;
		
		try{

			Signature signature = Signature.getInstance("SHA1withDSA", "SUN");
	
			PublicKey publicKey = cert.getPublicKey();
			
			signature.initVerify(publicKey);

			buffer = new byte[1024];
			while(buf_data_in.available()>0){

				len = buf_data_in.read(buffer);
				signature.update(buffer, 0, len);
			}

			verified = signature.verify(sign);

//			System.out.println("File verified: "+verified);

			buf_data_in.close();
			return verified;

		} catch (SignatureException | NoSuchAlgorithmException | NoSuchProviderException | InvalidKeyException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public Certificate importCertFromFile(String pathToCert){
		
		Certificate cert = null;
		
		try(BufferedInputStream buf_in = new BufferedInputStream(new FileInputStream(pathToCert))){

			CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

			while(buf_in.available() > 0){
				cert = certificateFactory.generateCertificate(buf_in);
			}
			
			return cert;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (CertificateException e) {
			e.printStackTrace();
			return null;
		}
	}

	byte[] getSignatureFromBytesFile(String signedFile){
		
		try( FileInputStream fin_sgn = new FileInputStream(signedFile)){
			byte realsgn[] = new byte[fin_sgn.available()];
			
			fin_sgn.read(realsgn);
			
			return realsgn;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	String getSignatureFromStringFile(String signedFile){

		try(BufferedReader br = new BufferedReader(new FileReader(signedFile))){

			StringBuffer strBuf = new StringBuffer();

			int c;
			while((c=br.read())!=-1){
				strBuf.append((char)c);
			}

			return strBuf.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public byte[] convertBase64ToByte(String str){

		Base64.Decoder decode = Base64.getDecoder();
		return decode.decode(str);
	}

}
