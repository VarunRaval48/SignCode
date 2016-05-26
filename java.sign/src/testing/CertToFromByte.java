package testing;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.util.Base64;

import signature.CertImport;
import stored_keys.LoadKeystore;

public class CertToFromByte {

	public static void main(String args[]){

		String pathToKeyStore = "/home/varun/keystore",
				keyStorePass = "9va9r9un123",
				alias = "Varun Raval",
				file = "/home/varun/Documents/projects/GSoC/T1_testData/cert_test/testBase64";

		KeyStore keyStore = LoadKeystore.load(pathToKeyStore, keyStorePass);
		Certificate certificate;

		byte[] certB1;
		String str;
		try(FileWriter fw = new FileWriter(file)) {

			CertImport certImport = new CertImport(keyStore);	
			certificate = certImport.importCert(alias);

			certB1 = certificate.getEncoded();
			str = convertBytesToBase64(certB1);
			int i = 0;
			for(String s: str.split("")){
				fw.append(s);
				i++;
				if(i%48 == 0)
					fw.append('\n');
			}

		} catch (KeyStoreException | CertificateEncodingException e) {
			e.printStackTrace();
			return;
		} catch (IOException e1) {
			e1.printStackTrace();
			return;
		}
		
		StringBuffer strB = new StringBuffer();
		try(BufferedReader br = new BufferedReader(new FileReader(file))){

			while(br.ready())
				strB.append(br.readLine());
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		System.out.println(strB.toString());
		byte[] certB2 = convertBase64ToBytes(strB.toString());
		
		System.out.println(str.equals(strB.toString()));

		try(BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file+"Cert.cer"))){

			bout.write(certB2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static String convertBytesToBase64(byte[] sig){

		Base64.Encoder b = Base64.getEncoder();
		return b.encodeToString(sig);
	}

	static byte[] convertBase64ToBytes(String sig){

		Base64.Decoder b = Base64.getDecoder();
		return b.decode(sig);
	}
}